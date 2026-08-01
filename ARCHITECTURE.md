# Arquitectura

Decisiones de diseño detrás de DeporShop. Para instrucciones de instalación ver [README.md](README.md); para el detalle de endpoints, [back/README.md](back/README.md) y [front/README.md](front/README.md).

## Vista general

```
┌─────────────────┐        HTTP/JSON        ┌──────────────────────┐
│  React + TS SPA  │ ───────────────────────▶ │  Spring Boot REST API │
│  (Vite, :5173)   │ ◀─────────────────────── │  (:8080)              │
└─────────────────┘                          └──────────┬───────────┘
                                                          │
                                                          ▼
                                                    H2 (en memoria)
```

No hay server-side rendering ni BFF: el frontend es una SPA pura que habla directo con la API vía `axios`, sin capa intermedia.

## Backend

### Capas: Controller → Service → Repository → Entity

Se mantiene la separación clásica de Spring: los controllers no tienen lógica de negocio (delegan a los services), los services no conocen HTTP, y JPA se limita a los repositories. Esto ya existía en el proyecto original (`ProductoController` → `ProductoService` → `ProductoRepository`); el trabajo de auth siguió el mismo patrón.

### DTOs solo donde hay validación de entrada

Los endpoints de catálogo (`Producto`, `Categoria`) siguen serializando la entidad JPA directamente — es una decisión pragmática, no ideal para una API grande, pero evita over-engineering en un catálogo de solo-lectura mayormente público. Los endpoints de auth sí usan DTOs (`RegisterRequest`, `LoginRequest`, `AuthResponse`, `UsuarioResponse`) porque:
1. Necesitan validación (`@NotBlank`, `@Email`, `@Size`) que no debe vivir en la entidad de persistencia.
2. La entidad `Usuario` tiene un campo `password` (hash BCrypt) que **nunca** debe salir en una respuesta JSON — `UsuarioResponse` lo excluye por construcción en vez de depender de anotaciones de serialización que alguien podría olvidar.

### Autenticación: JWT stateless, no sesiones

- `POST /api/auth/register` y `/login` devuelven un JWT firmado (HS512, 24hs de expiración, subject = email).
- `JwtAuthenticationFilter` valida el header `Authorization: Bearer <token>` en cada request y puebla el `SecurityContext`.
- `SessionCreationPolicy.STATELESS` — no hay `HttpSession` de por medio, lo que permite escalar horizontalmente sin sticky sessions.
- Se eligió JWT sobre sesiones tradicionales porque el frontend es una SPA separada (otro origen en dev: `:5173` vs `:8080`), y JWT evita la complejidad de compartir cookies de sesión entre orígenes.

**Trade-off explícito**: los endpoints de catálogo (`/api/productos/**`, `/api/categorias/**`, `/api/carrito/**`) quedaron públicos en `SecurityConfig` para no romper el comportamiento existente al introducir auth. En una API de producción real, mutaciones (`POST`/`PUT`/`DELETE`) sobre productos deberían requerir rol de administrador — hoy no hay modelo de roles, solo autenticado/no-autenticado.

### Pedidos: el cliente manda los items, el servidor manda los precios

`POST /api/pedidos` no lee del carrito de sesión (`CarritoService`, `@SessionScope`) — recibe los items directamente en el body del request (`{ items: [{ productoId, cantidad }] }`) y **recalcula el precio de cada uno desde el `Producto` real** antes de guardar el pedido. Esto no fue el diseño original: la primera versión sí armaba el pedido a partir del carrito de sesión, pero al probar el flujo completo en un navegador real (frontend en `:5173`, backend en `:8080`, dos orígenes distintos) la cookie de sesión no persistía de forma confiable entre recargas de página — el carrito aparecía vacío después de cualquier navegación completa, aunque el `Set-Cookie` del backend era correcto. En producción (dominios distintos) el problema es peor: cookies cross-site con `SameSite` por default directamente no se envían sin `SameSite=None; Secure`.

