package pruebas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import controlador.Club;
import logica.deportes.DeporteConjunto;
import logica.deportes.Equipo;
import logica.usuarios.Administrador;
import logica.usuarios.Entrenador;
import logica.usuarios.Socio;
import persistencia.RepositorioJSON;

public class EquiposTest {
	//Se crea carpeta temporal para hacer las pruebas
	@TempDir
	Path carpeta;

	private Club club;
	private Administrador admin;
	private Entrenador entrenador;
	private Socio ana;
	private Socio luis;

	@BeforeEach
	public void setUp() throws Exception {
		club = new Club(new RepositorioJSON(carpeta.toString()));
		admin = new Administrador("admin", "1234");
		entrenador = new Entrenador("entrenador1", "1", "COD-E1");
		club.getGestorUsuarios().agregarUsuario(admin);
		club.getGestorUsuarios().agregarUsuario(entrenador);
		ana = club.getGestorUsuarios().registrarSocio("ana", "1001", 20);
		luis = club.getGestorUsuarios().registrarSocio("luis", "1002", 22);
		club.getGestorEquipos().agregarDeporte(new DeporteConjunto("Futbol", 7));
		club.getGestorEquipos().crearEquipo(admin, "Futbol", "Millos", "Mayores", 15, "entrenador1");
	}

	@AfterEach
	public void tearDown() {
		club = null;
		admin = null;
		entrenador = null;
		ana = null;
		luis = null;
	}
	
	//Admin RF6 poder crear equipos
	
	@Test
	public void hu_equ01_ca1_testCrearEquipoConEntrenador() throws Exception{
		Equipo equipo = club.getGestorEquipos().crearEquipo(admin, "Futbol","Santafe", "Mayores", 15, "entrenador1");
		assertEquals("Santafe",equipo.getNombre());
		assertTrue(entrenador.dirige(equipo));
	}
	
	//Se intenta crear un equipo que tiene un nombre repetido
	@Test
	public void hu_equ01_ca2_testCrearEquipoNombreRepetido() throws Exception{
		Exception e = assertThrows(Exception.class, () -> club.getGestorEquipos().crearEquipo(admin, "Futbol", "Millos", "Mayores", 15, "entrenador1"));
		assertTrue(e.getMessage().contains("Ya existe"));
	}
	//Admin RF6 se intenta eliminar un equipo que no existe
	@Test
	public void hu_equ01_ca3_testEliminarEquipoQueNoExiste() {
		Exception e = assertThrows(Exception.class, ()-> club.getGestorEquipos().eliminarEquipo(admin, "Uniandinos"));
		assertTrue(e.getMessage().contains("No existe"));
	}
	//Admin RF7 se agregan y eliminan jugadores
	
	@Test
	public void hu_equ02_ca1_testAgregarJugador() throws Exception {
		club.getGestorEquipos().agregarJugador(admin, "Millos", "ana", "Delantero", 9);
		Equipo equipo = club.getGestorEquipos().buscarEquipo("Millos");
		assertEquals(1, equipo.getPlantilla().size());
	}
	// Se intenta añadir un jugador que ya esta
	@Test
	public void hu_equ02_ca2_testAgregarJugadorYaPresente() throws Exception {
		club.getGestorEquipos().agregarJugador(admin, "Millos", "ana", "Delantero", 9);
		Exception e = assertThrows(Exception.class, () -> club.getGestorEquipos().agregarJugador(admin, "Millos", "ana", "Portero", 1));
		assertTrue(e.getMessage().contains("ya pertenece"));
	}
	@Test
	public void hu_equ02_ca3_testQuitarJugadorQueNoEsta() throws Exception {
		Exception e = assertThrows(Exception.class, () -> club.getGestorEquipos().quitarJugador(admin, "Millos", "luis"));
		assertTrue(e.getMessage().contains("no pertenece"));
	}

}
