## Incidentes pendientes

Los incidentes con estado `PENDIENTE` se agregan a una cola propia basada en nodos. `IncidenteService` la usa para mantener el orden y consultar los pendientes.

- `GET /api/incidentes/pendientes`: devuelve la lista de incidentes pendientes.

## Manejo de errores

Los errores de validación, JSON inválido, parámetros de tipo incorrecto y registros no encontrados de usuarios e incidentes se responden con una estructura JSON común. Por ejemplo:

```json
{
  "fecha": "2026-11-13T18:30:00Z",
  "estado": 404,
  "error": "Not Found",
  "mensaje": "El usuario no existe",
  "ruta": "/api/usuarios/999"
}
```

Los datos inválidos devuelven el estado HTTP `400`; un usuario o incidente no encontrado devuelve `404`.
