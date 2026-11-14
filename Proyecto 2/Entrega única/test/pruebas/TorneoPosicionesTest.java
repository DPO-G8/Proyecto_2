package pruebas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import logica.torneos.InscripcionTorneo;
import logica.torneos.PersonaExterna;
import logica.torneos.Torneo;
import logica.torneos.TorneoIndividual;

public class TorneoPosicionesTest {
	// Este metodo se encarga de crear un torneo con participantes externos el cual se puede usar en las pruebas
	private Torneo crearTorneoConParticipantes(int participantes) {
		Torneo torneo = new TorneoIndividual(1, "Padel", "Intermedio", Torneo.SENCILLOS, 10, Torneo.RECREATIVO,
				LocalDate.now().plusDays(7), 0);
		for (int i = 1; i <= participantes; i++) {
			PersonaExterna externo = new PersonaExterna("Externo " + i, "DOC" + i, PersonaExterna.EXTERNO, "");
			torneo.agregarParticipante(new InscripcionTorneo(externo, false, 0));
		}
		return torneo;
	}
	
	// La tabla debe ordenarse de mayor a menor puntaje
	@Test
	public void testPosicionesOrdenadas() {
		Torneo torneo =crearTorneoConParticipantes(3);
		InscripcionTorneo p1 = torneo.getParticipantes().get(0);
		InscripcionTorneo p2 = torneo.getParticipantes().get(1);
		InscripcionTorneo p3 = torneo.getParticipantes().get(2);
		p1.sumarPuntos(1);
		p2.sumarPuntos(3);
		List<InscripcionTorneo> tabla = torneo.getPosiciones();
		assertSame(p2,tabla.get(0));
		assertSame(p1,tabla.get(1));
		assertSame(p3,tabla.get(2));	
	}
	// Con el mismo puntaje se conserva el orden de inscripcion de los participantes
	@Test
	public void testEmpateConservaElOrdenDeInscripcion() {
		Torneo torneo = crearTorneoConParticipantes(3);
		InscripcionTorneo p1 = torneo.getParticipantes().get(0);
		InscripcionTorneo p2 = torneo.getParticipantes().get(1);
		InscripcionTorneo p3 = torneo.getParticipantes().get(2);
		p1.sumarPuntos(3);
		p2.sumarPuntos(3);
		List<InscripcionTorneo> tabla = torneo.getPosiciones();
		assertSame(p1, tabla.get(0));
		assertSame(p2, tabla.get(1));
		assertSame(p3, tabla.get(2));
	}
	// La clasificacion final es la misma tabla de posiciones
	@Test
	public void testClasificacionEsLaTablaDePosiciones() {
		Torneo torneo = crearTorneoConParticipantes(3);
		torneo.getParticipantes().get(2).sumarPuntos(3);	
		assertEquals(torneo.getPosiciones(), torneo.getClasificacion());
		}


}
