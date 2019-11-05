package concurrente;

public class Salida implements Runnable {

	private int ta,tb;
	public static int nAutos;
	GestorMonitor monitor;
	
	public Salida(int ta,int tb,GestorMonitor monitor,int nAutos) {
		this.ta = ta;
		this.tb = tb;
		this.monitor = monitor;
		this.nAutos= nAutos;
	}

	@Override
	public void run() {
		try {
					
			while(nAutos>0)  {
				synchronized (this) {
					nAutos--;				
				}
				monitor.disparar_transicion(ta);
				Thread.sleep(50*Main.timeScale);
				monitor.disparar_transicion(tb);
				if(Main.debug) {
					System.out.println("Sale auto por " + Thread.currentThread().getName());
				}
			}			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
