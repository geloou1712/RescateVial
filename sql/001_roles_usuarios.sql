-- Esquema inicial de RescateVial. Ejecutar una sola vez en una base nueva.
-- Los campos id y nombre corresponden al record Usuario del backend.
BEGIN;

CREATE TABLE public.roles (
    id_rol BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre_rol VARCHAR(40) NOT NULL UNIQUE,
    descripcion VARCHAR(150),
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO'
);

CREATE TABLE public.usuarios (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(120) NOT NULL UNIQUE,
    contrasena_hash VARCHAR(255) NOT NULL,
    telefono VARCHAR(20),
    fecha_registro TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    id_rol BIGINT NOT NULL,
    CONSTRAINT fk_usuarios_roles
        FOREIGN KEY (id_rol)
        REFERENCES public.roles (id_rol)
);

CREATE INDEX idx_usuarios_id_rol
    ON public.usuarios (id_rol);

INSERT INTO public.roles (nombre_rol, descripcion) VALUES
    ('ADMINISTRADOR', 'Administra la plataforma'),
    ('CIUDADANO', 'Registra incidentes viales'),
    ('OPERADOR', 'Coordina la atención de incidentes');

COMMIT;
