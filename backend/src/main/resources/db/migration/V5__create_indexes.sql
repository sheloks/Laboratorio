-- Índices para búsquedas frecuentes
CREATE INDEX idx_clientes_tipo ON clientes(tipo_cliente);
CREATE INDEX idx_clientes_email ON clientes(email);
CREATE INDEX idx_clientes_activo ON clientes(activo);

-- Índices para las tablas hijas
CREATE INDEX idx_particulares_dni ON clientes_particulares(dni);
CREATE INDEX idx_odontologos_matricula ON odontologos(matricula);
CREATE INDEX idx_clinicas_cuit ON clinicas(cuit);