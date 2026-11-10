package structures;

public class PruebasEstructuras {
    public static void main(String[] args) {
        System.out.println("=== INICIANDO PRUEBAS UNITARIAS ===");

        // 1. Prueba de Cola (FIFO)
        Cola<String> cola = new Cola<>();
        assert cola.estaVacia() : "Error: La cola debe iniciar vacía";

        cola.encolar("Solicitud 1");
        cola.encolar("Solicitud 2");
        assert cola.getTamanio() == 2 : "Error: El tamaño de la cola debe ser 2";
        assert "Solicitud 1".equals(cola.verFrente()) : "Error: El frente debe ser 'Solicitud 1'";

        String atendido = cola.desencolar();
        assert "Solicitud 1".equals(atendido) : "Error: Debe extraer el primer elemento ingresado";
        assert cola.getTamanio() == 1 : "Error: El tamaño debe reducirse a 1";
        System.out.println("[✔] Pruebas de Cola (FIFO) pasaron con éxito.");

        // 2. Prueba de Lista Enlazada
        ListaEnlazada<String> lista = new ListaEnlazada<>();
        assert lista.estaVacia() : "Error: La lista debe iniciar vacía";

        lista.insertar("Multímetro");
        lista.insertar("Cautín");
        assert lista.getTamanio() == 2 : "Error: El tamaño de la lista debe ser 2";

        // Probando el método buscar requerido
        assert lista.buscar("Cautín") : "Error: Debe encontrar un elemento existente en la lista";
        assert !lista.buscar("Osciloscopio") : "Error: No debe encontrar un elemento inexistente";
        System.out.println("[✔] Pruebas de Lista Enlazada y Búsqueda pasaron con éxito.");

        System.out.println("\n🎉 ¡TODAS LAS PRUEBAS UNITARIAS PASARON CORRECTAMENTE!");
    }
}