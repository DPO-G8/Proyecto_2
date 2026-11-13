package consola;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import controlador.Club;
import logica.deportes.Partido;
import logica.deportes.PartidoCompetencia;
import logica.deportes.PartidoInterno;
import logica.deportes.PartidoPractica;
import logica.deportes.SesionEntrenamiento;
import logica.tienda.Articulo;
import logica.tienda.Bebida;
import logica.tienda.Producto;
import logica.tienda.Snack;
import logica.tienda.Sugerencia;
import logica.tienda.Venta;
import logica.torneos.Encuentro;
import logica.torneos.Torneo;
import logica.usuarios.Empleado;
import logica.usuarios.Entrenador;
import logica.usuarios.Turno;

public class MainEmpleado {
    private final Club club;
    private final Empleado empleado;
    private final Entrada entrada;

    public static void main(String[] args) {
        Arranque.iniciar("empleado", u -> u instanceof Empleado,
                s -> new MainEmpleado(s.getClub(), (Empleado) s.getUsuario(), s.getEntrada()).mostrar());
    }

    public MainEmpleado(Club club, Empleado empleado, Entrada entrada) {
        this.club = club;
        this.empleado = empleado;
        this.entrada = entrada;
    }

    public void mostrar() {
        Menu menu = new Menu("Empleado " + empleado.getLogin(), entrada)
                .opcion("Mis turnos y solicitudes de cambio", this::menuTurnos)
                .opcion("Proponer producto y ver mis sugerencias", this::menuSugerencias)
                .opcion("Comprar con mi descuento", this::comprar)
                .opcion("Compartir mi codigo de descuento", () -> {
                    club.getGestorUsuarios().compartirCodigo(empleado, entrada.texto("Login del socio"));
                    System.out.println("  Codigo compartido: el socio tendra 8% de descuento.");
                })
                .opcion("Torneos individuales", this::menuTorneos);
        if (empleado instanceof Entrenador) {
            menu.opcion("Entrenador: equipos, sesiones y partidos",
                    () -> new MenuEntrenador(club, (Entrenador) empleado, entrada).mostrar());
        }
        menu.mostrar();
    }

    private void menuTurnos() {
        new Menu("Turnos", entrada)
                .opcion("Ver mis turnos", () -> {
                    List<Turno> turnos = empleado.getTurnos();
                    for (int i = 0; i < turnos.size(); i++) {
                        System.out.println("  " + i + ". " + turnos.get(i).getDia() + " " + turnos.get(i).getHoraInicio()
                                + "-" + turnos.get(i).getHoraFin());
                    }
                })
                .opcion("Solicitar cambio de turno", () -> {
                    Turno t = entrada.elegir("Turno a cambiar:", empleado.getTurnos(),
                            x -> x.getDia() + " " + x.getHoraInicio() + "-" + x.getHoraFin());
                    club.getGestorTurnos().solicitarCambio(empleado, empleado.getTurnos().indexOf(t),
                            entrada.fecha("Fecha solicitada"), entrada.hora("Hora de inicio"), entrada.hora("Hora de fin"));
                    System.out.println("  Solicitud enviada (PENDIENTE).");
                })
                .mostrar();
    }

    private void menuSugerencias() {
        new Menu("Sugerencias de productos", entrada)
                .opcion("Proponer un producto", () -> {
                    int id = club.getGestorInventario().siguienteIdProducto();
                    String tipo = entrada.elegirTexto("Tipo:", "Articulo", "Bebida", "Snack");
                    String nombre = entrada.texto("Nombre");
                    double precio = entrada.decimal("Precio", 1);
                    Producto p;
                    if (tipo.equals("Articulo")) {
                        p = new Articulo(id, nombre, precio, entrada.texto("Categoria"), 0, "Bodega");
                    } else if (tipo.equals("Bebida")) {
                        p = new Bebida(id, nombre, precio, entrada.siNo("Es caliente"));
                    } else {
                        p = new Snack(id, nombre, precio, entrada.lista("Alergenos (o ninguno)"));
                    }
                    club.getGestorInventario().proponerProducto(empleado, p);
                    System.out.println("  Sugerencia enviada (PENDIENTE).");
                })
                .opcion("Ver mis sugerencias y su estado", () -> {
                    for (Sugerencia s : club.getGestorInventario().consultarSugerencias(empleado)) {
                        System.out.println("  " + MainSocio.producto(s.getProducto()) + " -> " + s.getEstado());
                    }
                })
                .mostrar();
    }

