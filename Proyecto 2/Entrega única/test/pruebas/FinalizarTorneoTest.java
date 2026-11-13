package pruebas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import controlador.GestorTorneos;
import logica.torneos.Encuentro;
import logica.torneos.InscripcionTorneo;
import logica.torneos.PersonaExterna;
import logica.torneos.Torneo;
import logica.usuarios.Administrador;

public class FinalizarTorneoTest {
	private Administrador admin;
	private GestorTorneos gestor;
	private Torneo torneo;

	@BeforeEach
	public void setUp() throws Exception {
		admin = new Administrador("admin", "9876");
		gestor = new GestorTorneos();
		torneo = gestor.crearTorneo(admin, "Padel", "Intermedio", 12, Torneo.RECREATIVO, Torneo.SENCILLOS, LocalDate.now().plusDays(7), 0);
		gestor.inscribirExterno(admin, torneo, "Externo 1", "1001", PersonaExterna.EXTERNO, null);
		gestor.inscribirExterno(admin, torneo, "Externo 2", "1002", PersonaExterna.EXTERNO, null);
		gestor.iniciar(admin, torneo);
	}

	@AfterEach
	public void tearDown() {
		admin = null;
		gestor = null;
		torneo = null;
	}
	//Funcion que sirve para registrar 1-0 en cada encuentro del torneo
	private void jugarTodos(Torneo t) throws Exception {
		for (Encuentro e : t.getEncuentros()) {
			gestor.registrarResultado(admin, t, e, 1, 0);
		}
	}
	//Al terminarse de jugar todos los encuentros el estado pasa a finalizado y se da la clasificación final
	@Test
	public void hu_adm08_ca1_testFinalizarTorneo() throws Exception{
		jugarTodos(torneo);
		gestor.finalizar(admin,torneo);
		assertEquals(Torneo.FINALIZADO, torneo.getEstado(), "El torneo deberia quedar en finalizado");
		assertEquals(2,torneo.getClasificacion().size(),"La clasificacion deberia tener a los dos participantes");
	}
	
	//Dado un torneo competitivo finalizado con 1000000 de recaudo se reparte el dinero en 600000, 300000 y 100000
	
	@Test
	public void hu_adm08_ca2_testRepartirPremios() throws Exception{
		Torneo competitivo = gestor.crearTorneo(admin, "Padel","Intermedio",12, Torneo.COMPETITIVO,Torneo.SENCILLOS,LocalDate.now().plusDays(7),250000);
		gestor.inscribirExterno(admin, competitivo, "Externo1", "101", PersonaExterna.EXTERNO, null);
		gestor.inscribirExterno(admin, competitivo, "Externo2", "102", PersonaExterna.EXTERNO, null);
		gestor.inscribirExterno(admin, competitivo, "Externo3", "103", PersonaExterna.EXTERNO, null);
		gestor.inscribirExterno(admin, competitivo, "Externo4", "104", PersonaExterna.EXTERNO, null);
		gestor.iniciar(admin, competitivo);
		jugarTodos(competitivo);
		gestor.finalizar(admin,competitivo);
		List<InscripcionTorneo> clasificacion = competitivo.getClasificacion();
		assertEquals(600000, clasificacion.get(0).getPremio(),0.001, "El primero deberia recibir 600.000");
		assertEquals(300000, clasificacion.get(1).getPremio(),0.001, "El segundo deberia recibir 300.000");
		assertEquals(100000, clasificacion.get(2).getPremio(),0.001, "El tercero deberia recibir 100.000");
		assertEquals(0, clasificacion.get(3).getPremio(),0.001, "El cuarto no deberia recibir nada");
	}
	// Un torneo con encuentros sin resultado no se puede finalizar
	@Test
	public void hu_adm08_ca5_testFinalizarSinResultados() throws Exception {
		Exception e = assertThrows(Exception.class, ()-> gestor.finalizar(admin, torneo));
		assertTrue(e.getMessage().contains("faltan"),"El mensaje deberia indicar cuantos encuentros faltan");
		assertEquals(Torneo.EN_CURSO, torneo.getEstado(),"El torneo deberia seguir en curso");
		
	}

}
