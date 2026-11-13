package pruebas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import controlador.GestorTorneos;
import logica.torneos.PersonaExterna;
import logica.torneos.Torneo;
import logica.usuarios.Administrador;

public class InscribirExternoTest {
	private Administrador admin;
	private GestorTorneos gestor;
	private Torneo torneo;

	@BeforeEach
	public void setUp() throws Exception {
		admin = new Administrador("admin", "9876");
		gestor = new GestorTorneos();
		torneo = gestor.crearTorneo(admin, "Padel", "Intermedio", 12, Torneo.RECREATIVO, Torneo.SENCILLOS, LocalDate.now().plusDays(7), 0);
	}
	@AfterEach 
	public void tearDown() {
		admin = null;
		gestor = null;
		torneo = null;
	}
	//Inscribir participante y torneo guarda un cupo
	@Test
	public void hu_adm05_ca1_testInscribirExternoOcupaCupo() throws Exception
	{
		gestor.inscribirExterno(admin,torneo,"Luis Perez","1001", PersonaExterna.EXTERNO,null);
		assertEquals(8,torneo.cuposRegularesLibres(),"El externo deberia ocupar un cupo regular");
	}
	//Aumenta el recaudo segun la inscripción de un participante y la tarifa pagada
	@Test
	public void hu_adm05_ca2_testInscribirExternoSumaRecaudo() throws Exception
	{
		Torneo competitivo = gestor.crearTorneo(admin, "Padel", "Intermedio", 12, Torneo.COMPETITIVO, Torneo.SENCILLOS, LocalDate.now().plusDays(7), 5000);
		gestor.inscribirExterno(admin,competitivo,"Luis Perez","1001", PersonaExterna.EXTERNO,null);
		assertEquals(5000,competitivo.totalRecaudado(),0.001,"La tarifa se debe sumar al total recaudado");
	}
	
	// Mismo documento no se puede inscribir dos veces al torneo
	@Test
	public void hu_adm05_ca3_testInscribirExternoDocumentoRepetido() throws Exception
	{
		gestor.inscribirExterno(admin,torneo,"Luis Perez","1001", PersonaExterna.EXTERNO,null);
		Exception e = assertThrows(Exception.class, () -> gestor.inscribirExterno(admin, torneo, "Luis Perez","1001", PersonaExterna.EXTERNO, null));
		assertTrue(e.getMessage().contains("ya esta inscrito"),"el mensaje deberia indicar que ya esta inscrito");
		assertEquals(8,torneo.cuposRegularesLibres(),"El rechazo no deberia ocupar otro cupo");
	}
	//Si se intenta inscribir a un torneo sin cupos se rechaza la inscripcion
	@Test
	public void hu_adm05_ca4_testInscribirExternoSinCupos() throws Exception {
		Torneo pequeno = gestor.crearTorneo(admin, "Padel", "Intermedio", 1, Torneo.RECREATIVO, Torneo.SENCILLOS, LocalDate.now().plusDays(7), 0);
		gestor.inscribirExterno(admin, pequeno, "Luis Perez", "1001", PersonaExterna.EXTERNO, null);
		Exception e = assertThrows(Exception.class, () -> gestor.inscribirExterno(admin, pequeno, "Ana Gomez", "1002", PersonaExterna.EXTERNO, null));
		assertTrue(e.getMessage().contains("no hay cupos"), "El mensaje deberia indicar que no hay cupos");
	}
	
}
