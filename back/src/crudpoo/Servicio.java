package crudpoo;

// ============================================================================
// Subclase Servicio
// ----------------------------------------------------------------------------
// Hereda de Producto y agrega duración (en horas).
// ============================================================================
public class Servicio extends Producto {

    private int duracionHoras;         // Atributo propio

    public Servicio(String nombre, double precio, int duracionHoras) { // Constructor
        super(nombre, precio);         // Constructor de Producto
        this.duracionHoras = duracionHoras; // Setea duración
    }

    public int getDuracionHoras() {    // Getter
        return duracionHoras;
    }

    public void setDuracionHoras(int duracionHoras) { // Setter
        this.duracionHoras = duracionHoras;
    }

    @Override
    public double calcularDescuento() { // Implementación concreta
        return getPrecio() * 0.80;      // 20% de descuento
    }

    @Override
    public String toString() {          // Representación legible
        return super.toString() + ", duracion=" + duracionHoras + "h";
    }
}
