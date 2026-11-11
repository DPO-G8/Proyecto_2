package logica.deportes;

import java.util.ArrayList;
import java.util.List;

import logica.Texto;

public class Instalacion {
    public static final String SENCILLOS = "Sencillos";
    public static final String DOBLES = "Dobles";

    private int id;
    private String nombre;
    private int capacidad;
    private boolean techada;
    private String tipoCancha;
    private List<Reserva> reservas;

    public Instalacion(int id, String nombre, int capacidad, boolean techada, String tipoCancha) {
        this.id = id;
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.techada = techada;
        this.tipoCancha = tipoCancha;
        this.reservas = new ArrayList<Reserva>();
    }

    public String getNombre() {
        return this.nombre;
    }

    public int getCapacidad() {
        return this.capacidad;
    }

    public boolean isTechada() {
        return this.techada;
    }
    

    public int getId() {
		return id;
	}

	public String getTipoCancha() {
        return this.tipoCancha;
    }

    public List<Reserva> getReservas() {
        return this.reservas;
    }

    public void agregarReserva(Reserva reserva) {
        this.reservas.add(reserva);
    }

    public boolean cabe(int cantidadJugadores, String modalidad) {
        if (cantidadJugadores > this.capacidad) {
            return false;
        }
        if (Texto.iguales(this.tipoCancha, "Padel") && !Texto.iguales(modalidad, DOBLES)) {
            return false;
        }
        if (Texto.iguales(modalidad, SENCILLOS)) {
            return cantidadJugadores == 2;
        }
        if (Texto.iguales(modalidad, DOBLES)) {
            return cantidadJugadores == 4;
        }
        return false;
    }
}
