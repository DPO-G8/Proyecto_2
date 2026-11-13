package logica.torneos;

import java.time.LocalDate;

import logica.Texto;

public class TorneoConjunto extends Torneo {
	private String deporte;

	public TorneoConjunto(int id, String deporte, String categoria, int cuposTotales, String tipo, LocalDate fecha,
			double tarifa) {
		super(id, categoria, cuposTotales, tipo, fecha, tarifa);
		this.deporte = deporte;
	}

	@Override
	public String getNombreDeporte() {
		return deporte;
	}

	@Override
	public boolean admiteEmpate() {
		return Texto.iguales(deporte, "Futbol");
	}
}
