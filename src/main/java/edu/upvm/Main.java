package edu.upvm;
import controller.InventarioController;
import model.Insumo;

public class Main {
    public static void main(String[] args) {
        InventarioController controller = new InventarioController();

        // Simulación del sistema operativo al 60%
        controller.registrarInsumo("001", "Multímetro digital", 5);
        controller.registrarInsumo("002", "Kit de Arduino", 10);

        controller.registrarSolicitudTurno("Alumno: Juan Pérez (Préstamo Multímetro)");

        System.out.println("=== SISTEMA DE INVENTARIOS Y PRÉSTAMOS UPVM ===");
        System.out.println("Insumos registrados correctamente en Lista Enlazada.");
        System.out.println("Siguiente turno en Cola: " + controller.atenderSiguienteSolicitud());
    }
}