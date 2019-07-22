package carskit.alg.cars.adaptation.dependent;




import com.google.common.base.Function;
import com.google.common.base.Functions;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.algorithms.scoring.PageRankWithPriors;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.OrderedSparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.VisualizationViewer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.List;
import java.util.*;

import static javax.swing.JFrame.EXIT_ON_CLOSE;

public class VecchioGrafo {

    private Graph graph;
    private HashMap<String, String[]> luoghi;
    private ArrayList<String> contesti;
    private List<String> contesto;
    private HashMap<String, ArrayList<String>> preferenze;
    private HashMap<String, Point2D> layoutvertici;
    private int width=1000;
    private int height=800;
    private int nodi_P = 0, nodi_C = 0, nodi_L = 0, nodi_D = 0, archi_PC = 0, archi_CL = 0, archi_LD = 0;
    private int P_map=0, C_map=0, L_map=0, D_map=0;
    static String delimiter= ",";
    static int column_userId=0;
    static int column_item=1;
    static int column_context=2;
    static int column_voto=3;
    static String percorso="D:\\Università\\TESI\\Movie_DePaulMovie_cort\\\\ratings.txt";
    static String userid="0";
    
    public VecchioGrafo(String data, int numero_persone, boolean full_connected, boolean diretto, int number_events) throws IOException {
        
        
        FromFile.SetData(data);
        //Save context user locally
       
        layoutvertici = new HashMap<>();
        //Instantiante Graph Oject
        graph = new OrderedSparseMultigraph<>();
        int num_events = number_events;
       int numero_utenti= getNumeroUtenti();
        //     int numero_utenti= 20;
        //Create user nodes
   
       

        // Read context from file
        contesti = FromFile.getContesti1();
        // Get number of context
        nodi_C = contesti.size();
      //nodi_C=11;
       
        Random r=new Random();
        File file= new File(percorso);
        BufferedReader br=new BufferedReader(new FileReader (file));
        String st;
        String[] all;
        int numCont;
        
        if (full_connected) {
            int i=0;
            // For each user node
            //for (int i = 0; i < numero_utenti; i++) {
               //AGGIUNTA
               while((st=br.readLine())!=null){
                   i++;
                        all=st.split(delimiter);
                       
                        if(all[3].equals("NA")){
                            
                        }
                        else{
                        if(graph.containsVertex("P_"+all[0])){
                            
                        }
                        else{
                            graph.addVertex("P_"+all[0]);
                            nodi_P++;
                        }
                        
              
                        
                    //FINE AGGIUNTA
                // For each different context parameters (TAKING ALL VALUES)
         
          
               if(Integer.parseInt(all[2])>=4){
                
                    // Create edge between (Users - Context Parameters)
                  
                   
                    graph.addEdge("PC:" + (++archi_PC), new Pair<>("P_" + all[0], "C_" + all[3]), EdgeType.UNDIRECTED);
                    graph.addEdge("PC:" + (++archi_PC), new Pair<>("P_" + all[0], "C_" + all[4]), EdgeType.UNDIRECTED);
                    graph.addEdge("PC:" + (++archi_PC), new Pair<>("P_" + all[0], "C_" + all[5]), EdgeType.UNDIRECTED);
                    graph.addEdge("CL:" + (++archi_CL), new Pair<>("C_" + all[3], "L_" + all[1]), EdgeType.UNDIRECTED);
                    graph.addEdge("CL:" + (++archi_CL), new Pair<>("C_" + all[4], "L_" + all[1]), EdgeType.UNDIRECTED);
                    graph.addEdge("CL:" + (++archi_CL), new Pair<>("C_" + all[5], "L_" + all[1]), EdgeType.UNDIRECTED);
                    
                  //  System.out.println("utente "+a + " per il locale "+ all[column_item]+" nel contesto "+ contesti.get(numCont-1)+" ha votato "+all[column_voto]);
                    
                    }
               }
            }//}
               }  else {
            // TODO: capire come connettere le altre persone
            // For each user
            for (int i = 0; i < numero_utenti; i++) {
                // For each different context parameters (TAKING ONLY USER VALUES)
                for (int y = 1; y < contesto.size(); y++) {
                    // Create edge between (Users - User Context Parameters)
                    graph.addEdge("PC:" + (++archi_PC), new Pair<>("P_" + i, contesto.get(y)), EdgeType.UNDIRECTED);
                }
            }
        }//Livello 0-1


        luoghi = FromFile.getPlacesNew();
       //HashMap<String, List<Object>> contestiCateg = FromFile.getContestiCategorizzati();
        //alcuni luoghi hanno lo stesso nome, al momento vengono accorpati (19 luoghi, 852 in totale 833 usati)
        Object[] nomi = luoghi.keySet().toArray();
        nodi_L = nomi.length;
        //nodi_D = FromFile.getCategorie().size();

        for (int i = 0; i < nomi.length; i++) {
            String posto = nomi[i].toString();
            for (int y = 0; y < luoghi.get(posto).length; y++) {
                //HO RIMOSSO IL LIVELLO 3
                //if(diretto) graph.addEdge("LD:" + (++archi_LD), new Pair<>("L_" + posto, "D_" + luoghi.get(posto)[y]), EdgeType.DIRECTED);
                //else graph.addEdge("LD:" + (++archi_LD), new Pair<>("L_" + posto, "D_" + luoghi.get(posto)[y]), EdgeType.UNDIRECTED);
            }
        }//Livello 2-3



        preferenze = new HashMap<>();
        //Per adesso, è spalmato equamente sui contesti
        //Prendo i contesti di fila, prendo un luogo a caso, verifico se il luogo ha tutte le categorie adatte per quel contesto, se si linko altrimenti riprovo
        int p = 0;
        /*for (int i = 0; i < num_events; i++) {
            if (p == contesti.size()) p = 0;
           // List<Object> contestiToCheck = contestiCateg.get(contesti.get(p));
            int rand_L = (RandomControl.getControl().nextInt(luoghi.size()));
            String luogo = nomi[rand_L].toString();
            String[] categorieLuogo = luoghi.get(luogo);
            boolean check = true;
           for (int y = 0; y < categorieLuogo.length; y++) {
             //  if (!contestiToCheck.contains(categorieLuogo[y])) {
                 //   check = false;
               // }
            }

            //System.out.println("Contesto:" +contesti.get(p) + " - Luogo: " + nomi[rand_L]+ " - Contesti Luogo: "+ Arrays.toString(categorieLuogo) + " - Check: " + check);
           if (check) {
                graph.addEdge("CL:" + (++archi_CL), new Pair<>("C_" + contesti.get(p), "L_" + nomi[rand_L].toString()), EdgeType.UNDIRECTED);
                //System.out.println("Ho collegato C_"+contesti.get(p)+ " e L_"+nomi[rand_L].toString());
                if(preferenze.containsKey(contesti.get(p))){
                    preferenze.get(contesti.get(p)).add(nomi[rand_L].toString());
                }else{
                    ArrayList<String> temp = new ArrayList<String>();
                    temp.add(nomi[rand_L].toString());
                    preferenze.put(contesti.get(p), temp);
                }
                p++;
            } else {
                i--;
            }

    
        }//Livello 1-2*/
        
    }

