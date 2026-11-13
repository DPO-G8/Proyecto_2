package consola;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import controlador.Club;
import logica.deportes.Competencia;
import logica.deportes.DisciplinaIndividual;
import logica.deportes.Equipo;
import logica.deportes.Estadistica;
import logica.deportes.Partido;
import logica.deportes.PartidoCompetencia;
import logica.deportes.SesionEntrenamiento;
import logica.torneos.Encuentro;
import logica.torneos.Torneo;
import logica.usuarios.Entrenador;

public class MenuEntrenador {
    private final Club club;
    private final Entrenador entrenador;
    private final Entrada entrada;

    public MenuEntrenador(Club club, Entrenador entrenador, Entrada entrada) {
        this.club = club;
        this.entrenador = entrenador;
        this.entrada = entrada;
    }

    public void mostrar() {
        new Menu("Entrenador " + entrenador.getLogin(), entrada)
                .opcion("Mis equipos", () -> {
                    for (Equipo e : entrenador.getEquipos()) {
                        System.out.println("  " + e.getNombre() + " (" + e.getDeporte().getNombre() + " "
                                + e.getCategoria() + ", " + e.getPlantilla().size() + " jugadores)");
                    }
                })
                .opcion("Sesiones de entrenamiento", this::menuSesiones)
                .opcion("Competencias y partidos", this::menuPartidos)
                .opcion("Torneos internos de conjunto", this::menuTorneos)
                .mostrar();
    }

    private Equipo elegirEquipo() throws Exception {
        return entrada.elegir("Equipo:", entrenador.getEquipos(), e -> e.getNombre());
    }

    private void menuSesiones() {
        new Menu("Sesiones", entrada)
                .opcion("Crear sesion de equipo", () -> {
                    Equipo e = elegirEquipo();
                    club.getGestorEntrenamientos().crearSesion(entrenador, entrada.fecha("Fecha"),
                            entrada.hora("Hora"), entrada.entero("Duracion en minutos", 1, 600),
                            entrada.texto("Tipo (tecnica, fisica...)"), entrada.entero("Id de la instalacion", 1, 9999),
                            e.getNombre());
                    System.out.println("  Sesion creada.");
                })
                .opcion("Crear sesion de academia (individual o grupo pequeno)", () -> {
                    DisciplinaIndividual d = entrada.elegir("Disciplina:", club.getGestorReservas().getDisciplinas(),
                            x -> x.getNombre());
                    club.getGestorEntrenamientos().crearSesionAcademia(entrenador, entrada.fecha("Fecha"),
                            entrada.hora("Hora"), entrada.entero("Duracion en minutos", 1, 600),
                            entrada.texto("Tipo"), entrada.entero("Id de la instalacion", 1, 9999), d.getNombre(),
                            entrada.lista("Logins de los convocados"));
                    System.out.println("  Sesion creada.");
                })
                .opcion("Registrar asistencia y nota", () -> {
                    List<SesionEntrenamiento> mias = new ArrayList<SesionEntrenamiento>();
                    for (SesionEntrenamiento s : club.getGestorEntrenamientos().getSesiones()) {
                        if (s.getEntrenador().equals(entrenador)) {
                            mias.add(s);
                        }
                    }
                    SesionEntrenamiento s = entrada.elegir("Sesiones:", mias, MainEmpleado::sesion);
                    String estado = entrada.elegirTexto("Asistencia:", "ASISTIO", "FALTO", "LLEGO_TARDE");
                    club.getGestorEntrenamientos().registrarAsistencia(entrenador, s, entrada.texto("Login del socio"),
                            estado, entrada.entero("Nota de 1 a 5 (0 = sin nota)", 0, 5),
                            entrada.textoOpcional("Comentario"));
                    System.out.println("  Asistencia registrada.");
                })
                .mostrar();
    }

