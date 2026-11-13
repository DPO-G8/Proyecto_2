package pruebas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import controlador.Club;
import logica.tienda.Articulo;
import logica.tienda.Venta;
import logica.usuarios.Bono;
import logica.usuarios.Socio;
import persistencia.RepositorioJSON;

public class BonoDescuentoTest {
	
	//Se crea una carpeta temporal para hacer las pruebas
	@TempDir
	Path carpeta;
	
	private Club club;
	private Socio ana;
	
	@BeforeEach
	public void setUp() throws Exception {
		club = new Club(new RepositorioJSON(carpeta.toString()));
		ana = club.getGestorUsuarios().registrarSocio("ana", "123", 20);
		club.getGestorVentas().agregarProducto(new Articulo(1, "Raqueta", 100000, "Raquetas", 5, "Estante A"));
	}

	@AfterEach
	public void tearDown() {
		club = null;
		ana = null;
	}
	
	//Se hace la compra de una raqueta (se busca tener una venta sobre la cual aplicar el bono)
	private Venta comprarRaqueta() throws Exception{
		Map<Integer,Integer> pedido = new HashMap<>();
		pedido.put(1, 1);
		return club.getGestorVentas().comprar(ana,pedido);
	}
	
	//Los bonos del socio se conservan al cerrar la aplicacion y volver a abrir el club
	@Test
	public void hu_soc07_ca1_testBonosSeConservanAlReiniciar() throws Exception {
		club.getGestorUsuarios().otorgarBono(ana, new Bono(0.20));
		Club otro = new Club(new RepositorioJSON(carpeta.toString()));
		Socio recargada = (Socio) otro.getGestorUsuarios().getUsuarios().get("ana");
		assertEquals(1, recargada.getBonos().size(), "El bono deberia seguir en la cuenta del socio");
		assertEquals(0.20, recargada.getBonos().get(0).getPorcentaje(), 0.001, "El bono deberia conservar su porcentaje");
		assertFalse(recargada.getBonos().get(0).isUsado(), "El bono deberia seguir disponible");
		}
	//Al redimirse un bono se aplica el porcentaje de este y la aplicacion lo muestra como usado
	@Test
	public void hu_soc07_ca2_testRedimirBono() throws Exception {
		Bono bono = new Bono(0.2);
		club.getGestorUsuarios().otorgarBono(ana,bono);
		Venta venta = comprarRaqueta();
		double antes = venta.calcularTotal();
		club.getGestorVentas().redimirBono(ana,bono,venta);
		assertEquals(antes*0.80, venta.calcularTotal(),0.001,"El total deberia bajar un 20% por el bono");
		assertTrue(bono.isUsado(),"El bono deberia quedar usado");
	}
	//Una compra no admite acumular dos bonos
		@Test
		public void hu_soc07_ca3_testRedimirBonoNoAcumulable() throws Exception {
			Bono primero = new Bono(0.20);
			Bono segundo = new Bono(0.15);
			club.getGestorUsuarios().otorgarBono(ana, primero);
			club.getGestorUsuarios().otorgarBono(ana, segundo);
			Venta venta = comprarRaqueta();
			club.getGestorVentas().redimirBono(ana, primero, venta);
			double conPrimero = venta.calcularTotal();

			Exception e = assertThrows(Exception.class, () -> club.getGestorVentas().redimirBono(ana, segundo, venta));
			assertTrue(e.getMessage().contains("no es acumulable"), "El mensaje deberia indicar que no es acumulable");
			assertFalse(segundo.isUsado(), "El segundo bono no deberia quedar usado");
			assertEquals(conPrimero, venta.calcularTotal(), 0.001, "El total no deberia cambiar");
		}

		//Un bono ya usado no se puede redimir otra vez pues queda en estado de usado
		@Test
		public void hu_soc07_ca4_testRedimirBonoYaUsado() throws Exception {
			Bono bono = new Bono(0.20);
			club.getGestorUsuarios().otorgarBono(ana, bono);
			club.getGestorVentas().redimirBono(ana, bono, comprarRaqueta());
			Venta otraVenta = comprarRaqueta();
			double antes = otraVenta.calcularTotal();

			Exception e = assertThrows(Exception.class, () -> club.getGestorVentas().redimirBono(ana, bono, otraVenta));
			assertTrue(e.getMessage().contains("ya fue usado"), "El mensaje deberia indicar que ya fue usado");
			assertEquals(antes, otraVenta.calcularTotal(), 0.001, "El total de la otra compra no deberia cambiar");
		}
}
