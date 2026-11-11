package logica.deportes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import logica.usuarios.Entrenador;
import logica.usuarios.Socio;

public class PartidoPractica extends Partido {
    private String modalidad;
    private DisciplinaIndividual disciplina;
    private Entrenador rival;
    private List<Socio> jugadores;

    public PartidoPractica(LocalDate fecha, String modalidad, DisciplinaIndividual disciplina, List<Socio> jugadores) {
        super(fecha);
        this.modalidad = modalidad;
        this.disciplina = disciplina;
        this.rival = null;
        this.jugadores = new ArrayList<Socio>(jugadores);
    }

    public String getModalidad() {
        return this.modalidad;
    }

    public DisciplinaIndividual getDisciplina() {
        return this.disciplina;
    }

    public Entrenador getRival() {
        return this.rival;
    }

    public void setRival(Entrenador rival) {
        this.rival = rival;
    }

    public List<Socio> getJugadores() {
        return this.jugadores;
    }
}
