package logica.torneos;

public class Encuentro {
	private int numero;
	private InscripcionTorneo participanteA;
	private InscripcionTorneo participanteB;
	private int marcadorA;
	private int marcadorB;
	private boolean jugado;

	public Encuentro(int numero, InscripcionTorneo participanteA, InscripcionTorneo participanteB) {
		this.numero = numero;
		this.participanteA = participanteA;
		this.participanteB = participanteB;
		this.jugado = false;
	}

	public int getNumero() {
		return numero;
	}

	public InscripcionTorneo getParticipanteA() {
		return participanteA;
	}

	public InscripcionTorneo getParticipanteB() {
		return participanteB;
	}

	public int getMarcadorA() {
		return marcadorA;
	}

	public int getMarcadorB() {
		return marcadorB;
	}

	public boolean isJugado() {
		return jugado;
	}

	public void registrarResultado(int marcadorA, int marcadorB) {
		this.marcadorA = marcadorA;
		this.marcadorB = marcadorB;
		this.jugado = true;
	}
}
