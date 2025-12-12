-- Datos iniciales para desarrollo

-- Insertar laboratorio de prueba
INSERT IGNORE INTO laboratorios (id, nombre, cuit, razon_social, telefono, email, direccion, punto_venta, suscripcion_activa, fecha_creacion) 
VALUES (1, 'Laboratorio Dental Plus', '30-12345678-9', 'Laboratorio Dental Plus S.A.', '+541134567890', 'info@dentalplus.com', 'Av. Corrientes 1234', '0001', true, NOW());

-- Insertar usuarios de prueba (password: 123456 - encriptado con BCrypt)
INSERT IGNORE INTO usuarios (id, email, password, rol, nombre, activo, laboratorio_id, fecha_creacion) 
VALUES 
(1, 'admin@dentalplus.com', '\\\', 'ADMIN', 'Administrador Principal', true, 1, NOW()),
(2, 'tecnico@dentalplus.com', '\\\', 'TECNICO', 'Técnico Especializado', true, 1, NOW()),
(3, 'facturacion@dentalplus.com', '\\\', 'FACTURACION', 'Responsable Facturación', true, 1, NOW());
