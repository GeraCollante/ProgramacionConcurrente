package concurrente;

public interface Politica {
	public int cual(int[] vector_m) ;
	public int[] getPrioridad();
	public void setPrioridad(int[] prioridad);
	
}
