package pruebas;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import controlador.GestorTorneos;
import logica.torneos.Torneo;
import logica.usuarios.Administrador;
import logica.usuarios.Socio;

public class OcuparCupoTest {
	private Administrador admin;
	private GestorTorneos gestor;
	private Socio ana;
	private Socio luis;
	private Socio sandra;
	
	@BeforeEach
	public void setUp() throws Exception{
		admin = new Administrador("admin","9876");
		gestor = new GestorTorneos();
		ana = new Socio("ana","1001",25);
		luis = new Socio("luis","2001",20);
		sandra = new Socio("sandra","3001",28);
		ana.inscribirEnDisciplinaConRanking("Padel", "Intermedio",1500);
		luis.inscribirEnDisciplinaConRanking("Padel", "Intermedio",1200);
		sandra.inscribirEnDisciplinaConRanking("Padel", "Intermedio",1000);
		gestor.registrarSocio(ana);
		gestor.registrarSocio(luis);
		gestor.registrarSocio(sandra);
	}
	
	@AfterEach
	public void tearDown() {
		admin = null;
		gestor = null;
		ana = null;
		luis = null; 
		sandra = null; 
	}
	
	// Jugador con alto puntaje de ranking se le asigna cupo preferencial
	@Test
	public void hu_soc04_ca1_testInscribirRankingAltoCupoPreferencial() throws Exception {
		Torneo torneo = gestor.crearTorneo(admin, "Padel", "Intermedio", 5, Torneo.RECREATIVO, Torneo.SENCILLOS, LocalDate.now().plusDays(7), 0);
		gestor.inscribirJugador(ana, torneo, null);
		assertEquals(0,torneo.cuposPreferencialesLibres(),"Ana es la mejor del ranking deberia ocupar el cupo preferencial");
		assertEquals(4, torneo.cuposRegularesLibres(),"Los cupos regulares no deberian cambiar");
	}
	// Jugador con bajo puntaje de ranking se le asigna cupo general
		@Test
		public void hu_soc04_ca2_testInscribirRankingBajoCupoGeneral() throws Exception {
			Torneo torneo = gestor.crearTorneo(admin, "Padel", "Intermedio", 5, Torneo.RECREATIVO, Torneo.SENCILLOS, LocalDate.now().plusDays(7), 0);
			gestor.inscribirJugador(sandra, torneo, null);
			assertEquals(1,torneo.cuposPreferencialesLibres(),"Los cupos preferenciales no deberian cambiar");
			assertEquals(3, torneo.cuposRegularesLibres(),"Se espera que los cupos regulares libres sean igual a 3");
		}
	//Jugador sin ranking preferente se inscribe a torneo con cupos regulares agotados
		@Test
		public void hu_soc04_ca3_testInscribirSinRankingCupoGeneraLleno() throws Exception {
			Torneo torneo = gestor.crearTorneo(admin, "Padel", "Intermedio", 2, Torneo.RECREATIVO, Torneo.SENCILLOS, LocalDate.now().plusDays(7), 0);
			gestor.inscribirJugador(luis, torneo, null);
			gestor.inscribirJugador(sandra, torneo, null);
			assertEquals(2, torneo.cuposOcupados(), "Los dos jugadores deberian tener cupo");
			assertEquals(0, torneo.cuposPreferencialesLibres(), "El cupo preferencial liberado deberia haberse ocupado");		
			}

}
