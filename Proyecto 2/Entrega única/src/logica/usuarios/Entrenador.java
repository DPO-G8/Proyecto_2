package logica.usuarios;

import java.util.ArrayList;
import java.util.List;

import logica.deportes.Equipo;

public class Entrenador extends Empleado {
    private List<Equipo> equipos;

    public Entrenador(String login, String password, String codigoDescuento) {
        super(login, password, codigoDescuento);
        this.equipos = new ArrayList<Equipo>();
    }

    public List<Equipo> getEquipos() {
        return this.equipos;
    }

    public void agregarEquipo(Equipo equipo) {
        this.equipos.add(equipo);
    }

    public boolean dirige(Equipo equipo) {
        return this.equipos.contains(equipo);
    }
}
