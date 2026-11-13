package logica.torneos;

import java.time.LocalDate;

public class TorneoIndividual extends Torneo {
	private String disciplina;
	private String modalidad;

	public TorneoIndividual(int id, String disciplina, String nivel, String modalidad, int cuposTotales, String tipo,
			LocalDate fecha, double tarifa) {
		super(id, nivel, cuposTotales, tipo, fecha, tarifa);
		this.disciplina = disciplina;
		this.modalidad = modalidad;
	}

	@Override
	public String getNombreDeporte() {
		return disciplina;
	}

	public boolean esDobles() {
		return DOBLES.equals(modalidad);
	}
}
