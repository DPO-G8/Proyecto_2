package consola;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import controlador.Club;
import controlador.GestorTorneos;
import logica.deportes.DeporteConjunto;
import logica.deportes.DisciplinaIndividual;
import logica.deportes.Equipo;
import logica.deportes.InscripcionDisciplina;
import logica.deportes.InscripcionEquipo;
import logica.deportes.Instalacion;
import logica.deportes.Partido;
import logica.deportes.PartidoInterno;
import logica.deportes.Reserva;
import logica.deportes.SesionEntrenamiento;
import logica.tienda.Articulo;
import logica.tienda.Producto;
import logica.tienda.Sugerencia;
import logica.tienda.Venta;
import logica.torneos.Encuentro;
import logica.torneos.PersonaExterna;
import logica.torneos.Torneo;
import logica.torneos.TorneoConjunto;
import logica.usuarios.Administrador;
import logica.usuarios.Empleado;
import logica.usuarios.SolicitudCambioTurno;
import logica.usuarios.Turno;
import logica.usuarios.Usuario;

public class MainAdministrador {
    private final Club club;
    private final Usuario admin;
    private final Entrada entrada;

    public static void main(String[] args) {
        Arranque.iniciar("administrador", u -> u instanceof Administrador,
                s -> new MainAdministrador(s.getClub(), s.getUsuario(), s.getEntrada()).mostrar());
    }

    public MainAdministrador(Club club, Usuario admin, Entrada entrada) {
        this.club = club;
        this.admin = admin;
        this.entrada = entrada;
    }

    public void mostrar() {
        new Menu("Administrador", entrada)
                .opcion("Torneos", this::menuTorneos)
                .opcion("Turnos de empleados", this::menuTurnos)
                .opcion("Inventario y sugerencias", this::menuInventario)
                .opcion("Equipos y jugadores", this::menuEquipos)
                .opcion("Ventas y reportes", this::menuReportes)
                .opcion("Historiales (reservas, partidos, sesiones)", this::menuHistoriales)
                .mostrar();
    }

    private void menuTurnos() {
        new Menu("Turnos", entrada)
                .opcion("Ver turnos de todos los empleados", this::verTurnos)
                .opcion("Asignar turno", () -> {
                    String login = entrada.texto("Login del empleado");
                    String dia = entrada.elegirTexto("Dia:", "Lunes", "Martes", "Miercoles", "Jueves", "Viernes",
                            "Sabado", "Domingo");
                    club.getGestorTurnos().asignarTurno(admin, login, dia, entrada.hora("Hora de inicio"),
                            entrada.hora("Hora de fin"));
                    System.out.println("  Turno asignado.");
                })
                .opcion("Resolver solicitudes de cambio", () -> {
                    List<SolicitudCambioTurno> pendientes = club.getGestorTurnos().pendientes();
                    SolicitudCambioTurno s = entrada.elegir("Solicitudes pendientes:", pendientes,
                            x -> x.getTurno().getDia() + " " + x.getTurno().getHoraInicio() + " -> "
                                    + x.getFechaSolicitada() + " " + x.getHoraInicio() + "-" + x.getHoraFin());
                    club.getGestorTurnos().resolver(admin, s, entrada.siNo("Aprobar"));
                    System.out.println("  Solicitud " + s.getEstado());
                })
                .mostrar();
    }

    private void verTurnos() {
        for (Usuario u : club.getGestorUsuarios().getUsuarios().values()) {
            if (u instanceof Empleado) {
                for (Turno t : ((Empleado) u).getTurnos()) {
                    System.out.println("  " + u.getLogin() + ": " + t.getDia() + " " + t.getHoraInicio() + "-"
                            + t.getHoraFin());
                }
            }
        }
    }

