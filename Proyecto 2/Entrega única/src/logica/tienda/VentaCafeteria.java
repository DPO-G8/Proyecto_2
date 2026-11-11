package logica.tienda;

import logica.usuarios.Usuario;

public class VentaCafeteria extends Venta {
    public static final double IMPUESTO_CONSUMO = 0.08;
    public static final double PROPINA_SUGERIDA = 0.10;

    private double porcentajePropina;

    public VentaCafeteria(Usuario comprador) {
        super(comprador);
        this.porcentajePropina = PROPINA_SUGERIDA;
    }

    public double getPorcentajePropina() {
        return this.porcentajePropina;
    }

    public void setPorcentajePropina(double porcentajePropina) {
        this.porcentajePropina = porcentajePropina;
    }

    @Override
    public double calcularTotal() {
        return super.calcularTotal() * (1 + IMPUESTO_CONSUMO + this.porcentajePropina);
    }
}
