package logica.tienda;

import logica.usuarios.Usuario;

public class VentaTienda extends Venta {
    public static final double IVA = 0.19;

    public VentaTienda(Usuario comprador) {
        super(comprador);
    }

    @Override
    public double calcularTotal() {
        return super.calcularTotal() * (1 + IVA);
    }
}
