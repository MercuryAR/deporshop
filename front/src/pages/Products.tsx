import { useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import ProductList from '../components/ProductList';
import { useProductStore } from '../store/productStore';

export default function Products() {
  const [searchParams] = useSearchParams();
  const setFiltro = useProductStore((s) => s.setFiltro);

  useEffect(() => {
    const categoriaId = searchParams.get('categoriaId');
    const buscar = searchParams.get('buscar');
    if (categoriaId) setFiltro('categoriaId', Number(categoriaId));
    if (buscar) setFiltro('buscar', buscar);
  }, [searchParams, setFiltro]);

  return (
    <div>
      <section className="bg-gradient-to-br from-primary to-primary-light text-white text-center py-16 px-6 rounded-b-2xl mb-10">
        <h1 className="text-3xl md:text-4xl font-bold mb-3 [text-shadow:0_2px_4px_rgba(0,0,0,0.1)]">
          Nuestros Productos
        </h1>
        <p className="text-lg opacity-95 max-w-xl mx-auto">
          Descubre nuestra colección completa de zapatillas y accesorios deportivos
        </p>
      </section>

      <section className="container-app pb-16">
        <ProductList />
      </section>
    </div>
  );
}
