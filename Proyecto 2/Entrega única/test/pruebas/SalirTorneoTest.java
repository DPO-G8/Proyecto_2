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


public class SalirTorneoTest {
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
	
	// Dado un torneo en estado ABIERTO al salirse se elimina la inscripcion y se libera un cupo
	@Test
	public void hu_soc05_ca1_testSalirDeTorneoLiberarCupoTarifa() throws Exception{
		Torneo torneo = gestor.crearTorneo(admin, "Padel", "Intermedio", 12, Torneo.COMPETITIVO, Torneo.SENCILLOS, LocalDate.now().plusDays(7), 5000);
		gestor.inscribirJugador(ana, torneo, null);
		gestor.salirDeTorneo(ana,torneo);
		assertEquals(0,torneo.cuposOcupados(),"No deberia haber cupos ocupados");
		assertEquals(0,torneo.totalRecaudado(),0.001,"El total recaudado deberia ser cero");
	}
	//En dobles si un integrante se retira del torneo se retira toda la pareja
	@Test
	public void hu_soc05_ca3_testSalirDeTorneoDobles() throws Exception{
		Torneo torneo = gestor.crearTorneo(admin, "Padel", "Intermedio", 12, Torneo.RECREATIVO, Torneo.DOBLES, LocalDate.now().plusDays(7), 0);
		gestor.inscribirJugador(ana, torneo, "luis");
		gestor.salirDeTorneo(luis,torneo);
		assertEquals(0,torneo.cuposOcupados(),"La pareja entera deberia quedar fuera del torneo");
	}
	// Mientras el torneo este en curso no se debe permitir ningun retiro
	
	@Test
	public void hu_soc05_ca5_testSalirDeTorneoEnCurso() throws Exception{
		Torneo torneo = gestor.crearTorneo(admin, "Padel", "Intermedio", 12, Torneo.RECREATIVO, Torneo.SENCILLOS, LocalDate.now().plusDays(7), 0);
		gestor.inscribirJugador(ana, torneo, null);
		gestor.inscribirJugador(luis, torneo, null);
		gestor.iniciar(admin,torneo);
		Exception e = assertThrows(Exception.class, () -> gestor.salirDeTorneo(luis,torneo));
		assertTrue(e.getMessage().contains("no se admiten retiros")," El mensaje debe indicar que ya no se permiten retiros");
		assertEquals(2,torneo.cuposOcupados(),"Los cupos deberian conservarse");
	}
	

}
