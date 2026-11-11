package logica.deportes;

import logica.usuarios.Socio;

public class EstadisticaFutbol extends Estadistica {
    private int goles;
    private int asistencias;
    private int tarjetasAmarillas;
    private int tarjetasRojas;

    public EstadisticaFutbol(Socio socio, Partido partido, int goles, int asistencias, int tarjetasAmarillas, int tarjetasRojas) {
        super(socio, partido);
        this.goles = goles;
        this.asistencias = asistencias;
        this.tarjetasAmarillas = tarjetasAmarillas;
        this.tarjetasRojas = tarjetasRojas;
    }

    public int getGoles() {
        return this.goles;
    }

    public int getAsistencias() {
        return this.asistencias;
    }

    public int getTarjetasAmarillas() {
        return this.tarjetasAmarillas;
    }

    public int getTarjetasRojas() {
        return this.tarjetasRojas;
    }
}
