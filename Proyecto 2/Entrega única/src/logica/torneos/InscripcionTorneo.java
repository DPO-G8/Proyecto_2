package logica.torneos;

import java.util.ArrayList;
import java.util.List;

import logica.deportes.Equipo;
import logica.deportes.InscripcionEquipo;
import logica.usuarios.Empleado;
import logica.usuarios.Socio;

public class InscripcionTorneo {
	private List<Socio> socios;
	private PersonaExterna externo;
	private Equipo equipo;
	private Empleado empleado;
	private boolean preferencial;
	private double tarifaPagada;
	private int puntos;
	private int aFavor;
	private int enContra;
	private double premio;

	public InscripcionTorneo(Socio socio, Socio pareja, boolean preferencial, double tarifaPagada) {
		this.socios = new ArrayList<Socio>();
		this.socios.add(socio);
		if (pareja != null) {
			this.socios.add(pareja);
		}
		this.externo = null;
		this.preferencial = preferencial;
		this.tarifaPagada = tarifaPagada;
	}

	public InscripcionTorneo(PersonaExterna externo, boolean preferencial, double tarifaPagada) {
		this.socios = new ArrayList<Socio>();
		this.externo = externo;
		this.preferencial = preferencial;
		this.tarifaPagada = tarifaPagada;
	}

	public InscripcionTorneo(Equipo equipo, boolean preferencial, double tarifaPagada) {
		this.socios = new ArrayList<Socio>();
		this.equipo = equipo;
		this.preferencial = preferencial;
		this.tarifaPagada = tarifaPagada;
	}

	public InscripcionTorneo(Empleado empleado, boolean preferencial) {
		this.socios = new ArrayList<Socio>();
		this.empleado = empleado;
		this.preferencial = preferencial;
		this.tarifaPagada = 0;
	}

	public PersonaExterna getExterno() {
		return externo;
	}

	public Equipo getEquipo() {
		return equipo;
	}

	public Empleado getEmpleado() {
		return empleado;
	}

	public String getNombre() {
		if (externo != null) {
			return externo.getNombre() + " (" + externo.getTipo().toLowerCase() + ")";
		}
		if (equipo != null) {
			return equipo.getNombre();
		}
		if (empleado != null) {
			return empleado.getLogin();
		}
		String nombre = socios.get(0).getLogin();
		if (socios.size() > 1) {
			nombre += " / " + socios.get(1).getLogin();
		}
		return nombre;
	}

	public boolean esEmpleado() {
		return empleado != null;
	}

	public List<Socio> getJugadores() {
		if (equipo == null) {
			return socios;
		}
		List<Socio> plantilla = new ArrayList<Socio>();
		for (InscripcionEquipo i : equipo.getPlantilla()) {
			plantilla.add(i.getSocio());
		}
		return plantilla;
	}

	public boolean incluye(String loginODocumento) {
		if (externo != null) {
			return externo.getDocumento().equals(loginODocumento);
		}
		if (equipo != null) {
			return equipo.getNombre().equals(loginODocumento);
		}
		if (empleado != null) {
			return empleado.getLogin().equals(loginODocumento);
		}
		for (Socio s : socios) {
			if (s.getLogin().equals(loginODocumento)) {
				return true;
			}
		}
		return false;
	}

	public boolean isPreferencial() {
		return preferencial;
	}

	public double getTarifaPagada() {
		return tarifaPagada;
	}

	public int getPuntos() {
		return puntos;
	}

	public void sumarPuntos(int puntos) {
		this.puntos += puntos;
	}

	public int getAFavor() {
		return aFavor;
	}

	public int getEnContra() {
		return enContra;
	}

	public int getDiferencia() {
		return aFavor - enContra;
	}

	public void sumarMarcador(int aFavor, int enContra) {
		this.aFavor += aFavor;
		this.enContra += enContra;
	}

	public double getPremio() {
		return premio;
	}

	public void setPremio(double premio) {
		this.premio = premio;
	}
}
