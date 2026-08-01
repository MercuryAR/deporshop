# DeporShop — E-commerce Full Stack

Tienda de zapatillas y accesorios deportivos. Proyecto full-stack con backend REST en Spring Boot (Java) y frontend en React + TypeScript, construido como portfolio.

**Demo en vivo:** [deporshop-frontend.onrender.com](https://deporshop-frontend.onrender.com) · API: [deporshop-api.onrender.com](https://deporshop-api.onrender.com) · [Swagger](https://deporshop-api.onrender.com/swagger-ui/index.html)

> El backend está en el free tier de Render: si nadie lo usó en un rato, el primer request puede tardar ~50 segundos en despertar. Además, la base es H2 en memoria — cada vez que el servicio se redeploya, se borran todos los usuarios y pedidos (ver [Datos de ejemplo](#datos-de-ejemplo)).

## Stack

**Backend**
- Java 17, Spring Boot 3.2
- Spring Security + JWT (autenticación stateless)
- Spring Data JPA + H2 (en memoria)
- Bean Validation, manejo global de excepciones
- Swagger / OpenAPI (springdoc)
- Maven

**Frontend**
- React 19 + TypeScript
- Vite
- Tailwind CSS
- Zustand (estado global: productos, carrito, autenticación)
- React Router
- Axios

## Estructura del proyecto

```
CarritoDeComprasFullStack/
├── back/                   # API REST (Spring Boot) — ver back/README.md
│   └── src/main/java/com/deporshop/
│       ├── controller/     # Endpoints REST
│       ├── service/        # Lógica de negocio
│       ├── repository/     # Spring Data JPA
│       ├── model/          # Entidades JPA
│       ├── dto/            # Request/response de la API
│       ├── security/       # JWT provider + filter
│       ├── exception/      # Excepciones + handler global
│       └── config/         # Security, Swagger, carga de datos
│
└── front/                  # SPA (React + TypeScript) — ver front/README.md
    └── src/
        ├── pages/          # Vistas (Home, Products, Cart, Login, etc.)
        ├── components/     # Componentes reutilizables
        ├── store/          # Zustand: productStore, cartStore, authStore
        ├── services/       # Cliente API (axios)
        └── types/          # Interfaces TypeScript
```

Ver también [ARCHITECTURE.md](ARCHITECTURE.md) para las decisiones de diseño detrás de la autenticación JWT, el manejo de estado y la separación DTO/entidad.

## Cómo correr el proyecto

### Backend

```bash
cd back
mvn spring-boot:run
```

- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- H2 Console: `http://localhost:8080/h2-console`

Detalle completo de endpoints en [back/README.md](back/README.md).

### Frontend

```bash
cd front
npm install
npm run dev
```

- App: `http://localhost:5173`

El cliente API apunta a `http://localhost:8080/api` (ver `front/src/services/api.ts`), así que el backend tiene que estar corriendo para que la app funcione con datos reales.

## Deploy

Los dos servicios corren en **Render**, definidos como Blueprint en [`render.yaml`](render.yaml):

- **`deporshop-api`** — Web Service, Docker (`back/Dockerfile`). `JWT_SECRET` se genera solo; `CORS_ALLOWED_ORIGINS` hay que setearlo a mano con la URL del frontend.
- **`deporshop-frontend`** — Static Site (`front/`, `npm run build`, publica `dist/`). Variable de build `VITE_API_BASE_URL` apuntando a la URL de `deporshop-api` + `/api`.

**Pasos:** New → Blueprint → conectar el repo → Render detecta `render.yaml` y crea ambos servicios. Deployar `deporshop-api` primero (para tener su URL), completar `VITE_API_BASE_URL` en `deporshop-frontend` con esa URL, y por último volver a `deporshop-api` para setear `CORS_ALLOWED_ORIGINS` con la URL final del frontend.

> Nota operativa: en el free tier, el auto-deploy en cada push no siempre se dispara solo — puede hacer falta un **Manual Deploy** desde el dashboard de cada servicio. Si el backend no toma cambios de código después de un deploy que sí figura como "live", probá **"Clear build cache & deploy"** (puede haber cache de Docker desactualizada).

## Funcionalidades

- Catálogo de productos con filtro por categoría, búsqueda y orden (precio, nombre, rating)
- Carrito de compras persistido en el backend
- Registro / login con JWT, rutas protegidas (`/checkout`, `/perfil`)
- Checkout con creación real de pedidos — el precio se recalcula en el servidor a partir del producto real, nunca se confía en lo que manda el cliente
- Historial de pedidos en el perfil de usuario
- Formulario de contacto con validación
- Documentación interactiva de la API vía Swagger

## Datos de ejemplo

Al iniciar, el backend carga automáticamente 3 categorías y 6 productos de ejemplo (`config/DataLoader.java`), con precios en pesos argentinos. Al usar H2 en memoria, **estos datos —y cualquier usuario o pedido creado— se pierden cada vez que el servidor se reinicia o redeploya**. Si al hacer checkout en la demo en vivo da error de "usuario no encontrado", es porque el backend se redeployó después de que iniciaste sesión: cerrá sesión, registrate de nuevo y va a funcionar.

## Próximos pasos

- Base de datos persistente (PostgreSQL) para que usuarios y pedidos sobrevivan a un redeploy
- Reviews/ratings de productos y wishlist
- Tests E2E (Cypress) y tests de frontend (Jest/Vitest + Testing Library)
- Configurar auto-deploy confiable en Render (actualmente a veces requiere Manual Deploy)

## Licencia

Proyecto educativo/portfolio. Libre para uso y modificación.
