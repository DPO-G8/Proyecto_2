package pruebas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import controlador.Club;
import logica.deportes.Competencia;
import logica.deportes.DeporteConjunto;
import logica.deportes.Equipo;
import logica.deportes.EstadisticaFutbol;
import logica.deportes.PartidoCompetencia;
import logica.usuarios.Administrador;
import logica.usuarios.Entrenador;
import logica.usuarios.Socio;
import persistencia.RepositorioJSON;

public class CompetenciaTest {
	//Se crea carpeta temporal para hacer las pruebas
	@TempDir
	Path carpeta;

	private Club club;
	private Entrenador entrenador1;
	private Entrenador entrenador2;
	private Socio jugador1;

	@BeforeEach
	public void setUp() throws Exception {
		club = new Club(new RepositorioJSON(carpeta.toString()));
		Administrador admin = new Administrador("admin", "1234");
		entrenador1 = new Entrenador("entrenador1", "1", "COD-E1");
		entrenador2 = new Entrenador("entrenador2", "1", "COD-E2");
		club.getGestorUsuarios().agregarUsuario(admin);
		club.getGestorUsuarios().agregarUsuario(entrenador1);
		club.getGestorUsuarios().agregarUsuario(entrenador2);
		DeporteConjunto futbol = new DeporteConjunto("Futbol", 7);
		club.getGestorEquipos().agregarDeporte(futbol);
		club.getGestorEquipos().crearEquipo(admin, "Futbol", "Millos", "Mayores", 15, "entrenador1");
		for (int i = 1; i <= 8; i++) {
			Socio socio = club.getGestorUsuarios().registrarSocio("jugador" + i, "1", 22);
			club.getGestorEquipos().agregarJugador(admin, "Millos", "jugador" + i, "Delantero", i);
			if (i == 1) {
				jugador1 = socio;
			}
		}
		club.getGestorUsuarios().registrarSocio("cami", "1", 30);
		club.getGestorCompetencias().agregarCompetencia(new Competencia("Torneo de la casa", "Universidades de Bogota", "2026", "Mayores", "Liga", 7, futbol));
	}

	@AfterEach
	public void tearDown() {
		club = null;
		entrenador1 = null;
		entrenador2 = null;
		jugador1 = null;
	}

	// Esto permite crear una lista con logins para realizar algunas de las pruebas
	private List<String> logins(int cantidad) {
		ArrayList<String> lista = new ArrayList<String>();
		for (int i = 1; i <= cantidad; i++) {
			lista.add("jugador" + i);
		}
		return lista;
	}

	// Este metodo permite inscribir a Millos en la competencia y le programa un partido para hoy
	private PartidoCompetencia inscribirYProgramar() throws Exception {
		club.getGestorCompetencias().inscribirEquipo(entrenador1, "Millos", "Torneo de la casa", logins(8));
		return club.getGestorCompetencias().programarPartido(entrenador1, "Millos", "Torneo de la casa",LocalDate.now(), "Santafe FC", "El campinucho", "Grupos");
	}

	// Entrenador RF4: inscribir el equipo en una competencia
	@Test
	public void hu_com01_ca1_testInscribirEquipoEnCompetencia() throws Exception {
		club.getGestorCompetencias().inscribirEquipo(entrenador1, "Millos", "Torneo de la casa", logins(8));
		Competencia competencia = club.getGestorCompetencias().buscarCompetencia("Torneo de la casa");
		assertEquals(1, competencia.getInscripciones().size());
	}
	//Se intenta inscribir el equipo con una nomina menor a la minima
	@Test
	public void hu_com01_ca2_testInscribirConNominaMenorAlMinimo() {
		Exception e = assertThrows(Exception.class, () ->club.getGestorCompetencias().inscribirEquipo(entrenador1, "Millos", "Torneo de la casa", logins(5))	);
		assertTrue(e.getMessage().contains("minimo"));
	}
	//Se intenta inscribir un equipo con un jugador que no es parte de este
	@Test
	public void hu_com01_ca3_testInscribirJugadorQueNoEsDelEquipo() {
		List<String> nomina = logins(7);
		nomina.set(0, "cami");
		Exception e = assertThrows(Exception.class, () -> club.getGestorCompetencias().inscribirEquipo(entrenador1, "Millos", "Torneo de la casa", nomina));
		assertTrue(e.getMessage().contains("no pertenece"));
	}
	// Otro entrenador intenta inscribir el equipo a la competencia
	@Test
	public void hu_com01_ca4_testOtroEntrenadorNoInscribeElEquipo() {
		Exception e = assertThrows(Exception.class, () ->club.getGestorCompetencias().inscribirEquipo(entrenador2, "Millos", "Torneo de la casa", logins(8)));
		assertTrue(e.getMessage().contains("Solo el entrenador"));
	}

