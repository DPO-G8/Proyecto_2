package pruebas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import controlador.Club;
import logica.deportes.DisciplinaIndividual;
import logica.deportes.Instalacion;
import logica.tienda.Articulo;
import logica.tienda.Bebida;
import logica.tienda.Snack;
import logica.tienda.Venta;
import logica.usuarios.Empleado;
import logica.usuarios.Socio;
import persistencia.RepositorioJSON;

public class VentasTest {
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
		club.getGestorVentas().agregarProducto(new Articulo(1,"Raqueta",100000, "Raquetas",5, "Estante A"));
		club.getGestorVentas().agregarProducto(new Bebida(2, "Cafe", 5000, true));
		club.getGestorVentas().agregarProducto(new Bebida(3, "Limonada", 4000, false));
		ArrayList<String> alergenos = new ArrayList<String>();
		alergenos.add("mani");
		club.getGestorVentas().agregarProducto(new Snack(4,"Barra de mani",3000,alergenos));
	
	}
	
	@AfterEach
	public void tearDown() {
		club = null;
		ana = null; 
		luis = null; 
		empleado = null; 
	}
	
	// Crea un pedido para poder hacer las pruebas
	private Map<Integer, Integer> pedido(int idProducto, int cantidad) {
		Map<Integer, Integer> pedido = new LinkedHashMap<Integer, Integer>();
		pedido.put(idProducto, cantidad);
		return pedido;
	}
	
	//Socio RF5 no bebida caliente si hay reserva de padel cerca
	@Test
	public void hu_soc05_ca1_testBebidaCalienteConReservaCercana() throws Exception {
		DisciplinaIndividual padel = new DisciplinaIndividual("Padel", 1);
		padel.agregarInstalacion(new Instalacion(1, "Cancha Padel 1", 4, false, "Padel"));
		club.getGestorReservas().agregarDisciplina(padel);

		LocalDateTime enDiezMinutos = LocalDateTime.now().plusMinutes(10);
		LocalTime inicio = enDiezMinutos.toLocalTime();
		LocalTime fin = inicio.plusMinutes(30).isAfter(inicio) ? inicio.plusMinutes(30) : LocalTime.of(23, 59);
		club.getGestorReservas().reservar(luis, 1, enDiezMinutos.toLocalDate(), inicio, fin, 4, "DOBLES");

		Exception e = assertThrows(Exception.class, () -> {
			club.getGestorVentas().comprar(luis, pedido(2, 1));
		});

		assertTrue(e.getMessage().contains("bebida caliente"));
	}
	
	//Socio RF6 y RF9: IVA 19% y puntos del 2%
	@Test 
	public void hu_soc06_ca1_testComprarTiendaConIvaPuntos() throws Exception{
		Venta venta = club.getGestorVentas().comprar(ana, pedido(1,2));
		assertEquals(238000,Math.round(venta.calcularTotal()));
		assertEquals(4760, ana.getPuntosFidelidad());
	
	}
	// Socio RF6 una compra rechazada no cambia nada
	@Test
	public void hu_soc06_ca2_testComprarMasDelStock() {
		Exception e = assertThrows(Exception.class, () -> {
			club.getGestorVentas().comprar(ana, pedido(1, 10));
		});

		assertTrue(e.getMessage().contains("suficientes"));
		assertEquals(5, ((Articulo) club.getGestorVentas().getProductos().get(1)).getCantidadDisponible());
		assertEquals(0, ana.getPuntosFidelidad());
	}
	
	//Socio RF7 se deben informar los alergenos del snack
	
	@Test
	public void hu_soc07_ca1_testAlergenos() throws Exception {
		assertEquals("mani", club.getGestorVentas().alergenosDe(4).get(0));
	}
	//Socio RF8 consumo del 8% y propina sugerida del 10%
	@Test
	public void hu_soc08_ca1_testCompraCafeteriaConConsumoYPropina() throws Exception{
		Venta venta = club.getGestorVentas().comprar(ana,pedido(3,1));
		assertEquals(4720, Math.round(venta.calcularTotal()));
	}
	
	//Empleado RF3: compra con su descuento del 15%
	@Test
	public void hu_emp03_ca1_testEmpleadoCompraConDescuento() throws Exception{
		Venta venta = club.getGestorVentas().comprar(empleado, pedido(1,1));
		assertEquals(101150, Math.round(venta.calcularTotal()));
	}
	
}
