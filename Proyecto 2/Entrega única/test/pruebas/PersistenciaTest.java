package pruebas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import controlador.Club;
import logica.deportes.Competencia;
import logica.deportes.DeporteConjunto;
import logica.deportes.Equipo;
import logica.deportes.PartidoCompetencia;
import logica.tienda.Articulo;
import logica.usuarios.Administrador;
import logica.usuarios.Entrenador;
import logica.usuarios.Socio;
import logica.usuarios.Usuario;
import persistencia.RepositorioJSON;

public class PersistenciaTest {
	//Se crea carpeta temporal para hacer las pruebas
		@TempDir
		Path carpeta;

		private Club club;
		private Entrenador entrenador;

		@BeforeEach
		public void setUp() throws Exception {
			club = new Club(new RepositorioJSON(carpeta.toString()));
			Administrador admin = new Administrador("admin", "1234");
			entrenador = new Entrenador("entrenador1", "1", "COD-E1");
			club.getGestorUsuarios().agregarUsuario(admin);
			club.getGestorUsuarios().agregarUsuario(entrenador);
			DeporteConjunto futbol = new DeporteConjunto("Futbol", 7);
			club.getGestorEquipos().agregarDeporte(futbol);
			club.getGestorEquipos().crearEquipo(admin, "Futbol", "Millos", "Mayores", 15, "entrenador1");
			for (int i = 1; i <= 8; i++) {
				club.getGestorUsuarios().registrarSocio("jugador" + i, "1", 22);
				club.getGestorEquipos().agregarJugador(admin, "Millos", "jugador" + i, "Delantero", i);
			}
			club.getGestorCompetencias().agregarCompetencia(new Competencia("Torneo de la casa", "Universidades de Bogota", "2026", "Mayores", "Liga", 7, futbol));
			club.getGestorVentas().agregarProducto(new Articulo(1, "Raqueta", 100000, "Raquetas", 5, "Estante A"));
		}

		@AfterEach
		public void tearDown() {
			club = null;
			entrenador = null;
		}
		
		//Se abre un club nuevo sobre la misma carpeta a la hora de volver a iniciar el programa
		private Club reabrir() throws Exception{
			return new Club(new RepositorioJSON(carpeta.toString()));
		}
		//Sistema RF1 los usuarios se deben recuperar al iniciar
		@Test
		public void hu_sis01_ca1_testUsuariosSeRecuperan() throws Exception {
			Club otro = reabrir();
			Usuario usuario = otro.getGestorUsuarios().autenticar("jugador1", "1");
			assertEquals("jugador1", usuario.getLogin());
		}
		//Una compra debe cambiar el inventario y quedar guardada
		@Test
		public void hu_sis01_ca2_testCompraSeRecupera() throws Exception {
			Socio jugador1 = (Socio) club.getGestorUsuarios().getUsuarios().get("jugador1");
			Map<Integer, Integer> pedido = new LinkedHashMap<Integer, Integer>();
			pedido.put(1, 2);
			club.getGestorVentas().comprar(jugador1, pedido);
			Club otro = reabrir();
			assertEquals(1, otro.getGestorVentas().getVentas().size());
			assertEquals(3, ((Articulo) otro.getGestorVentas().getProductos().get(1)).getCantidadDisponible());
		}
		//El equipo, la platilla y el entrenador deben recuperarse
		@Test
		public void hu_sis01_ca3_testEquipoSeRecupera() throws Exception {
			Club otro = reabrir();
			Equipo millos = otro.getGestorEquipos().buscarEquipo("Millos");
			Entrenador entrenadorRecuperado = (Entrenador) otro.getGestorUsuarios().getUsuarios().get("entrenador1");
			assertEquals(8, millos.getPlantilla().size());
			assertTrue(entrenadorRecuperado.dirige(millos));
		}
		
		//El resultado registrado de algun dato debe seguir contando en la tabla
		@Test
		public void hu_sis01_ca4_testResultadoSeRecupera() throws Exception {
			List<String> nomina = new ArrayList<String>();
			for (int i = 1; i <= 8; i++) {
				nomina.add("jugador" + i);
			}
			club.getGestorCompetencias().inscribirEquipo(entrenador, "Millos", "Torneo de la casa", nomina);
			PartidoCompetencia partido = club.getGestorCompetencias().programarPartido(entrenador, "Millos","Torneo de la casa", LocalDate.now(), "Santafe FC", "El campinucho", "Grupos");
			club.getGestorCompetencias().registrarResultado(entrenador, partido, 2, 1);
			Club otro = reabrir();
			Map<Equipo, Integer> tabla = otro.getGestorCompetencias().tablaPosiciones("Torneo de la casa");
			Equipo millos = otro.getGestorEquipos().buscarEquipo("Millos");
			assertEquals(3, tabla.get(millos).intValue());
		}
		

}
