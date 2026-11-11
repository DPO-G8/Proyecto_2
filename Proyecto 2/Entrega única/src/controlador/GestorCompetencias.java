package controlador;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import logica.Texto;
import logica.deportes.Competencia;
import logica.deportes.DisciplinaIndividual;
import logica.deportes.Equipo;
import logica.deportes.Estadistica;
import logica.deportes.EstadisticaFutbol;
import logica.deportes.InscripcionCompetencia;
import logica.deportes.InscripcionDisciplina;
import logica.deportes.InscripcionEquipo;
import logica.deportes.Instalacion;
import logica.deportes.Partido;
import logica.deportes.PartidoCompetencia;
import logica.deportes.PartidoInterno;
import logica.deportes.PartidoPractica;
import logica.usuarios.Entrenador;
import logica.usuarios.Socio;
import logica.usuarios.Usuario;
import persistencia.RepositorioDatos;

public class GestorCompetencias {

    // Dias de suspension (tarjetas)
    public static final int DIAS_SUSPENSION = 7;

    private final RepositorioDatos repositorio;
    private final Map<String, Usuario> usuarios;
    private final GestorEquipos gestorEquipos;
    private final GestorReservas gestorReservas;
    private final ArrayList<Competencia> competencias;
    private final ArrayList<Partido> partidos;

    public GestorCompetencias(RepositorioDatos repositorio, Map<String, Usuario> usuarios,
            GestorEquipos gestorEquipos, GestorReservas gestorReservas) throws Exception {
        this.repositorio = repositorio;
        this.usuarios = usuarios;
        this.gestorEquipos = gestorEquipos;
        this.gestorReservas = gestorReservas;
        this.competencias = repositorio.cargarCompetencias(gestorEquipos.getDeportes(), usuarios);
        this.partidos = repositorio.cargarPartidos(usuarios, gestorEquipos.getDeportes(), competencias,
                gestorReservas.getDisciplinas());
    }

    public ArrayList<Competencia> getCompetencias() {
        return competencias;
    }

    public ArrayList<Partido> getPartidos() {
        return partidos;
    }

    // Dato inicial
    public void agregarCompetencia(Competencia competencia) throws Exception {
        for (Competencia c : competencias) {
            if (Texto.iguales(c.getNombre(), competencia.getNombre())) {
                throw new Exception("Ya existe la competencia " + competencia.getNombre());
            }
        }
        competencias.add(competencia);
        repositorio.guardarCompetencias(competencias);
    }

    public Competencia buscarCompetencia(String nombre) throws Exception {
        for (Competencia c : competencias) {
            if (Texto.iguales(c.getNombre(), nombre)) {
                return c;
            }
        }
        throw new Exception("No existe la competencia '" + nombre + "'");
    }

    // RF4 Entrenador
    public void inscribirEquipo(Usuario quien, String nombreEquipo, String nombreCompetencia,
            List<String> loginsNomina) throws Exception {
        Equipo equipo = gestorEquipos.buscarEquipo(nombreEquipo);
        exigirEntrenadorDe(quien, equipo);
        Competencia competencia = buscarCompetencia(nombreCompetencia);
        if (!Texto.iguales(competencia.getDeporte().getNombre(), equipo.getDeporte().getNombre())) {
            throw new Exception("La competencia " + competencia.getNombre() + " es de "
                    + competencia.getDeporte().getNombre() + " y el equipo es de " + equipo.getDeporte().getNombre());
        }
        for (InscripcionCompetencia i : competencia.getInscripciones()) {
            if (i.getEquipo() == equipo) {
                throw new Exception("El equipo " + nombreEquipo + " ya esta inscrito en " + nombreCompetencia);
            }
        }
        ArrayList<Socio> nomina = new ArrayList<Socio>();
        for (String login : loginsNomina) {
            nomina.add(buscarSocio(login));
        }
        equipo.inscribirseEnCompetencia(competencia, nomina);
        repositorio.guardarCompetencias(competencias);
    }

    // Programa un partido oficial
    public PartidoCompetencia programarPartido(Usuario quien, String nombreEquipo, String nombreCompetencia,
            LocalDate fecha, String institucionRival, String escenario, String fase) throws Exception {
        Equipo equipo = gestorEquipos.buscarEquipo(nombreEquipo);
        exigirEntrenadorDe(quien, equipo);
        Competencia competencia = buscarCompetencia(nombreCompetencia);
        if (!estaInscrito(equipo, competencia)) {
            throw new Exception("El equipo " + nombreEquipo + " no esta inscrito en " + nombreCompetencia);
        }
        PartidoCompetencia partido = new PartidoCompetencia(fecha, institucionRival, escenario, fase, competencia,
                equipo);
        partidos.add(partido);
        repositorio.guardarPartidos(partidos);
        return partido;
    }

