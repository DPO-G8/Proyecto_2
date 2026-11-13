package controlador;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import logica.Texto;
import logica.deportes.DeporteConjunto;
import logica.deportes.DisciplinaIndividual;
import logica.deportes.Equipo;
import logica.deportes.InscripcionDisciplina;
import logica.torneos.Encuentro;
import logica.torneos.InscripcionTorneo;
import logica.torneos.PersonaExterna;
import logica.torneos.Torneo;
import logica.torneos.TorneoConjunto;
import logica.torneos.TorneoIndividual;
import logica.usuarios.Administrador;
import logica.usuarios.Bono;
import logica.usuarios.Empleado;
import logica.usuarios.Entrenador;
import logica.usuarios.Socio;
import logica.usuarios.Turno;
import logica.usuarios.Usuario;
import persistencia.RepositorioDatos;

public class GestorTorneos {
	public static final String[] NIVELES = { "Principiante", "Intermedio", "Avanzado" };
	public static final String[] CATEGORIAS = { "Sub-13", "Sub-15", "Sub-17", "Mayores" };
	public static final int MINIMO_PARTICIPANTES = 2;
	public static final String[] DIAS = { "Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado", "Domingo" };
	public static final int PUNTOS_VICTORIA = 3;
	public static final int PUNTOS_EMPATE = 1;
	public static final double PUNTOS_RANKING = 20;
	public static final double[] PREMIO_BONO = { 0.2, 0.15, 0.1 };
	public static final double[] PREMIO_EFECTIVO = { 0.6, 0.3, 0.1 };

	private final RepositorioDatos repositorio;
	private final List<Torneo> torneos;
	private final Map<String, Usuario> usuarios;
	private final List<DisciplinaIndividual> disciplinas;
	private final List<DeporteConjunto> deportes;
	private final List<Equipo> equipos;

	public GestorTorneos() {
		this.repositorio = null;
		this.torneos = new ArrayList<Torneo>();
		this.usuarios = new LinkedHashMap<String, Usuario>();
		this.disciplinas = new ArrayList<DisciplinaIndividual>();
		this.deportes = new ArrayList<DeporteConjunto>();
		this.equipos = new ArrayList<Equipo>();
	}

	public GestorTorneos(RepositorioDatos repositorio, Map<String, Usuario> usuarios,
			ArrayList<DisciplinaIndividual> disciplinas, ArrayList<DeporteConjunto> deportes) throws Exception {
		this.repositorio = repositorio;
		this.usuarios = usuarios;
		this.disciplinas = disciplinas;
		this.deportes = deportes;
		this.equipos = new ArrayList<Equipo>();
		this.torneos = repositorio.cargarTorneos(usuarios, deportes);
	}

	public void registrarSocio(Socio s) {
		usuarios.put(s.getLogin(), s);
	}

	public void registrarEquipo(Equipo equipo) {
		equipos.add(equipo);
	}

