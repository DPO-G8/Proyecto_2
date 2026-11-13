package consola;

public class Opcion{
    private final String nombre;
    private final Accion accion;

    public Opcion(String nombre, Accion accion){
        this.nombre = nombre;
        this.accion = accion;
    }

    public String getNombre(){
        return this.nombre;
    }
    public void ejecutar() throws Exception{
        this.accion.ejecutar();
    }

    public interface Accion{
        void ejecutar() throws Exception;
    }
}