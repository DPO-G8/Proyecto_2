package controlador;
import java.time.LocalDate;
import java.util.List;

import logica.torneos.Torneo;
import logica.usuarios.Usuario;
import logica.usuarios.Socio;
import logica.torneos.InscripcionTorneo;
import logica.torneos.Encuentro;

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
	public void iniciar(Usuario quien, Torneo t) throws Exception {
		throw new UnsupportedOperationException();
	}
	public void registrarResultado(Usuario quien, Torneo t, Encuentro e, int marcadorA, int marcadorB) throws Exception {
		throw new UnsupportedOperationException();
	}
	public void finalizar(Usuario quien, Torneo t) throws Exception {
		throw new UnsupportedOperationException();
	}
	public void retirarExterno(Usuario quien, Torneo t, String documento) throws Exception {
		throw new UnsupportedOperationException();
	}
	public void registrarSocio(Socio s) {
		throw new UnsupportedOperationException();
	}
	public InscripcionTorneo inscribirJugador(Usuario quien, Torneo t, String loginPareja) throws Exception {
		throw new UnsupportedOperationException();
	}
	public void salirDeTorneo(Usuario quien, Torneo t) throws Exception{
		throw new UnsupportedOperationException();
	}
	public Torneo buscarTorneo(int id) throws Exception{
		throw new UnsupportedOperationException();
	}
	
}
