-- Tabla para ClienteParticular
CREATE TABLE IF NOT EXISTS clientes_particulares (
    id BIGINT PRIMARY KEY,
    dni VARCHAR(20) UNIQUE,
    fecha_nacimiento DATE,
    FOREIGN KEY (id) REFERENCES clientes(id) ON DELETE CASCADE
);

-- Tabla para Odontologo
CREATE TABLE IF NOT EXISTS odontologos (
    id BIGINT PRIMARY KEY,
    matricula VARCHAR(50) UNIQUE NOT NULL,
    especialidad VARCHAR(100),
    universidad VARCHAR(100),
    fecha_graduacion DATE,
    FOREIGN KEY (id) REFERENCES clientes(id) ON DELETE CASCADE
);

-- Tabla para Clinica
CREATE TABLE IF NOT EXISTS clinicas (
    id BIGINT PRIMARY KEY,
    cuit VARCHAR(20) UNIQUE NOT NULL,
    razon_social VARCHAR(255) NOT NULL,
    nombre_fantasia VARCHAR(255),
    fecha_fundacion DATE,
    sitio_web VARCHAR(255),
    FOREIGN KEY (id) REFERENCES clientes(id) ON DELETE CASCADE
);