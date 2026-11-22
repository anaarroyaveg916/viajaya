-- =============================================================================
-- ViajaYa — script completo de base de datos (equipo)
-- MySQL 8 · Docker bd_viajaya · puerto 3310 · root / admin123
-- Ejecutar en MySQL Workbench o cliente mysql.
-- spring.jpa.hibernate.ddl-auto=none  →  este script crea el esquema.
-- =============================================================================

CREATE DATABASE IF NOT EXISTS viajaya;
USE viajaya;

-- -----------------------------------------------------------------------------
-- Tablas
-- -----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    correo VARCHAR(150) NOT NULL UNIQUE,
    telefono VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS reservas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    fecha_reserva DATE NOT NULL,
    destino VARCHAR(100) NOT NULL,
    valor DECIMAL(12,2) NOT NULL,
    estado VARCHAR(30) NOT NULL,
    usuario_id INT NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

CREATE TABLE IF NOT EXISTS vuelos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    aerolinea VARCHAR(100) NOT NULL,
    origen VARCHAR(100) NOT NULL,
    destino VARCHAR(100) NOT NULL,
    fecha_salida DATE NOT NULL,
    hora_salida TIME NOT NULL,
    fecha_llegada DATE NULL,
    hora_llegada TIME NULL,
    duracion_minutos INT NULL,
    precio DECIMAL(12, 2) NOT NULL,
    escalas INT NOT NULL DEFAULT 0,
    equipaje VARCHAR(255) NULL,
    cupos_disponibles INT NOT NULL DEFAULT 0
);

-- -----------------------------------------------------------------------------
-- Datos de prueba
-- Si ya existen, comenta los INSERT o limpia antes para evitar duplicados.
-- Fechas de vuelos: ajústalas a >= hoy si /api/vuelos/buscar no las muestra.
-- -----------------------------------------------------------------------------

INSERT INTO usuarios (nombre, apellido, correo, telefono)
VALUES
('Laura', 'Gomez', 'laura@gmail.com', '3001234567'),
('Carlos', 'Ramirez', 'carlos@gmail.com', '3107654321');

INSERT INTO reservas (fecha_reserva, destino, valor, estado, usuario_id)
VALUES
('2026-11-18', 'Cartagena', 850000, 'Confirmada', 1),
('2026-11-19', 'Medellin', 450000, 'Pendiente', 1),
('2026-11-20', 'Santa Marta', 1200000, 'Confirmada', 2);

INSERT INTO vuelos (
    aerolinea, origen, destino, fecha_salida, hora_salida,
    fecha_llegada, hora_llegada, duracion_minutos, precio, escalas, equipaje, cupos_disponibles
) VALUES
('Avianca', 'Bogotá', 'Medellín', '2026-11-25', '08:00:00', '2026-11-25', '09:05:00', 65, 189000.00, 0, '1 pieza de mano 10kg', 12),
('LATAM', 'Bogotá', 'Medellín', '2026-11-25', '14:30:00', '2026-11-25', '15:40:00', 70, 215000.00, 0, 'Cabina + bodega 23kg', 8),
('Wingo', 'Bogotá', 'Cartagena', '2026-11-26', '06:15:00', '2026-11-26', '07:45:00', 90, 159000.00, 0, '1 pieza de mano 10kg', 20),
('Avianca', 'Medellín', 'Bogotá', '2026-11-27', '10:00:00', '2026-11-27', '11:10:00', 70, 195000.00, 0, '1 pieza de mano 10kg', 15),
('LATAM', 'Bogotá', 'Cali', '2026-11-28', '16:00:00', '2026-11-28', '17:10:00', 70, 175000.00, 1, 'Escala en Pereira; cabina 10kg', 5);

-- -----------------------------------------------------------------------------
-- Verificación
-- -----------------------------------------------------------------------------

SHOW TABLES;

SELECT COUNT(*) AS total_usuarios FROM usuarios;
SELECT COUNT(*) AS total_reservas FROM reservas;
SELECT COUNT(*) AS total_vuelos FROM vuelos;

SELECT u.nombre, u.apellido, r.destino, r.fecha_reserva, r.valor, r.estado
FROM usuarios u
INNER JOIN reservas r ON u.id = r.usuario_id;

SELECT id, aerolinea, origen, destino, fecha_salida, precio, cupos_disponibles
FROM vuelos;
