# RescateVial

API para registrar y consultar usuarios, incidentes y asignaciones de un sistema de atención vial. Actualmente los datos de los módulos se almacenan en memoria y se reinician al detener la aplicación.

## Requisitos

- Java 17 y PostgreSQL si se desea comprobar la conexión a la base de datos.
- El proyecto incluye Gradle Wrapper; no es necesario instalar Gradle por separado.

## Configuración local

1. Copia `.env.example` a un archivo llamado `.env` en la raíz del proyecto. En PowerShell puedes ejecutar:

   ```powershell
   Copy-Item .env.example .env
   ```

2. Ajusta en `.env` `DB_URL`, `DB_USERNAME` y `DB_PASSWORD` según tu instalación local de PostgreSQL. La URL de ejemplo apunta a una base llamada `rescatevial`.
3. No subas `.env` a Git: está excluido en `.gitignore`. El archivo `.env.example` muestra los nombres de las variables sin guardar contraseñas.

Spring carga el archivo `.env` desde la raíz al iniciar la aplicación. El endpoint de salud de base de datos comprueba la conexión, pero los módulos todavía no guardan información en PostgreSQL.

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
