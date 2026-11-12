package pruebas;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import controlador.GestorTorneos;
import logica.torneos.Torneo;
import logica.usuarios.Administrador;


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
	
	//Se crea torneo con 12 cupos
	@Test
	public void hu_adm04_ca2_testCrearTorneoTresPreferencialesNueveRegulares() throws Exception{
		Torneo torneo = gestor.crearTorneo(admin,"Padel","Intermedio", 12, Torneo.RECREATIVO,Torneo.SENCILLOS, LocalDate.now().plusDays(7),0);
		assertEquals(12, torneo.getCuposTotales(), "La cantidad de cupos totales esperados no es correcta");
		assertEquals(3,torneo.getCuposPreferenciales(),"La cantidad de cupos preferenciales esperados no es correcta");
		assertEquals(9,torneo.getCuposRegulares(),"La cantidad de cupos regulares esperados no es correcta");
	}
	

}