	public Torneo crearTorneo(Usuario quien, String disciplina, String categoriaONivel, int cupos, String tipo,
			String modalidad, LocalDate fecha, double tarifa) throws Exception {
		exigirAdministrador(quien, "crear torneos");
		boolean conjunto = modalidad == null;
		if (vacio(disciplina) || vacio(categoriaONivel) || vacio(tipo) || fecha == null) {
			throw new Exception("Falta un dato del torneo: deporte o disciplina, categoria o nivel, tipo y fecha son "
					+ "obligatorios");
		}
		if (!tipo.equals(Torneo.RECREATIVO) && !tipo.equals(Torneo.COMPETITIVO)) {
			throw new Exception("El tipo debe ser RECREATIVO o COMPETITIVO");
		}
		if (conjunto) {
			if (!esUnoDe(categoriaONivel, CATEGORIAS)) {
				throw new Exception("La categoria '" + categoriaONivel + "' no corresponde a " + disciplina
						+ " (Sub-13, Sub-15, Sub-17 o Mayores)");
			}
			if (!deportes.isEmpty() && !existeDeporte(disciplina)) {
				throw new Exception("No existe el deporte de conjunto '" + disciplina + "'");
			}
		} else {
			if (!modalidad.equals(Torneo.SENCILLOS) && !modalidad.equals(Torneo.DOBLES)) {
				throw new Exception("La modalidad debe ser SENCILLOS o DOBLES");
			}
			if (!esUnoDe(categoriaONivel, NIVELES)) {
				throw new Exception("El nivel '" + categoriaONivel + "' no corresponde a " + disciplina
						+ " (Principiante, Intermedio o Avanzado)");
			}
			if (!disciplinas.isEmpty() && !existeDisciplina(disciplina)) {
				throw new Exception("No existe la disciplina individual '" + disciplina + "'");
			}
		}
		if (cupos <= 0) {
			throw new Exception("Los cupos deben ser mayores que cero");
		}
		if (!fecha.isAfter(LocalDate.now())) {
			throw new Exception("La fecha del torneo debe ser futura");
		}
		if (tipo.equals(Torneo.COMPETITIVO) && tarifa <= 0) {
			throw new Exception("Un torneo competitivo necesita una tarifa de inscripcion positiva");
		}
		double tarifaRegistrada = Torneo.COMPETITIVO.equals(tipo) ? tarifa : 0;
		Torneo torneo;
		if (modalidad == null) {
			torneo = new TorneoConjunto(torneos.size() + 1, disciplina, categoriaONivel, cupos, tipo, fecha,
					tarifaRegistrada);
		} else {
			torneo = new TorneoIndividual(torneos.size() + 1, disciplina, categoriaONivel, modalidad, cupos, tipo,
					fecha, tarifaRegistrada);
		}
		torneos.add(torneo);
		guardar();
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
		exigirAdministrador(quien, "inscribir participantes sin cuenta");
		exigirAbierto(t, "ya no hay inscripciones disponibles");
		if (vacio(nombre) || vacio(documento) || vacio(tipo)) {
			throw new Exception("Faltan datos del participante: nombre, documento y tipo son obligatorios");
		}
		if (!tipo.equals(PersonaExterna.ESTUDIANTE) && !tipo.equals(PersonaExterna.EXTERNO)) {
			throw new Exception("El tipo debe ser ESTUDIANTE o EXTERNO");
		}
		if (tipo.equals(PersonaExterna.ESTUDIANTE) && vacio(codigo)) {
			throw new Exception("Falta el codigo estudiantil del estudiante");
		}
		if (t.buscarParticipante(documento) != null) {
			throw new Exception("El documento " + documento + " ya esta inscrito en el torneo");
		}
		boolean preferencial = t.asignarCupo(false);
		InscripcionTorneo inscripcion = new InscripcionTorneo(new PersonaExterna(nombre, documento, tipo, codigo),
				preferencial, tarifaACobrar(t));
		t.agregarParticipante(inscripcion);
		guardar();
		return inscripcion;
	}

	public void retirarExterno(Usuario quien, Torneo t, String documento) throws Exception {
		exigirAdministrador(quien, "retirar participantes sin cuenta");
		exigirAbierto(t, "no se admiten retiros");
		InscripcionTorneo inscripcion = t.buscarParticipante(documento);
		if (inscripcion == null) {
			throw new Exception("El documento " + documento + " no esta inscrito en el torneo");
		}
		t.quitarParticipante(inscripcion);
		guardar();
	}

	public InscripcionTorneo inscribirJugador(Usuario quien, Torneo t, String loginPareja) throws Exception {
		if (!(quien instanceof Socio)) {
			throw new Exception("El usuario no tiene permiso para inscribirse como jugador: debe ser socio");
		}
		if (!(t instanceof TorneoIndividual)) {
			throw new Exception("Solo se inscriben jugadores en un torneo individual");
		}
		Socio jugador = (Socio) quien;
		exigirAbierto(t, "las inscripciones estan cerradas");
		exigirNivel(jugador, t);
		Socio pareja = null;
		if (((TorneoIndividual) t).esDobles()) {
			if (vacio(loginPareja)) {
				throw new Exception("En dobles debe indicar la pareja");
			}
			if (!(usuarios.get(loginPareja) instanceof Socio) || loginPareja.equals(jugador.getLogin())) {
				throw new Exception("La pareja debe ser otro socio registrado");
			}
			pareja = (Socio) usuarios.get(loginPareja);
			exigirNivel(pareja, t);
		}
		if (t.buscarParticipante(jugador.getLogin()) != null) {
			throw new Exception(jugador.getLogin() + " ya esta inscrito en el torneo");
		}
		if (pareja != null && t.buscarParticipante(pareja.getLogin()) != null) {
			throw new Exception(pareja.getLogin() + " ya esta inscrito en el torneo");
		}
		boolean preferencial = t.asignarCupo(estaEntreLosMejores(jugador, t));
		InscripcionTorneo inscripcion = new InscripcionTorneo(jugador, pareja, preferencial, tarifaACobrar(t));
		t.agregarParticipante(inscripcion);
		guardar();
		return inscripcion;
	}

