package logica.tienda;

public class Articulo extends Producto {
    private String categoria;
    private int cantidadDisponible;
    private String ubicacion;

    public Articulo(int idProducto, String nombre, double precio, String categoria, int cantidadDisponible,
            String ubicacion) {
        super(idProducto, nombre, precio);
        this.categoria = categoria;
        this.cantidadDisponible = cantidadDisponible;
        this.ubicacion = ubicacion;
    }

    public String getCategoria() {
        return this.categoria;
    }

    public int getCantidadDisponible() {
        return this.cantidadDisponible;
    }

    public String getUbicacion() {
        return this.ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public void descontarInventario(int cantidad) throws Exception {
        if (cantidad > this.cantidadDisponible) {
            throw new Exception("No hay suficientes unidades de " + getNombre() + ": se piden " + cantidad
                    + " y hay " + this.cantidadDisponible);
        }
        this.cantidadDisponible -= cantidad;
    }

    public void reponer(int cantidad) throws Exception {
        if (cantidad <= 0) {
            throw new Exception("La cantidad a reponer debe ser positiva, se recibio " + cantidad);
        }
        this.cantidadDisponible += cantidad;
    }
}
