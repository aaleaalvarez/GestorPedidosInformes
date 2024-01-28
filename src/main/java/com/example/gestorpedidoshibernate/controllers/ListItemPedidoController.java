package com.example.gestorpedidoshibernate.controllers;

import com.example.gestorpedidoshibernate.App;
import com.example.gestorpedidoshibernate.Session;
import com.example.gestorpedidoshibernate.domain.ItemPedido.ItemPedido;
import com.example.gestorpedidoshibernate.domain.ItemPedido.ItemPedidoDAO;
import com.example.gestorpedidoshibernate.domain.Pedido.Pedido;
import com.example.gestorpedidoshibernate.domain.Pedido.PedidoDAO;
import com.example.gestorpedidoshibernate.domain.Producto.Producto;
import com.example.gestorpedidoshibernate.domain.Producto.ProductoDAO;
import com.example.gestorpedidoshibernate.ReportService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.extern.java.Log;
import net.sf.jasperreports.engine.JRException;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;

/**
 * Controlador para la vista que muestra los ítems de un pedido.
 * Permite añadir, borrar y gestionar los ítems asociados a un pedido.
 *
 * @author Alejandro Alvarez Mérida
 * @version 26-11-2023
 */
@Log
public class ListItemPedidoController implements Initializable {
    private ItemPedidoDAO itemPedidoDAO = new ItemPedidoDAO();
    private ProductoDAO productoDAO = new ProductoDAO();
    @javafx.fxml.FXML
    private MenuItem menuLogout;
    @javafx.fxml.FXML
    private MenuItem menuCerrar;
    @javafx.fxml.FXML
    private Button volverButton;
    @javafx.fxml.FXML
    private TableView<ItemPedido> tablaItems;
    @javafx.fxml.FXML
    private TableColumn<ItemPedido, String> idColumn;
    @javafx.fxml.FXML
    private TableColumn<ItemPedido, String> pedidoIdColumn;
    @javafx.fxml.FXML
    private TableColumn<ItemPedido, String> productoIdColumn;
    @javafx.fxml.FXML
    private TableColumn<ItemPedido, String> cantidadColumn;
    private Pedido pedido;
    @javafx.fxml.FXML
    private Label pedidoIdLabel;
    @javafx.fxml.FXML
    private Button añadirItem;
    @javafx.fxml.FXML
    private Button borrarItem;
    @javafx.fxml.FXML
    private Button borrarProducto;
    @javafx.fxml.FXML
    private ComboBox productoBox;
    private Pedido pedidoActual = null;
    @javafx.fxml.FXML
    private Button crearInforme;

    @javafx.fxml.FXML
    public void initialize() {
        cargarDatosItemPedido();
        cargarItemCombo();
    }

