package concurrente;

import java.util.Random;

public class DisparoUnico implements Runnable {

	int ta;
	int tiempo;
	GestorMonitor monitor;
	Random rnd;
	public DisparoUnico(int ta,int tiempo,GestorMonitor monitor) {
		this.ta = ta;
		this.monitor = monitor;	
		this.tiempo = tiempo;
		rnd = new Random();
	}

	@Override
	public void run() {
		try {
			while(true)  {
				monitor.disparar_transicion(ta);				
				Thread.sleep(rnd.nextInt(tiempo*Main.timeScale));
			}					
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

}
