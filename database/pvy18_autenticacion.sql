USE viajaya;

-- =========================================================
-- VIAJAYA - PVY-18: INICIAR Y CERRAR SESION
-- Migracion de base de datos: hash de contrasenas
-- =========================================================

ALTER TABLE usuarios
    ADD COLUMN password_hash VARCHAR(255) NULL AFTER telefono;

-- Hash BCrypt de la clave de prueba: ViajaYa123
-- Los registros vía POST /api/usuarios/registro generan su propio hash.
UPDATE usuarios
SET password_hash = '$2b$10$iwGdLJbt3RKAxNQwyS.IIep3UtHTJZznOGO796jR3zRgp0sOB.BMu'
WHERE correo IN ('laura@gmail.com', 'carlos@gmail.com', 'santik@gmail.com')
  AND password_hash IS NULL;
