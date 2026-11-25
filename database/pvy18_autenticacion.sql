USE viajaya;

-- =========================================================
-- VIAJAYA - PVY-18: INICIAR Y CERRAR SESION
-- Migracion de base de datos: hash de contrasenas
-- =========================================================

ALTER TABLE usuarios
    ADD COLUMN password_hash VARCHAR(255) NULL AFTER telefono;

-- Ejemplo de hash BCrypt (corresponde a la clave de prueba: ViajaYa123!)
-- Al implementar BCryptPasswordEncoder en el servicio, los nuevos usuarios
-- y cambios de clave generaran su propio hash automaticamente.
UPDATE usuarios
SET password_hash = '$2a$10$7EqJtq98hPqEX7fNZaFWoOhi5Vz8q4G7x1x1x1x1x1x1x1x1x1x1u'
WHERE correo IN ('laura@gmail.com', 'carlos@gmail.com','santik@gmail.com')
  AND password_hash IS NULL;