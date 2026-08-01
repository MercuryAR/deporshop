# DeporShop API — Backend REST

API REST para la tienda de zapatillas deportivas DeporShop, construida con Spring Boot.

## Stack

- Java 17, Spring Boot 3.2
- Spring Security + JWT (`io.jsonwebtoken`)
- Spring Data JPA + H2 (en memoria)
- Bean Validation (Jakarta)
- springdoc-openapi (Swagger UI)
- Lombok, Maven

## Requisitos

- Java 17+
- Maven 3.6+

## Instalación y ejecución

```bash
cd back
mvn spring-boot:run
```

O compilando el jar:

```bash
mvn clean package
java -jar target/deporshop-api-1.0.0.jar
```

## Tests

```bash
mvn test
```

48 tests: unitarios de servicios (`ProductoService`, `CategoriaService`, `UsuarioService`, `PedidoService`) y de `JwtTokenProvider` con Mockito, más tests de integración full-stack (`AuthControllerIntegrationTest`, `PedidoControllerIntegrationTest` — contexto Spring real + MockMvc) que cubren el contrato exacto que consume el frontend.

> Nota de entorno: si corrés en un JDK muy nuevo (25+), Mockito puede fallar al instrumentar clases concretas con el mock maker "inline" por defecto. El repo ya incluye `src/test/resources/mockito-extensions/org.mockito.plugins.MockMaker` forzando el mock maker "subclass", que evita el problema.

## Acceso

- **API**: `http://localhost:8080`
- **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`
- **H2 Console**: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:deporshop`, user `sa`, sin password)

## Autenticación

La API usa JWT stateless. Los endpoints de catálogo (`productos`, `categorias`, `carrito`) son públicos; cualquier endpoint que se agregue a futuro sin declarar explícitamente en `SecurityConfig` requiere un token válido.

### `POST /api/auth/register`

```json
{ "nombre": "Juan Perez", "email": "juan@example.com", "password": "secret123" }
```

→ `201 Created`

```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "usuario": { "id": 1, "nombre": "Juan Perez", "email": "juan@example.com" }
}
```

Validaciones: `nombre` no vacío, `email` con formato válido, `password` mínimo 6 caracteres. Email duplicado → `400`.

### `POST /api/auth/login`

```json
{ "email": "juan@example.com", "password": "secret123" }
```

→ `200 OK` con la misma forma que `register`. Credenciales inválidas → `401`.

### Requests autenticados

```
Authorization: Bearer <token>
```

El token expira a las 24hs (`jwt.expiration-ms` en `application.properties`), firmado con HS512, subject = email del usuario.

## Endpoints

### Categorías

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/categorias` | Listar todas |
| GET | `/api/categorias/{id}` | Obtener una |
| POST | `/api/categorias` | Crear |
| PUT | `/api/categorias/{id}` | Actualizar |
| DELETE | `/api/categorias/{id}` | Eliminar |

### Productos

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/productos` | Listar todos (soporta `?categoriaId=` y `?buscar=`) |
| GET | `/api/productos/{id}` | Obtener uno |
| GET | `/api/productos/categoria/{categoriaId}` | Filtrar por categoría |
| GET | `/api/productos/buscar/{termino}` | Buscar por nombre |
| POST | `/api/productos` | Crear |
| PUT | `/api/productos/{id}` | Actualizar |
| DELETE | `/api/productos/{id}` | Eliminar |

### Carrito (por sesión)

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/carrito` | Obtener carrito actual |
| POST | `/api/carrito/agregar?productoId=&cantidad=` | Agregar producto |
| DELETE | `/api/carrito/eliminar/{productoId}` | Eliminar producto |
| PUT | `/api/carrito/actualizar/{productoId}?cantidad=` | Actualizar cantidad |
| DELETE | `/api/carrito/limpiar` | Vaciar carrito |
| GET | `/api/carrito/total` | Total del carrito |

### Pedidos (requiere JWT)

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/pedidos` | Crea un pedido a partir de `{ items: [{ productoId, cantidad }] }` |
| GET | `/api/pedidos/mis-pedidos` | Historial del usuario autenticado, más reciente primero |
| GET | `/api/pedidos/{id}` | Un pedido puntual — `403` si no es del usuario autenticado |

El precio de cada item **se recalcula siempre en el servidor** a partir del `Producto` actual (nunca se usa el precio que mande el cliente). El endpoint no depende del carrito de sesión: el frontend manda los items desde su propio estado (Zustand + localStorage), porque el carrito de sesión HTTP no persiste de forma confiable en una SPA cross-origin. Carrito vacío/sin items → `400`. Producto inexistente → `404`.

## Manejo de errores

Todas las respuestas de error siguen esta forma (`exception/GlobalExceptionHandler.java`):

```json
{ "status": 400, "error": "mensaje", "timestamp": "2026-07-31T19:51:02" }
```

Los errores de validación (`@Valid`) además incluyen `fieldErrors` con el detalle por campo.

## Base de datos

H2 en memoria (`spring.jpa.hibernate.ddl-auto=create-drop`). Los datos se recrean en cada arranque con 3 categorías y 6 productos de ejemplo (`config/DataLoader.java`, precios en ARS) y **se pierden al reiniciar o redeployar** — incluyendo usuarios registrados y pedidos. Un JWT emitido antes de un restart sigue siendo válido (la firma no cambia), pero cualquier endpoint que busque al usuario por email va a devolver `404 Usuario no encontrado` hasta que se vuelva a registrar. Para producción real, cambiar a una base persistente (PostgreSQL, MySQL) en `application.properties`.

## Notas

- CORS: orígenes permitidos configurables vía `CORS_ALLOWED_ORIGINS` (variable de entorno, coma-separada), default `http://localhost:5173,http://localhost:3000`. Ver `app.cors.allowed-origins` en `application.properties` y `SecurityConfig`.
- `JWT_SECRET` también es variable de entorno (con default solo para dev local — no reusar ese default si el repo es público).
- Los campos `precio`, `stock`, etc. en `Producto`/`Categoria` tienen validación `@Positive`/`@NotBlank`: un `PUT` que omita esos campos ahora es rechazado con `400` en vez de conservar el valor anterior silenciosamente.
