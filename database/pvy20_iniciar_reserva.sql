
USE viajaya;

-- =========================================================
-- VIAJAYA - PVY-20: INICIO DE RESERVA
-- Migracion de base de datos
-- Duracion del bloqueo temporal: 10 minutos
-- =========================================================


-- =========================================================
-- 1. AMPLIAR LA TABLA RESERVAS
-- =========================================================

-- Se conservan las reservas existentes.
-- Los nuevos campos permiten relacionarlas con vuelos.

ALTER TABLE reservas
    ADD COLUMN vuelo_id INT NULL,
    ADD COLUMN cantidad_pasajeros INT NULL,
    ADD COLUMN correo_contacto VARCHAR(150) NULL,
    ADD COLUMN telefono_contacto VARCHAR(20) NULL;

ALTER TABLE reservas
    ADD CONSTRAINT fk_reserva_vuelo
    FOREIGN KEY (vuelo_id)
    REFERENCES vuelos(id);

CREATE INDEX idx_reservas_vuelo
    ON reservas(vuelo_id);


-- =========================================================
-- 2. CREAR TABLA PASAJEROS
-- =========================================================

CREATE TABLE IF NOT EXISTS pasajeros (
    id INT AUTO_INCREMENT PRIMARY KEY,
    reserva_id INT NOT NULL,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    tipo_documento VARCHAR(30) NOT NULL,
    numero_documento VARCHAR(30) NOT NULL,
    fecha_nacimiento DATE NOT NULL,

    CONSTRAINT fk_pasajero_reserva
        FOREIGN KEY (reserva_id)
        REFERENCES reservas(id)
        ON DELETE CASCADE,

    INDEX idx_pasajeros_reserva (reserva_id)
);


-- =========================================================
-- 3. CREAR TABLA BLOQUEOS_CUPOS
-- =========================================================

CREATE TABLE IF NOT EXISTS bloqueos_cupos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    reserva_id INT NOT NULL,
    vuelo_id INT NOT NULL,
    cantidad_cupos INT NOT NULL,
    fecha_inicio DATETIME NOT NULL,
    fecha_vencimiento DATETIME NOT NULL,
    estado VARCHAR(20) NOT NULL,

    CONSTRAINT fk_bloqueo_reserva
        FOREIGN KEY (reserva_id)
        REFERENCES reservas(id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_bloqueo_vuelo
        FOREIGN KEY (vuelo_id)
        REFERENCES vuelos(id),

    CONSTRAINT uk_bloqueo_reserva
        UNIQUE (reserva_id),

    INDEX idx_bloqueo_vencimiento
        (estado, fecha_vencimiento),

    INDEX idx_bloqueo_vuelo_estado
        (vuelo_id, estado)
);


-- =========================================================
-- 4. VERIFICAR ESTRUCTURAS
-- =========================================================

DESCRIBE reservas;

DESCRIBE pasajeros;

DESCRIBE bloqueos_cupos;


-- =========================================================
-- 5. VERIFICAR RELACIONES
-- =========================================================

SELECT
    TABLE_NAME,
    COLUMN_NAME,
    CONSTRAINT_NAME,
    REFERENCED_TABLE_NAME,
    REFERENCED_COLUMN_NAME
FROM information_schema.KEY_COLUMN_USAGE
WHERE TABLE_SCHEMA = 'viajaya'
  AND TABLE_NAME IN (
      'reservas',
      'pasajeros',
      'bloqueos_cupos'
  )
  AND REFERENCED_TABLE_NAME IS NOT NULL;
