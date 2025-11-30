# Shoppy – Microservicios para tienda agrícola

Ecosistema de microservicios (Spring Boot + PostgreSQL + Spring Cloud Gateway) que expone catálogo de productos, usuarios, órdenes y soporte para HuertoHogar. El despliegue actual se hace en un servidor local publicado a Internet mediante un túnel (Cloudflare Tunnel), de modo que el gateway queda accesible en un dominio público sin exponer directamente la red interna.

## Documentación Swagger (vía Gateway)

- Catalog: `https://api.onieto.cl/catalog/swagger-ui/index.html` (API docs: `/catalog/v3/api-docs`)
- Users: `https://api.onieto.cl/users/swagger-ui/index.html` (API docs: `/users/v3/api-docs`)
- Order: `https://api.onieto.cl/order/swagger-ui/index.html` (API docs: `/order/v3/api-docs`)
- Supports: `https://api.onieto.cl/supports/swagger-ui/index.html` (API docs: `/supports/v3/api-docs`)


## Ejecutar con Docker

```bash
# detener y limpiar
docker compose down
# rebuild sin caché (para tomar cambios recientes)
docker compose build --no-cache
# levantar servicios
docker compose up -d
```

Servicios desplegados: `api-gateway`, `catalog-service`, `users-service`, `order-service`, `supports-service`, `postgres`.

## Perfiles y base de datos

- Perfiles Docker usan Postgres interno (`postgres` en la red `shoppy-network`).
- Semillas: `Catalog` carga categorías/unidades y productos iniciales con imágenes en base64; `Users` carga regiones/comunas/roles y usuarios de ejemplo.

## Autenticación

- JWT gestionado en `users-service`, propagado por el gateway. Rutas abiertas: `/api/auth/**`, `/api/regions/**`, `/api/comunas/**`, y las rutas Swagger anteriores.

## Notas sobre el túnel

- El dominio `api.onieto.cl` se expone a Internet mediante Cloudflare Tunnel que redirige al gateway en el servidor local. A nivel de código no requiere cambios: todas las URLs anteriores ya van vía gateway y funcionan tanto en local como en el dominio público.
