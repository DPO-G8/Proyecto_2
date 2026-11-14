package pruebas;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import logica.deportes.EstadisticaFutbol;
import logica.usuarios.Socio;

public class SuspensionTest {
	private Socio ana;
	private LocalDate finSuspension;

	@BeforeEach
	public void setUp() {
		ana = new Socio("ana", "1", 20);
		finSuspension = LocalDate.now().plusDays(7);
	}

	@AfterEach
	public void tearDown() {
		ana = null;
		finSuspension = null;
	}
	// Registra las estadisticas de cada jugador
	private void registrar(int amarillas, int rojas) {
		EstadisticaFutbol estadistica = new EstadisticaFutbol(ana, null, 0, 0, amarillas, rojas);
		ana.agregarEstadistica(estadistica);
		ana.actualizarSuspension(estadistica, finSuspension);
		}
	//No se suspende a jugador con 0 tarjetas
	@Test
	public void testSinTarjetasNoSuspende() {
		registrar(0,0);
		assertFalse(ana.estaSuspendido());
	}
	//No se suspende a jugador con 1 tarjeta amararilla
	@Test
	public void testAmarillaNoSuspende() {
		registrar(1,0);
		assertFalse(ana.estaSuspendido());
	}
	// Se suspende a jugador con 2 tarjeta amarillas
	@Test
	public void testDosAmarillasSuspende() {
		registrar(1,0);
		registrar(1,0);
		assertTrue(ana.estaSuspendido());
	}
	//Se suspende a jugador con 1 tarjeta roja
	@Test
	public void testRojaSuspende() {
		registrar(0,1);
		assertTrue(ana.estaSuspendido());
	}
	// La suspension termina cuando pasa su fecha de fin
	@Test
	public void testSuspensionVencidaYaNoSuspende() {
		ana.setFechaFinSuspension(LocalDate.now().minusDays(1));
		assertFalse(ana.estaSuspendido());
	}

}
