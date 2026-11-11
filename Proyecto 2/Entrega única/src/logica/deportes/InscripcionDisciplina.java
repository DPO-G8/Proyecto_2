package logica.deportes;

import logica.Texto;
import logica.usuarios.Socio;

public class InscripcionDisciplina {
    private String nivel;
    private double puntajeRanking;
    private Socio socio;

    public InscripcionDisciplina(Socio socio, String nivel, double puntajeRanking) {
        this.socio = socio;
        this.nivel = nivel;
        this.puntajeRanking = puntajeRanking;
    }

    public String getNivel() {
        return this.nivel;
    }

    public double getPuntajeRanking() {
        return this.puntajeRanking;
    }

    public Socio getSocio() {
        return this.socio;
    }

    public boolean nivelCompatible(InscripcionDisciplina otra) {
        int diferencia = valorNivel(this.nivel) - valorNivel(otra.getNivel());
        return diferencia >= -1 && diferencia <= 1;
    }

    private int valorNivel(String nivel) {
        if (Texto.iguales(nivel, "Principiante")) {
            return 0;
        }
        if (Texto.iguales(nivel, "Intermedio")) {
            return 1;
        }
        return 2;
    }
}
