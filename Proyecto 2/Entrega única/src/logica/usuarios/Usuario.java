package logica.usuarios;

public abstract class Usuario {
    private String login;
    private String password;
    private int puntosFidelidad;

    public Usuario(String login, String password) {
        this.login = login;
        this.password = password;
        this.puntosFidelidad = 0;
    }

    public String getLogin() {
        return this.login;
    }

    public String getPassword() {
        return this.password;
    }

    public int getPuntosFidelidad() {
        return this.puntosFidelidad;
    }

    public boolean puedeReservar() {
        return false;
    }

    public boolean puedeRegistrarEquipo() {
        return false;
    }

    public boolean puedeAprobarTurno() {
        return false;
    }

    public void acreditaPuntos(int puntos) {
        this.puntosFidelidad += puntos;
    }

    public void redimirPuntos(int puntos) throws Exception {
        if (puntos <= 0) {
            throw new Exception("La cantidad de puntos a redimir debe ser positiva, se recibio " + puntos);
        }
        if (puntos > this.puntosFidelidad) {
            throw new Exception("Puntos insuficientes: " + this.login + " tiene " + this.puntosFidelidad
                    + " y quiere redimir " + puntos);
        }
        this.puntosFidelidad -= puntos;
    }

    public double getPorcentajeDescuento() {
        return 0;
    }
}