    private void menuInventario() {
        new Menu("Inventario y sugerencias", entrada)
                .opcion("Ver inventario", () -> {
                    for (Producto p : club.getGestorInventario().listarProductos(admin)) {
                        System.out.println("  " + MainSocio.producto(p));
                    }
                })
                .opcion("Reponer un articulo", () -> {
                    Producto p = elegirArticulo();
                    club.getGestorInventario().reponerInventario(admin, p.getIdProducto(),
                            entrada.entero("Unidades a agregar", 1, 100000));
                    System.out.println("  Ahora hay " + ((Articulo) p).getCantidadDisponible());
                })
                .opcion("Mover un articulo de ubicacion", () -> {
                    Producto p = elegirArticulo();
                    club.getGestorInventario().moverArticulo(admin, p.getIdProducto(),
                            entrada.texto("Nueva ubicacion"));
                    System.out.println("  Movido a " + ((Articulo) p).getUbicacion());
                })
                .opcion("Aprobar o rechazar sugerencias", () -> {
                    List<Sugerencia> pendientes = club.getGestorInventario().sugerenciasPendientes(admin);
                    Sugerencia s = entrada.elegir("Sugerencias pendientes:", pendientes,
                            x -> MainSocio.producto(x.getProducto()));
                    club.getGestorInventario().resolverSugerencia(admin, s, entrada.siNo("Aprobar"));
                    System.out.println("  Sugerencia " + s.getEstado());
                })
                .mostrar();
    }

    private Producto elegirArticulo() throws Exception {
        List<Producto> articulos = new java.util.ArrayList<Producto>();
        for (Producto p : club.getGestorInventario().listarProductos(admin)) {
            if (p instanceof Articulo) {
                articulos.add(p);
            }
        }
        return entrada.elegir("Articulos:", articulos, MainSocio::producto);
    }

    private void menuEquipos() {
        new Menu("Equipos y jugadores", entrada)
                .opcion("Ver equipos", () -> {
                    for (DeporteConjunto d : club.getGestorEquipos().getDeportes()) {
                        for (Equipo e : d.getEquipos()) {
                            System.out.println("  " + e.getNombre() + " (" + d.getNombre() + " " + e.getCategoria()
                                    + "): " + e.getPlantilla().size() + "/" + e.getCupoMaximo() + " jugadores");
                            for (InscripcionEquipo i : e.getPlantilla()) {
                                System.out.println("     #" + i.getNumeroCamiseta() + " " + i.getSocio().getLogin()
                                        + " - " + i.getPosicion());
                            }
                        }
                    }
                })
                .opcion("Crear equipo", () -> {
                    DeporteConjunto d = entrada.elegir("Deporte:", club.getGestorEquipos().getDeportes(),
                            x -> x.getNombre());
                    club.getGestorEquipos().crearEquipo(admin, d.getNombre(), entrada.texto("Nombre"),
                            entrada.elegirTexto("Categoria:", GestorTorneos.CATEGORIAS), entrada.entero("Cupo maximo", 1, 60),
                            entrada.texto("Login del entrenador"));
                    System.out.println("  Equipo creado.");
                })
                .opcion("Eliminar equipo", () -> {
                    club.getGestorEquipos().eliminarEquipo(admin, entrada.texto("Nombre del equipo"));
                    System.out.println("  Equipo eliminado.");
                })
                .opcion("Agregar jugador a un equipo", () -> {
                    club.getGestorEquipos().agregarJugador(admin, entrada.texto("Equipo"),
                            entrada.texto("Login del socio"), entrada.texto("Posicion"),
                            entrada.entero("Numero de camiseta", 1, 99));
                    System.out.println("  Jugador agregado.");
                })
                .opcion("Quitar jugador de un equipo", () -> {
                    club.getGestorEquipos().quitarJugador(admin, entrada.texto("Equipo"),
                            entrada.texto("Login del socio"));
                    System.out.println("  Jugador retirado.");
                })
                .opcion("Registrar socio nuevo", () -> {
                    club.getGestorUsuarios().registrarSocio(entrada.texto("Login"), entrada.texto("Contrasena"),
                            entrada.entero("Edad", 1, 120));
                    System.out.println("  Socio registrado.");
                })
                .mostrar();
    }

