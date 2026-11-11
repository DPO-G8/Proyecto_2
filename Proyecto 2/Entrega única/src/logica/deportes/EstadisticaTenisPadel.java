package logica.deportes;

import logica.usuarios.Socio;

public class EstadisticaTenisPadel extends Estadistica {
    private int setsGanados;
    private int gamesGanados;
    private int aces;
    private int erroresNoForzados;

    public EstadisticaTenisPadel(Socio socio, Partido partido, int setsGanados, int gamesGanados, int aces, int erroresNoForzados) {
        super(socio, partido);
        this.setsGanados = setsGanados;
        this.gamesGanados = gamesGanados;
        this.aces = aces;
        this.erroresNoForzados = erroresNoForzados;
    }

    public int getSetsGanados() {
        return this.setsGanados;
    }

    public int getGamesGanados() {
        return this.gamesGanados;
    }

    public int getAces() {
        return this.aces;
    }

    public int getErroresNoForzados() {
        return this.erroresNoForzados;
    }
}