    private void menuPartidos() {
        new Menu("Competencias y partidos", entrada)
                .opcion("Inscribir equipo en una competencia", () -> {
                    Equipo e = elegirEquipo();
                    Competencia c = entrada.elegir("Competencia:", club.getGestorCompetencias().getCompetencias(),
                            x -> x.getNombre() + " (" + x.getDeporte().getNombre() + " " + x.getCategoriaHabilitada() + ")");
                    club.getGestorCompetencias().inscribirEquipo(entrenador, e.getNombre(), c.getNombre(),
                            entrada.lista("Logins de la nomina"));
                    System.out.println("  Equipo inscrito.");
                })
                .opcion("Programar partido de competencia", () -> {
                    Equipo e = elegirEquipo();
                    club.getGestorCompetencias().programarPartido(entrenador, e.getNombre(),
                            entrada.texto("Competencia"), entrada.fecha("Fecha"), entrada.texto("Institucion rival"),
                            entrada.texto("Escenario"), entrada.texto("Fase"));
                    System.out.println("  Partido programado.");
                })
                .opcion("Consultar jugadores disponibles para una fecha", () -> {
                    Equipo e = elegirEquipo();
                    LocalDate fecha = entrada.fecha("Fecha del partido");
                    Map<String, String> no = club.getGestorCompetencias().noDisponibles(entrenador, e.getNombre(), fecha);
                    for (Map.Entry<String, String> x : no.entrySet()) {
                        System.out.println("  No disponible: " + x.getKey() + " (" + x.getValue() + ")");
                    }
                    boolean puede = club.getGestorCompetencias().puedeJugarse(entrenador, e.getNombre(), fecha);
                    System.out.println("  " + (e.getPlantilla().size() - no.size()) + " habilitados; "
                            + e.getDeporte().getNombre() + " exige " + e.getDeporte().getMinimoJugadores() + ": "
                            + (puede ? "el partido puede jugarse" : "el partido NO puede jugarse"));
                })
                .opcion("Convocar alineacion para un partido de competencia", () -> {
                    PartidoCompetencia p = elegirPartidoCompetencia(true);
                    club.getGestorCompetencias().convocar(entrenador, p, entrada.lista("Logins de los jugadores"));
                    System.out.println("  Alineacion registrada.");
                })
                .opcion("Registrar resultado de un partido de competencia", () -> {
                    PartidoCompetencia p = elegirPartidoCompetencia(true);
                    club.getGestorCompetencias().registrarResultado(entrenador, p, entrada.entero("Marcador a favor", 0, 999),
                            entrada.entero("Marcador en contra", 0, 999));
                    System.out.println("  Resultado registrado.");
                })
                .opcion("Registrar estadisticas de un partido", () -> {
                    PartidoCompetencia p = elegirPartidoCompetencia(false);
                    for (Estadistica e : FormularioEstadisticas.pedir(entrada, club.getGestorUsuarios().getUsuarios(),
                            p, p.getEquipo().getDeporte().getNombre())) {
                        club.getGestorCompetencias().registrarEstadistica(entrenador, p, e);
                    }
                    System.out.println("  Estadisticas registradas.");
                })
                .opcion("Estadisticas de mi equipo", () -> {
                    Equipo e = elegirEquipo();
                    for (Estadistica x : club.getGestorCompetencias().estadisticasDeEquipo(entrenador, e.getNombre())) {
                        System.out.println("  " + x.getSocio().getLogin() + ": "
                                + x.getClass().getSimpleName().replace("Estadistica", "") + " en "
                                + MainEmpleado.partido(x.getPartido()));
                    }
                })
                .opcion("Programar partido interno", () -> {
                    Equipo e = elegirEquipo();
                    club.getGestorCompetencias().crearPartidoInterno(entrenador, e.getNombre(),
                            entrada.texto("Equipo rival del club"), entrada.fecha("Fecha"));
                    System.out.println("  Partido interno programado.");
                })
                .mostrar();
    }

    private PartidoCompetencia elegirPartidoCompetencia(boolean soloPendientes) throws Exception {
        List<PartidoCompetencia> lista = new ArrayList<PartidoCompetencia>();
        for (Partido p : club.getGestorCompetencias().getPartidos()) {
            if (p instanceof PartidoCompetencia && entrenador.dirige(((PartidoCompetencia) p).getEquipo())
                    && (!soloPendientes || p.getMarcador().isEmpty())) {
                lista.add((PartidoCompetencia) p);
            }
        }
        return entrada.elegir("Partidos:", lista, MainEmpleado::partido);
    }

    private void menuTorneos() {
        new Menu("Torneos de conjunto", entrada)
                .opcion("Listar torneos", () -> VistaTorneos.listar(club, entrenador))
                .opcion("Ver detalle e historial", () -> VistaTorneos.detalle(club, entrenador, entrada))
                .opcion("Inscribir mi equipo", () -> {
                    Equipo e = elegirEquipo();
                    Torneo t = VistaTorneos.elegir(club, entrenador, entrada);
                    boolean pref = club.getGestorTorneos().inscribirEquipo(entrenador, t, e.getNombre())
                            .isPreferencial();
                    System.out.println("  Equipo inscrito con cupo " + (pref ? "preferencial" : "regular"));
                })
                .opcion("Retirar mi equipo", () -> {
                    Equipo e = elegirEquipo();
                    Torneo t = VistaTorneos.elegir(club, entrenador, entrada);
                    club.getGestorTorneos().retirarEquipo(entrenador, t, e.getNombre());
                    System.out.println("  Equipo retirado; el cupo queda libre.");
                })
                .opcion("Presentar alineacion para un encuentro", () -> {
                    Equipo e = elegirEquipo();
                    Torneo t = VistaTorneos.elegir(club, entrenador, entrada);
                    List<Encuentro> suyos = new ArrayList<Encuentro>();
                    for (Encuentro p : t.getEncuentrosPendientes()) {
                        if ((p.getParticipanteA().incluye(e.getNombre()) || p.getParticipanteB().incluye(e.getNombre()))) {
                            suyos.add(p);
                        }
                    }
                    Encuentro p = entrada.elegir("Encuentros pendientes:", suyos, VistaTorneos::encuentro);
                    club.getGestorTorneos().alinear(entrenador, t, p, e.getNombre(),
                            entrada.lista("Logins de los jugadores"));
                    System.out.println("  Alineacion registrada.");
                })
                .mostrar();
    }
}
