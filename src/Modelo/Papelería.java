package Modelo;

public class Papelería extends Producto {
    private final String marca;
    private int stock;

    public Papelería(String codigo, String nombre, double precio, String marca, int stock) {
        super(codigo, nombre, precio);
        this.marca = marca;
        this.stock = stock;
    }

    public String getMarca() {
        return marca;
    }

    @Override
    public String getTipo() {
        return "Papelería";
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
