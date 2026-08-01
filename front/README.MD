# DeporShop Frontend

SPA de la tienda DeporShop, construida con React + TypeScript. Consume la API REST del [backend](../back/README.md).

## Stack

- React 19 + TypeScript
- Vite
- Tailwind CSS
- Zustand (estado global)
- React Router v7
- Axios

## Requisitos

- Node 18+
- El backend corriendo en `http://localhost:8080` (ver [back/README.md](../back/README.md)) — la app hace fetch de datos reales, no tiene mocks.

## Instalación y ejecución

```bash
cd front
npm install
npm run dev
```

App en `http://localhost:5173`.

## Scripts

| Comando | Qué hace |
|---|---|
| `npm run dev` | Servidor de desarrollo (Vite) |
| `npm run build` | Type-check (`tsc -b`) + build de producción |
| `npm run preview` | Sirve el build de producción localmente |
| `npm run lint` | Lint con oxlint |

## Estructura

```
src/
├── pages/          # Una por ruta: Home, Products, ProductDetail, Cart, Checkout, Login, User, Contact
├── components/     # Reutilizables: Header, Footer, Layout, ProductCard, ProductList, CartItem, LoginForm, Carousel, ProtectedRoute
├── store/          # Zustand: productStore, cartStore, authStore
├── services/       # api.ts — instancia de axios + funciones tipadas por endpoint
└── types/          # Interfaces TS que reflejan los DTOs del backend
```

## Rutas

| Ruta | Página | Protegida |
|---|---|---|
| `/` | Home | No |
| `/productos` | Products | No |
| `/producto/:id` | ProductDetail | No |
| `/carrito` | Cart | No |
| `/checkout` | Checkout | Sí |
| `/login` | Login (toggle login/registro) | No |
| `/perfil` | User | Sí |
| `/contacto` | Contact | No |

Las rutas protegidas usan `components/ProtectedRoute.tsx`, que redirige a `/login` si `authStore` no tiene sesión activa.

## Estado global (Zustand)

- **`productStore`** — catálogo, categorías, filtros (categoría/búsqueda) y orden (precio, nombre, rating).
- **`cartStore`** — ítems del carrito, actualizado a partir de la respuesta de cada mutación (`agregar`/`actualizar`/`eliminar`/`limpiar` contra `/api/carrito`) y persistido en `localStorage`. A propósito **no** se re-sincroniza automáticamente contra el backend en cada carga de página: el carrito vive en la sesión HTTP del servidor, que no persiste de forma confiable entre recargas en una SPA cross-origin, así que el estado local (localStorage) es la fuente de verdad.
- **`authStore`** — usuario, token JWT y `isAuthenticated`, persistido en `localStorage`. El token se inyecta automáticamente en cada request saliente vía interceptor de axios (`services/api.ts`).

`services/api.ts` también expone `formatearPrecio` (formato ARS), `crearPedido(items)` y `getMisPedidos()` — el checkout arma el pedido con los items del `cartStore` local (no del carrito de sesión), y el backend recalcula los precios reales.

## Estilos

Tailwind CSS con la paleta de marca: gris `#1F2937` (primario) y naranja `#EA580C` (acento), tipografía Inter + Caveat (Google Fonts). Configuración en `tailwind.config.js` — cambiar el color ahí se propaga a todo el sitio vía las utilidades `accent`/`accent-light` de Tailwind.

## Notas

- Las imágenes de producto viven en `public/img/`.
- No hay tests todavía (ver [README raíz](../README.md#próximos-pasos)).
