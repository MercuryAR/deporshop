import { useEffect } from 'react';
import { useProductStore } from '../store/productStore';
import ProductCard from './ProductCard';

interface ProductListProps {
  showFilters?: boolean;
}

export default function ProductList({ showFilters = true }: ProductListProps) {
  const {
    categorias,
    filtros,
    loading,
    error,
    fetchProductos,
    fetchCategorias,
    setFiltro,
    resetFiltros,
    getProductosFiltrados,
  } = useProductStore();

  useEffect(() => {
    fetchProductos();
    fetchCategorias();
  }, [fetchProductos, fetchCategorias]);

  const productos = getProductosFiltrados();

  return (
    <div>
      {showFilters && (
        <div className="bg-white p-6 rounded-xl shadow-md mb-8">
          <div className="grid gap-4 md:grid-cols-5 items-end">
            <div className="md:col-span-2 flex flex-col gap-2">
              <label htmlFor="buscar" className="text-sm font-semibold text-gray-500 uppercase tracking-wide">
                Buscar
              </label>
              <input
                id="buscar"
                type="text"
                className="form-input"
                placeholder="Buscar productos..."
                value={filtros.buscar}
                onChange={(e) => setFiltro('buscar', e.target.value)}
              />
            </div>

            <div className="flex flex-col gap-2">
              <label htmlFor="categoria" className="text-sm font-semibold text-gray-500 uppercase tracking-wide">
                Categoría
              </label>
              <select
                id="categoria"
                className="form-select"
                value={filtros.categoriaId}
                onChange={(e) =>
                  setFiltro('categoriaId', e.target.value === 'all' ? 'all' : Number(e.target.value))
                }
              >
                <option value="all">Todas</option>
                {categorias.map((cat) => (
                  <option key={cat.id} value={cat.id}>
                    {cat.nombre}
                  </option>
                ))}
              </select>
            </div>

            <div className="flex flex-col gap-2">
              <label htmlFor="ordenar" className="text-sm font-semibold text-gray-500 uppercase tracking-wide">
                Ordenar por
              </label>
              <select
                id="ordenar"
                className="form-select"
                value={filtros.ordenar}
                onChange={(e) => setFiltro('ordenar', e.target.value as typeof filtros.ordenar)}
              >
                <option value="relevancia">Relevancia</option>
                <option value="precio-asc">Precio: menor a mayor</option>
                <option value="precio-desc">Precio: mayor a menor</option>
                <option value="nombre">Nombre</option>
                <option value="rating">Mejor calificados</option>
              </select>
            </div>

            <div className="flex flex-col gap-2">
              <label className="flex items-center gap-2 text-sm cursor-pointer">
                <input
                  type="checkbox"
                  checked={filtros.soloEnStock}
                  onChange={(e) => setFiltro('soloEnStock', e.target.checked)}
                />
                Solo en stock
              </label>
              <label className="flex items-center gap-2 text-sm cursor-pointer">
                <input
                  type="checkbox"
                  checked={filtros.soloConDescuento}
                  onChange={(e) => setFiltro('soloConDescuento', e.target.checked)}
                />
                Con descuento
              </label>
            </div>
          </div>

          <div className="flex items-center justify-between mt-4 pt-4 border-t border-gray-200">
            <p className="text-sm text-gray-500">
              {productos.length} producto{productos.length !== 1 ? 's' : ''} encontrado
              {productos.length !== 1 ? 's' : ''}
            </p>
            <button className="btn-ghost text-primary text-sm font-medium" onClick={resetFiltros}>
              Limpiar filtros
            </button>
          </div>
        </div>
      )}

      {loading && <p className="text-center py-16 text-gray-500">Cargando productos…</p>}

      {error && (
        <div className="text-center py-16">
          <p className="text-error font-semibold">{error}</p>
        </div>
      )}

      {!loading && !error && productos.length === 0 && (
        <div className="text-center py-16">
          <p className="text-lg text-gray-500 mb-4">No hay productos que coincidan con tus filtros</p>
          <button className="btn-primary" onClick={resetFiltros}>
            Limpiar filtros
          </button>
        </div>
      )}

      {!loading && !error && productos.length > 0 && (
        <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
          {productos.map((producto) => (
            <ProductCard key={producto.id} producto={producto} />
          ))}
        </div>
      )}
    </div>
  );
}
