package concurrente;

import java.util.Arrays;

public class RedPetri {
	
	int [][] marcado; // marcado mx1
	int [][] incidencia; //matriz de incidencia nxm
	int [][] h; // matriz de inhibicion nxm
	int [][] r; // matriz de brazo lector nxm	
	int [][] e; // vector de transiciones sensibilizadas por marcado actual nx1
	int [][] z; //vector de transiciones desensibilizadas por tiempo nx1
	int [][] b; // vector de transiciones desensibilizadas por arco inhibidor nx1
	int [][] l; // vector de transiciones desensibilizdas por arco lector nx1
	int [][] ex; // vector de transiciones sensibilzadas extendidasz nx1
	long [][] timeStamp;
	long [][] alfa;
	long [][] beta;
	int pInvariantes[][];
	int pInvariantesCheck[][];
	GestorMonitor monitor;
	String str = "abcdefghijklmnopq";
	String myLog ="";
	/**
	 * Constructor. 
	 */
	public RedPetri() {
		alfa = new long [17][1];
		beta = new long [17][1];
		timeStamp = new long [17][1];
		for(int i =0;i<17 ;i++) {
			alfa[i][0]=0;
			beta[i][0]=Long.MAX_VALUE;
			timeStamp[i][0]=0;
		}
		alfa[0][0]=20;
		alfa[1][0]=20;
		alfa[2][0]=20;
		alfa[10][0]=150;//PA
		alfa[11][0]=150;//PB
		alfa[16][0]=25;//tiempo que da el TP para la caja
		setPInvariantes();	
	}
	
	private void setPInvariantes() {
		pInvariantes = new int [9][20];
		pInvariantesCheck = new int [9][1];
		for(int i=0;i<9;i++) {
			for(int j=0;j<20;j++) {
				pInvariantes[i][j]=0;
			}
		}
		pInvariantes[0][1]=1;
		pInvariantes[0][2]=1;
		pInvariantes[1][3]=1;
		pInvariantes[1][4]=1;
		pInvariantes[2][5]=1;
		pInvariantes[2][6]=1;
		pInvariantes[3][7]=1;
		pInvariantes[3][8]=1;
		pInvariantes[4][10]=1;
		pInvariantes[4][11]=1;
		pInvariantes[5][12]=1;
		pInvariantes[5][13]=1;
		pInvariantes[6][11]=1;
		pInvariantes[6][13]=1;
		pInvariantes[6][14]=1;
		pInvariantes[7][16]=1;
		pInvariantes[7][17]=1;
		pInvariantes[8][18]=1;
		pInvariantes[8][19]=1;
		pInvariantesCheck[0][0]=3;
		pInvariantesCheck[1][0]=3;
		pInvariantesCheck[2][0]=3;
		pInvariantesCheck[3][0]=1;
		pInvariantesCheck[4][0]=30;
		pInvariantesCheck[5][0]=30;
		pInvariantesCheck[6][0]=60;
		pInvariantesCheck[7][0]=1;
		pInvariantesCheck[8][0]=1;
		if(Main.debug) {
			for(int i=0;i<9;i++) {
				for(int j=0;j<20;j++) {
					System.out.print(pInvariantes[i][j]+" ");
				}
				System.out.println("");
			}
		}		
	}
	
	/**
	 * Funcion que determina si una transicion esta sensibilizada segun el criterio E (tokens).
	 * @param tr transicion
	 * @return true si se puede disparar; false si no
	 */
	public boolean esSensibilizada(int tr) {		
		int [][] auxp =  deepCopy(marcado);
		for(int i=0;i<getP();i++) {
			auxp[i][0]=auxp[i][0]+incidencia[i][tr];
			if(auxp[i][0]<0) {
				return false;
			}				
		}		
		return true;	
	}
	
	public void setMonitor(GestorMonitor m) {
		monitor = m;
	}
	
	/**
	 * Setea matriz de incidencia
	 * @param incidencia
	 */
	public void setIncidencia(int[][] incidencia) {
		this.incidencia = incidencia.clone();
		e = new int[getT()][1];
		z = new int[getT()][1];
		b = new int[getT()][1];
		l = new int[getT()][1];
		ex = new int[getT()][1];
	}

	/**
	 * Setea marcado
	 * @param marcado
	 */
	public void setMarcado(int[][] marcado) {
		this.marcado = marcado;
	}
	
	/**
	 * Devuelve marcado
	 * @return
	 */
	public int[][] getMarcado() {
		return marcado;
	}
	
	/**
	 * Si la funcion es disparable, se dispara la transicion pasada por parametro
	 * @param tr transicion a disparar
	 * @return true si se disparo, false si no
	 */
	public boolean disparar(int tr) {
		if(this.ex[tr][0] == 1){
			for(int i=0;i<marcado.length;i++) {
				marcado[i][0]=marcado[i][0]+incidencia[i][tr];				
			}
			//Actualiza vectores E, L, B, Ex y TimeStamp
			calculaE();
			calculaL();
			calculaB();
			calculaEx();
			
			calculaTimeStamp();
			myLog=myLog+(str.charAt(tr));	
			
			if(Main.log) {
				System.out.print(str.charAt(tr));			
			}
			if(Main.debug) {
				System.out.println("Hilo " + Thread.currentThread().getName() + " dispara la transicion: " +tr);
			}			
			this.chequearPInvariantes();
			return true;
		}else{
			this.chequearPInvariantes();
			return false;
		}
	}
	
