package logica.usuarios;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import logica.deportes.Competencia;
import logica.deportes.Estadistica;
import logica.deportes.EstadisticaFutbol;
import logica.deportes.Partido;
import logica.deportes.PartidoCompetencia;

public class Socio extends Usuario {
    public static final double DESCUENTO_COMPARTIDO = 0.08;

    private int edad;
    private LocalDate fechaFinSuspension;
    private Empleado empleadoDescuento;
    private List<Estadistica> estadisticas;

    public Socio(String login, String password, int edad) {
        super(login, password);
        this.edad = edad;
        this.fechaFinSuspension = null;
        this.empleadoDescuento = null;
        this.estadisticas = new ArrayList<Estadistica>();
    }

    public int getEdad() {
        return this.edad;
    }

    public LocalDate getFechaFinSuspension() {
        return this.fechaFinSuspension;
    }

    public void setFechaFinSuspension(LocalDate fechaFinSuspension) {
        this.fechaFinSuspension = fechaFinSuspension;
    }

    public Empleado getEmpleadoDescuento() {
        return this.empleadoDescuento;
    }

    public void setEmpleadoDescuento(Empleado empleadoDescuento) {
        this.empleadoDescuento = empleadoDescuento;
    }

    public List<Estadistica> getEstadisticas() {
        return this.estadisticas;
    }

    public void agregarEstadistica(Estadistica estadistica) {
        this.estadisticas.add(estadistica);
    }

    @Override
    public boolean puedeReservar() {
        return true;
    }

    public boolean estaSuspendido() {
        return this.fechaFinSuspension != null && !LocalDate.now().isAfter(this.fechaFinSuspension);
    }

    public void actualizarSuspension(EstadisticaFutbol estadistica, LocalDate fechaFin) {
        Competencia competencia = competenciaDe(estadistica.getPartido());
        int amarillasAntes = 0;
        for (Estadistica e : this.estadisticas) {
            if (e != estadistica && e instanceof EstadisticaFutbol && competenciaDe(e.getPartido()) == competencia) {
                amarillasAntes += ((EstadisticaFutbol) e).getTarjetasAmarillas();
            }
        }
        int amarillasDespues = amarillasAntes + estadistica.getTarjetasAmarillas();
        if (estadistica.getTarjetasRojas() >= 1 || amarillasAntes / 2 < amarillasDespues / 2) {
            this.fechaFinSuspension = fechaFin;
        }
    }

    private Competencia competenciaDe(Partido partido) {
        if (partido instanceof PartidoCompetencia) {
            return ((PartidoCompetencia) partido).getCompetencia();
        }
        return null;
    }

    public boolean tieneDescuentoCompartido() {
        return this.empleadoDescuento != null;
    }

    @Override
    public double getPorcentajeDescuento() {
        if (tieneDescuentoCompartido()) {
            return DESCUENTO_COMPARTIDO;
        }
        return 0;
    }
}
