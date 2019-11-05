package concurrente;

import java.util.Comparator;

public class SortbyTiempo implements Comparator<Patron>{
	
	public int compare (Patron a, Patron b)
	{
		return a.getTiempo()-b.getTiempo();
	}

}
