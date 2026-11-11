package logica.usuarios;

import java.util.ArrayList;
import java.util.List;

import logica.tienda.Sugerencia;

public class Empleado extends Usuario {
    public static final double DESCUENTO_EMPLEADO = 0.15;

    private String codigoDescuento;
    private List<Turno> turnos;
    private List<SolicitudCambioTurno> solicitudes;
    private List<Sugerencia> sugerencias;

    public Empleado(String login, String password, String codigoDescuento) {
        super(login, password);
        this.codigoDescuento = codigoDescuento;
        this.turnos = new ArrayList<Turno>();
        this.solicitudes = new ArrayList<SolicitudCambioTurno>();
        this.sugerencias = new ArrayList<Sugerencia>();
    }

    public String getCodigoDescuento() {
        return this.codigoDescuento;
    }

    public List<Turno> getTurnos() {
        return this.turnos;
    }

    public void agregarTurno(Turno turno) {
        this.turnos.add(turno);
    }

    public List<SolicitudCambioTurno> getSolicitudes() {
        return this.solicitudes;
    }

    public void agregarSolicitud(SolicitudCambioTurno solicitud) {
        this.solicitudes.add(solicitud);
    }

    public List<Sugerencia> getSugerencias() {
        return this.sugerencias;
    }

    public void agregarSugerencia(Sugerencia sugerencia) {
        this.sugerencias.add(sugerencia);
    }

    @Override
    public double getPorcentajeDescuento() {
        return DESCUENTO_EMPLEADO;
    }
}
