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
import logica.usuarios.Socio;


public class IniciarTorneoTest {
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
	//Inscribe participantes para armar los distintos casos, se usa en los varios casos, se usa con tal de reutilizarla en todos los casos
	private void inscribirExternos(Torneo t, int cantidad) throws Exception {
		for (int i = 1; i <= cantidad; i++) {
			gestor.inscribirExterno(admin, t, "Externo " + i, "100" + i, PersonaExterna.EXTERNO, null);
		}
	}
	// El torneo se pone en estado EN_CURSO y se generan todos los encuentros todos contra todos
	@Test 
	public void hu_adm06_ca1_testIniciarTorneoEnCursoConEncuentros() throws Exception{
		inscribirExternos(torneo,2);
		gestor.iniciar(admin,torneo);
		assertEquals(Torneo.EN_CURSO, torneo.getEstado(),"El torneo deberia quedar en curso");
		assertEquals(1, torneo.getEncuentros().size(),"Con dos participantes debe haber un solo encuentro");
	}
	// No se puede iniciar torneo con 2 participantes
	@Test 
	public void hu_adm06_ca2_testIniciarTorneoConUnParticipante() throws Exception{
		inscribirExternos(torneo,1);
		Exception e = assertThrows(Exception.class, () -> gestor.iniciar(admin,torneo));
		assertTrue(e.getMessage().contains("faltan"),"El mensaje deberia indicar que faltan participantes");
		assertEquals(Torneo.ABIERTO, torneo.getEstado(),"Como fallo el inicio torneo deberia seguir en ABIERTO");
	}
	// Usuario que no es administrador intenta abrir un torneo
	@Test 
	public void hu_adm06_ca4_testIniciarTorneoSinPermiso() throws Exception{
		inscribirExternos(torneo,2);
		Socio socio = new Socio("ana","1234",25);
		Exception e = assertThrows(Exception.class, () -> gestor.iniciar(socio,torneo));
		assertTrue(e.getMessage().contains("no tiene permiso"),"El mensaje deberia indicar que no tiene permiso");
		assertEquals(Torneo.ABIERTO, torneo.getEstado(),"Como fallo el inicio torneo deberia seguir en ABIERTO");
	}
	

}