	public void salirDeTorneo(Usuario quien, Torneo t) throws Exception {
		InscripcionTorneo inscripcion = t.buscarParticipante(quien.getLogin());
		if (inscripcion == null) {
			throw new Exception(quien.getLogin() + " no esta inscrito en el torneo");
		}
		exigirAbierto(t, "no se admiten retiros");
		t.quitarParticipante(inscripcion);
		guardar();
	}

	public InscripcionTorneo inscribirEmpleado(Usuario quien, Torneo t) throws Exception {
		if (!(quien instanceof Empleado)) {
			throw new Exception("El usuario no tiene permiso para inscribirse como empleado");
		}
		Empleado empleado = (Empleado) quien;
		if (!(t instanceof TorneoIndividual)) {
			throw new Exception("Los empleados solo pueden inscribirse en torneos individuales");
		}
		exigirAbierto(t, "las inscripciones estan cerradas");
		String dia = DIAS[t.getFecha().getDayOfWeek().getValue() - 1];
		for (Turno turno : empleado.getTurnos()) {
			if (Texto.iguales(turno.getDia(), dia)) {
				throw new Exception(empleado.getLogin() + " esta de turno el " + dia + " del torneo");
			}
		}
		if (t.buscarParticipante(empleado.getLogin()) != null) {
			throw new Exception(empleado.getLogin() + " ya esta inscrito en el torneo");
		}
		boolean preferencial = t.asignarCupo(false);
		InscripcionTorneo inscripcion = new InscripcionTorneo(empleado, preferencial);
		t.agregarParticipante(inscripcion);
		guardar();
		return inscripcion;
	}

	public InscripcionTorneo inscribirEquipo(Usuario quien, Torneo t, Equipo equipo) throws Exception {
		exigirEntrenadorDe(quien, equipo, "inscribirlo");
		if (!(t instanceof TorneoConjunto)) {
			throw new Exception("Solo se inscriben equipos en un torneo de conjunto");
		}
		exigirAbierto(t, "las inscripciones estan cerradas");
		if (!Texto.iguales(equipo.getDeporte().getNombre(), t.getNombreDeporte())
				|| !Texto.iguales(equipo.getCategoria(), t.getCategoriaONivel())) {
			throw new Exception("El equipo " + equipo.getNombre() + " es de " + equipo.getDeporte().getNombre() + " "
					+ equipo.getCategoria() + " y el torneo es de " + t.getNombreDeporte() + " "
					+ t.getCategoriaONivel());
		}
		int minimo = equipo.getDeporte().getMinimoJugadores();
		if (equipo.getPlantilla().size() < minimo) {
			throw new Exception("El equipo " + equipo.getNombre() + " tiene " + equipo.getPlantilla().size()
					+ " jugadores y el minimo de " + equipo.getDeporte().getNombre() + " es " + minimo);
		}
		if (t.buscarParticipante(equipo.getNombre()) != null) {
			throw new Exception("El equipo " + equipo.getNombre() + " ya esta inscrito en el torneo");
		}
		boolean preferencial = t.asignarCupo(estaEntreLosMejores(equipo, t));
		InscripcionTorneo inscripcion = new InscripcionTorneo(equipo, preferencial, tarifaACobrar(t));
		t.agregarParticipante(inscripcion);
		guardar();
		return inscripcion;
	}

