package crudpoo;

// ============================================================================
// Interfaz Vendible
// ----------------------------------------------------------------------------
// Contrato: cualquier clase "vendible" debe poder calcular su precio con
// descuento. No implementa nada, solo define la firma del método.
// es obligatorio que las clases que implementen esta interfaz definan este método.
// ============================================================================
public interface Vendible {
    // hasta java 8 los métodos en interfaces no podían tener implementación.
    // desde java 8 se pueden definir métodos default con implementación.
    // desde java 8 también se pueden definir métodos estáticos con implementación.
    // desde java 9 se pueden definir métodos privados con implementación.
    // desde java 14 se pueden definir métodos de instancia en interfaces.
    // clase que implementa una interfaz que tiene un method que dentro de la interfaz esta
    //implementado como default, puede sobrescribirlo o no.
    // si no lo sobrescribe, usa el de la interfaz.
    // si lo sobrescribe, usa el de la clase.
    double calcularDescuento();
}