    // RF3 Entrenador
    public void convocar(Usuario quien, PartidoCompetencia partido, List<String> logins) throws Exception {
        exigirEntrenadorDe(quien, partido.getEquipo());
        ArrayList<Socio> jugadores = new ArrayList<Socio>();
        for (String login : logins) {
            jugadores.add(buscarSocio(login));
        }
        partido.getEquipo().alinearJugadores(partido, jugadores);
        repositorio.guardarPartidos(partidos);
    }

    // Registra el marcador
    public void registrarResultado(Usuario quien, PartidoCompetencia partido, int golesFavor, int golesContra)
            throws Exception {
        exigirEntrenadorDe(quien, partido.getEquipo());
        // calcularPuntos lanza excepcion si el resultado no es valido para el deporte (p. ej. empate en baloncesto)
        partido.getCompetencia().calcularPuntos(golesFavor, golesContra);
        partido.setMarcador(golesFavor + "-" + golesContra);
        repositorio.guardarPartidos(partidos);
    }

    // Registra estadistica; en futbol actualiza la suspension
    public void registrarEstadistica(Usuario quien, Partido partido, Estadistica estadistica) throws Exception {
        if (partido instanceof PartidoCompetencia) {
            exigirEntrenadorDe(quien, ((PartidoCompetencia) partido).getEquipo());
        } else if (partido instanceof PartidoInterno) {
            exigirEntrenadorDe(quien, ((PartidoInterno) partido).getLocal());
        } else if (!(quien instanceof Entrenador)) {
            throw new Exception("Solo un entrenador puede registrar estadisticas");
        }
        Socio socio = estadistica.getSocio();
        boolean participo = partido.getAlineados().contains(socio);
        if (partido instanceof PartidoPractica) {
            participo = ((PartidoPractica) partido).getJugadores().contains(socio);
        }
        if (!participo) {
            throw new Exception(socio.getLogin() + " no participo en este partido");
        }
        partido.agregarEstadistica(estadistica);
        socio.agregarEstadistica(estadistica);
        if (estadistica instanceof EstadisticaFutbol) {
            socio.actualizarSuspension((EstadisticaFutbol) estadistica, partido.getFecha().plusDays(DIAS_SUSPENSION));
            repositorio.guardarUsuarios(usuarios.values());
        }
        repositorio.guardarPartidos(partidos);
    }

    // Tabla de posiciones
    public Map<Equipo, Integer> tablaPosiciones(String nombreCompetencia) throws Exception {
        Competencia competencia = buscarCompetencia(nombreCompetencia);
        final Map<Equipo, Integer> puntos = new LinkedHashMap<Equipo, Integer>();
        for (InscripcionCompetencia i : competencia.getInscripciones()) {
            puntos.put(i.getEquipo(), 0);
        }
        for (Partido p : partidos) {
            if (p instanceof PartidoCompetencia && ((PartidoCompetencia) p).getCompetencia() == competencia
                    && !p.getMarcador().isEmpty()) {
                String[] partes = p.getMarcador().split("-");
                int ganados = competencia.calcularPuntos(Integer.parseInt(partes[0]), Integer.parseInt(partes[1]));
                Equipo equipo = ((PartidoCompetencia) p).getEquipo();
                puntos.put(equipo, puntos.get(equipo) + ganados);
            }
        }
        ArrayList<Map.Entry<Equipo, Integer>> orden = new ArrayList<Map.Entry<Equipo, Integer>>(puntos.entrySet());
        Collections.sort(orden, new Comparator<Map.Entry<Equipo, Integer>>() {
            public int compare(Map.Entry<Equipo, Integer> a, Map.Entry<Equipo, Integer> b) {
                return b.getValue() - a.getValue();
            }
        });
        Map<Equipo, Integer> tabla = new LinkedHashMap<Equipo, Integer>();
        for (Map.Entry<Equipo, Integer> e : orden) {
            tabla.put(e.getKey(), e.getValue());
        }
        return tabla;
    }

    // RF5 Entrenador
    public ArrayList<Estadistica> estadisticasDeEquipo(Usuario quien, String nombreEquipo) throws Exception {
        Equipo equipo = gestorEquipos.buscarEquipo(nombreEquipo);
        exigirEntrenadorDe(quien, equipo);
        ArrayList<Estadistica> resultado = new ArrayList<Estadistica>();
        for (InscripcionEquipo i : equipo.getPlantilla()) {
            resultado.addAll(i.getSocio().getEstadisticas());
        }
        return resultado;
    }

