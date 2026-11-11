package logica.tienda;

public class Sugerencia {
    public static final String PENDIENTE = "PENDIENTE";
    public static final String APROBADA = "APROBADA";
    public static final String RECHAZADA = "RECHAZADA";

    private String estado;
    private Producto producto;

    public Sugerencia(Producto producto) {
        this.producto = producto;
        this.estado = PENDIENTE;
    }

    public String getEstado() {
        return this.estado;
    }

    public Producto getProducto() {
        return this.producto;
    }

    public void aprobar() throws Exception {
        if (!this.estado.equals(PENDIENTE)) {
            throw new Exception("Solo se puede aprobar una sugerencia pendiente; esta ya esta " + this.estado);
        }
        this.estado = APROBADA;
    }

    public void rechazar() throws Exception {
        if (!this.estado.equals(PENDIENTE)) {
            throw new Exception("Solo se puede rechazar una sugerencia pendiente; esta ya esta " + this.estado);
        }
        this.estado = RECHAZADA;
    }
}
