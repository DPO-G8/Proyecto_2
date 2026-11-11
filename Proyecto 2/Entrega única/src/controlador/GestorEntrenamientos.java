package controlador;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Map;

import logica.deportes.Asistencia;
import logica.deportes.Equipo;
import logica.deportes.InscripcionEquipo;
import logica.deportes.Instalacion;
import logica.deportes.Reserva;
import logica.deportes.SesionEntrenamiento;
import logica.usuarios.Entrenador;
import logica.usuarios.Socio;
import logica.usuarios.Usuario;
import persistencia.RepositorioDatos;

public class GestorEntrenamientos {

    private final RepositorioDatos repositorio;
    private final Map<String, Usuario> usuarios;
    private final GestorEquipos gestorEquipos;
    private final GestorReservas gestorReservas;
    private final ArrayList<SesionEntrenamiento> sesiones;

    public GestorEntrenamientos(RepositorioDatos repositorio, Map<String, Usuario> usuarios,
            GestorEquipos gestorEquipos, GestorReservas gestorReservas) throws Exception {
        this.repositorio = repositorio;
        this.usuarios = usuarios;
        this.gestorEquipos = gestorEquipos;
        this.gestorReservas = gestorReservas;
        this.sesiones = repositorio.cargarSesiones(usuarios, gestorEquipos.getDeportes(),
                gestorReservas.getDisciplinas());
    }

    public ArrayList<SesionEntrenamiento> getSesiones() {
        return sesiones;
    }

    // RF1 Entrenador
    public SesionEntrenamiento crearSesion(Usuario quien, LocalDate fecha, LocalTime hora, int duracionMinutos,
            String tipo, int idInstalacion, String nombreEquipo) throws Exception {
        if (!(quien instanceof Entrenador)) {
            throw new Exception("Solo un entrenador puede crear sesiones de entrenamiento");
        }
        Equipo equipo = gestorEquipos.buscarEquipo(nombreEquipo);
        if (!((Entrenador) quien).dirige(equipo)) {
            throw new Exception("El entrenador no dirige el equipo " + nombreEquipo);
        }
        if (duracionMinutos <= 0) {
            throw new Exception("La duracion debe ser positiva");
        }
        if (fecha.isBefore(LocalDate.now())) {
            throw new Exception("No se puede crear una sesion en una fecha pasada");
        }
        LocalTime fin = hora.plusMinutes(duracionMinutos);
        if (!fin.isAfter(hora)) {
            throw new Exception("La sesion debe terminar el mismo dia en que empieza");
        }
        Instalacion instalacion = gestorReservas.getInstalacion(idInstalacion);
        for (Reserva r : instalacion.getReservas()) {
            if (r.getFecha().equals(fecha) && hora.isBefore(r.getHoraFin()) && fin.isAfter(r.getHoraInicio())) {
                throw new Exception("La instalacion '" + instalacion.getNombre() + "' esta reservada en ese horario");
            }
        }
        for (SesionEntrenamiento s : sesiones) {
            LocalTime finOtra = s.getHora().plusMinutes(s.getDuracion());
            if (s.getInstalacion() == instalacion && s.getFecha().equals(fecha) && hora.isBefore(finOtra)
                    && fin.isAfter(s.getHora())) {
                throw new Exception("La instalacion '" + instalacion.getNombre() + "' ya tiene otra sesion en ese horario");
            }
        }
        SesionEntrenamiento sesion = new SesionEntrenamiento(fecha, hora, duracionMinutos, tipo,
                (Entrenador) quien, instalacion);
        sesion.setEquipo(equipo);
        sesiones.add(sesion);
        repositorio.guardarSesiones(sesiones);
        return sesion;
    }

    // RF2 Entrenador
    public void registrarAsistencia(Usuario quien, SesionEntrenamiento sesion, String loginSocio, String estado,
            int nota, String comentario) throws Exception {
        if (sesion.getEntrenador() != quien) {
            throw new Exception("Solo el entrenador de la sesion puede registrar asistencias");
        }
        Usuario u = usuarios.get(loginSocio);
        if (!(u instanceof Socio)) {
            throw new Exception("No existe un socio con login '" + loginSocio + "'");
        }
        boolean pertenece = false;
        for (InscripcionEquipo i : sesion.getEquipo().getPlantilla()) {
            if (i.getSocio() == u) {
                pertenece = true;
            }
        }
        if (!pertenece) {
            throw new Exception(loginSocio + " no pertenece al equipo " + sesion.getEquipo().getNombre());
        }
        sesion.registrarAsistencia((Socio) u, estado, nota, comentario);
        repositorio.guardarSesiones(sesiones);
    }

    // RF12 Socio
    public ArrayList<Asistencia> asistenciasDe(Socio socio) {
        ArrayList<Asistencia> resultado = new ArrayList<Asistencia>();
        for (SesionEntrenamiento s : sesiones) {
            for (Asistencia a : s.getAsistencias()) {
                if (a.getSocio() == socio) {
                    resultado.add(a);
                }
            }
        }
        return resultado;
    }
}
