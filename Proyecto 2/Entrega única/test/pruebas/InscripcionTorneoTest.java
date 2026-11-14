package pruebas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import logica.torneos.InscripcionTorneo;
import logica.torneos.PersonaExterna;

public class InscripcionTorneoTest {
	//Sirve para crear una inscripcion de una persona
	private InscripcionTorneo crearInscripcion() {
		PersonaExterna externo = new PersonaExterna("Externo 1", "DOC1", PersonaExterna.EXTERNO, "");
		return new InscripcionTorneo(externo, false, 5000);
	}

	// Los puntos se acumulan en una misma inscripcion
	@Test
	public void testSumarPuntosAcumula() {
		InscripcionTorneo inscripcion = crearInscripcion();
		inscripcion.sumarPuntos(3);
		inscripcion.sumarPuntos(1);
		assertEquals(4, inscripcion.getPuntos());
	}

	// El premio del torneo empieza en cero y se puede asignar
	@Test
	public void testPremioEmpiezaEnCeroYSeAsigna() {
		InscripcionTorneo inscripcion = crearInscripcion();
		assertEquals(0, inscripcion.getPremio(), 0.001);
		inscripcion.setPremio(300000);
		assertEquals(300000, inscripcion.getPremio(), 0.001);
	}

	// Una inscripcion se identifica por el documento asignado al externo
	@Test
	public void testIncluyeBuscaPorDocumento() {
		InscripcionTorneo inscripcion = crearInscripcion();
		assertTrue(inscripcion.incluye("DOC1"));
		assertFalse(inscripcion.incluye("DOC2"));
	}
}