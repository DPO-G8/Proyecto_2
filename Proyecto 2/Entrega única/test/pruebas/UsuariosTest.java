package pruebas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import controlador.Club;
import logica.usuarios.Empleado;
import logica.usuarios.Socio;
import logica.usuarios.Usuario;
import persistencia.RepositorioJSON;

public class UsuariosTest {
	//Se crea carpeta temporal para hacer las pruebas
	@TempDir
	Path carpeta;
	
	private Club club;
	private Socio ana;
	private Socio luis; 
	private Empleado empleado; 
	
	@BeforeEach
	public void setUp() throws Exception {
		club = new Club(new RepositorioJSON(carpeta.toString()));
		ana = club.getGestorUsuarios().registrarSocio("ana", "1001", 24);
		luis = club.getGestorUsuarios().registrarSocio("luis", "1002",25);
		empleado = new Empleado("empleado1", "1", "COD-M1");
		club.getGestorUsuarios().agregarUsuario(empleado);
	}
	
	@AfterEach
	public void tearDown() {
		club = null;
		ana = null;
		luis = null;
		empleado = null;
	}
	
	// Usuario RF1: acceso con login y password
	@Test
	public void hu_usu01_ca1_testLoginCorrecto() throws Exception {
		Usuario usuario = club.getGestorUsuarios().autenticar("ana","1001");
		assertNotNull(usuario);
		assertEquals("ana",usuario.getLogin());
	}
	@Test
	public void hu_usu01_ca2_testLoginClaveIncorrecta() {
		Exception e = assertThrows(Exception.class, () -> {
			club.getGestorUsuarios().autenticar("ana", "xxx");
		});

		assertTrue(e.getMessage().contains("incorrecta"));
	}

	@Test
	public void hu_usu01_ca3_testRegistrarLoginRepetido() {
		Exception e = assertThrows(Exception.class, () -> {
			club.getGestorUsuarios().registrarSocio("ana", "999", 30);
		});

		assertTrue(e.getMessage().contains("existe"));
	}
	//Empleado RF4 y Socio RF10: el socio recibe el 8% de descuento
	@Test
	public void hu_emp04_ca1_testCompartirCodigoDa8PorCiento() throws Exception{
		club.getGestorUsuarios().compartirCodigo(empleado, "luis");
		assertEquals(0.08, luis.getPorcentajeDescuento(),0.001);
	}
	

}