	// Entrenador RF6 se da el resultado del partido y la tabla de posiciones
	@Test
	public void hu_com02_ca1_testVictoriaVale3Puntos() throws Exception {
		PartidoCompetencia partido = inscribirYProgramar();
		club.getGestorCompetencias().registrarResultado(entrenador1, partido, 2, 1);
		Map<Equipo, Integer> tabla = club.getGestorCompetencias().tablaPosiciones("Torneo de la casa");
		Equipo millos = club.getGestorEquipos().buscarEquipo("Millos");
		assertEquals(3, tabla.get(millos));
	}
	//Ambos equipos empatan y se revisa la tabla de posiciones
	@Test
	public void hu_com02_ca2_testEmpateEnFutbolVale1Punto() throws Exception {
		PartidoCompetencia partido = inscribirYProgramar();
		club.getGestorCompetencias().registrarResultado(entrenador1, partido, 1, 1);
		Map<Equipo, Integer> tabla = club.getGestorCompetencias().tablaPosiciones("Torneo de la casa");
		Equipo millos = club.getGestorEquipos().buscarEquipo("Millos");
		assertEquals(1, tabla.get(millos));
	}
	// Entrenador RF6 y Socio RF2 donde la tarjeta roja suspende al jugador
	@Test
	public void hu_com03_ca1_testTarjetaRojaSuspendeAlJugador() throws Exception {
		PartidoCompetencia partido = inscribirYProgramar();
		club.getGestorCompetencias().convocar(entrenador1, partido, logins(7));
		club.getGestorCompetencias().registrarEstadistica(entrenador1, partido,new EstadisticaFutbol(jugador1, partido, 1, 0, 0, 1));
		assertTrue(jugador1.estaSuspendido());
		assertTrue(club.getGestorUsuarios().estadoDe(jugador1).contains("SUSPENDIDO"));
	}

	// Socio RF1 el socio puede ver su posicion en el proximo partido
	@Test
	public void hu_com04_ca1_testSocioVeSuPosicion() throws Exception {
		PartidoCompetencia partido = inscribirYProgramar();
		club.getGestorCompetencias().convocar(entrenador1, partido, logins(7));
		assertEquals("Delantero", club.getGestorCompetencias().proximaPosicion(jugador1));
	}

	// Entrenador RF5 ve las estadisticas de los miembros de sus equipos
	@Test
	public void hu_com05_ca1_testEntrenadorVeEstadisticasDeSuEquipo() throws Exception {
		PartidoCompetencia partido = inscribirYProgramar();
		club.getGestorCompetencias().convocar(entrenador1, partido, logins(7));
		club.getGestorCompetencias().registrarEstadistica(entrenador1, partido,new EstadisticaFutbol(jugador1, partido, 1, 0, 0, 0));
		assertEquals(1, club.getGestorCompetencias().estadisticasDeEquipo(entrenador1, "Millos").size());
	}
	//Otro entrenador intenta ver las estadisticas de un equipo que no es el suyo
	@Test
	public void hu_com05_ca2_testOtroEntrenadorNoVeEstadisticas() {
		Exception e = assertThrows(Exception.class, () -> club.getGestorCompetencias().estadisticasDeEquipo(entrenador2, "Millos"));
		assertTrue(e.getMessage().contains("Solo el entrenador"));
	}
}