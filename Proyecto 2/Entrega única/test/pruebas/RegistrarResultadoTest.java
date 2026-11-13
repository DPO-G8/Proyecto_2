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
	
	//El ganador de un partido suma 20 a su ranking y el perdedor resto 20
	@Test
	public void hu_adm07_ca2_testRegistrarResultadoActualizaRanking() throws Exception{
		Socio ana= new Socio("ana","1001",25);
		Socio luis= new Socio("luis","1002",23);
		ana.inscribirEnDisciplinaConRanking("Padel","Intermedio",1500);
		luis.inscribirEnDisciplinaConRanking("Padel","Intermedio",1200);
		gestor.registrarSocio(ana);
		gestor.registrarSocio(luis);
		Torneo conSocios = gestor.crearTorneo(admin, "Padel", "Intermedio", 12, Torneo.RECREATIVO, Torneo.SENCILLOS, LocalDate.now().plusDays(7), 0);
		gestor.inscribirJugador(ana, conSocios, null);
		gestor.inscribirJugador(luis, conSocios, null);
		gestor.iniciar(admin, conSocios);
		Encuentro partido = conSocios.getEncuentros().get(0);
		Socio ganador = partido.getParticipanteA().getJugadores().get(0);
		Socio perdedor = partido.getParticipanteB().getJugadores().get(0);
		double rankingGanador = ganador.getRanking("Padel");
		double rankingPerdedor = perdedor.getRanking("Padel");
		gestor.registrarResultado(admin, conSocios, partido, 3, 1);
		assertEquals(rankingGanador + 20, ganador.getRanking("Padel"),0.001, "El ganador deberia ganar 20 puntos");
		assertEquals(rankingPerdedor - 20, perdedor.getRanking("Padel"),0.001, "El perdedor deberia perder 20 puntos");	
	}
	//El ranking del perdedor no puede bajar de 0
	@Test
	public void hu_adm07_ca2_testRegistrarResultadoActualizaRankingNoMenorCero() throws Exception{
		Socio ana= new Socio("ana","1001",25);
		Socio luis= new Socio("luis","1002",23);
		ana.inscribirEnDisciplinaConRanking("Padel","Intermedio",10);
		luis.inscribirEnDisciplinaConRanking("Padel","Intermedio",10);
		gestor.registrarSocio(ana);
		gestor.registrarSocio(luis);
		Torneo conSocios = gestor.crearTorneo(admin, "Padel", "Intermedio", 12, Torneo.RECREATIVO, Torneo.SENCILLOS, LocalDate.now().plusDays(7), 0);
		gestor.inscribirJugador(ana, conSocios, null);
		gestor.inscribirJugador(luis, conSocios, null);
		gestor.iniciar(admin, conSocios);
		Encuentro partido = conSocios.getEncuentros().get(0);
		Socio ganador = partido.getParticipanteA().getJugadores().get(0);
		Socio perdedor = partido.getParticipanteB().getJugadores().get(0);
		double rankingGanador = ganador.getRanking("Padel");
		double rankingPerdedor = perdedor.getRanking("Padel");
		gestor.registrarResultado(admin, conSocios, partido, 3, 1);
		assertEquals(30, ganador.getRanking("Padel"),0.001, "El ganador deberia tener 30 puntos");
		assertEquals(0, perdedor.getRanking("Padel"),0.001, "El perdedor deberia tener 0 puntos");	
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
