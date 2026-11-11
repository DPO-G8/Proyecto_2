package logica.usuarios;

import java.time.LocalTime;

public class Turno {
    private String dia;
    private LocalTime horaInicio;
    private LocalTime horaFin;

    public Turno(String dia, LocalTime horaInicio, LocalTime horaFin) {
        this.dia = dia;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    public String getDia() {
        return this.dia;
    }

    public LocalTime getHoraInicio() {
        return this.horaInicio;
    }

    public LocalTime getHoraFin() {
        return this.horaFin;
    }
}
