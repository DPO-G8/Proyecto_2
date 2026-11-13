package consola;

import java.util.ArrayList;
import java.util.List;

public class Menu {
    private final String titulo;
    private final Entrada entrada;
    private final List<Opcion> opciones;

    public Menu(String titulo, Entrada entrada) {
        this.titulo = titulo;
        this.entrada = entrada;
        this.opciones = new ArrayList<Opcion>();
    }

    public Menu opcion(String nombre, Opcion.Accion accion) {
        this.opciones.add(new Opcion(nombre, accion));
        return this;
    }

    public void mostrar() {
        while (true) {
            System.out.println();
            System.out.println("=== " + titulo + " ===");
            for (int i = 0; i < opciones.size(); i++) {
                System.out.println("  " + (i + 1) + ". " + opciones.get(i).getNombre());
            }
            System.out.println("  0. Volver");
            int opcion = entrada.entero("Opcion", 0, opciones.size());
            if (opcion == 0) {
                return;
            }
            try {
                opciones.get(opcion - 1).ejecutar();
            } catch (Entrada.FinEntrada e) {
                throw e;
            } catch (Exception e) {
                System.out.println("  No se pudo: " + e.getMessage());
            }
        }
    }
}
