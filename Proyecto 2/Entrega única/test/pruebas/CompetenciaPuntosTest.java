package pruebas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import logica.deportes.Competencia;
import logica.deportes.DeporteConjunto;

public class CompetenciaPuntosTest {
	//Este metodo se encarga de crear una competencia del deporte que se le introduzca
	private Competencia crearCompetencia(String deporte) {
		return new Competencia("Torneo de la casa", "Universidades de Bogota", "2026", "Mayores", "Liga", 5,
				new DeporteConjunto(deporte, 5));
	}
	//Se prueba cuantos puntos suma ganar un partido de futbol
	@Test
	public void testVictoriaValeTres() throws Exception{
		Competencia competencia = crearCompetencia("Futbol");
		assertEquals(3,competencia.calcularPuntos(2, 1));
	}
	//Se prueba cuantos puntos suma perder un partido de futbol
	@Test
	public void testDerrotaValeCero() throws Exception{
		Competencia competencia = crearCompetencia("Futbol");
		assertEquals(0,competencia.calcularPuntos(0, 2));
	}
	//Empate solo se puede en futbol y genera 1 punto
	@Test
	public void testEmpateValeUno() throws Exception{
		Competencia competencia = crearCompetencia("Futbol");
		assertEquals(1,competencia.calcularPuntos(1, 1));
	}	
	//Empate en basket no se permite
	@Test
	public void testEmpateEnBaloncestoNoSePermite() {
		Competencia competencia = crearCompetencia("Baloncesto");
		Exception e = assertThrows(Exception.class, () -> competencia.calcularPuntos(80, 80));
		assertTrue(e.getMessage().contains("empates"));
	}
}


