package consola;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import controlador.Club;
import logica.deportes.Asistencia;
import logica.deportes.Competencia;
import logica.deportes.DeporteConjunto;
import logica.deportes.DisciplinaIndividual;
import logica.deportes.Estadistica;
import logica.deportes.EstadisticaFutbol;
import logica.deportes.Instalacion;
import logica.deportes.Partido;
import logica.deportes.PartidoCompetencia;
import logica.deportes.Reserva;
import logica.deportes.SesionEntrenamiento;
import logica.tienda.Articulo;
import logica.tienda.Bebida;
import logica.tienda.Producto;
import logica.tienda.Snack;
import logica.tienda.Sugerencia;
import logica.tienda.Venta;
import logica.usuarios.Administrador;
import logica.usuarios.Empleado;
import logica.usuarios.Entrenador;
import logica.usuarios.Fisioterapeuta;
import logica.usuarios.SolicitudCambioTurno;
import logica.usuarios.Socio;
import logica.usuarios.Usuario;
import persistencia.RepositorioJSON;

// Consola de demostracion: cada RF corre sus casos de exito y de rechazo sobre los datos de ejemplo
public class Consola {

    interface Paso {
        void ejecutar() throws Exception;
    }

    private static Scanner entrada;
    private static String carpeta;
    private static int inesperados = 0;

    public static void main(String[] args) throws Exception {
        entrada = new Scanner(System.in);
        System.out.println("=== Club Deportivo Seneca - consola de demostracion de requerimientos ===");
        carpeta = elegirCarpeta();
        restaurarDatos(carpeta);
        System.out.println("Datos de ejemplo cargados en '" + carpeta + "'.");
        boolean seguir = true;
        while (seguir) {
            System.out.println();
            System.out.println("Menu principal");
            System.out.println("  1. Sistema");
            System.out.println("  2. Usuario");
            System.out.println("  3. Socio");
            System.out.println("  4. Empleado");
            System.out.println("  5. Administrador");
            System.out.println("  6. Entrenador");
            System.out.println("  7. Ejecutar todos los requerimientos");
            System.out.println("  8. Restaurar datos de ejemplo");
            System.out.println("  0. Salir");
            int opcion = leerOpcion();
            if (opcion == 1) {
                menuSistema();
            } else if (opcion == 2) {
                menuUsuario();
            } else if (opcion == 3) {
                menuSocio();
            } else if (opcion == 4) {
                menuEmpleado();
            } else if (opcion == 5) {
                menuAdministrador();
            } else if (opcion == 6) {
                menuEntrenador();
            } else if (opcion == 7) {
                ejecutarTodos();
            } else if (opcion == 8) {
                restaurarDatos(carpeta);
                System.out.println("Datos de ejemplo restaurados en '" + carpeta + "'.");
            } else if (opcion == 0) {
                seguir = false;
            } else {
                System.out.println("Opcion invalida");
            }
        }
        System.out.println("Hasta luego.");
    }

    public static String elegirCarpeta() {
        System.out.println("Carpeta de datos (Enter para usar 'datos').");
        System.out.println("ATENCION: cada demostracion reemplaza el contenido de esa carpeta con los datos de ejemplo.");
        System.out.print("> ");
        String linea = "";
        if (entrada.hasNextLine()) {
            linea = entrada.nextLine().trim();
        }
        if (linea.isEmpty()) {
            return "datos";
        }
        return linea;
    }

