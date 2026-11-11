package logica.deportes;

import java.util.ArrayList;
import java.util.List;

public class DeporteConjunto {
    private String nombre;
    private int minimoJugadoresHabilitados;
    private List<Equipo> equipos;

    public DeporteConjunto(String nombre, int minimoJugadoresHabilitados) {
        this.nombre = nombre;
        this.minimoJugadoresHabilitados = minimoJugadoresHabilitados;
        this.equipos = new ArrayList<Equipo>();
    }

    public String getNombre() {
        return this.nombre;
    }

    public int getMinimoJugadores() {
        return this.minimoJugadoresHabilitados;
    }

    public List<Equipo> getEquipos() {
        return this.equipos;
    }

    public void agregarEquipo(Equipo equipo) {
        this.equipos.add(equipo);
    }
}
