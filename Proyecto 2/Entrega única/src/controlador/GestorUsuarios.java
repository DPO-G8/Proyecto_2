package controlador;
import java.util.Map;

import logica.usuarios.Empleado;
import logica.usuarios.Socio;
import logica.usuarios.Usuario;
import persistencia.RepositorioDatos;
public class GestorUsuarios {
	private final RepositorioDatos repositorio;
    private final Map<String, Usuario> usuarios;
//Ctor
    public GestorUsuarios(RepositorioDatos repositorio) throws Exception {
        this.repositorio = repositorio;
        this.usuarios = repositorio.cargarUsuarios();
    }
//Metodos
    public Map<String, Usuario> getUsuarios() {
        return usuarios;
    }
    public Usuario autenticar (String login, String password) throws Exception {
    	Usuario usuario = usuarios.get(login);
        if (usuario == null) {
            throw new Exception("No existe un usuario con el login '" + login + "'");
        }
        if (!usuario.getPassword().equals(password)) {
            throw new Exception("La contraseña es incorrecta");
        }
        return usuario;
    }
    public Socio registrarSocio(String login, String password, int edad) throws Exception {
        if (usuarios.containsKey(login)) {
            throw new Exception("Ya existe un usuario con el login '" + login + "'");
        }
        Socio socio = new Socio(login, password, edad);
        usuarios.put(login, socio);
        repositorio.guardarUsuarios(usuarios.values());
        return socio;
    }

    // Dato inicial
    public void agregarUsuario(Usuario usuario) throws Exception {
        if (usuarios.containsKey(usuario.getLogin())) {
            throw new Exception("Ya existe un usuario con el login '" + usuario.getLogin() + "'");
        }
        usuarios.put(usuario.getLogin(), usuario);
        repositorio.guardarUsuarios(usuarios.values());
    }

    // RF4 Empleado
    public void compartirCodigo(Usuario quien, String loginSocio) throws Exception {
        if (!(quien instanceof Empleado)) {
            throw new Exception("Solo un empleado puede compartir su codigo de descuento");
        }
        Usuario destino = usuarios.get(loginSocio);
        if (!(destino instanceof Socio)) {
            throw new Exception("No existe un socio con login '" + loginSocio + "'");
        }
        Socio socio = (Socio) destino;
        if (socio.tieneDescuentoCompartido()) {
            throw new Exception(loginSocio + " ya tiene un codigo de descuento compartido");
        }
        socio.setEmpleadoDescuento((Empleado) quien);
        repositorio.guardarUsuarios(usuarios.values());
    }

    // RF2 Socio
    public String estadoDe(Socio socio) {
        if (socio.estaSuspendido()) {
            return "SUSPENDIDO hasta " + socio.getFechaFinSuspension();
        }
        return "HABILITADO";
    }
}
