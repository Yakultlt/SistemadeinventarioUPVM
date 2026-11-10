import structures.Cola;
import structures.ListaEnlazada;

public class PruebasEstrucuras {
    public static void ejecutarPruebas() {
        // Prueba de Cola (FIFO)
        Cola<String> cola = new Cola<>();
        cola.encolar("Solicitud 1");
        cola.encolar("Solicitud 2");
        assert "Solicitud 1".equals(cola.desencolar()) : "Error en encolar/desencolar Cola";

        // Prueba de Lista Enlazada
        ListaEnlazada<String> lista = new ListaEnlazada<>();
        lista.insertar("Elemento A");
        assert !lista.estaVacia() : "Error en inserción de Lista";

        System.out.println("¡Pruebas unitarias ejecutadas con éxito!");
    }
}