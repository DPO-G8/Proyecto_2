package logica.torneos;
import java.util.List;

public class Torneo {
	public static final String RECREATIVO = "RECREATIVO";
	public static final String SENCILLOS = "SENCILLOS";
	public static final String DOBLES = "DOBLES";
	public static final String COMPETITIVO = "COMPETITIVO";
	public static final String ABIERTO = "ABIERTO";
	public static final String EN_CURSO = "EN_CURSO";
	public static final String FINALIZADO = "FINALIZADO";
	
	public int getCuposTotales() {
		throw new UnsupportedOperationException();
	}
	
	public int getCuposPreferenciales() {
		throw new UnsupportedOperationException();
	}
	
	public int getCuposRegulares() {
		throw new UnsupportedOperationException();
	}
	public String getEstado() {
		throw new UnsupportedOperationException();
	}
	public double getTarifa() {
		throw new UnsupportedOperationException();
	}
	public int cuposRegularesLibres() {
		throw new UnsupportedOperationException();
	}

	public double totalRecaudado() {
		throw new UnsupportedOperationException();
	}
	public List<Encuentro> getEncuentros() {
		throw new UnsupportedOperationException();
	}
	public List<InscripcionTorneo> getClasificacion() {
		throw new UnsupportedOperationException();
	}
	public int cuposOcupados() {
		throw new UnsupportedOperationException();
	}
}
