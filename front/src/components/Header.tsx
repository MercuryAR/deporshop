import { useState } from 'react';
import { Link, NavLink } from 'react-router-dom';
import { useCartStore } from '../store/cartStore';
import { useAuthStore } from '../store/authStore';

export default function Header() {
  const cantidad = useCartStore((s) => s.cantidad);
  const isAuthenticated = useAuthStore((s) => s.isAuthenticated);
  const [menuOpen, setMenuOpen] = useState(false);

  const navLinkClass = ({ isActive }: { isActive: boolean }) =>
    `px-4 py-2 rounded-lg font-medium transition-colors ${
      isActive ? 'bg-white/15' : 'hover:bg-white/15'
    }`;

  return (
    <header className="bg-gradient-to-br from-primary to-primary-light text-white sticky top-0 z-50 shadow-md">
      <div className="container-app flex items-center justify-between gap-4 flex-wrap py-4">
        <Link to="/" className="flex items-center gap-3">
          <svg viewBox="0 0 50 50" className="w-10 h-10 drop-shadow-sm">
            <circle cx="25" cy="25" r="20" fill="currentColor" />
            <path d="M15 25 L25 35 L40 20" stroke="white" strokeWidth="3" fill="none" />
          </svg>
          <span className="font-display text-3xl font-bold [text-shadow:2px_2px_4px_rgba(0,0,0,0.2)]">
            DeporShop
          </span>
        </Link>

        <nav className="hidden md:flex gap-1" aria-label="Navegación principal">
          <NavLink to="/" end className={navLinkClass}>
            Inicio
          </NavLink>
          <NavLink to="/productos" className={navLinkClass}>
            Productos
          </NavLink>
          <NavLink to="/contacto" className={navLinkClass}>
            Contacto
          </NavLink>
        </nav>

        <div className="flex items-center gap-2">
          <Link
            to={isAuthenticated ? '/perfil' : '/login'}
            aria-label="Usuario"
            className="w-10 h-10 flex items-center justify-center rounded-lg bg-white/10 hover:bg-white/20 hover:-translate-y-0.5 transition-all"
          >
            <img src="/img/Usuario.svg" alt="" className="w-5 h-5" />
          </Link>
          <Link
            to="/carrito"
            aria-label="Carrito de compras"
            className="relative w-10 h-10 flex items-center justify-center rounded-lg bg-white/10 hover:bg-white/20 hover:-translate-y-0.5 transition-all"
          >
            <img src="/img/Carrito.png" alt="" className="w-5 h-5" />
            {cantidad > 0 && (
              <span className="absolute -top-1 -right-1 bg-accent text-white text-xs font-bold min-w-[18px] text-center rounded-full px-1.5 py-0.5">
                {cantidad}
              </span>
            )}
          </Link>
          <button
            className="md:hidden w-10 h-10 flex items-center justify-center rounded-lg bg-white/10 hover:bg-white/20 transition-all"
            aria-label="Abrir menú"
            onClick={() => setMenuOpen((o) => !o)}
          >
            ☰
          </button>
        </div>
      </div>

      {menuOpen && (
        <nav className="md:hidden flex flex-col gap-1 px-4 pb-4" aria-label="Navegación móvil">
          <NavLink to="/" end className={navLinkClass} onClick={() => setMenuOpen(false)}>
            Inicio
          </NavLink>
          <NavLink to="/productos" className={navLinkClass} onClick={() => setMenuOpen(false)}>
            Productos
          </NavLink>
          <NavLink to="/contacto" className={navLinkClass} onClick={() => setMenuOpen(false)}>
            Contacto
          </NavLink>
        </nav>
      )}
    </header>
  );
}