    private void comprar() throws Exception {
        Map<Integer, Integer> pedido = new LinkedHashMap<Integer, Integer>();
        boolean cafeteria = false;
        do {
            Producto p = entrada.elegir("Producto:", new java.util.ArrayList<Producto>(club.getGestorVentas().getProductos().values()), MainSocio::producto);
            if (p instanceof Snack) {
                System.out.println("  Aviso de alergenos: " + ((Snack) p).getAlergenos());
            }
            cafeteria = cafeteria || !(p instanceof Articulo);
            pedido.put(p.getIdProducto(), entrada.entero("Cantidad", 1, 1000));
        } while (entrada.siNo("Agregar otro producto"));
        double propina = cafeteria ? entrada.entero("Propina en % (sugerida 10)", 0, 100) / 100.0 : 0;
        int puntos = 0;
        if (empleado.getPuntosFidelidad() > 0 && entrada.siNo("Redimir puntos (tiene " + empleado.getPuntosFidelidad() + ")")) {
            puntos = entrada.entero("Puntos a redimir", 1, empleado.getPuntosFidelidad());
        }
        Venta v = club.getGestorVentas().comprar(empleado, pedido, propina, puntos);
        System.out.println("  Compra con 15% de descuento. Total: " + VistaTorneos.dinero(v.calcularTotal()));
    }

    private void menuTorneos() {
        new Menu("Torneos individuales", entrada)
                .opcion("Listar torneos", () -> VistaTorneos.listar(club, empleado))
                .opcion("Ver detalle e historial", () -> VistaTorneos.detalle(club, empleado, entrada))
                .opcion("Inscribirme (gratis, sin premio en efectivo)", () -> {
                    Torneo t = VistaTorneos.elegir(club, empleado, entrada);
                    club.getGestorTorneos().inscribirEmpleado(empleado, t);
                    System.out.println("  Inscrito como empleado.");
                })
                .opcion("Salirme de un torneo", () -> {
                    Torneo t = VistaTorneos.elegir(club, empleado, entrada);
                    club.getGestorTorneos().salirDeTorneo(empleado, t);
                    System.out.println("  Inscripcion retirada.");
                })
                .mostrar();
    }

    static String partido(Partido p) {
        String marcador = p.getMarcador().isEmpty() ? "sin resultado" : p.getMarcador();
        String que;
        if (p instanceof PartidoCompetencia) {
            PartidoCompetencia pc = (PartidoCompetencia) p;
            que = "Competencia: " + pc.getEquipo().getNombre() + " vs " + pc.getInstitucionRival() + " ("
                    + pc.getCompetencia().getNombre() + ", " + pc.getEscenario() + ")";
        } else if (p instanceof PartidoInterno) {
            PartidoInterno pi = (PartidoInterno) p;
            que = "Interno: " + pi.getLocal().getNombre() + " vs " + pi.getVisitante().getNombre();
        } else {
            PartidoPractica pp = (PartidoPractica) p;
            que = "Practica " + pp.getDisciplina().getNombre() + " " + pp.getModalidad() + ": " + pp.getJugadores().size()
                    + " jugadores";
        }
        return p.getFecha() + " " + que + " - " + marcador;
    }

    static String sesion(SesionEntrenamiento s) {
        String grupo = s.getEquipo() != null ? "equipo " + s.getEquipo().getNombre() : "sin equipo";
        String lugar = s.getInstalacion() != null ? s.getInstalacion().getNombre() : "sin instalacion";
        return s.getFecha() + " " + s.getHora() + " (" + s.getDuracion() + " min) " + s.getTipo() + " - "
                + s.getEntrenador().getLogin() + ", " + grupo + " en " + lugar + ", " + s.getAsistencias().size()
                + " asistencias";
    }
}