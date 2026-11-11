package logica.tienda;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import logica.usuarios.Usuario;

public abstract class Venta {
    private LocalDateTime fecha;
    private Usuario comprador;
    private List<LineaVenta> lineas;
    private int puntosRedimidos;

    public Venta(Usuario comprador) {
        this.comprador = comprador;
        this.fecha = LocalDateTime.now();
        this.lineas = new ArrayList<LineaVenta>();
    }

    public LocalDateTime getFecha() {
        return this.fecha;
    }

    /** Solo se usa al cargar una venta guardada: conserva la fecha original. */
    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public int getPuntosRedimidos() {
        return this.puntosRedimidos;
    }

    /** Cada punto redimido descuenta un peso del subtotal (ya con descuento de socio/empleado). */
    public void setPuntosRedimidos(int puntosRedimidos) {
        this.puntosRedimidos = puntosRedimidos;
    }

    public Usuario getComprador() {
        return this.comprador;
    }

    public List<LineaVenta> getLineas() {
        return this.lineas;
    }

    public void agregarLinea(Producto producto, int cantidad, double precio) {
        this.lineas.add(new LineaVenta(producto, cantidad, precio));
    }

    public double calcularSubtotal() {
        double subtotal = 0;
        for (LineaVenta linea : this.lineas) {
            subtotal += linea.getPrecioUnitario() * linea.getCantidad();
        }
        return subtotal;
    }

    public double calcularTotal() {
        double base = calcularSubtotal() * (1 - this.comprador.getPorcentajeDescuento()) - this.puntosRedimidos;
        return Math.max(base, 0);
    }
}
