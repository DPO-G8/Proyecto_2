package pruebas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import controlador.GestorTorneos;
import logica.torneos.Torneo;
import logica.usuarios.Administrador;
import logica.usuarios.Socio;

public class InscribirJugadorTest {
	private Administrador admin;
	private GestorTorneos gestor;
	private Socio ana;
	private Socio luis;

	@BeforeEach
	public void setUp() throws Exception {
		admin = new Administrador("admin", "9876");
		gestor = new GestorTorneos();
		ana = new Socio("ana", "1234", 25);
		luis = new Socio("luis", "5678", 27);
		ana.inscribirEnDisciplina("Padel", "Intermedio");
		luis.inscribirEnDisciplina("Padel", "Intermedio");
		gestor.registrarSocio(ana);
		gestor.registrarSocio(luis);
	}

	@AfterEach
	public void tearDown() {
		admin = null;
		gestor = null;
		ana = null;
		luis = null;
	}
	
	// Un jugador se inscribe a un torneo recreativo sin pagar y ocupando un cupo
	@Test
	public void hu_soc03_ca1_testInscribirJugadorRecreativo() throws Exception{
		Torneo torneo = gestor.crearTorneo(admin, "Padel", "Intermedio", 12, Torneo.RECREATIVO, Torneo.SENCILLOS, LocalDate.now().plusDays(7), 0);
		gestor.inscribirJugador(ana,torneo,null);
		assertEquals(1, torneo.cuposOcupados(),"La inscripción deberia ocupar un cupo");
		assertEquals(0, torneo.totalRecaudado(), 0.001, "Un torneo recreativo no cobra");
	}
		
	// En un torneo competitivo la tarifa se debe sumar al recaudo
		@Test
		public void hu_soc03_ca2_testInscribirJugadorCompetitivo() throws Exception{
			Torneo torneo = gestor.crearTorneo(admin, "Padel", "Intermedio", 12, Torneo.COMPETITIVO, Torneo.SENCILLOS, LocalDate.now().plusDays(7), 5000);
			gestor.inscribirJugador(ana,torneo,null);
			assertEquals(1, torneo.cuposOcupados(),"La inscripción deberia ocupar un cupo");
			assertEquals(5000, torneo.totalRecaudado(), 0.001, "El recaudo deberia ser de 5000");
		}
		// En dobles el sistema toma a las parejas como solo un cupo
		@Test
		public void hu_soc03_ca3_testInscribirJugadorDobles() throws Exception{
			Torneo torneo = gestor.crearTorneo(admin, "Padel", "Intermedio", 12, Torneo.RECREATIVO, Torneo.DOBLES, LocalDate.now().plusDays(7), 0);
			gestor.inscribirJugador(ana,torneo,"luis");
			assertEquals(1, torneo.cuposOcupados(),"La inscripción deberia ocupar un cupo");
		}
		// En dobles no se puede realizar inscripción si no se indica la pareja
		@Test
		public void hu_soc03_ca4_testInscribirJugadorDoblesSinPareja() throws Exception {
			Torneo torneo = gestor.crearTorneo(admin, "Padel", "Intermedio", 12, Torneo.RECREATIVO, Torneo.DOBLES, LocalDate.now().plusDays(7), 0);
			Exception e = assertThrows(Exception.class, () -> gestor.inscribirJugador(ana, torneo, null));
			assertTrue(e.getMessage().contains("pareja"), "El mensaje deberia indicar que se requiere una pareja");
			assertEquals(0, torneo.cuposOcupados(), "No deberia ocuparse ningun cupo");
		}
		
		//Sin cupos disponibles la inscripción se debe rechazar
		@Test
		public void hu_soc03_ca6_testInscribirJugadorSinCupo() throws Exception {
			Torneo torneo = gestor.crearTorneo(admin, "Padel", "Intermedio", 1, Torneo.RECREATIVO, Torneo.SENCILLOS, LocalDate.now().plusDays(7), 0);
			gestor.inscribirJugador(ana, torneo, null);
			Exception e = assertThrows(Exception.class, () -> gestor.inscribirJugador(luis, torneo, null));
			assertTrue(e.getMessage().contains("no hay cupos"), "El mensaje deberia indicar que no hay cupos");
			assertEquals(1, torneo.cuposOcupados(), "El cupo ocupado no deberia cambiar");
		}

}
