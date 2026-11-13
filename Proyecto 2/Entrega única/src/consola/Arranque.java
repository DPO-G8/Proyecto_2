package consola;

import controlador.Club;
import logica.usuarios.Usuario;
import persistencia.RepositorioJSON;

public class Arranque{
    public static final String CARPETA_DATOS = "datos";

   
    public interface Aplicacion{
        void correr(Sesion sesion);
    }
    public static void iniciar(String nombreRol, Login.Rol rol, Aplicacion aplicacion){
        Club club;
        try{
            club = new Club(new RepositorioJSON(CARPETA_DATOS));
        } catch (Exception e){
            System.out.println("No se pudieron cargar los datos de la carpeta ' " + CARPETA_DATOS + "': " + e.getMessage());
             return;
        }
        Entrada entrada = new Entrada();
        try{
            Usuario usuario = Login.autenticar(club, entrada, nombreRol, rol);
            if (usuario == null) {
                return;
            }
            System.out.println("Bienvenido, " + usuario.getLogin());
            aplicacion.correr(new Sesion(club, usuario, entrada));
        } catch (Entrada.FinEntrada e){
            System.out.println();
        }
        System.out.println("Cambios guardados. Hasta luego.");
    }

}