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

public class RetirarExternoTest {
	private Administrador admin;
	private GestorTorneos gestor;
	private Torneo torneo;

	@BeforeEach
	public void setUp() throws Exception {
		admin = new Administrador("admin", "9876");
		gestor = new GestorTorneos();
		torneo = gestor.crearTorneo(admin, "Padel", "Intermedio", 12, Torneo.COMPETITIVO, Torneo.SENCILLOS, LocalDate.now().plusDays(7), 5000);
		gestor.inscribirExterno(admin, torneo, "Externo 1", "1001", PersonaExterna.EXTERNO, null);
	}

	@AfterEach
	public void tearDown() {
		admin = null;
		gestor = null;
		torneo = null;
	}
	// Al retirar un inscrito se libera su cupo y su tarifa se resta al recaudo total
	@Test
	public void hu_adm09_ca1_testRetirarExternoLiberarCupoTarifa() throws Exception{
		gestor.retirarExterno(admin,torneo,"1001");
		assertEquals(9, torneo.cuposRegularesLibres(),"El cupo regular del torneo deberia quedar en 9");
		assertEquals(0,torneo.totalRecaudado(),0.001,"La tarifa ya no deberia estar contada en el recaudo total");
		
	}
	// Se retira alguien que no esta inscrito
	@Test
	public void hu_adm09_ca2_testRetirarExternoNoInscrito() throws Exception{
		Exception e = assertThrows(Exception.class, ()-> gestor.retirarExterno(admin,torneo,"2001"));
		assertTrue(e.getMessage().contains("no esta inscrito"), "El mensaje deberia indicar que no esta inscrito");
		assertEquals(8, torneo.cuposRegularesLibres(),"Los cupos no deberian cambiar");
		assertEquals(5000,torneo.totalRecaudado(),0.001,"El recaudo no debería cambiar");
	}
	
	// Cuando el torneo esta en estado EN_CURSO no se aceptan retiros
	
	@Test
	public void hu_adm09_ca3_testRetirarExternoEnCurso() throws Exception{
		gestor.inscribirExterno(admin, torneo, "Externo 2", "1002", PersonaExterna.EXTERNO, null);
		gestor.iniciar(admin, torneo);
		Exception e = assertThrows(Exception.class, ()-> gestor.retirarExterno(admin,torneo,"1001"));
		assertTrue(e.getMessage().contains("no se admiten retiros"), "El mensaje deberia indicar que ya no se admiten retiros");
		assertEquals(Torneo.EN_CURSO, torneo.getEstado(),"El torneo deberia seguir en curso");	
	}
	

}
