package logica.deportes;

import logica.usuarios.Socio;

public class EstadisticaVoleibol extends Estadistica {
    private int aces;
    private int bloqueos;

    public EstadisticaVoleibol(Socio socio, Partido partido, int aces, int bloqueos) {
        super(socio, partido);
        this.aces = aces;
        this.bloqueos = bloqueos;
    }

    public int getAces() {
        return this.aces;
    }

    public int getBloqueos() {
        return this.bloqueos;
    }
}
