import { useEffect } from 'react';
import { Link } from 'react-router-dom';
import Carousel from '../components/Carousel';
import ProductCard from '../components/ProductCard';
import { useProductStore } from '../store/productStore';
import { ENVIO_GRATIS_DESDE } from '../store/cartStore';
import { formatearPrecio } from '../services/api';
import type { CarouselSlide } from '../components/Carousel';

const FEATURED_SLIDES: CarouselSlide[] = [
  {
    id: 1,
    image: '/img/zapatillahombre1.jpeg',
    title: 'Nueva colección',
    subtitle: 'Zapatillas deportivas de alto rendimiento',
  },
  {
    id: 2,
    image: '/img/zapatillahombre3.jpeg',
    title: 'Estilo urbano',
    subtitle: 'Comodidad y diseño para el día a día',
  },
  {
    id: 3,
    image: '/img/zapatillahombre5.jpeg',
    title: 'Rendimiento deportivo',
    subtitle: 'Pensadas para el entrenamiento exigente',
  },
  {
    id: 4,
    image: '/img/zapatillahombre7.jpeg',
    title: 'Ofertas especiales',
    subtitle: 'Descuentos por tiempo limitado',
  },
];

const BENEFITS = [
  { icon: '🚚', title: 'Envío Gratis', text: `En compras superiores a ${formatearPrecio(ENVIO_GRATIS_DESDE)}` },
  { icon: '↩️', title: 'Devoluciones fáciles', text: '30 días para cambios y devoluciones' },
  { icon: '🔒', title: 'Compra segura', text: 'Tus datos siempre protegidos' },
  { icon: '⭐', title: 'Calidad garantizada', text: 'Productos seleccionados y verificados' },
];

export default function Home() {
  const { productos, fetchProductos, fetchCategorias } = useProductStore();

  useEffect(() => {
    fetchProductos();
    fetchCategorias();
  }, [fetchProductos, fetchCategorias]);

  const destacados = productos.slice(0, 8);

  return (
    <div>
      <section className="bg-gradient-to-b from-gray-50 to-white py-12 md:py-16">
        <div className="container-app">
          <div className="text-center mb-10">
            <h1 className="text-3xl md:text-4xl font-bold leading-tight mb-4">
              Encuentra tu próximo par en <span className="text-accent">DeporShop</span>
            </h1>
            <p className="text-lg text-gray-500">
              Zapatillas y accesorios deportivos de calidad al mejor precio
            </p>
          </div>

          <div className="px-4">
            <Carousel slides={FEATURED_SLIDES} />
          </div>
        </div>
      </section>

      <section className="py-16 md:py-24">
        <div className="container-app">
          <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-4">
            {BENEFITS.map((b) => (
              <div
                key={b.title}
                className="bg-white p-8 rounded-xl text-center shadow-md border border-black/5 transition-all hover:-translate-y-2 hover:shadow-xl"
              >
                <div className="text-5xl mb-4">{b.icon}</div>
                <h3 className="text-xl font-semibold mb-2">{b.title}</h3>
                <p className="text-gray-500">{b.text}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      <section className="py-16 md:py-24 bg-gray-50">
        <div className="container-app">
          <h2 className="text-3xl md:text-4xl font-bold text-center mb-3">Productos destacados</h2>
          <p className="text-lg text-gray-500 text-center mb-10">
            Los favoritos de nuestros clientes
          </p>

          {destacados.length > 0 ? (
            <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
              {destacados.map((producto) => (
                <ProductCard key={producto.id} producto={producto} />
              ))}
            </div>
          ) : (
            <p className="text-center text-gray-500">Cargando productos destacados…</p>
          )}

          <div className="text-center mt-10">
            <Link to="/productos" className="btn-primary">
              Ver todos los productos
            </Link>
          </div>
        </div>
      </section>
    </div>
  );
}
