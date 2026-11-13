package logica.torneos;

public class PersonaExterna {
	public static final String ESTUDIANTE = "ESTUDIANTE";
	public static final String EXTERNO = "EXTERNO";

	private String nombre;
	private String documento;
	private String tipo;
	private String codigo;

	public PersonaExterna(String nombre, String documento, String tipo, String codigo) {
		this.nombre = nombre;
		this.documento = documento;
		this.tipo = tipo;
		this.codigo = codigo;
	}

	public String getDocumento() {
		return documento;
	}
}
