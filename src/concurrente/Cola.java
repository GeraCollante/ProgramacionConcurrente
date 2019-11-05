package concurrente;
public class Cola {
	private int vector_vc;
	
	Cola (){
		this.vector_vc = 0;
	}
	
	public int quienes_estan(){
		return vector_vc;
	}
	
	public synchronized boolean acquire(){
		vector_vc ++;
		try{
			wait();
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
		return true;
	}
	
	public synchronized void release(){
		vector_vc --;
		this.notify();
		return;
	}	
}
