package pruebas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import controlador.Club;
import logica.tienda.Snack;
import logica.tienda.Sugerencia;
import logica.usuarios.Administrador;
import logica.usuarios.Empleado;
import persistencia.RepositorioJSON;

public class AprobarSugerenciaTest {

	// Crea una carpeta temporal para hacer todas las pruebas
	@TempDir
	Path carpeta;

	private Club club;
	private Administrador admin;
	private Empleado empleado;

	@BeforeEach
	public void setUp() throws Exception {
		club = new Club(new RepositorioJSON(carpeta.toString()));
		admin = new Administrador("admin", "9876");
		empleado = new Empleado("empleado 1", "1", "COD-M1");
		club.getGestorUsuarios().agregarUsuario(admin);
		club.getGestorUsuarios().agregarUsuario(empleado);
	}

	@AfterEach
	public void tearDown() throws Exception {
		club = null;
		admin = null;
		empleado = null;
	}

	// Se acepta un snack con alergenos declarados donde el sistema crea el producto
	// y queda disponible para la venta
	@Test
	public void hu_adm03_ca1_testAprobarSnackConAlergenos() throws Exception {
		Sugerencia sugerencia = club.getGestorInventario().proponerProducto(empleado,
				new Snack(10, "Barra de cereal", 3000, Arrays.asList("mani")));
		club.getGestorInventario().resolverSugerencia(admin, sugerencia, true);
		assertEquals(Sugerencia.APROBADA, sugerencia.getEstado(), "La sugerencia deberia quedar aprobada");
		assertTrue(club.getGestorVentas().getProductos().containsKey(10), "El producto deberia estar en el catalogo");
	}

	// No se puede aprobar un snack sin declarar alergenos
	@Test
	public void hu_adm03_ca2_testAprobarSnackSinAlergenos() throws Exception {
		Sugerencia sugerencia = new Sugerencia(new Snack(11, "Galleta", 2000, new ArrayList<String>()));
		Exception e = assertThrows(Exception.class,
				() -> club.getGestorInventario().resolverSugerencia(admin, sugerencia, true));
		assertTrue(e.getMessage().contains("alergenos"),
				"El mensaje deberia indicar que el snack debe declarar sus alergenos");
		assertEquals(Sugerencia.PENDIENTE, sugerencia.getEstado(), "La sugerencia deberia seguir pendiente");
		assertEquals(0, club.getGestorVentas().getProductos().size(), "No deberia agregarse ningun producto");
	}
	//El empleado puede ver la sugerencia rechazada y el producto no entra al catalogo
	@Test
	public void hu_adm03_ca3_testConsultarSugerenciaRechazada() throws Exception {
		Sugerencia sugerencia = club.getGestorInventario().proponerProducto(empleado, new Snack(12, "Papas", 2500, Arrays.asList("ninguno")));
		club.getGestorInventario().resolverSugerencia(admin, sugerencia, false);
		List<Sugerencia> mias = club.getGestorInventario().consultarSugerencias(empleado);
		assertEquals(1, mias.size(), "El empleado deberia ver su sugerencia");
		assertEquals(Sugerencia.RECHAZADA, mias.get(0).getEstado(), "La sugerencia deberia verse como rechazada");
		assertFalse(club.getGestorVentas().getProductos().containsKey(12), "El producto no deberia aparecer en el catalogo");
		}

}
