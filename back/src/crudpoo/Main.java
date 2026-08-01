package crudpoo;

import java.util.ArrayList;

// ============================================================================
// Main (orquestador limpio)
// ----------------------------------------------------------------------------
// Prepara listas en memoria y delega en CrudProductos / CrudCategorias.
// ============================================================================
public class Main {
    public static void main(String[] args) {
        // porque no son static?
        //   - Porque queremos instanciar objetos de estas clases.
        //   - Porque cada instancia puede tener su propio estado.
        //   - Porque no queremos que las listas persistan más allá del ciclo de vida de la instancia.
        // son mutables?
        //   - Sí, podemos agregar, eliminar y modificar elementos en las listas.
        //   - La referencia es inmutable (final), pero el contenido de las listas sí puede cambiar.
        //   - Esto es útil para mantener el estado de los productos y categorías.
        // porque no es private?
        //   - Porque queremos acceder a estas listas desde otras clases (CrudProductos, CrudCategorias).
        //   - Porque no necesitamos encapsular estas listas en la clase Main.
        //   - Porque Main actúa como un orquestador y no tiene lógica de negocio.
        final ArrayList<Producto> productos = new ArrayList<>();
        final ArrayList<Categoria> categorias = new ArrayList<>();

        categorias.add(new Categoria("Tecnología"));
        categorias.add(new Categoria("Hogar"));
        categorias.add(new Categoria("Libros"));
        
        // instanciamos los CRUDs con las listas compartidas
        // por qué pasamos las listas en el constructor?
        //   - Porque queremos que los CRUDs trabajen con las mismas listas.
        //   - Porque facilita la inyección de dependencias y el testing.
        //   - Porque evita el uso de variables globales o estáticas.
        // que pasa si no las pasamos?
        //   - Los CRUDs no tendrían acceso a las listas y no podrían gestionar productos/categorías.
        //   - Tendríamos que usar variables estáticas o globales, lo cual es una mala práctica.
        //   - Perderíamos la flexibilidad y modularidad del código.
        // que es inyección de dependencias?
        //   - Es un patrón de diseño que consiste en pasar las dependencias (listas)
        //     a un objeto en lugar de que el objeto las cree o busque por sí mismo.
        //   - Facilita el testing, ya que podemos pasar listas mock o de prueba.
        //   - Mejora la modularidad y separación de responsabilidades.
        // porque no son private los crud?
        //   - Porque no necesitamos encapsularlos en la clase Main.
        //   - Porque Main actúa como un orquestador y no tiene lógica de negocio.
        //   - Porque no hay riesgo de acceso indebido desde otras clases.
        final CrudProductos crudProd = new CrudProductos(productos, categorias);
        final CrudCategorias crudCat = new CrudCategorias(categorias);

        int opcion;
        do {
            System.out.println("\n=== Menú Principal ===");
            System.out.println("1) CRUD de Productos");
            System.out.println("2) CRUD de Categorías");
            System.out.println("0) Salir");
            System.out.print("Opción: ");
            String linea = crudProd.scanner.nextLine();
            // que es manejo de excepciones?
            //   - Es una técnica para manejar errores en tiempo de ejecución.
            //   - Permite que el programa continúe ejecutándose en lugar de fallar abruptamente.
            //   - Mejora la experiencia de usuario al proporcionar mensajes claros de error.
            // que pasa si no manejamos la excepción?
            //   - El programa podría fallar y cerrarse inesperadamente.
            //   - El usuario no entendería qué salió mal.
            //   - Podríamos perder datos o estado importante del programa.
            // que es NumberFormatException?
            //   - Es una excepción que ocurre cuando se intenta convertir una cadena a un número
            //     y la cadena no tiene un formato válido.
            //   - Es una subclase de IllegalArgumentException.
            //   - Es común al leer entradas del usuario que no son números.
            try {
                opcion = Integer.parseInt(linea.trim());
            } catch (NumberFormatException e) {
                opcion = -1;
            }

            switch (opcion) {
                case 1 -> {
                    int op;
                    do {
                        crudProd.mostrarOpciones();
                        op = crudProd.leerEntero("");
                        switch (op) {
                            case 1 -> crudProd.crear();
                            case 2 -> crudProd.listar();
                            case 3 -> crudProd.actualizar();
                            case 4 -> crudProd.eliminar();
                            case 0 -> System.out.println("Volviendo al menú principal...");
                            default -> System.out.println("Opción inválida");
                        }
                    } while (op != 0);
                }
                case 2 -> {
                    int op;
                    do {
                        crudCat.mostrarOpciones();
                        op = crudCat.leerEntero("");
                        switch (op) {
                            case 1 -> crudCat.crear();
                            case 2 -> crudCat.listar();
                            case 3 -> crudCat.actualizar();
                            case 4 -> crudCat.eliminar();
                            case 0 -> System.out.println("Volviendo al menú principal...");
                            default -> System.out.println("Opción inválida");
                        }
                    } while (op != 0);
                }
                case 0 -> System.out.println("¡Hasta luego!");
                default -> System.out.println("Opción inválida");
            }
        } while (opcion != 0);
    }
}
