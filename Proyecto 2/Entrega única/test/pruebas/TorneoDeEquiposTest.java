package pruebas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import controlador.GestorTorneos;
import logica.deportes.DeporteConjunto;
import logica.deportes.Equipo;
import logica.deportes.InscripcionEquipo;
import logica.torneos.Encuentro;
import logica.torneos.Torneo;
import logica.usuarios.Administrador;
import logica.usuarios.Entrenador;
import logica.usuarios.Socio;

public class TorneoDeEquiposTest {

	private Administrador admin;
	private GestorTorneos gestor;
	private Torneo torneo;

	@BeforeEach
	public void setUp() throws Exception {
		admin = new Administrador("admin", "9876");
		gestor = new GestorTorneos();
		DeporteConjunto futbol = new DeporteConjunto("Futbol", 7);
		Entrenador entrenador = new Entrenador("entrenador1", "1", "COD-E1");
		Equipo millos = equipoCompleto("Millos", futbol);
		Equipo santafe = equipoCompleto("Santafe", futbol);
		entrenador.agregarEquipo(millos);
		entrenador.agregarEquipo(santafe);
		torneo = gestor.crearTorneo(admin, "Futbol", "Mayores", 12, Torneo.RECREATIVO, null, LocalDate.now().plusDays(7), 0);
		gestor.inscribirEquipo(entrenador, torneo, millos);
		gestor.inscribirEquipo(entrenador, torneo, santafe);
	}

	@AfterEach
	public void tearDown() {
		admin = null;
		gestor = null;
		torneo = null;
	}

	//Crea un equipo de futbol con 7 jugadores que equivale al minimo de jugadores que se usara en las pruebas
	private Equipo equipoCompleto(String nombre, DeporteConjunto deporte) throws Exception {
		Equipo equipo = new Equipo(nombre, "Mayores", 15, deporte);
		for (int i = 1; i <= 7; i++) {
			equipo.agregarJugador(new InscripcionEquipo(new Socio(nombre + i, "1", 22), "Delantero", i));
		}
		return equipo;
	}

	//Un torneo de equipos con dos inscritos arranca y el sistema crea el unico encuentro que se puede crear
	@Test
	public void hu_adm06_ca1_testIniciarTorneoDeEquipos() throws Exception {
		gestor.iniciar(admin, torneo);
		assertEquals(Torneo.EN_CURSO, torneo.getEstado(), "El torneo de equipos deberia quedar en curso");
		assertEquals(1, torneo.getEncuentros().size(), "Dos equipos deberian jugar un solo encuentro");
	}

	//En el caso de futbol cada empate si suma puntos
	@Test
	public void hu_adm07_ca1_testEmpateEnTorneoDeEquipos() throws Exception {
		gestor.iniciar(admin, torneo);
		Encuentro partido = torneo.getEncuentros().get(0);
		gestor.registrarResultado(admin, torneo, partido, 2, 2);
		assertTrue(partido.isJugado(), "El encuentro deberia quedar como jugado");
		assertEquals(1, partido.getParticipanteA().getPuntos(), "Cada equipo deberia sumar 1 punto por el empate");
		assertEquals(1, partido.getParticipanteB().getPuntos(), "Cada equipo deberia sumar 1 punto por el empate");
	}

	//Al jugarse todos los encuentros el torneo de equipos se finaliza y queda la clasificacion
	@Test
	public void hu_adm08_ca1_testFinalizarTorneoDeEquipos() throws Exception {
		gestor.iniciar(admin, torneo);
		gestor.registrarResultado(admin, torneo, torneo.getEncuentros().get(0), 1, 0);
		gestor.finalizar(admin, torneo);
		assertEquals(Torneo.FINALIZADO, torneo.getEstado(), "El torneo de equipos deberia quedar finalizado");
		assertEquals(2, torneo.getClasificacion().size(), "La clasificacion deberia tener los dos equipos");
		assertEquals(3, torneo.getClasificacion().get(0).getPuntos(), "El primer equipo deberia tener 3 puntos");
	}
}