    public VecchioGrafo() {
		// TODO Auto-generated constructor stub
	}

	public void Dettagli(String citta, int numero_persone, boolean full_connected, boolean diretto, List<String> input_contesto, int number_events) throws IOException{
        //-----------DETTAGLI-------------------
        //alcuni dati sul jung.Grafo a scopo di verifica
        //Livello 0 - nodi Persona, al momento caso singolo =1;
        //Livello 1 - nodi Contesto, dati dal file contesti.csv generato in base a quelli forniti dal professore, volendo sono modificabili
        //Livello 2 - nodi Luogo, estratti dal file business_torino.csv
        //Livello 3 - nodi Descrizione, estratti dal file business_torino.csv
        //Archi 0-1 PC - archi da Persona a Contesti, livello full connected (TOTEST NOT FULL CONNECTED?)
        //Archi 1-2 CL - archi da Contesti a Luoghi, al momento generati casualmente in base al valore num_events, ma da sostituire con le azioni passate degli utenti
        //Archi 2-3 LD - archi da Luoghi a Descrizioni, livello statico estratti dal file business_torino.csv
		System.out.println("_________________");
        System.out.println("GRAPH DETAILS");
		System.out.println("Livello 0 - User Nodes:\t#"+ nodi_P);
		System.out.println("Livello 1 - Context Nodes:\t#"+ nodi_C);
		System.out.println("Livello 2 - Businesses Nodes:\t#"+ nodi_L);
		//System.out.println("Livello 3 - Category Nodes:\t#"+ nodi_D);
		System.out.println("Archi 0-1 User - Context:\t#"+ archi_PC);
		System.out.println("Archi 1-2 Context - Businesses:\t#"+ archi_CL);
		//System.out.println("Archi 2-3 Businesses - Category:\t#"+ archi_LD);
        String conn = full_connected ? "Completa" : "SoloContesto";
        String grap = diretto ? "Diretto" : "Misto";
        System.out.println("- Connessione: " + conn + " - Grafo: " + grap + " - Contesto: " + input_contesto);
        //System.out.println("---Collegamenti:");
        HashMap<String, ArrayList<String>> prefs = getP();
        for (Map.Entry<String, ArrayList<String>> item : prefs.entrySet()) {
            ArrayList<String> temp_prefs_place = item.getValue();
            Collections.sort(temp_prefs_place);
            String si="-";
            if(contesto.contains("C_"+item.getKey())) si="+";

           //tipo 1
           // String pref_cats = " - {";
           // for (String s : temp_prefs_place) {
             //   pref_cats = pref_cats +  Arrays.toString(luoghi.get(s)) + " - ";
            //}
            //pref_cats=pref_cats.substring(0, pref_cats.length() -3);
            System.out.println(si + item.getKey() + " -> " + temp_prefs_place);

/*

            //tipo 2
            String pref_cats = "[";
            for (String s : temp_prefs_place) {
                pref_cats = pref_cats + s +  Arrays.toString(luoghi.get(s)).replace("[", "(").replace("]", ")") + " - ";
            }
            pref_cats=pref_cats.substring(0, pref_cats.length() -3);
            System.out.println(si + item.getKey() + " -> " + pref_cats + "]");
*/
        }
       /* System.out.print("---Collegamenti Contesto: ");
        List<String> list = new ArrayList<>();
        for (String s: contesto) {
            if (s == contesto.get(0)) continue;
            s=s.substring(2);
            list.addAll(prefs.get(s));
        }
        System.out.println(list);*/
        //System.out.print("---Categorie Collegamenti:");
        //List<String> list2 = new ArrayList<>();
        //for (String l: list) {
        //    list2.addAll(Arrays.asList(luoghi.get(l)));
        //}
       // System.out.println(list2);
    }

