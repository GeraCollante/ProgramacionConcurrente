package concurrente;

import java.util.Random;

public class Entrada implements Runnable {
	
	private int ta,tb;
	public static int nAutos;
	GestorMonitor monitor;
	Random rnd;
	public Entrada(int ta,int tb,GestorMonitor monitor,int nAutos) {
		this.ta = ta;
		this.tb = tb;
		this.monitor = monitor;
		this.nAutos = nAutos;
		rnd = new Random();
	}

	@Override
	public void run() {
		try {
			
			while(nAutos>0) {
				synchronized (this) {
					nAutos--;				
				}
				Thread.sleep(rnd.nextInt(1000*Main.timeScale));
				monitor.disparar_transicion(ta);
				Thread.sleep(50*Main.timeScale);
				monitor.disparar_transicion(tb);				
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	

}
