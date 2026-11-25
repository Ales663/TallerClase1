import Modelo.Cliente;
import Modelo.Librosdigitales;
import Modelo.Librospapel;
import Modelo.Papelería;
import Modelo.Producto;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.AbstractDocument;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Main {
    private final List<Producto> productos = new ArrayList<>();
    private final List<Cliente> clientes = new ArrayList<>();
    private final Map<Producto, Integer> carrito = new LinkedHashMap<>();
    private final NumberFormat moneda = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-EC"));

    private final JTextField codigoField = new JTextField(16);
    private final JTextField nombreField = new JTextField(16);
    private final JTextField precioField = new JTextField(16);
    private final JTextField autorPapelField = new JTextField(16);
    private final JTextField autorDigitalField = new JTextField(16);
    private final JTextField apodoPapelField = new JTextField(16);
    private final JTextField apodoDigitalField = new JTextField(16);
    private final JComboBox<String> formatoCombo = new JComboBox<>(new String[]{"PDF", "EPUB"});
    private final JTextField marcaField = new JTextField(16);
    private final JTextField stockPapelField = new JTextField(16);
    private final JTextField stockPapeleriaField = new JTextField(16);
    private final JComboBox<String> tipoCombo = new JComboBox<>(
            new String[]{"Libro de papel", "Libro digital", "Papelería"});
    private final JPanel detallesProducto = new JPanel(new CardLayout());
    private final JTextField buscarField = new JTextField(20);
    private final DefaultTableModel productosModel = modeloTabla(
            new String[]{"Código", "Nombre", "Apodo", "Tipo", "Precio", "Stock"});
    private final JTable productosTable = new JTable(productosModel);

    private final JTextField clienteNombreField = new JTextField(20);
    private final JCheckBox clienteSocioCheck = new JCheckBox("Tiene tarjeta de socio");
    private final DefaultTableModel clientesModel = modeloTabla(new String[]{"Nombre", "Tarjeta de socio"});
    private final JTable clientesTable = new JTable(clientesModel);
    private final JComboBox<Cliente> clienteCombo = new JComboBox<>();
    private final JComboBox<Producto> productoCombo = new JComboBox<>();
    private final JSpinner cantidadSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));
    private final DefaultTableModel carritoModel = modeloTabla(
            new String[]{"Producto", "Cantidad", "Precio unitario", "Subtotal"});
    private final JTable carritoTable = new JTable(carritoModel);

    private Main() {
        ((AbstractDocument) codigoField.getDocument()).setDocumentFilter(new CodigoDocumentFilter());
        ((AbstractDocument) nombreField.getDocument()).setDocumentFilter(new MaxLengthDocumentFilter(40));
        ((AbstractDocument) precioField.getDocument()).setDocumentFilter(new NumeroDocumentFilter(true));
        ((AbstractDocument) stockPapelField.getDocument()).setDocumentFilter(new NumeroDocumentFilter(false));
        ((AbstractDocument) stockPapeleriaField.getDocument()).setDocumentFilter(new NumeroDocumentFilter(false));
        formatoCombo.setEditable(false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().mostrar());
    }

    private void mostrar() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "No se pudo aplicar el estilo del sistema: " + e.getMessage(),
                    "Aviso", JOptionPane.WARNING_MESSAGE);
        }

        JFrame ventana = new JFrame("Páginas Andinas - Caja");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setSize(950, 650);
        ventana.setLocationRelativeTo(null);

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.setFont(new Font("SansSerif", Font.BOLD, 16));
        pestanas.addTab("Productos", construirPanelProductos());
        pestanas.addTab("Clientes", construirPanelClientes());
        pestanas.addTab("Ventas", construirPanelVentas());
        ventana.add(pestanas);
        ventana.setVisible(true);
    }

    private JPanel construirPanelProductos() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel formulario = new JPanel(new BorderLayout(8, 8));
        formulario.setBorder(BorderFactory.createTitledBorder("Registrar producto"));
        JPanel comunes = new JPanel(new GridLayout(0, 2, 8, 8));
        comunes.add(new JLabel("Tipo:"));
        comunes.add(tipoCombo);
        comunes.add(new JLabel("Código único:"));
        comunes.add(codigoField);
        comunes.add(new JLabel("Nombre:"));
        comunes.add(nombreField);
        comunes.add(new JLabel("Precio:"));
        comunes.add(precioField);

        detallesProducto.add(panelLibroPapel(), "Libro de papel");
        detallesProducto.add(panelLibroDigital(), "Libro digital");
        detallesProducto.add(panelPapeleria(), "Papelería");
        tipoCombo.addActionListener(e -> ((CardLayout) detallesProducto.getLayout())
                .show(detallesProducto, (String) tipoCombo.getSelectedItem()));

        JButton registrar = boton("Registrar producto");
        registrar.addActionListener(e -> registrarProducto());
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        acciones.add(registrar);
        formulario.add(comunes, BorderLayout.NORTH);
        formulario.add(detallesProducto, BorderLayout.CENTER);
        formulario.add(acciones, BorderLayout.SOUTH);

        JPanel listado = new JPanel(new BorderLayout(8, 8));
        listado.setBorder(BorderFactory.createTitledBorder("Productos registrados"));
        JPanel busqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        busqueda.add(new JLabel("Buscar por nombre:"));
        busqueda.add(buscarField);
        JButton buscar = boton("Buscar");
        buscar.addActionListener(e -> actualizarTablaProductos());
        JButton limpiarBusqueda = boton("Mostrar todos");
        limpiarBusqueda.addActionListener(e -> {
            buscarField.setText("");
            actualizarTablaProductos();
        });
        busqueda.add(buscar);
        busqueda.add(limpiarBusqueda);
        buscarField.addActionListener(e -> actualizarTablaProductos());
        listado.add(busqueda, BorderLayout.NORTH);
        listado.add(new JScrollPane(productosTable), BorderLayout.CENTER);

        panel.add(formulario, BorderLayout.NORTH);
        panel.add(listado, BorderLayout.CENTER);
        return panel;
    }

    private JPanel panelLibroPapel() {
        JPanel p = new JPanel(new GridLayout(0, 2, 8, 8));
        p.add(new JLabel("Autor:"));
        p.add(autorPapelField);
        p.add(new JLabel("Unidades disponibles:"));
        p.add(stockPapelField);
        p.add(new JLabel("Apodo (opcional):"));
        p.add(apodoPapelField);
        return p;
    }

    private JPanel panelLibroDigital() {
        JPanel p = new JPanel(new GridLayout(0, 2, 8, 8));
        p.add(new JLabel("Autor:"));
        p.add(autorDigitalField);
        p.add(new JLabel("Formato:"));
        p.add(formatoCombo);
        p.add(new JLabel("Apodo (opcional):"));
        p.add(apodoDigitalField);
        return p;
    }

    private JPanel panelPapeleria() {
        JPanel p = new JPanel(new GridLayout(0, 2, 8, 8));
        p.add(new JLabel("Marca:"));
        p.add(marcaField);
        p.add(new JLabel("Unidades disponibles:"));
        p.add(stockPapeleriaField);
        return p;
    }

    private JPanel construirPanelVentas() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JPanel encabezado = new JPanel(new GridLayout(0, 2, 8, 8));
        encabezado.setBorder(BorderFactory.createTitledBorder("Nueva venta"));
        encabezado.add(new JLabel("Cliente:"));
        encabezado.add(clienteCombo);
        encabezado.add(new JLabel("Producto:"));
        encabezado.add(productoCombo);
        encabezado.add(new JLabel("Cantidad:"));
        encabezado.add(cantidadSpinner);

        JButton agregar = boton("Agregar al carrito");
        agregar.addActionListener(e -> agregarAlCarrito());
        JPanel accionesLinea = new JPanel(new FlowLayout(FlowLayout.LEFT));
        accionesLinea.add(agregar);
        encabezado.add(new JLabel(""));
        encabezado.add(accionesLinea);

        JPanel carritoPanel = new JPanel(new BorderLayout(8, 8));
        carritoPanel.setBorder(BorderFactory.createTitledBorder("Productos de la venta"));
        carritoPanel.add(new JScrollPane(carritoTable), BorderLayout.CENTER);
        JButton quitar = boton("Quitar producto seleccionado");
        quitar.addActionListener(e -> quitarDelCarrito());
        carritoPanel.add(quitar, BorderLayout.SOUTH);

        JButton registrarVenta = boton("Registrar venta y mostrar total");
        registrarVenta.addActionListener(e -> registrarVenta());
        JPanel inferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        inferior.add(registrarVenta);

        panel.add(encabezado, BorderLayout.NORTH);
        panel.add(carritoPanel, BorderLayout.CENTER);
        panel.add(inferior, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel construirPanelClientes() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel formulario = new JPanel(new GridLayout(0, 2, 8, 8));
        formulario.setBorder(BorderFactory.createTitledBorder("Registrar cliente"));
        formulario.add(new JLabel("Nombre:"));
        formulario.add(clienteNombreField);
        formulario.add(new JLabel("Descuento de socio:"));
        formulario.add(clienteSocioCheck);

        JButton registrar = boton("Registrar cliente");
        registrar.addActionListener(e -> registrarCliente());
        formulario.add(new JLabel(""));
        formulario.add(registrar);

        JPanel listado = new JPanel(new BorderLayout());
        listado.setBorder(BorderFactory.createTitledBorder("Clientes registrados"));
        listado.add(new JScrollPane(clientesTable), BorderLayout.CENTER);

        panel.add(formulario, BorderLayout.NORTH);
        panel.add(listado, BorderLayout.CENTER);
        return panel;
    }

    private void registrarCliente() {
        String nombre = clienteNombreField.getText().trim();
        if (nombre.isEmpty()) {
            mostrarError("Ingrese el nombre del cliente.");
            return;
        }
        for (Cliente cliente : clientes) {
            if (cliente.getNombre().equalsIgnoreCase(nombre)) {
                mostrarError("Ya existe un cliente con ese nombre.");
                return;
            }
        }

        Cliente cliente = new Cliente(nombre, clienteSocioCheck.isSelected());
        clientes.add(cliente);
        clienteCombo.addItem(cliente);
        clientesModel.addRow(new Object[]{cliente.getNombre(), cliente.esSocio() ? "Sí" : "No"});
        clienteNombreField.setText("");
        clienteSocioCheck.setSelected(false);
        JOptionPane.showMessageDialog(null, "Cliente registrado y disponible para las ventas.");
    }

    private void registrarProducto() {
        try {
            String codigo = codigoField.getText().trim();
            String nombre = nombreField.getText().trim();
            String tipo = (String) tipoCombo.getSelectedItem();
            if (!codigo.matches("[A-Z]{3}-[0-9]{3}")) {
                throw new IllegalArgumentException("El código debe tener el formato ABC-001 (tres letras, guion y tres números).");
            }
            if (nombre.length() < 3 || nombre.length() > 40) {
                throw new IllegalArgumentException("El nombre debe tener entre 3 y 40 caracteres.");
            }
            for (Producto producto : productos) {
                if (producto.getCodigo().equalsIgnoreCase(codigo)) {
                    throw new IllegalArgumentException("Ya existe un producto con ese código.");
                }
            }
            if (precioField.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("El precio es obligatorio.");
            }
            BigDecimal precio = new BigDecimal(precioField.getText().trim().replace(',', '.'))
                    .setScale(2, RoundingMode.HALF_UP);
            if (precio.signum() < 0) {
                throw new IllegalArgumentException("El precio no puede ser negativo.");
            }

            Producto nuevo;
            if ("Libro de papel".equals(tipo)) {
                nuevo = new Librospapel(codigo, nombre, precio.doubleValue(),
                        obligatorio(autorPapelField.getText(), "El autor es obligatorio."),
                        apodoPapelField.getText().trim(),
                        leerStock(stockPapelField));
            } else if ("Libro digital".equals(tipo)) {
                String formato = (String) formatoCombo.getSelectedItem();
                nuevo = new Librosdigitales(codigo, nombre, precio.doubleValue(),
                        obligatorio(autorDigitalField.getText(), "El autor es obligatorio."),
                        formato,
                        apodoDigitalField.getText().trim());
            } else {
                nuevo = new Papelería(codigo, nombre, precio.doubleValue(),
                        obligatorio(marcaField.getText(), "La marca es obligatoria."),
                        leerStock(stockPapeleriaField));
            }
            productos.add(nuevo);
            limpiarFormularioProducto();
            actualizarTablaProductos();
            actualizarProductosVenta();
            JOptionPane.showMessageDialog(null, "Producto registrado correctamente.");
        } catch (NumberFormatException e) {
            mostrarError("Ingrese un precio válido y un número entero de unidades.");
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    private int leerStock(JTextField campo) {
        int stock = Integer.parseInt(campo.getText().trim());
        if (stock < 0) {
            throw new IllegalArgumentException("Las unidades disponibles no pueden ser negativas.");
        }
        return stock;
    }

    private String obligatorio(String valor, String mensaje) {
        if (valor.trim().isEmpty()) {
            throw new IllegalArgumentException(mensaje);
        }
        return valor.trim();
    }

    private void limpiarFormularioProducto() {
        codigoField.setText("");
        nombreField.setText("");
        precioField.setText("");
        autorPapelField.setText("");
        autorDigitalField.setText("");
        apodoPapelField.setText("");
        apodoDigitalField.setText("");
        formatoCombo.setSelectedIndex(0);
        marcaField.setText("");
        stockPapelField.setText("");
        stockPapeleriaField.setText("");
    }

    private void actualizarTablaProductos() {
        productosModel.setRowCount(0);
        String filtro = normalizar(buscarField.getText().trim());
        List<ProductoConPuntaje> resultados = new ArrayList<>();
        for (Producto producto : productos) {
            double puntaje = filtro.isEmpty() ? 1.0 : puntajeCoincidencia(filtro, producto);
            if (filtro.isEmpty() || puntaje >= 0.30) {
                resultados.add(new ProductoConPuntaje(producto, puntaje));
            }
        }
        resultados.sort(Comparator.comparingDouble(ProductoConPuntaje::getPuntaje).reversed());
        int limite = filtro.isEmpty() ? resultados.size() : Math.min(10, resultados.size());
        for (int i = 0; i < limite; i++) {
            Producto producto = resultados.get(i).getProducto();
            productosModel.addRow(new Object[]{
                    producto.getCodigo(),
                    producto.getNombre(),
                    obtenerApodo(producto),
                    producto.getTipo(),
                    moneda.format(producto.getPrecio()),
                    producto.getStock() == null ? "Sin límite" : producto.getStock()
            });
        }
    }

    private double puntajeCoincidencia(String consulta, Producto producto) {
        String nombre = normalizar(producto.getNombre());
        String apodo = normalizar(obtenerApodo(producto));
        if (nombre.contains(consulta) || (!apodo.isEmpty() && apodo.contains(consulta))) {
            return 1.0;
        }
        return Math.max(puntajeTexto(consulta, nombre), puntajeTexto(consulta, apodo));
    }

    private double puntajeTexto(String consulta, String texto) {
        if (texto.isEmpty()) {
            return 0.0;
        }
        String[] palabrasConsulta = consulta.split("\\s+");
        String[] palabrasTexto = texto.split("\\s+");
        double suma = 0.0;
        int comparadas = 0;
        for (String palabraConsulta : palabrasConsulta) {
            if (palabraConsulta.length() < 2) {
                continue;
            }
            double mejor = 0.0;
            for (String palabraTexto : palabrasTexto) {
                mejor = Math.max(mejor, similitud(palabraConsulta, palabraTexto));
            }
            suma += mejor;
            comparadas++;
        }
        if (comparadas == 0) {
            return 0.0;
        }
        return suma / comparadas;
    }

    private double similitud(String a, String b) {
        int longitud = Math.max(a.length(), b.length());
        return longitud == 0 ? 1.0 : 1.0 - (double) distanciaEdicion(a, b) / longitud;
    }

    private int distanciaEdicion(String a, String b) {
        int[] anterior = new int[b.length() + 1];
        for (int j = 0; j <= b.length(); j++) {
            anterior[j] = j;
        }
        for (int i = 1; i <= a.length(); i++) {
            int[] actual = new int[b.length() + 1];
            actual[0] = i;
            for (int j = 1; j <= b.length(); j++) {
                int costo = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                actual[j] = Math.min(Math.min(actual[j - 1] + 1, anterior[j] + 1),
                        anterior[j - 1] + costo);
            }
            anterior = actual;
        }
        return anterior[b.length()];
    }

    private String normalizar(String texto) {
        return Normalizer.normalize(texto.toLowerCase(Locale.ROOT), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }

    private String obtenerApodo(Producto producto) {
        if (producto instanceof Librospapel) {
            return ((Librospapel) producto).getApodo();
        }
        if (producto instanceof Librosdigitales) {
            return ((Librosdigitales) producto).getApodo();
        }
        return "";
    }

    private void actualizarProductosVenta() {
        productoCombo.removeAllItems();
        for (Producto producto : productos) {
            productoCombo.addItem(producto);
        }
    }

    private void agregarAlCarrito() {
        Producto producto = (Producto) productoCombo.getSelectedItem();
        if (producto == null) {
            mostrarError("Registre primero al menos un producto.");
            return;
        }
        int cantidad = (Integer) cantidadSpinner.getValue();
        int totalSolicitado = cantidad + carrito.getOrDefault(producto, 0);
        if (!producto.tieneStock(totalSolicitado)) {
            mostrarError("No hay suficientes unidades disponibles de " + producto.getNombre() + ".");
            return;
        }
        carrito.put(producto, totalSolicitado);
        actualizarTablaCarrito();
    }

    private void quitarDelCarrito() {
        int fila = carritoTable.getSelectedRow();
        if (fila < 0) {
            mostrarError("Seleccione un producto del carrito.");
            return;
        }
        Producto producto = new ArrayList<>(carrito.keySet()).get(fila);
        carrito.remove(producto);
        actualizarTablaCarrito();
    }

    private void actualizarTablaCarrito() {
        carritoModel.setRowCount(0);
        for (Map.Entry<Producto, Integer> item : carrito.entrySet()) {
            BigDecimal subtotal = BigDecimal.valueOf(item.getKey().getPrecio())
                    .multiply(BigDecimal.valueOf(item.getValue()));
            carritoModel.addRow(new Object[]{
                    item.getKey().getNombre(),
                    item.getValue(),
                    moneda.format(item.getKey().getPrecio()),
                    moneda.format(subtotal)
            });
        }
    }

    private void registrarVenta() {
        if (carrito.isEmpty()) {
            mostrarError("Agregue al menos un producto a la venta.");
            return;
        }
        Cliente cliente = (Cliente) clienteCombo.getSelectedItem();
        if (cliente == null) {
            mostrarError("No hay clientes precargados.");
            return;
        }

        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal descuento = BigDecimal.ZERO;
        BigDecimal iva = BigDecimal.ZERO;
        StringBuilder detalle = new StringBuilder();

        for (Map.Entry<Producto, Integer> item : carrito.entrySet()) {
            Producto producto = item.getKey();
            int cantidad = item.getValue();
            if (!producto.tieneStock(cantidad)) {
                mostrarError("Stock insuficiente para " + producto.getNombre() + ".");
                return;
            }
            BigDecimal importe = BigDecimal.valueOf(producto.getPrecio())
                    .multiply(BigDecimal.valueOf(cantidad));
            subtotal = subtotal.add(importe);
            BigDecimal descuentoLinea = cliente.esSocio() && !(producto instanceof Papelería)
                    ? importe.multiply(new BigDecimal("0.10")).setScale(2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;
            BigDecimal ivaLinea = producto instanceof Papelería
                    ? importe.multiply(new BigDecimal("0.15")).setScale(2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;
            descuento = descuento.add(descuentoLinea);
            iva = iva.add(ivaLinea);
            detalle.append(cantidad).append(" x ").append(producto.getNombre())
                    .append(": ").append(moneda.format(importe));
            if (descuentoLinea.signum() > 0) {
                detalle.append(" (descuento ").append(moneda.format(descuentoLinea)).append(")");
            }
            if (ivaLinea.signum() > 0) {
                detalle.append(" (IVA ").append(moneda.format(ivaLinea)).append(")");
            }
            detalle.append("\n");
        }

        for (Map.Entry<Producto, Integer> item : carrito.entrySet()) {
            item.getKey().descontarStock(item.getValue());
        }
        BigDecimal total = subtotal.subtract(descuento).add(iva);
        JOptionPane.showMessageDialog(null,
                "Venta para: " + cliente.getNombre()
                        + (cliente.esSocio() ? " (socio)" : "") + "\n\n"
                        + detalle
                        + "\nSubtotal: " + moneda.format(subtotal)
                        + "\nDescuento: -" + moneda.format(descuento)
                        + "\nIVA: " + moneda.format(iva)
                        + "\nTOTAL A PAGAR: " + moneda.format(total),
                "Venta registrada", JOptionPane.INFORMATION_MESSAGE);
        carrito.clear();
        actualizarTablaCarrito();
        actualizarTablaProductos();
    }

    private JButton boton(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("SansSerif", Font.BOLD, 15));
        return boton;
    }

    private static DefaultTableModel modeloTabla(String[] columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private static final class ProductoConPuntaje {
        private final Producto producto;
        private final double puntaje;

        private ProductoConPuntaje(Producto producto, double puntaje) {
            this.producto = producto;
            this.puntaje = puntaje;
        }

        private Producto getProducto() {
            return producto;
        }

        private double getPuntaje() {
            return puntaje;
        }
    }

    private static final class CodigoDocumentFilter extends DocumentFilter {
        private static final String FORMATO_PARCIAL = "(?:[A-Z]{0,3}|[A-Z]{3}-[0-9]{0,3})";

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                throws BadLocationException {
            reemplazar(fb, offset, 0, string, attr);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                throws BadLocationException {
            reemplazar(fb, offset, length, text, attrs);
        }

        private void reemplazar(FilterBypass fb, int offset, int length, String texto, AttributeSet attrs)
                throws BadLocationException {
            String actual = fb.getDocument().getText(0, fb.getDocument().getLength());
            String nuevo = actual.substring(0, offset)
                    + (texto == null ? "" : texto.toUpperCase(Locale.ROOT))
                    + actual.substring(offset + length);
            if (nuevo.matches(FORMATO_PARCIAL)) {
                fb.replace(offset, length, texto == null ? "" : texto.toUpperCase(Locale.ROOT), attrs);
            }
        }
    }

    private static final class NumeroDocumentFilter extends DocumentFilter {
        private final boolean decimal;

        private NumeroDocumentFilter(boolean decimal) {
            this.decimal = decimal;
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                throws BadLocationException {
            reemplazar(fb, offset, 0, string, attr);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                throws BadLocationException {
            reemplazar(fb, offset, length, text, attrs);
        }

        private void reemplazar(FilterBypass fb, int offset, int length, String texto, AttributeSet attrs)
                throws BadLocationException {
            String actual = fb.getDocument().getText(0, fb.getDocument().getLength());
            String nuevo = actual.substring(0, offset) + (texto == null ? "" : texto)
                    + actual.substring(offset + length);
            String formato = decimal ? "[0-9]*(?:[.,][0-9]{0,2})?" : "[0-9]*";
            if (nuevo.matches(formato)) {
                fb.replace(offset, length, texto == null ? "" : texto, attrs);
            }
        }
    }

    private static final class MaxLengthDocumentFilter extends DocumentFilter {
        private final int maximo;

        private MaxLengthDocumentFilter(int maximo) {
            this.maximo = maximo;
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                throws BadLocationException {
            reemplazar(fb, offset, 0, string, attr);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                throws BadLocationException {
            reemplazar(fb, offset, length, text, attrs);
        }

        private void reemplazar(FilterBypass fb, int offset, int length, String texto, AttributeSet attrs)
                throws BadLocationException {
            String actual = fb.getDocument().getText(0, fb.getDocument().getLength());
            String nuevo = actual.substring(0, offset) + (texto == null ? "" : texto)
                    + actual.substring(offset + length);
            if (nuevo.length() <= maximo) {
                fb.replace(offset, length, texto == null ? "" : texto, attrs);
            }
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "No se pudo completar", JOptionPane.ERROR_MESSAGE);
    }

}
