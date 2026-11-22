-- Esquema para las pruebas (H2 en modo MySQL). Mismas tablas de viajaya_completo.sql.
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

CREATE TABLE reservas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    fecha_reserva DATE NOT NULL,
    destino VARCHAR(100) NOT NULL,
    valor DECIMAL(12,2) NOT NULL,
    estado VARCHAR(30) NOT NULL,
    usuario_id INT NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
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
    cupos_disponibles INT NOT NULL DEFAULT 0
);