	public InscripcionTorneo retirarEquipo(Usuario quien, Torneo t, Equipo equipo) throws Exception {
		exigirEntrenadorDe(quien, equipo, "retirarlo");
		InscripcionTorneo inscripcion = t.buscarParticipante(equipo.getNombre());
		if (inscripcion == null) {
			throw new Exception("El equipo " + equipo.getNombre() + " no esta inscrito en el torneo");
		}
		exigirAbierto(t, "no se admiten retiros");
		t.quitarParticipante(inscripcion);
		guardar();
		return inscripcion;
	}

	public void iniciar(Usuario quien, Torneo t) throws Exception {
		exigirAdministrador(quien, "iniciar torneos");
		exigirAbierto(t, "no se puede iniciar");
		int faltan = MINIMO_PARTICIPANTES - t.cuposOcupados();
		if (faltan > 0) {
			throw new Exception("No se puede iniciar: faltan " + faltan + " participante(s), el minimo es "
					+ MINIMO_PARTICIPANTES);
		}
		t.generarEncuentros();
		t.setEstado(Torneo.EN_CURSO);
		guardar();
	}

	public void registrarResultado(Usuario quien, Torneo t, Encuentro e, int marcadorA, int marcadorB)
			throws Exception {
		exigirAdministrador(quien, "registrar resultados");
		exigirEnCurso(t, "registrar resultados");
		if (e == null || !t.getEncuentros().contains(e)) {
			throw new Exception("El encuentro no pertenece al torneo");
		}
		if (e.isJugado()) {
			throw new Exception("El encuentro ya fue jugado");
		}
		if (marcadorA < 0 || marcadorB < 0) {
			throw new Exception("El marcador no puede ser negativo");
		}
		if (marcadorA == marcadorB && !t.admiteEmpate()) {
			throw new Exception("En " + t.getNombreDeporte() + " no se admiten empates");
		}
		InscripcionTorneo ganador = marcadorA > marcadorB ? e.getParticipanteA() : e.getParticipanteB();
		InscripcionTorneo perdedor = marcadorA > marcadorB ? e.getParticipanteB() : e.getParticipanteA();
		List<InscripcionDisciplina> rankingGanador = new ArrayList<InscripcionDisciplina>();
		List<InscripcionDisciplina> rankingPerdedor = new ArrayList<InscripcionDisciplina>();
		if (t instanceof TorneoIndividual && marcadorA != marcadorB) {
			rankingGanador = rankingDe(ganador, t);
			rankingPerdedor = rankingDe(perdedor, t);
		}
		e.registrarResultado(marcadorA, marcadorB);
		if (marcadorA == marcadorB) {
			e.getParticipanteA().sumarPuntos(PUNTOS_EMPATE);
			e.getParticipanteB().sumarPuntos(PUNTOS_EMPATE);
		} else {
			ganador.sumarPuntos(PUNTOS_VICTORIA);
		}
		for (InscripcionDisciplina i : rankingGanador) {
			i.ajustarPuntaje(PUNTOS_RANKING);
		}
		for (InscripcionDisciplina i : rankingPerdedor) {
			i.ajustarPuntaje(-PUNTOS_RANKING);
		}
		guardar();
		if (repositorio != null && !(rankingGanador.isEmpty() && rankingPerdedor.isEmpty())) {
			repositorio.guardarDisciplinas(disciplinas);
		}
	}

