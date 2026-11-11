package controlador;

import java.util.ArrayList;
import java.util.Map;

import logica.tienda.Articulo;
import logica.tienda.Producto;
import logica.tienda.Snack;
import logica.tienda.Sugerencia;
import logica.usuarios.Administrador;
import logica.usuarios.Empleado;
import logica.usuarios.Usuario;
import persistencia.RepositorioDatos;

public class GestorInventario {

    private final RepositorioDatos repositorio;
    private final Map<String, Usuario> usuarios;
    private final GestorVentas gestorVentas;

    public GestorInventario(RepositorioDatos repositorio, Map<String, Usuario> usuarios, GestorVentas gestorVentas)
            throws Exception {
        this.repositorio = repositorio;
        this.usuarios = usuarios;
        this.gestorVentas = gestorVentas;
        repositorio.cargarSugerencias(usuarios);
    }

    // RF4 Administrador
    public Producto consultarProducto(Usuario quien, int idProducto) throws Exception {
        exigirAdministrador(quien);
        return gestorVentas.buscarProducto(idProducto);
    }

    public ArrayList<Producto> listarProductos(Usuario quien) throws Exception {
        exigirAdministrador(quien);
        return new ArrayList<Producto>(gestorVentas.getProductos().values());
    }

    // RF5 Administrador
    public void reponerInventario(Usuario quien, int idProducto, int cantidad) throws Exception {
        exigirAdministrador(quien);
        Producto p = gestorVentas.buscarProducto(idProducto);
        if (!(p instanceof Articulo)) {
            throw new Exception("'" + p.getNombre() + "' no es un articulo con inventario");
        }
        ((Articulo) p).reponer(cantidad);
        repositorio.guardarProductos(gestorVentas.getProductos().values());
    }

    // RF5 Administrador: mover un articulo a otra ubicacion del inventario
    public void moverArticulo(Usuario quien, int idProducto, String ubicacion) throws Exception {
        exigirAdministrador(quien);
        Producto p = gestorVentas.buscarProducto(idProducto);
        if (!(p instanceof Articulo)) {
            throw new Exception("'" + p.getNombre() + "' no es un articulo con ubicacion en inventario");
        }
        if (ubicacion == null || ubicacion.trim().isEmpty()) {
            throw new Exception("La nueva ubicacion no puede estar vacia");
        }
        ((Articulo) p).setUbicacion(ubicacion.trim());
        repositorio.guardarProductos(gestorVentas.getProductos().values());
    }

    // Siguiente id libre
    public int siguienteIdProducto() {
        int max = 0;
        for (Integer id : gestorVentas.getProductos().keySet()) {
            max = Math.max(max, id);
        }
        for (Usuario u : usuarios.values()) {
            if (u instanceof Empleado) {
                for (Sugerencia s : ((Empleado) u).getSugerencias()) {
                    max = Math.max(max, s.getProducto().getIdProducto());
                }
            }
        }
        return max + 1;
    }

    // RF2 Empleado
    public Sugerencia proponerProducto(Usuario quien, Producto producto) throws Exception {
        if (!(quien instanceof Empleado)) {
            throw new Exception("Solo un empleado puede proponer productos");
        }
        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            throw new Exception("El producto necesita un nombre");
        }
        if (producto.getPrecio() <= 0) {
            throw new Exception("El producto necesita un precio positivo");
        }
        if (producto instanceof Snack && ((Snack) producto).getAlergenos() == null) {
            throw new Exception("Un snack debe registrar sus alergenos");
        }
        if (producto instanceof Articulo && ((Articulo) producto).getCategoria().trim().isEmpty()) {
            throw new Exception("Un articulo necesita categoria");
        }
        if (gestorVentas.getProductos().containsKey(producto.getIdProducto())
                || existeSugerencia(producto.getIdProducto())) {
            throw new Exception("El id " + producto.getIdProducto() + " ya esta en uso");
        }
        Sugerencia sugerencia = new Sugerencia(producto);
        ((Empleado) quien).agregarSugerencia(sugerencia);
        repositorio.guardarSugerencias(usuarios.values());
        return sugerencia;
    }

    public ArrayList<Sugerencia> sugerenciasPendientes(Usuario quien) throws Exception {
        exigirAdministrador(quien);
        ArrayList<Sugerencia> resultado = new ArrayList<Sugerencia>();
        for (Usuario u : usuarios.values()) {
            if (u instanceof Empleado) {
                for (Sugerencia s : ((Empleado) u).getSugerencias()) {
                    if (s.getEstado().equals(Sugerencia.PENDIENTE)) {
                        resultado.add(s);
                    }
                }
            }
        }
        return resultado;
    }

    // RF2 Administrador
    public void resolverSugerencia(Usuario quien, Sugerencia sugerencia, boolean aprobar) throws Exception {
        exigirAdministrador(quien);
        if (aprobar) {
            sugerencia.aprobar();
            gestorVentas.agregarProducto(sugerencia.getProducto());
            ((Administrador) quien).agregarSugerenciaAprobada(sugerencia);
        } else {
            sugerencia.rechazar();
        }
        repositorio.guardarSugerencias(usuarios.values());
    }

    private boolean existeSugerencia(int id) {
        for (Usuario u : usuarios.values()) {
            if (u instanceof Empleado) {
                for (Sugerencia s : ((Empleado) u).getSugerencias()) {
                    if (s.getProducto().getIdProducto() == id) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void exigirAdministrador(Usuario quien) throws Exception {
        if (!(quien instanceof Administrador)) {
            throw new Exception("Solo un administrador puede hacer esta operacion");
        }
    }
}
