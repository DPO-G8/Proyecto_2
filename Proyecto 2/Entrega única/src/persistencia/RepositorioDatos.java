package persistencia;
import java.util.Collection;
import java.util.Map;
import logica.usuarios.Usuario;
import java.util.ArrayList;
import logica.deportes.Competencia;
import logica.deportes.DeporteConjunto;
import logica.deportes.Partido;
import logica.deportes.SesionEntrenamiento;
import logica.tienda.Producto;
import logica.tienda.Venta;
import logica.deportes.DisciplinaIndividual;

public interface RepositorioDatos {
	Map<String, Usuario> cargarUsuarios() throws Exception;
	void guardarUsuarios(Collection<Usuario> usuarios) throws Exception;

	ArrayList<DisciplinaIndividual> cargarDisciplinas(Map<String, Usuario> usuarios) throws Exception;

	void guardarDisciplinas(Collection<DisciplinaIndividual> disciplinas) throws Exception;

	ArrayList<Producto> cargarProductos() throws Exception;

	void guardarProductos(Collection<Producto> productos) throws Exception;

	ArrayList<Venta> cargarVentas(Map<String, Usuario> usuarios, Map<Integer, Producto> productos) throws Exception;

	void guardarVentas(Collection<Venta> ventas) throws Exception;

	// ---- deportes de conjunto con sus equipos, plantillas y entrenadores
	ArrayList<DeporteConjunto> cargarDeportes(Map<String, Usuario> usuarios) throws Exception;

	void guardarDeportes(Collection<DeporteConjunto> deportes, Collection<Usuario> usuarios) throws Exception;

	// ---- competencias con sus inscripciones de equipos
	ArrayList<Competencia> cargarCompetencias(ArrayList<DeporteConjunto> deportes, Map<String, Usuario> usuarios)
			throws Exception;

	void guardarCompetencias(Collection<Competencia> competencias) throws Exception;

	// ---- partidos (competencia, internos y de practica) con sus estadisticas
	ArrayList<Partido> cargarPartidos(Map<String, Usuario> usuarios, ArrayList<DeporteConjunto> deportes,
			ArrayList<Competencia> competencias, ArrayList<DisciplinaIndividual> disciplinas) throws Exception;

	void guardarPartidos(Collection<Partido> partidos) throws Exception;

	// ---- sesiones de entrenamiento con sus asistencias
	ArrayList<SesionEntrenamiento> cargarSesiones(Map<String, Usuario> usuarios, ArrayList<DeporteConjunto> deportes,
			ArrayList<DisciplinaIndividual> disciplinas) throws Exception;

	void guardarSesiones(Collection<SesionEntrenamiento> sesiones) throws Exception;

	// ---- turnos y solicitudes de cambio: se cargan dentro de los empleados
	void cargarTurnos(Map<String, Usuario> usuarios) throws Exception;

	void guardarTurnos(Collection<Usuario> usuarios) throws Exception;

	// ---- sugerencias de productos: se cargan dentro de los empleados
	void cargarSugerencias(Map<String, Usuario> usuarios) throws Exception;

	void guardarSugerencias(Collection<Usuario> usuarios) throws Exception;
}
