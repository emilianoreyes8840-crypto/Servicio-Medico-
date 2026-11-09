#!/bin/bash
# =============================================================================
# Imagen 1 - Capa de Datos
# -----------------------------------------------------------------------------
# Los 9 microservicios originales usaban 3 motores/instancias distintas:
#   - 6 servicios -> PostgreSQL, cada uno con su propia BD/instancia
#   - 3 servicios (patient, doctor, appointment) -> MariaDB
#
# CORRECCION APLICADA: se unifica todo en UNA sola instancia de PostgreSQL
# (un solo contenedor/imagen), con una base de datos lógica independiente
# por servicio (patrón "database-per-service" dentro de un único motor).
# Esto evita mezclar MariaDB + PostgreSQL en el stack local y reduce el
# sistema a la "Imagen 1 (BD)" que pide el requerimiento.
#
# Este script corre automáticamente la primera vez que se crea el volumen
# del contenedor `db` (mecanismo estándar de la imagen oficial postgres,
# que ejecuta todo lo que esté en /docker-entrypoint-initdb.d/).
# =============================================================================
set -e

DATABASES=(
  auth_db          # auth-service
  user_db          # user-service
  patient_db       # patient-service   (antes MariaDB)
  doctor_db        # doctor-service    (antes MariaDB)
  appointment_db    # appointment-service (antes MariaDB)
  billing_db        # billing-service
  pharmacy_db       # pharmacy-service
  notification_db   # notification-service
  analytics_db      # report-analyst-service
)

for db in "${DATABASES[@]}"; do
  echo "==> Creando base de datos '$db' (si no existe)..."
  psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
    SELECT 'CREATE DATABASE $db'
    WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = '$db')\gexec
EOSQL
done

echo "==> Todas las bases de datos del sistema hospitalario fueron creadas."
