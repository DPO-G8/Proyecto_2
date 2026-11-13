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

public class InscribirEquipoTest {

	private Administrador admin;
	private GestorTorneos gestor;
	private Torneo torneo;
	private Equipo millos;
	private Equipo santafe;
	private Entrenador entrenador1;
	private Entrenador entrenador2;

	@BeforeEach
	public void setUp() throws Exception {
		admin = new Administrador("admin", "9876");
		gestor = new GestorTorneos();
		DeporteConjunto futbol = new DeporteConjunto("Futbol", 7);
		millos = new Equipo("Millos", "Mayores", 15, futbol);
		santafe = new Equipo("Santafe", "Mayores", 15, futbol);
		for (int i = 1; i <= 7; i++) {
			millos.agregarJugador(new InscripcionEquipo(new Socio("jugador" + i, "1", 22), "Delantero", i));
		}
		for (int i = 1; i <= 3; i++) {
			santafe.agregarJugador(new InscripcionEquipo(new Socio("novato" + i, "1", 22), "Delantero", i));
		}
		entrenador1 = new Entrenador("entrenador1", "1", "COD-E1");
		entrenador1.agregarEquipo(millos);
		entrenador1.agregarEquipo(santafe);
		entrenador2 = new Entrenador("entrenador2", "1", "COD-E2");
		torneo = gestor.crearTorneo(admin, "Futbol", "Mayores", 12, Torneo.RECREATIVO, null, LocalDate.now().plusDays(7), 0);
	}

	@AfterEach
	public void tearDown() {
		admin = null;
		gestor = null;
		torneo = null;
		millos = null;
		santafe = null;
		entrenador1 = null;
		entrenador2 = null;
	}
	
	//Crea un equipo completo el cual posee puntos para luego ser llamado en las pruebas
	private Equipo equipoCompleto(String nombre, int puntos) throws Exception {
		Equipo equipo = new Equipo(nombre, "Mayores", 15, millos.getDeporte());
		for (int i = 1; i <= 7; i++) {
			equipo.agregarJugador(new InscripcionEquipo(new Socio(nombre + i, "1", 22), "Delantero", i));
		}
		equipo.sumarPuntosCompetencia(puntos);
		entrenador1.agregarEquipo(equipo);
		gestor.registrarEquipo(equipo);
		return equipo;
	}

	//El entrenador inscribe a su equipo completo en un torneo recreativo ocupando un cupo y sin pagar
	@Test
	public void hu_ent03_ca1_testInscribirEquipoRecreativo() throws Exception {
		gestor.inscribirEquipo(entrenador1, torneo, millos);
		assertEquals(1, torneo.cuposOcupados(), "El equipo deberia ocupar un cupo");
		assertEquals(0, torneo.totalRecaudado(), 0.001, "Un torneo recreativo no cobra");
	}
	// El equipo con mejor puntaje entre varios debe recibir cupo preferencial para la inscripcion
	
	@Test
	public void hu_ent03_ca3_testCupoPreferencialPorPuntos() throws Exception{
		Equipo mejor = equipoCompleto("Fuerte",30);
		Equipo peor = equipoCompleto("Peor",10);
		Torneo nuevo = gestor.crearTorneo(admin, "Futbol", "Mayores", 5, Torneo.RECREATIVO, null, LocalDate.now().plusDays(7), 0);
		gestor.inscribirEquipo(entrenador1, nuevo, mejor);
		assertEquals(0, nuevo.cuposPreferencialesLibres(),"El mejor equipo deberia ocupar los cupos preferenciales");
		assertEquals(4, nuevo.cuposRegularesLibres(),"Los cupos regulares no deberian cambiar");
		gestor.inscribirEquipo(entrenador1, nuevo, peor);
		assertEquals(0, nuevo.cuposPreferencialesLibres(),"El cupo preferencial deberia seguir lleno");
		assertEquals(3, nuevo.cuposRegularesLibres(),"Los cupos regulares deben ser 3");
	}

	//Un equipo con menos jugadores que el minimo del deporte se rechaza
	@Test
	public void hu_ent03_ca7_testInscribirEquipoSinMinimoDeJugadores() throws Exception {
		Exception e = assertThrows(Exception.class, () -> gestor.inscribirEquipo(entrenador1, torneo, santafe));
		assertTrue(e.getMessage().contains("minimo"), "El mensaje deberia indicar el minimo de jugadores");
		assertEquals(0, torneo.cuposOcupados(), "No deberia ocuparse ningun cupo");
	}

	//Solo el entrenador del equipo puede inscribirlo
	@Test
	public void hu_ent03_ca8_testInscribirEquipoOtroEntrenador() throws Exception {
		Exception e = assertThrows(Exception.class, () -> gestor.inscribirEquipo(entrenador2, torneo, millos));
		assertTrue(e.getMessage().contains("entrenador"), "El mensaje deberia indicar que solo el entrenador del equipo puede hacerlo");
		assertEquals(0, torneo.cuposOcupados(), "No deberia ocuparse ningun cupo");
	}

}
