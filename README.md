# API Gateway Games

Proyecto de ejemplo con arquitectura de microservicios en **Java + Spring Boot** usando:

- **Auth-API** para registrar usuarios y emitir JWT.
- **Game Service API** para gestionar juegos (CRUD).
- **API Gateway** para enrutar peticiones y proteger endpoints con JWT.

El flujo recomendado es consumir todo a través del Gateway (`http://localhost:8082`).

## Arquitectura

- `Auth-API` (puerto `8081`):
  - Endpoint público para crear usuario y devolver token JWT.
- `game-service-api` (puerto `8080`):
  - Endpoints de juegos (`/v1/games/**`).
- `api-gateway` (puerto `8082`):
  - Rutas:
    - `/v1/auth/**` -> `Auth-API`
    - `/v1/games/**` -> `game-service-api`
  - Añade seguridad JWT para endpoints protegidos.

## Requisitos

- Java **17+**
- Maven (opcional, porque el proyecto incluye `mvnw`)
- PostgreSQL en local

## Configuración de base de datos

Este proyecto espera dos bases de datos PostgreSQL:

- `users` (usada por `Auth-API`)
- `reto` (usada por `game-service-api`)

Con la configuración por defecto del proyecto:

- Usuario: `postgres`
- Password: `1234`
- Host: `localhost`
- Puerto: `5432`

Puedes crearlas con:

```sql
CREATE DATABASE users;
CREATE DATABASE reto;
```

> Si quieres usar otras credenciales/host, modifica los `application.yaml` de cada servicio.

## Cómo ejecutar el proyecto

> Abre **3 terminales**, una por servicio.

### 1) Levantar Auth-API

```bash
cd Auth-API
./mvnw spring-boot:run
```

### 2) Levantar Game Service API

```bash
cd game-service-api
./mvnw spring-boot:run
```

### 3) Levantar API Gateway

```bash
cd api-gateway
./mvnw spring-boot:run
```

Cuando todo esté arriba, consume los endpoints desde:

- `http://localhost:8082`

## Endpoints principales (vía Gateway)

### Auth

- `POST /v1/auth/register`
  - Body JSON:

```json
{
  "email": "user@example.com",
  "password": "123456"
}
```

  - Respuesta esperada: token JWT.

### Games (protegidos con JWT)

Para estos endpoints añade header:

- `Authorization: Bearer <TOKEN>`

Endpoints:

- `POST /v1/games`
- `GET /v1/games`
- `GET /v1/games/{id}`
- `PUT /v1/games/{id}`
- `DELETE /v1/games/{id}`

## Uso de Postman

He añadido (o voy a añadir) una colección de Postman exportada al repositorio para facilitar las pruebas de todos los endpoints.

### Pasos para importarla

1. Abre Postman.
2. Pulsa **Import**.
3. Selecciona el archivo `.postman_collection.json` del repositorio.
4. Importa la colección.
5. Ejecuta primero la petición de registro (`/v1/auth/register`) para obtener el token.
6. Usa ese token en las peticiones protegidas (`Authorization: Bearer <TOKEN>`).

> Recomendación: guarda el token en una variable de entorno de Postman (por ejemplo `jwtToken`) y úsala en el header como `Bearer {{jwtToken}}`.

## Estructura del repositorio

```text
API-Gateway-Games/
├── Auth-API/
├── api-gateway/
└── game-service-api/
```

## Notas

- Si algún servicio no arranca, revisa:
  - que PostgreSQL esté activo,
  - que las bases de datos existan,
  - y que los puertos `8080`, `8081`, `8082` estén libres.
- El endpoint público actual en Auth es `register`; el resto de rutas de juegos pasan por autenticación JWT en el gateway.
