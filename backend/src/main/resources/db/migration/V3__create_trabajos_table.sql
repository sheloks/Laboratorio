CREATE TABLE IF NOT EXISTS  trabajos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    descripcion TEXT NOT NULL,
    tipo_trabajo VARCHAR(100) NOT NULL,
    estado ENUM('PENDIENTE', 'EN_PROCESO', 'COMPLETADO', 'ENTREGADO') DEFAULT 'PENDIENTE',
    fecha_entrega_estimada DATE,
    fecha_entrega_real DATE,
    precio DECIMAL(10,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (cliente_id) REFERENCES clientes(id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);