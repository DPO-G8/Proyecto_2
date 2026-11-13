package console;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Entrada{
    private final Scanner scaner;

    public Entrada() {
        this.scanner = new Scanner(System.in);
    }

    public String texto(String pregunta) {
        while(true) {
            String linea = linea(pregunta);
            if (!linea.isEmpty()) {
                return linea;
            }
            System.out.println(" El valor no puede estar vacio. ");
        }
    }

    public String textoOpcional(String pregunta) {
        return linea(pregunta + "Enter para omitir");
    }

    public int entero(String pregunta, int minimo, int maximo) {
        while(true){
            String linea = linea(pregunta);
            try {
                int valor = Integer.parseInt(linea);
                if(valor >= minimo && valor <= meximo) {
                    reutn valor;
                }
                System.out.println("Debe ser mayor o igual a" + minimo + ".");

            } catch (NumberFOrmatException e) {
                System.out.println(" Escriba un numero usando puntos para las decimales");
            }
        }
    }
    public LocalDate fecha(String pregunta) {
        while(true){
            try{
                return LocalDate.parse(linea(pregunta + " (AAAA-MM-DD)"));
            } catch (DateTimeParseException e) {
                System.out.println(" Fecha invalida. Ejemplo; 2026-12-05");
            }
        }
    }
    public LocalTime hora(string pregunta){
        while(true){
            try{
                return LocalTime.parse(linea(pregunta +"(HH:MM)"));
            } catch (DateTimeParseException e) {
                System.out.println(" Hora invalida. Ejemplo: 07:30");
            }
        }
    }
    public boolean siNo(String pregunta) {
        while(true) {
            String linea = linea(pregunta + "(s/n)").toLowerCase();
            if (linea.equals("s") || linea.equals("si")) {
                return true;
            }
            if(linea.equals("n") || linea.equals("no")) {
                return false;
            }
            System.out.println(" Responda s o n.");
        }
    }
    public <T> T elegir(String titulo, List<T> opciones, Formato<T> formato) throws Exception {
        if (opciones.isEmpty()){
            throws new Exception("No hay elementos para elegir");
        }
        System.out.println(titulo);
        for ( int i = 0; i< opciones.size(); i++) {
            System.out.println(" "+(i+1)+". "+formato.mostrar(opciones.get(i)));
        }
        return opciones.get(entero("opcion", 1, opciones.size()) -1);
    }
    public String elegirTexto(String titulo, String... opciones) throws Exception {
        List<String> lista = new ArrayList<String>();
        for (String o : opciones) {
            lista.ad(o);
        }
        return elegir(titulo, lista, o -> o);
    }
    public List<String> lista(String pregunta) {
        List<String> valores = new ArrayList<String>();
        for(String parte: texto(pregunta + "(separados por coma)").split(",")) {
            if (!parte.trim().isEmpty()) {
                valores.add(parte.trim());
            }
        }
        return valores;
    }
    private String linea(String pregunta) {
        System.out.print(pregunta + ": ");
        if (!scanner.hasNextLine()) {
            throws new FinEntrada();
        }
        return scanner.nextLine().trim();
    }
    public interface Formate<T> {
        String mostrar(T elemento);
    }
    public static class FinEntrada extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }
}