package pruebas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import controlador.GestorTorneos;
import logica.torneos.Encuentro;
import logica.torneos.InscripcionTorneo;
import logica.torneos.PersonaExterna;
import logica.torneos.Torneo;
import logica.usuarios.Administrador;

public class DesempateTorneoTest {
	private Administrador admin;
	private GestorTorneos gestor;
	private Torneo torneo;

	@BeforeEach
	public void setUp() throws Exception {
		admin = new Administrador("admin", "9876");
		gestor = new GestorTorneos();
		torneo = gestor.crearTorneo(admin, "Padel", "Intermedio", 12, Torneo.RECREATIVO, Torneo.SENCILLOS, LocalDate.now().plusDays(7), 0);
		for (int i = 1; i <= 3; i++) {
			gestor.inscribirExterno(admin, torneo, "Externo " + i, "100" + i, PersonaExterna.EXTERNO, null);
		}
		gestor.iniciar(admin, torneo);
	}

	@AfterEach
	public void tearDown() {
		admin = null;
		gestor = null;
		torneo = null;
	}

	// Registra los 3 encuentros en el orden en que se generan
	private void jugar(int a0, int b0, int a1, int b1, int a2, int b2) throws Exception {
		List<Encuentro> encuentros = torneo.getEncuentros();
		gestor.registrarResultado(admin, torneo, encuentros.get(0), a0, b0);
		gestor.registrarResultado(admin, torneo, encuentros.get(1), a1, b1);
		gestor.registrarResultado(admin, torneo, encuentros.get(2), a2, b2);
	}

	// Si hay empate en puntos, queda arriba quien tiene mayor diferencia de marcador
	@Test
	public void hu_adm07_ca6_testDesempatePorDiferenciaDeMarcador() throws Exception {
		jugar(1, 0, 0, 3, 1, 0);
		List<InscripcionTorneo> posiciones = torneo.getPosiciones();
		assertEquals(3, posiciones.get(0).getPuntos(), "Los tres deberian tener 3 puntos");
		assertEquals(3, posiciones.get(1).getPuntos(), "Los tres deberian tener 3 puntos");
		assertEquals(3, posiciones.get(2).getPuntos(), "Los tres deberian tener 3 puntos");
		assertSame(torneo.buscarParticipante("1003"), posiciones.get(0), "1003 tiene diferencia +2 y deberia ser primero");
		assertSame(torneo.buscarParticipante("1002"), posiciones.get(1), "1002 tiene diferencia 0 y deberia ser segundo");
		assertSame(torneo.buscarParticipante("1001"), posiciones.get(2), "1001 tiene diferencia -2 y deberia ser tercero");
	}

	// Si el empate sigue tras la diferencia, queda arriba quien se inscribio primero
	@Test
	public void hu_adm07_ca7_testDesempatePorOrdenDeInscripcion() throws Exception {
		jugar(1, 0, 0, 1, 1, 0);
		List<InscripcionTorneo> posiciones = torneo.getPosiciones();
		assertSame(torneo.buscarParticipante("1001"), posiciones.get(0), "1001 se inscribio primero y deberia ser primero");
		assertSame(torneo.buscarParticipante("1002"), posiciones.get(1), "1002 deberia ser segundo");
		assertSame(torneo.buscarParticipante("1003"), posiciones.get(2), "1003 se inscribio de ultimo y deberia ser tercero");
	}
}
