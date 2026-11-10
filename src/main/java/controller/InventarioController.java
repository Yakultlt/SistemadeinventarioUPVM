package controller;
import structures.Cola;
import structures.ListaEnlazada;
import model.Insumo;

public class InventarioController {
    private ListaEnlazada<Insumo> catalogo;
    private Cola<String> colaSolicitudes;

    public InventarioController() {
        this.catalogo = new ListaEnlazada<>();
        this.colaSolicitudes = new Cola<>();
    }

    public void registrarInsumo(String id, String nombre, int stock) {
        Insumo nuevo = new Insumo(id, nombre, stock);
        catalogo.insertar(nuevo);
    }

    public void registrarSolicitudTurno(String solicitante) {
        colaSolicitudes.encolar(solicitante);
    }

    public String atenderSiguienteSolicitud() {
        return colaSolicitudes.desencolar();
    }

    public ListaEnlazada<Insumo> getCatalogo() {
        return catalogo;
    }
}