package logica.usuarios;

import java.util.ArrayList;
import java.util.List;

import logica.tienda.Sugerencia;

public class Administrador extends Usuario {
    private List<SolicitudCambioTurno> solicitudesResueltas;
    private List<Sugerencia> sugerenciasAprobadas;

    public Administrador(String login, String password) {
        super(login, password);
        this.solicitudesResueltas = new ArrayList<SolicitudCambioTurno>();
        this.sugerenciasAprobadas = new ArrayList<Sugerencia>();
    }

    public List<SolicitudCambioTurno> getSolicitudesResueltas() {
        return this.solicitudesResueltas;
    }

    public void agregarSolicitudResuelta(SolicitudCambioTurno solicitud) {
        this.solicitudesResueltas.add(solicitud);
    }

    public List<Sugerencia> getSugerenciasAprobadas() {
        return this.sugerenciasAprobadas;
    }

    public void agregarSugerenciaAprobada(Sugerencia sugerencia) {
        this.sugerenciasAprobadas.add(sugerencia);
    }

    @Override
    public boolean puedeReservar() {
        return false;
    }

    @Override
    public boolean puedeRegistrarEquipo() {
        return true;
    }

    @Override
    public boolean puedeAprobarTurno() {
        return true;
    }
}
