package concurrente;

import java.io.File;
import java.util.Arrays;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class PipeParser {
	
	private	int marcado[][];
	private int iPlus[][];
	private int iMinus[][];
	private int iCombined[][];
	private int inhibition[][];
	private int armReader[][];
	private int sizePlace = 0;
	private int sizeTransition = 0;
	private Document doc;
	private NodeList placeList;
	private NodeList transitionList;
	private NodeList arcList;
	
	public PipeParser(String path) {
		openFile(path);
		countPlace();
		setInitialMark();
		countTransition();
		setMatrix();
		calculateCombined();
		calculateArmReader();
	}
   
	/**
	 * Funcion que abre el archivo xml del Pipe. Lista plazas, transiciones y arcos
	 * @param path
	 */
   private void openFile(String path){	   
	   try{
		   File inputFile = new File(path);
	       DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	       DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	       doc = dBuilder.parse(inputFile);
	       doc.getDocumentElement().normalize();
//	       System.out.println("Root element :" + doc.getDocumentElement().getNodeName());	       
	   }catch(Exception e) {
		   e.printStackTrace();
	   }
	   placeList = doc.getElementsByTagName("place");
  	   transitionList = doc.getElementsByTagName("transition");
       arcList = doc.getElementsByTagName("arc");
   }
   
   /**
    * Se cuenta la cantidad de plazas y se setea sizePlace
    */
   private void countPlace() {
	 //CONTAR CANTIDAD DE PLAZAS	  
	   int place=0;
      for (int temp = 0; temp < placeList.getLength(); temp++) {
          Node nNode = placeList.item(temp);
//          System.out.println("\nCurrent Element :" + nNode.getNodeName());
          if (nNode.getNodeType() == Node.ELEMENT_NODE) {
//             Element eElement = (Element) nNode;
//             System.out.println("" 
//                     + eElement.getAttribute("id"));
        	  place++;
             //setSizePlace(getSizePlace() + 1);
          }
      }
      setSizePlace(place);
   }

   /**
    * Se setea el marcado inicial
    */
	private void setInitialMark() {
		marcado = new int[getSizePlace()][1];		
       for (int temp = 0; temp < placeList.getLength(); temp++) {
           Node nNode = placeList.item(temp);
//           System.out.println("\nCurrent Element :" + nNode.getNodeName());
           if (nNode.getNodeType() == Node.ELEMENT_NODE) {
              Element eElement = (Element) nNode;
              int place = Integer.parseInt(eElement.getAttribute("id").replaceAll("\\D+",""));
              //System.out.println("get"+getSizePlace()+"initialmark"+initialMark.length);
              int value = Integer.parseInt(
              eElement
              .getElementsByTagName("initialMarking")
              .item(0)
              .getTextContent()
              .replaceAll("\\D+","")
				);
              
//              System.out.println(""+eElement
//              .getElementsByTagName("initialMarking")
//              .item(0)
//              .getTextContent()
//              .replaceAll("\\D+",""));
              marcado[place][0] = value;
           }
       }        
//       System.out.println("Matriz marcado inicial");
//       System.out.println(Arrays.toString(initialMark));
       
	}
	
	/**
	 * Funcion para obtener un elemento hijo de un elemento padre segun su nombre
	 * @param parent Elemento padre
	 * @param name String para matchear el nombre del hijo
	 * @return
	 */
	public static Element getDirectChild(Element parent, String name)
	{
	    for(Node child = parent.getFirstChild(); child != null; child = child.getNextSibling())
	    {
	        if(child instanceof Element && name.equals(child.getNodeName())) return (Element) child;
	    }
	    return null;
	}
	
	
	/**
	 * Funcion para contar transiciones, luego se setea en sizeTransition
	 */
	private void countTransition() {	  	
		int sizeTransition = 0;
        for (int temp = 0; temp < transitionList.getLength(); temp++) {
            Node nNode = transitionList.item(temp);
//            System.out.println("\nCurrent Element :" + nNode.getNodeName());
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
//               Element eElement = (Element) nNode;
//               System.out.println("" 
//                       + eElement.getAttribute("id"));
               sizeTransition++;
            }
        }
        setSizeTransition(sizeTransition);
	}

	/**
	 * Se setea la matriz de I+,I- e inhibicion.
	 */
	private void setMatrix() {
		  //SETEAR TAM DE LAS MATRICES DE INCIDENCIA
        iPlus = new int[sizePlace][sizeTransition];
        iMinus = new int[sizePlace][sizeTransition];
        iCombined = new int[sizePlace][sizeTransition];
        inhibition = new int[sizePlace][sizeTransition];
        armReader = new int[sizePlace][sizeTransition];
        //SETEAR VALORES DE MATRICES DE INCIDENCIA
        for (int temp = 0; temp < arcList.getLength(); temp++) {
           Node nNode = arcList.item(temp);
//           System.out.println("\nCurrent Element :" + nNode.getNodeName());
           if (nNode.getNodeType() == Node.ELEMENT_NODE) {
              Element eElement = (Element) nNode;
//              System.out.println("target: " 
//                 + eElement.getAttribute("target"));
//              System.out.println("source: " 
//                 + eElement.getAttribute("type value"));
//              System.out.println("valor: " 
//                 + eElement
//                 .getElementsByTagName("value")
//                 .item(0)
//                 .getTextContent());
              Element nodoHijo = getDirectChild(eElement, "type");
//              System.out.println("messi"+nodoHijo.getAttribute("value"));
              String tipoOrigen = eElement.getAttribute("target").substring(0, 1);
              int origen = Integer.parseInt(eElement.getAttribute("target").replaceAll("\\D+",""));
//              System.out.println("origen"+origen+eElement.getAttribute("target"));
//              String tipoDestino = eElement.getAttribute("source").substring(0, 1);
              int destino = Integer.parseInt(eElement.getAttribute("source").replaceAll("\\D+",""));
//              System.out.println("destino"+destino+eElement.getAttribute("source"));
//              System.out.println(tipoOrigen + " " + origen);
//              System.out.println(tipoDestino + " " + destino);
              if(!nodoHijo.getAttribute("value").equals("inhibitor")) 
              {
            	  if(tipoOrigen.equals("P")) 
            	  {
            		  iPlus[origen][destino]=1;
            	  }
            	  if(tipoOrigen.equals("T")) 
            	  {
            		  iMinus[destino][origen]=1;
            	  }
              }
              else 
              	{
        	  		inhibition[destino][origen]=1;  
              	}            	  
           }
        }
	}
	
	/**
	 * Funcion para calcular I
	 */
	private void calculateCombined() {
		for (int i = 0; i < sizePlace; i++)
	     {
	    	 for (int j = 0; j < sizeTransition; j++)
	         {iCombined[i][j] = iPlus[i][j] - iMinus[i][j];}
	     }		
	}
	
	public int[][] getInhibition(){
		return inhibition;
	}
	
	public int[][] getCombined() {
		return iCombined;
	}
	
	private void calculateArmReader() {
		for (int i = 0; i < sizePlace; i++)
	     {
	    	 for (int j = 0; j < sizeTransition; j++)
	         {armReader[i][j] = iPlus[i][j] * iMinus[i][j];
	    	 }
	     }
	}
	
	/**
	 * Funcion para imprimir todas las matrices
	 */
	public void printAll() {
		//IMPRIMIR TAM DE PLAZAS Y TRANCISIONES
	     System.out.println("Cantidad de plazas: " + sizePlace);
	     System.out.println("Cantidad de transiciones: " + sizeTransition);
	     //IMPRIMIR MATRICES
	     System.out.println("Matriz I+");
	     System.out.println(Arrays.deepToString(iPlus));   
	     System.out.println("Matriz I-");
	     System.out.println(Arrays.deepToString(iMinus));
	     System.out.println("Matriz Inhibition");
	     System.out.println(Arrays.deepToString(inhibition));
	     System.out.println("Matriz I combinada");
	     System.out.println(Arrays.deepToString(iCombined));
	     System.out.println("Matriz marcado inicial");
	     System.out.println(Arrays.deepToString(marcado));
	     System.out.println("Matriz brazo lector");
	     System.out.println(Arrays.deepToString(armReader));
	}
	
	public int[][] getMarcado() {
		return marcado;
	}
   /**
    * @return sizePlace
    */
	public int getSizePlace() {
		return sizePlace;
	}
	/**  Setea cantidad de plazas
	 * @param sizePlace
	 */
	public void setSizePlace(int sizePlace) {
		this.sizePlace = sizePlace;
	}
   /** 
    * @return sizeTransition
    */
	public int getSizeTransition() {
		return sizeTransition;
	}
	
	/**
	 * Setea cantidad de trancisiones
	 * @param sizeTransition
	 */
	public void setSizeTransition(int sizeTransition) {
		this.sizeTransition = sizeTransition;
	}

	public void setArmReader(int[][] armReader) {
		this.armReader = armReader;
	}

	public int[][] getArmReader() {
		return armReader;
	}
	
	
	
}