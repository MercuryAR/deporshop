package crudpoo;

// ============================================================================
// Subclase Articulo
// ----------------------------------------------------------------------------
// Hereda de Producto y agrega Categoria (composición).
// ============================================================================
public class Articulo extends Producto {

    private Categoria categoria;      // Atributo propio

    public Articulo(String nombre, double precio, Categoria categoria) { // Constructor
        super(nombre, precio);        // Llama al constructor de Producto
        this.categoria = categoria;   // Setea categoría
    }

    public Categoria getCategoria() { // Getter categoría
        return categoria;
    }

    public void setCategoria(Categoria categoria) { // Setter categoría
        this.categoria = categoria;
    }

     // sobrescritura de método abstracto de la superclase
     // que al mismo tiempo la superclase implementa una interfaz que tenia este method
    @Override
    public double calcularDescuento() { // Implementación concreta
        return getPrecio() * 0.90;      // 10% de descuento
    }

    @Override
    public String toString() {          // Representación legible
        return super.toString() + ", categoria=" + (categoria != null ? categoria.getNombre() : "Sin categoría");
    }
}
