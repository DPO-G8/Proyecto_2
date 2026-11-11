package logica.deportes;

import logica.usuarios.Socio;

public class Asistencia {
    public static final String ASISTIO = "ASISTIO";
    public static final String FALTO = "FALTO";
    public static final String LLEGO_TARDE = "LLEGO_TARDE";

    private String estado;
    private int notaDesempeno;
    private String comentario;
    private Socio socio;

    public Asistencia(Socio socio, String estado) {
        this.socio = socio;
        this.estado = estado;
        this.notaDesempeno = 0;
        this.comentario = "";
    }

    public String getEstado() {
        return this.estado;
    }

    public int getNotaDesempeno() {
        return this.notaDesempeno;
    }

    public void setNotaDesempeno(int notaDesempeno) throws Exception {
        if (notaDesempeno < 1 || notaDesempeno > 5) {
            throw new Exception("La nota debe estar entre 1 y 5, se recibio " + notaDesempeno);
        }
        this.notaDesempeno = notaDesempeno;
    }

    public String getComentario() {
        return this.comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Socio getSocio() {
        return this.socio;
    }
}
