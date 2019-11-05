package concurrente;

public class Patron {
	int indice;
	int tiempo;
	boolean bueno;
	
	public Patron() {
		
	}
	public Patron(int indice,int tiempo,boolean bueno) {
		this.indice=indice;
		this.tiempo=tiempo;
		this.bueno=bueno;
		
	}
	
	public int getIndice() {
		return indice;
	}

	public void setIndice(int indice) {
		this.indice = indice;
	}

	public int getTiempo() {
		return tiempo;
	}

	public void setTiempo(int tiempo) {
		this.tiempo = tiempo;
	}
	
	public boolean isBueno() {
		return bueno;
	}
	public void setBueno(boolean bueno) {
		this.bueno = bueno;
	}

	public String toString()
	{
		return indice+" "+tiempo+" "+bueno;
	}
	
	

}
