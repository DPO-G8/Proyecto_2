package logica.deportes;

import java.util.ArrayList;
import java.util.List;

import logica.Texto;
import logica.usuarios.Socio;

public class Equipo {
    private String nombre;
    private String categoria;
    private int cupoMaximo;
    private DeporteConjunto deporte;
    private List<InscripcionEquipo> plantilla;
    private List<InscripcionCompetencia> inscripcionesCompetencia;

    public Equipo(String nombre, String categoria, int cupoMaximo, DeporteConjunto deporte) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.cupoMaximo = cupoMaximo;
        this.deporte = deporte;
        this.plantilla = new ArrayList<InscripcionEquipo>();
        this.inscripcionesCompetencia = new ArrayList<InscripcionCompetencia>();
    }

    public String getNombre() {
        return this.nombre;
    }

    public String getCategoria() {
        return this.categoria;
    }

    public int getCupoMaximo() {
        return this.cupoMaximo;
    }

    public DeporteConjunto getDeporte() {
        return this.deporte;
    }

    public List<InscripcionEquipo> getPlantilla() {
        return this.plantilla;
    }

    public void agregarJugador(InscripcionEquipo inscripcion) throws Exception {
        Socio socio = inscripcion.getSocio();
        if (this.plantilla.size() >= this.cupoMaximo) {
            throw new Exception("El equipo " + this.nombre + " ya tiene el cupo completo (" + this.cupoMaximo + ")");
        }
        if (estaEnPlantilla(socio)) {
            throw new Exception(socio.getLogin() + " ya pertenece al equipo " + this.nombre);
        }
        for (InscripcionEquipo otra : this.plantilla) {
            if (otra.getNumeroCamiseta() == inscripcion.getNumeroCamiseta()) {
                throw new Exception("La camiseta " + inscripcion.getNumeroCamiseta() + " ya la usa "
                        + otra.getSocio().getLogin());
            }
        }
        int edadMaxima = edadMaximaCategoria();
        if (edadMaxima > 0 && socio.getEdad() >= edadMaxima) {
            throw new Exception(socio.getLogin() + " tiene " + socio.getEdad() + " anios y no puede jugar en "
                    + this.categoria);
        }
        this.plantilla.add(inscripcion);
    }

    public List<InscripcionCompetencia> getInscripcionesCompetencia() {
        return this.inscripcionesCompetencia;
    }

    public void inscribirseEnCompetencia(Competencia competencia, List<Socio> nomina) throws Exception {
        if (!competencia.categoriaValida(this.categoria)) {
            throw new Exception("La categoria " + this.categoria + " no esta habilitada en " + competencia.getNombre()
                    + " (solo " + competencia.getCategoriaHabilitada() + ")");
        }
        if (nomina.size() < competencia.getNominaMinima()) {
            throw new Exception("La nomina tiene " + nomina.size() + " jugadores y " + competencia.getNombre()
                    + " exige minimo " + competencia.getNominaMinima());
        }
        for (Socio jugador : nomina) {
            if (!estaEnPlantilla(jugador)) {
                throw new Exception(jugador.getLogin() + " no pertenece al equipo " + this.nombre);
            }
        }
        InscripcionCompetencia inscripcion = InscripcionCompetencia.crearEquipo(this, competencia, nomina);
        this.inscripcionesCompetencia.add(inscripcion);
    }

    public void alinearJugadores(Partido partido, List<Socio> jugadores) throws Exception {
        if (jugadores.size() < this.deporte.getMinimoJugadores()) {
            throw new Exception("Se alinearon " + jugadores.size() + " jugadores y " + this.deporte.getNombre()
                    + " exige minimo " + this.deporte.getMinimoJugadores());
        }
        for (Socio jugador : jugadores) {
            if (!estaEnPlantilla(jugador)) {
                throw new Exception(jugador.getLogin() + " no pertenece al equipo " + this.nombre);
            }
            if (jugador.estaSuspendido()) {
                throw new Exception(jugador.getLogin() + " esta suspendido hasta " + jugador.getFechaFinSuspension());
            }
            if (!partido.puedeAlinear(jugador)) {
                throw new Exception(jugador.getLogin() + " no esta en la nomina inscrita para este partido");
            }
        }
        partido.registroAlineacion(jugadores);
    }

    private int edadMaximaCategoria() {
        if (Texto.iguales(this.categoria, "Sub-13")) {
            return 13;
        }
        if (Texto.iguales(this.categoria, "Sub-15")) {
            return 15;
        }
        if (Texto.iguales(this.categoria, "Sub-17")) {
            return 17;
        }
        return 0;
    }

    private boolean estaEnPlantilla(Socio socio) {
        for (InscripcionEquipo inscripcion : this.plantilla) {
            if (inscripcion.getSocio() == socio) {
                return true;
            }
        }
        return false;
    }
}
