package pruebas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import controlador.Club;
import logica.deportes.Competencia;
import logica.deportes.DeporteConjunto;
import logica.deportes.PartidoCompetencia;
import logica.usuarios.Administrador;
import logica.usuarios.Entrenador;
import logica.usuarios.Socio;
import persistencia.RepositorioJSON;

public class AlinearJugadoresTest {
	//Se crea carpeta temporal para hacer las pruebas
	
	@TempDir
	Path carpeta;
	
	private Club club;
	private Entrenador entrenador;
	private PartidoCompetencia partido;
	
	@BeforeEach
	public void setUp() throws Exception {
		club = new Club(new RepositorioJSON(carpeta.toString()));
		Administrador admin = new Administrador("admin", "9876");
		entrenador = new Entrenador("entrenador1", "1", "COD-E1");
		club.getGestorUsuarios().agregarUsuario(admin);
		club.getGestorUsuarios().agregarUsuario(entrenador);

		DeporteConjunto futbol = new DeporteConjunto("Futbol", 7);
		club.getGestorEquipos().agregarDeporte(futbol);
		club.getGestorEquipos().crearEquipo(admin, "Futbol", "Millos", "Mayores", 15, "entrenador1");
		for (int i = 1; i <= 8; i++) {
			club.getGestorUsuarios().registrarSocio("jugador" + i, "1", 22);
			club.getGestorEquipos().agregarJugador(admin, "Millos", "jugador" + i, "Delantero", i);
		}

		club.getGestorCompetencias().agregarCompetencia(new Competencia("Torneo de la casa", "Universidades", "2026", "Mayores", "Liga", 7, futbol));
		club.getGestorCompetencias().inscribirEquipo(entrenador, "Millos", "Torneo de la casa", logins(7));
		partido = club.getGestorCompetencias().programarPartido(entrenador, "Millos", "Torneo de la casa", LocalDate.now().plusDays(3), "Santafe FC", "El campinucho", "Grupos");
	}
	@AfterEach
	public void tearDown() {
		club = null;
		entrenador = null;
		partido = null;
	}
	
	// El siguiente metodo se encarga de devolver los login de cada jugador
	
	private List<String> logins(int hasta){
		List<String> logins = new ArrayList<>();
		for (int i =1; i <= hasta;i++) {
			logins.add("jugador" + i);
		}
		return logins;
	}

	//Una alineacion con el total de jugadores de la nomina y sin sanciones queda registrada
	@Test
	public void hu_ent01_ca1_testAlinearJugadoresValidos() throws Exception {
		club.getGestorCompetencias().convocar(entrenador, partido, logins(7));
		assertEquals(7, partido.getAlineados().size(), "La alineacion deberia quedar registrada con 7 jugadores");
	}

		//Un jugador que no esta en la nomina de la competencia no se puede alinear
	@Test
	public void hu_ent01_ca2_testAlinearJugadorFueraDeLaNomina() throws Exception {
		List<String> alineacion = logins(6);
		alineacion.add("jugador8");
		Exception e = assertThrows(Exception.class, () -> club.getGestorCompetencias().convocar(entrenador, partido, alineacion));
		assertTrue(e.getMessage().contains("habilitado"), "El mensaje deberia indicar que el jugador no esta habilitado para el torneo");
		assertEquals(0, partido.getAlineados().size(), "No deberia registrarse la alineacion");
	}

		//Un jugador suspendido no se puede alinear
	@Test
	public void hu_ent01_ca3_testAlinearJugadorSuspendido() throws Exception {
		Socio suspendido = (Socio) club.getGestorUsuarios().getUsuarios().get("jugador3");
		suspendido.setFechaFinSuspension(LocalDate.now().plusDays(5));
		Exception e = assertThrows(Exception.class, () -> club.getGestorCompetencias().convocar(entrenador, partido, logins(7)));
		assertTrue(e.getMessage().contains("suspendido"), "El mensaje deberia indicar que el jugador esta suspendido");
		assertEquals(0, partido.getAlineados().size(), "No deberia registrarse la alineacion");
		}
	}

