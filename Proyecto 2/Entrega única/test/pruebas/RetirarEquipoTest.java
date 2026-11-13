package pruebas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import controlador.GestorTorneos;
import logica.deportes.DeporteConjunto;
import logica.deportes.Equipo;
import logica.deportes.InscripcionEquipo;
import logica.torneos.Torneo;
import logica.usuarios.Administrador;
import logica.usuarios.Entrenador;
import logica.usuarios.Socio;

public class RetirarEquipoTest {

	private Administrador admin;
	private GestorTorneos gestor;
	private Torneo torneo;
	private Equipo millos;
	private Equipo junior;
	private Entrenador entrenador1;
	private Entrenador entrenador2;

	@BeforeEach
	public void setUp() throws Exception {
		admin = new Administrador("admin", "9876");
		gestor = new GestorTorneos();
		DeporteConjunto futbol = new DeporteConjunto("Futbol", 7);
		millos = new Equipo("Millos", "Mayores", 15, futbol);
		junior = new Equipo("Junior", "Mayores", 15, futbol);
		for (int i = 1; i <= 7; i++) {
			millos.agregarJugador(new InscripcionEquipo(new Socio("jugador" + i, "1", 22), "Delantero", i));
			junior.agregarJugador(new InscripcionEquipo(new Socio("otro" + i, "1", 22), "Delantero", i));
		}
		entrenador1 = new Entrenador("entrenador1", "1", "COD-E1");
		entrenador1.agregarEquipo(millos);
		entrenador1.agregarEquipo(junior);
		entrenador2 = new Entrenador("entrenador2", "1", "COD-E2");
		torneo = gestor.crearTorneo(admin, "Futbol", "Mayores", 12, Torneo.COMPETITIVO, null, LocalDate.now().plusDays(7), 5000);
		gestor.inscribirEquipo(entrenador1, torneo, millos);
	}

	@AfterEach
	public void tearDown() {
		admin = null;
		gestor = null;
		torneo = null;
		millos = null;
		junior = null;
		entrenador1 = null;
		entrenador2 = null;
	}

	//Al retirar al equipo se libera su cupo y su tarifa deja de contar en el recaudo
	@Test
	public void hu_ent04_ca1_testRetirarEquipoLiberaCupoYTarifa() throws Exception {
		gestor.retirarEquipo(entrenador1, torneo, millos);
		assertEquals(0, torneo.cuposOcupados(), "El cupo deberia quedar libre");
		assertEquals(0, torneo.totalRecaudado(), 0.001, "La tarifa ya no deberia contar en el recaudo");
	}

	//Retirar a un equipo que no esta inscrito se rechaza
	@Test
	public void hu_ent04_ca3_testRetirarEquipoNoInscrito() throws Exception {
		Exception e = assertThrows(Exception.class, () -> gestor.retirarEquipo(entrenador1, torneo, junior));
		assertTrue(e.getMessage().contains("no esta inscrito"), "El mensaje deberia indicar que no esta inscrito");
		assertEquals(1, torneo.cuposOcupados(), "Los cupos no deberian cambiar");
		assertEquals(5000, torneo.totalRecaudado(), 0.001, "El recaudo no deberia cambiar");
	}

	//Solo el entrenador del equipo puede retirarlo
	@Test
	public void hu_ent04_ca5_testRetirarEquipoOtroEntrenador() throws Exception {
		Exception e = assertThrows(Exception.class, () -> gestor.retirarEquipo(entrenador2, torneo, millos));
		assertTrue(e.getMessage().contains("entrenador"), "El mensaje deberia indicar que solo el entrenador del equipo puede hacerlo");
		assertEquals(1, torneo.cuposOcupados(), "Los cupos no deberian cambiar");
	}
}
