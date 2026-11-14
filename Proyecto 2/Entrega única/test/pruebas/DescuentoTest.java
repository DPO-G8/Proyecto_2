package pruebas;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import logica.tienda.Articulo;
import logica.tienda.Bebida;
import logica.tienda.Venta;
import logica.tienda.VentaCafeteria;
import logica.tienda.VentaTienda;
import logica.usuarios.Empleado;
import logica.usuarios.Socio;

public class DescuentoTest {
	
	//Permite crear una raqueta con unos valores fijos
	private Articulo raqueta() {
		return new Articulo(1, "Raqueta", 100000, "Raquetas", 5, "Estante A");
	}

	// Porcentajes de descuento por cada tipo de usuario
	@Test
	public void testSocioSinCodigoNoTieneDescuento() {
		Socio ana = new Socio("ana", "1", 20);
		assertEquals(0, ana.getPorcentajeDescuento(), 0.001);
	}

	@Test
	public void testSocioConCodigoCompartidoTiene8Porciento() {
		Socio ana = new Socio("ana", "1", 20);
		ana.setEmpleadoDescuento(new Empleado("empleado1", "1", "COD-M1"));
		assertEquals(0.08, ana.getPorcentajeDescuento(), 0.001);
	}

	@Test
	public void testEmpleadoTiene15Porciento() {
		Empleado empleado = new Empleado("empleado1", "1", "COD-M1");
		assertEquals(0.15, empleado.getPorcentajeDescuento(), 0.001);
	}

	// Total de la venta se le aplica descuento y luego IVA 19%
	@Test
	public void testVentaDeTiendaConDescuentoCompartido() {
		Socio ana = new Socio("ana", "1", 20);
		ana.setEmpleadoDescuento(new Empleado("empleado1", "1", "COD-M1"));
		Venta venta = new VentaTienda(ana);
		venta.agregarLinea(raqueta(), 1, 100000);
		assertEquals(109480, venta.calcularTotal(), 0.5);
	}

	// El bono no se acumula con el descuento compartido, en caso de ingresar ambos el bono tiene prioridad
	@Test
	public void testBonoSustituyeAlDescuentoCompartido() {
		Socio ana = new Socio("ana", "1", 20);
		ana.setEmpleadoDescuento(new Empleado("empleado1", "1", "COD-M1"));
		Venta venta = new VentaTienda(ana);
		venta.agregarLinea(raqueta(), 1, 100000);
		venta.setPorcentajeBono(0.20);
		assertEquals(95200, venta.calcularTotal(), 0.5);
	}

	// Los puntos redimidos se restan antes de aplicar el IVA
	@Test
	public void testPuntosRedimidosRestanAntesDelIva() {
		Socio ana = new Socio("ana", "1", 20);
		Venta venta = new VentaTienda(ana);
		venta.agregarLinea(raqueta(), 1, 100000);
		venta.setPuntosRedimidos(10000);
		assertEquals(107100, venta.calcularTotal(), 0.5);
	}

	// En cafeteria consumo cuesta 8% mas la propina que elija el socio
	@Test
	public void testCafeteriaConPropinaPersonalizada() {
		Socio ana = new Socio("ana", "1", 20);
		VentaCafeteria venta = new VentaCafeteria(ana);
		venta.agregarLinea(new Bebida(3, "Limonada", 4000, false), 1, 4000);
		venta.setPorcentajePropina(0.15);
		assertEquals(4920, venta.calcularTotal(), 0.5);
	}
}