	public String getLog() {
		return myLog;
	}

	/**
	 * Implementa el disparo con ventana de tiempo. Si la transicion no esta sensibilizada por tiempo 
	 * duerme al hilo hasta estar dentro de la ventana.
	 * @param tr la transicion a disparar
	 * @return true si pudo disparar, false en caso contrario.
	 */
	public boolean disparoTemporal(int tr) {
		boolean k=true;
		if( esSensibilizadaExtendida(tr)) {
			//esta sensibilizado
			if (testVentanaTiempo(tr)) {
				// ventana == true
				timeStamp[tr][0]=System.currentTimeMillis();
				
			}else {
				// ventana == false
				long ahora = System.currentTimeMillis();
				if(Main.debug) {
					System.out.println(Thread.currentThread().getName()+". ahora vale: "+ahora);
					System.out.println(Thread.currentThread().getName()+". timestamp vale: "+timeStamp[tr][0]);
				}
				if( ahora - timeStamp[tr][0] < alfa[tr][0] ) //Es antes??
				{										
					try {
						monitor.getMutex().release();
						if(Main.debug) {
							System.out.println(Thread.currentThread().getName()+" duerme "+ (alfa[tr][0] - ahora + timeStamp[tr][0]) );
						}
						Thread.sleep( alfa[tr][0] - ahora + timeStamp[tr][0] );
						monitor.getMutex().acquire();
					}catch (Exception e) {
						e.printStackTrace();
					}
				}else {						
					k = false;
				}				
			}
			if (k) disparar(tr);
			if(!this.chequearPInvariantes())
				if(Main.alerts) {
					System.out.println("Hilo " + Thread.currentThread().getName() + " NO CUMPLIO CON INVARIANTES "+tr);
				}
			return k;
		}else {
			if(Main.debug) {
				System.out.println("Hilo " + Thread.currentThread().getName() + " No dispara la transicion: "+tr);
			}
			//no esta sensibilizado
			return false;
		}		
	}
	
	/**
	 * Chequea que la transicion tr este sensibilizada considerando ademas de E, arcos lectores e inhibidores.
	 * @param tr la transicion a chequear
	 * @return true si esta sensibilizada, false en caso contrario
	 */
	public boolean esSensibilizadaExtendida(int tr) {

		int e = getE()[tr][0];
		int b = getB()[tr][0];
		int l = getL()[tr][0];
		return (e == 1 && b == l && l== 1);
	}

	/**
	 * Verifica si la transicion esta sensibilizada para su ventana de tiempo.
	 * @param tr la transicion a chequear
	 * @return true si esta dentro de la ventana, false en caso contrario
	 */
	private boolean testVentanaTiempo(int tr) {
		long ahora = System.currentTimeMillis();
		if ( (ahora - timeStamp[tr][0]) >= alfa[tr][0] && (ahora - timeStamp[tr][0]) < beta[tr][0] )
			return true;
		else {					
			return false;
		}		
	}
	/**
	 * Funcion para imprimir el marcado
	 * @return Una cadena que representa el marcado.
	 */
	public String getMarcadoString() {
		String valor;
		valor="El marcado es [";
		for(int i=0;i<marcado.length;i++) {
			valor=valor+marcado[i][0]+" ";
		}
		valor = valor + "] \n";
		return valor;
	}
	
	/**
	 * Funcion para convertir matriz de int a boolean inversa
	 * @param A: matriz a convertir
	 * @return matriz de boolean
	 */
	public static int[][] cero(int A[][]) {
		int[][] resultado = new int[A.length][A[0].length];
		for(int i=0; i < A.length; i++) {
			for(int j=0; j < A[0].length ; j++) {
				if(A[i][j]>0) resultado[i][j] = 0;
				else resultado[i][j] = 1;
			}
		}		
		return resultado;
	}
	
	/**
	 * Funcion para convertir matriz de int a boolean
	 * @param A: matriz a convertir
	 * @return: matriz de boolean
	 */
	public static int[][] uno(int A[][]) {
		int[][] resultado = new int[A.length][A[0].length];
		for(int i=0; i < A.length; i++) {
			for(int j=0; j < A[0].length ; j++) {
				if(A[i][j]>0) resultado[i][j] = 1;
				else resultado[i][j] = 0;
			}
		}		
		return resultado;
	}

	
	/**
	 * Producto de matrices
	 * @param A
	 * @param B
	 * @return producto punto de matrices
	 */
	private static int[][] producto(int A[][], int B[][]){
		int suma = 0;
		int result[][] = new int[A.length][B[0].length];
		for(int i = 0; i < A.length; i++){
			for(int j = 0; j < B[0].length; j++){
				suma = 0;
				for(int k = 0; k < B.length; k++){
					suma += A[i][k] * B[k][j];
				}
				result[i][j] = suma;
			}
		}
		return result;
	}
	