    // Borra los archivos JSON de la carpeta y vuelve a crear los datos de ejemplo
    public static void restaurarDatos(String carpeta) throws Exception {
        File dir = new File(carpeta);
        dir.mkdirs();
        for (File f : dir.listFiles()) {
            if (f.getName().endsWith(".json")) {
                f.delete();
            }
        }
        RepositorioJSON repositorio = new RepositorioJSON(carpeta);
        Club club = new Club(repositorio);

        Administrador admin = new Administrador("admin", "1234");
        club.getGestorUsuarios().agregarUsuario(admin);
        club.getGestorUsuarios().agregarUsuario(new Entrenador("entrenador1", "1", "COD-E1"));
        club.getGestorUsuarios().agregarUsuario(new Entrenador("entrenador2", "1", "COD-E2"));
        club.getGestorUsuarios().agregarUsuario(new Entrenador("entrenador3", "1", "COD-E3"));
        club.getGestorUsuarios().agregarUsuario(new Fisioterapeuta("fisio1", "1", "COD-F1"));
        club.getGestorUsuarios().agregarUsuario(new Empleado("empleado1", "1", "COD-M1"));
        Socio ana = club.getGestorUsuarios().registrarSocio("ana", "123", 20);
        Socio beto = club.getGestorUsuarios().registrarSocio("beto", "123", 25);
        Socio cami = club.getGestorUsuarios().registrarSocio("cami", "123", 30);
        for (int i = 1; i <= 8; i++) {
            club.getGestorUsuarios().registrarSocio("jugador" + i, "1", 22);
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
        club.getGestorReservas().inscribirEnDisciplina(ana, "Padel", "Principiante");
        club.getGestorReservas().inscribirEnDisciplina(cami, "Padel", "Intermedio");
        club.getGestorReservas().inscribirEnDisciplina(beto, "Padel", "Avanzado");

        club.getGestorEquipos().agregarDeporte(new DeporteConjunto("Futbol", 7));
        club.getGestorEquipos().agregarDeporte(new DeporteConjunto("Baloncesto", 5));
        DeporteConjunto futbol = club.getGestorEquipos().buscarDeporte("Futbol");
        club.getGestorCompetencias().agregarCompetencia(
                new Competencia("Torneo de los Cerros", "Universidades de Bogota", "2026", "Mayores", "Liga", 7, futbol));
        club.getGestorCompetencias().agregarCompetencia(
                new Competencia("ASCUN", "ASCUN", "2026", "Mayores", "Fases", 7, futbol));
        club.getGestorEquipos().crearEquipo(admin, "Futbol", "Leones", "Mayores", 15, "entrenador1");
        for (int i = 1; i <= 8; i++) {
            String posicion = "Delantero";
            if (i == 1) {
                posicion = "Portero";
            } else if (i <= 4) {
                posicion = "Defensa";
            }
            club.getGestorEquipos().agregarJugador(admin, "Leones", "jugador" + i, posicion, i);
        }

        club.getGestorTurnos().asignarTurno(admin, "entrenador1", "Lunes", LocalTime.of(8, 0), LocalTime.of(12, 0));
        club.getGestorTurnos().asignarTurno(admin, "entrenador2", "Lunes", LocalTime.of(8, 0), LocalTime.of(12, 0));
        club.getGestorTurnos().asignarTurno(admin, "empleado1", "Martes", LocalTime.of(8, 0), LocalTime.of(12, 0));

        club.getGestorVentas().agregarProducto(new Articulo(1, "Raqueta", 100000, "Raquetas", 5, "Estante A"));
        club.getGestorVentas().agregarProducto(new Bebida(2, "Cafe", 5000, true));
        club.getGestorVentas().agregarProducto(new Bebida(3, "Limonada", 4000, false));
        ArrayList<String> alergenos = new ArrayList<String>();
        alergenos.add("mani");
        club.getGestorVentas().agregarProducto(new Snack(4, "Barra de mani", 3000, alergenos));
        club.getGestorVentas().agregarProducto(new Articulo(5, "Balon", 60000, "Balones", 10, "Estante B"));

        // Ventas de ejemplo con fechas distintas para el reporte por periodo
        club.getGestorVentas().comprar(ana, pedido(1, 1));
        club.getGestorVentas().comprar(beto, pedido(1, 1));
        club.getGestorVentas().comprar(cami, pedido(3, 1));
        List<Venta> ventas = club.getGestorVentas().getVentas();
        ventas.get(1).setFecha(LocalDateTime.now().minusDays(5));
        ventas.get(2).setFecha(LocalDateTime.now().minusDays(20));
        repositorio.guardarVentas(ventas);
    }

    public static void menuSistema() throws Exception {
        System.out.println("Sistema");
        System.out.println("  1. RF1 - Guardar los cambios y recuperarlos al iniciar");
        System.out.println("  2. RF2 - Un socio solo consulta su propia informacion");
        int opcion = leerOpcion();
        if (opcion == 1) {
            sistemaRF1();
        } else if (opcion == 2) {
            sistemaRF2();
        }
    }

    public static void menuUsuario() throws Exception {
        System.out.println("Usuario");
        System.out.println("  1. RF1 - Acceder al perfil con login y password");
        if (leerOpcion() == 1) {
            usuarioRF1();
        }
    }

    public static void menuSocio() throws Exception {
        System.out.println("Socio");
        System.out.println("  1. RF1 - Ver la posicion para el proximo partido");
        System.out.println("  2. RF2 - Ver el estado para el proximo partido (suspension)");
        System.out.println("  3. RF3 - Crear partidos de practica");
        System.out.println("  4. RF4 - Reservar instalaciones");
        System.out.println("  5. RF5 - Bebidas calientes y canchas restringidas");
        System.out.println("  6. RF6 - Comprar varios articulos con IVA");
        System.out.println("  7. RF7 - Alergenos de los snacks");
        System.out.println("  8. RF8 - Cafeteria: impuesto al consumo y propina");
        System.out.println("  9. RF9 - Puntos de fidelidad");
        System.out.println(" 10. RF10 - Descuento compartido por un empleado");
        System.out.println(" 11. RF11 - Inscribirse a una academia o a un equipo");
        System.out.println(" 12. RF12 - Consultar su informacion personal");
        int opcion = leerOpcion();
        if (opcion == 1) {
            socioRF1();
        } else if (opcion == 2) {
            socioRF2();
        } else if (opcion == 3) {
            socioRF3();
        } else if (opcion == 4) {
            socioRF4();
        } else if (opcion == 5) {
            socioRF5();
        } else if (opcion == 6) {
            socioRF6();
        } else if (opcion == 7) {
            socioRF7();
        } else if (opcion == 8) {
            socioRF8();
        } else if (opcion == 9) {
            socioRF9();
        } else if (opcion == 10) {
            socioRF10();
        } else if (opcion == 11) {
            socioRF11();
        } else if (opcion == 12) {
            socioRF12();
        }
    }

    public static void menuEmpleado() throws Exception {
        System.out.println("Empleado");
        System.out.println("  1. RF1 - Solicitar un cambio de turno");
        System.out.println("  2. RF2 - Proponer productos");
        System.out.println("  3. RF3 - Comprar con su descuento (15%)");
        System.out.println("  4. RF4 - Compartir su codigo de descuento");
        int opcion = leerOpcion();
        if (opcion == 1) {
            empleadoRF1();
        } else if (opcion == 2) {
            empleadoRF2();
        } else if (opcion == 3) {
            empleadoRF3();
        } else if (opcion == 4) {
            empleadoRF4();
        }
    }

    public static void menuAdministrador() throws Exception {
        System.out.println("Administrador");
        System.out.println("  1. RF1 - Aprobar o rechazar cambios de turno");
        System.out.println("  2. RF2 - Aprobar o rechazar sugerencias");
        System.out.println("  3. RF3 - Ventas por rubro y periodo");
        System.out.println("  4. RF4 - Consultar el inventario");
        System.out.println("  5. RF5 - Actualizar el inventario");
        System.out.println("  6. RF6 - Crear y eliminar equipos");
        System.out.println("  7. RF7 - Anadir y quitar jugadores");
        System.out.println("  8. RF8 - Administrar turnos");
        System.out.println("  9. RF9 - Historial de reservas, partidos y sesiones");
        int opcion = leerOpcion();
        if (opcion == 1) {
            administradorRF1();
        } else if (opcion == 2) {
            administradorRF2();
        } else if (opcion == 3) {
            administradorRF3();
        } else if (opcion == 4) {
            administradorRF4();
        } else if (opcion == 5) {
            administradorRF5();
        } else if (opcion == 6) {
            administradorRF6();
        } else if (opcion == 7) {
            administradorRF7();
        } else if (opcion == 8) {
            administradorRF8();
        } else if (opcion == 9) {
            administradorRF9();
        }
    }

    public static void menuEntrenador() throws Exception {
        System.out.println("Entrenador");
        System.out.println("  1. RF1 - Crear una sesion de entrenamiento");
        System.out.println("  2. RF2 - Registrar asistencia y nota");
        System.out.println("  3. RF3 - Convocar jugadores a un partido");
        System.out.println("  4. RF4 - Inscribir un equipo en una competencia");
        System.out.println("  5. RF5 - Consultar las estadisticas del equipo");
        int opcion = leerOpcion();
        if (opcion == 1) {
            entrenadorRF1();
        } else if (opcion == 2) {
            entrenadorRF2();
        } else if (opcion == 3) {
            entrenadorRF3();
        } else if (opcion == 4) {
            entrenadorRF4();
        } else if (opcion == 5) {
            entrenadorRF5();
        }
    }

    public static void ejecutarTodos() throws Exception {
        inesperados = 0;
        sistemaRF1();
        sistemaRF2();
        usuarioRF1();
        socioRF1();
        socioRF2();
        socioRF3();
        socioRF4();
        socioRF5();
        socioRF6();
        socioRF7();
        socioRF8();
        socioRF9();
        socioRF10();
        socioRF11();
        socioRF12();
        empleadoRF1();
        empleadoRF2();
        empleadoRF3();
        empleadoRF4();
        administradorRF1();
        administradorRF2();
        administradorRF3();
        administradorRF4();
        administradorRF5();
        administradorRF6();
        administradorRF7();
        administradorRF8();
        administradorRF9();
        entrenadorRF1();
        entrenadorRF2();
        entrenadorRF3();
        entrenadorRF4();
        entrenadorRF5();
        System.out.println();
        if (inesperados == 0) {
            System.out.println("RESUMEN: todos los casos dieron el resultado esperado.");
        } else {
            System.out.println("RESUMEN: " + inesperados + " casos con resultado INESPERADO.");
        }
    }

    // ------------------------------------------------------------------ Sistema

    private static void sistemaRF1() throws Exception {
        Club club = preparar("Sistema RF1 - Guardar los cambios y recuperarlos al iniciar");
        Usuario admin = usuario(club, "admin");
        estado("raquetas en inventario: " + stock(club, 1));
        exito("el administrador repone 10 raquetas", () -> club.getGestorInventario().reponerInventario(admin, 1, 10));
        Club recargado = new Club(new RepositorioJSON(carpeta));
        estado("se vuelve a abrir el club desde '" + carpeta + "': raquetas = " + stock(recargado, 1));
        comprobar("el cambio quedo guardado en los archivos", stock(recargado, 1) == 13);
        rechazo("ana intenta comprar 99 raquetas", () -> recargado.getGestorVentas().comprar(usuario(recargado, "ana"),
                pedido(1, 99)));
        Club otraVez = new Club(new RepositorioJSON(carpeta));
        estado("despues del rechazo, al recargar: raquetas = " + stock(otraVez, 1));
        comprobar("una operacion rechazada no deja cambios guardados", stock(otraVez, 1) == 13);
    }

    private static void sistemaRF2() throws Exception {
        Club club = preparar("Sistema RF2 - Un socio solo consulta su propia informacion");
        Socio ana = socio(club, "ana");
        Socio beto = socio(club, "beto");
        exito("ana consulta su propia informacion", () -> mostrarInformacion(club, ana, ana));
        rechazo("ana intenta consultar la informacion de beto", () -> mostrarInformacion(club, ana, beto));
        exito("el administrador consulta la informacion de beto", () -> mostrarInformacion(club, usuario(club, "admin"), beto));
    }

    // La consola solo muestra la informacion de un socio a el mismo o a un administrador
    private static void mostrarInformacion(Club club, Usuario quien, Socio socio) throws Exception {
        if (quien instanceof Socio && quien != socio) {
            throw new Exception("Un socio solo puede consultar su propia informacion");
        }
        estado(socio.getLogin() + ": edad " + socio.getEdad() + ", puntos " + socio.getPuntosFidelidad() + ", estado "
                + club.getGestorUsuarios().estadoDe(socio));
    }

    // ------------------------------------------------------------------ Usuario

    private static void usuarioRF1() throws Exception {
        Club club = preparar("Usuario RF1 - Acceder al perfil con login y password");
        exito("ana entra con su password", () -> {
            Usuario u = club.getGestorUsuarios().autenticar("ana", "123");
            estado("perfil: " + u.getLogin() + " (" + u.getClass().getSimpleName() + ")");
        });
        exito("el administrador entra con su password", () -> {
            Usuario u = club.getGestorUsuarios().autenticar("admin", "1234");
            estado("perfil: " + u.getLogin() + " (" + u.getClass().getSimpleName() + ")");
        });
        rechazo("ana con un password incorrecto", () -> club.getGestorUsuarios().autenticar("ana", "xxx"));
        rechazo("un login que no existe", () -> club.getGestorUsuarios().autenticar("nadie", "123"));
    }

    // ------------------------------------------------------------------ Socio

    private static void socioRF1() throws Exception {
        Club club = preparar("Socio RF1 - Ver la posicion para el proximo partido");
        Entrenador e1 = (Entrenador) usuario(club, "entrenador1");
        paso("entrenador1 inscribe a Leones en el Torneo de los Cerros", () -> club.getGestorCompetencias()
                .inscribirEquipo(e1, "Leones", "Torneo de los Cerros", jugadores(1, 8)));
        PartidoCompetencia partido = club.getGestorCompetencias().programarPartido(e1, "Leones", "Torneo de los Cerros",
                LocalDate.now().plusDays(3), "Universidad X", "Estadio Norte", "Jornada 1");
        paso("entrenador1 convoca de jugador1 a jugador7", () -> club.getGestorCompetencias().convocar(e1, partido,
                jugadores(1, 7)));
        exito("jugador1 consulta su posicion", () -> estado("jugador1 jugara de "
                + club.getGestorCompetencias().proximaPosicion(socio(club, "jugador1"))));
        exito("jugador3 consulta su posicion", () -> estado("jugador3 jugara de "
                + club.getGestorCompetencias().proximaPosicion(socio(club, "jugador3"))));
        rechazo("jugador8 no fue convocado", () -> club.getGestorCompetencias().proximaPosicion(socio(club, "jugador8")));
    }

    private static void socioRF2() throws Exception {
        Club club = preparar("Socio RF2 - Ver el estado para el proximo partido (suspension)");
        Entrenador e1 = (Entrenador) usuario(club, "entrenador1");
        PartidoCompetencia partido = partidoJugadoHoy(club, e1);
        Socio jugador2 = socio(club, "jugador2");
        estado("jugador2 antes de la tarjeta: " + club.getGestorUsuarios().estadoDe(jugador2));
        paso("jugador2 recibe una tarjeta roja", () -> club.getGestorCompetencias().registrarEstadistica(e1, partido,
                new EstadisticaFutbol(jugador2, partido, 0, 0, 0, 1)));
        exito("jugador1 consulta su estado", () -> estado("jugador1: "
                + club.getGestorUsuarios().estadoDe(socio(club, "jugador1"))));
        exito("jugador2 consulta su estado", () -> estado("jugador2: " + club.getGestorUsuarios().estadoDe(jugador2)));
        PartidoCompetencia siguiente = club.getGestorCompetencias().programarPartido(e1, "Leones",
                "Torneo de los Cerros", LocalDate.now().plusDays(1), "Universidad Y", "Estadio Sur", "Jornada 2");
        rechazo("convocar a jugador2 mientras esta suspendido", () -> club.getGestorCompetencias().convocar(e1, siguiente,
                jugadores(1, 7)));
    }

    private static void socioRF3() throws Exception {
        Club club = preparar("Socio RF3 - Crear partidos de practica");
        Socio ana = socio(club, "ana");
        estado("niveles en Padel: ana Principiante, cami Intermedio, beto Avanzado");
        rechazo("ana (Principiante) contra beto (Avanzado)", () -> club.getGestorCompetencias().crearPartidoPractica(ana,
                "Padel", "Sencillos", LocalDate.now(), lista("beto")));
        exito("ana (Principiante) contra cami (Intermedio)", () -> club.getGestorCompetencias().crearPartidoPractica(ana,
                "Padel", "Sencillos", LocalDate.now(), lista("cami")));
        exito("ana contra entrenador1 (los entrenadores no tienen restriccion de nivel)", () -> club
                .getGestorCompetencias().crearPartidoPractica(ana, "Padel", "Sencillos", LocalDate.now(),
                        lista("entrenador1")));
        rechazo("ana en una disciplina en la que no esta inscrita", () -> club.getGestorCompetencias()
                .crearPartidoPractica(ana, "Tenis", "Sencillos", LocalDate.now(), lista("cami")));
    }

    private static void socioRF4() throws Exception {
        Club club = preparar("Socio RF4 - Reservar instalaciones");
        LocalDate manana = LocalDate.now().plusDays(1);
        estado("reservas en Cancha Padel 1: " + reservasDe(club, 1));
        exito("ana reserva la cancha de padel manana de 10:00 a 11:00 para 4 (Dobles)", () -> club.getGestorReservas()
                .reservar(socio(club, "ana"), 1, manana, LocalTime.of(10, 0), LocalTime.of(11, 0), 4, "Dobles"));
        estado("reservas en Cancha Padel 1: " + reservasDe(club, 1));
        rechazo("beto pide la misma cancha de 10:30 a 11:30", () -> club.getGestorReservas().reservar(socio(club, "beto"),
                1, manana, LocalTime.of(10, 30), LocalTime.of(11, 30), 4, "Dobles"));
        rechazo("beto pide padel para 2 jugadores (Sencillos)", () -> club.getGestorReservas().reservar(socio(club, "beto"),
                1, manana, LocalTime.of(15, 0), LocalTime.of(16, 0), 2, "Sencillos"));
        rechazo("el administrador intenta reservar", () -> club.getGestorReservas().reservar(usuario(club, "admin"), 1,
                manana, LocalTime.of(15, 0), LocalTime.of(16, 0), 4, "Dobles"));
        rechazo("beto reserva en una fecha pasada", () -> club.getGestorReservas().reservar(socio(club, "beto"), 1,
                LocalDate.now().minusDays(1), LocalTime.of(15, 0), LocalTime.of(16, 0), 4, "Dobles"));
    }

    private static void socioRF5() throws Exception {
        Club club = preparar("Socio RF5 - Bebidas calientes y canchas restringidas");
        LocalDateTime en10 = LocalDateTime.now().plusMinutes(10);
        LocalTime inicio = en10.toLocalTime();
        LocalTime fin = LocalTime.of(23, 59);
        if (inicio.plusMinutes(30).isAfter(inicio)) {
            fin = inicio.plusMinutes(30);
        }
        final LocalTime finReserva = fin;
        paso("cami reserva padel (cancha restringida) que empieza en 10 minutos", () -> club.getGestorReservas()
                .reservar(socio(club, "cami"), 1, en10.toLocalDate(), inicio, finReserva, 4, "Dobles"));
        rechazo("vender un cafe caliente a cami", () -> club.getGestorVentas().comprar(socio(club, "cami"), pedido(2, 1)));
        exito("vender una limonada fria a cami", () -> club.getGestorVentas().comprar(socio(club, "cami"), pedido(3, 1)));
        exito("beto (sin reserva) compra un cafe caliente", () -> club.getGestorVentas().comprar(socio(club, "beto"),
                pedido(2, 1)));
        rechazo("beto entra a la cancha de padel con el cafe", () -> club.getGestorVentas().verificarAcceso(
                socio(club, "beto"), 1));
        exito("beto entra a la cancha de tenis (no restringida)", () -> club.getGestorVentas().verificarAcceso(
                socio(club, "beto"), 3));
    }

    private static void socioRF6() throws Exception {
        Club club = preparar("Socio RF6 - Comprar varios articulos con IVA");
        estado("inventario: raquetas " + stock(club, 1) + ", balones " + stock(club, 5));
        Map<Integer, Integer> compra = pedido(1, 2);
        compra.put(5, 1);
        exito("ana compra 2 raquetas y 1 balon en una sola transaccion", () -> {
            Venta v = club.getGestorVentas().comprar(socio(club, "ana"), compra);
            estado("total = (2 x 100000 + 60000) x 1.19 = " + Math.round(v.calcularTotal()));
            comprobar("el total incluye el IVA del 19%", Math.round(v.calcularTotal()) == 309400);
        });
        estado("inventario: raquetas " + stock(club, 1) + ", balones " + stock(club, 5));
        rechazo("ana pide 99 raquetas", () -> club.getGestorVentas().comprar(socio(club, "ana"), pedido(1, 99)));
        estado("inventario sin cambios: raquetas " + stock(club, 1));
        Map<Integer, Integer> mezcla = pedido(1, 1);
        mezcla.put(3, 1);
        rechazo("mezclar un articulo de la tienda con una limonada", () -> club.getGestorVentas().comprar(
                socio(club, "ana"), mezcla));
    }

    private static void socioRF7() throws Exception {
        Club club = preparar("Socio RF7 - Alergenos de los snacks");
        exito("se informan los alergenos antes de vender la barra de mani", () -> {
            estado("alergenos de 'Barra de mani': " + club.getGestorVentas().alergenosDe(4));
            club.getGestorVentas().comprar(socio(club, "ana"), pedido(4, 1));
        });
        rechazo("pedir alergenos de un producto que no es snack (cafe)", () -> club.getGestorVentas().alergenosDe(2));
    }

    private static void socioRF8() throws Exception {
        Club club = preparar("Socio RF8 - Cafeteria: impuesto al consumo y propina");
        exito("ana compra una limonada con la propina sugerida (10%)", () -> {
            Venta v = club.getGestorVentas().comprar(socio(club, "ana"), pedido(3, 1));
            estado("total = 4000 x (1 + 0.08 + 0.10) = " + Math.round(v.calcularTotal()));
            comprobar("impuesto al consumo y propina sugerida", Math.round(v.calcularTotal()) == 4720);
        });
        exito("ana cambia la propina a 5%", () -> {
            Venta v = club.getGestorVentas().comprar(socio(club, "ana"), pedido(3, 1), 0.05, 0);
            estado("total = 4000 x (1 + 0.08 + 0.05) = " + Math.round(v.calcularTotal()));
            comprobar("la propina modificada se aplica", Math.round(v.calcularTotal()) == 4520);
        });
        rechazo("una propina de 150%", () -> club.getGestorVentas().comprar(socio(club, "ana"), pedido(3, 1), 1.5, 0));
    }

    private static void socioRF9() throws Exception {
        Club club = preparar("Socio RF9 - Puntos de fidelidad");
        Socio ana = socio(club, "ana");
        estado("puntos de ana: " + ana.getPuntosFidelidad());
        exito("ana compra un balon (60000 x 1.19 = 71400)", () -> club.getGestorVentas().comprar(ana, pedido(5, 1)));
        estado("puntos de ana: " + ana.getPuntosFidelidad() + " (gano el 2% = 1428)");
        exito("ana redime 1000 puntos en una limonada", () -> {
            Venta v = club.getGestorVentas().comprar(ana, pedido(3, 1), 0.10, 1000);
            estado("total = (4000 - 1000) x 1.18 = " + Math.round(v.calcularTotal()));
        });
        estado("puntos de ana: " + ana.getPuntosFidelidad());
        Socio cami = socio(club, "cami");
        estado("puntos de cami: " + cami.getPuntosFidelidad());
        rechazo("cami intenta redimir 500 puntos", () -> club.getGestorVentas().comprar(cami, pedido(1, 1), 0.10, 500));
    }

    private static void socioRF10() throws Exception {
        Club club = preparar("Socio RF10 - Descuento compartido por un empleado");
        paso("empleado1 comparte su codigo con beto", () -> club.getGestorUsuarios().compartirCodigo(
                usuario(club, "empleado1"), "beto"));
        exito("beto compra una raqueta con el 8% de descuento", () -> {
            Venta v = club.getGestorVentas().comprar(socio(club, "beto"), pedido(1, 1));
            estado("total = 100000 x 0.92 x 1.19 = " + Math.round(v.calcularTotal()));
            comprobar("se aplico el 8%", Math.round(v.calcularTotal()) == 109480);
        });
        exito("cami (sin codigo) compra una raqueta a precio normal", () -> {
            Venta v = club.getGestorVentas().comprar(socio(club, "cami"), pedido(1, 1));
            estado("total = 100000 x 1.19 = " + Math.round(v.calcularTotal()));
        });
        rechazo("ana (socia) intenta compartir un codigo con cami", () -> club.getGestorUsuarios().compartirCodigo(
                socio(club, "ana"), "cami"));
    }

    private static void socioRF11() throws Exception {
        Club club = preparar("Socio RF11 - Inscribirse a una academia o a un equipo");
        Socio ana = socio(club, "ana");
        exito("ana se inscribe en la academia de Tenis (Intermedio)", () -> club.getGestorReservas()
                .inscribirEnDisciplina(ana, "Tenis", "Intermedio"));
        rechazo("ana se inscribe otra vez en Tenis", () -> club.getGestorReservas().inscribirEnDisciplina(ana, "Tenis",
                "Intermedio"));
        estado("plantilla de Leones: " + plantilla(club, "Leones") + " jugadores");
        exito("ana se inscribe a si misma en el equipo Leones", () -> club.getGestorEquipos().agregarJugador(ana,
                "Leones", "ana", "Defensa", 30));
        estado("plantilla de Leones: " + plantilla(club, "Leones") + " jugadores");
        rechazo("ana intenta inscribir a beto", () -> club.getGestorEquipos().agregarJugador(ana, "Leones", "beto",
                "Defensa", 31));
    }

    private static void socioRF12() throws Exception {
        Club club = preparar("Socio RF12 - Consultar su informacion personal");
        Entrenador e1 = (Entrenador) usuario(club, "entrenador1");
        SesionEntrenamiento sesion = club.getGestorEntrenamientos().crearSesion(e1, LocalDate.now().plusDays(1),
                LocalTime.of(17, 0), 90, "Tecnico", 3, "Leones");
        paso("entrenador1 registra la asistencia de jugador1", () -> club.getGestorEntrenamientos().registrarAsistencia(
                e1, sesion, "jugador1", "ASISTIO", 5, "Muy bien"));
        PartidoCompetencia partido = partidoJugadoHoy(club, e1);
        Socio jugador1 = socio(club, "jugador1");
        paso("se registran 2 goles de jugador1", () -> club.getGestorCompetencias().registrarEstadistica(e1, partido,
                new EstadisticaFutbol(jugador1, partido, 2, 1, 0, 0)));
        exito("jugador1 consulta su informacion", () -> {
            estado("estado: " + club.getGestorUsuarios().estadoDe(jugador1));
            for (Asistencia a : club.getGestorEntrenamientos().asistenciasDe(jugador1)) {
                estado("asistencia: " + a.getEstado() + ", nota " + a.getNotaDesempeno() + ", '" + a.getComentario() + "'");
            }
            for (Estadistica e : jugador1.getEstadisticas()) {
                EstadisticaFutbol ef = (EstadisticaFutbol) e;
                estado("estadistica: " + ef.getGoles() + " goles, " + ef.getAsistencias() + " asistencias");
            }
        });
        rechazo("jugador1 intenta ver las estadisticas de todo el equipo", () -> club.getGestorCompetencias()
                .estadisticasDeEquipo(jugador1, "Leones"));
    }

    // ------------------------------------------------------------------ Empleado

    private static void empleadoRF1() throws Exception {
        Club club = preparar("Empleado RF1 - Solicitar un cambio de turno");
        Usuario emp = usuario(club, "empleado1");
        exito("empleado1 pide cambiar su turno para manana de 9:00 a 13:00", () -> club.getGestorTurnos()
                .solicitarCambio(emp, 0, LocalDate.now().plusDays(1), LocalTime.of(9, 0), LocalTime.of(13, 0)));
        estado("solicitudes pendientes: " + club.getGestorTurnos().pendientes().size());
        rechazo("pedir un cambio para una fecha que ya paso", () -> club.getGestorTurnos().solicitarCambio(emp, 0,
                LocalDate.now().minusDays(1), LocalTime.of(9, 0), LocalTime.of(13, 0)));
        rechazo("pedir un cambio de un turno que no tiene", () -> club.getGestorTurnos().solicitarCambio(emp, 5,
                LocalDate.now().plusDays(1), LocalTime.of(9, 0), LocalTime.of(13, 0)));
    }

    private static void empleadoRF2() throws Exception {
        Club club = preparar("Empleado RF2 - Proponer productos");
        Usuario emp = usuario(club, "empleado1");
        Usuario admin = usuario(club, "admin");
        estado("sugerencias pendientes: " + club.getGestorInventario().sugerenciasPendientes(admin).size());
        exito("empleado1 propone una galleta de avena", () -> club.getGestorInventario().proponerProducto(emp,
                new Snack(club.getGestorInventario().siguienteIdProducto(), "Galleta de avena", 2500,
                        new ArrayList<String>())));
        estado("sugerencias pendientes: " + club.getGestorInventario().sugerenciasPendientes(admin).size());
        rechazo("proponer un snack sin precio", () -> club.getGestorInventario().proponerProducto(emp, new Snack(
                club.getGestorInventario().siguienteIdProducto(), "Galleta", 0, new ArrayList<String>())));
    }

    private static void empleadoRF3() throws Exception {
        Club club = preparar("Empleado RF3 - Comprar con su descuento (15%)");
        Usuario emp = usuario(club, "empleado1");
        exito("empleado1 compra una raqueta", () -> {
            Venta v = club.getGestorVentas().comprar(emp, pedido(1, 1));
            estado("total = 100000 x 0.85 x 1.19 = " + Math.round(v.calcularTotal()));
            comprobar("se aplico el 15% del empleado", Math.round(v.calcularTotal()) == 101150);
        });
        estado("puntos de empleado1: " + emp.getPuntosFidelidad());
        rechazo("empleado1 intenta redimir 5000 puntos", () -> club.getGestorVentas().comprar(emp, pedido(1, 1), 0.10,
                5000));
    }

    private static void empleadoRF4() throws Exception {
        Club club = preparar("Empleado RF4 - Compartir su codigo de descuento");
        Socio beto = socio(club, "beto");
        estado("descuento de beto: " + Math.round(beto.getPorcentajeDescuento() * 100) + "%");
        exito("empleado1 comparte su codigo con beto", () -> club.getGestorUsuarios().compartirCodigo(
                usuario(club, "empleado1"), "beto"));
        estado("descuento de beto: " + Math.round(beto.getPorcentajeDescuento() * 100) + "%");
        rechazo("compartir otra vez con beto", () -> club.getGestorUsuarios().compartirCodigo(usuario(club, "fisio1"),
                "beto"));
        rechazo("compartir con un login que no es socio", () -> club.getGestorUsuarios().compartirCodigo(
                usuario(club, "empleado1"), "admin"));
    }

    // ------------------------------------------------------------------ Administrador

    private static void administradorRF1() throws Exception {
        Club club = preparar("Administrador RF1 - Aprobar o rechazar cambios de turno");
        Usuario admin = usuario(club, "admin");
        LocalDate manana = LocalDate.now().plusDays(1);
        SolicitudCambioTurno deEmpleado = club.getGestorTurnos().solicitarCambio(usuario(club, "empleado1"), 0, manana,
                LocalTime.of(9, 0), LocalTime.of(13, 0));
        SolicitudCambioTurno deEntrenador = club.getGestorTurnos().solicitarCambio(usuario(club, "entrenador1"), 0, manana,
                LocalTime.of(9, 0), LocalTime.of(13, 0));
        estado("solicitudes pendientes: " + club.getGestorTurnos().pendientes().size());
        exito("el administrador aprueba el cambio de empleado1", () -> club.getGestorTurnos().resolver(admin, deEmpleado,
                true));
        estado("solicitud de empleado1: " + deEmpleado.getEstado());
        rechazo("aprobar el cambio de entrenador1 (quedarian menos de 2 entrenadores)", () -> club.getGestorTurnos()
                .resolver(admin, deEntrenador, true));
        estado("solicitud de entrenador1: " + deEntrenador.getEstado());
        rechazo("un socio intenta resolver una solicitud", () -> club.getGestorTurnos().resolver(socio(club, "ana"),
                deEntrenador, false));
    }

    private static void administradorRF2() throws Exception {
        Club club = preparar("Administrador RF2 - Aprobar o rechazar sugerencias");
        Usuario admin = usuario(club, "admin");
        Sugerencia galleta = club.getGestorInventario().proponerProducto(usuario(club, "empleado1"), new Snack(
                club.getGestorInventario().siguienteIdProducto(), "Galleta de avena", 2500, new ArrayList<String>()));
        Sugerencia jugo = club.getGestorInventario().proponerProducto(usuario(club, "fisio1"), new Bebida(
                club.getGestorInventario().siguienteIdProducto(), "Jugo verde", 6000, false));
        estado("productos en catalogo: " + club.getGestorVentas().getProductos().size());
        exito("aprobar la galleta de avena", () -> club.getGestorInventario().resolverSugerencia(admin, galleta, true));
        exito("rechazar el jugo verde", () -> club.getGestorInventario().resolverSugerencia(admin, jugo, false));
        estado("productos en catalogo: " + club.getGestorVentas().getProductos().size() + "; galleta "
                + galleta.getEstado() + ", jugo " + jugo.getEstado());
        rechazo("aprobar dos veces la galleta", () -> club.getGestorInventario().resolverSugerencia(admin, galleta, true));
        rechazo("un socio intenta aprobar una sugerencia", () -> club.getGestorInventario().resolverSugerencia(
                socio(club, "ana"), jugo, true));
    }

    private static void administradorRF3() throws Exception {
        Club club = preparar("Administrador RF3 - Ventas por rubro y periodo");
        Usuario admin = usuario(club, "admin");
        LocalDate hoy = LocalDate.now();
        estado("ventas de ejemplo: tienda hoy (ana), tienda hace 5 dias (beto), cafeteria hace 20 dias (cami)");
        exito("tienda, hoy", () -> mostrarVentas(club.getGestorVentas().ventasPorRubro(admin, "TIENDA", hoy, hoy)));
        exito("tienda, ultima semana", () -> mostrarVentas(club.getGestorVentas().ventasPorRubro(admin, "tienda",
                hoy.minusDays(6), hoy)));
        exito("cafeteria, ultimo mes", () -> mostrarVentas(club.getGestorVentas().ventasPorRubro(admin, "Cafetería",
                hoy.minusDays(29), hoy)));
        rechazo("un socio pide el reporte", () -> club.getGestorVentas().ventasPorRubro(socio(club, "ana"), "TIENDA", hoy,
                hoy));
        rechazo("un rubro que no existe (mensualidades)", () -> club.getGestorVentas().ventasPorRubro(admin,
                "MENSUALIDADES", hoy.minusDays(29), hoy));
        rechazo("un periodo al reves", () -> club.getGestorVentas().ventasPorRubro(admin, "TIENDA", hoy, hoy.minusDays(6)));
    }

    private static void mostrarVentas(List<Venta> ventas) {
        double total = 0;
        for (Venta v : ventas) {
            total += v.calcularTotal();
            estado(v.getFecha().toLocalDate() + "  " + v.getComprador().getLogin() + "  " + Math.round(v.calcularTotal()));
        }
        estado(ventas.size() + " ventas, total " + Math.round(total));
    }

    private static void administradorRF4() throws Exception {
        Club club = preparar("Administrador RF4 - Consultar el inventario");
        Usuario admin = usuario(club, "admin");
        exito("el administrador lista los productos", () -> {
            for (Producto p : club.getGestorInventario().listarProductos(admin)) {
                String detalle = "";
                if (p instanceof Articulo) {
                    Articulo a = (Articulo) p;
                    detalle = ", " + a.getCantidadDisponible() + " unidades en " + a.getUbicacion();
                }
                estado(p.getIdProducto() + ". " + p.getNombre() + " $" + Math.round(p.getPrecio()) + detalle);
            }
        });
        exito("el administrador consulta la raqueta", () -> estado("producto 1: "
                + club.getGestorInventario().consultarProducto(admin, 1).getNombre()));
        rechazo("un socio consulta el inventario", () -> club.getGestorInventario().consultarProducto(socio(club, "ana"),
                1));
        rechazo("consultar un producto que no existe", () -> club.getGestorInventario().consultarProducto(admin, 99));
    }

    private static void administradorRF5() throws Exception {
        Club club = preparar("Administrador RF5 - Actualizar el inventario");
        Usuario admin = usuario(club, "admin");
        estado("raquetas: " + stock(club, 1) + " en " + ubicacion(club, 1));
        exito("reponer 10 raquetas", () -> club.getGestorInventario().reponerInventario(admin, 1, 10));
        exito("mover las raquetas a la Bodega 2", () -> club.getGestorInventario().moverArticulo(admin, 1, "Bodega 2"));
        estado("raquetas: " + stock(club, 1) + " en " + ubicacion(club, 1));
        rechazo("reponer una cantidad negativa", () -> club.getGestorInventario().reponerInventario(admin, 1, -3));
        rechazo("mover el cafe (no es un articulo de inventario)", () -> club.getGestorInventario().moverArticulo(admin,
                2, "Bodega 2"));
        rechazo("un socio intenta mover un articulo", () -> club.getGestorInventario().moverArticulo(socio(club, "ana"),
                1, "Estante C"));
    }

    private static void administradorRF6() throws Exception {
        Club club = preparar("Administrador RF6 - Crear y eliminar equipos");
        Usuario admin = usuario(club, "admin");
        exito("crear el equipo Tigres de Baloncesto", () -> club.getGestorEquipos().crearEquipo(admin, "Baloncesto",
                "Tigres", "Mayores", 12, "entrenador2"));
        rechazo("crear otra vez un equipo llamado Leones", () -> club.getGestorEquipos().crearEquipo(admin, "Futbol",
                "Leones", "Mayores", 15, "entrenador2"));
        exito("eliminar el equipo Tigres", () -> club.getGestorEquipos().eliminarEquipo(admin, "Tigres"));
        rechazo("eliminar un equipo que no existe", () -> club.getGestorEquipos().eliminarEquipo(admin, "Fantasmas"));
        rechazo("un socio intenta crear un equipo", () -> club.getGestorEquipos().crearEquipo(socio(club, "ana"),
                "Futbol", "Otros", "Mayores", 15, "entrenador2"));
    }

    private static void administradorRF7() throws Exception {
        Club club = preparar("Administrador RF7 - Anadir y quitar jugadores");
        Usuario admin = usuario(club, "admin");
        estado("plantilla de Leones: " + plantilla(club, "Leones") + " jugadores");
        exito("anadir a ana con la camiseta 30", () -> club.getGestorEquipos().agregarJugador(admin, "Leones", "ana",
                "Defensa", 30));
        rechazo("anadir otra vez a ana", () -> club.getGestorEquipos().agregarJugador(admin, "Leones", "ana", "Defensa",
                31));
        rechazo("anadir a beto con la camiseta 1 (ya la usa jugador1)", () -> club.getGestorEquipos().agregarJugador(admin,
                "Leones", "beto", "Defensa", 1));
        rechazo("anadir un login que no existe", () -> club.getGestorEquipos().agregarJugador(admin, "Leones", "nadie",
                "Defensa", 40));
        exito("quitar a ana", () -> club.getGestorEquipos().quitarJugador(admin, "Leones", "ana"));
        estado("plantilla de Leones: " + plantilla(club, "Leones") + " jugadores");
        rechazo("quitar a ana, que ya no esta", () -> club.getGestorEquipos().quitarJugador(admin, "Leones", "ana"));
    }

    private static void administradorRF8() throws Exception {
        Club club = preparar("Administrador RF8 - Administrar turnos");
        Usuario admin = usuario(club, "admin");
        exito("asignar a entrenador3 el miercoles de 8:00 a 12:00", () -> club.getGestorTurnos().asignarTurno(admin,
                "entrenador3", "Miercoles", LocalTime.of(8, 0), LocalTime.of(12, 0)));
        estado("turnos de entrenador3: " + ((Empleado) usuario(club, "entrenador3")).getTurnos().size());
        rechazo("asignar a entrenador1 un turno que se cruza (lunes 11:00 a 13:00)", () -> club.getGestorTurnos()
                .asignarTurno(admin, "entrenador1", "Lunes", LocalTime.of(11, 0), LocalTime.of(13, 0)));
        rechazo("un socio intenta asignar turnos", () -> club.getGestorTurnos().asignarTurno(socio(club, "ana"),
                "entrenador3", "Jueves", LocalTime.of(8, 0), LocalTime.of(12, 0)));
    }

    private static void administradorRF9() throws Exception {
        Club club = preparar("Administrador RF9 - Historial de reservas, partidos y sesiones");
        Entrenador e1 = (Entrenador) usuario(club, "entrenador1");
        LocalDate manana = LocalDate.now().plusDays(1);
        paso("ana reserva padel manana", () -> club.getGestorReservas().reservar(socio(club, "ana"), 1, manana,
                LocalTime.of(10, 0), LocalTime.of(11, 0), 4, "Dobles"));
        paso("ana juega un partido de practica contra cami", () -> club.getGestorCompetencias().crearPartidoPractica(
                socio(club, "ana"), "Padel", "Sencillos", LocalDate.now(), lista("cami")));
        partidoJugadoHoy(club, e1);
        paso("entrenador1 programa una sesion", () -> club.getGestorEntrenamientos().crearSesion(e1, manana,
                LocalTime.of(17, 0), 90, "Tecnico", 3, "Leones"));
        System.out.println("  Reservas:");
        for (DisciplinaIndividual d : club.getGestorReservas().getDisciplinas()) {
            for (Instalacion i : d.getInstalaciones()) {
                for (Reserva r : i.getReservas()) {
                    estado(i.getNombre() + "  " + r.getFecha() + " " + r.getHoraInicio() + "-" + r.getHoraFin() + "  "
                            + r.getSocio().getLogin() + "  " + r.getCantidadJugadores() + " jugadores");
                }
            }
        }
        System.out.println("  Partidos:");
        for (Partido p : club.getGestorCompetencias().getPartidos()) {
            String marcador = p.getMarcador();
            if (marcador.isEmpty()) {
                marcador = "sin marcador";
            }
            estado(p.getFecha() + "  " + p.getClass().getSimpleName() + "  " + marcador);
        }
        System.out.println("  Sesiones de entrenamiento:");
        for (SesionEntrenamiento s : club.getGestorEntrenamientos().getSesiones()) {
            estado(s.getFecha() + " " + s.getHora() + "  " + s.getTipo() + "  " + s.getDuracion() + " min  "
                    + s.getEntrenador().getLogin() + "  " + s.getInstalacion().getNombre());
        }
    }

    // ------------------------------------------------------------------ Entrenador

    private static void entrenadorRF1() throws Exception {
        Club club = preparar("Entrenador RF1 - Crear una sesion de entrenamiento");
        Entrenador e1 = (Entrenador) usuario(club, "entrenador1");
        LocalDate manana = LocalDate.now().plusDays(1);
        exito("entrenador1 crea una sesion manana a las 17:00 en la cancha de tenis", () -> club
                .getGestorEntrenamientos().crearSesion(e1, manana, LocalTime.of(17, 0), 90, "Tecnico", 3, "Leones"));
        estado("sesiones programadas: " + club.getGestorEntrenamientos().getSesiones().size());
        rechazo("otra sesion a las 17:30 en la misma cancha", () -> club.getGestorEntrenamientos().crearSesion(e1, manana,
                LocalTime.of(17, 30), 60, "Fisico", 3, "Leones"));
        rechazo("entrenador2 crea una sesion para Leones (no es su equipo)", () -> club.getGestorEntrenamientos()
                .crearSesion(usuario(club, "entrenador2"), manana, LocalTime.of(19, 0), 60, "Fisico", 3, "Leones"));
    }

    private static void entrenadorRF2() throws Exception {
        Club club = preparar("Entrenador RF2 - Registrar asistencia y nota");
        Entrenador e1 = (Entrenador) usuario(club, "entrenador1");
        SesionEntrenamiento sesion = club.getGestorEntrenamientos().crearSesion(e1, LocalDate.now().plusDays(1),
                LocalTime.of(17, 0), 90, "Tecnico", 3, "Leones");
        exito("jugador1 asistio, nota 5", () -> club.getGestorEntrenamientos().registrarAsistencia(e1, sesion, "jugador1",
                "ASISTIO", 5, "Muy bien"));
        exito("jugador2 llego tarde, sin nota", () -> club.getGestorEntrenamientos().registrarAsistencia(e1, sesion,
                "jugador2", "LLEGO_TARDE", 0, ""));
        estado("asistencias registradas: " + sesion.getAsistencias().size());
        rechazo("jugador3 con nota 9", () -> club.getGestorEntrenamientos().registrarAsistencia(e1, sesion, "jugador3",
                "ASISTIO", 9, ""));
        rechazo("cami, que no es del equipo", () -> club.getGestorEntrenamientos().registrarAsistencia(e1, sesion, "cami",
                "ASISTIO", 3, ""));
    }

    private static void entrenadorRF3() throws Exception {
        Club club = preparar("Entrenador RF3 - Convocar jugadores a un partido");
        Entrenador e1 = (Entrenador) usuario(club, "entrenador1");
        paso("entrenador1 inscribe a Leones en el Torneo de los Cerros", () -> club.getGestorCompetencias()
                .inscribirEquipo(e1, "Leones", "Torneo de los Cerros", jugadores(1, 8)));
        PartidoCompetencia partido = club.getGestorCompetencias().programarPartido(e1, "Leones", "Torneo de los Cerros",
                LocalDate.now(), "Universidad X", "Estadio Norte", "Jornada 1");
        rechazo("convocar solo 6 jugadores (futbol exige 7)", () -> club.getGestorCompetencias().convocar(e1, partido,
                jugadores(1, 6)));
        exito("convocar de jugador1 a jugador7", () -> club.getGestorCompetencias().convocar(e1, partido, jugadores(1, 7)));
        paso("jugador1 recibe una tarjeta roja", () -> club.getGestorCompetencias().registrarEstadistica(e1, partido,
                new EstadisticaFutbol(socio(club, "jugador1"), partido, 0, 0, 0, 1)));
        PartidoCompetencia siguiente = club.getGestorCompetencias().programarPartido(e1, "Leones",
                "Torneo de los Cerros", LocalDate.now().plusDays(1), "Universidad Y", "Estadio Sur", "Jornada 2");
        rechazo("convocar a jugador1, que esta suspendido", () -> club.getGestorCompetencias().convocar(e1, siguiente,
                jugadores(1, 7)));
        exito("convocar de jugador2 a jugador8", () -> club.getGestorCompetencias().convocar(e1, siguiente,
                jugadores(2, 8)));
    }

    private static void entrenadorRF4() throws Exception {
        Club club = preparar("Entrenador RF4 - Inscribir un equipo en una competencia");
        Entrenador e1 = (Entrenador) usuario(club, "entrenador1");
        rechazo("inscribir una nomina de 5 jugadores (minimo 7)", () -> club.getGestorCompetencias().inscribirEquipo(e1,
                "Leones", "Torneo de los Cerros", jugadores(1, 5)));
        rechazo("inscribir una nomina con cami, que no es del equipo", () -> {
            List<String> nomina = jugadores(1, 7);
            nomina.add("cami");
            club.getGestorCompetencias().inscribirEquipo(e1, "Leones", "Torneo de los Cerros", nomina);
        });
        rechazo("entrenador2 intenta inscribir a Leones", () -> club.getGestorCompetencias().inscribirEquipo(
                usuario(club, "entrenador2"), "Leones", "Torneo de los Cerros", jugadores(1, 8)));
        exito("entrenador1 inscribe a Leones con 8 jugadores", () -> club.getGestorCompetencias().inscribirEquipo(e1,
                "Leones", "Torneo de los Cerros", jugadores(1, 8)));
        rechazo("inscribir dos veces el mismo equipo", () -> club.getGestorCompetencias().inscribirEquipo(e1, "Leones",
                "Torneo de los Cerros", jugadores(1, 8)));
    }

    private static void entrenadorRF5() throws Exception {
        Club club = preparar("Entrenador RF5 - Consultar las estadisticas del equipo");
        Entrenador e1 = (Entrenador) usuario(club, "entrenador1");
        PartidoCompetencia partido = partidoJugadoHoy(club, e1);
        paso("se registran estadisticas de jugador1 y jugador2", () -> {
            club.getGestorCompetencias().registrarEstadistica(e1, partido, new EstadisticaFutbol(socio(club, "jugador1"),
                    partido, 2, 0, 0, 0));
            club.getGestorCompetencias().registrarEstadistica(e1, partido, new EstadisticaFutbol(socio(club, "jugador2"),
                    partido, 0, 1, 1, 0));
        });
        exito("entrenador1 consulta las estadisticas de Leones", () -> {
            for (Estadistica e : club.getGestorCompetencias().estadisticasDeEquipo(e1, "Leones")) {
                EstadisticaFutbol ef = (EstadisticaFutbol) e;
                estado(e.getSocio().getLogin() + ": " + ef.getGoles() + " goles, " + ef.getAsistencias() + " asistencias, "
                        + ef.getTarjetasAmarillas() + " amarillas");
            }
        });
        rechazo("entrenador2 consulta las estadisticas de Leones", () -> club.getGestorCompetencias()
                .estadisticasDeEquipo(usuario(club, "entrenador2"), "Leones"));
    }

    // ------------------------------------------------------------------ apoyo

    // Restaura los datos de ejemplo y abre el club desde la carpeta
    private static Club preparar(String titulo) throws Exception {
        System.out.println();
        System.out.println("=== " + titulo);
        restaurarDatos(carpeta);
        return new Club(new RepositorioJSON(carpeta));
    }

    // Inscribe a Leones, programa un partido para hoy y convoca a jugador1..jugador7
    private static PartidoCompetencia partidoJugadoHoy(Club club, Entrenador e1) throws Exception {
        club.getGestorCompetencias().inscribirEquipo(e1, "Leones", "Torneo de los Cerros", jugadores(1, 8));
        PartidoCompetencia partido = club.getGestorCompetencias().programarPartido(e1, "Leones", "Torneo de los Cerros",
                LocalDate.now(), "Universidad X", "Estadio Norte", "Jornada 1");
        club.getGestorCompetencias().convocar(e1, partido, jugadores(1, 7));
        System.out.println("  (preparacion) Leones inscrito en el Torneo de los Cerros, partido hoy, jugador1-7 convocados");
        return partido;
    }

    private static void exito(String descripcion, Paso paso) {
        try {
            paso.ejecutar();
            System.out.println("  EXITO       " + descripcion);
        } catch (Exception e) {
            inesperados++;
            System.out.println("  INESPERADO  " + descripcion + " -> rechazado: " + e.getMessage());
        }
    }

    private static void rechazo(String descripcion, Paso paso) {
        try {
            paso.ejecutar();
            inesperados++;
            System.out.println("  INESPERADO  " + descripcion + " -> se acepto y debia rechazarse");
        } catch (Exception e) {
            System.out.println("  RECHAZADO   " + descripcion + ": " + e.getMessage());
        }
    }

    private static void paso(String descripcion, Paso paso) {
        try {
            paso.ejecutar();
            System.out.println("  (preparacion) " + descripcion);
        } catch (Exception e) {
            inesperados++;
            System.out.println("  INESPERADO  preparacion '" + descripcion + "' fallo: " + e.getMessage());
        }
    }

    private static void comprobar(String descripcion, boolean condicion) {
        if (condicion) {
            System.out.println("  EXITO       " + descripcion);
        } else {
            inesperados++;
            System.out.println("  INESPERADO  " + descripcion);
        }
    }

    private static void estado(String texto) {
        System.out.println("      estado: " + texto);
    }

    private static int leerOpcion() {
        System.out.print("> ");
        if (!entrada.hasNextLine()) {
            return 0;
        }
        try {
            return Integer.parseInt(entrada.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static Usuario usuario(Club club, String login) {
        return club.getGestorUsuarios().getUsuarios().get(login);
    }

    private static Socio socio(Club club, String login) {
        return (Socio) usuario(club, login);
    }

    private static int stock(Club club, int idProducto) {
        return ((Articulo) club.getGestorVentas().getProductos().get(idProducto)).getCantidadDisponible();
    }

    private static String ubicacion(Club club, int idProducto) {
        return ((Articulo) club.getGestorVentas().getProductos().get(idProducto)).getUbicacion();
    }

    private static int plantilla(Club club, String equipo) throws Exception {
        return club.getGestorEquipos().buscarEquipo(equipo).getPlantilla().size();
    }

    private static int reservasDe(Club club, int idInstalacion) throws Exception {
        return club.getGestorReservas().getInstalacion(idInstalacion).getReservas().size();
    }

    private static List<String> jugadores(int desde, int hasta) {
        List<String> logins = new ArrayList<String>();
        for (int i = desde; i <= hasta; i++) {
            logins.add("jugador" + i);
        }
        return logins;
    }

    private static List<String> lista(String login) {
        List<String> logins = new ArrayList<String>();
        logins.add(login);
        return logins;
    }

    private static Map<Integer, Integer> pedido(int idProducto, int cantidad) {
        Map<Integer, Integer> pedido = new LinkedHashMap<Integer, Integer>();
        pedido.put(idProducto, cantidad);
        return pedido;
    }
}
