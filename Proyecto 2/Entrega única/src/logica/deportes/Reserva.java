package logica.deportes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import logica.usuarios.Socio;

public class Reserva {
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private int cantidadJugadores;
    private String modalidad;
    private Socio socio;
    private List<Socio> invitados;

    public Reserva(Socio socio, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin, int cantidadJugadores,
            String modalidad) {
        this.socio = socio;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.cantidadJugadores = cantidadJugadores;
        this.modalidad = modalidad;
        this.invitados = new ArrayList<Socio>();
    }

    public LocalDate getFecha() {
        return this.fecha;
    }

    public LocalTime getHoraInicio() {
        return this.horaInicio;
    }

    public LocalTime getHoraFin() {
        return this.horaFin;
    }

    public int getCantidadJugadores() {
        return this.cantidadJugadores;
    }

    public String getModalidad() {
        return this.modalidad;
    }

    public Socio getSocio() {
        return this.socio;
    }

    public List<Socio> getInvitados() {
        return this.invitados;
    }

    public void agregarInvitado(Socio invitado) {
        this.invitados.add(invitado);
    }
}
