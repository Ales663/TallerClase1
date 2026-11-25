package Modelo;

public class Cliente {
    private final String nombre;
    private final boolean socio;

    public Cliente(String nombre, boolean socio) {
        this.nombre = nombre;
        this.socio = socio;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean esSocio() {
        return socio;
    }

    @Override
    public String toString() {
        return nombre + (socio ? " - socio" : "");
    }
}
