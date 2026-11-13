package pruebas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import controlador.GestorTorneos;
import logica.torneos.Encuentro;
import logica.torneos.PersonaExterna;
import logica.torneos.Torneo;
import logica.usuarios.Administrador;
import logica.usuarios.Socio;


public class RegistrarResultadoTest {
	private Administrador admin;
	private GestorTorneos gestor;
	private Torneo torneo;
	private Encuentro encuentro;
	
	@BeforeEach
	public void setUp() throws Exception{
		admin = new Administrador("admin","9876");
		gestor = new GestorTorneos();
		torneo = gestor.crearTorneo(admin, "Padel", "Intermedio", 12, Torneo.RECREATIVO, Torneo.SENCILLOS, LocalDate.now().plusDays(7), 0);
		gestor.inscribirExterno(admin, torneo, "Externo 1", "1001", PersonaExterna.EXTERNO, null);
		gestor.inscribirExterno(admin, torneo, "Externo 2", "1002", PersonaExterna.EXTERNO, null);
		gestor.iniciar(admin, torneo);
		encuentro = torneo.getEncuentros().get(0);
	}
	@AfterEach
	public void tearDown() {
		admin = null;
		gestor = null;
		torneo = null;
		encuentro = null;
	}
	//Cuando se registra un marcador valido el sistema guarda el resultado
	@Test
	public void hu_adm07_ca1_testRegistrarResultado() throws Exception{
		gestor.registrarResultado(admin,torneo,encuentro,3,1);
		assertTrue(encuentro.isJugado(),"El encuentro deberia quedar como jugado");
		assertEquals(3, encuentro.getParticipanteA().getPuntos(),"El ganador deberia tener 3 puntos");
		assertEquals(0, encuentro.getParticipanteB().getPuntos(),"El perdedor deberia tener 0 puntos");
	}
	// Un deporte que no admite empates rechaza el resultado
	@Test
	public void hu_adm07_ca3_testRegistrarDeporteSinEmpates() throws Exception{
		Exception e = assertThrows(Exception.class, () -> gestor.registrarResultado(admin, torneo, encuentro, 2, 2));
		assertTrue(e.getMessage().contains("empates"), "El mensaje deberia indicar que no se admiten empates");
		assertFalse(encuentro.isJugado(), "El encuentro no deberia quedar jugado");
	}
	
	//Un usuario que no es administrador no debe poder registrar el resultado
	
	@Test
	public void hu_adm07_ca6_testRegistrarSinPermisos() throws Exception{
		Socio socio = new Socio("ana","1234",25);
		Exception e = assertThrows(Exception.class, () -> gestor.registrarResultado(socio, torneo, encuentro, 3, 1));
		assertTrue(e.getMessage().contains("no tiene permiso"), "El mensaje deberia indicar que no se tiene permiso");
		assertFalse(encuentro.isJugado(), "El encuentro no deberia quedar jugado");
	}
	
	

}
