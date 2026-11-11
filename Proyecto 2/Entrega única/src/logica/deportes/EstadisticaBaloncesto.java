package logica.deportes;

import logica.usuarios.Socio;

public class EstadisticaBaloncesto extends Estadistica {
    private int puntos;
    private int rebotes;

    public EstadisticaBaloncesto(Socio socio, Partido partido, int puntos, int rebotes) {
        super(socio, partido);
        this.puntos = puntos;
        this.rebotes = rebotes;
    }

    public int getPuntos() {
        return this.puntos;
    }

    public int getRebotes() {
        return this.rebotes;
    }
}
