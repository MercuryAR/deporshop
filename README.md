# DeporShop — E-commerce Full Stack

Tienda de zapatillas y accesorios deportivos. Proyecto full-stack con backend REST en Spring Boot (Java) y frontend en React + TypeScript, construido como portfolio.

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

**Backend en Render** (Docker):
1. New → Blueprint, apuntar al repo → Render detecta `render.yaml` en la raíz.
2. Define `JWT_SECRET` automáticamente (`generateValue: true`). Después del primer deploy, setear `CORS_ALLOWED_ORIGINS` con la URL de Netlify (ej: `https://deporshop.netlify.app`).
3. Alternativa manual: New → Web Service → Docker, root directory `back/`.

**Frontend en Netlify**:
1. New site from Git, apuntar al repo, base directory `front/`. Netlify lee `front/netlify.toml` (build command, publish dir, redirect SPA).
2. Env var de build: `VITE_API_BASE_URL=https://<tu-servicio>.onrender.com/api`.

**Orden recomendado:** deployar backend primero (para tener la URL de Render), después el frontend con esa URL en `VITE_API_BASE_URL`, y por último volver a Render para setear `CORS_ALLOWED_ORIGINS` con la URL final de Netlify.

## Funcionalidades

- Catálogo de productos con filtro por categoría, búsqueda y orden (precio, nombre, rating)
- Carrito de compras persistido en el backend
- Registro / login con JWT, rutas protegidas (`/checkout`, `/perfil`)
- Checkout y perfil de usuario
- Formulario de contacto con validación
- Documentación interactiva de la API vía Swagger

## Datos de ejemplo

Al iniciar, el backend carga automáticamente 3 categorías y 6 productos de ejemplo (`config/DataLoader.java`). Al usar H2 en memoria, estos datos se pierden al reiniciar el servidor.

## Próximos pasos

- Tests automatizados (backend y frontend)
- Gestión de pedidos (checkout real + historial de compras)
- Base de datos persistente (PostgreSQL) para producción
- Deploy (backend en Railway/Render, frontend en Vercel/Netlify)

## Licencia

Proyecto educativo/portfolio. Libre para uso y modificación.
