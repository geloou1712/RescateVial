## Incidentes pendientes

Los incidentes con estado `PENDIENTE` se agregan a una cola propia basada en nodos. `IncidenteService` la usa para mantener el orden y consultar los pendientes.

- `GET /api/incidentes/pendientes`: devuelve la lista de incidentes pendientes.