package view;

import controller.InventarioController;
import java.util.Scanner;

public class MenuConsola {
    private InventarioController controller;
    private Scanner scanner;

    public MenuConsola() {
        this.controller = new InventarioController();
        this.scanner = new Scanner(System.in);
    }

    public void iniciar() {
        int opcion = -1;
        do {
            try {
                System.out.println("\n=== SISTEMA DE CONTROL DE INVENTARIOS UPVM ===");
                System.out.println("1. Registrar Insumo");
                System.out.println("2. Registrar Turno de Solicitud");
                System.out.println("3. Atender Siguiente Solicitud");
                System.out.println("4. Salir");
                System.out.print("Seleccione una opción: ");

                String entrada = scanner.nextLine();
                opcion = Integer.parseInt(entrada);

                switch (opcion) {
                    case 1:
                        System.out.print("ID Insumo: ");
                        String id = scanner.nextLine();
                        System.out.print("Nombre: ");
                        String nom = scanner.nextLine();
                        System.out.print("Cantidad Stock: ");
                        int cant = Integer.parseInt(scanner.nextLine());
                        controller.registrarInsumo(id, nom, cant);
                        System.out.println("-> Insumo registrado con éxito.");
                        break;
                    case 2:
                        System.out.print("Nombre del Solicitante: ");
                        String sol = scanner.nextLine();
                        controller.registrarSolicitudTurno(sol);
                        System.out.println("-> Solicitud agregada a la Cola.");
                        break;
                    case 3:
                        String atendido = controller.atenderSiguienteSolicitud();
                        if (atendido != null) {
                            System.out.println("-> Se atendió a: " + atendido);
                        } else {
                            System.out.println("-> No hay solicitudes pendientes.");
                        }
                        break;
                    case 4:
                        System.out.println("Saliendo del sistema...");
                        break;
                    default:
                        System.out.println("Opción no válida.");
                }
            } catch (NumberFormatException e) {
                System.out.println(" Error: Ingrese solo números válidos para las opciones/cantidades.");
            } catch (Exception e) {
                System.out.println(" Ocurrió un error inesperado: " + e.getMessage());
            }
        } while (opcion != 4);
    }
}