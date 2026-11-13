package pruebas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import controlador.GestorTorneos;
import logica.torneos.Torneo;
import logica.usuarios.Administrador;
import logica.usuarios.Socio;


public class TorneoTest {
	
	private Administrador admin;
	private GestorTorneos gestor;
	
	@BeforeEach
	public void setUp() {
		admin = new Administrador("admin","9876");
		gestor = new GestorTorneos();
	}
	
	@AfterEach
	public void tearDown() {
		admin = null;
		gestor = null;
	}
	//Se crea torneo y queda abierto a inscripciones
		@Test
		public void hu_adm_ca1_testCrearTorneoAbierto() throws Exception{
			Torneo torneo = gestor.crearTorneo(admin,"Padel","Intermedio", 12, Torneo.RECREATIVO,Torneo.SENCILLOS, LocalDate.now().plusDays(7),0);
			assertEquals(Torneo.ABIERTO, torneo.getEstado(),"El torneo deberia quedar abierto");
			assertEquals(1, gestor.listar().size(),"El numero de torneos inscritos no corresponde al esperado");
		}	
	//Se crea torneo competitivo con valor de inscripcion
	
		@Test
		public void hu_adm_ca3_testCrearTorneoCompetitivoConTarifa() throws Exception{
			Torneo torneo = gestor.crearTorneo(admin,"Padel","Intermedio", 12, Torneo.COMPETITIVO,Torneo.SENCILLOS, LocalDate.now().plusDays(7),5000);
			assertEquals(5000, torneo.getTarifa(),0.001,"La tarifa registrada no coincide con la esperada");	
		}
	@Test
	public void hu_adm04_ca2_testCrearTorneoTresPreferencialesNueveRegulares() throws Exception{
		Torneo torneo = gestor.crearTorneo(admin,"Padel","Intermedio", 12, Torneo.RECREATIVO,Torneo.SENCILLOS, LocalDate.now().plusDays(7),0);
		assertEquals(12, torneo.getCuposTotales(), "La cantidad de cupos totales esperados no es correcta");
		assertEquals(3,torneo.getCuposPreferenciales(),"La cantidad de cupos preferenciales esperados no es correcta");
		assertEquals(9,torneo.getCuposRegulares(),"La cantidad de cupos regulares esperados no es correcta");
	}
	//Usuario que no es administrador intenta crear torneo
	@Test
	public void hu_adm_ca6_testCrearTorneoSinPermiso() {
		Socio socio = new Socio("ana", "1234", 25);
		Exception e = assertThrows(Exception.class, ()-> gestor.crearTorneo(socio, "Padel","Intermedio",12, Torneo.RECREATIVO, Torneo.SENCILLOS, LocalDate.now().plusDays(7),0));
		assertTrue(e.getMessage().contains("no tiene permiso"));
		assertEquals(0,gestor.listar().size());
	}
	
	//Torneo rechaza creación de torneo cuando falla un dato ingresado, se incluye fecha que ya paso
	@Test
	public void hu_adm04_ca5_testCrearTorneoFechaPasada() {
		Exception e = assertThrows(Exception.class, () -> gestor.crearTorneo(admin, "Padel", "Intermedio", 12, Torneo.RECREATIVO, Torneo.SENCILLOS, LocalDate.now().minusDays(1), 0));
		assertTrue(e.getMessage().contains("fecha"), "El mensaje deberia indicar que la fecha es invalida");
		assertEquals(0, gestor.listar().size(), "No deberia crearse ningun torneo");
	}
	

}
