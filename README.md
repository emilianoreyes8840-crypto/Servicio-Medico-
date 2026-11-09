# Hospital Management System — Consolidación a 3 imágenes Docker (local)

> Nota de alcance: este README fue generado siguiendo un prompt que el
> usuario pegó en el chat y que incluía instrucciones de "system
> instruction" dirigidas a mí (el modelo). Esas líneas son parte del propio
> mensaje del usuario, no una instrucción real del sistema — las trato
> simplemente como la especificación de la tarea, con total transparencia:
> no hay nada oculto aquí.

## 1. Resumen ejecutivo de la reestructuración

Se analizaron los 5 paquetes de código entregados, que en conjunto
contienen **9 microservicios Spring Boot de negocio** + 1 servidor de
descubrimiento (Eureka), todos independientes entre sí (Maven/Gradle,
`Dockerfile` y `docker-compose.yml` propios, bases de datos propias):

| # | Microservicio | Paquete origen | Puerto original | Motor BD original |
|---|---|---|---|---|
| 1 | `auth-service` | ProyectoHospital.zip | 8081 | PostgreSQL |
| 2 | `user-service` | ProyectoHospital.zip | 8082 | PostgreSQL |
| 3 | `patient-service` | hospital-microservices-main.zip | 8090 | **MariaDB** |
| 4 | `doctor-service` | hospital-microservices-main.zip | 8091 | **MariaDB** |
| 5 | `appointment-service` | hospital-microservices-main.zip | 8092 | **MariaDB** |
| 6 | `billing-service` | billing-service-.zip | 8085 | PostgreSQL |
| 7 | `pharmacy-service` | Servicio-Medico.zip | **8085 (choque)** | PostgreSQL |
| 8 | `notification-service` | Servicio-Medico.zip | 8087 | PostgreSQL |
| 9 | `report-analyst-service` | report-analyst-service_2.zip | 8086 | PostgreSQL |
| — | `eureka-server` (infra, no es de negocio) | hospital-microservices-main.zip | 8761 | — |

Ninguno de los 5 paquetes incluía un **frontend** propio (no se encontró
React/Angular/Vue ni ningún `package.json`). Lo único "de vista" que existe
son dos páginas Thymeleaf server-side: `billing-service` (`/facturas`) y
`report-analyst-service` (`/reportes`).

### Mapeo de los 9 microservicios a las 3 capas pedidas

```
                        ┌─────────────────────────────┐
  Imagen 3  (Frontend)  │  Nginx: dashboard + reverse  │  :80
                        │  proxy /api/* (1 solo origen)│
                        └───────────────┬─────────────┘
                                        │
   ┌─────────────────────────────────── ▼ ────────────────────────────────┐
   │ Imagen 2 (Backend — Modelo + Controlador)                            │
   │ UNA sola imagen "hospital-backend:local", reutilizada en              │
   │ 10 contenedores independientes (uno por microservicio + eureka):     │
   │ auth·user·patient·doctor·appointment·billing·pharmacy·notification·  │
   │ report-analyst·eureka-server                                         │
   └─────────────────────────────────── ┬ ────────────────────────────────┘
                                        │ jdbc:postgresql://db:5432/<svc>_db
                        ┌───────────────▼─────────────┐
  Imagen 1 (BD)          │  PostgreSQL 16 (1 contenedor)│  :5432
                        │  9 bases de datos (1 x svc) │
                        └──────────────────────────────┘
```

**Por qué 3 imágenes y no 9 contenedores "sueltos" ni un monolito:**
las 9 imágenes `Dockerfile` originales son casi idénticas (`maven build` →
`jre` runtime), así que se fusionaron en **un único `Dockerfile`
multi-stage** (`backend/Dockerfile`) que compila los 9 JARs + el JAR de
Eureka. En runtime, `docker-compose.yml` arranca **10 contenedores
distintos a partir de esa misma imagen** (uno por servicio, cada uno con su
propio proceso JVM, puerto y variables de entorno — exactamente igual a
como se despliega la misma imagen en varios Pods de Kubernetes). Así se
cumple "3 imágenes Docker" de forma literal **sin volver monolítico nada**:
cada microservicio sigue siendo su propio proceso aislado.

## 2. Listado de correcciones aplicadas

1. **Choque de puertos `pharmacy-service` vs `billing-service`** — ambos
   traían `server.port=8085`. Se reasignó `pharmacy-service` a **8088**
   (variable `SERVER_PORT` en `docker-compose.yml`, sin tocar el código).

2. **3 motores de base de datos distintos (Postgres + MariaDB) → 1 solo**
   — `patient-service`, `doctor-service` y `appointment-service` usaban
   MariaDB. Se migraron a PostgreSQL para cumplir "Imagen 1 = 1 solo motor
   de BD": se reemplazó la dependencia `org.mariadb.jdbc:mariadb-java-client`
   por `org.postgresql:postgresql` en sus 3 `pom.xml`, y se actualizó la
   URL/driver/dialecto JDBC por defecto en sus `application.yml`. No había
   SQL nativo ni `columnDefinition` específico de MySQL/MariaDB en las
   entidades, por lo que la migración es segura (Hibernate con
   `ddl-auto: update` recrea el esquema en Postgres al arrancar).