    public HashMap<String, ArrayList<String>> getP() {
        return preferenze;
    }

    public HashMap<String, Double> Pagerank(int top_results) {

        HashMap<String, Double> result_pr = new HashMap<String, Double>();

        PageRank ranker = new PageRank(graph, 0.3);
        ranker.evaluate();

        System.out.println("\n---PageRank - Tolerance = " + ranker.getTolerance()+" - Dump factor = " + (1.00d - ranker.getAlpha()+ " - Max iterations = " + ranker.getMaxIterations()));
        HashMap<String, Double> map = new HashMap();
        for (Object v : graph.getVertices()) {
            if (v.toString().contains("L_") && !ranker.getVertexScore(v).toString().equals("0.0")) {
                map.put(v.toString(), (Double) ranker.getVertexScore(v));
            }
        }

        Object[] obj = map.entrySet().stream()
                .sorted((k1, k2) -> -k1.getValue().compareTo(k2.getValue())).toArray();

        for (int i = 0; i < top_results; i++) {
            String nome = obj[i].toString();
            //System.out.println(nome);
            String score = nome.substring(nome.indexOf("=") + 1);
            nome = nome.substring(nome.indexOf("_") + 1, nome.indexOf("="));
            result_pr.put(nome, Double.valueOf(score));
            String stamp = i + 1 + " - " + nome + " - Score: " + score;
            String[] cats = luoghi.get(nome);
            
            	/**
            	 * Fare attenzione qui
            	 */
            
               // ranker.getVertexScore("L_"+nome);
                System.out.println(stamp);
            
           /* for (String s : cats
            ) {
                stamp = stamp.concat(s + ",");
            }
            stamp = stamp.substring(0, stamp.length() - 1).concat("]");
            System.out.println(stamp);
    */    }

        return result_pr;
    }