	public void finalizar(Usuario quien, Torneo t) throws Exception {
		exigirAdministrador(quien, "finalizar torneos");
		exigirEnCurso(t, "finalizar");
		int faltan = t.getEncuentrosPendientes().size();
		if (faltan > 0) {
			throw new Exception("No se puede finalizar: faltan " + faltan + " encuentro(s) por jugar");
		}
		List<InscripcionTorneo> clasificacion = t.getClasificacion();
		if (t.esCompetitivo()) {
			int puesto = 0;
			for (InscripcionTorneo i : clasificacion) {
				if (puesto < PREMIO_EFECTIVO.length && !i.esEmpleado()) {
					i.setPremio(t.totalRecaudado() * PREMIO_EFECTIVO[puesto]);
					puesto++;
				}
			}
		} else {
			for (int i = 0; i < PREMIO_BONO.length && i < clasificacion.size(); i++) {
				for (Socio s : clasificacion.get(i).getJugadores()) {
					s.agregarBono(new Bono(PREMIO_BONO[i]));
				}
			}
		}
		t.setEstado(Torneo.FINALIZADO);
		guardar();
		if (repositorio != null && !t.esCompetitivo()) {
			repositorio.guardarUsuarios(usuarios.values());
		}
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

	private void exigirEnCurso(Torneo t, String accion) throws Exception {
		if (!Torneo.EN_CURSO.equals(t.getEstado())) {
			throw new Exception("No se puede " + accion + ": el torneo esta " + t.getEstado());
		}
	}

	private void exigirNivel(Socio socio, Torneo t) throws Exception {
		InscripcionDisciplina inscripcion = socio.getInscripcionDisciplina(t.getNombreDeporte());
		if (inscripcion == null || !Texto.iguales(inscripcion.getNivel(), t.getCategoriaONivel())) {
			throw new Exception(socio.getLogin() + " no cumple el nivel: debe estar inscrito en " + t.getNombreDeporte()
					+ " con nivel " + t.getCategoriaONivel());
		}
	}

	private void exigirEntrenadorDe(Usuario quien, Equipo equipo, String accion) throws Exception {
		if (!(quien instanceof Entrenador) || !((Entrenador) quien).dirige(equipo)) {
			throw new Exception("Solo el entrenador del equipo " + equipo.getNombre() + " puede " + accion);
		}
	}

	private boolean estaEntreLosMejores(Socio jugador, Torneo t) {
		List<InscripcionDisciplina> ranking = new ArrayList<InscripcionDisciplina>();
		for (Usuario u : usuarios.values()) {
			if (!(u instanceof Socio)) {
				continue;
			}
			InscripcionDisciplina i = ((Socio) u).getInscripcionDisciplina(t.getNombreDeporte());
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

	private boolean estaEntreLosMejores(Equipo equipo, Torneo t) {
		List<Equipo> candidatos = new ArrayList<Equipo>(equipos);
		for (DeporteConjunto d : deportes) {
			candidatos.addAll(d.getEquipos());
		}
		List<Equipo> tabla = new ArrayList<Equipo>();
		for (Equipo e : candidatos) {
			if (Texto.iguales(e.getDeporte().getNombre(), t.getNombreDeporte())
					&& Texto.iguales(e.getCategoria(), t.getCategoriaONivel())) {
				tabla.add(e);
			}
		}
		Collections.sort(tabla, new Comparator<Equipo>() {
			public int compare(Equipo x, Equipo y) {
				return y.getPuntosCompetencia() - x.getPuntosCompetencia();
			}
		});
		return tabla.subList(0, Math.min(t.getCuposPreferenciales(), tabla.size())).contains(equipo);
	}

	private List<InscripcionDisciplina> rankingDe(InscripcionTorneo participante, Torneo t) throws Exception {
		List<InscripcionDisciplina> lista = new ArrayList<InscripcionDisciplina>();
		for (Socio s : participante.getJugadores()) {
			InscripcionDisciplina i = s.getInscripcionDisciplina(t.getNombreDeporte());
			if (i == null) {
				throw new Exception(s.getLogin() + " no tiene ranking en " + t.getNombreDeporte());
			}
			lista.add(i);
		}
		return lista;
	}

	private void guardar() throws Exception {
		if (repositorio != null) {
			repositorio.guardarTorneos(torneos);
		}
	}

	private double tarifaACobrar(Torneo t) {
		return t.esCompetitivo() ? t.getTarifa() : 0;
	}

	private boolean esUnoDe(String valor, String[] opciones) {
		for (String o : opciones) {
			if (Texto.iguales(o, valor)) {
				return true;
			}
		}
		return false;
	}

	private boolean existeDeporte(String nombre) {
		for (DeporteConjunto d : deportes) {
			if (Texto.iguales(d.getNombre(), nombre)) {
				return true;
			}
		}
		return false;
	}

	private boolean existeDisciplina(String nombre) {
		for (DisciplinaIndividual d : disciplinas) {
			if (Texto.iguales(d.getNombre(), nombre)) {
				return true;
			}
		}
		return false;
	}

	private boolean vacio(String s) {
		return s == null || s.trim().isEmpty();
	}
}
