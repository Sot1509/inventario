# Inventario (Java + React + Docker)
Monorepo con dos microservicios (products, inventory) y un frontend React.

Sistema full‑stack con **dos microservicios** (Products e Inventory) y un **frontend React**. Respuestas **JSON:API**, autenticación mediante **API Key**, documentación **Swagger**, resiliencia entre servicios y orquestación con **Docker Compose**.

---

## 1) Arquitectura

```
┌──────────────────────┐     HTTP + X-API-KEY     ┌──────────────────────┐
│  products-service    │ <───────────────────────> │  inventory-service    │
│  Spring Boot + MySQL │                           │  Spring Boot + MySQL  │
└──────────┬───────────┘                           └──────────┬────────────┘
           │                                                 │
           ▼                                                 ▼
        Tabla product                                    Tabla inventory

                     ▲
                     │ HTTP JSON:API
                     ▼
               React (Vite)
```

* **Formato:** `application/vnd.api+json` (JSON:API)
* **Auth:** Header `X-API-KEY: secret123` (configurable)
* **Resilience (inventory → products):** retry, timeout y circuit breaker (Resilience4j)
* **Observabilidad:** `/actuator/health`

---

## 2) Requisitos

* Docker Desktop
* Git
* (Opcional Dev local) Java 21 + Maven, Node 20+ y npm 10+

---

## 3) Ejecución rápida (Docker)

```powershell
# En la raíz del repo
docker compose build
docker compose up -d
```

Servicios:

* Products: [http://localhost:8081](http://localhost:8081)
* Inventory: [http://localhost:8082](http://localhost:8082)
* MySQL: puerto 3307 (usuario: `shop`, pass: `shop`, DB: `shop`)

**Health (protegido por API Key):**

```powershell
curl.exe -H "X-API-KEY: secret123" http://localhost:8081/actuator/health
curl.exe -H "X-API-KEY: secret123" http://localhost:8082/actuator/health
```

**Swagger:**

* Products: [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)
* Inventory: [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html)
  *(para “Try it out”, añade `X-API-KEY` manualmente)*

---

## 4) Frontend (React + Vite)

```powershell
cd frontend
npm install
npm run dev
# abre http://localhost:5173
```

Variables (`frontend/.env`):

```
VITE_API_KEY=secret123
VITE_PRODUCTS_URL=http://localhost:8081
VITE_INVENTORY_URL=http://localhost:8082
```

---

## 5) API (JSON:API) — ejemplos cURL

> **Todas** las peticiones requieren `-H "X-API-KEY: secret123"`.

### 5.1 Products

* **Crear**

```bash
curl -X POST http://localhost:8081/products \
  -H "X-API-KEY: secret123" -H "Content-Type: application/vnd.api+json" \
  -d '{"data":{"type":"products","attributes":{"sku":"SKU-001","name":"Teclado","description":"Teclado mecánico","price":59.90}}}'
```

* **Listar (paginado)**

```bash
curl -H "X-API-KEY: secret123" -H "Accept: application/vnd.api+json" \
  "http://localhost:8081/products?page%5Bnumber%5D=1&page%5Bsize%5D=10"
```

* **Detalle**

```bash
curl -H "X-API-KEY: secret123" http://localhost:8081/products/<id>
```

* **Actualizar parcial**

```bash
curl -X PATCH http://localhost:8081/products/<id> \
  -H "X-API-KEY: secret123" -H "Content-Type: application/vnd.api+json" \
  -d '{"data":{"type":"products","attributes":{"name":"Teclado Pro","price":79.90}}}'
```

* **Eliminar**

```bash
curl -X DELETE -H "X-API-KEY: secret123" http://localhost:8081/products/<id>
```

### 5.2 Inventory

* **Consultar cantidad**

```bash
curl -H "X-API-KEY: secret123" http://localhost:8082/inventories/<productId>
```

* **Decrementar (compra)**

```bash
curl -X POST -H "X-API-KEY: secret123" \
  "http://localhost:8082/inventories/<productId>/decrement?by=1"
```

* **Inicializar/Reponer stock**:

```powershell
$MYSQL = (docker ps --filter "ancestor=mysql:8.0" --format "{{.Names}}"); if ([string]::IsNullOrWhiteSpace($MYSQL)) { $MYSQL = "inventario-mysql-1" }
# ejemplo: setear 10 unidades
$ID = "<productId>"
docker exec -i $MYSQL mysql -u shop -pshop -e "USE shop; \
INSERT INTO inventory (product_id, quantity) VALUES (UUID_TO_BIN('$ID'), 10) \
ON DUPLICATE KEY UPDATE quantity=VALUES(quantity);"
```

---

## 6) Seguridad, errores y resiliencia

* **API Key**: header `X-API-KEY` validado por filtro en ambos servicios.
* **Errores JSON:API** (ejemplos):

```json
{"errors":[{"status":"401","title":"Unauthorized","detail":"Missing or invalid API key"}]}
{"errors":[{"status":"404","title":"Not Found","detail":"Product ... not found"}]}
{"errors":[{"status":"400","title":"Bad Request","detail":"Insufficient stock"}]}
```

* **Resilience (inventory → products)**

  * `retry.maxAttempts=3`, `wait=100ms`
  * `timeLimiter.timeout=2s`
  * `circuitBreaker.failureRateThreshold=50`, `slidingWindow=10`

---

## 7) Justificación técnica (resumen)

* **Java 21 + Spring Boot 3**: maduro, rápido de desarrollar, integración nativa con Swagger, Actuator y Resilience4j.
* **MySQL**: datos transaccionales con relaciones claras; ACID + SQL familiar.
* **JSON:API**: contrato consistente (data/meta/links/errors) y fácil de consumir desde front.
* **React + Vite + React Query**: entrega veloz, cache de datos, estados de carga y error sencillos de manejar.
* **Docker Compose**: desarrollo reproducible y aislado.

---

## 8) Troubleshooting

* **401 Unauthorized**: falta `X-API-KEY`.
* **404 Not Found**: ID inexistente (producto o inventario).
* **400 Insufficient stock**: decrementar más de lo disponible.
* **Swagger no ejecuta**: agrega header `X-API-KEY` en "Authorize" o en cada request.
* **MySQL contenedor**: nombre común `inventario-mysql-1`; confirma con `docker ps`.

---

## 9) Flujo Git y commits (sugerido)

* **Conv. Commits** y ramas por feature.
* Ejemplos usados:

```
chore(repo): scaffold monorepo + estructura inicial + gitignore
feat(backend): agregar proyectos Spring Boot base (products, inventory)
feat(config): configurar application.yml (MySQL docker, API key, swagger)
chore(docker): Dockerfiles multi-stage para servicios backend
chore(compose): orquestación con MySQL + products + inventory
feat(api): JSON:API mínima (products CRUD, inventory get/decrement)
feat(obs): resiliencia (retry/circuit-breaker/time-limiter) en llamada a products
feat(frontend): lista, detalle, cantidad y compra con React Query + Axios
docs(readme): guía completa de instalación, uso y decisiones
```

---



**Fin** 
