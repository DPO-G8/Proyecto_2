package pruebas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import controlador.Club;
import logica.tienda.Articulo;
import logica.tienda.Producto;
import logica.tienda.Snack;
import logica.usuarios.Administrador;
import logica.usuarios.Empleado;
import logica.usuarios.Socio;
import persistencia.RepositorioJSON;

public class InventarioTest {
	//Se crea carpeta temporal para hacer las pruebas
	@TempDir
	Path carpeta;

	private Club club;
	private Administrador admin;
	private Empleado empleado;
	private Socio ana;

	@BeforeEach
	public void setUp() throws Exception {
		club = new Club(new RepositorioJSON(carpeta.toString()));
		admin = new Administrador("admin", "1234");
		empleado = new Empleado("empleado1", "1", "COD-M1");
		club.getGestorUsuarios().agregarUsuario(admin);
		club.getGestorUsuarios().agregarUsuario(empleado);
		ana = club.getGestorUsuarios().registrarSocio("ana", "1001", 24);
		club.getGestorVentas().agregarProducto(new Articulo(1, "Raqueta", 100000, "Raquetas", 5, "Estante A"));
	}

	@AfterEach
	public void tearDown() {
		club = null;
		admin = null;
		empleado = null;
		ana = null;
	}
	
	// Admin RF4: el administrador consulta la info de un producto en especifico
	@Test
	public void hu_inv01_ca1_testConsultarProducto() throws Exception{
		Producto producto = club.getGestorInventario().consultarProducto(admin, 1);
		assertEquals("Raqueta",producto.getNombre());
	}
	
	//Socio no puede hacerlo
	@Test
	public void hu_inv01_ca2_testConsultarProductoSocio() throws Exception{
		Exception e = assertThrows(Exception.class, ()-> club.getGestorInventario().consultarProducto(ana, 1));
		assertTrue(e.getMessage().contains("administrador"));
	}
	//Admin RF5 donde el administrador puede actualizar el inventario
	@Test
	public void hu_inv02_ca1_testReponerInventario() throws Exception{
		club.getGestorInventario().reponerInventario(admin, 1, 10);
		Articulo raqueta = (Articulo) club.getGestorVentas().getProductos().get(1);
		assertEquals(15,raqueta.getCantidadDisponible());
	}
	
	//Empleado RF2 no se pueden proponer productos que no tengan un precio positivo
	@Test
	public void hu_emp02_ca1_testProponerSnackSinPrecio() {
		ArrayList<String> ninguno = new ArrayList<String>();
		ninguno.add("ninguno");
		Exception e = assertThrows(Exception.class, () -> {
			club.getGestorInventario().proponerProducto(empleado, new Snack(10, "Galleta", 0, ninguno));});
		assertTrue(e.getMessage().contains("precio"));
	}

}
