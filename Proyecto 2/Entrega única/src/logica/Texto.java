package logica;
import java.text.Normalizer;

public class Texto {

    public static boolean iguales(String a, String b) {
        return normalizar(a).equals(normalizar(b));
    }

    private static String normalizar(String s) {
        return Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", "").toLowerCase();
    }
}