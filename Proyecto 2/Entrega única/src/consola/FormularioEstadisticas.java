package consola;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import logica.Texto;
import logica.deportes.Estadistica;
import logica.deportes.EstadisticaBaloncesto;
import logica.deportes.EstadisticaFutbol;
import logica.deportes.EstadisticaTenisMesa;
import logica.deportes.EstadisticaTenisPadel;
import logica.deportes.EstadisticaVoleibol;
import logica.deportes.Partido;
import logica.usuarios.Socio;
import logica.usuarios.Usuario;

public class FormularioEstadisticas {

    public static List<Estadistica> pedir(Entrada entrada, Map<String, Usuario> usuarios, Partido partido, String deporte) {
        List<Estadistica> lista = new ArrayList<Estadistica>();
        while(entrada.siNo("Agregar estadistica de un jugador")) {
            Usuario u = usuarios.get(entrada.texto("Login del jugador"));
            if(!(u instanceof Socio)) {
                System.out.println(" No existe un socio con ese login");
                continue;
            }
            lista.add(una(entrada, (Socio) u, partido, deporte));
        }
        return lista;
    }

    public static Estadistica una(Entrada entrada, Socio socio, Partido partido, String deporte) {
        int max = 999;
        if(Texto.iguales(deporte, "Futbol")) {
            return new EstadisticaFutbol(socio, partido, entrada.entero("Goles", 0, max), entrada.entero("Asistencias", 0, max), entrada.entero("Tarjetas amarillas", 0, 2),
            entrada.entero("Tarjetas rojas", 0, 1));
        }
        if(Texto.iguales(deporte, "Baloncesto")) {
            return new EstadisticaBaloncesto(socio, partido, entrada.entero("Puntos", 0, max), entrada.entero("Rebotes", 0, max));
        }
        if(Texto.iguales(deporte, "Voleibol")) {
            return new EstadisticaVoleibol(socio, partido, entrada.entero("Aces", 0, max), entrada.entero("Bloqueos", 0, max));
        }
        if(Texto.iguales(deporte, "Tenis de mesa")) {
            return new EstadisticaTenisMesa(socio, partido, entrada.entero("Sets ganados", 0, 7), entrada.entero("Puntos totales", 0, max));
        }
        return new EstadisticaTenisPadel(socio, partido, entrada.entero("Sets ganados",0,5), entrada.entero("Games ganados",0,max),
        entrada.entero("Aces",0,max),entrada.entero("errores no forzados",0,max));
    }
}