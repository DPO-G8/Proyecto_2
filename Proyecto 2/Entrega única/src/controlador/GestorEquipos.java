package controlador;

import java.util.ArrayList;
import java.util.Map;

import logica.deportes.DeporteConjunto;
import logica.deportes.Equipo;
import logica.deportes.InscripcionCompetencia;
import logica.deportes.InscripcionEquipo;
import logica.Texto;
import logica.usuarios.Entrenador;
import logica.usuarios.Socio;
import logica.usuarios.Usuario;
import persistencia.RepositorioDatos;

public class GestorEquipos {

    private final RepositorioDatos repositorio;
    private final Map<String, Usuario> usuarios;
    private final ArrayList<DeporteConjunto> deportes;

    public GestorEquipos(RepositorioDatos repositorio, Map<String, Usuario> usuarios) throws Exception {
        this.repositorio = repositorio;
        this.usuarios = usuarios;
        this.deportes = repositorio.cargarDeportes(usuarios);
    }

    public ArrayList<DeporteConjunto> getDeportes() {
        return deportes;
    }

    // Dato inicial
    public void agregarDeporte(DeporteConjunto deporte) throws Exception {
        for (DeporteConjunto d : deportes) {
            if (Texto.iguales(d.getNombre(), deporte.getNombre())) {
                throw new Exception("Ya existe el deporte " + deporte.getNombre());
            }
        }
        deportes.add(deporte);
        guardar();
    }

    public Equipo buscarEquipo(String nombre) throws Exception {
        for (DeporteConjunto d : deportes) {
            for (Equipo e : d.getEquipos()) {
                if (Texto.iguales(e.getNombre(), nombre)) {
                    return e;
                }
            }
        }
        throw new Exception("No existe el equipo '" + nombre + "'");
    }

    public DeporteConjunto buscarDeporte(String nombre) throws Exception {
        for (DeporteConjunto d : deportes) {
            if (Texto.iguales(d.getNombre(), nombre)) {
                return d;
            }
        }
        throw new Exception("No existe el deporte '" + nombre + "'");
    }

    // RF6 Administrador
    public Equipo crearEquipo(Usuario quien, String nombreDeporte, String nombre, String categoria, int cupoMaximo,
            String loginEntrenador) throws Exception {
        exigirAdministrador(quien);
        if (nombre == null || nombre.trim().isEmpty() || categoria == null || categoria.trim().isEmpty()) {
            throw new Exception("El equipo necesita nombre y categoria");
        }
        if (cupoMaximo <= 0) {
            throw new Exception("El cupo maximo debe ser positivo");
        }
        DeporteConjunto deporte = buscarDeporte(nombreDeporte);
        boolean existe = true;
        try {
            buscarEquipo(nombre);
        } catch (Exception e) {
            existe = false;
        }
        if (existe) {
            throw new Exception("Ya existe un equipo llamado '" + nombre + "'");
        }
        Usuario entrenador = usuarios.get(loginEntrenador);
        if (!(entrenador instanceof Entrenador)) {
            throw new Exception("No existe un entrenador con login '" + loginEntrenador + "'");
        }
        Equipo equipo = new Equipo(nombre, categoria, cupoMaximo, deporte);
        deporte.agregarEquipo(equipo);
        ((Entrenador) entrenador).agregarEquipo(equipo);
        guardar();
        return equipo;
    }

    // RF6 Administrador
    public void eliminarEquipo(Usuario quien, String nombre) throws Exception {
        exigirAdministrador(quien);
        Equipo equipo = buscarEquipo(nombre);
        if (!equipo.getInscripcionesCompetencia().isEmpty()) {
            throw new Exception("No se puede eliminar '" + nombre + "': esta inscrito en una competencia");
        }
        equipo.getDeporte().getEquipos().remove(equipo);
        for (Usuario u : usuarios.values()) {
            if (u instanceof Entrenador) {
                ((Entrenador) u).getEquipos().remove(equipo);
            }
        }
        guardar();
    }

    // RF7 Administrador / RF11 Socio
    public void agregarJugador(Usuario quien, String nombreEquipo, String loginSocio, String posicion,
            int numeroCamiseta) throws Exception {
        Usuario destino = usuarios.get(loginSocio);
        if (!(destino instanceof Socio)) {
            throw new Exception("No existe un socio con login '" + loginSocio + "'");
        }
        if (!(quien instanceof logica.usuarios.Administrador) && quien != destino) {
            throw new Exception("Solo un administrador o el propio socio puede inscribirlo en un equipo");
        }
        Equipo equipo = buscarEquipo(nombreEquipo);
        equipo.agregarJugador(new InscripcionEquipo((Socio) destino, posicion, numeroCamiseta));
        guardar();
    }

    // RF7 Administrador
    public void quitarJugador(Usuario quien, String nombreEquipo, String loginSocio) throws Exception {
        exigirAdministrador(quien);
        Equipo equipo = buscarEquipo(nombreEquipo);
        InscripcionEquipo encontrada = null;
        for (InscripcionEquipo i : equipo.getPlantilla()) {
            if (i.getSocio().getLogin().equals(loginSocio)) {
                encontrada = i;
            }
        }
        if (encontrada == null) {
            throw new Exception(loginSocio + " no pertenece al equipo " + nombreEquipo);
        }
        equipo.getPlantilla().remove(encontrada);
        guardar();
    }

    private void exigirAdministrador(Usuario quien) throws Exception {
        if (quien == null || !quien.puedeRegistrarEquipo()) {
            throw new Exception("Solo un administrador puede hacer esta operacion");
        }
    }

    private void guardar() throws Exception {
        repositorio.guardarDeportes(deportes, usuarios.values());
    }
}
