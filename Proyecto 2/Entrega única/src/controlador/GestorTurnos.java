package controlador;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Map;

import logica.Texto;
import logica.usuarios.Administrador;
import logica.usuarios.Empleado;
import logica.usuarios.Entrenador;
import logica.usuarios.Fisioterapeuta;
import logica.usuarios.SolicitudCambioTurno;
import logica.usuarios.Turno;
import logica.usuarios.Usuario;
import persistencia.RepositorioDatos;

public class GestorTurnos {

    public static final String PENDIENTE = "PENDIENTE";
    public static final String APROBADA = "APROBADA";
    public static final String RECHAZADA = "RECHAZADA";
    // Cobertura minima por franja
    public static final int MIN_ENTRENADORES = 2;
    public static final int MIN_FISIOTERAPEUTAS = 1;

    private final RepositorioDatos repositorio;
    private final Map<String, Usuario> usuarios;

    public GestorTurnos(RepositorioDatos repositorio, Map<String, Usuario> usuarios) throws Exception {
        this.repositorio = repositorio;
        this.usuarios = usuarios;
        repositorio.cargarTurnos(usuarios);
    }

    // RF8 Administrador
    public Turno asignarTurno(Usuario quien, String loginEmpleado, String dia, LocalTime inicio, LocalTime fin)
            throws Exception {
        exigirAdministrador(quien);
        Usuario u = usuarios.get(loginEmpleado);
        if (!(u instanceof Empleado)) {
            throw new Exception("No existe un empleado con login '" + loginEmpleado + "'");
        }
        if (!fin.isAfter(inicio)) {
            throw new Exception("La hora de fin debe ser posterior a la de inicio");
        }
        Empleado empleado = (Empleado) u;
        for (Turno t : empleado.getTurnos()) {
            if (Texto.iguales(t.getDia(), dia) && inicio.isBefore(t.getHoraFin()) && fin.isAfter(t.getHoraInicio())) {
                throw new Exception(loginEmpleado + " ya tiene un turno que se cruza ese " + dia);
            }
        }
        Turno turno = new Turno(dia, inicio, fin);
        empleado.agregarTurno(turno);
        repositorio.guardarTurnos(usuarios.values());
        return turno;
    }

    // RF1 Empleado
    public SolicitudCambioTurno solicitarCambio(Usuario quien, int indiceTurno, LocalDate fecha,
            LocalTime inicio, LocalTime fin) throws Exception {
        if (!(quien instanceof Empleado)) {
            throw new Exception("Solo un empleado puede solicitar cambios de turno");
        }
        Empleado empleado = (Empleado) quien;
        if (indiceTurno < 0 || indiceTurno >= empleado.getTurnos().size()) {
            throw new Exception("El empleado no tiene un turno en la posicion " + indiceTurno);
        }
        if (fecha.isBefore(LocalDate.now())) {
            throw new Exception("La fecha solicitada ya paso");
        }
        if (!fin.isAfter(inicio)) {
            throw new Exception("La hora de fin debe ser posterior a la de inicio");
        }
        SolicitudCambioTurno solicitud = new SolicitudCambioTurno(empleado.getTurnos().get(indiceTurno), fecha,
                inicio, fin);
        empleado.agregarSolicitud(solicitud);
        repositorio.guardarTurnos(usuarios.values());
        return solicitud;
    }

    public ArrayList<SolicitudCambioTurno> pendientes() {
        ArrayList<SolicitudCambioTurno> resultado = new ArrayList<SolicitudCambioTurno>();
        for (Usuario u : usuarios.values()) {
            if (u instanceof Empleado) {
                for (SolicitudCambioTurno s : ((Empleado) u).getSolicitudes()) {
                    if (s.getEstado().equals(PENDIENTE)) {
                        resultado.add(s);
                    }
                }
            }
        }
        return resultado;
    }

    // RF1 Administrador
    public void resolver(Usuario quien, SolicitudCambioTurno solicitud, boolean aprobar) throws Exception {
        exigirAdministrador(quien);
        if (!solicitud.getEstado().equals(PENDIENTE)) {
            throw new Exception("La solicitud ya esta " + solicitud.getEstado());
        }
        Empleado dueno = null;
        for (Usuario u : usuarios.values()) {
            if (u instanceof Empleado && ((Empleado) u).getSolicitudes().contains(solicitud)) {
                dueno = (Empleado) u;
            }
        }
        String motivo = null;
        if (aprobar && dueno != null) {
            motivo = coberturaInsuficiente(dueno, solicitud.getTurno());
        }
        if (aprobar && motivo == null) {
            solicitud.setEstado(APROBADA);
        } else {
            solicitud.setEstado(RECHAZADA);
        }
        ((Administrador) quien).agregarSolicitudResuelta(solicitud);
        repositorio.guardarTurnos(usuarios.values());
        if (motivo != null) {
            throw new Exception("Solicitud rechazada automaticamente: " + motivo);
        }
    }

    private String coberturaInsuficiente(Empleado dueno, Turno turno) {
        int entrenadores = 0;
        int fisios = 0;
        for (Usuario u : usuarios.values()) {
            if (u == dueno || !(u instanceof Empleado)) {
                continue;
            }
            for (Turno t : ((Empleado) u).getTurnos()) {
                if (Texto.iguales(t.getDia(), turno.getDia()) && turno.getHoraInicio().isBefore(t.getHoraFin())
                        && turno.getHoraFin().isAfter(t.getHoraInicio())) {
                    if (u instanceof Entrenador) {
                        entrenadores++;
                    } else if (u instanceof Fisioterapeuta) {
                        fisios++;
                    }
                }
            }
        }
        if (dueno instanceof Entrenador && entrenadores < MIN_ENTRENADORES) {
            return "la franja quedaria con " + entrenadores + " entrenadores (minimo " + MIN_ENTRENADORES + ")";
        }
        if (dueno instanceof Fisioterapeuta && fisios < MIN_FISIOTERAPEUTAS) {
            return "la franja quedaria sin fisioterapeuta (minimo " + MIN_FISIOTERAPEUTAS + ")";
        }
        return null;
    }

    private void exigirAdministrador(Usuario quien) throws Exception {
        if (quien == null || !quien.puedeAprobarTurno()) {
            throw new Exception("Solo un administrador puede hacer esta operacion");
        }
    }
}
