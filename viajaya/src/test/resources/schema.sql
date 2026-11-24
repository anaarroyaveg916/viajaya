-- Esquema para las pruebas (H2 en modo MySQL).
-- Mismas tablas de database/viajaya_completo.sql + migración database/pvy20_iniciar_reserva.sql.
DROP TABLE IF EXISTS bloqueos_cupos;
DROP TABLE IF EXISTS pasajeros;
DROP TABLE IF EXISTS reservas;
DROP TABLE IF EXISTS vuelos;
DROP TABLE IF EXISTS usuarios;

CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    correo VARCHAR(150) NOT NULL UNIQUE,
    telefono VARCHAR(20)
);

CREATE TABLE vuelos (
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
    cupos_disponibles INT NOT NULL DEFAULT 0,
    cupos_totales INT NOT NULL DEFAULT 0
);

CREATE TABLE reservas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    fecha_reserva DATE NOT NULL,
    destino VARCHAR(100) NOT NULL,
    valor DECIMAL(12,2) NOT NULL,
    estado VARCHAR(30) NOT NULL,
    usuario_id INT NOT NULL,
    vuelo_id INT NULL,
    cantidad_pasajeros INT NULL,
    correo_contacto VARCHAR(150) NULL,
    telefono_contacto VARCHAR(20) NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (vuelo_id) REFERENCES vuelos(id)
);

CREATE TABLE pasajeros (
    id INT AUTO_INCREMENT PRIMARY KEY,
    reserva_id INT NOT NULL,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    tipo_documento VARCHAR(30) NOT NULL,
    numero_documento VARCHAR(30) NOT NULL,
    fecha_nacimiento DATE NOT NULL,
    FOREIGN KEY (reserva_id) REFERENCES reservas(id) ON DELETE CASCADE
);

CREATE TABLE bloqueos_cupos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    reserva_id INT NOT NULL UNIQUE,
    vuelo_id INT NOT NULL,
    cantidad_cupos INT NOT NULL,
    fecha_inicio DATETIME NOT NULL,
    fecha_vencimiento DATETIME NOT NULL,
    estado VARCHAR(20) NOT NULL,
    FOREIGN KEY (reserva_id) REFERENCES reservas(id),
    FOREIGN KEY (vuelo_id) REFERENCES vuelos(id)
);
