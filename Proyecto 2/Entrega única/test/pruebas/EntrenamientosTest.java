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
import logica.deportes.DeporteConjunto;
import logica.deportes.DisciplinaIndividual;
import logica.deportes.Instalacion;
import logica.deportes.SesionEntrenamiento;
import logica.usuarios.Administrador;
import logica.usuarios.Entrenador;
import logica.usuarios.Socio;
import persistencia.RepositorioJSON;

public class EntrenamientosTest {
	//Se crea carpeta temporal para hacer las pruebas
	@TempDir
	Path carpeta;

	private Club club;
	private Entrenador entrenador;
	private Socio ana;
	private Socio luis;
	private LocalDate manana;

	@BeforeEach
	public void setUp() throws Exception {
		club = new Club(new RepositorioJSON(carpeta.toString()));
		Administrador admin = new Administrador("admin", "1234");
		entrenador = new Entrenador("entrenador1", "1", "COD-E1");
		club.getGestorUsuarios().agregarUsuario(admin);
		club.getGestorUsuarios().agregarUsuario(entrenador);
		ana = club.getGestorUsuarios().registrarSocio("ana", "1001", 20);
		luis = club.getGestorUsuarios().registrarSocio("luis", "1002", 22);
		DisciplinaIndividual padel = new DisciplinaIndividual("Padel", 2);
		padel.agregarInstalacion(new Instalacion(1, "Cancha Padel 1", 4, false, "Padel"));
		club.getGestorReservas().agregarDisciplina(padel);
		club.getGestorEquipos().agregarDeporte(new DeporteConjunto("Futbol", 7));
		club.getGestorEquipos().crearEquipo(admin, "Futbol", "Millos", "Mayores", 15, "entrenador1");
		club.getGestorEquipos().agregarJugador(admin, "Millos", "ana", "Delantero", 9);
		manana = LocalDate.now().plusDays(1);
	}

	@AfterEach
	public void tearDown() {
		club = null;
		entrenador = null;
		ana = null;
		luis = null;
		manana = null;
	}

	// Se crea una sesion base que se puede ejecutar en todas laspruebas
	private SesionEntrenamiento crearSesionBase() throws Exception {
		return club.getGestorEntrenamientos().crearSesion(entrenador, manana, LocalTime.of(17, 0), 90,
				"Tecnico", 1, "Millos");
	}

	// Entrenador RF1 puede crear una sesion de entrenamiento
	@Test
	public void hu_ent01_ca1_testCrearSesion() throws Exception {
		SesionEntrenamiento sesion = crearSesionBase();
		assertEquals("Millos", sesion.getEquipo().getNombre());
		assertEquals(1, club.getGestorEntrenamientos().getSesiones().size());
	}
	
	//Caso en el cual se intenta crear una sesion en una instalacion ocupada
	@Test
	public void hu_ent01_ca2_testCrearSesionEnInstalacionOcupada() throws Exception {
		crearSesionBase();
		Exception e = assertThrows(Exception.class, () ->club.getGestorEntrenamientos().crearSesion(entrenador, manana, LocalTime.of(17, 30), 60,"Fisico", 1, "Millos"));
		assertTrue(e.getMessage().contains("otra sesion"));
		assertEquals(1, club.getGestorEntrenamientos().getSesiones().size());
	}

	// Entrenador RF2 puede registrar asistencia con la respectiva nota
	@Test
	public void hu_ent02_ca1_testRegistrarAsistenciaConNota() throws Exception {
		SesionEntrenamiento sesion = crearSesionBase();
		club.getGestorEntrenamientos().registrarAsistencia(entrenador, sesion, "ana", "ASISTIO", 5, "Muy bien");
		assertEquals(1, sesion.getAsistencias().size());
		assertEquals(5, sesion.getAsistencias().get(0).getNotaDesempeno());
	}
	//Entrenador digita una nota que esta fuera del rango permitido
	@Test 
	public void hu_ent02_ca2_testNotaFueraDeRango() throws Exception {
		SesionEntrenamiento sesion = crearSesionBase();
		Exception e = assertThrows(Exception.class, () ->club.getGestorEntrenamientos().registrarAsistencia(entrenador, sesion, "ana", "ASISTIO", 9, ""));
		assertTrue(e.getMessage().contains("nota"));
		assertEquals(0, sesion.getAsistencias().size());
	}
	//Entrenador intenta registrar la asistencia de alguien que no pertenece al equipo
	@Test
	public void hu_ent02_ca3_testAsistenciaDeAlguienQueNoEsDelEquipo() throws Exception {
		SesionEntrenamiento sesion = crearSesionBase();
		Exception e = assertThrows(Exception.class, () ->club.getGestorEntrenamientos().registrarAsistencia(entrenador, sesion, "luis", "ASISTIO", 3, ""));
		assertTrue(e.getMessage().contains("no pertenece"));
	}

	// Socio RF12 el socio consulta su asistencia al entrenamiento
	@Test
	public void hu_soc12_ca1_testSocioConsultaSuAsistencia() throws Exception {
		SesionEntrenamiento sesion = crearSesionBase();
		club.getGestorEntrenamientos().registrarAsistencia(entrenador, sesion, "ana", "ASISTIO", 4, "");
		assertEquals(1, club.getGestorEntrenamientos().asistenciasDe(ana).size());
		assertEquals(0, club.getGestorEntrenamientos().asistenciasDe(luis).size());
	}
}