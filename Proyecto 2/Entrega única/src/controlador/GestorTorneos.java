package controlador;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import logica.Texto;
import logica.deportes.InscripcionDisciplina;
import logica.torneos.Encuentro;
import logica.torneos.InscripcionTorneo;
import logica.torneos.PersonaExterna;
import logica.torneos.Torneo;
import logica.torneos.TorneoIndividual;
import logica.usuarios.Administrador;
import logica.usuarios.Bono;
import logica.usuarios.Socio;
import logica.usuarios.Usuario;

public class GestorTorneos {
	public static final int MINIMO_PARTICIPANTES = 2;
	public static final int PUNTOS_VICTORIA = 3;
	public static final double PUNTOS_RANKING = 20;
	public static final double[] PREMIO_BONO = { 0.2, 0.15, 0.1 };
	public static final double[] PREMIO_EFECTIVO = { 0.6, 0.3, 0.1 };

	private final List<Torneo> torneos;
	private final Map<String, Socio> socios;

	public GestorTorneos() {
		this.torneos = new ArrayList<Torneo>();
		this.socios = new LinkedHashMap<String, Socio>();
	}

	public void registrarSocio(Socio s) {
		socios.put(s.getLogin(), s);
	}

	public Torneo crearTorneo(Usuario quien, String disciplina, String categoriaONivel, int cupos, String tipo,
			String modalidad, LocalDate fecha, double tarifa) throws Exception {
		exigirAdministrador(quien, "crear torneos");
		if (!fecha.isAfter(LocalDate.now())) {
			throw new Exception("La fecha del torneo debe ser futura");
		}
		Torneo torneo = new TorneoIndividual(torneos.size() + 1, disciplina, categoriaONivel, modalidad, cupos, tipo,
				fecha, tarifa);
		torneos.add(torneo);
		return torneo;
	}

	public List<Torneo> listar() {
		return new ArrayList<Torneo>(torneos);
	}

	public Torneo buscarTorneo(int id) throws Exception {
		for (Torneo t : torneos) {
			if (t.getId() == id) {
				return t;
			}
		}
		throw new Exception("El torneo " + id + " no existe");
	}

	public InscripcionTorneo inscribirExterno(Usuario quien, Torneo t, String nombre, String documento, String tipo,
			String codigo) throws Exception {
		if (t.buscarParticipante(documento) != null) {
			throw new Exception("El documento " + documento + " ya esta inscrito en el torneo");
		}
		boolean preferencial = t.asignarCupo(false);
		InscripcionTorneo inscripcion = new InscripcionTorneo(new PersonaExterna(nombre, documento, tipo, codigo),
				preferencial, tarifaACobrar(t));
		t.agregarParticipante(inscripcion);
		return inscripcion;
	}

	public void retirarExterno(Usuario quien, Torneo t, String documento) throws Exception {
		exigirAbierto(t, "no se admiten retiros");
		InscripcionTorneo inscripcion = t.buscarParticipante(documento);
		if (inscripcion == null) {
			throw new Exception("El documento " + documento + " no esta inscrito en el torneo");
		}
		t.quitarParticipante(inscripcion);
	}

	public InscripcionTorneo inscribirJugador(Usuario quien, Torneo t, String loginPareja) throws Exception {
		Socio jugador = (Socio) quien;
		Socio pareja = null;
		if (((TorneoIndividual) t).esDobles()) {
			if (vacio(loginPareja)) {
				throw new Exception("En dobles debe indicar la pareja");
			}
			pareja = socios.get(loginPareja);
		}
		boolean preferencial = t.asignarCupo(estaEntreLosMejores(jugador, t));
		InscripcionTorneo inscripcion = new InscripcionTorneo(jugador, pareja, preferencial, tarifaACobrar(t));
		t.agregarParticipante(inscripcion);
		return inscripcion;
	}

	public void salirDeTorneo(Usuario quien, Torneo t) throws Exception {
		InscripcionTorneo inscripcion = t.buscarParticipante(quien.getLogin());
		exigirAbierto(t, "no se admiten retiros");
		t.quitarParticipante(inscripcion);
	}

