package view;

import controller.InventarioController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Insumo;

import java.io.*;
import java.util.Optional;

public class MenuVentana extends Application {

    private InventarioController controller = new InventarioController();
    private ObservableList<Insumo> listaInsumos = FXCollections.observableArrayList();
    private int contadorId = 1;
    private final String ARCHIVO_DATOS = "inventario.txt";

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Sistema de Control de Inventarios y Trámites - UPVM");

        // Cargar los datos guardados previamente al iniciar
        cargarDatosDesdeArchivo();

        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-background-color: #f8fafc;");


        Tab tabInsumos = new Tab("Gestión de Inventario");
        tabInsumos.setClosable(false);

        TextField txtBuscar = new TextField();
        txtBuscar.setPromptText(" Buscar por ID o Nombre...");
        txtBuscar.setPrefWidth(300);
        txtBuscar.setStyle("-fx-background-radius: 6px; -fx-padding: 8px;");

        FilteredList<Insumo> datosFiltrados = new FilteredList<>(listaInsumos, b -> true);
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            datosFiltrados.setPredicate(insumo -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String filtroLower = newValue.toLowerCase();
                if (insumo.getId().toLowerCase().contains(filtroLower)) {
                    return true;
                } else if (insumo.getNombre().toLowerCase().contains(filtroLower)) {
                    return true;
                }
                return false;
            });
        });

        TableView<Insumo> table = new TableView<>();
        table.setItems(datosFiltrados);
        table.setStyle("-fx-background-radius: 8px; -fx-border-color: #e2e8f0; -fx-border-radius: 8px;");

        TableColumn<Insumo, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(100);

        TableColumn<Insumo, String> colNombre = new TableColumn<>("Nombre del Insumo");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colNombre.setPrefWidth(250);

        TableColumn<Insumo, Integer> colStock = new TableColumn<>("Stock / Existencias");
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colStock.setPrefWidth(150);

        table.getColumns().addAll(colId, colNombre, colStock);

        TextField txtNombreInsumo = new TextField();
        txtNombreInsumo.setPromptText("Nombre del insumo");
        txtNombreInsumo.setPrefWidth(180);
        txtNombreInsumo.setStyle("-fx-background-radius: 6px; -fx-padding: 7px;");

        TextField txtStockInsumo = new TextField();
        txtStockInsumo.setPromptText("Stock inicial");
        txtStockInsumo.setPrefWidth(100);
        txtStockInsumo.setStyle("-fx-background-radius: 6px; -fx-padding: 7px;");

        Button btnRegistrarInsumo = new Button("Registrar Insumo");
        btnRegistrarInsumo.setStyle("-fx-background-color: #0ea5e9; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-padding: 8px 14px;");

        Button btnEntrada = new Button("Abastecer (Entrada)");
        btnEntrada.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-padding: 8px 14px;");

        Button btnSalida = new Button("Descontar (Salida)");
        btnSalida.setStyle("-fx-background-color: #f59e0b; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-padding: 8px 14px;");

        Button btnEliminar = new Button("Eliminar");
        btnEliminar.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-padding: 8px 14px;");

        // Lógica de Registro con persistencia
        btnRegistrarInsumo.setOnAction(e -> {
            String nombre = txtNombreInsumo.getText().trim();
            String stockStr = txtStockInsumo.getText().trim();

            if (!nombre.isEmpty() && !stockStr.isEmpty()) {
                try {
                    int stock = Integer.parseInt(stockStr);
                    String idGenerado = "INS-" + String.format("%03d", contadorId++);

                    controller.registrarInsumo(idGenerado, nombre, stock);
                    listaInsumos.add(new Insumo(idGenerado, nombre, stock));
                    guardarDatosEnArchivo();

                    txtNombreInsumo.clear();
                    txtStockInsumo.clear();
                } catch (NumberFormatException ex) {
                    mostrarAlerta("Error", "El stock debe ser un número entero válido.");
                }
            } else {
                mostrarAlerta("Campos vacíos", "Llena los campos de nombre y stock inicial.");
            }
        });

        // Lógica de Abastecer Stock con persistencia
        btnEntrada.setOnAction(e -> {
            Insumo seleccionado = table.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                TextInputDialog dialog = new TextInputDialog("1");
                dialog.setTitle("Abastecer Inventario");
                dialog.setHeaderText("Añadir stock para: " + seleccionado.getNombre());
                dialog.setContentText("Cantidad a agregar:");
                Optional<String> result = dialog.showAndWait();
                result.ifPresent(cantidadStr -> {
                    try {
                        int cantidad = Integer.parseInt(cantidadStr);
                        if (cantidad > 0) {
                            seleccionado.setStock(seleccionado.getStock() + cantidad);
                            table.refresh();
                            guardarDatosEnArchivo();
                        } else {
                            mostrarAlerta("Error", "La cantidad debe ser mayor a 0.");
                        }
                    } catch (NumberFormatException ex) {
                        mostrarAlerta("Error", "Ingresa un número válido.");
                    }
                });
            } else {
                mostrarAlerta("Selección vacía", "Selecciona un insumo de la tabla para abastecer.");
            }
        });

        // Lógica de Descontar Stock con persistencia
        btnSalida.setOnAction(e -> {
            Insumo seleccionado = table.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                TextInputDialog dialog = new TextInputDialog("1");
                dialog.setTitle("Descontar Inventario");
                dialog.setHeaderText("Retirar stock para: " + seleccionado.getNombre());
                dialog.setContentText("Cantidad a retirar:");
                Optional<String> result = dialog.showAndWait();
                result.ifPresent(cantidadStr -> {
                    try {
                        int cantidad = Integer.parseInt(cantidadStr);
                        if (cantidad > 0 && cantidad <= seleccionado.getStock()) {
                            seleccionado.setStock(seleccionado.getStock() - cantidad);
                            table.refresh();
                            guardarDatosEnArchivo();
                        } else {
                            mostrarAlerta("Error", "Cantidad inválida o excede el stock disponible.");
                        }
                    } catch (NumberFormatException ex) {
                        mostrarAlerta("Error", "Ingresa un número válido.");
                    }
                });
            } else {
                mostrarAlerta("Selección vacía", "Selecciona un insumo de la tabla para descontar.");
            }
        });

        // Lógica de Eliminación con persistencia
        btnEliminar.setOnAction(e -> {
            Insumo seleccionado = table.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                listaInsumos.remove(seleccionado);
                guardarDatosEnArchivo();
            } else {
                mostrarAlerta("Selección vacía", "Selecciona un insumo para eliminar.");
            }
        });

        HBox searchBox = new HBox(10, txtBuscar);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPadding(new Insets(5, 0, 5, 0));

        HBox formInsumos = new HBox(12, txtNombreInsumo, txtStockInsumo, btnRegistrarInsumo, btnEntrada, btnSalida, btnEliminar);
        formInsumos.setAlignment(Pos.CENTER);
        formInsumos.setPadding(new Insets(10));

        VBox layoutInsumos = new VBox(12, searchBox, table, formInsumos);
        layoutInsumos.setPadding(new Insets(20));
        tabInsumos.setContent(layoutInsumos);



        Tab tabTurnos = new Tab("Control de Turnos");
        tabTurnos.setClosable(false);

        Label lblTurnoTitulo = new Label("Asignación por Orden de Solicitud (Cola FIFO)");
        lblTurnoTitulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        TextField txtSolicitante = new TextField();
        txtSolicitante.setPromptText("Nombre del Solicitante o Área");
        txtSolicitante.setPrefWidth(300);
        txtSolicitante.setStyle("-fx-background-radius: 6px; -fx-padding: 8px;");

        Button btnRegistrarTurno = new Button("➕ Registrar Turno");
        btnRegistrarTurno.setStyle("-fx-background-color: #0ea5e9; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-padding: 8px 16px;");

        Button btnAtenderTurno = new Button("🔔 Atender Siguiente Solicitud");
        btnAtenderTurno.setStyle("-fx-background-color: #8b5cf6; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-padding: 8px 16px;");

        Label lblResultadoTurno = new Label("Estado: Sin acciones recientes en la cola.");
        lblResultadoTurno.setStyle("-fx-font-size: 14px; -fx-text-fill: #475569; -fx-font-weight: bold;");

        btnRegistrarTurno.setOnAction(e -> {
            String solicitante = txtSolicitante.getText().trim();
            if (!solicitante.isEmpty()) {
                controller.registrarSolicitudTurno(solicitante);
                lblResultadoTurno.setText("-> Solicitud agregada a la Cola para: " + solicitante);
                txtSolicitante.clear();
            } else {
                mostrarAlerta("Campo vacío", "Ingresa el nombre del solicitante.");
            }
        });

        btnAtenderTurno.setOnAction(e -> {
            String atendido = controller.atenderSiguienteSolicitud();
            if (atendido != null) {
                lblResultadoTurno.setText("-> Se atendió exitosamente a: " + atendido);
            } else {
                lblResultadoTurno.setText("-> No hay solicitudes pendientes en la cola.");
            }
        });

        VBox layoutTurnos = new VBox(18, lblTurnoTitulo, txtSolicitante, btnRegistrarTurno, btnAtenderTurno, lblResultadoTurno);
        layoutTurnos.setAlignment(Pos.CENTER);
        layoutTurnos.setPadding(new Insets(40));
        tabTurnos.setContent(layoutTurnos);


        tabPane.getTabs().addAll(tabInsumos, tabTurnos);

        BorderPane root = new BorderPane();
        root.setCenter(tabPane);

        Scene scene = new Scene(root, 820, 520);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void guardarDatosEnArchivo() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_DATOS))) {
            for (Insumo insumo : listaInsumos) {
                pw.println(insumo.getId() + "," + insumo.getNombre() + "," + insumo.getStock());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cargarDatosDesdeArchivo() {
        File archivo = new File(ARCHIVO_DATOS);
        if (!archivo.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            int maxId = 0;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(",");
                if (partes.length == 3) {
                    String id = partes[0];
                    String nombre = partes[1];
                    int stock = Integer.parseInt(partes[2]);

                    listaInsumos.add(new Insumo(id, nombre, stock));

                    try {
                        int num = Integer.parseInt(id.replace("INS-", ""));
                        if (num > maxId) maxId = num;
                    } catch (Exception ignored) {}
                }
            }
            contadorId = maxId + 1;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}