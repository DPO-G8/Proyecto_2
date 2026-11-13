package consola;

import java.util.List;
import java.util.Map;

import controlador.Club;
import controlador.GestorTorneos;
import logica.torneos.FilaPosicion;
import logica.torneos.InscripcionTorneo;
import logica.torneos.PartidoTorneo;
import logica.torneos.Premio;
import logica.torneos.Torneo;
import logica.torneos.TorneoIndividual;
import logica.usuarios.Socio;
import logica.usuarios.Usuario;

public class VistaTorneos {

    public static String resumen(Torneo t) {
        String modalidad = t instanceof TorneoIndividual ? " " + ((TorneoIndividual) t).getModalidad() : "";
        return "[" + t.getId() + "] " + t.getNombre() + " - " + t.getNombreDeporte() + modalidad + " "
                + t.getCategoriaONivel() + ", " + t.getTipo().toLowerCase() + ", " + t.getFecha() + " - "
                + t.getEstadoTexto();
    }

    public static void listar(Club club, Usuario usuario) throws Exception {
        List<Torneo> torneos = club.getGestorTorneos().listarTorneos(usuario);
        if (torneos.isEmpty()) {
            System.out.println("  No hay torneos.");
        }
        for (Torneo t : torneos) {
            System.out.println("  " + resumen(t));
        }
    }

    public static Torneo elegir(Club club, Usuario usuario, Entrada entrada) throws Exception {
        return entrada.elegir("Torneos:", club.getGestorTorneos().listarTorneos(usuario), t -> resumen(t));
    }

    public static void detalle(Club club, Usuario usuario, Entrada entrada) throws Exception {
        listar(club, usuario);
        int id = entrada.entero("Id del torneo", 1, Integer.MAX_VALUE);
        Torneo t = club.getGestorTorneos().detalleTorneo(usuario, id);
        System.out.println();
        System.out.println(resumen(t));
        if (t.esCompetitivo()) {
            System.out.println("  Tarifa: " + dinero(t.getTarifa()) + " | Recaudado: " + dinero(t.getTotalRecaudado()));
        }
        System.out.println("  Participantes (" + t.getInscripciones().size() + "/" + t.getCupos() + "):");
        for (InscripcionTorneo i : t.getInscripciones()) {
            System.out.println("    - " + i.getNombre() + (i.isPreferencial() ? " [cupo preferencial]" : "")
                    + (i.esEmpleado() ? " [empleado]" : ""));
        }
        if (Torneo.ABIERTO.equals(t.getEstado())) {
            System.out.println("  Cupos libres: " + t.cuposLibres() + " (preferenciales libres: "
                    + Math.max(0, t.preferencialesLibres()) + ", regulares libres: " + Math.max(0, t.regularesLibres())
                    + ")");
            return;
        }
        System.out.println("  Encuentros:");
        for (PartidoTorneo p : t.getPartidos()) {
            System.out.println("    " + encuentro(p));
        }
        String titulo = Torneo.FINALIZADO.equals(t.getEstado()) ? "Clasificacion final" : "Posiciones";
        tabla(titulo, club.getGestorTorneos().tablaPosiciones(t));
        if (Torneo.FINALIZADO.equals(t.getEstado())) {
            if (t.esCompetitivo()) {
                System.out.println("  Premios en efectivo:");
                for (Premio p : t.getPremios()) {
                    System.out.println("    " + p.getPuesto() + ". " + p.getGanador().getNombre() + ": "
                            + dinero(p.getMonto()));
                }
            } else {
                System.out.println("  Bonos de descuento entregados:");
                List<FilaPosicion> clasificacion = club.getGestorTorneos().tablaPosiciones(t);
                for (int i = 0; i < clasificacion.size() && i < GestorTorneos.PREMIO_BONO.length; i++) {
                    String socios = "";
                    for (Socio s : clasificacion.get(i).getParticipante().getSocios()) {
                        socios += (socios.isEmpty() ? "" : ", ") + s.getLogin();
                    }
                    System.out.println("    " + (i + 1) + ". " + clasificacion.get(i).getParticipante().getNombre() + ": "
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

    public static String encuentro(PartidoTorneo p) {
        String resultado = p.isJugado() ? p.getMarcador() + " en " + p.getEscenario() : "pendiente";
        return "#" + p.getId() + " " + p.getLocal().getNombre() + " vs " + p.getVisitante().getNombre() + ": "
                + resultado;
    }

    private static void tabla(String titulo, List<FilaPosicion> filas) {
        System.out.println("  " + titulo + " (Pts, PJ, Dif):");
        int puesto = 1;
        for (FilaPosicion f : filas) {
            System.out.println("    " + puesto++ + ". " + f.getParticipante().getNombre() + "  " + f.getPuntos() + "  "
                    + f.getJugados() + "  " + f.getDiferencia());
        }
    }

    public static String dinero(double valor) {
        return "$" + String.format("%,.0f", valor);
    }
}
