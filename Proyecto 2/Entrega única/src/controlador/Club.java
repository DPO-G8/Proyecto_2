package controlador;

import persistencia.RepositorioDatos;
import persistencia.RepositorioJSON;

public class Club {
    private final GestorUsuarios gestorUsuarios;
    private final GestorReservas gestorReservas;
    private final GestorVentas gestorVentas;
    private final GestorInventario gestorInventario;
    private final GestorEquipos gestorEquipos;
    private final GestorCompetencias gestorCompetencias;
    private final GestorEntrenamientos gestorEntrenamientos;
    private final GestorTurnos gestorTurnos;

    // Usa la carpeta datos
    public Club() throws Exception {
        this(new RepositorioJSON("datos"));
    }

    public Club(RepositorioDatos repositorio) throws Exception {
        this.gestorUsuarios = new GestorUsuarios(repositorio);
        this.gestorReservas = new GestorReservas(repositorio, gestorUsuarios.getUsuarios());
        this.gestorVentas = new GestorVentas(repositorio, gestorUsuarios.getUsuarios(), gestorReservas);
        this.gestorInventario = new GestorInventario(repositorio, gestorUsuarios.getUsuarios(), gestorVentas);
        this.gestorEquipos = new GestorEquipos(repositorio, gestorUsuarios.getUsuarios());
        this.gestorCompetencias = new GestorCompetencias(repositorio, gestorUsuarios.getUsuarios(), gestorEquipos,
                gestorReservas);
        this.gestorEntrenamientos = new GestorEntrenamientos(repositorio, gestorUsuarios.getUsuarios(),
                gestorEquipos, gestorReservas);
        this.gestorTurnos = new GestorTurnos(repositorio, gestorUsuarios.getUsuarios());
    }

    public GestorUsuarios getGestorUsuarios() {
        return gestorUsuarios;
    }

    public GestorReservas getGestorReservas() {
        return gestorReservas;
    }

    public GestorVentas getGestorVentas() {
        return gestorVentas;
    }

    public GestorInventario getGestorInventario() {
        return gestorInventario;
    }

    public GestorEquipos getGestorEquipos() {
        return gestorEquipos;
    }

    public GestorCompetencias getGestorCompetencias() {
        return gestorCompetencias;
    }

    public GestorEntrenamientos getGestorEntrenamientos() {
        return gestorEntrenamientos;
    }

    public GestorTurnos getGestorTurnos() {
        return gestorTurnos;
    }
}
