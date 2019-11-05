package concurrente;
import java.util.concurrent.Semaphore;

public class Mutex {
	private Semaphore semaforo;

	public Mutex(){
		semaforo = new Semaphore(1,true);
	}
	
	public boolean acquire(){
		try{
			semaforo.acquire();
			if(Main.alerts) {
				if(semaforo.availablePermits() > 1) System.out.println("MUTEX MALO: "+semaforo.availablePermits());
			}
		} catch(InterruptedException e){
			e.printStackTrace();
		}
		return true;
	}

	public boolean release(){ 
		semaforo.release();
		return true;
	}
}
