package consola;

import controlador.Club;
import logica.usuarios.Usuario;

public class Login {
    public static final int INTENTOS = 3;

    public interface Rol{
        boolean permite(Usuario usuario);
    }

    public static Usuario autenticar(Club club, Entrada entrada, String nombreRol, Rol rol) {
        System.out.println("=== Club Deportivo Seneca: consola de " + nombreRol + " ===");
        for (int intento = 1; intento <= INTENTOS; intento++) {
            String login= entrada.texto("Login");
            String password = entrada.texto("Contrasena");
            try{
                Usuario usuario = club.getGestorUsuarios().autenticar(login, password);
                if (rol.permite(usuario)){
                    return usuario;
                }
                System.out.println("  Este usuario no es " + nombreRol + ": use la consola que le corresponde.");
            } catch (Exception e) {
                System.out.println(" "+ e.getMessage());
            }
            System.out.println(" Intentos restantes: " +(INTENTOS-intento));
        }
        System.out.println("Demasiados intentos fallidos.");
        return null;
    }
}