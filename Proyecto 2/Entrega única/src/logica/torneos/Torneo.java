package logica.torneos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class Torneo {
	public static final String RECREATIVO = "RECREATIVO";
	public static final String SENCILLOS = "SENCILLOS";
	public static final String DOBLES = "DOBLES";
	public static final String COMPETITIVO = "COMPETITIVO";
	public static final String ABIERTO = "ABIERTO";
	public static final String EN_CURSO = "EN_CURSO";
	public static final String FINALIZADO = "FINALIZADO";

	private int id;
	private String categoriaONivel;
	private int cuposTotales;
	private int cuposPreferenciales;
	private String tipo;
	private LocalDate fecha;
	private double tarifa;
	private String estado;
	private List<InscripcionTorneo> participantes;
	private List<Encuentro> encuentros;

	public Torneo(int id, String categoriaONivel, int cuposTotales, String tipo, LocalDate fecha, double tarifa) {
		this.id = id;
		this.categoriaONivel = categoriaONivel;
		this.cuposTotales = cuposTotales;
		this.cuposPreferenciales = (cuposTotales + 4) / 5;
		this.tipo = tipo;
		this.fecha = fecha;
		this.tarifa = tarifa;
		this.estado = ABIERTO;
		this.participantes = new ArrayList<InscripcionTorneo>();
		this.encuentros = new ArrayList<Encuentro>();
	}

	public int getId() {
		return id;
	}

	public String getCategoriaONivel() {
		return categoriaONivel;
	}

	public double getTarifa() {
		return tarifa;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public boolean esCompetitivo() {
		return COMPETITIVO.equals(tipo);
	}

	public abstract String getNombreDeporte();

	public int getCuposTotales() {
		return cuposTotales;
	}

	public int getCuposPreferenciales() {
		return cuposPreferenciales;
	}

	public int getCuposRegulares() {
		return cuposTotales - cuposPreferenciales;
	}

	public int cuposOcupados() {
		return participantes.size();
	}

	public int cuposPreferencialesLibres() {
		int ocupados = 0;
		for (InscripcionTorneo i : participantes) {
			if (i.isPreferencial()) {
				ocupados++;
			}
		}
		return cuposPreferenciales - ocupados;
	}

	public int cuposRegularesLibres() {
		int ocupados = 0;
		for (InscripcionTorneo i : participantes) {
			if (!i.isPreferencial()) {
				ocupados++;
			}
		}
		return getCuposRegulares() - ocupados;
	}

		public boolean asignarCupo(boolean entreLosMejores) throws Exception {
		if (entreLosMejores && cuposPreferencialesLibres() > 0) {
			return true;
		}
		if (cuposRegularesLibres() > 0) {
			return false;
		}
		if (cuposPreferencialesLibres() > 0) {
			return true;
		}
		throw new Exception("Rechazado: no hay cupos disponibles en el torneo");
	}

	public List<InscripcionTorneo> getParticipantes() {
		return participantes;
	}

	public void agregarParticipante(InscripcionTorneo inscripcion) {
		participantes.add(inscripcion);
	}

	public void quitarParticipante(InscripcionTorneo inscripcion) {
		participantes.remove(inscripcion);
	}

	public InscripcionTorneo buscarParticipante(String loginODocumento) {
		for (InscripcionTorneo i : participantes) {
			if (i.incluye(loginODocumento)) {
				return i;
			}
		}
		return null;
	}

	public double totalRecaudado() {
		double total = 0;
		for (InscripcionTorneo i : participantes) {
			total += i.getTarifaPagada();
		}
		return total;
	}

	public List<Encuentro> getEncuentros() {
		return encuentros;
	}

	public void generarEncuentros() {
		for (int a = 0; a < participantes.size(); a++) {
			for (int b = a + 1; b < participantes.size(); b++) {
				encuentros.add(new Encuentro(encuentros.size() + 1, participantes.get(a), participantes.get(b)));
			}
		}
	}

	public List<Encuentro> getEncuentrosJugados() {
		return filtrarEncuentros(true);
	}

	public List<Encuentro> getEncuentrosPendientes() {
		return filtrarEncuentros(false);
	}

	private List<Encuentro> filtrarEncuentros(boolean jugados) {
		List<Encuentro> lista = new ArrayList<Encuentro>();
		for (Encuentro e : encuentros) {
			if (e.isJugado() == jugados) {
				lista.add(e);
			}
		}
		return lista;
	}

	public List<InscripcionTorneo> getPosiciones() {
		List<InscripcionTorneo> tabla = new ArrayList<InscripcionTorneo>(participantes);
		Collections.sort(tabla, new Comparator<InscripcionTorneo>() {
			public int compare(InscripcionTorneo x, InscripcionTorneo y) {
				return y.getPuntos() - x.getPuntos();
			}
		});
		return tabla;
	}

	public List<InscripcionTorneo> getClasificacion() {
		return getPosiciones();
	}
}
