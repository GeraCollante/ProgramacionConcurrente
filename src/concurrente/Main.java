package concurrente;

public class Main {

	public static boolean debug = false;
	public static boolean debugRegex = false;
	public static boolean alerts = true;
	public static boolean log = true;
	public static int nAutos;
	public static int timeScale =  1;
	
	public static void main(String[] args) {
	
	if(Main.alerts) {
		System.out.println("Modo ALERTAS");
	}	
	
	if(Main.debug) {
		System.out.println("Modo DEBUG");
	}
	
	if(Main.debugRegex) {
		System.out.println("Modo DEBUG Regex");
	}
	
		String pathToFile ="./Red/RdPv2.xml";
		PipeParser pp = new PipeParser(pathToFile);
		RedPetri rp = new RedPetri();
		rp.setMarcado(pp.getMarcado());	
		rp.setIncidencia(pp.getCombined());
		rp.setH(pp.getInhibition());
		rp.setR(pp.getArmReader());		
		rp.calculaE();
		rp.calculaL();
		rp.calculaB();
		rp.calculaEx();
		Politica politicaA = new PoliticaA() ;
		Politica politicaB = new PoliticaB() ;
		GestorMonitor gdm = new GestorMonitor(rp,politicaA);
		gdm.setPolitica(politicaB);		
		rp.setMonitor(gdm);
		nAutos = rp.getMarcado() [0][0];
		
		if(Main.debug) {
			rp.printAll();
			System.out.println( rp.chequearPInvariantes() );		
		}

		Thread threads[]=new Thread[11];

		threads[0] = new Thread(new Entrada(0, 4, gdm, nAutos));
		threads[0].setName("Entrada "+1);
		threads[1] = new Thread(new Entrada(1, 5, gdm, nAutos));
		threads[1].setName("Entrada "+2);
		threads[2] = new Thread(new Entrada(2, 6, gdm, nAutos));
		threads[2].setName("Entrada "+3);
		
		threads[3] = new Thread(new Cartel(3, 7, gdm));
		threads[3].setName("Cartel");		
		threads[3].setDaemon(true);
		
		threads[4] = new Thread(new DisparoUnico(8,1, gdm));
		threads[4].setName("Ingreso PB");
		threads[4].setDaemon(true);
		threads[5] = new Thread(new DisparoUnico(9,1, gdm));
		threads[5].setName("Ingreso PA");
		threads[5].setDaemon(true);
		threads[6] = new Thread(new DisparoUnico(10,10, gdm));
		threads[6].setName("Egreso PB");
		threads[6].setDaemon(true);
		threads[7] = new Thread(new DisparoUnico(11,10, gdm));
		threads[7].setName("Egreso PA");
		threads[7].setDaemon(true);
		
		threads[8] = new Thread(new DisparoUnico(16,1,gdm));
		threads[8].setName("Caja "+1);
		threads[8].setDaemon(true);
		
		threads[9] = new Thread(new Salida(12, 14, gdm, nAutos));
		threads[9].setName("Salida "+1);
		threads[10] = new Thread(new Salida(13, 15, gdm, nAutos));
		threads[10].setName("Salida "+2);
		
		System.out.println("INICIO del programa");
		
		for (int i =0;i<threads.length;i++) {
			threads[i].start();
		}
		
		try {
			threads[9].join();
			threads[10].join();
			Thread.sleep(5000);
			System.out.println();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		Regex reg = new Regex(rp.getLog(),nAutos);
		reg.chequearTInvariantes();
		reg.printStats();
		
		System.out.println("FIN del programa");
	}
}
