package logica.tienda;

import java.util.ArrayList;
import java.util.List;

public class Snack extends Producto {
    private List<String> alergenos;

    public Snack(int idProducto, String nombre, double precio, List<String> alergenos) {
        super(idProducto, nombre, precio);
        this.alergenos = new ArrayList<String>(alergenos);
    }

    public List<String> getAlergenos() {
        return this.alergenos;
    }
}