    // RF1 Socio
    public String proximaPosicion(Socio socio) throws Exception {
        PartidoCompetencia proximo = null;
        for (Partido p : partidos) {
            if (p instanceof PartidoCompetencia && p.getMarcador().isEmpty() && !p.getFecha().isBefore(LocalDate.now())
                    && p.getAlineados().contains(socio)
                    && (proximo == null || p.getFecha().isBefore(proximo.getFecha()))) {
                proximo = (PartidoCompetencia) p;
            }
        }
        if (proximo == null) {
            throw new Exception(socio.getLogin() + " no ha sido convocado a ningun proximo partido");
        }
        for (InscripcionEquipo i : proximo.getEquipo().getPlantilla()) {
            if (i.getSocio() == socio) {
                return i.getPosicion();
            }
        }
        throw new Exception(socio.getLogin() + " ya no pertenece al equipo del partido");
    }

    // RF3 Socio
    public PartidoPractica crearPartidoPractica(Usuario quien, String nombreDisciplina, String modalidad,
            LocalDate fecha, List<String> otros) throws Exception {
        if (!(quien instanceof Socio)) {
            throw new Exception("Solo un socio puede crear partidos de practica");
        }
        Socio creador = (Socio) quien;
        if (fecha.isBefore(LocalDate.now())) {
            throw new Exception("No se puede crear un partido en una fecha pasada");
        }
        DisciplinaIndividual disciplina = null;
        for (DisciplinaIndividual d : gestorReservas.getDisciplinas()) {
            if (Texto.iguales(d.getNombre(), nombreDisciplina)) {
                disciplina = d;
            }
        }
        if (disciplina == null) {
            throw new Exception("No existe la disciplina '" + nombreDisciplina + "'");
        }
        int esperados;
        if (Texto.iguales(modalidad, Instalacion.SENCILLOS)) {
            esperados = 1;
        } else if (Texto.iguales(modalidad, Instalacion.DOBLES)) {
            esperados = 3;
        } else {
            throw new Exception("Modalidad invalida: " + modalidad + " (Sencillos o Dobles)");
        }
        if (otros.size() != esperados) {
            throw new Exception("En " + modalidad + " se necesitan " + esperados + " participantes ademas del creador");
        }
        InscripcionDisciplina inscCreador = inscripcionDe(disciplina, creador);
        if (inscCreador == null) {
            throw new Exception(creador.getLogin() + " no esta inscrito en " + disciplina.getNombre());
        }
        ArrayList<Socio> jugadores = new ArrayList<Socio>();
        jugadores.add(creador);
        Entrenador rival = null;
        for (String login : otros) {
            Usuario u = usuarios.get(login);
            if (u instanceof Entrenador) {
                rival = (Entrenador) u;
            } else if (u instanceof Socio) {
                Socio otro = (Socio) u;
                InscripcionDisciplina insc = inscripcionDe(disciplina, otro);
                if (insc == null) {
                    throw new Exception(login + " no esta inscrito en " + disciplina.getNombre());
                }
                if (!inscCreador.nivelCompatible(insc)) {
                    throw new Exception("Los niveles de " + creador.getLogin() + " (" + inscCreador.getNivel() + ") y "
                            + login + " (" + insc.getNivel() + ") no son iguales ni adyacentes");
                }
                if (jugadores.contains(otro)) {
                    throw new Exception(login + " esta repetido en el partido");
                }
                jugadores.add(otro);
            } else {
                throw new Exception("No existe un usuario con login '" + login + "'");
            }
        }
        PartidoPractica partido = new PartidoPractica(fecha, modalidad, disciplina, jugadores);
        partido.setRival(rival);
        partidos.add(partido);
        repositorio.guardarPartidos(partidos);
        return partido;
    }

    // ---------------------------------------------------------------- ayudas

    private void exigirEntrenadorDe(Usuario quien, Equipo equipo) throws Exception {
        if (!(quien instanceof Entrenador) || !((Entrenador) quien).dirige(equipo)) {
            throw new Exception("Solo el entrenador del equipo " + equipo.getNombre() + " puede hacer esto");
        }
    }

    private Socio buscarSocio(String login) throws Exception {
        Usuario u = usuarios.get(login);
        if (!(u instanceof Socio)) {
            throw new Exception("No existe un socio con login '" + login + "'");
        }
        return (Socio) u;
    }

    private boolean estaInscrito(Equipo equipo, Competencia competencia) {
        for (InscripcionCompetencia i : competencia.getInscripciones()) {
            if (i.getEquipo() == equipo) {
                return true;
            }
        }
        return false;
    }

    private InscripcionDisciplina inscripcionDe(DisciplinaIndividual disciplina, Socio socio) {
        for (InscripcionDisciplina i : disciplina.getInscripciones()) {
            if (i.getSocio() == socio) {
                return i;
            }
        }
        return null;
    }
}
