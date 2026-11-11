package logica.deportes;

import java.util.ArrayList;
import java.util.List;

import logica.Texto;

public class Competencia {
    private String nombre;
    private String entidadOrganizadora;
    private String temporada;
    private String categoriaHabilitada;
    private String formatoJuego;
    private int nominaMinima;
    private DeporteConjunto deporte;
    private List<InscripcionCompetencia> inscripciones;

    public Competencia(String nombre, String entidadOrganizadora, String temporada, String categoriaHabilitada,
            String formatoJuego, int nominaMinima, DeporteConjunto deporte) {
        this.nombre = nombre;
        this.entidadOrganizadora = entidadOrganizadora;
        this.temporada = temporada;
        this.categoriaHabilitada = categoriaHabilitada;
        this.formatoJuego = formatoJuego;
        this.nominaMinima = nominaMinima;
        this.deporte = deporte;
        this.inscripciones = new ArrayList<InscripcionCompetencia>();
    }

    public String getNombre() {
        return this.nombre;
    }

    public String getEntidadOrganizadora() {
        return this.entidadOrganizadora;
    }

    public String getTemporada() {
        return this.temporada;
    }

    public String getCategoriaHabilitada() {
        return this.categoriaHabilitada;
    }

    public String getFormatoJuego() {
        return this.formatoJuego;
    }

    public int getNominaMinima() {
        return this.nominaMinima;
    }

    public DeporteConjunto getDeporte() {
        return this.deporte;
    }

    public List<InscripcionCompetencia> getInscripciones() {
        return this.inscripciones;
    }

    public void agregarInscripcion(InscripcionCompetencia inscripcion) {
        this.inscripciones.add(inscripcion);
    }

    public boolean categoriaValida(String categoria) {
        return Texto.iguales(this.categoriaHabilitada, categoria);
    }

    public int calcularPuntos(int golesFavor, int golesContra) throws Exception {
        if (golesFavor > golesContra) {
            return 3;
        }
        if (golesFavor < golesContra) {
            return 0;
        }
        if (Texto.iguales(this.deporte.getNombre(), "Futbol")) {
            return 1;
        }
        throw new Exception("En " + this.deporte.getNombre() + " no hay empates: " + golesFavor + "-" + golesContra);
    }
}
