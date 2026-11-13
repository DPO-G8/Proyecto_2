package logica.usuarios;

public class Bono {
	private double porcentaje;
	private boolean usado;

	public Bono(double porcentaje) {
		this.porcentaje = porcentaje;
		this.usado = false;
	}

	public double getPorcentaje() {
		return porcentaje;
	}

	public boolean isUsado() {
		return usado;
	}

	public void marcarUsado() {
		this.usado = true;
	}
}
