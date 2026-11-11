package logica.tienda;

public class LineaVenta {
    private int cantidad;
    private double precioUnitario;
    private Producto producto;

    public LineaVenta(Producto producto, int cantidad, double precioUnitario) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public int getCantidad() {
        return this.cantidad;
    }

    public double getPrecioUnitario() {
        return this.precioUnitario;
    }

    public Producto getProducto() {
        return this.producto;
    }
}