    private void menuReportes() {
        new Menu("Ventas y reportes", entrada)
                .opcion("Historial completo de ventas", () -> mostrarVentas(club.getGestorVentas().consultarVentas(admin)))
                .opcion("Ventas por rubro y periodo", () -> {
                    String rubro = entrada.elegirTexto("Rubro:", "TIENDA", "CAFETERIA");
                    LocalDate desde = entrada.fecha("Desde");
                    mostrarVentas(club.getGestorVentas().ventasPorRubro(admin, rubro, desde, entrada.fecha("Hasta")));
                })
                .opcion("Ranking actual por disciplina", () -> {
                    for (DisciplinaIndividual d : club.getGestorReservas().getDisciplinas()) {
                        for (InscripcionDisciplina i : d.getInscripciones()) {
                            System.out.println("  " + d.getNombre() + " " + i.getSocio().getLogin() + " ("
                                    + i.getNivel() + "): " + (int) i.getPuntajeRanking());
                        }
                    }
                })
                .mostrar();
    }

    private void mostrarVentas(List<Venta> ventas) {
        double total = 0;
        for (Venta v : ventas) {
            System.out.println("  " + v.getFecha().toLocalDate() + " " + v.getClass().getSimpleName().replace("Venta", "")
                    + " " + v.getComprador().getLogin() + ": " + VistaTorneos.dinero(v.calcularTotal()));
            total += v.calcularTotal();
        }
        System.out.println("  Total: " + VistaTorneos.dinero(total) + " en " + ventas.size() + " ventas");
    }

    private void menuHistoriales() {
        new Menu("Historiales", entrada)
                .opcion("Reservas", () -> {
                    for (DisciplinaIndividual d : club.getGestorReservas().getDisciplinas()) {
                        for (Instalacion i : d.getInstalaciones()) {
                            for (Reserva r : i.getReservas()) {
                                System.out.println("  " + r.getFecha() + " " + r.getHoraInicio() + "-" + r.getHoraFin()
                                        + " " + i.getNombre() + ": " + r.getSocio().getLogin() + " ("
                                        + r.getModalidad() + ", " + r.getCantidadJugadores() + " jugadores)");
                            }
                        }
                    }
                })
                .opcion("Partidos", () -> {
                    for (Partido p : club.getGestorCompetencias().getPartidos()) {
                        System.out.println("  " + MainEmpleado.partido(p));
                    }
                })
                .opcion("Sesiones de entrenamiento", () -> {
                    for (SesionEntrenamiento s : club.getGestorEntrenamientos().getSesiones()) {
                        System.out.println("  " + MainEmpleado.sesion(s));
                    }
                })
                .mostrar();
    }

    private void menuTorneos() {
        new Menu("Torneos", entrada)
                .opcion("Listar torneos", () -> VistaTorneos.listar(club, admin))
                .opcion("Ver detalle e historial", () -> VistaTorneos.detalle(club, admin, entrada))
                .opcion("Crear torneo", this::crear)
                .opcion("Inscribir estudiante o persona externa", this::inscribirExterno)
                .opcion("Retirar estudiante o persona externa", this::retirarExterno)
                .opcion("Iniciar torneo", this::iniciar)
                .opcion("Registrar resultado de un encuentro", this::registrarResultado)
                .opcion("Finalizar torneo y entregar premios", this::finalizar)
                .mostrar();
    }

