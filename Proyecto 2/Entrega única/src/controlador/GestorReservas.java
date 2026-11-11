package controlador;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Map;

import logica.deportes.DisciplinaIndividual;
import logica.deportes.InscripcionDisciplina;
import logica.deportes.Instalacion;
import logica.deportes.Reserva;
import logica.Texto;
import logica.usuarios.Socio;
import logica.usuarios.Usuario;
import persistencia.RepositorioDatos;

public class GestorReservas {

    public static final double PUNTAJE_INICIAL = 1000;

    private final RepositorioDatos repositorio;
    private final Map<String, Usuario> usuarios;
    private final ArrayList<DisciplinaIndividual> disciplinas;

    public GestorReservas(RepositorioDatos repositorio, Map<String, Usuario> usuarios) throws Exception {
        this.repositorio = repositorio;
        this.usuarios = usuarios;
        this.disciplinas = repositorio.cargarDisciplinas(usuarios);
    }

    public ArrayList<DisciplinaIndividual> getDisciplinas() {
        return disciplinas;
    }

    // RF4 Socio
    public Reserva reservar(Usuario usuario, int idInstalacion, LocalDate fecha, LocalTime horaInicio,
            LocalTime horaFin, int cantidadJugadores, String modalidad) throws Exception {

        if (!(usuario instanceof Socio) || !usuario.puedeReservar()) {
            throw new Exception("Este usuario no tiene permiso para reservar");
        }
        if (!horaFin.isAfter(horaInicio)) {
            throw new Exception("La hora de fin debe ser posterior a la hora de inicio");
        }
        if (fecha.isBefore(LocalDate.now())) {
            throw new Exception("No se puede reservar en una fecha pasada");
        }

        DisciplinaIndividual disciplina = buscarDisciplinaDe(idInstalacion);
        Instalacion instalacion = buscarInstalacion(disciplina, idInstalacion);

        if (!instalacion.cabe(cantidadJugadores, modalidad)) {
            throw new Exception("La instalacion '" + instalacion.getNombre() + "' no admite " + cantidadJugadores
                    + " jugadores en la modalidad " + modalidad);
        }
        if (estaOcupada(instalacion, fecha, horaInicio, horaFin)) {
            throw new Exception("La instalacion '" + instalacion.getNombre() + "' ya esta reservada en ese horario");
        }

        // R17: maximo de instalaciones de la disciplina usadas al mismo tiempo
        int enUso = 0;
        for (Instalacion otra : disciplina.getInstalaciones()) {
            if (otra != instalacion && estaOcupada(otra, fecha, horaInicio, horaFin)) {
                enUso++;
            }
        }
        if (enUso + 1 > disciplina.getMaxInstalacionesSimultaneas()) {
            throw new Exception("La disciplina " + disciplina.getNombre() + " ya usa el maximo de "
                    + disciplina.getMaxInstalacionesSimultaneas() + " instalaciones en ese horario");
        }

        Reserva reserva = new Reserva((Socio) usuario, fecha, horaInicio, horaFin, cantidadJugadores, modalidad);
        instalacion.agregarReserva(reserva);
        repositorio.guardarDisciplinas(disciplinas);
        return reserva;
    }

    // RF11 Socio
    public void inscribirEnDisciplina(Usuario quien, String nombreDisciplina, String nivel) throws Exception {
        if (!(quien instanceof Socio)) {
            throw new Exception("Solo un socio puede inscribirse en una academia");
        }
        if (!Texto.iguales(nivel, "Principiante") && !Texto.iguales(nivel, "Intermedio")
                && !Texto.iguales(nivel, "Avanzado")) {
            throw new Exception("Nivel invalido: " + nivel + " (Principiante, Intermedio o Avanzado)");
        }
        DisciplinaIndividual disciplina = null;
        for (DisciplinaIndividual d : disciplinas) {
            if (Texto.iguales(d.getNombre(), nombreDisciplina)) {
                disciplina = d;
            }
        }
        if (disciplina == null) {
            throw new Exception("No existe la disciplina '" + nombreDisciplina + "'");
        }
        for (InscripcionDisciplina i : disciplina.getInscripciones()) {
            if (i.getSocio() == quien) {
                throw new Exception("Ya esta inscrito en " + disciplina.getNombre());
            }
        }
        disciplina.agregarInscripcion(new InscripcionDisciplina((Socio) quien, nivel, PUNTAJE_INICIAL));
        repositorio.guardarDisciplinas(disciplinas);
    }

    // Dato inicial
    public void agregarDisciplina(DisciplinaIndividual disciplina) throws Exception {
        disciplinas.add(disciplina);
        repositorio.guardarDisciplinas(disciplinas);
    }

    // Busca una instalacion por id
    public Instalacion getInstalacion(int idInstalacion) throws Exception {
        return buscarInstalacion(buscarDisciplinaDe(idInstalacion), idInstalacion);
    }

    // RF5
    public boolean instalacionRestringida(int idInstalacion) throws Exception {
        DisciplinaIndividual disciplina = buscarDisciplinaDe(idInstalacion);
        return prohibeBebidasCalientes(disciplina, buscarInstalacion(disciplina, idInstalacion));
    }

    // RF5
    public Reserva reservaRestringidaCercana(Socio socio, LocalDateTime momento, int minutos) {
        LocalDateTime limite = momento.plusMinutes(minutos);
        for (DisciplinaIndividual d : disciplinas) {
            for (Instalacion i : d.getInstalaciones()) {
                if (!prohibeBebidasCalientes(d, i)) {
                    continue;
                }
                for (Reserva r : i.getReservas()) {
                    if (!r.getSocio().getLogin().equals(socio.getLogin())) {
                        continue;
                    }
                    LocalDateTime inicio = LocalDateTime.of(r.getFecha(), r.getHoraInicio());
                    LocalDateTime fin = LocalDateTime.of(r.getFecha(), r.getHoraFin());
                    if (inicio.isBefore(limite) && fin.isAfter(momento)) {
                        return r;
                    }
                }
            }
        }
        return null;
    }

    private boolean prohibeBebidasCalientes(DisciplinaIndividual disciplina, Instalacion instalacion) {
        boolean padel = Texto.iguales(disciplina.getNombre(), "Padel");
        boolean mesaTechada = Texto.iguales(disciplina.getNombre(), "Tenis de mesa") && instalacion.isTechada();
        return padel || mesaTechada;
    }

    // ---------------------------------------------------------------- ayudas

    private DisciplinaIndividual buscarDisciplinaDe(int idInstalacion) throws Exception {
        for (DisciplinaIndividual d : disciplinas) {
            for (Instalacion i : d.getInstalaciones()) {
                if (i.getId() == idInstalacion) {
                    return d;
                }
            }
        }
        throw new Exception("No existe una instalacion con id " + idInstalacion);
    }

    private Instalacion buscarInstalacion(DisciplinaIndividual disciplina, int idInstalacion) throws Exception {
        for (Instalacion i : disciplina.getInstalaciones()) {
            if (i.getId() == idInstalacion) {
                return i;
            }
        }
        throw new Exception("No existe una instalacion con id " + idInstalacion);
    }

    // Se cruzan si una empieza antes de que termine la otra
    private boolean estaOcupada(Instalacion instalacion, LocalDate fecha, LocalTime inicio, LocalTime fin) {
        for (Reserva r : instalacion.getReservas()) {
            if (r.getFecha().equals(fecha) && inicio.isBefore(r.getHoraFin()) && fin.isAfter(r.getHoraInicio())) {
                return true;
            }
        }
        return false;
    }
}
