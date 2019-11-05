package concurrente;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regex {

	String logStr;
	Matcher[] m;
	int countFound;
	int autos;
	int []stats;
	Patron patron[];
	Pattern[] p = {
			Pattern.compile( "(?:a)([a-df-q]*?)(?:e)([a-hj-q]*?)(?:i)([a-jl-q]*?)(?:k)([a-p]*?)(?:q)([a-ln-q]*?)(?:m)([a-np-q]*?)(?:o)"  ),
			Pattern.compile( "(?:a)([a-df-q]*?)(?:e)([a-hj-q]*?)(?:i)([a-jl-q]*?)(?:k)([a-p]*?)(?:q)([a-mo-q]*?)(?:n)([a-oq]*?)(?:p)"    ),
			Pattern.compile( "(?:a)([a-df-q]*?)(?:e)([a-ik-q]*?)(?:j)([a-km-q]*?)(?:l)([a-p]*?)(?:q)([a-ln-q]*?)(?:m)([a-np-q]*?)(?:o)"  ),
			Pattern.compile( "(?:a)([a-df-q]*?)(?:e)([a-ik-q]*?)(?:j)([a-km-q]*?)(?:l)([a-p]*?)(?:q)([a-mo-q]*?)(?:n)([a-oq]*?)(?:p)"    ),
			Pattern.compile( "(?:b)([a-eg-q]*?)(?:f)([a-hj-q]*?)(?:i)([a-jl-q]*?)(?:k)([a-p]*?)(?:q)([a-ln-q]*?)(?:m)([a-np-q]*?)(?:o)"  ),
			Pattern.compile( "(?:b)([a-eg-q]*?)(?:f)([a-hj-q]*?)(?:i)([a-jl-q]*?)(?:k)([a-p]*?)(?:q)([a-mo-q]*?)(?:n)([a-oq]*?)(?:p)"    ),
			Pattern.compile( "(?:b)([a-eg-q]*?)(?:f)([a-ik-q]*?)(?:j)([a-km-q]*?)(?:l)([a-p]*?)(?:q)([a-ln-q]*?)(?:m)([a-np-q]*?)(?:o)"  ),
			Pattern.compile( "(?:b)([a-eg-q]*?)(?:f)([a-ik-q]*?)(?:j)([a-km-q]*?)(?:l)([a-p]*?)(?:q)([a-mo-q]*?)(?:n)([a-oq]*?)(?:p)"    ),
			Pattern.compile( "(?:c)([a-fh-q]*?)(?:g)([a-hj-q]*?)(?:i)([a-jl-q]*?)(?:k)([a-p]*?)(?:q)([a-ln-q]*?)(?:m)([a-np-q]*?)(?:o)"  ),
			Pattern.compile( "(?:c)([a-fh-q]*?)(?:g)([a-hj-q]*?)(?:i)([a-jl-q]*?)(?:k)([a-p]*?)(?:q)([a-mo-q]*?)(?:n)([a-oq]*?)(?:p)"    ),
			Pattern.compile( "(?:c)([a-fh-q]*?)(?:g)([a-ik-q]*?)(?:j)([a-km-q]*?)(?:l)([a-p]*?)(?:q)([a-ln-q]*?)(?:m)([a-np-q]*?)(?:o)"  ),
			Pattern.compile( "(?:c)([a-fh-q]*?)(?:g)([a-ik-q]*?)(?:j)([a-km-q]*?)(?:l)([a-p]*?)(?:q)([a-mo-q]*?)(?:n)([a-oq]*?)(?:p)"    ),
			Pattern.compile( "(?:d)([a-q]*?)(?:h)"																					     )
	};
	
	String tInvariante[] = {
			"Entrada 1, Planta Baja, Calle A",
			"Entrada 1, Planta Baja, Calle B",
			"Entrada 1, Planta Alta, Calle A",
			"Entrada 1, Planta Alta, Calle B",
			"Entrada 2, Planta Baja, Calle A",
			"Entrada 2, Planta Baja, Calle B",
			"Entrada 2, Planta Alta, Calle A",
			"Entrada 2, Planta Alta, Calle B",
			"Entrada 3, Planta Baja, Calle A",
			"Entrada 3, Planta Baja, Calle B",
			"Entrada 3, Planta Alta, Calle A",
			"Entrada 3, Planta Alta, Calle B",
			"Encendido-Apagado Cartel"
	};

	public Regex(String log, int autos) {
		this.logStr = log;
		this.autos = autos;
		m = new Matcher[p.length];
		stats = new int[p.length];
		countFound = 0;
	}

	/**
	 * Chequea si se cumplen los T invariantes para la Red de Petri dada.
	 */	
	public void chequearTInvariantes() {
		int index[][] = new int[2][m.length];
		patron = new Patron[m.length];
		while (countFound < autos) { // mientras no encuentre todos no salgo

			// actualizo matcher y los indices por defecto
			for (int i = 0; i < p.length; i++) {
				m[i] = p[i].matcher(logStr);
				index[0][i] = logStr.length();
				index[1][i] = logStr.length();
				patron[i] = new Patron(i, logStr.length() + 1, false);
			}
			// para cada matcher veo donde empieza y termina
			// calculo los matchs
			for (int i = 0; i < m.length; i++) {
				// si tiene match guardo el inicio y el final
				if (m[i].find()) {
					index[0][i] = m[i].start();
					index[1][i] = m[i].end();
					patron[i].setIndice(i);
					patron[i].setTiempo(m[i].end());
					patron[i].setBueno(true);
					if (Main.debugRegex) {
						System.out.println("m[" + i + "] tiene match en rango [" + index[0][i] + "-" + index[1][i]
								+ "]: " + m[i].group());
					}
				}
			}
			Arrays.sort(patron, new SortbyTiempo());

			for (int i = 0; i < patron.length; i++) {
				if (Main.debugRegex) {
					System.out.println(patron[i]);
				}
			}

			for (int k = 0; k < patron.length; k++) {
				if (good(patron[k].getIndice()) && patron[k].isBueno()) {
					take(patron[k].getIndice());
					break;
				} else {
					if (Main.debugRegex) {
						System.out.println("Falla: " + patron[k]);
					}
				}
			}
			if (Main.debugRegex) {
				System.out.println("FIN ITERACION " + countFound);
			}
		}
	}
	
	public void printStats() {
		for(int i=0;i<stats.length;i++) {
			System.out.println(tInvariante[i]+": "+stats[i]);
		}
	}

	private boolean good(int matcher) {
		String s = m[matcher].replaceFirst("$1$2$3$4$5$6");
		char aux = 'a';
		if (s.length() > 0) {
			aux = s.charAt(0);
		}
		if (aux != 'a' && aux != 'b' && aux != 'c') {
			if (Main.debugRegex) {
				System.out.println("GOOD FALLA: " + s);
			}
			return false;
		}
		return true;
	}

	private void take(int matcher) {
		if(Main.debugRegex) {
			System.out.print(matcher + " ");
			System.out.print(m[matcher].group());
			System.out.println();
		}
		
		logStr = m[matcher].replaceFirst("$1$2$3$4$5$6");
		countFound++;
		stats[matcher]++;
		for (int i = 0; i < p.length; i++) {
			m[i] = p[i].matcher(logStr);
		}
		
		if(Main.debugRegex) {
			System.out.println("["+logStr.length()+"]: "+logStr);
			System.out.println(countFound+"/"+autos);					
		}
	}

}
