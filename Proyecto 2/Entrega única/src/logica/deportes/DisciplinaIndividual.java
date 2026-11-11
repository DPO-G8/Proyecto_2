package logica.deportes;

import java.util.ArrayList;
import java.util.List;

public class DisciplinaIndividual {
    private String nombre;
    private int maxInstalacionesSimultaneas;
    private List<InscripcionDisciplina> inscripciones;
    private List<Instalacion> instalaciones;

    public DisciplinaIndividual(String nombre, int maxInstalacionesSimultaneas) {
        this.nombre = nombre;
        this.maxInstalacionesSimultaneas = maxInstalacionesSimultaneas;
        this.inscripciones = new ArrayList<InscripcionDisciplina>();
        this.instalaciones = new ArrayList<Instalacion>();
    }

    public String getNombre() {
        return this.nombre;
    }

    public int getMaxInstalacionesSimultaneas() {
        return this.maxInstalacionesSimultaneas;
    }

    public List<InscripcionDisciplina> getInscripciones() {
        return this.inscripciones;
    }

    public void agregarInscripcion(InscripcionDisciplina inscripcion) {
        this.inscripciones.add(inscripcion);
    }

    public List<Instalacion> getInstalaciones() {
        return this.instalaciones;
    }

    public void agregarInstalacion(Instalacion instalacion) {
        this.instalaciones.add(instalacion);
    }
}
