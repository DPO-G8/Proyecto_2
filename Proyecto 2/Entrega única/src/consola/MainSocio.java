package consola;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import controlador.Club;
import logica.deportes.Asistencia;
import logica.deportes.DisciplinaIndividual;
import logica.deportes.Estadistica;
import logica.deportes.Instalacion;
import logica.deportes.Partido;
import logica.deportes.PartidoPractica;
import logica.deportes.Reserva;
import logica.tienda.Articulo;
import logica.tienda.Bebida;
import logica.tienda.Producto;
import logica.tienda.Snack;
import logica.tienda.Venta;
import logica.torneos.Bono;
import logica.torneos.Torneo;
import logica.usuarios.Socio;

public class MainSocio {
    private final Club club;
    private final Socio socio;
    private final Entrada entrada;

    public static void main(String[] args) {
        Arranque.iniciar("socio", u -> u instanceof Socio,
                s -> new MainSocio(s.getClub(), (Socio) s.getUsuario(), s.getEntrada()).mostrar());
    }

    public MainSocio(Club club, Socio socio, Entrada entrada) {
        this.club = club;
        this.socio = socio;
        this.entrada = entrada;
    }

    public void mostrar() {
        new Menu("Socio " + socio.getLogin(), entrada)
                .opcion("Mi perfil", this::perfil)
                .opcion("Reservas", this::menuReservas)
                .opcion("Equipos, academias y partidos de practica", this::menuDeportes)
                .opcion("Tienda y cafeteria", this::menuTienda)
                .opcion("Mi desempeno", this::desempeno)
                .opcion("Torneos", this::menuTorneos)
                .mostrar();
    }

    private void perfil() throws Exception {
        System.out.println("  Login: " + socio.getLogin() + " | Edad: " + socio.getEdad());
        System.out.println("  Estado: " + club.getGestorUsuarios().estadoDe(socio));
        System.out.println("  Puntos de fidelidad: " + socio.getPuntosFidelidad());
        System.out.println("  Descuento: " + (int) (socio.getPorcentajeDescuento() * 100) + "%"
                + (socio.tieneDescuentoCompartido() ? " (codigo de " + socio.getEmpleadoDescuento().getLogin() + ")" : ""));
        mostrarBonos();
    }

    private void menuReservas() {
        new Menu("Reservas", entrada)
                .opcion("Reservar una instalacion", () -> {
                    Instalacion i = elegirInstalacion();
                    String modalidad = entrada.elegirTexto("Modalidad:", Instalacion.SENCILLOS, Instalacion.DOBLES);
                    int jugadores = entrada.entero("Cantidad de jugadores", 1, 4);
                    List<String> invitados = new ArrayList<String>();
                    if (jugadores > 1 && entrada.siNo("Indicar socios invitados")) {
                        invitados = entrada.lista("Logins de los invitados");
                    }
                    Reserva r = club.getGestorReservas().reservar(socio, i.getId(), entrada.fecha("Fecha"),
                            entrada.hora("Hora de inicio"), entrada.hora("Hora de fin"), jugadores, modalidad, invitados);
                    System.out.println("  Reserva registrada en " + i.getNombre() + " el " + r.getFecha());
                })
                .opcion("Mis reservas", () -> {
                    for (Reserva r : club.getGestorReservas().reservasDe(socio)) {
                        System.out.println("  " + r.getFecha() + " " + r.getHoraInicio() + "-" + r.getHoraFin() + " ("
                                + r.getModalidad() + ", " + r.getCantidadJugadores() + " jugadores) de "
                                + r.getSocio().getLogin());
                    }
                })
                .opcion("Entrar a una instalacion (control de bebida caliente)", () -> {
                    Instalacion i = elegirInstalacion();
                    club.getGestorVentas().verificarAcceso(socio, i.getId());
                    System.out.println("  Acceso permitido a " + i.getNombre());
                })
                .mostrar();
    }

    private Instalacion elegirInstalacion() throws Exception {
        List<Instalacion> todas = new ArrayList<Instalacion>();
        for (DisciplinaIndividual d : club.getGestorReservas().getDisciplinas()) {
            todas.addAll(d.getInstalaciones());
        }
        return entrada.elegir("Instalaciones:", todas, i -> i.getNombre() + " (capacidad " + i.getCapacidad()
                + (i.isTechada() ? ", techada" : "") + ")");
    }

