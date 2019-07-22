package carskit.alg.cars.adaptation.dependent.sim;

import java.util.HashMap;
import java.util.List;
import java.util.AbstractMap.SimpleImmutableEntry;

import org.apache.commons.math3.analysis.differentiation.GradientFunction;

import com.google.common.base.Function;
import com.google.common.collect.HashBasedTable;

import carskit.alg.cars.adaptation.dependent.VecchioGrafo;
import carskit.data.structure.SparseMatrix;
import carskit.generic.ContextRecommender;
import carskit.generic.Recommender;
import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.algorithms.scoring.PageRankWithPriors;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.OrderedSparseMultigraph;
import edu.uci.ics.jung.graph.event.GraphEvent;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;
import librec.data.DenseMatrix;
import librec.data.MatrixEntry;
import librec.data.SymmMatrix;

public class P_RANK extends ContextRecommender {
	
	static Graph grafo;
    private int nodi_P = 0, nodi_C = 0, nodi_L = 0, nodi_D = 0, archi_PC = 0, archi_CL = 0, archi_LD = 0;
    private int P_map=0, C_map=0, L_map=0, D_map=0;
    HashMap<String, Double> map;
    String user;
   static  PageRank ranker ;
   static SparseMatrix train;


    
	
    public P_RANK(SparseMatrix trainMatrix, SparseMatrix testMatrix, int fold) {
		super(trainMatrix, testMatrix, fold);
		// TODO Auto-generated constructor stub
		train=trainMatrix;
	}
	
	/*The main task of the setup method is to initialize the member variables. 
	 * For example, reading arguments from the configuration file. If the algorithm does not need to set extra configurations, the program does not need to re-write the methods.
	 * It should be noted that, in the rewrite of the setup method, users need to call the original abstract class in the setup method. That is, to put super.setup() in the first line of the implementation, 
	 * to ensure that the basic parameters of the algorithm is initialized.
	 * (non-Javadoc)
	 * @see carskit.generic.Recommender#initModel()
	 */
	
		protected void initModel() throws Exception {
    	/**
    	 * inizializzando il modello della superclasse vengono inizializzati i parametri di base
    	 */
        super.initModel();
        /**
    	 * Impostazione del nome dell'algoritmo
    	 */
        this.algoName = "P_RANK";
        /**
         * inizializzazione del grafo simple structure
         */
       grafo= new OrderedSparseMultigraph<>();
       
       map = new HashMap();
       
  
        }
    
		/**
		 *Per ottenere l'utente  	rateDao.getUserId(rateDao.getUserIdFromUI(ui))
		 *Per ottenere l'item   	rateDao.getItemId(rateDao.getItemIdFromUI(ui))
		 *Per ottenere il contesto 	rateDao.getContextConditionId(ctx)
		 *Per ottenere il rating singolo me.get
		 */
    
	    @Override
	    protected void buildModel() throws Exception {

	       

	            loss = 0;
	            for (MatrixEntry me : trainMatrix) {
	            	
	            	//System.out.println(me.get());
	                int ui = me.row(); // user-item
	              	
	               // System.out.println(rateDao.getUserId(rateDao.getUserIdFromUI(ui)));
	                int u= rateDao.getUserIdFromUI(ui);

	               //System.out.println(rateDao.getItemId(rateDao.getItemIdFromUI(ui)));    
	               
	                grafo.addVertex("P_"+rateDao.getUserId(rateDao.getUserIdFromUI(ui)));
	                //System.out.print("P_"+rateDao.getUserId(rateDao.getUserIdFromUI(ui)));
	                    
	                int j= rateDao.getItemIdFromUI(ui); 
	                /**
	                 * rating x user x item x context
	                 */
	                double rujc = me.get();
	                
	            
	                int ctx = me.column(); // context
	                
	                //System.out.println(rateDao.getContextConditionId(ctx));
	                
	                String user = rateDao.getUserId(rateDao.getUserIdFromUI(ui));
	                String context= rateDao.getContextConditionId(ctx);
	    			String item = rateDao.getItemId(rateDao.getItemIdFromUI(ui));
	    			int randomID = 0 + (int) (Math.random() * 1000000000);
	                
	              if(rujc>=3.0) {
	               
	                

	                	
	                	 grafo.addEdge("PC:" + (randomID), new Pair<>("P_" + user, "C_" + context), EdgeType.UNDIRECTED);
	                     grafo.addEdge("CL:" + (randomID), new Pair<>("C_" + context, "L_" +item), EdgeType.UNDIRECTED);
	              
	                	
	                
	              }
	      
	                
	            
	                }

	            
	    	//ranker = new PageRank(grafo, 0.2);
	      	 //ranker.evaluate();
	        
	    }
	                     
	    
	
	/**
	 * Evaluate your model by overriding the method predict(int u, int j, boolean bounded) (for rating prediction) 
	 * or ranking(int u, int j) (for item recommendation), if necessary.
	 * 
	 * The task of the Predict method is to use the trained model to make predictions.
	 *	For example, for rating prediction algorithms, the predict method is used to predict each rating in the test set. 
	 *	Particularly, for a given userIndex and itemIndex, using the model to predict the rating score of userIndex-itemIndex pair.
	 */

	
        

	
	      
	    @Override
	   protected double predict(int u, int item) throws Exception {
		   if(grafo.containsVertex("L_"+rateDao.getItemId(rateDao.getItemIdFromUI(item)))) {
		   //System.out.println("For user: "+ rateDao.getUserId(rateDao.getUserIdFromUI(u))+" ranking item "+ rateDao.getItemId(rateDao.getItemIdFromUI(item)));
	        
		   return (Double) ranker.getVertexScore("L_"+rateDao.getItemId(rateDao.getItemIdFromUI(item)));
		   }
		   else {
			   //System.out.println("For user" +rateDao.getUserId(rateDao.getUserIdFromUI(u))+ " item "+ rateDao.getItemId(rateDao.getItemIdFromUI(item))+" not in graph" );
		        
			   return 0.0;
		   }
	    }
	
	    
		public static void runPageRank() {

			System.out.println("Running pagerank");
			
			ranker = new PageRank(grafo, 0.3);
			ranker.evaluate();
		}
}
