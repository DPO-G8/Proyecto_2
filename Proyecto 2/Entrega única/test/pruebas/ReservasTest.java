package pruebas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import controlador.Club;
import logica.deportes.DisciplinaIndividual;
import logica.deportes.Instalacion;
import logica.usuarios.Socio;
import persistencia.RepositorioJSON;

public class ReservasTest {
	//Se crea carpeta temporal para hacer las pruebas
	@TempDir
	Path carpeta;

	private Club club;
	private Socio ana;
	private DisciplinaIndividual padel;

	@BeforeEach
	public void setUp() throws Exception {
		club = new Club(new RepositorioJSON(carpeta.toString()));
		ana = club.getGestorUsuarios().registrarSocio("ana", "1001", 20);
		padel = new DisciplinaIndividual("Padel", 2);
		padel.agregarInstalacion(new Instalacion(1, "Cancha Padel 1", 4, false, "Padel"));
		club.getGestorReservas().agregarDisciplina(padel);
	}

	@AfterEach
	public void tearDown() {
		club = null;
		ana = null;
		padel = null;
	}
	
	//Socio RF11 puede inscribirse a una disciplina individual
	@Test
	public void hu_res01_ca1_testInscribirseDisciplina() throws Exception
	{
		club.getGestorReservas().inscribirEnDisciplina(ana, "Padel", "Principiante");
		assertEquals(1,padel.getInscripciones().size());
	}
	//Se intenta hacer inscripción con un nivel que no es valido
	@Test
	public void hu_res01_ca2_testInscribirseConNivelInvalido() {
		Exception e = assertThrows(Exception.class, () -> club.getGestorReservas().inscribirEnDisciplina(ana, "Padel", "Experto"));
		assertTrue(e.getMessage().contains("invalido"));
		assertEquals(0, padel.getInscripciones().size());
	}
	//La misma persona intenta inscribirse dos veces
	@Test
	public void hu_res01_ca3_testInscribirseDosVeces() throws Exception {
		club.getGestorReservas().inscribirEnDisciplina(ana, "Padel", "Principiante");
		
		Exception e = assertThrows(Exception.class, () -> club.getGestorReservas().inscribirEnDisciplina(ana, "Padel", "Intermedio"));
		assertTrue(e.getMessage().contains("inscrito"));
	}
	// Socio RF4 no puede reservar en una fecha que ya paso 
	@Test
	public void hu_res02_ca1_testReservarEnFechaPasada() {
		Exception e = assertThrows(Exception.class, () ->club.getGestorReservas().reservar(ana, 1, LocalDate.now().minusDays(1),LocalTime.of(15, 0), LocalTime.of(16, 0), 4, "DOBLES"));
		assertTrue(e.getMessage().contains("fecha pasada"));
	}
	
}
