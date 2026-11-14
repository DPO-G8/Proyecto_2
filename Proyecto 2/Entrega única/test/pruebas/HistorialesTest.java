package pruebas;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import controlador.Club;
import logica.deportes.DeporteConjunto;
import logica.deportes.DisciplinaIndividual;
import logica.deportes.Instalacion;
import logica.usuarios.Administrador;
import logica.usuarios.Entrenador;
import logica.usuarios.Socio;
import persistencia.RepositorioJSON;

public class HistorialesTest {
	@TempDir
	Path carpeta;

	private Club club;
	private DisciplinaIndividual padel;

	@BeforeEach
	public void setUp() throws Exception {
		club = new Club(new RepositorioJSON(carpeta.toString()));
		Administrador admin = new Administrador("admin", "1234");
		Entrenador entrenador = new Entrenador("entrenador1", "1", "COD-E1");
		club.getGestorUsuarios().agregarUsuario(admin);
		club.getGestorUsuarios().agregarUsuario(entrenador);
		Socio ana = club.getGestorUsuarios().registrarSocio("ana", "1001", 20);
		Socio cami = club.getGestorUsuarios().registrarSocio("cami", "1003", 22);

		padel = new DisciplinaIndividual("Padel", 2);
		padel.agregarInstalacion(new Instalacion(1, "Cancha Padel 1", 4, false, "Padel"));
		club.getGestorReservas().agregarDisciplina(padel);
		club.getGestorReservas().inscribirEnDisciplina(ana, "Padel", "Principiante");
		club.getGestorReservas().inscribirEnDisciplina(cami, "Padel", "Intermedio");

		club.getGestorEquipos().agregarDeporte(new DeporteConjunto("Futbol", 7));
		club.getGestorEquipos().crearEquipo(admin, "Futbol", "Millos", "Mayores", 15, "entrenador1");

		LocalDate manana = LocalDate.now().plusDays(1);
		club.getGestorReservas().reservar(ana, 1, manana, LocalTime.of(10, 0), LocalTime.of(11, 0), 4, "DOBLES");
		club.getGestorCompetencias().crearPartidoPractica(ana, "Padel", "Sencillos", LocalDate.now(), Arrays.asList("cami"));
		club.getGestorEntrenamientos().crearSesion(entrenador, manana, LocalTime.of(17, 0), 90, "Fisico", 1, "Millos");
	}

	@AfterEach
	public void tearDown() {
		club = null;
		padel = null;
	}

	@Test
	public void hu_his01_ca1_testHistorialDeReservas() {
		assertEquals(1, padel.getInstalaciones().get(0).getReservas().size(), "Deberia aparecer la reserva de ana en el historial");
	}

	@Test
	public void hu_his01_ca2_testHistorialDePartidos() {
		assertEquals(1, club.getGestorCompetencias().getPartidos().size(), "Deberia aparecer el partido de practica en el historial");
	}

	@Test
	public void hu_his01_ca3_testHistorialDeSesiones() {
		assertEquals(1, club.getGestorEntrenamientos().getSesiones().size(), "Deberia aparecer la sesion de entrenamiento en el historial");
	}
}
