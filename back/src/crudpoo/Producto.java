package crudpoo;

// ============================================================================
// Clase abstracta Producto (SUPERCLASE)
// ----------------------------------------------------------------------------
// - No se instancia directamente (abstract).
// - Contiene atributos y comportamiento común a Articulo y Servicio.
// - Implementa Vendible para obligar a definir calcularDescuento() en hijas.
// porque elegir una clase abstracta en lugar de una interfaz?
//   - Porque tiene atributos y comportamiento común.
//   - Porque queremos compartir código entre las clases hijas.
//   - Porque queremos definir un contrato (Vendible) y compartir código.
// porque no usamos protected?
//   - Por que queremos encapsular los atributos y forzar el uso de getters/setters.
//  - Por que queremos controlar el acceso a los atributos.
// que pasa si usamos protected?
//   - Que las clases hijas pueden acceder directamente a los atributos.
//   - Que se rompe el encapsulamiento.
//   - Que no podemos controlar el acceso a los atributos.
// ============================================================================
public abstract class Producto implements Vendible {

    private int id;              // Identidad única
    private String nombre;       // Nombre legible
    private double precio;       // Precio base sin descuento

    private static int contador = 1; // Contador compartido entre instancias

    public Producto(String nombre, double precio) { // Constructor
        this.id = contador++;       // Asignación de id autoincremental
        this.nombre = nombre;       // Guardamos nombre
        this.precio = precio;       // Guardamos precio
    }

    public int getId() {            // Getter id
        return id;
    }

    public String getNombre() {     // Getter nombre
        return nombre;
    }

    public void setNombre(String nombre) { // Setter nombre
        this.nombre = nombre;
    }

    public double getPrecio() {     // Getter precio
        return precio;
    }

    public void setPrecio(double precio) { // Setter precio
        this.precio = precio;
    }

   /*  public  double calcularDescuento(){
        return precio * 0.95;
    };*/
    public abstract double calcularDescuento(); // Contrato a implementar en hijas
   // public abstract String mostrarDatos(); // Contrato a implementar en hijas
    @Override
    public String toString() {      // Representación legible
        return "Producto{id=" + id + ", nombre='" + nombre + "', precio=" + precio + "}";
    }
}
