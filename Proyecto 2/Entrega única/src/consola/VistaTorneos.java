package consola;

import java.util.List;
import java.util.Map;

import controlador.Club;
import controlador.GestorTorneos;
import logica.torneos.Encuentro;
import logica.torneos.InscripcionTorneo;
import logica.torneos.Torneo;
import logica.usuarios.Socio;
import logica.usuarios.Usuario;

public class VistaTorneos {

    public static String resumen(Torneo t) {
        return "[" + t.getId() + "] " + t.getNombre() + ", " + t.getTipo().toLowerCase() + ", " + t.getFecha()
                + " - " + t.getEstadoTexto();
    }

    public static void listar(Club club, Usuario usuario) throws Exception {
        List<Torneo> torneos = club.getGestorTorneos().listar();
        if (torneos.isEmpty()) {
            System.out.println("  No hay torneos.");
        }
        for (Torneo t : torneos) {
            System.out.println("  " + resumen(t));
        }
    }

    public static Torneo elegir(Club club, Usuario usuario, Entrada entrada) throws Exception {
        return entrada.elegir("Torneos:", club.getGestorTorneos().listar(), t -> resumen(t));
    }

    public static void detalle(Club club, Usuario usuario, Entrada entrada) throws Exception {
        listar(club, usuario);
        int id = entrada.entero("Id del torneo", 1, Integer.MAX_VALUE);
        Torneo t = club.getGestorTorneos().buscarTorneo(id);
        System.out.println();
        System.out.println(resumen(t));
        if (t.esCompetitivo()) {
            System.out.println("  Tarifa: " + dinero(t.getTarifa()) + " | Recaudado: " + dinero(t.totalRecaudado()));
        }
        System.out.println("  Participantes (" + t.getParticipantes().size() + "/" + t.getCuposTotales() + "):");
        for (InscripcionTorneo i : t.getParticipantes()) {
            System.out.println("    - " + i.getNombre() + (i.isPreferencial() ? " [cupo preferencial]" : "")
                    + (i.esEmpleado() ? " [empleado]" : ""));
        }
        if (Torneo.ABIERTO.equals(t.getEstado())) {
            System.out.println("  Cupos libres: " + t.cuposLibres() + " (preferenciales libres: "
                    + Math.max(0, t.cuposPreferencialesLibres()) + ", regulares libres: " + Math.max(0, t.cuposRegularesLibres())
                    + ")");
            return;
        }
        System.out.println("  Encuentros jugados:");
        for (Encuentro p : t.getEncuentrosJugados()) {
            System.out.println("    " + encuentro(p));
        }
        if (!t.getEncuentrosPendientes().isEmpty()) {
            System.out.println("  Encuentros pendientes:");
            for (Encuentro p : t.getEncuentrosPendientes()) {
                System.out.println("    " + encuentro(p));
            }
        }
        if (!Torneo.FINALIZADO.equals(t.getEstado())) {
            tabla("Posiciones", t.getPosiciones());
        } else {
            tabla("Clasificacion final", t.getClasificacion());
            if (t.esCompetitivo()) {
                System.out.println("  Premios en efectivo:");
                for (InscripcionTorneo i : t.getClasificacion()) {
                    if (i.getPremio() > 0) {
                        System.out.println("    " + i.getNombre() + ": " + dinero(i.getPremio()));
                    }
                }
            } else {
                System.out.println("  Bonos de descuento entregados:");
                List<InscripcionTorneo> clasificacion = t.getClasificacion();
                for (int i = 0; i < clasificacion.size() && i < GestorTorneos.PREMIO_BONO.length; i++) {
                    String socios = "";
                    for (Socio s : clasificacion.get(i).getSocios()) {
                        socios += (socios.isEmpty() ? "" : ", ") + s.getLogin();
                    }
                    System.out.println("    " + (i + 1) + ". " + clasificacion.get(i).getNombre() + ": "
                            + (int) Math.round(GestorTorneos.PREMIO_BONO[i] * 100) + "% para "
                            + (socios.isEmpty() ? "nadie (sin socios)" : socios));
                }
            }
        }
        Map<String, String> lideres = club.getGestorTorneos().lideresEstadisticas(t);
        if (!lideres.isEmpty()) {
            System.out.println("  Lideres de estadisticas:");
            for (Map.Entry<String, String> e : lideres.entrySet()) {
                System.out.println("    " + e.getKey() + ": " + e.getValue());
            }
        }
    }

    public static String encuentro(Encuentro p) {
        String resultado = p.isJugado() ? p.getMarcador() + (p.getEscenario().isEmpty() ? "" : " (" + p.getEscenario() + ")")
                : "pendiente";
        return "#" + p.getId() + " " + p.getParticipanteA().getNombre() + " vs " + p.getParticipanteB().getNombre()
                + ": " + resultado;
    }

    private static void tabla(String titulo, List<InscripcionTorneo> filas) {
        System.out.println("  " + titulo + " (Pts, PJ, Dif):");
        int puesto = 1;
        for (InscripcionTorneo f : filas) {
            System.out.println("    " + puesto++ + ". " + f.getNombre() + "  " + f.getPuntos() + "  " + f.getJugados()
                    + "  " + f.getDiferencia());
        }
    }

    public static String dinero(double valor) {
        return "$" + String.format("%,.0f", valor);
    }
}