    public HashMap<String, Double> PagerankPriors(int top_results, Function f, HashMap<String, Integer> itemVoto ) throws FileNotFoundException, IOException {
        
           File file= new File(percorso);
           BufferedReader br= new BufferedReader(new FileReader(file));
           
          String st;
          String[] all;
          
        HashMap<String, Double> result_prp = new HashMap<String, Double>();

      /* Function f = ((Object i) -> {
           
            if (contesto.contains(i)) return 1.0;
            else return 0.0;
        });
           /*     Function f = ((Object i) -> {
                   
            if (contesto.contains(i)) return 1.0;
            else return 0.0;
        });;*/

        PageRankWithPriors ranker = new PageRankWithPriors(graph, f, 0.7);
       
        ranker.evaluate();

        System.out.println("\n---PageRankWithPriors - Tolerance = " + ranker.getTolerance()+" - Dump factor = " + (1.00d - ranker.getAlpha()+ " - Max iterations = " + ranker.getMaxIterations()));
        //Magari dopo i risultati rifiltrare per categoria per ottenere risultati completamente coerenti
        //System.out.println("Contesto preso in considerazione: " + contesto);
        HashMap<String, Double> map = new HashMap();
        for (Object v : graph.getVertices()) {
            if (v.toString().contains("L_") && !ranker.getVertexScore(v).toString().equals("0.0")) {
                map.put(v.toString(), (Double) ranker.getVertexScore(v));
            }
        }

        Object[] obj = map.entrySet().stream()
               .sorted((k1, k2) -> -k1.getValue().compareTo(k2.getValue())).toArray();
    


        for (int i = 0; i < top_results; i++) {
            
    
            String nome = obj[i].toString();
        /* if(itemVoto.containsKey(nome.substring(nome.indexOf("_") + 1, nome.indexOf("=")))){
              //System.out.println("Item già votato");
             i--;
         }
         else{*/
                 String score = nome.substring(nome.indexOf("=") + 1);
            nome = nome.substring(nome.indexOf("_") + 1, nome.indexOf("="));
           
            String stamp = i + 1 + " - " + nome + " - Score: " + score;
            String[] cats = luoghi.get(nome);
            System.out.println(stamp);//}
            
           
/*            for (String s : cats
            ) {
                stamp = stamp.concat(s + ",");
            }
            stamp = stamp.substring(0, stamp.length() - 1).concat("]");
            System.out.println(stamp);
     */   
       
    } return result_prp;
    }

