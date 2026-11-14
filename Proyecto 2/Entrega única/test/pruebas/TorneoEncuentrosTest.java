package pruebas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import logica.torneos.Encuentro;
import logica.torneos.InscripcionTorneo;
import logica.torneos.PersonaExterna;
import logica.torneos.Torneo;
import logica.torneos.TorneoIndividual;

public class TorneoEncuentrosTest {
	// Crea un torneo con participantes externos y devuelve el torneo ya con todos los participantes
	private Torneo crearTorneoConParticipantes(int participantes) {
		Torneo torneo = new TorneoIndividual(1, "Padel", "Intermedio", Torneo.SENCILLOS, 10, Torneo.RECREATIVO,
				LocalDate.now().plusDays(7), 0);
		for (int i = 1; i <= participantes; i++) {
			PersonaExterna externo = new PersonaExterna("Externo " + i, "DOC" + i, PersonaExterna.EXTERNO, "");
			torneo.agregarParticipante(new InscripcionTorneo(externo, false, 0));
		}
		return torneo;
	}
	
	//Con n participantes se generan n*(n-1)/2 encuentros
	@Test
	public void testCuatroParticipantesSeisEncuentros() {
		Torneo torneo = crearTorneoConParticipantes(4);
		torneo.generarEncuentros();
		assertEquals(6,torneo.getEncuentros().size());
	}
	@Test
	public void testTresParticipantesTresEncuentros() {
		Torneo torneo = crearTorneoConParticipantes(3);
		torneo.generarEncuentros();
		assertEquals(3,torneo.getEncuentros().size());
	}
	@Test
	public void testDosParticipantesUnEncuentros() {
		Torneo torneo = crearTorneoConParticipantes(2);
		torneo.generarEncuentros();
		assertEquals(1,torneo.getEncuentros().size());
	}
	// Nadie puede jugar contra si mismo y ningun cruce de contras puede repetirse
	@Test
	public void testNadieJuegaContraSiMismoNiSeRepiteUnCruce() {
		Torneo torneo = crearTorneoConParticipantes(4);
		torneo.generarEncuentros();
		List<String> cruces = new ArrayList<String>();
		for (Encuentro e : torneo.getEncuentros()) {
			assertFalse(e.getParticipanteA() == e.getParticipanteB());
			String cruce = torneo.getParticipantes().indexOf(e.getParticipanteA()) + "-"+ torneo.getParticipantes().indexOf(e.getParticipanteB());
			assertFalse(cruces.contains(cruce));
			cruces.add(cruce);
		}
	}
	//Al generarse todos los encuentros deben estar en el estado de pendiente
	@Test
	public void testEncuentrosNuevosPendientes() {
		Torneo torneo = crearTorneoConParticipantes(3);
		torneo.generarEncuentros();
		assertEquals(3,torneo.getEncuentrosPendientes().size());
		assertEquals(0,torneo.getEncuentrosJugados().size());
	}
	//Registrar un resultado mueve el encuentro de pendiente a jugado
	@Test
	public void testRegistrarCambiaEstadoAJugado() {
		Torneo torneo = crearTorneoConParticipantes(3);
		torneo.generarEncuentros();
		Encuentro primero = torneo.getEncuentros().get(0);
		primero.registrarResultado(1, 0);
		assertTrue(primero.isJugado());
		assertEquals(1, torneo.getEncuentrosJugados().size());
		assertEquals(2, torneo.getEncuentrosPendientes().size());
	}

}
