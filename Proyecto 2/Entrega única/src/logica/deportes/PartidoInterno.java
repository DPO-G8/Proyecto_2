package logica.deportes;

import java.time.LocalDate;

public class PartidoInterno extends Partido {
    private Equipo local;
    private Equipo visitante;

    public PartidoInterno(LocalDate fecha, Equipo local, Equipo visitante) {
        super(fecha);
        this.local = local;
        this.visitante = visitante;
    }

    public Equipo getLocal() {
        return this.local;
    }

    public Equipo getVisitante() {
        return this.visitante;
    }
}