En vez de pelear con cookies cross-origin para una operación que involucra dinero, se movió la fuente de verdad al cliente (el `cartStore` de Zustand, persistido en `localStorage`, ya es confiable) y se blindó el servidor para no confiar en los precios que manda ese cliente. Es el patrón más común en APIs REST reales: el front dice *qué* quiere comprar, el back decide *cuánto* cuesta.

### Manejo de errores centralizado

`GlobalExceptionHandler` (`@RestControllerAdvice`) traduce excepciones a una forma de respuesta consistente (`{status, error, timestamp}`, con `fieldErrors` en validaciones) en vez de dejar que cada controller maneje sus propios try/catch. Las excepciones de dominio (`EmailYaRegistradoException`, `CredencialesInvalidasException`) son tipos propios en vez de `RuntimeException` genérica, para poder mapear cada una a su status HTTP correcto (400 vs 401) sin inspeccionar mensajes de texto.

## Frontend

### Estado: Zustand, no Redux ni Context API

Tres stores independientes (`productStore`, `cartStore`, `authStore`) en vez de un store monolítico. Zustand se eligió sobre Redux por menos boilerplate (no hay actions/reducers/slices separados) y sobre Context API porque Context re-renderiza todos los consumidores en cada cambio — con tres dominios de estado que cambian a ritmos distintos (productos se filtran seguido, auth casi nunca), stores separados evitan renders innecesarios.

`cartStore` y `authStore` persisten a `localStorage` (carrito y sesión sobreviven a un refresh); `productStore` no persiste — el catálogo se vuelve a pedir en cada carga, que es lo correcto para datos que cambian del lado del servidor. `cartStore` en particular actualiza su estado a partir de la respuesta de cada mutación (agregar/actualizar/eliminar), pero a propósito no hace un fetch de sincronización contra `/api/carrito` en cada carga de página — ese fetch existía en una versión anterior (en `Header` y en la página `Cart`) y terminaba pisando el estado local correcto con un carrito de sesión vacío, por la misma razón de cookies cross-origin que se explica más arriba, en "Pedidos".

### Cliente API: una función tipada por endpoint, no un wrapper genérico

`services/api.ts` expone funciones como `getProductos()`, `addToCart()`, `login()` en vez de un `apiCall(method, url, body)` genérico. Es más código, pero cada función tiene su tipo de retorno explícito (`Promise<Producto[]>`, etc.), así los errores de contrato con el backend se detectan en compilación, no en runtime.

El token JWT se inyecta vía interceptor de request de axios (lee `authStore.getState().token`), así ningún componente tiene que acordarse de mandar el header manualmente.

### Rutas protegidas

`ProtectedRoute` es un wrapper de React Router que chequea `authStore.isAuthenticated` y redirige a `/login` si no hay sesión. Se aplica a `/checkout` y `/perfil` — las únicas dos vistas que necesitan un usuario identificado hoy.

## Decisión pendiente: persistencia

H2 en memoria fue una decisión consciente para desarrollo/demo (cero setup, datos de ejemplo se recargan solos), pero es la limitación más visible del proyecto en producción: **cada redeploy de Render recrea la base vacía** (`ddl-auto=create-drop`), lo que borra todos los usuarios y pedidos existentes. El síntoma concreto, visto en la demo en vivo: un usuario logueado *antes* de un redeploy sigue teniendo un JWT válido (la firma no depende de la base), pero al intentar comprar el backend responde `404 Usuario no encontrado`, porque ese usuario ya no existe en la base recién creada. La única forma de notarlo es probando el flujo end-to-end después de cada deploy — no aparece en ningún log de error del build. Migrar a PostgreSQL resolvería esto de raíz: es un cambio directo en `application.properties` (más migraciones con Flyway), pero implica decidir dónde hostear la base.
