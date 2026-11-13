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

	@Override
	public String getNombre() {
		return super.getNombre() + (esDobles() ? " Dobles" : " Sencillos");
	}

	public String getModalidad() {
		return modalidad;
	}

	public boolean esDobles() {
		return DOBLES.equals(modalidad);
	}

	@Override
	public boolean admiteEmpate() {
		return false;
	}
}