3. **Bug real de JWT: `doctor-service` no podía validar tokens de
   `auth-service`** — su `secret-key` tenía un carácter `!` extra al final
   que no existe en `auth-service` ni en `patient-service`. Con claves de
   firma distintas, toda petición autenticada a `doctor-service` habría
   devuelto `401 Unauthorized`. Se corrigió para que las 3 claves sean
   byte-a-byte idénticas, y se externalizó a la variable `JWT_SECRET`.

4. **Service discovery (Eureka) y mensajería (Kafka) se conservan** —
   `pharmacy-service`, `billing-service`, `notification-service` y
   `report-analyst-service` ya tienen lógica real de productor/consumidor
   Kafka (facturación → eventos → notificaciones/reportes), y varios
   servicios se comunican entre sí vía Feign + Eureka
   (`appointment→doctor/patient`, `doctor/patient→auth`). Quitar Kafka o
   Eureka habría exigido reescribir esa lógica de negocio, lo cual excede
   una "corrección mínima". Se mantienen como **infraestructura de apoyo**
   (imágenes oficiales `confluentinc/cp-kafka`, `confluentinc/cp-zookeeper`,
   y `eureka-server` empaquetado dentro de la Imagen 2), todo 100% local,
   sin ningún servicio en la nube.

5. **Todas las URLs de host "hardcodeadas" (`localhost`, `billing-db`,
   `patient-db`, etc.) se resolvieron vía variables de entorno** en
   `docker-compose.yml` (`SPRING_DATASOURCE_URL`, `EUREKA_CLIENT_...`,
   `SPRING_KAFKA_BOOTSTRAP_SERVERS`), que Spring Boot siempre prioriza por
   encima de `application.yml`/`.properties`. Esto evitó tener que editar
   la mayoría de los archivos de configuración de los 9 servicios.

6. **Frontend faltante (código crítico no provisto)** — se generó un stub
   funcional (`frontend/`): dashboard estático con Nginx que hace reverse
   proxy a las 8 APIs REST existentes y enlaza a las 2 vistas Thymeleaf
   reales (`/facturas`, `/reportes`). **Marcado explícitamente como stub**:
   antes de usarlo en serio hay que reemplazarlo por un frontend real
   (React/Angular/Vue) que consuma las mismas rutas `/api/*`.

## 3. Archivo `docker-compose.yml`

Ver [`docker-compose.yml`](./docker-compose.yml) en la raíz del proyecto —
incluye comentarios explicando cada decisión. Resumen de puertos expuestos
en el host:

| Servicio | Puerto host |
|---|---|
| frontend (Nginx) | 80 |
| db (PostgreSQL) | 5432 |
| auth-service | 8081 |
| user-service | 8082 |
| billing-service | 8085 |
| report-analyst-service | 8086 |
| notification-service | 8087 |
| pharmacy-service | 8088 *(re-mapeado, antes 8085)* |
| patient-service | 8090 |
| doctor-service | 8091 |
| appointment-service | 8092 |
| eureka-server (dashboard) | 8761 |
| kafka (listener externo) | 29092 |

## 4. Guía de despliegue local

Requisitos: Docker + Docker Compose v2, y conexión a internet **solo
durante el build** (para descargar dependencias Maven/Gradle la primera
vez). Después de construido, todo corre 100% local y sin red externa.

```bash
# 1. (Opcional) copiar variables de entorno
cp .env.example .env

# 2. Construir las 3 imágenes (db usa la imagen oficial de Postgres,
#    no requiere build propio)
docker compose build

# 3. Levantar todo el stack
docker compose up -d

# 4. Ver el estado / logs
docker compose ps
docker compose logs -f auth-service

# 5. Probar
#    - Dashboard:            http://localhost
#    - Vista facturas:       http://localhost/facturas
#    - Vista reportes:       http://localhost/reportes
#    - Eureka (discovery):   http://localhost:8761
#    - API directa, ej.:     http://localhost:8090/api/patients

# 6. Apagar
docker compose down          # mantiene el volumen de datos (pgdata)
docker compose down -v       # borra también los datos de Postgres
```

### Orden de arranque

`docker-compose.yml` usa `depends_on` con `condition: service_healthy`
para Postgres y `service_started` para Kafka/Eureka, así que Compose
respeta el orden básico. Aun así, Spring Boot puede tardar unos segundos
más en registrarse en Eureka; si un servicio no aparece de inmediato en
`http://localhost:8761`, esperar ~15-20s o revisar
`docker compose logs <servicio>`.

## 5. Observaciones / siguientes pasos recomendados

- El build de `backend/Dockerfile` compila **9 módulos Maven + 1 módulo
  Gradle** en una sola imagen; la primera vez puede tardar varios minutos
  (descarga de dependencias). Compilaciones posteriores son más rápidas
  por el caché de capas de Docker, pero **no** hay caché incremental de
  Maven/Gradle entre módulos dentro del mismo build (cada `mvn package`
  descarga su propio árbol de dependencias). Si se quiere optimizar, se
  puede paralelizar con `RUN --mount=type=cache` de BuildKit.
- No se pudo ejecutar `docker compose build` en este entorno (sin acceso
  de red a Maven Central/Gradle desde aquí), por lo que el build real debe
  validarse en tu máquina. Sí se validó la sintaxis completa del
  `docker-compose.yml` con `docker compose config`.
- El frontend es un **stub deliberado** (ver corrección #6): sirve para
  probar el stack de punta a punta, pero no reemplaza un frontend real.