	public void iniciar(Usuario quien, Torneo t) throws Exception {
		exigirAdministrador(quien, "iniciar torneos");
		int faltan = MINIMO_PARTICIPANTES - t.cuposOcupados();
		if (faltan > 0) {
			throw new Exception("No se puede iniciar: faltan " + faltan + " participante(s), el minimo es "
					+ MINIMO_PARTICIPANTES);
		}
		t.generarEncuentros();
		t.setEstado(Torneo.EN_CURSO);
	}

	public void registrarResultado(Usuario quien, Torneo t, Encuentro e, int marcadorA, int marcadorB)
			throws Exception {
		exigirAdministrador(quien, "registrar resultados");
		if (marcadorA == marcadorB) {
			throw new Exception("En " + t.getNombreDeporte() + " no se admiten empates");
		}
		InscripcionTorneo ganador = marcadorA > marcadorB ? e.getParticipanteA() : e.getParticipanteB();
		InscripcionTorneo perdedor = marcadorA > marcadorB ? e.getParticipanteB() : e.getParticipanteA();
		e.registrarResultado(marcadorA, marcadorB);
		ganador.sumarPuntos(PUNTOS_VICTORIA);
		ajustarRanking(ganador, t, PUNTOS_RANKING);
		ajustarRanking(perdedor, t, -PUNTOS_RANKING);
	}

	public void finalizar(Usuario quien, Torneo t) throws Exception {
		int faltan = t.getEncuentrosPendientes().size();
		if (faltan > 0) {
			throw new Exception("No se puede finalizar: faltan " + faltan + " encuentro(s) por jugar");
		}
		List<InscripcionTorneo> clasificacion = t.getClasificacion();
		if (t.esCompetitivo()) {
			for (int i = 0; i < PREMIO_EFECTIVO.length && i < clasificacion.size(); i++) {
				clasificacion.get(i).setPremio(t.totalRecaudado() * PREMIO_EFECTIVO[i]);
			}
		} else {
			for (int i = 0; i < PREMIO_BONO.length && i < clasificacion.size(); i++) {
				for (Socio s : clasificacion.get(i).getJugadores()) {
					s.agregarBono(new Bono(PREMIO_BONO[i]));
				}
			}
		}
		t.setEstado(Torneo.FINALIZADO);
	}

	private void exigirAdministrador(Usuario quien, String accion) throws Exception {
		if (!(quien instanceof Administrador)) {
			throw new Exception("El usuario no tiene permiso para " + accion + ": solo el administrador");
		}
	}

	private void exigirAbierto(Torneo t, String rechazo) throws Exception {
		if (!Torneo.ABIERTO.equals(t.getEstado())) {
			throw new Exception("El torneo esta " + t.getEstado() + ": " + rechazo);
		}
	}

	private boolean estaEntreLosMejores(Socio jugador, Torneo t) {
		List<InscripcionDisciplina> ranking = new ArrayList<InscripcionDisciplina>();
		for (Socio s : socios.values()) {
			InscripcionDisciplina i = s.getInscripcionDisciplina(t.getNombreDeporte());
			if (i != null && Texto.iguales(i.getNivel(), t.getCategoriaONivel())) {
				ranking.add(i);
			}
		}
		Collections.sort(ranking, new Comparator<InscripcionDisciplina>() {
			public int compare(InscripcionDisciplina x, InscripcionDisciplina y) {
				return Double.compare(y.getPuntajeRanking(), x.getPuntajeRanking());
			}
		});
		for (int i = 0; i < t.getCuposPreferenciales() && i < ranking.size(); i++) {
			if (ranking.get(i).getSocio().getLogin().equals(jugador.getLogin())) {
				return true;
			}
		}
		return false;
	}

	private void ajustarRanking(InscripcionTorneo participante, Torneo t, double delta) {
		for (Socio s : participante.getJugadores()) {
			s.getInscripcionDisciplina(t.getNombreDeporte()).ajustarPuntaje(delta);
		}
	}

	private double tarifaACobrar(Torneo t) {
		return t.esCompetitivo() ? t.getTarifa() : 0;
	}

	private boolean vacio(String s) {
		return s == null || s.trim().isEmpty();
	}
}
