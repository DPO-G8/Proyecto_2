package pruebas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import controlador.GestorTorneos;
import logica.torneos.Torneo;
import logica.usuarios.Administrador;
import logica.usuarios.Entrenador;
import logica.usuarios.Turno;

public class InscribirEmpleadoTest {
	private Administrador admin;
	private GestorTorneos gestor;
	private LocalDate lunes;
	private Torneo torneo;
	private Entrenador libre;
	private Entrenador deTurno;
	
	@BeforeEach
	public void setUp() throws Exception {
		admin = new Administrador("admin", "9876");
		gestor = new GestorTorneos();
		lunes = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
		torneo = gestor.crearTorneo(admin, "Padel", "Intermedio", 12, Torneo.COMPETITIVO, Torneo.SENCILLOS, lunes, 5000);
		libre = new Entrenador("entrenador1", "1", "COD-E1");
		deTurno = new Entrenador("entrenador2", "1", "COD-E2");
		deTurno.agregarTurno(new Turno("Lunes", LocalTime.of(8, 0), LocalTime.of(12, 0)));
	}
	
	@AfterEach
	public void tearDown() {
		admin = null;
		gestor = null;
		lunes = null;
		torneo = null;
		libre = null;
		deTurno = null;
	}
	// Un empleado que no tiene tuerno ese dia se inscribe a un torneo competitivo sin tener que pagar y sin sumar al recaudo
	@Test
	public void hu_emp01_ca2_testInscribirEmpleadoCompetitivoSinCobrar() throws Exception{
		gestor.inscribirEmpleado(libre, torneo);
		assertEquals(1, torneo.cuposOcupados(),"La inscripcion deberia ocupar un cupo");
		assertEquals(0,torneo.totalRecaudado(),0.001,"El empleado no paga tarifa ni suma al recaudo");
	}
	
	// Un empleado con turno el dia del torneo no debe poderse inscribir
	@Test
	public void hu_emp01_ca3_testInscribirEmpleadoCompetitoDiaTurno() throws Exception{
		Exception e = assertThrows(Exception.class, ()-> gestor.inscribirEmpleado(deTurno, torneo));
		assertTrue(e.getMessage().contains("de turno"), "El mensaje deberia indicar que esta de turno");
		assertEquals(0,torneo.cuposOcupados(),"No se deberian ocupar cupos");
	}
	//Un empleado solo puede inscribirse a torneos que sean individuales
	@Test
	public void hu_emp01_ca4_testInscribirEmpleadoConjunto() throws Exception{
		Torneo conjunto = gestor.crearTorneo(admin, "Futbol", "Mayores", 12, Torneo.RECREATIVO, null, lunes, 0);
		Exception e = assertThrows(Exception.class, ()-> gestor.inscribirEmpleado(libre, conjunto));
		assertTrue(e.getMessage().contains("individuales"), "El mensaje deberia indicar que solo pueden inscribirse en torneos individuales");
		assertEquals(0,conjunto.cuposOcupados(),"No se deberian ocupar cupos");
	}

}
