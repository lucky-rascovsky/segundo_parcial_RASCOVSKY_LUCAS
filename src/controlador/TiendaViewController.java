package controlador;




import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import modelo.ItemCarrito;
import modelo.Producto;


public class TiendaViewController implements Initializable {

    private static final String ARCHIVO_DAT = "productos.dat";
    private static final String ARCHIVO_TICKET = "ticket.txt";

    @FXML
    private TableView<Producto> tablaProductos;

    @FXML
    private TableColumn<Producto, String> colNombre;

    @FXML
    private TableColumn<Producto, Double> colPrecio;

    @FXML
    private TableColumn<Producto, Integer> colStock;

    @FXML
    private TextField txtCantidad;

    @FXML
    private Button btnAgregar;

    @FXML
    private ListView<ItemCarrito> listaCarrito;

    @FXML
    private Button btnConfirmar;

    @FXML
    private Label lblMensaje;

    private ArrayList<Producto> productos;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));

        productos = deserializarProductos();
        if (productos != null) {
            tablaProductos.getItems().setAll(productos);
        }
    }

    private ArrayList<Producto> deserializarProductos() {
        File archivo = new File(ARCHIVO_DAT);
        ObjectInputStream ois = null;
        ArrayList<Producto> listaCargada = null;

        try {
            FileInputStream fis = new FileInputStream(archivo);
            ois = new ObjectInputStream(fis);
            listaCargada = (ArrayList<Producto>) ois.readObject();
            lblMensaje.setText("Productos cargados correctamente.");
        } catch (IOException | ClassNotFoundException e) {
            lblMensaje.setText("Error al leer el archivo de productos: " + e.getMessage());
        } finally {
            try {
                if (ois != null) {
                    ois.close();
                }
            } catch (IOException e) {
                lblMensaje.setText("Error al cerrar el archivo: " + e.getMessage());
            }
        }
        return listaCargada;
    }

    @FXML
    private void agregarAlCarrito() {
        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            lblMensaje.setText("Debe seleccionar un producto de la tabla.");
            return;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(txtCantidad.getText().trim());
        } catch (NumberFormatException e) {
            lblMensaje.setText("Ingrese una cantidad numérica válida.");
            return;
        }

        if (cantidad <= 0) {
            lblMensaje.setText("La cantidad debe ser mayor a 0.");
            return;
        }

        int cantidadYaEnCarrito = 0;
        for (ItemCarrito item : listaCarrito.getItems()) {
            if (item.getProducto().equals(seleccionado)) {
                cantidadYaEnCarrito += item.getCantidad();
            }
        }

        if (cantidad + cantidadYaEnCarrito > seleccionado.getStock()) {
            lblMensaje.setText("Stock insuficiente para el producto seleccionado.");
            return;
        }

        boolean encontrado = false;
        for (ItemCarrito item : listaCarrito.getItems()) {
            if (item.getProducto().equals(seleccionado)) {
                item.setCantidad(item.getCantidad() + cantidad);
                encontrado = true;
                break;
            }
        }

        if (!encontrado) {
            listaCarrito.getItems().add(new ItemCarrito(seleccionado, cantidad));
        }

        listaCarrito.refresh();
        txtCantidad.clear();
        lblMensaje.setText("Producto agregado al carrito.");
    }

    @FXML
    private void confirmarCompra() {
        if (listaCarrito.getItems().isEmpty()) {
            lblMensaje.setText("El carrito está vacío. Agregue al menos un producto.");
            return;
        }

        double total = 0;
        for (ItemCarrito item : listaCarrito.getItems()) {
            total += item.getSubtotal();
        }

        if (!generarTicket(total)) {
            return;
        }

        for (ItemCarrito item : listaCarrito.getItems()) {
            Producto prod = item.getProducto();
            int nuevoStock = prod.getStock() - item.getCantidad();
            prod.setStock(nuevoStock);
        }

        tablaProductos.refresh();

        if (!serializarProductos()) {
            return;
        }

        listaCarrito.getItems().clear();
        lblMensaje.setText("Compra confirmada. Ticket generado y stock actualizado.");
    }

    private boolean generarTicket(double total) {
        File archivo = new File(ARCHIVO_TICKET);
        BufferedWriter bw = null;

        try {
            FileWriter fw = new FileWriter(archivo);
            bw = new BufferedWriter(fw);
            bw.write("Nombre producto\tCantidad\tSubtotal");
            bw.newLine();

            for (ItemCarrito item : listaCarrito.getItems()) {
                bw.write(item.getProducto().getNombre() + "\t" + item.getCantidad() + "\t" + item.getSubtotal());
                bw.newLine();
            }

            bw.write("TOTAL A PAGAR: $" + total);
            bw.newLine();
            return true;
        } catch (IOException e) {
            lblMensaje.setText("Error al generar el ticket: " + e.getMessage());
            return false;
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException e) {
                lblMensaje.setText("Error al cerrar el ticket: " + e.getMessage());
            }
        }
    }

    private boolean serializarProductos() {
        File archivo = new File(ARCHIVO_DAT);
        ObjectOutputStream oos = null;

        try {
            FileOutputStream fos = new FileOutputStream(archivo);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(productos);
            return true;
        } catch (IOException e) {
            lblMensaje.setText("Error al guardar el archivo de productos: " + e.getMessage());
            return false;
        } finally {
            try {
                if (oos != null) {
                    oos.close();
                }
            } catch (IOException e) {
                lblMensaje.setText("Error al cerrar el archivo .dat: " + e.getMessage());
            }
        }
    }
    }    
    

