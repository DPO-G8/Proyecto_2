package controlador;
import java.time.LocalDate;
import java.util.List;

import logica.torneos.Torneo;
import logica.usuarios.Usuario;
import logica.torneos.InscripcionTorneo;

public class GestorTorneos {
	public Torneo crearTorneo(Usuario quien, String disciplina, String categoriaONivel, int cupos, String tipo, String modalidad, LocalDate fecha, double tarifa) throws Exception{
		throw new UnsupportedOperationException();
	}
	public List<Torneo> listar(){
		throw new UnsupportedOperationException();
	}
	public InscripcionTorneo inscribirExterno(Usuario quien, Torneo t, String nombre, String documento, String tipo, String codigo) throws Exception {
		throw new UnsupportedOperationException();
	}

}
