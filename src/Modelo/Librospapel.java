package Modelo;

public class Librospapel extends Producto {
    private final String autor;
    private final String apodo;
    private int stock;

    public Librospapel(String codigo, String nombre, double precio, String autor, String apodo, int stock) {
        super(codigo, nombre, precio);
        this.autor = autor;
        this.apodo = apodo;
        this.stock = stock;
    }

    public String getAutor() {
        return autor;
    }

    public String getApodo() {
        return apodo;
    }

    @Override
    public String getTipo() {
        return "Libro de papel";
    }

    @Override
    public Integer getStock() {
        return stock;
    }

    @Override
    public boolean tieneStock(int cantidad) {
        return cantidad > 0 && stock >= cantidad;
    }

    @Override
    public void descontarStock(int cantidad) {
        if (!tieneStock(cantidad)) {
            throw new IllegalArgumentException("No hay suficientes unidades de " + getNombre());
        }
        stock -= cantidad;
    }
}
