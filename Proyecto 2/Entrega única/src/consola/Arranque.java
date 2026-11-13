package consola;

import controlador.Club;
import logica.usuarios.Usuarios;

public class Arranque{
    public static final String CARPETA_DATOS = 'datos';

   
    public interface Aplicacion{
        void corer(Sesion sesion);
    }
    public static void iniciar(String nombreRol, Login.Rol rol, Aplicacion aplicacion){
        Club club:
        try{
            club = new Club();
        } catch (Exception e){
            System.out.println("No se pudieron cargar los datos de ' " + CARPETA_DATOS + "': " + e.getMessage());
             return;
        }
        Entrada entrada = new Entrada();
        try{
            Usuario usuario = login(club, entrada, nombreRol, rol);
            if (usuario == null) {
                return;
            }
            System.out.println("Bienvenido, " + usuario.getLogin());
            aplicacion.correr(club, usuario,entrada);
        } catch (Entrada.FinEntrada e){
            System.out.println();
        } finally {
            try {
                club.guardarTodo();
                System.out.println("Cambios guardados. Hasta luego.");
            } catch ( Exception e) {
                System.out.println("Error al guardar: " + e.getMessage());
            }
        }
    }

}