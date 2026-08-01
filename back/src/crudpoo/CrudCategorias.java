package crudpoo;

import java.util.ArrayList;

// ============================================================================
// CrudCategorias
// ----------------------------------------------------------------------------
// CRUD específico para categorías (simple y directo).
// ============================================================================
public class CrudCategorias extends CrudConsola<Categoria> {

    private final ArrayList<Categoria> categorias;

    public CrudCategorias(ArrayList<Categoria> categorias) {
        this.categorias = categorias;
    }

    @Override
    public void crear() {
        String nombre = leerTexto("Nombre de la nueva categoría: ");
        categorias.add(new Categoria(nombre));
        System.out.println("Categoría creada.");
    }

    @Override
    public void listar() {
        if (categorias.isEmpty()) {
            System.out.println("(sin categorías)");
        } else {
            for (Categoria c : categorias) {
                System.out.println(c);
            }
        }
    }

    @Override
    public void actualizar() {
        int id = leerEntero("Id de la categoría: ");
        for (Categoria c : categorias) {
            if (c.getId() == id) {
                String nuevo = leerTexto("Nuevo nombre: ");
                c.setNombre(nuevo);
                System.out.println("Actualizada: " + c);
                return;
            }
        }
        System.out.println("No se encontró categoría con id " + id);
    }

    @Override
    public void eliminar() {
        int id = leerEntero("Id de la categoría a eliminar: ");
        boolean eliminado = categorias.removeIf(c -> c.getId() == id);
        System.out.println(eliminado ? "Categoría eliminada." : "No existía ese id.");
    }
}
