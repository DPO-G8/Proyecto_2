package logica.deportes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import logica.Texto;
import logica.usuarios.Entrenador;
import logica.usuarios.Socio;

public class SesionEntrenamiento {
    private LocalDate fecha;
    private LocalTime hora;
    private int duracion;
    private String tipo;
    private Entrenador entrenador;
    private Instalacion instalacion;
    private Equipo equipo;
    private List<Asistencia> asistencias;

    public SesionEntrenamiento(LocalDate fecha, LocalTime hora, int duracion, String tipo, Entrenador entrenador,
            Instalacion instalacion) {
        this.fecha = fecha;
        this.hora = hora;
        this.duracion = duracion;
        this.tipo = tipo;
        this.entrenador = entrenador;
        this.instalacion = instalacion;
        this.equipo = null;
        this.asistencias = new ArrayList<Asistencia>();
    }

    public LocalDate getFecha() {
        return this.fecha;
    }

    public LocalTime getHora() {
        return this.hora;
    }

    public int getDuracion() {
        return this.duracion;
    }

    public String getTipo() {
        return this.tipo;
    }

    public Entrenador getEntrenador() {
        return this.entrenador;
    }

    public Instalacion getInstalacion() {
        return this.instalacion;
    }

    public Equipo getEquipo() {
        return this.equipo;
    }

    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
    }

    public List<Asistencia> getAsistencias() {
        return this.asistencias;
    }

    public void registrarAsistencia(Socio socio, String estado, int nota, String comentario) throws Exception {
        String estadoValido;
        if (Texto.iguales(estado, Asistencia.ASISTIO)) {
            estadoValido = Asistencia.ASISTIO;
        } else if (Texto.iguales(estado, Asistencia.FALTO)) {
            estadoValido = Asistencia.FALTO;
        } else if (Texto.iguales(estado, Asistencia.LLEGO_TARDE)) {
            estadoValido = Asistencia.LLEGO_TARDE;
        } else {
            throw new Exception("Estado de asistencia invalido: " + estado + " (ASISTIO, FALTO o LLEGO_TARDE)");
        }
        Asistencia asistencia = new Asistencia(socio, estadoValido);
        if (nota != 0) {
            asistencia.setNotaDesempeno(nota);
        }
        asistencia.setComentario(comentario);
        this.asistencias.add(asistencia);
    }
}