    private void menuDeportes() {
        new Menu("Equipos, academias y partidos de practica", entrada)
                .opcion("Inscribirme en un equipo", () -> {
                    club.getGestorEquipos().agregarJugador(socio, entrada.texto("Nombre del equipo"),
                            socio.getLogin(), entrada.texto("Posicion"), entrada.entero("Numero de camiseta", 1, 99));
                    System.out.println("  Inscrito en el equipo.");
                })
                .opcion("Inscribirme en una academia", () -> {
                    DisciplinaIndividual d = entrada.elegir("Disciplina:", club.getGestorReservas().getDisciplinas(),
                            x -> x.getNombre());
                    club.getGestorReservas().inscribirEnDisciplina(socio, d.getNombre(),
                            entrada.elegirTexto("Nivel:", "Principiante", "Intermedio", "Avanzado"));
                    System.out.println("  Inscrito en " + d.getNombre());
                })
                .opcion("Programar partido de practica", () -> {
                    DisciplinaIndividual d = entrada.elegir("Disciplina:", club.getGestorReservas().getDisciplinas(),
                            x -> x.getNombre());
                    String modalidad = entrada.elegirTexto("Modalidad:", Instalacion.SENCILLOS, Instalacion.DOBLES);
                    club.getGestorCompetencias().crearPartidoPractica(socio, d.getNombre(), modalidad,
                            entrada.fecha("Fecha"), entrada.lista("Logins de los demas jugadores"));
                    System.out.println("  Partido de practica programado.");
                })
                .opcion("Registrar resultado de un partido de practica", () -> {
                    List<PartidoPractica> mios = new ArrayList<PartidoPractica>();
                    for (Partido p : club.getGestorCompetencias().getPartidos()) {
                        if (p instanceof PartidoPractica && p.getMarcador().isEmpty()
                                && ((PartidoPractica) p).getJugadores().contains(socio)) {
                            mios.add((PartidoPractica) p);
                        }
                    }
                    PartidoPractica p = entrada.elegir("Partidos pendientes:", mios, x -> MainEmpleado.partido(x));
                    String marcador = entrada.texto("Marcador (por ejemplo 6-4 6-3)");
                    List<String> ganadores = entrada.lista("Login(s) de los ganadores");
                    club.getGestorCompetencias().registrarResultadoPractica(socio, p, marcador, ganadores,
                            FormularioEstadisticas.pedir(entrada, club.getGestorUsuarios().getUsuarios(), p,
                                    p.getDisciplina().getNombre()));
                    System.out.println("  Resultado registrado y ranking actualizado.");
                })
                .mostrar();
    }
    private void menuTienda() {
        new Menu("Tienda y cafeteria", entrada)
                .opcion("Ver catalogo", () -> {
                    for (Producto p : club.getGestorVentas().catalogo()) {
                        System.out.println("  " + producto(p));
                    }
                })
                .opcion("Comprar", this::comprar)
                .opcion("Mis bonos de descuento", this::mostrarBonos)
                .mostrar();
    }

    private void comprar() throws Exception {
        Map<Integer, Integer> pedido = new LinkedHashMap<Integer, Integer>();
        boolean cafeteria = false;
        do {
            Producto p = entrada.elegir("Producto:", club.getGestorVentas().catalogo(), MainSocio::producto);
            if (p instanceof Snack) {
                System.out.println("  Aviso de alergenos: " + ((Snack) p).getAlergenos());
            }
            cafeteria = cafeteria || !(p instanceof Articulo);
            pedido.put(p.getIdProducto(), entrada.entero("Cantidad", 1, 1000));
        } while (entrada.siNo("Agregar otro producto"));
        double propina = 0;
        if (cafeteria) {
            propina = entrada.entero("Propina en % (sugerida 10)", 0, 100) / 100.0;
        }
        Bono bono = null;
        List<Bono> disponibles = new ArrayList<Bono>();
        for (Bono b : socio.getBonos()) {
            if (!b.isUsado()) {
                disponibles.add(b);
            }
        }
        if (!disponibles.isEmpty() && entrada.siNo("Usar un bono de torneo (no acumulable)")) {
            bono = entrada.elegir("Bonos disponibles:", disponibles, MainSocio::bono);
        }
        int puntos = 0;
        if (bono == null && socio.getPuntosFidelidad() > 0 && entrada.siNo("Redimir puntos (tiene "
                + socio.getPuntosFidelidad() + ")")) {
            puntos = entrada.entero("Puntos a redimir", 1, socio.getPuntosFidelidad());
        }
        Venta v = club.getGestorVentas().comprar(socio, pedido, propina, puntos, bono);
        System.out.println("  Compra registrada. Total: " + VistaTorneos.dinero(v.calcularTotal())
                + " | Puntos ahora: " + socio.getPuntosFidelidad());
    }

