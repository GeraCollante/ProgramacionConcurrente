package concurrente;


public class GestorMonitor {

	private Mutex mtx;
	private RedPetri rdp;
	private Cola[] colas;
	private Politica politica;

	private int [] vs,vc,m;
	private boolean k; 
	
	public GestorMonitor(RedPetri rdp,Politica politica){
		this.mtx = new Mutex();	
		this.politica = politica;
		this.rdp = rdp;
		
		vc = new int[rdp.getT()];   
		m = new int[rdp.getT()];		
		vs = new int[rdp.getT()];
		this.colas =  new Cola[this.rdp.getT()];
		for(int j = 0; j < colas.length; j++){
			colas[j] = new Cola();			
		}
	}
	
	/**
	 * Intenta disparar la transicion dentro de la seccion critica.
	 * @param transicion la transicion de la Red de Petri a disparar
	 */
	public void disparar_transicion(int transicion){
		
		try {
			mtx.acquire();
		}catch (Exception e) {
			e.printStackTrace();
		}
		k= true;		
		while(k){
			k= rdp.disparoTemporal(transicion);			
			if(k){
				vs = RedPetri.transponer(rdp.getEx())[0];
				m = get_m(vs,vc,m);
				if(m_no_cero(m)!=0){
					int next_transicion = politica.cual(m);					
					colas[next_transicion].release();
					break;					
				}				
				else{
					k = false;
				}
			}			
			else {
				mtx.release();				
				colas[transicion].acquire();
				mtx.acquire();
				k=true;
			}
		}
		mtx.release();
		return;
	}
	
	/**
	 * Chequea que el vector m tenga al menos un valor distinto de cero.
	 * @param m
	 * @return true si algun valor es distinto de cero, falso en caso contrario
	 */
	private int m_no_cero(int[] m) {
		for(int b : m){ if(b!=0) return 1; } return 0;
	}

	/**
	 * Hace el AND elemento a elemento entre vs y vc
	 * @param vs Vector de transiciones sensibilizadas
	 * @param vc Vector de colas en cada transicion
	 * @param m	Vector m donde se aloja el resultado
	 * @return m resultado de la operacion
	 */
	private int[] get_m(int [] vs,int [] vc, int [] m) {
		for(int j = 0; j < this.colas.length; j++){			
			vc[j] = this.colas[j].quienes_estan();
			m[j] = vs[j] & vc[j];
		}
		return m;
	}

	public Mutex getMutex() {
		return mtx;
	}
	
	public Politica getPolitica() {
		return politica;
	}

	public void setPolitica(Politica politica) {
		this.politica = politica;
	}
	
}
