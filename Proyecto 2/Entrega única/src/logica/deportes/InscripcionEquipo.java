package logica.deportes;

import logica.usuarios.Socio;

public class InscripcionEquipo {
    private String posicion;
    private int numeroCamiseta;
    private Socio socio;

    public InscripcionEquipo(Socio socio, String posicion, int numeroCamiseta) {
        this.socio = socio;
        this.posicion = posicion;
        this.numeroCamiseta = numeroCamiseta;
    }

    public String getPosicion() {
        return this.posicion;
    }

    public int getNumeroCamiseta() {
        return this.numeroCamiseta;
    }

    public Socio getSocio() {
        return this.socio;
    }
}
