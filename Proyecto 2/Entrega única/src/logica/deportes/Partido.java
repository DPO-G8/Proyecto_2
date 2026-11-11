package logica.deportes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import logica.usuarios.Socio;

public abstract class Partido {
    private LocalDate fecha;
    private String marcador;
    private List<Socio> alineados;
    private List<Estadistica> estadisticas;

    public Partido(LocalDate fecha) {
        this.fecha = fecha;
        this.marcador = "";
        this.alineados = new ArrayList<Socio>();
        this.estadisticas = new ArrayList<Estadistica>();
    }

    public LocalDate getFecha() {
        return this.fecha;
    }

    public String getMarcador() {
        return this.marcador;
    }

    public void setMarcador(String marcador) {
        this.marcador = marcador;
    }

    public List<Socio> getAlineados() {
        return this.alineados;
    }

    public List<Estadistica> getEstadisticas() {
        return this.estadisticas;
    }

    public void agregarEstadistica(Estadistica estadistica) {
        this.estadisticas.add(estadistica);
    }

    public void registroAlineacion(List<Socio> jugadores) throws Exception {
        if (jugadores.isEmpty()) {
            throw new Exception("No se puede registrar una alineacion sin jugadores");
        }
        this.alineados = new ArrayList<Socio>(jugadores);
    }

    public boolean puedeAlinear(Socio socio) {
        return true;
    }
}
