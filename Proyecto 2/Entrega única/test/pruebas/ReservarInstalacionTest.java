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
import logica.deportes.DisciplinaIndividual;
import logica.deportes.Instalacion;
import logica.deportes.Reserva;
import logica.usuarios.Socio;
import persistencia.RepositorioJSON;

public class ReservarInstalacionTest {
	//Se crea carpeta temporal para realizar las pruebas
	@TempDir
	Path carpeta;
	
	private Club club;
	private Socio ana;
	private Socio luis;
	private LocalDate manana;
	
	@BeforeEach
	public void setUp() throws Exception {
		club = new Club(new RepositorioJSON(carpeta.toString()));
		ana = club.getGestorUsuarios().registrarSocio("ana", "123", 20);
		luis = club.getGestorUsuarios().registrarSocio("luis", "123", 25);
		DisciplinaIndividual padel = new DisciplinaIndividual("Padel", 1);
		padel.agregarInstalacion(new Instalacion(1, "Cancha Padel 1", 4, false, "Padel"));
		club.getGestorReservas().agregarDisciplina(padel);
		manana = LocalDate.now().plusDays(1);
	}
	
	@AfterEach
	public void tearDown() {
		club = null;
		ana = null;
		luis = null;
		manana = null;
	}
	//Si hay disponibilidad la reservar debe quedar registrada
	@Test
	public void hu_soc01_ca1_testReservarInstalacion() throws Exception {
		Reserva reserva = club.getGestorReservas().reservar(ana, 1, manana, LocalTime.of(10, 0), LocalTime.of(11, 0), 4, Instalacion.DOBLES);
		assertEquals(ana, reserva.getSocio(), "La reserva deberia ser de ana");
		assertEquals(1, club.getGestorReservas().getInstalacion(1).getReservas().size(), "La reserva deberia quedar en la instalacion");
	}

	//Si la instalacion ya esta ocupada se rechaza por disponibilidad
	@Test
	public void hu_soc01_ca2_testReservarSinDisponibilidad() throws Exception {
		club.getGestorReservas().reservar(ana, 1, manana, LocalTime.of(10, 0), LocalTime.of(11, 0), 4, Instalacion.DOBLES);
		Exception e = assertThrows(Exception.class, () -> club.getGestorReservas().reservar(luis, 1, manana, LocalTime.of(10, 0), LocalTime.of(11, 0), 4, Instalacion.DOBLES));
		assertTrue(e.getMessage().contains("disponibilidad"), "El mensaje deberia indicar que no hay disponibilidad");
		assertEquals(1, club.getGestorReservas().getInstalacion(1).getReservas().size(), "No deberia guardarse la segunda reserva");
	}

	//El padel siempre se debe jugar en dobles
	@Test
	public void hu_soc01_ca3_testReservarPadelConDosJugadores() throws Exception {
		Exception e = assertThrows(Exception.class, () -> club.getGestorReservas().reservar(ana, 1, manana, LocalTime.of(10, 0), LocalTime.of(11, 0), 2, Instalacion.SENCILLOS));
		assertTrue(e.getMessage().contains("no admite"), "El mensaje deberia indicar que la instalacion no admite 2 jugadores");
		assertEquals(0, club.getGestorReservas().getInstalacion(1).getReservas().size(), "No deberia guardarse ninguna reserva");
	}
	

}
