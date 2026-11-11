import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import controlador.Club;
import logica.deportes.Competencia;
import logica.deportes.DeporteConjunto;
import logica.deportes.DisciplinaIndividual;
import logica.deportes.Equipo;
import logica.deportes.EstadisticaFutbol;
import logica.deportes.InscripcionDisciplina;
import logica.deportes.Instalacion;
import logica.deportes.PartidoCompetencia;
import logica.deportes.Reserva;
import logica.deportes.SesionEntrenamiento;
import logica.tienda.Articulo;
import logica.tienda.Bebida;
import logica.tienda.Snack;
import logica.tienda.Sugerencia;
import logica.tienda.Venta;
import logica.usuarios.Administrador;
import logica.usuarios.Empleado;
import logica.usuarios.Entrenador;
import logica.usuarios.Fisioterapeuta;
import logica.usuarios.SolicitudCambioTurno;
import logica.usuarios.Socio;
import persistencia.RepositorioJSON;

// Prueba final: datos iniciales en datos_prueba y recorrido de los RF
public class PruebaClub {

    interface Paso {
        void ejecutar() throws Exception;
    }

    static int fallos = 0;

    // Debe funcionar
    static void debeFuncionar(String descripcion, Paso paso) {
        try {
            paso.ejecutar();
            System.out.println("OK      " + descripcion);
        } catch (Exception e) {
            fallos++;
            System.out.println("FALLO   " + descripcion + " -> " + e.getMessage());
        }
    }

    // Debe ser rechazado
    static void debeRechazar(String descripcion, Paso paso) {
        try {
            paso.ejecutar();
            fallos++;
            System.out.println("FALLO   " + descripcion + " -> se acepto y debia rechazarse");
        } catch (Exception e) {
            System.out.println("OK      " + descripcion + " -> rechazado: " + e.getMessage());
        }
    }

    static void verificar(String descripcion, boolean condicion) {
        if (condicion) {
            System.out.println("OK      " + descripcion);
        } else {
            fallos++;
            System.out.println("FALLO   " + descripcion);
        }
    }

