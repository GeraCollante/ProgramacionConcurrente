package concurrente;

public class PoliticaA implements Politica {
	int [] prioridad; //Las transiciones en orden descendente de prioridad
	
	public PoliticaA() {
	//  int aux [] = {3,7,14,12,16,10,8,4,0,15,13,11,9,5,1,6,2};	//Calle A, PB, EntA
	//	int aux [] = {3,7,14,12,16,10,8,5,1,15,13,11,9,6,2,4,0};	//Calle A, PB, EntB
	//	int aux [] = {3,7,14,12,16,10,8,6,2,15,13,11,9,5,1,4,0};	//Calle A, PB, EntC	
		int aux [] = {3,7,14,12,16,11,9,6,2,15,13,10,8,5,1,4,0};	//Calle A, PA, EntC


		//		pol B{3,7,15,13,16,10,11,8,9,4,5,6,0,1,2,12,14};

		prioridad = aux;
	}
	
	public int cual(int[] vector_m) {
		int i;
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

