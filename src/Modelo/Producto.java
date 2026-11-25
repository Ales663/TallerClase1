package Modelo;

public abstract class Producto {
    private final String codigo;
    private final String nombre;
    private final double precio;

    protected Producto(String codigo, String nombre, double precio) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public abstract String getTipo();

    public abstract Integer getStock();

    public abstract boolean tieneStock(int cantidad);

    public abstract void descontarStock(int cantidad);

    @Override
    public String toString() {
        return codigo + " - " + nombre;
    }
}
