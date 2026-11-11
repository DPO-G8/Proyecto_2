package logica.tienda;

public class Bebida extends Producto {
    private boolean caliente;

    public Bebida(int idProducto, String nombre, double precio, boolean caliente) {
        super(idProducto, nombre, precio);
        this.caliente = caliente;
    }

    public boolean isCaliente() {
        return this.caliente;
    }
}