	/**
	 * Trasponer matriz
	 * @param A
	 * @return matriz traspeusta
	 */
	static int[][] transponer(int[][] A){
		int [][] resultado = new int[A[0].length][A.length];

		for (int x=0; x < A.length; x++) {
			for (int y=0; y < A[x].length; y++) {
				resultado[y][x] = A[x][y];
			}
		}
		return resultado;
	}	
	
	@Override
	/**
	 * Para imprimir resultados
	 */
	public String toString(){
		try {
		String valor= this.getMarcadoString();
		valor=valor+ "Incidencia: \n";
		for(int i=0;i<getP();i++) {
			for(int j=0;j<getT();j++) {
				valor= valor + incidencia[i][j]+" ";
			}
			valor= valor +"\n";
		}
		valor= valor +"\n";
		return valor;
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}		
	}

	/**
	 * @return cantidad de transiciones
 	 */
	public int getT() {		
		return incidencia[0].length;
	}
	
	public int getP() {		
		return incidencia.length;
	}
	
	public void printAll() {
		System.out.println("Marcado: " + Arrays.deepToString(marcado));
		System.out.println("Incidencia: " + Arrays.deepToString(incidencia));
		System.out.println("H: " +Arrays.deepToString(h));
		System.out.println("R: " +Arrays.deepToString(r));
		System.out.println("Vector sensibilizado E: " +Arrays.deepToString(e));
		System.out.println("Vector inhibidor B: " +Arrays.deepToString(b));
		System.out.println("Vector lector L: " +Arrays.deepToString(l));
		System.out.println("Vector sensibilzado extendido Ex: " +Arrays.deepToString(ex));
	}

	/**
	 * Funcion para comprobar si alguna transicion esta sensibilizada
	 * @return true si alguna tr esta sensibilizada; false caso contrario
	 */
	public int[][] getE() {
		return e;
	}
	
	public int[][] getB() {
		return b;
	}

	public void setH(int[][] h) {
		this.h = h;
	}

	public int[][] getZ() {
		return z;
	}

	public void setZ(int[][] z) {
		this.z = z;
	}

	public int[][] getEx() {
		return ex;
	}

	public int[][] getL() {
		return l;
	}

	public void setR(int[][] r) {
		this.r = r;
	}
	
	public int[][] getIncidencia() {
		return incidencia;
	}
	/**
	 * Calcula el vector de inhibicion.
	 */
	public void calculaB() {
		this.b=producto(transponer(h),cero(marcado));
		int[][] hTrans = transponer(h);
		
		for (int i = 0; i < getT(); i++)
		{
			boolean bandera = true;
			for(int j = 0; j < getP(); j++)
			{
				if (hTrans[i][j] == 1) bandera = false; 
			}
			if (bandera) this.b[i][0]=1;
		}
	}
	
	/**
	 * Calcula el nuevo timeStamp para todas las transiciones sensibilizadas.
	 */
	public void calculaTimeStamp() {		
		for (int i = 0; i < getT(); i++)
		{
			if (esSensibilizadaExtendida(i))	timeStamp[i][0]= System.currentTimeMillis();
			else timeStamp[i][0]= 0;			
		}		
	}

	public void calculaL() {
		this.l=producto(transponer(r),uno(marcado));
		int[][] rTrans = transponer(r);
		
		for (int i = 0; i < getT(); i++)
		{
			boolean bandera = true;
			for(int j = 0; j < getP(); j++)
			{
				if (rTrans[i][j] == 1) bandera = false; 
			}
			if (bandera) this.l[i][0]=1;
		}
	}
	public void calculaEx() {
		for (int i= 0; i < getT(); i++)
		{
			if (e[i][0]==1 && b[i][0]==1 && l[i][0]==1) this.ex[i][0] = 1;
			else this.ex[i][0] = 0;
		}
	}
	public void calculaE() {
		for(int i=0;i<getT();i++) {
			if(this.esSensibilizada(i)) e[i][0] = 1; else e[i][0] = 0;
		}		
	}
	
	public static int[][] deepCopy(int[][] original) {
	    if (original == null) {
	        return null;
	    }

	    final int[][] result = new int[original.length][];
	    for (int i = 0; i < original.length; i++) {
	        result[i] = Arrays.copyOf(original[i], original[i].length);
	    }
	    return result;
	}
	
	/**
	 * Chequea si se cumplen los P invariantes para la Red de Petri dada.
	 * @return true si se cumplen los P invariantes, falso en caso contrario
	 */
	public boolean chequearPInvariantes() {
		int sum;
		for(int i=0;i<9;i++) {
			sum=0;
			for(int j=0;j<20;j++) {
				sum += marcado[j][0] * pInvariantes[i][j];
			}
			if(sum != pInvariantesCheck[i][0] ) {
				if(Main.alerts) {
					System.out.println("La suma"+i +" dio: "+sum+" cuando debia dar: "+ pInvariantesCheck[i][0]);
				}	
				return false;
			}
		}		
		return true;			
	}
}