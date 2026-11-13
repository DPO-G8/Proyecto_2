package consola;

import controlador.Club;
import logica.usuarios.Usuario;

public class Sesion {
    private final Club club;
    private final Usuario usuario;
    private final Entrada entrada;

    public Sesion(Club club, Usuario usuario, Entrada entrada) {
        this.club = club;
        this.usuario = usuario;
        this.entrada = entrada;
    }

    public Club getClub() {
        return this.club;
    }

    public Usuario getUsuario() {
        return this.usuario;
    }

    public Entrada getEntrada() {
        return this.entrada;
    }
}