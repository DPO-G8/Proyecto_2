package pruebas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import controlador.GestorTorneos;
import logica.torneos.Encuentro;
import logica.torneos.InscripcionTorneo;
import logica.torneos.PersonaExterna;
import logica.torneos.Torneo;
import logica.usuarios.Administrador;
import logica.usuarios.Socio;

public class FinalizarTorneoTest {
	private Administrador admin;
	private GestorTorneos gestor;
	private Torneo torneo;

	@BeforeEach
	public void setUp() throws Exception {
		admin = new Administrador("admin", "9876");
		gestor = new GestorTorneos();
		torneo = gestor.crearTorneo(admin, "Padel", "Intermedio", 12, Torneo.RECREATIVO, Torneo.SENCILLOS,
				LocalDate.now().plusDays(7), 0);
		gestor.inscribirExterno(admin, torneo, "Externo 1", "1001", PersonaExterna.EXTERNO, null);
		gestor.inscribirExterno(admin, torneo, "Externo 2", "1002", PersonaExterna.EXTERNO, null);
		gestor.iniciar(admin, torneo);
	}

	@AfterEach
	public void tearDown() {
		admin = null;
		gestor = null;
		torneo = null;
	}

	// Funcion que sirve para registrar 1-0 en cada encuentro del torneo
	private void jugarTodos(Torneo t) throws Exception {
		for (Encuentro e : t.getEncuentros()) {
			gestor.registrarResultado(admin, t, e, 1, 0);
		}
	}

	// Al terminarse de jugar todos los encuentros el estado pasa a finalizado y se
	// da la clasificación final
	@Test
	public void hu_adm08_ca1_testFinalizarTorneo() throws Exception {
		jugarTodos(torneo);
		gestor.finalizar(admin, torneo);
		assertEquals(Torneo.FINALIZADO, torneo.getEstado(), "El torneo deberia quedar en finalizado");
		assertEquals(2, torneo.getClasificacion().size(), "La clasificacion deberia tener a los dos participantes");
	}

	// Dado un torneo competitivo finalizado con 1000000 de recaudo se reparte el
	// dinero en 600000, 300000 y 100000

	@Test
	public void hu_adm08_ca2_testRepartirPremios() throws Exception {
		Torneo competitivo = gestor.crearTorneo(admin, "Padel", "Intermedio", 12, Torneo.COMPETITIVO, Torneo.SENCILLOS,
				LocalDate.now().plusDays(7), 250000);
		gestor.inscribirExterno(admin, competitivo, "Externo1", "101", PersonaExterna.EXTERNO, null);
		gestor.inscribirExterno(admin, competitivo, "Externo2", "102", PersonaExterna.EXTERNO, null);
		gestor.inscribirExterno(admin, competitivo, "Externo3", "103", PersonaExterna.EXTERNO, null);
		gestor.inscribirExterno(admin, competitivo, "Externo4", "104", PersonaExterna.EXTERNO, null);
		gestor.iniciar(admin, competitivo);
		jugarTodos(competitivo);
		gestor.finalizar(admin, competitivo);
		List<InscripcionTorneo> clasificacion = competitivo.getClasificacion();
		assertEquals(600000, clasificacion.get(0).getPremio(), 0.001, "El primero deberia recibir 600.000");
		assertEquals(300000, clasificacion.get(1).getPremio(), 0.001, "El segundo deberia recibir 300.000");
		assertEquals(100000, clasificacion.get(2).getPremio(), 0.001, "El tercero deberia recibir 100.000");
		assertEquals(0, clasificacion.get(3).getPremio(), 0.001, "El cuarto no deberia recibir nada");
	}

	// En un recreativo en estado finalizado los tres primeros socios deben recibir
	// un bono de 20%, 15% y 10%
	@Test
	public void hu_adm08_ca4_testBonosRecreativo() throws Exception {
		Socio ana = new Socio("ana", "1005", 25);
		Socio luis = new Socio("luis", "1006", 27);
		Socio marta = new Socio("marta", "1007", 24);
		ana.inscribirEnDisciplina("Padel", "Intermedio");
		luis.inscribirEnDisciplina("Padel", "Intermedio");
		marta.inscribirEnDisciplina("Padel", "Intermedio");
		gestor.registrarSocio(ana);
		gestor.registrarSocio(luis);
		gestor.registrarSocio(marta);

		Torneo recreativo = gestor.crearTorneo(admin, "Padel", "Intermedio", 12, Torneo.RECREATIVO, Torneo.SENCILLOS,
				LocalDate.now().plusDays(7), 0);
		gestor.inscribirJugador(ana, recreativo, null);
		gestor.inscribirJugador(luis, recreativo, null);
		gestor.inscribirJugador(marta, recreativo, null);
		gestor.iniciar(admin, recreativo);
		jugarTodos(recreativo);
		gestor.finalizar(admin, recreativo);

		assertEquals(1, ana.getBonos().size(), "Ana deberia recibir un bono");
		assertEquals(1, luis.getBonos().size(), "Luis deberia recibir un bono");
		assertEquals(1, marta.getBonos().size(), "Marta deberia recibir un bono");

		List<Double> porcentajes = new ArrayList<>();
		porcentajes.add(ana.getBonos().get(0).getPorcentaje());
		porcentajes.add(luis.getBonos().get(0).getPorcentaje());
		porcentajes.add(marta.getBonos().get(0).getPorcentaje());
		Collections.sort(porcentajes);
		assertEquals(0.10, porcentajes.get(0), 0.001, "El tercer lugar deberia recibir 10%");
		assertEquals(0.15, porcentajes.get(1), 0.001, "El segundo lugar deberia recibir 15%");
		assertEquals(0.20, porcentajes.get(2), 0.001, "El primer lugar deberia recibir 20%");
	}

	// Un torneo con encuentros sin resultado no se puede finalizar
	@Test
	public void hu_adm08_ca5_testFinalizarSinResultados() throws Exception {
		Exception e = assertThrows(Exception.class, () -> gestor.finalizar(admin, torneo));
		assertTrue(e.getMessage().contains("faltan"), "El mensaje deberia indicar cuantos encuentros faltan");
		assertEquals(Torneo.EN_CURSO, torneo.getEstado(), "El torneo deberia seguir en curso");

	}

}
