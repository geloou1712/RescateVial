# Backend en Render

## Archivos y comportamiento

- `Dockerfile`: compila y ejecuta las pruebas con el wrapper Gradle y JDK 17.
  La imagen final contiene el JAR y JRE 17 y ejecuta como usuario sin privilegios.
- `.dockerignore`: solo permite los archivos necesarios para compilar; excluye
  `.env`, Git, resultados locales y claves privadas del contexto de Docker.
- `render.yaml`: describe un Web Service Docker gratuito, basado en `develop`,
  con despliegues automáticos desactivados. No crea una base de datos.
- `application.properties`: escucha en `0.0.0.0` y `${PORT:8080}`; deshabilita
  la inicialización automática de SQL. Neon se reutiliza sin ejecutar scripts.
- CORS usa coincidencias exactas de origen, sin comodines.

## Antes de desplegar

1. Revisar y fusionar el PR de despliegue hacia `develop`.
2. Comprobar que el commit elegido contiene `Dockerfile`, `.dockerignore` y
   `render.yaml`. No desplegar una rama antigua ni hacer commits directos en main.
3. En Render, crear **New > Web Service**, conectar el repositorio
   `geloou1712/RescateVial`, seleccionar `develop`, Language **Docker**,
   Dockerfile Path `./Dockerfile`, contexto raíz y plan **Free**.
   Dejar Docker Command vacío para usar ENTRYPOINT. No configurar pre-deploy SQL.
4. Configurar Health Check Path `/api/health` y Auto Deploy **Off**.
   Como alternativa, usar New > Blueprint con el `render.yaml` ya fusionado;
   elegir un solo método para no crear servicios duplicados.
5. Introducir las variables siguientes en el panel privado de Render.

| Variable | Valor |
| --- | --- |
| `DB_URL` | URL JDBC de la base Neon existente: `jdbc:postgresql://<host>/<base>?sslmode=require`, conservando los parámetros TLS adicionales necesarios. Sin usuario ni contraseña dentro de la URL. |
| `DB_USERNAME` | Usuario de esa base Neon. |
| `DB_PASSWORD` | Contraseña de ese usuario. |
| `JWT_SECRET` | Secreto aleatorio de al menos 32 caracteres; nunca subirlo a Git. |
| `JWT_EXPIRACION_HORAS` | `8` |
| `CORS_ORIGENES` | `https://rescatevialfr.vercel.app` |

Render proporciona `PORT` (normalmente `10000`). `EXPOSE` documenta el puerto;
Spring Boot realmente escucha en el valor de la variable. Para Docker local,
establecer `PORT=10000` y publicar `-p 10000:10000`.

No usar ARG/ENV con secretos en el Dockerfile ni subir `.env`. Las variables de
Render se suministran al ejecutar el contenedor. No imprimir tokens, credenciales
ni cuerpos de login en logs o capturas. No crear otra base ni ejecutar
`sql/001_roles_usuarios.sql` sobre Neon existente.

## Orígenes del frontend

El 26 de noviembre de 2026, producción respondió HTTP 200. La URL
`https://rescatevialfr-git-develop-angel-3b35.vercel.app` redirigió al login de
Vercel; no quedó verificada su aplicación. Solo añadir ese origen si el equipo
confirma que lo necesita, separado por coma del origen de producción, sin barra
final ni rutas. No autorizar `*.vercel.app` ni `*`.

## Verificación del despliegue real

Esperar a que Render muestre Live y anotar la URL HTTPS y el commit desplegado.
Con una cuenta de prueba existente, comprobar:

| Solicitud | Resultado esperado |
| --- | --- |
| `GET /api/health` | HTTP 200, `OK` |
| `GET /api/health/db` | HTTP 200, `{"estado":"UP"}` |
| `POST /api/auth/login` con email/password | HTTP 200 y token, sin mostrarlo |
| `GET /api/auth/yo` con Bearer | HTTP 200 y usuario de prueba correcto |
| `GET /api/tipos-incidente` con Bearer | HTTP 200 y datos |
| OPTIONS de login con Origin autorizado, método POST y content-type | Permite el origen exacto y método POST |
| OPTIONS de tipos-incidente con Origin autorizado, método GET y authorization | Permite el origen exacto, método GET y header authorization |
| OPTIONS con origen ajeno | Rechazado, sin Access-Control-Allow-Origin |

Repetir preflight por cada origen autorizado. Verificar también que las rutas
protegidas sin token respondan 401. Un build local correcto no demuestra que
Neon, CORS o el servicio público funcionen en Render.

## Coordinación con Vercel

Una vez verificada la API, entregar al chat Front:

```text
VITE_API_URL=<URL_HTTPS_REAL_ASIGNADA_POR_RENDER>
```

No añadir `/api` ni una barra final: el cliente agrega las rutas `/api/...`.
Actualizar los entornos Vercel correspondientes y volver a desplegar el frontend;
Vite incorpora esta variable durante el build. Verificar el login desde la web.

## Límites y fuentes

El plan Free puede suspenderse tras inactividad; el primer acceso puede tardar.
Incidentes y asignaciones todavía usan memoria y se pierden al reiniciar:
este despliegue no implementa su persistencia ni cambia sus contratos.

- https://render.com/docs/docker
- https://render.com/docs/web-services
- https://render.com/docs/blueprint-spec
- https://render.com/docs/free
