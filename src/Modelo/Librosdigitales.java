package Modelo;

public class Librosdigitales extends Producto {
    private final String autor;
    private final String formato;
    private final String apodo;

    public Librosdigitales(String codigo, String nombre, double precio, String autor, String formato, String apodo) {
        super(codigo, nombre, precio);
        this.autor = autor;
        this.formato = formato;
        this.apodo = apodo;
    }

    public String getAutor() {
        return autor;
    }

    public String getFormato() {
        return formato;
    }

    public String getApodo() {
        return apodo;
    }

    @Override
    public String getTipo() {
        return "Libro digital";
    }

    @Override
    public Integer getStock() {
        return null;
    }

    @Override
    public boolean tieneStock(int cantidad) {
        return cantidad > 0;
    }

    @Override
    public void descontarStock(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }
    }
}
