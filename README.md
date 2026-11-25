# RescateVial

API para registrar y consultar usuarios, incidentes y asignaciones de un sistema de atención vial. La autenticación y los usuarios se guardan en PostgreSQL (Neon). Los demás módulos conservan sus estructuras en memoria y se reinician al detener la aplicación.

## Requisitos

- Java 17 y una base PostgreSQL disponible para autenticación y usuarios.
- El proyecto incluye Gradle Wrapper; no es necesario instalar Gradle por separado.

## Configuración local

1. Copia `.env.example` a un archivo llamado `.env` en la raíz del proyecto. En PowerShell puedes ejecutar:

   ```powershell
   Copy-Item .env.example .env
   ```

2. Ajusta en `.env` `DB_URL`, `DB_USERNAME` y `DB_PASSWORD` según tu instalación local de PostgreSQL. La URL de ejemplo apunta a una base llamada `rescatevial`.
3. No subas `.env` a Git: está excluido en `.gitignore`. El archivo `.env.example` muestra los nombres de las variables sin guardar contraseñas.

Spring carga `.env` desde la raíz al iniciar la aplicación. Configura también `JWT_SECRET`, `JWT_EXPIRACION_HORAS` (8 por defecto) y `CORS_ORIGENES`. Para Neon usa una URL JDBC con `sslmode=require&channelBinding=require`. El esquema inicial está en `sql/001_roles_usuarios.sql`: se aplica una sola vez a una base nueva. En la base de desarrollo RescateVial ya está aplicado.

## Ejecutar

Desde la raíz del proyecto, en Windows:

```powershell
.\gradlew.bat bootRun
```

Para compilar y ejecutar las pruebas:

```powershell
.\gradlew.bat build
```

## Estado de la aplicación

- `GET /api/health`: devuelve `200 OK` y el texto `OK` cuando la aplicación responde.
- `GET /api/health/db`: devuelve `200 OK` con `{"estado":"UP"}` si la conexión es válida. Si no puede conectarse o no hay una fuente de datos, devuelve `503 Service Unavailable` con `{"estado":"DOWN"}`.

La aplicación debe estar iniciada para consultar ambos endpoints. Para comprobar el resultado `UP` se necesita una base PostgreSQL disponible y configurada.

## Incidentes pendientes

Los incidentes con estado `PENDIENTE` se agregan a una cola propia basada en nodos. `IncidenteService` la usa para mantener el orden y consultar los pendientes.

- `GET /api/incidentes/pendientes`: devuelve la lista de incidentes pendientes.

# Integrantes

| Autor | GitHub |
| --- | --- |
| Angel Omar Luna Castillo | [@geloou1712](https://github.com/geloou1712) |
| Santiago Ley Rodriguez | [@santiagoleymy-lang](https://github.com/santiagoleymy-lang) |

## Módulos y endpoints

- **Usuarios:** `GET /api/usuarios`, `GET /api/usuarios/{id}`, `POST /api/usuarios`
- **Incidentes:** `GET /api/incidentes`, `GET /api/incidentes/{id}`, `POST /api/incidentes`, `GET /api/incidentes/pendientes`
- **Asignaciones:** `GET /api/asignaciones`, `GET /api/asignaciones/{id}`, `POST /api/asignaciones`
- **Tipos de incidente:** `GET /api/tipos-incidente`, `GET /api/tipos-incidente/{id}`, `POST /api/tipos-incidente`
- **Unidades:** `GET /api/unidades`, `GET /api/unidades/{id}`, `POST /api/unidades`
- **Servicios de apoyo:** `GET /api/servicios-apoyo`, `GET /api/servicios-apoyo/{id}`, `POST /api/servicios-apoyo`

### Salud

- `GET /api/health`
- `GET /api/health/db`

## Autenticación y permisos

- `POST /api/auth/registro`: recibe `name`, `email`, `password` (mínimo 6 caracteres) y `role` opcional. Devuelve el usuario con HTTP 201.
- `POST /api/auth/login`: recibe `email`, `password` y `role` opcional. Devuelve `{ "token": "...", "user": { "id": 1, "name": "...", "email": "...", "role": "ciudadano", "roles": ["ciudadano"] } }`.
- `GET /api/auth/yo`: requiere `Authorization: Bearer <token>` y devuelve el usuario autenticado.
- Los errores incluyen `message`, que consume el frontend. Se conservan los campos anteriores del error (`mensaje`, estado, fecha y ruta).
- Todas las rutas `/api/**` requieren token, excepto login, registro y health. Los preflight OPTIONS se atienden mediante CORS.
- `/api/usuarios` y `/api/usuarios/{id}` requieren `ADMINISTRADOR`. La lista y el detalle no exponen hashes.
- El registro público admite ciudadano y operador. El rol por defecto es ciudadano y rechaza administrador. Los roles internos de Neon y del JWT son `CIUDADANO`, `OPERADOR` y `ADMINISTRADOR`; las respuestas públicas usan minúsculas para conservar el contrato del frontend.

El alta administrativa `POST /api/usuarios` recibe `nombre`, `correo`, `telefono`, `rol`, `estado` y `password`. La contraseña es obligatoria y se guarda con BCrypt. El administrador puede crear otras cuentas desde este endpoint protegido.

## Frontend local

El frontend está en la carpeta RescateVial-front. Su `.env.local` usa `VITE_API_URL=http://localhost:8080`; iniciar con `npm run dev` y abrir `http://localhost:5173`. Para publicar, configurar una URL HTTPS del backend y autorizar el origen del frontend en `CORS_ORIGENES`.

## Cuentas y comprobaciones

Se prepararon cuentas locales de prueba para administrador, ciudadano y operador. Los correos y contraseñas están únicamente en `.env`, en las variables `TEST_ADMIN_*`, `TEST_CIUDADANO_*` y `TEST_OPERADOR_*`. No se incluyen credenciales en Git.

Para el primer administrador de otra base: registrar una cuenta normal, cambiar únicamente su `id_rol` al rol ADMINISTRADOR con autorización del responsable y volver a iniciar sesión. La API pública no permite registrar administradores.

La colección `postman/RescateVial-auth.postman_collection.json` cubre registro, login, perfil, roles, errores, CORS y administración. Importar `postman/auth.environment.example.json`, completar las contraseñas desde el `.env` local y seleccionar ese entorno. Con Postman CLI se puede ejecutar:

```powershell
postman collection run postman/RescateVial-auth.postman_collection.json -e RUTA_DEL_ENTORNO_PRIVADO.json --no-report-events
```

Verificación local: 15 pruebas Java, 26 solicitudes HTTP con 57 aserciones, 15 pruebas del frontend y compilación Vite correctas. También se probó en navegador el registro, los errores de login, el acceso al panel, la restricción de Usuarios y el cierre de sesión.

## Estructuras de datos

- **Lista enlazada propia:** `ListaEnlazada`, implementada con nodos y usada por los servicios para mantener sus registros en memoria.
- **Cola propia:** `ColaIncidentesPendientes`, implementada con nodos y usada para mantener el orden de los incidentes pendientes.