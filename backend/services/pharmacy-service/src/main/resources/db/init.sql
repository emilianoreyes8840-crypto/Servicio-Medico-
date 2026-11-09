-- =========================================================
-- Script de inicialización - pharmacy_db (pharmacy-service)
-- =========================================================
-- Ejecutar contra la base de datos "pharmacy_db".
-- Nota: con spring.jpa.hibernate.ddl-auto=update Hibernate
-- puede crear/actualizar estas tablas automáticamente; este
-- script se entrega para despliegues manuales / control de versión.

CREATE TABLE IF NOT EXISTS medicines (
    id            BIGSERIAL PRIMARY KEY,
    code          VARCHAR(50)  NOT NULL UNIQUE,
    name          VARCHAR(150) NOT NULL,
    description   VARCHAR(500),
    stock         INTEGER      NOT NULL CHECK (stock >= 0),
    unit_price    NUMERIC(12,2) NOT NULL CHECK (unit_price > 0),
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS prescriptions (
    id                  BIGSERIAL PRIMARY KEY,
    prescription_code   VARCHAR(50)  NOT NULL UNIQUE,
    patient_id          BIGINT       NOT NULL,
    patient_email       VARCHAR(150) NOT NULL,
    issued_at           TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_medicines_code ON medicines(code);
CREATE INDEX IF NOT EXISTS idx_prescriptions_patient_id ON prescriptions(patient_id);

-- =========================================================
-- Datos de prueba
-- =========================================================
INSERT INTO medicines (code, name, description, stock, unit_price) VALUES
    ('MED-001', 'Paracetamol 500mg',  'Analgésico y antipirético, caja x 20 tabletas', 150, 3.50),
    ('MED-002', 'Ibuprofeno 400mg',   'Antiinflamatorio no esteroideo, caja x 10 tabletas', 100, 5.20),
    ('MED-003', 'Amoxicilina 500mg',  'Antibiótico de amplio espectro, caja x 15 cápsulas', 80, 12.00),
    ('MED-004', 'Loratadina 10mg',    'Antihistamínico, caja x 10 tabletas', 60, 4.75),
    ('MED-005', 'Omeprazol 20mg',     'Protector gástrico, caja x 14 cápsulas', 90, 8.90)
ON CONFLICT (code) DO NOTHING;
