package concurrente;

public class Cartel implements Runnable {

	int tPrende, tApaga;
	GestorMonitor monitor;

	public Cartel(int ta, int tb, GestorMonitor monitor) {
		this.tPrende = ta;
		this.tApaga = tb;
		this.monitor = monitor;
	}

	@Override
	public void run() {
		while (true) {
			monitor.disparar_transicion(tPrende);
			if (Main.alerts) {
				System.out.println("LUGARES LIBRES");
			}
			monitor.disparar_transicion(tApaga);
			if (Main.alerts) {
				System.out.println("PLAYA LLENA");
			}
		}
	}
}