    /**
     * Carga los datos de los ítems del pedido en la tabla.
     */
    private void cargarDatosItemPedido() {
        try {
            // Obtén el ItemPedido almacenado en la sesión
            Pedido itemPedido = Session.getCurrentItemPedido();

            // Depuración
            System.out.println("Código de Pedido: " + itemPedido.getCodigo());

            if (itemPedidoDAO == null) {
                System.out.println("Error: itemPedidoDAO no ha sido inicializado");
                return;
            }

            // Obtén los ítems reales del pedido desde la base de datos
            List<ItemPedido> items = itemPedidoDAO.findItemsByPedidoCodigo(itemPedido.getCodigo());

            // Depuración
            System.out.println("Número de ítems encontrados: " + items.size());

            // Configura las columnas de la tabla
            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

            // Configura la columna de PedidoId con un Callback personalizado
            pedidoIdColumn.setCellValueFactory(cellData -> {
                String pedidoId = cellData.getValue().getPedido().getCodigo();
                return new SimpleStringProperty(pedidoId);
            });

            // Configura la columna de ProductoId con un Callback personalizado
            productoIdColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(Math.toIntExact(cellData.getValue().getProducto().getId())).asObject().asString());

            cantidadColumn.setCellValueFactory(new PropertyValueFactory<>("cantidad"));

            // Asigna los datos a la tabla
            tablaItems.setItems(FXCollections.observableArrayList(items));

            // Imprime los datos en la consola
            for (ItemPedido item : items) {
                System.out.println(item.toString());
            }
        } catch (Exception e) {
            // Maneja las excepciones según tus necesidades

            // Loggea la excepción
            log.log(Level.SEVERE, "Error al cargar datos del pedido", e);

            // Muestra un mensaje de alerta para el usuario (puedes personalizar esto)
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error al cargar datos");
            alert.setHeaderText(null);
            alert.setContentText("Hubo un error al cargar los datos del pedido.");
            alert.showAndWait();
        }
    }

    /**
     * Carga los productos disponibles en el ComboBox.
     */
    private void cargarItemCombo() {
        List<Producto> productos = productoDAO.getAll();
        List<String> nombreProductos = new ArrayList<>();
        for (Producto producto : productos) {
            nombreProductos.add(producto.getNombre());
        }
        productoBox.getItems().addAll(nombreProductos);
        System.out.println(nombreProductos);
    }

    /**
     * Agrega un listener a la tabla para detectar selecciones de ítems.
     */
    private void agregarListenerTabla() {
        tablaItems.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                ItemPedido itemSeleccionado = tablaItems.getSelectionModel().getSelectedItem();
                actualizarInterfazConItemSelec(itemSeleccionado);
            }
        });
    }

    /**
     * Actualiza los elementos de la interfaz con los detalles del item elegido
     *
     * @param itemSeleccionado Item de pedido seleccionado en la tabla.
     */
    private void actualizarInterfazConItemSelec(ItemPedido itemSeleccionado) {
        productoBox.getSelectionModel().select(itemSeleccionado.getProducto().getNombre());
    }

    /**
     * Maneja el evento de clic en el botón "Logout".
     *
     * @param actionEvent El evento de acción asociado al clic del botón.
     */
    @javafx.fxml.FXML
    public void Logout(ActionEvent actionEvent) {
        try {
            App.changeScene("login-view.fxml", "Login");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Maneja el evento de clic en el botón "Cerrar".
     *
     * @param actionEvent El evento de acción asociado al clic del botón.
     */
    @javafx.fxml.FXML
    public void Cerrar(ActionEvent actionEvent) {
        System.exit(0);
    }

    /**
     * Maneja el evento de clic en el botón "Volver".
     *
     * @param actionEvent El evento de acción asociado al clic del botón.
     */
    @javafx.fxml.FXML
    public void volver(ActionEvent actionEvent) {
        try {
            App.changeScene("listPedidos-view.fxml", "Lista de pedidos");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Inicializa la clase. Se ejecuta automáticamente al cargar la vista.
     *
     * @param url            La ubicación para la inicialización.
     * @param resourceBundle Los recursos para la inicialización.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cargarDatosItemPedido();
        cargarItemCombo();
        agregarListenerTabla();
    }

    /**
     * Maneja el evento de clic en el botón "Añadir Producto".
     *
     * @param actionEvent El evento de acción asociado al clic del botón.
     */
    @javafx.fxml.FXML
    public void añadirProducto(ActionEvent actionEvent) {
        if (itemPedidoDAO == null) {
            itemPedidoDAO = new ItemPedidoDAO();
        }
        if (productoDAO == null) {
            productoDAO = new ProductoDAO();
        }
        String codigoPedido = Session.getCurrentItemPedido().getCodigo();
        pedidoActual = PedidoDAO.findByCodigo(codigoPedido);

        String nombreProd = productoBox.getValue() != null ? productoBox.getValue().toString() : null;

        if (nombreProd == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de Entrada");
            alert.setHeaderText("Datos de Producto Inválidos");
            alert.setContentText("Por favor, selecciona un producto válido.");
            alert.showAndWait();
            return;
        }

        Producto productoSelec = productoDAO.findByName(nombreProd);
        if (productoSelec == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de Producto");
            alert.setHeaderText("Producto No Encontrado");
            alert.setContentText("El producto seleccionado no existe. Por favor, selecciona otro producto.");
            alert.showAndWait();
            return;
        }

        ItemPedido nuevoItem = new ItemPedido();
        nuevoItem.setProducto(productoSelec);
        nuevoItem.setCantidad(Integer.parseInt("1"));
        nuevoItem.setPedido(pedidoActual);

        itemPedidoDAO.save(nuevoItem);
        recargarYRefrescarTabla();
    }

    /**
     * Maneja el evento de clic en el botón "Borrar Producto".
     *
     * @param actionEvent El evento de acción asociado al clic del botón.
     */
    @javafx.fxml.FXML
    public void borrarProducto(ActionEvent actionEvent) {
        String codigoPedido = Session.getCurrentItemPedido().getCodigo();
        PedidoDAO pedidoDAO = new PedidoDAO();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("¿Quieres cancelar el pedido?");
        var result = alert.showAndWait();
        if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
            pedidoDAO.deleteByCodigo(codigoPedido);
            volver(null);
        }
    }

    /**
     * Obtiene el ítem seleccionado en la tabla.
     *
     * @return El ítem seleccionado en la tabla.
     */
    public ItemPedido getSelectedItemPedido() {
        return tablaItems.getSelectionModel().getSelectedItem();
    }

    /**
     * Maneja el evento de clic en el botón "Borrar Ítem".
     *
     * @param actionEvent El evento de acción asociado al clic del botón.
     */
    @javafx.fxml.FXML
    public void borrarItem(ActionEvent actionEvent) {
        ItemPedido itemSeleccionado = getSelectedItemPedido();

        if (itemSeleccionado != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText("¿Quieres quitar el producto de la compra?");
            var result = alert.showAndWait();

            if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                itemPedidoDAO.remove(itemSeleccionado);
                recargarYRefrescarTabla();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Por favor, selecciona un artículo para borrar.");
            alert.showAndWait();
        }
    }

    /**
     * Recarga y refresca los datos de la tabla de ítems.
     */
    private void recargarYRefrescarTabla() {
        String codigoPedido = Session.getCurrentItemPedido().getCodigo();
        List<ItemPedido> itemActualizados = itemPedidoDAO.findItemsByPedidoCodigo(codigoPedido);

        tablaItems.setItems(FXCollections.observableList(itemActualizados));
        tablaItems.refresh();
    }

    @javafx.fxml.FXML
    public void crearInforme(ActionEvent actionEvent) {
        try {
            // Obtén el código del pedido desde tu lógica de la aplicación
            String codigoPedido = Session.getCurrentItemPedido().getCodigo(); // Asegúrate de tener la lógica correcta para obtener el código del pedido

            System.out.println(codigoPedido);
            // Llama al servicio de informe y pasa el código del pedido como parámetro
            (new ReportService()).generateReport(codigoPedido);
        } catch (JRException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}