    public static void main(String[] args) throws Exception {
        // ---------------------------------------------------------- datos iniciales
        File carpeta = new File("datos_prueba");
        if (carpeta.exists()) {
            for (File f : carpeta.listFiles()) {
                f.delete();
            }
        }
        final Club club = new Club(new RepositorioJSON("datos_prueba"));

        final Administrador admin = new Administrador("admin", "1234");
        final Entrenador e1 = new Entrenador("entrenador1", "1", "COD-E1");
        final Entrenador e2 = new Entrenador("entrenador2", "1", "COD-E2");
        final Entrenador e3 = new Entrenador("entrenador3", "1", "COD-E3");
        final Fisioterapeuta fisio = new Fisioterapeuta("fisio1", "1", "COD-F1");
        final Empleado emp = new Empleado("empleado1", "1", "COD-M1");
        club.getGestorUsuarios().agregarUsuario(admin);
        club.getGestorUsuarios().agregarUsuario(e1);
        club.getGestorUsuarios().agregarUsuario(e2);
        club.getGestorUsuarios().agregarUsuario(e3);
        club.getGestorUsuarios().agregarUsuario(fisio);
        club.getGestorUsuarios().agregarUsuario(emp);
        final Socio ana = club.getGestorUsuarios().registrarSocio("ana", "123", 20);
        final Socio beto = club.getGestorUsuarios().registrarSocio("beto", "123", 25);
        final Socio cami = club.getGestorUsuarios().registrarSocio("cami", "123", 30);
        final ArrayList<String> jugadores = new ArrayList<String>();
        for (int i = 1; i <= 8; i++) {
            club.getGestorUsuarios().registrarSocio("jugador" + i, "1", 22);
            jugadores.add("jugador" + i);
        }

        DisciplinaIndividual padel = new DisciplinaIndividual("Padel", 2);
        padel.agregarInstalacion(new Instalacion(1, "Cancha Padel 1", 4, false, "Padel"));
        DisciplinaIndividual mesa = new DisciplinaIndividual("Tenis de mesa", 1);
        mesa.agregarInstalacion(new Instalacion(2, "Mesa 1", 4, true, "Mesa"));
        DisciplinaIndividual tenis = new DisciplinaIndividual("Tenis", 2);
        tenis.agregarInstalacion(new Instalacion(3, "Cancha Tenis 1", 4, false, "Tenis"));
        club.getGestorReservas().agregarDisciplina(padel);
        club.getGestorReservas().agregarDisciplina(mesa);
        club.getGestorReservas().agregarDisciplina(tenis);

        club.getGestorEquipos().agregarDeporte(new DeporteConjunto("Futbol", 7));
        club.getGestorEquipos().agregarDeporte(new DeporteConjunto("Baloncesto", 5));
        DeporteConjunto futbol = club.getGestorEquipos().buscarDeporte("Futbol");
        club.getGestorCompetencias().agregarCompetencia(
                new Competencia("Torneo de los Cerros", "Universidades de Bogota", "2026", "Mayores", "Liga", 7, futbol));
        club.getGestorCompetencias().agregarCompetencia(
                new Competencia("ASCUN", "ASCUN", "2026", "Mayores", "Fases", 7, futbol));

        club.getGestorVentas().agregarProducto(new Articulo(1, "Raqueta", 100000, "Raquetas", 5, "Estante A"));
        club.getGestorVentas().agregarProducto(new Bebida(2, "Cafe", 5000, true));
        club.getGestorVentas().agregarProducto(new Bebida(3, "Limonada", 4000, false));
        ArrayList<String> alergenos = new ArrayList<String>();
        alergenos.add("mani");
        club.getGestorVentas().agregarProducto(new Snack(4, "Barra de mani", 3000, alergenos));

        // ------------------------------------------------------------------- usuarios
        System.out.println("\n== Usuarios");
        debeFuncionar("login correcto", () -> club.getGestorUsuarios().autenticar("ana", "123"));
        debeRechazar("login con clave incorrecta", () -> club.getGestorUsuarios().autenticar("ana", "xxx"));
        debeRechazar("registrar login repetido", () -> club.getGestorUsuarios().registrarSocio("ana", "1", 20));
        debeFuncionar("empleado comparte su codigo con beto", () -> club.getGestorUsuarios().compartirCodigo(emp, "beto"));
        verificar("beto tiene 8% de descuento", beto.getPorcentajeDescuento() == 0.08);

        // ------------------------------------------------------------------- reservas
        System.out.println("\n== Reservas");
        final LocalDate manana = LocalDate.now().plusDays(1);
        debeFuncionar("ana reserva padel", () -> club.getGestorReservas().reservar(ana, 1, manana,
                LocalTime.of(10, 0), LocalTime.of(11, 0), 4, "Dobles"));
        debeRechazar("misma cancha y horario", () -> club.getGestorReservas().reservar(beto, 1, manana,
                LocalTime.of(10, 30), LocalTime.of(11, 30), 4, "Dobles"));
        debeRechazar("cancha de padel con 2 jugadores", () -> club.getGestorReservas().reservar(beto, 1, manana,
                LocalTime.of(15, 0), LocalTime.of(16, 0), 2, "Sencillos"));
        debeRechazar("el administrador no puede reservar", () -> club.getGestorReservas().reservar(admin, 1, manana,
                LocalTime.of(15, 0), LocalTime.of(16, 0), 4, "Dobles"));
        debeRechazar("reservar en fecha pasada", () -> club.getGestorReservas().reservar(beto, 1,
                LocalDate.now().minusDays(1), LocalTime.of(15, 0), LocalTime.of(16, 0), 4, "Dobles"));
        debeFuncionar("cami se inscribe en Padel (Intermedio)",
                () -> club.getGestorReservas().inscribirEnDisciplina(cami, "Padel", "Intermedio"));
        debeFuncionar("ana se inscribe en Padel (Principiante)",
                () -> club.getGestorReservas().inscribirEnDisciplina(ana, "Padel", "Principiante"));
        debeFuncionar("beto se inscribe en Padel (Avanzado)",
                () -> club.getGestorReservas().inscribirEnDisciplina(beto, "Padel", "Avanzado"));

        // -------------------------------------------------------------------- partidos de practica
        System.out.println("\n== Partidos de practica (RF3)");
        final LocalDate hoy = LocalDate.now();
        final ArrayList<String> contraBeto = new ArrayList<String>();
        contraBeto.add("beto");
        final ArrayList<String> contraCami = new ArrayList<String>();
        contraCami.add("cami");
        debeRechazar("principiante contra avanzado", () -> club.getGestorCompetencias().crearPartidoPractica(ana, "Padel",
                "Sencillos", hoy, contraBeto));
        debeFuncionar("principiante contra intermedio", () -> club.getGestorCompetencias().crearPartidoPractica(ana, "Padel",
                "Sencillos", hoy, contraCami));
        final ArrayList<String> contraEntrenador = new ArrayList<String>();
        contraEntrenador.add("entrenador1");
        debeFuncionar("principiante contra un entrenador (excepcion de nivel)",
                () -> club.getGestorCompetencias().crearPartidoPractica(ana, "Padel", "Sencillos", hoy, contraEntrenador));

        // --------------------------------------------------------------------- ventas
        System.out.println("\n== Ventas");
        final Map<Integer, Integer> raquetas = pedido(1, 2);
        debeFuncionar("compra de 2 raquetas con IVA", () -> {
            Venta v = club.getGestorVentas().comprar(ana, raquetas);
            verificar("   total = 200000 * 1.19", Math.round(v.calcularTotal()) == 238000);
        });
        verificar("ana recibio el 2% en puntos (4760)", ana.getPuntosFidelidad() == 4760);
        debeRechazar("comprar mas raquetas de las que hay", () -> club.getGestorVentas().comprar(ana, pedido(1, 10)));
        debeRechazar("mezclar tienda y cafeteria", () -> {
            Map<Integer, Integer> mezcla = pedido(1, 1);
            mezcla.put(3, 1);
            club.getGestorVentas().comprar(ana, mezcla);
        });
        debeFuncionar("compra de limonada con 8% y propina 10%", () -> {
            Venta v = club.getGestorVentas().comprar(ana, pedido(3, 1));
            verificar("   total = 4000 * 1.18", Math.round(v.calcularTotal()) == 4720);
        });
        debeFuncionar("redimir puntos en una compra", () -> club.getGestorVentas().comprar(ana, pedido(1, 1), 0.10, 1000));
        debeRechazar("redimir mas puntos de los que tiene", () -> club.getGestorVentas().comprar(cami, pedido(1, 1), 0.10, 500));
        debeFuncionar("snack: se informan los alergenos", () -> {
            verificar("   alergenos = [mani]", club.getGestorVentas().alergenosDe(4).get(0).equals("mani"));
            club.getGestorVentas().comprar(ana, pedido(4, 1));
        });
        debeFuncionar("el empleado compra con su descuento del 15%", () -> {
            Venta v = club.getGestorVentas().comprar(emp, pedido(1, 1));
            verificar("   total = 100000 * 0.85 * 1.19", Math.round(v.calcularTotal()) == 101150);
        });

        System.out.println("\n== Bebida caliente y canchas (RF5)");
        LocalDateTime en10 = LocalDateTime.now().plusMinutes(10);
        final LocalDate fechaCerca = en10.toLocalDate();
        final LocalTime inicioCerca = en10.toLocalTime();
        final LocalTime finCerca = inicioCerca.plusMinutes(30).isAfter(inicioCerca) ? inicioCerca.plusMinutes(30)
                : LocalTime.of(23, 59);
        debeFuncionar("cami reserva padel que empieza en 10 minutos", () -> club.getGestorReservas().reservar(cami, 1,
                fechaCerca, inicioCerca, finCerca, 4, "Dobles"));
        debeRechazar("cafe caliente a cami con reserva cercana", () -> club.getGestorVentas().comprar(cami, pedido(2, 1)));
        debeFuncionar("limonada fria a cami si se puede", () -> club.getGestorVentas().comprar(cami, pedido(3, 1)));
        debeFuncionar("beto compra un cafe caliente (sin reserva cercana)", () -> club.getGestorVentas().comprar(beto, pedido(2, 1)));
        debeRechazar("beto con cafe reciente no entra a padel", () -> club.getGestorVentas().verificarAcceso(beto, 1));
        debeFuncionar("beto con cafe si entra a una cancha de tenis", () -> club.getGestorVentas().verificarAcceso(beto, 3));

        // ------------------------------------------------------------------- inventario
        System.out.println("\n== Inventario");
        debeRechazar("un socio no consulta inventario", () -> club.getGestorInventario().consultarProducto(ana, 1));
        debeFuncionar("admin consulta un producto", () -> club.getGestorInventario().consultarProducto(admin, 1));
        debeRechazar("admin consulta un producto que no existe", () -> club.getGestorInventario().consultarProducto(admin, 99));
        debeFuncionar("admin repone 10 raquetas", () -> club.getGestorInventario().reponerInventario(admin, 1, 10));
        verificar("hay 11 raquetas (1 + 10 repuestas)",
                ((Articulo) club.getGestorVentas().getProductos().get(1)).getCantidadDisponible() == 11);
        debeRechazar("empleado propone snack sin precio", () -> club.getGestorInventario().proponerProducto(emp,
                new Snack(club.getGestorInventario().siguienteIdProducto(), "Galleta", 0, new ArrayList<String>())));
        final Sugerencia sugerencia = club.getGestorInventario().proponerProducto(fisio, new Snack(
                club.getGestorInventario().siguienteIdProducto(), "Galleta de avena", 2500, new ArrayList<String>()));
        verificar("hay una sugerencia pendiente", club.getGestorInventario().sugerenciasPendientes(admin).size() == 1);
        debeFuncionar("admin aprueba la sugerencia", () -> club.getGestorInventario().resolverSugerencia(admin, sugerencia, true));
        verificar("la galleta ya esta en el catalogo", club.getGestorVentas().getProductos().containsKey(5));
        debeRechazar("aprobar dos veces la misma sugerencia", () -> club.getGestorInventario().resolverSugerencia(admin, sugerencia, true));
        debeFuncionar("admin ve el historial de ventas", () -> club.getGestorVentas().consultarVentas(admin));
        debeRechazar("un socio no ve el historial de ventas", () -> club.getGestorVentas().consultarVentas(ana));

        // --------------------------------------------------------------------- equipos
        System.out.println("\n== Equipos");
        debeFuncionar("admin crea el equipo con su entrenador", () -> club.getGestorEquipos().crearEquipo(admin, "Futbol", "Leones", "Mayores", 15, "entrenador1"));
        debeRechazar("crear un equipo con nombre repetido", () -> club.getGestorEquipos().crearEquipo(admin, "Futbol", "Leones", "Mayores", 15, "entrenador1"));
        debeRechazar("eliminar un equipo que no existe", () -> club.getGestorEquipos().eliminarEquipo(admin, "Fantasmas"));
        debeRechazar("un socio no crea equipos", () -> club.getGestorEquipos().crearEquipo(ana, "Futbol", "Otros", "Mayores", 15, "entrenador2"));
        for (int i = 0; i < jugadores.size(); i++) {
            final int numero = i + 1;
            final String login = jugadores.get(i);
            club.getGestorEquipos().agregarJugador(admin, "Leones", login, "Delantero", numero);
        }
        System.out.println("OK      8 jugadores agregados al equipo");
        debeRechazar("agregar un jugador que ya esta", () -> club.getGestorEquipos().agregarJugador(admin, "Leones", "jugador1", "Portero", 20));
        debeRechazar("camiseta repetida", () -> club.getGestorEquipos().agregarJugador(admin, "Leones", "ana", "Portero", 3));
        debeRechazar("quitar un jugador que no esta", () -> club.getGestorEquipos().quitarJugador(admin, "Leones", "ana"));
        debeFuncionar("socio se inscribe a si mismo", () -> club.getGestorEquipos().agregarJugador(ana, "Leones", "ana", "Defensa", 30));
        debeFuncionar("admin quita al jugador", () -> club.getGestorEquipos().quitarJugador(admin, "Leones", "ana"));
        debeRechazar("un socio no inscribe a otro", () -> club.getGestorEquipos().agregarJugador(ana, "Leones", "beto", "Defensa", 31));

        // ----------------------------------------------------------------- competencias
        System.out.println("\n== Competencias");
        final ArrayList<String> pocos = new ArrayList<String>(jugadores.subList(0, 5));
        debeRechazar("inscribir con nomina menor a la minima", () -> club.getGestorCompetencias().inscribirEquipo(e1, "Leones", "Torneo de los Cerros", pocos));
        debeRechazar("inscribir un jugador que no es del equipo", () -> {
            ArrayList<String> mala = new ArrayList<String>(jugadores);
            mala.set(0, "cami");
            club.getGestorCompetencias().inscribirEquipo(e1, "Leones", "Torneo de los Cerros", mala);
        });
        debeRechazar("otro entrenador no inscribe el equipo", () -> club.getGestorCompetencias().inscribirEquipo(e2, "Leones", "Torneo de los Cerros", jugadores));
        debeFuncionar("entrenador inscribe el equipo", () -> club.getGestorCompetencias().inscribirEquipo(e1, "Leones", "Torneo de los Cerros", jugadores));
        debeRechazar("inscribir dos veces", () -> club.getGestorCompetencias().inscribirEquipo(e1, "Leones", "Torneo de los Cerros", jugadores));
        final PartidoCompetencia partido1 = club.getGestorCompetencias().programarPartido(e1, "Leones", "Torneo de los Cerros",
                LocalDate.now(), "Universidad X", "Estadio Norte", "Jornada 1");
        final ArrayList<String> seis = new ArrayList<String>(jugadores.subList(0, 6));
        debeRechazar("convocar menos del minimo de futbol", () -> club.getGestorCompetencias().convocar(e1, partido1, seis));
        debeFuncionar("convocar 7 jugadores", () -> club.getGestorCompetencias().convocar(e1, partido1,
                new ArrayList<String>(jugadores.subList(0, 7))));
        final Socio jugador1 = (Socio) club.getGestorUsuarios().getUsuarios().get("jugador1");
        final Socio jugador2 = (Socio) club.getGestorUsuarios().getUsuarios().get("jugador2");
        verificar("jugador1 sabe su posicion", club.getGestorCompetencias().proximaPosicion(jugador1).equals("Delantero"));
        debeFuncionar("resultado 2-1 (3 puntos)", () -> club.getGestorCompetencias().registrarResultado(e1, partido1, 2, 1));
        debeFuncionar("estadistica con tarjeta roja", () -> club.getGestorCompetencias().registrarEstadistica(e1, partido1,
                new EstadisticaFutbol(jugador1, partido1, 1, 0, 0, 1)));
        verificar("jugador1 queda suspendido", jugador1.estaSuspendido());
        System.out.println("        estado de jugador1: " + club.getGestorUsuarios().estadoDe(jugador1));
        debeFuncionar("dos amarillas en partidos distintos", () -> {
            club.getGestorCompetencias().registrarEstadistica(e1, partido1, new EstadisticaFutbol(jugador2, partido1, 0, 0, 1, 0));
        });
        final PartidoCompetencia partido2 = club.getGestorCompetencias().programarPartido(e1, "Leones", "Torneo de los Cerros",
                LocalDate.now().plusDays(1), "Universidad Y", "Estadio Sur", "Jornada 2");
        debeRechazar("convocar a un jugador suspendido", () -> club.getGestorCompetencias().convocar(e1, partido2,
                new ArrayList<String>(jugadores.subList(0, 7))));
        debeFuncionar("empate en futbol vale 1 punto", () -> club.getGestorCompetencias().registrarResultado(e1, partido2, 1, 1));
        Map<Equipo, Integer> tabla = club.getGestorCompetencias().tablaPosiciones("Torneo de los Cerros");
        verificar("tabla: Leones tiene 4 puntos", tabla.get(club.getGestorEquipos().buscarEquipo("Leones")) == 4);
        debeFuncionar("entrenador ve estadisticas de su equipo", () -> club.getGestorCompetencias().estadisticasDeEquipo(e1, "Leones"));
        debeRechazar("otro entrenador no ve estadisticas", () -> club.getGestorCompetencias().estadisticasDeEquipo(e2, "Leones"));
        debeRechazar("eliminar un equipo inscrito en una competencia", () -> club.getGestorEquipos().eliminarEquipo(admin, "Leones"));

        // ------------------------------------------------------------ entrenamientos
        System.out.println("\n== Entrenamientos");
        final SesionEntrenamiento sesion = club.getGestorEntrenamientos().crearSesion(e1, manana, LocalTime.of(17, 0), 90,
                "Tecnico", 3, "Leones");
        System.out.println("OK      entrenador crea una sesion");
        debeRechazar("sesion en una instalacion ocupada", () -> club.getGestorEntrenamientos().crearSesion(e1, manana,
                LocalTime.of(17, 30), 60, "Fisico", 3, "Leones"));
        debeRechazar("sesion sobre una reserva", () -> club.getGestorEntrenamientos().crearSesion(e1, manana,
                LocalTime.of(10, 30), 60, "Fisico", 1, "Leones"));
        debeRechazar("otro entrenador crea una sesion para el equipo", () -> club.getGestorEntrenamientos().crearSesion(e2,
                manana, LocalTime.of(19, 0), 60, "Fisico", 3, "Leones"));
        debeFuncionar("registrar asistencia con nota", () -> club.getGestorEntrenamientos().registrarAsistencia(e1, sesion,
                "jugador1", "ASISTIO", 5, "Muy bien"));
        debeRechazar("nota fuera de rango", () -> club.getGestorEntrenamientos().registrarAsistencia(e1, sesion,
                "jugador2", "ASISTIO", 9, ""));
        debeRechazar("asistencia de alguien que no es del equipo", () -> club.getGestorEntrenamientos().registrarAsistencia(e1,
                sesion, "cami", "ASISTIO", 3, ""));
        verificar("jugador1 ve su asistencia", club.getGestorEntrenamientos().asistenciasDe(jugador1).size() == 1);

        // ---------------------------------------------------------------------- turnos
        System.out.println("\n== Turnos");
        debeFuncionar("asignar turno a entrenador1", () -> club.getGestorTurnos().asignarTurno(admin, "entrenador1", "Lunes", LocalTime.of(8, 0), LocalTime.of(12, 0)));
        debeFuncionar("asignar turno a entrenador2", () -> club.getGestorTurnos().asignarTurno(admin, "entrenador2", "Lunes", LocalTime.of(8, 0), LocalTime.of(12, 0)));
        debeRechazar("turno que se cruza", () -> club.getGestorTurnos().asignarTurno(admin, "entrenador1", "Lunes", LocalTime.of(11, 0), LocalTime.of(13, 0)));
        debeFuncionar("asignar turno a empleado1", () -> club.getGestorTurnos().asignarTurno(admin, "empleado1", "Martes", LocalTime.of(8, 0), LocalTime.of(12, 0)));
        debeRechazar("solicitar cambio para una fecha pasada", () -> club.getGestorTurnos().solicitarCambio(emp, 0,
                LocalDate.now().minusDays(1), LocalTime.of(9, 0), LocalTime.of(13, 0)));
        final SolicitudCambioTurno solEmp = club.getGestorTurnos().solicitarCambio(emp, 0, manana, LocalTime.of(9, 0), LocalTime.of(13, 0));
        debeFuncionar("admin aprueba el cambio del empleado", () -> club.getGestorTurnos().resolver(admin, solEmp, true));
        verificar("la solicitud queda APROBADA", solEmp.getEstado().equals("APROBADA"));
        final SolicitudCambioTurno solEnt = club.getGestorTurnos().solicitarCambio(e1, 0, manana, LocalTime.of(9, 0), LocalTime.of(13, 0));
        debeRechazar("cambio de entrenador deja la franja sin cobertura (rechazo automatico)",
                () -> club.getGestorTurnos().resolver(admin, solEnt, true));
        verificar("la solicitud queda RECHAZADA", solEnt.getEstado().equals("RECHAZADA"));

        // ------------------------------------------------------------- persistencia
        System.out.println("\n== Persistencia (se vuelve a abrir el club desde los archivos)");
        Club otro = new Club(new RepositorioJSON("datos_prueba"));
        verificar("usuarios: " + otro.getGestorUsuarios().getUsuarios().size(), otro.getGestorUsuarios().getUsuarios().size() == 17);
        verificar("productos: " + otro.getGestorVentas().getProductos().size(), otro.getGestorVentas().getProductos().size() == 5);
        verificar("ventas: " + otro.getGestorVentas().getVentas().size(), otro.getGestorVentas().getVentas().size() == 7);
        verificar("raquetas en inventario",
                ((Articulo) otro.getGestorVentas().getProductos().get(1)).getCantidadDisponible() == 11);
        verificar("equipo Leones con 8 jugadores", otro.getGestorEquipos().buscarEquipo("Leones").getPlantilla().size() == 8);
        verificar("entrenador1 dirige a Leones", ((Entrenador) otro.getGestorUsuarios().getUsuarios().get("entrenador1"))
                .dirige(otro.getGestorEquipos().buscarEquipo("Leones")));
        verificar("partidos guardados: " + otro.getGestorCompetencias().getPartidos().size(), otro.getGestorCompetencias().getPartidos().size() == 4);
        verificar("jugador1 sigue suspendido", ((Socio) otro.getGestorUsuarios().getUsuarios().get("jugador1")).estaSuspendido());
        verificar("sesiones guardadas", otro.getGestorEntrenamientos().getSesiones().size() == 1);
        verificar("asistencia guardada", otro.getGestorEntrenamientos().getSesiones().get(0).getAsistencias().size() == 1);
        verificar("tabla igual despues de recargar", otro.getGestorCompetencias().tablaPosiciones("Torneo de los Cerros")
                .get(otro.getGestorEquipos().buscarEquipo("Leones")) == 4);
        verificar("reservas guardadas", otro.getGestorReservas().getDisciplinas().get(0).getInstalaciones().get(0).getReservas().size() == 2);
        verificar("sugerencia aprobada guardada", ((Empleado) otro.getGestorUsuarios().getUsuarios().get("fisio1"))
                .getSugerencias().get(0).getEstado().equals("APROBADA"));
        verificar("solicitud rechazada guardada", ((Empleado) otro.getGestorUsuarios().getUsuarios().get("entrenador1"))
                .getSolicitudes().get(0).getEstado().equals("RECHAZADA"));

        System.out.println("\n" + (fallos == 0 ? "TODO BIEN" : "HAY " + fallos + " FALLOS"));
    }

    static Map<Integer, Integer> pedido(int idProducto, int cantidad) {
        Map<Integer, Integer> pedido = new LinkedHashMap<Integer, Integer>();
        pedido.put(idProducto, cantidad);
        return pedido;
    }
}