    private void crear() throws Exception {
        String clase = entrada.elegirTexto("Clase de torneo:", "Deporte de conjunto", "Disciplina individual");
        String deporte;
        String categoria;
        String modalidad = null;
        if (clase.startsWith("Deporte")) {
            deporte = entrada.elegir("Deporte:", club.getGestorEquipos().getDeportes(), d -> d.getNombre()).getNombre();
            categoria = entrada.elegirTexto("Categoria:", GestorTorneos.CATEGORIAS);
        } else {
            deporte = entrada.elegir("Disciplina:", club.getGestorReservas().getDisciplinas(), d -> d.getNombre())
                    .getNombre();
            categoria = entrada.elegirTexto("Nivel:", GestorTorneos.NIVELES);
            modalidad = entrada.elegirTexto("Modalidad:", Torneo.SENCILLOS, Torneo.DOBLES);
        }
        int cupos = entrada.entero("Numero de cupos", 1, 1000);
        String tipo = entrada.elegirTexto("Tipo:", Torneo.RECREATIVO, Torneo.COMPETITIVO);
        double tarifa = 0;
        if (tipo.equals(Torneo.COMPETITIVO)) {
            tarifa = entrada.decimal("Tarifa de inscripcion", 1);
        }
        Torneo t = club.getGestorTorneos().crearTorneo(admin, deporte, categoria, cupos, tipo, modalidad,
                entrada.fecha("Fecha del torneo"), tarifa);
        System.out.println("  Torneo creado: " + VistaTorneos.resumen(t));
        System.out.println("  Cupos: " + t.getCuposTotales() + " (" + t.getCuposPreferenciales() + " preferenciales y "
                + t.getCuposRegulares() + " regulares)");
    }

    private void inscribirExterno() throws Exception {
        Torneo t = VistaTorneos.elegir(club, admin, entrada);
        String nombre = entrada.texto("Nombre");
        String documento = entrada.texto("Documento");
        String tipo = entrada.elegirTexto("Tipo:", PersonaExterna.ESTUDIANTE, PersonaExterna.EXTERNO);
        String codigo = null;
        if (tipo.equals(PersonaExterna.ESTUDIANTE)) {
            codigo = entrada.texto("Codigo estudiantil");
        }
        club.getGestorTorneos().inscribirExterno(admin, t, nombre, documento, tipo, codigo);
        System.out.println("  Inscrito. Cupos libres: " + t.cuposLibres());
    }

    private void retirarExterno() throws Exception {
        Torneo t = VistaTorneos.elegir(club, admin, entrada);
        club.getGestorTorneos().retirarExterno(admin, t, entrada.texto("Documento del participante"));
        System.out.println("  Retirado. Cupos libres: " + t.cuposLibres());
    }

    private void iniciar() throws Exception {
        Torneo t = VistaTorneos.elegir(club, admin, entrada);
        club.getGestorTorneos().iniciar(admin, t);
        System.out.println("  Torneo en curso con " + t.getEncuentros().size() + " encuentros:");
        for (Encuentro p : t.getEncuentros()) {
            System.out.println("    " + VistaTorneos.encuentro(p));
        }
    }

    private void registrarResultado() throws Exception {
        Torneo t = VistaTorneos.elegir(club, admin, entrada);
        Encuentro p = elegirPendiente(t);
        String unidad = t instanceof TorneoConjunto && !t.getNombreDeporte().equalsIgnoreCase("Voleibol") ? "" : " (sets)";
        int marcadorA = entrada.entero("Marcador de " + p.getParticipanteA().getNombre() + unidad, 0, 999);
        int marcadorB = entrada.entero("Marcador de " + p.getParticipanteB().getNombre() + unidad, 0, 999);
        club.getGestorTorneos().registrarResultado(admin, t, p, marcadorA, marcadorB);
        System.out.println("  Resultado registrado: " + VistaTorneos.encuentro(p));
    }

    private void finalizar() throws Exception {
        Torneo t = VistaTorneos.elegir(club, admin, entrada);
        club.getGestorTorneos().finalizar(admin, t);
        System.out.println("  Torneo finalizado.");
    }

    private Encuentro elegirPendiente(Torneo t) throws Exception {
        List<Encuentro> pendientes = t.getEncuentrosPendientes();
        if (pendientes.isEmpty()) {
            throw new Exception("El torneo no tiene encuentros pendientes (esta " + t.getEstadoTexto() + ")");
        }
        return entrada.elegir("Encuentros pendientes:", pendientes, p -> VistaTorneos.encuentro(p));
    }
}