    public void AddToMap(Object j){
        if(j.toString().contains("P_")){
            float offset= (float)(P_map+1)/(nodi_P+1);
            layoutvertici.put(j.toString(),new Point2D.Float(offset*width, height/11));
            P_map++;
        }
        else if (j.toString().contains("C_")){
            float offset= (float)(C_map+1)/(nodi_C+1);
            layoutvertici.put(j.toString(),new Point2D.Float(offset*width, 4*(height/11)));
            C_map++;
        }
        else if (j.toString().contains("L_")){
            float offset= (float)(L_map+1)/(nodi_L+1);
            layoutvertici.put(j.toString(),new Point2D.Float(offset*width, 7*(height/11)));
            L_map++;
        }
        else if (j.toString().contains("D_")){
            float offset= (float)(D_map+1)/(nodi_D+1);
            layoutvertici.put(j.toString(),new Point2D.Float(offset*width, 10*(height/11)));
            D_map++;
        }
        else{
            layoutvertici.put(j.toString(),new Point2D.Float(0, 0));
            System.out.println("GRAVE ERRORE");}
    }


    public void Mostra() {
        graph.getVertices().stream().forEach((Object j) -> AddToMap(j));
        Function<String, Point2D> vertexLocations = Functions.forMap(layoutvertici);

        StaticLayout layout = new StaticLayout(graph, vertexLocations);
        VisualizationViewer<String, String> vs = new VisualizationViewer<String, String>(layout, new Dimension(width, height));
        vs.getRenderer().setVertexRenderer(new CustomRenderer());
        JFrame frame = new JFrame();

        vs.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode()== KeyEvent.VK_P) {
                    Container content = frame.getContentPane();
                    BufferedImage img = new BufferedImage(content.getWidth(), content.getHeight(), BufferedImage.TYPE_INT_RGB);
                    Graphics2D g2d = img.createGraphics();
                    content.printAll(g2d);
                    g2d.dispose();

                    try {
                        ImageIO.write(img, "png", new File("GraphImage.png"));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        frame.getContentPane().add(vs);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setTitle("Holistic Recommendation Graph");
        frame.setIconImage(new ImageIcon("data/icon.png").getImage());
        frame.setLocationRelativeTo(null);

    }

    public void Esporta(String type, String filename) throws IOException {

        //fix graphml export
        if (type.equals("GraphML")) ExportGraph.exportAsGraphML(graph, filename);
        else if (type.equals("Net")) ExportGraph.exportAsNet(graph, filename);
        else System.out.println("Export Type Error");
    }
    
    
    
    
   public Integer getNumeroUtenti() throws FileNotFoundException, IOException{
       
      File file=new File(percorso);
      BufferedReader br= new BufferedReader(new FileReader(file));
      ArrayList<String> utenti=new ArrayList<String>();
      utenti.add("1");
  
         
        String st;
	String[] all;
	

	while ((st = br.readLine()) != null) {
		all = st.split(delimiter);
                if(utenti.contains(all[column_userId])){
                    
                }
                else{
		utenti.add(all[column_userId]);
                }
		}
		br.close();
		return utenti.size();
         
          
      }
   
   
   static List<String> getUtentiContesti(int par) throws FileNotFoundException, IOException{
       
       File file= new File(percorso);
       BufferedReader br= new BufferedReader(new FileReader(file));
       List<String> lista= new ArrayList<String>();
       
       String st;
       String[] all;
       int i=1;
       
       lista.add("P_"+par);
       while((st=br.readLine())!=null){
           
           
               all=st.split(delimiter);
               if(Integer.parseInt(all[column_userId])==par && lista.contains("C_"+all[column_context])==false && Integer.parseInt(all[column_voto])>=3 ){
                  
                   
                   lista.add("C_"+all[column_context]);
                   
                   
           
               }
           
       } return lista;
   }
   
    static HashMap<String, Integer> getItemVoto(String par) throws FileNotFoundException, IOException{
       
       File file= new File(percorso);
       BufferedReader br= new BufferedReader(new FileReader(file));
       HashMap<String, Integer> itemVoti= new HashMap<String, Integer>();
       
       String st;
       String[] all;
       int i=1;
       

       while((st=br.readLine())!=null){
           
           
               all=st.split(delimiter);
               if(all[0].equals(par)){
               itemVoti.put(all[1], Integer.parseInt(all[2]));
               }
           
       }return itemVoti;
   }
   
   
   
    
      
   }
    
    


