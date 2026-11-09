#!/bin/sh
# Selecciona que microservicio ejecutar dentro de la imagen "backend" segun
# la variable de entorno SERVICE_NAME, inyectada por cada servicio en
# docker-compose.yml. Permite que UNA sola imagen sirva a los 10 contenedores
# (9 microservicios + eureka-server) sin duplicar la imagen ni mezclarlos
# en un unico proceso.
set -e

if [ -z "$SERVICE_NAME" ]; then
  echo "ERROR: falta la variable de entorno SERVICE_NAME." >&2
  echo "Valores validos: auth-service, user-service, patient-service, doctor-service," >&2
  echo "  appointment-service, billing-service, pharmacy-service, notification-service," >&2
  echo "  report-analyst-service, eureka-server" >&2
  exit 1
fi

JAR="/app/jars/${SERVICE_NAME}.jar"

if [ ! -f "$JAR" ]; then
  echo "ERROR: no existe el jar para SERVICE_NAME='${SERVICE_NAME}' ($JAR)." >&2
  exit 1
fi

echo "==> Iniciando ${SERVICE_NAME} ($JAR) en el puerto ${SERVER_PORT:-<default>}"
exec java ${JAVA_OPTS} -jar "$JAR"
