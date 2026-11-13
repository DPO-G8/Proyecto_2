package pruebas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import controlador.GestorTorneos;
import logica.torneos.Encuentro;
import logica.torneos.PersonaExterna;
import logica.torneos.Torneo;
import logica.usuarios.Administrador;


public class ConsultarTorneosTest {
	private Administrador admin;
	private GestorTorneos gestor;

	@BeforeEach
	public void setUp() throws Exception {
		admin = new Administrador("admin", "9876");
		gestor = new GestorTorneos();
	}

	@AfterEach
	public void tearDown() {
		admin = null;
		gestor = null;
	}
	// La lista debe mostrar todos los torneos existentes junto con su estado
	@Test
	public void hu_soc06_ca1_testListarTorneosConEstado() throws Exception {
		gestor.crearTorneo(admin, "Padel", "Intermedio", 12, Torneo.RECREATIVO, Torneo.SENCILLOS, LocalDate.now().plusDays(7), 0);
		gestor.crearTorneo(admin, "Padel", "Intermedio", 12, Torneo.COMPETITIVO, Torneo.SENCILLOS, LocalDate.now().plusDays(7), 5000);
		List<Torneo> torneos = gestor.listar();
		assertEquals(2, torneos.size(), "La lista deberia tener los dos torneos");
		for (Torneo t : torneos) {
			assertEquals(Torneo.ABIERTO, t.getEstado(), "Cada torneo recien creado deberia estar abierto");
		}
	}
	//Cuando un usuario consulta un torneo el detalle debe mostrar participantes, encuentros jugados y pendientes y posiciones.
	
	@Test
	public void hu_soc06_ca3_testDetalleTorneoEnCurso() throws Exception {
		Torneo torneo = gestor.crearTorneo(admin, "Padel", "Intermedio", 12, Torneo.RECREATIVO, Torneo.SENCILLOS, LocalDate.now().plusDays(7), 0);
		gestor.inscribirExterno(admin, torneo, "Externo 1", "1001", PersonaExterna.EXTERNO, null);
		gestor.inscribirExterno(admin, torneo, "Externo 2", "1002", PersonaExterna.EXTERNO, null);
		gestor.inscribirExterno(admin, torneo, "Externo 3", "1003", PersonaExterna.EXTERNO, null);
		gestor.iniciar(admin, torneo);
		Encuentro primero = torneo.getEncuentros().get(0);
		gestor.registrarResultado(admin, torneo, primero, 1, 0);
		
		assertEquals(3, torneo.getParticipantes().size(), "Deberia mostrar los 3 participantes inscritos");
		assertEquals(1, torneo.getEncuentrosJugados().size(), "Deberia mostrar 1 encuentro jugado");
		assertEquals(2, torneo.getEncuentrosPendientes().size(), "Deberia mostrar los 2 encuentros pendientes");
		assertEquals(3, torneo.getPosiciones().size(), "Deberia mostrar las 3 posiciones de los participantes inscritos");
		assertEquals(3, torneo.getPosiciones().get(0).getPuntos(), "El primero deberia tener 3 puntos");
	}
	// Cuando se consulta un torneo que no existe se rechaza
	@Test
	public void hu_soc06_ca5_testConsultarTorneoNoExiste() throws Exception {
		Exception e = assertThrows(Exception.class, () -> gestor.buscarTorneo(999));
		assertTrue(e.getMessage().contains("no existe"), "El mensaje deberia indicar que el torneo no existe");
	}
	
}
