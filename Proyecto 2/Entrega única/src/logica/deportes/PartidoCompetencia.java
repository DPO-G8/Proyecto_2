package logica.deportes;

import java.time.LocalDate;

import logica.usuarios.Socio;

public class PartidoCompetencia extends Partido {
    private String institucionRival;
    private String escenario;
    private String fase;
    private Competencia competencia;
    private Equipo equipo;

    public PartidoCompetencia(LocalDate fecha, String institucionRival, String escenario, String fase,
            Competencia competencia, Equipo equipo) {
        super(fecha);
        this.institucionRival = institucionRival;
        this.escenario = escenario;
        this.fase = fase;
        this.competencia = competencia;
        this.equipo = equipo;
    }

    public String getInstitucionRival() {
        return this.institucionRival;
    }

    public String getEscenario() {
        return this.escenario;
    }

    public String getFase() {
        return this.fase;
    }

    public Competencia getCompetencia() {
        return this.competencia;
    }

    public Equipo getEquipo() {
        return this.equipo;
    }

    @Override
    public boolean puedeAlinear(Socio socio) {
        for (InscripcionCompetencia inscripcion : this.competencia.getInscripciones()) {
            if (inscripcion.getEquipo() == this.equipo && inscripcion.getNomina().contains(socio)) {
                return true;
            }
        }
        return false;
    }
}
