package pruebas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import controlador.Club;
import controlador.GestorTurnos;
import logica.usuarios.Administrador;
import logica.usuarios.Empleado;
import logica.usuarios.Entrenador;
import logica.usuarios.SolicitudCambioTurno;
import persistencia.RepositorioJSON;


public class TurnosTest {
	//Se crea carpeta temporal para hacer las pruebas
		@TempDir
		Path carpeta;

		private Club club;
		private Administrador admin;
		private Empleado empleado;
		private Entrenador entrenador1;
		private Entrenador entrenador2;
		private LocalDate manana;

		@BeforeEach
		public void setUp() throws Exception {
			club = new Club(new RepositorioJSON(carpeta.toString()));
			admin = new Administrador("admin", "1234");
			empleado = new Empleado("empleado1", "1", "COD-M1");
			entrenador1 = new Entrenador("entrenador1", "1", "COD-E1");
			entrenador2 = new Entrenador("entrenador2", "1", "COD-E2");
			club.getGestorUsuarios().agregarUsuario(admin);
			club.getGestorUsuarios().agregarUsuario(empleado);
			club.getGestorUsuarios().agregarUsuario(entrenador1);
			club.getGestorUsuarios().agregarUsuario(entrenador2);
			manana = LocalDate.now().plusDays(1);
		}

		@AfterEach
		public void tearDown() {
			club = null;
			admin = null;
			empleado = null;
			entrenador1 = null;
			entrenador2 = null;
			manana = null;
		}
		
		//Admin RF8 administrar los turnos de los empleados
		@Test public void hu_tur01_ca1_testAsignarTurno() throws Exception{
			club.getGestorTurnos().asignarTurno(admin, "empleado1", "Martes", LocalTime.of(8,0),LocalTime.of(12, 0));
			assertEquals(1,empleado.getTurnos().size());
		}
		//Se intenta asignar un turno que se cruza
		@Test
		public void hu_tur01_ca2_testAsignarTurnoQueSeCruza() throws Exception {
			club.getGestorTurnos().asignarTurno(admin, "empleado1", "Martes", LocalTime.of(8, 0), LocalTime.of(12, 0));
			Exception e = assertThrows(Exception.class, () ->club.getGestorTurnos().asignarTurno(admin, "empleado1", "Martes", LocalTime.of(11, 0), LocalTime.of(13, 0)));
			assertTrue(e.getMessage().contains("cruza"));
			assertEquals(1, empleado.getTurnos().size());
		}
		//Empleado RF1 no se puede pedir un cambio de turno para una fecha que ya paso
		@Test
		public void hu_tur02_ca1_testSolicitarCambioFechaPasada() throws Exception {
			club.getGestorTurnos().asignarTurno(admin, "empleado1", "Martes", LocalTime.of(8, 0), LocalTime.of(12, 0));
			Exception e = assertThrows(Exception.class, () -> club.getGestorTurnos().solicitarCambio(empleado, 0, LocalDate.now().minusDays(1),LocalTime.of(9, 0), LocalTime.of(13, 0)));
			assertTrue(e.getMessage().contains("paso"));
		}
		// Admin RF1 puede aprobar una solicitud de cambio de turno
		@Test
		public void hu_tur03_ca1_testAprobarCambioDeEmpleado() throws Exception {
			club.getGestorTurnos().asignarTurno(admin, "empleado1", "Martes", LocalTime.of(8, 0), LocalTime.of(12, 0));
			SolicitudCambioTurno solicitud = club.getGestorTurnos().solicitarCambio(empleado, 0, manana,LocalTime.of(9, 0), LocalTime.of(13, 0));
			club.getGestorTurnos().resolver(admin, solicitud, true);
			assertEquals(GestorTurnos.APROBADA, solicitud.getEstado());
		}

		// Admin RF1 se hace un rechazo automatico si quedan menos de 2 entrenadores
		@Test
		public void hu_tur03_ca2_testRechazoAutomaticoPorFaltaDeEntrenadores() throws Exception {
			club.getGestorTurnos().asignarTurno(admin, "entrenador1", "Lunes", LocalTime.of(8, 0), LocalTime.of(12, 0));
			club.getGestorTurnos().asignarTurno(admin, "entrenador2", "Lunes", LocalTime.of(8, 0), LocalTime.of(12, 0));
			SolicitudCambioTurno solicitud = club.getGestorTurnos().solicitarCambio(entrenador1, 0, manana,LocalTime.of(9, 0), LocalTime.of(13, 0));
			Exception e = assertThrows(Exception.class, () -> club.getGestorTurnos().resolver(admin, solicitud, true));
			assertTrue(e.getMessage().contains("automaticamente"));
			assertEquals(GestorTurnos.RECHAZADA, solicitud.getEstado());
		}

	

}
