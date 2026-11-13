package pruebas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import controlador.Club;
import logica.deportes.DisciplinaIndividual;
import logica.deportes.Instalacion;
import logica.deportes.PartidoPractica;
import logica.usuarios.Entrenador;
import logica.usuarios.Socio;
import persistencia.RepositorioJSON;

public class PartidosPracticaTest {
	//Se crea carpeta temporal para hacer las pruebas
	@TempDir
	Path carpeta;

	private Club club;
	private Socio ana;
	private Entrenador entrenador;

	@BeforeEach
	public void setUp() throws Exception {
		club = new Club(new RepositorioJSON(carpeta.toString()));
		entrenador = new Entrenador("entrenador1", "1", "COD-E1");
		club.getGestorUsuarios().agregarUsuario(entrenador);
		ana = club.getGestorUsuarios().registrarSocio("ana", "1001", 20);
		Socio luis = club.getGestorUsuarios().registrarSocio("luis", "1002", 25);
		Socio cami = club.getGestorUsuarios().registrarSocio("cami", "1003", 30);
		DisciplinaIndividual padel = new DisciplinaIndividual("Padel", 2);
		padel.agregarInstalacion(new Instalacion(1, "Cancha Padel 1", 4, false, "Padel"));
		club.getGestorReservas().agregarDisciplina(padel);
		club.getGestorReservas().inscribirEnDisciplina(ana, "Padel", "Principiante");
		club.getGestorReservas().inscribirEnDisciplina(cami, "Padel", "Intermedio");
		club.getGestorReservas().inscribirEnDisciplina(luis, "Padel", "Avanzado");
	}

	@AfterEach
	public void tearDown() {
		club = null;
		ana = null;
		entrenador = null;
	}
	
	//Este metodo permite determinar el contra quien y se usa en varias pruebas
	private ArrayList<String> contra(String login) {
		ArrayList<String> lista = new ArrayList<String>();
		lista.add(login);
		return lista;
	}

	// Socio RF3 se hace unpartido contra un socio de nivel adyacente
	@Test
	public void hu_par01_ca1_testPartidoContraNivelAdyacente() throws Exception {
		PartidoPractica partido = club.getGestorCompetencias().crearPartidoPractica(ana, "Padel", "Sencillos",LocalDate.now(), contra("cami"));
		assertEquals(2, partido.getJugadores().size());
	}
	//Se intenta hacer un partido contra una persona de nivel no adyacente
	@Test
	public void hu_par01_ca2_testPartidoContraNivelNoAdyacente() {
		Exception e = assertThrows(Exception.class, () ->club.getGestorCompetencias().crearPartidoPractica(ana, "Padel", "Sencillos",LocalDate.now(), contra("luis")));
		assertTrue(e.getMessage().contains("adyacentes"));
	}
	// Socio RF3 contra un entrenador no importa el nivel
	@Test
	public void hu_par01_ca3_testPartidoContraEntrenador() throws Exception {
		PartidoPractica partido = club.getGestorCompetencias().crearPartidoPractica(ana, "Padel", "Sencillos",LocalDate.now(), contra("entrenador1"));
		assertEquals(entrenador, partido.getRival());
	}
}
