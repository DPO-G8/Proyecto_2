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
import logica.usuarios.Administrador;
import logica.usuarios.Socio;
import persistencia.RepositorioJSON;

public class InformacionSocioTest {
	@TempDir
	Path carpeta;

	private Club club;
	private Administrador admin;
	private Socio ana;
	private Socio luis;

	@BeforeEach
	public void setUp() throws Exception {
		club = new Club(new RepositorioJSON(carpeta.toString()));
		admin = new Administrador("admin", "1234");
		club.getGestorUsuarios().agregarUsuario(admin);
		ana = club.getGestorUsuarios().registrarSocio("ana", "1001", 20);
		luis = club.getGestorUsuarios().registrarSocio("luis", "1002", 25);
	}

	@AfterEach
	public void tearDown() {
		club = null;
		admin = null;
		ana = null;
		luis = null;
	}

	@Test
	public void hu_sis02_ca1_testSocioConsultaSuPropiaInformacion() throws Exception {
		Socio consultado = club.getGestorUsuarios().consultarInformacion(ana, ana);
		assertEquals(ana, consultado, "Ana deberia poder consultar su propia informacion");
	}

	@Test
	public void hu_sis02_ca2_testSocioNoConsultaInformacionDeOtro() {
		Exception e = assertThrows(Exception.class, () -> club.getGestorUsuarios().consultarInformacion(ana, luis));
		assertTrue(e.getMessage().contains("propia"), "El mensaje deberia indicar que solo puede consultar su propia informacion");
	}

	@Test
	public void hu_sis02_ca3_testAdministradorConsultaCualquierSocio() throws Exception {
		Socio consultado = club.getGestorUsuarios().consultarInformacion(admin, luis);
		assertEquals(luis, consultado, "El administrador deberia poder consultar la informacion de luis");
	}
}
