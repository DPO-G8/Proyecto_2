package pruebas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import logica.torneos.InscripcionTorneo;
import logica.torneos.PersonaExterna;
import logica.torneos.Torneo;
import logica.torneos.TorneoIndividual;

public class TorneoCuposTest {
	//Crea un torneo recreativo de padel con los cupos que se le introduzcan
	private Torneo crearTorneo(int cupos) {
		return new TorneoIndividual(1,"Padel","Intermedio",Torneo.SENCILLOS,cupos, Torneo.RECREATIVO,LocalDate.now().plusDays(7),0);
	}
	
	//Agrega un participante externoque ocupa un cupo regular o preferencial
	private void ocupar(Torneo torneo, boolean preferencial, int numero) {
		PersonaExterna externo = new PersonaExterna("Externo " + numero, "DOC" + numero, PersonaExterna.EXTERNO, "");
		torneo.agregarParticipante(new InscripcionTorneo(externo, preferencial, 0));
	}
	
	//20% de los cupos tiene que ser preferencial (redondea hacia arriba)
	@Test
	//Torneo de 5 cupos
	public void testVeintePorCientoPreferencialCincoCupos() {
		Torneo torneo = crearTorneo(5);
		assertEquals(1,torneo.getCuposPreferenciales());
		assertEquals(4,torneo.getCuposRegulares());
	}
	//Torneo de 10 cupos
	@Test
	public void testVeintePorCientoPreferencialDiezCupos() {
		Torneo torneo = crearTorneo(10);
		assertEquals(2,torneo.getCuposPreferenciales());
		assertEquals(8,torneo.getCuposRegulares());
	}
	//Torneo de 12 cupos
	@Test
	public void testVeintePorCientoPreferencialDoceCupos() {
		Torneo torneo = crearTorneo(12);
		assertEquals(3,torneo.getCuposPreferenciales());
		assertEquals(9,torneo.getCuposRegulares());
	}
	// Al asignar cupo debe seguir logica de asignamiento segun ranking, false significa regular y true preferencial
	@Test
	public void testMejorRankingCupoPreferencial() throws Exception{
		Torneo torneo =crearTorneo(5);
		assertTrue(torneo.asignarCupo(true));
	}
	@Test
	public void testJugadorComunTomaCupo() throws Exception{
		Torneo torneo =crearTorneo(5);
		assertFalse(torneo.asignarCupo(false));
	}
	//Cuando se agotan los cupos regulares el preferencial libre se libera
	@Test
	public void testPreferencialSeLibera() throws Exception{
		Torneo torneo = crearTorneo(5);
		for (int i = 1; i<=4;i++) {
			ocupar(torneo,false, i);
		}
		assertTrue(torneo.asignarCupo(false));
	}
	@Test
	public void testSinCuposSeRechaza() {
		Torneo torneo = crearTorneo(5);
		ocupar(torneo, true, 1);
		for (int i = 2; i <= 5; i++) {
			ocupar(torneo, false, i);
		}
		Exception e = assertThrows(Exception.class, () ->torneo.asignarCupo(true));
		assertTrue(e.getMessage().contains("no hay cupos"));
	}
	//Ocupar un cupo que es preferencial solo debe afectar el contador de preferencial
	
	@Test
	public void testOcuparPreferencialYActualizarCupos() {
		Torneo torneo = crearTorneo(5);
		ocupar(torneo,true,1);
		assertEquals(0,torneo.cuposPreferencialesLibres());
		assertEquals(4,torneo.cuposRegularesLibres());
		assertEquals(1,torneo.cuposOcupados());
	}
	
}
