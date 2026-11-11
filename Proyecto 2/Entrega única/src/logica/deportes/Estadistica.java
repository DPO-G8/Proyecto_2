package logica.deportes;

import logica.usuarios.Socio;

public abstract class Estadistica {
    private Socio socio;
    private Partido partido;

    public Estadistica(Socio socio, Partido partido) {
        this.socio = socio;
        this.partido = partido;
    }

    public Socio getSocio() {
        return this.socio;
    }

    public Partido getPartido() {
        return this.partido;
    }
}
