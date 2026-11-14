package pruebas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import logica.deportes.InscripcionDisciplina;
import logica.usuarios.Socio;

public class RankingTest {
	//Crea una inscripcion a una disciplina con el nivel y puntaje que se indique al sistema
	private InscripcionDisciplina crearInscripcion(String nivel, double puntaje) {
		return new InscripcionDisciplina(new Socio("ana", "1", 20), nivel, puntaje);
	}
	
	//El ganador se le suma 20 y el perdedor se le restan 20
	@Test
	public void testGanadorSuma20() {
		InscripcionDisciplina inscripcion = crearInscripcion("Intermedio",1500);
		inscripcion.ajustarPuntaje(20);
		assertEquals(1520,inscripcion.getPuntajeRanking(),0.001);
	}
	@Test
	public void testPerdedorResta20() {
		InscripcionDisciplina inscripcion = crearInscripcion("Intermedio",1200);
		inscripcion.ajustarPuntaje(-20);
		assertEquals(1180,inscripcion.getPuntajeRanking(),0.001);
	}
	//Puntaje nunca puede bajar de cero
	@Test
	public void testNuncaBajaCero() {
		InscripcionDisciplina inscripcion = crearInscripcion("Intermedio",10);
		inscripcion.ajustarPuntaje(-20);
		assertEquals(0,inscripcion.getPuntajeRanking(),0.001);
	}
	
	// Para ser compatibles o ser adyacentes se deben tener mismo rango o maximo uno abajo
	@Test 
	public void testMismoNivelCompatible() {
		InscripcionDisciplina a = crearInscripcion("Intermedio",1000);
		InscripcionDisciplina b = crearInscripcion("Intermedio",1000);
		assertTrue(a.nivelCompatible(b));
	}
	@Test 
	public void testAdyacenteCompatible() {
		InscripcionDisciplina a = crearInscripcion("Principiante",1000);
		InscripcionDisciplina b = crearInscripcion("Intermedio",1000);
		assertTrue(a.nivelCompatible(b));
	}
	@Test 
	public void testNoAdyacenteNoCompatible() {
		InscripcionDisciplina a = crearInscripcion("Principiante",1000);
		InscripcionDisciplina b = crearInscripcion("Avanzado",1000);
		assertFalse(a.nivelCompatible(b));
	}
}
