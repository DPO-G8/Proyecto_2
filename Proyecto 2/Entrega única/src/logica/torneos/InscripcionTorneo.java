package logica.torneos;
import java.util.List;
import logica.usuarios.Socio;

import java.util.ArrayList;

public class InscripcionTorneo {
	private List<Socio> socios;
	private PersonaExterna externo;
	private boolean preferencial;
	private double tarifaPagada;
	private int puntos;
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

	public boolean incluye(String loginODocumento) {
		if (externo != null) {
			return externo.getDocumento().equals(loginODocumento);
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

	public double getPremio() {
		return premio;
	}

	public void setPremio(double premio) {
		this.premio = premio;
	}
	public List<Socio> getJugadores(){
		return socios;
	}
}
