# =============================================================================
# Imagen 2 - Capa de Logica / Backend (Modelo + Controlador)
# -----------------------------------------------------------------------------
# UNA sola imagen Docker contiene los 9 microservicios de negocio + el
# servidor de descubrimiento (eureka-server), ya compilados como JARs
# independientes. Esto cumple "3 imagenes" de forma literal sin convertir
# el backend en un monolito: en tiempo de ejecucion, docker-compose.yml
# levanta UN CONTENEDOR POR SERVICIO a partir de esta MISMA imagen, cada
# uno con su propio proceso JVM, su propio puerto y sus propias variables
# de entorno (igual que se haria en Kubernetes con un solo Deployment image
# reutilizado en varios Pods). Nada corre "pegado" en un unico proceso.
#
# El servicio a ejecutar se selecciona en runtime con la variable de
# entorno SERVICE_NAME (ver entrypoint.sh y docker-compose.yml).
# =============================================================================

# ---------- Etapa 1: build de los 9 microservicios Maven ----------
FROM maven:3.9.6-eclipse-temurin-21 AS maven-build
WORKDIR /build

# Se copia cada modulo a una subcarpeta propia y se compila de forma
# independiente (mismo aislamiento de dependencias que tenian por separado).
COPY services/auth-service                ./auth-service
COPY services/user-service                ./user-service
COPY services/patient-service             ./patient-service
COPY services/doctor-service              ./doctor-service
COPY services/appointment-service         ./appointment-service
COPY services/billing-service             ./billing-service
COPY services/pharmacy-service            ./pharmacy-service
COPY services/notification-service        ./notification-service
COPY services/report-analyst-service      ./report-analyst-service

RUN set -eux; \
    for svc in auth-service user-service patient-service doctor-service \
               appointment-service billing-service pharmacy-service \
               notification-service report-analyst-service; do \
        echo "==> Compilando $svc"; \
        (cd "$svc" && mvn -B -q clean package -DskipTests); \
    done

# ---------- Etapa 2: build de eureka-server (Gradle 8.14) ----------
FROM gradle:8.14-jdk17 AS gradle-build
WORKDIR /build/eureka-server
COPY services/eureka-server ./
RUN gradle bootJar --no-daemon -x test

# ---------- Etapa 3: runtime unico para todos los servicios ----------
FROM eclipse-temurin:21-jre-jammy
RUN addgroup --system hospital && adduser --system --ingroup hospital hospital
WORKDIR /app

# Cada JAR se deja listo con un nombre normalizado <servicio>.jar
COPY --from=maven-build /build/auth-service/target/*.jar                /app/jars/auth-service.jar
COPY --from=maven-build /build/user-service/target/*.jar                /app/jars/user-service.jar
COPY --from=maven-build /build/patient-service/target/*.jar             /app/jars/patient-service.jar
COPY --from=maven-build /build/doctor-service/target/*.jar              /app/jars/doctor-service.jar
COPY --from=maven-build /build/appointment-service/target/*.jar         /app/jars/appointment-service.jar
COPY --from=maven-build /build/billing-service/target/*.jar             /app/jars/billing-service.jar
COPY --from=maven-build /build/pharmacy-service/target/*.jar            /app/jars/pharmacy-service.jar
COPY --from=maven-build /build/notification-service/target/*.jar        /app/jars/notification-service.jar
COPY --from=maven-build /build/report-analyst-service/target/*.jar      /app/jars/report-analyst-service.jar
COPY --from=gradle-build /build/eureka-server/build/libs/*.jar          /app/jars/eureka-server.jar

COPY entrypoint.sh /app/entrypoint.sh
RUN chmod +x /app/entrypoint.sh && chown -R hospital:hospital /app

USER hospital
ENTRYPOINT ["/app/entrypoint.sh"]