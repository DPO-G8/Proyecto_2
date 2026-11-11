package logica.deportes;

import java.util.ArrayList;
import java.util.List;

import logica.usuarios.Socio;

public class InscripcionCompetencia {
    private Equipo equipo;
    private Competencia competencia;
    private List<Socio> nomina;

    private InscripcionCompetencia(Equipo equipo, Competencia competencia, List<Socio> nomina) {
        this.equipo = equipo;
        this.competencia = competencia;
        this.nomina = new ArrayList<Socio>(nomina);
    }

    public Equipo getEquipo() {
        return this.equipo;
    }

    public Competencia getCompetencia() {
        return this.competencia;
    }

    public List<Socio> getNomina() {
        return this.nomina;
    }

    public static InscripcionCompetencia crearEquipo(Equipo equipo, Competencia competencia, List<Socio> nomina) {
        InscripcionCompetencia inscripcion = new InscripcionCompetencia(equipo, competencia, nomina);
        competencia.agregarInscripcion(inscripcion);
        return inscripcion;
    }
}
