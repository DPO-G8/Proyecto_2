package logica.usuarios;

import java.time.LocalDate;
import java.time.LocalTime;

public class SolicitudCambioTurno {
    private LocalDate fechaSolicitada;
    private String estado;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Turno turno;

    public SolicitudCambioTurno(Turno turno, LocalDate fechaSolicitada, LocalTime horaInicio, LocalTime horaFin) {
        this.turno = turno;
        this.fechaSolicitada = fechaSolicitada;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.estado = "PENDIENTE";
    }

    public LocalDate getFechaSolicitada() {
        return this.fechaSolicitada;
    }

    public String getEstado() {
        return this.estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalTime getHoraInicio() {
        return this.horaInicio;
    }

    public LocalTime getHoraFin() {
        return this.horaFin;
    }

    public Turno getTurno() {
        return this.turno;
    }
}
