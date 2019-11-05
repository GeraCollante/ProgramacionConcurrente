package concurrente;

import java.util.Random;

public class PoliticaB implements Politica {
	int [] prioridad; 
	public PoliticaB () {
		int aux [] = {3,7,15,13,16,10,11,8,9,4,5,6,0,1,2,12,14};
		prioridad = aux;
	}
	@Override
	public int cual(int[] vector_m) {
		int i;
		Random rnd = new Random();
		int a = rnd.nextInt(2);
		if (a == 0) {
			int aux=prioridad[9];
			prioridad[9]=prioridad[10];
			prioridad[10]=aux;
		}
		for (i = 0; i<vector_m.length; i++){
			if (vector_m[ prioridad[i] ] != 0) {
				if(Main.debug) {
					System.out.println("Devuelvo transicion: "+prioridad[i]);
				}
				return prioridad[i];
			}
		}		
		return -1;
	}

}