    private void mostrarBonos() throws Exception {
        List<Bono> bonos = club.getGestorTorneos().consultarBonos(socio);
        if (bonos.isEmpty()) {
            System.out.println("  Bonos: ninguno");
        }
        for (Bono b : bonos) {
            System.out.println("  Bono: " + bono(b));
        }
    }

    private void desempeno() {
        System.out.println("  Asistencias a entrenamientos:");
        for (Asistencia a : club.getGestorEntrenamientos().asistenciasDe(socio)) {
            System.out.println("    " + a.getEstado() + (a.getNotaDesempeno() > 0 ? ", nota " + a.getNotaDesempeno() : "")
                    + (a.getComentario().isEmpty() ? "" : ": " + a.getComentario()));
        }
        System.out.println("  Estadisticas registradas: " + socio.getEstadisticas().size());
        for (Estadistica e : socio.getEstadisticas()) {
            System.out.println("    " + e.getClass().getSimpleName().replace("Estadistica", "") + " - "
                    + MainEmpleado.partido(e.getPartido()));
        }
        try {
            System.out.println("  Posicion en el proximo partido: "
                    + club.getGestorCompetencias().proximaPosicion(socio));
        } catch (Exception e) {
            System.out.println("  " + e.getMessage());
        }
    }

    private void menuTorneos() {
        new Menu("Torneos", entrada)
                .opcion("Listar torneos", () -> VistaTorneos.listar(club, socio))
                .opcion("Ver detalle e historial", () -> VistaTorneos.detalle(club, socio, entrada))
                .opcion("Inscribirme en un torneo individual", () -> {
                    Torneo t = VistaTorneos.elegir(club, socio, entrada);
                    String pareja = entrada.textoOpcional("Login de la pareja (solo dobles)");
                    boolean preferencial = club.getGestorTorneos().inscribirJugador(socio, t.getId(), pareja)
                            .isPreferencial();
                    System.out.println("  Inscrito con cupo " + (preferencial ? "preferencial" : "regular")
                            + (t.esCompetitivo() ? ". Tarifa pagada: " + VistaTorneos.dinero(t.getTarifa()) : ""));
                })
                .opcion("Salirme de un torneo", () -> {
                    Torneo t = VistaTorneos.elegir(club, socio, entrada);
                    club.getGestorTorneos().salirDeTorneo(socio, t.getId());
                    System.out.println("  Inscripcion retirada; el cupo queda libre.");
                })
                .opcion("Mis bonos", this::mostrarBonos)
                .mostrar();
    }

    static String producto(Producto p) {
        String extra = "";
        if (p instanceof Articulo) {
            Articulo a = (Articulo) p;
            extra = " | " + a.getCategoria() + ", " + a.getCantidadDisponible() + " en " + a.getUbicacion();
        } else if (p instanceof Bebida) {
            extra = ((Bebida) p).isCaliente() ? " | bebida caliente" : " | bebida fria";
        } else if (p instanceof Snack) {
            extra = " | snack, alergenos: " + ((Snack) p).getAlergenos();
        }
        return "[" + p.getIdProducto() + "] " + p.getNombre() + " " + VistaTorneos.dinero(p.getPrecio()) + extra;
    }

    static String bono(Bono b) {
        return (int) Math.round(b.getPorcentaje() * 100) + "% (" + b.getNombreTorneo() + ") - " + b.getEstado();
    }
}