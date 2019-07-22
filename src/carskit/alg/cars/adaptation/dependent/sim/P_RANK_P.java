package carskit.alg.cars.adaptation.dependent.sim;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.common.base.Function;

import carskit.data.structure.DenseMatrix;
import carskit.data.structure.SparseMatrix;
import carskit.generic.ContextRecommender;
import carskit.generic.IterativeRecommender;
import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.algorithms.scoring.PageRankWithPriors;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.OrderedSparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;
import librec.data.MatrixEntry;
import java.math.*;

public class P_RANK_P extends ContextRecommender {

	static Graph grafo;
	private int nodi_P = 0, nodi_C = 0, nodi_L = 0, nodi_D = 0, archi_PC = 0, archi_CL = 0, archi_LD = 0;
	private static int ratings = 0;
	private int P_map = 0, C_map = 0, L_map = 0, D_map = 0;
	HashMap<String, Double> map;
	String user;
	static PageRankWithPriors ranker;
	static SparseMatrix train;
	List<String> contesti;
	List<String> pref_c;
	static HashMap<Integer, ArrayList<Integer>> user_context = new HashMap<Integer, ArrayList<Integer>> ();
	
	public P_RANK_P(SparseMatrix trainMatrix, SparseMatrix testMatrix, int fold) {
		super(trainMatrix, testMatrix, fold);
		// TODO Auto-generated constructor stub
		train = trainMatrix;
		grafo = new OrderedSparseMultigraph<>();

	}

	/*
	 * The main task of the setup method is to initialize the member variables. For
	 * example, reading arguments from the configuration file. If the algorithm does
	 * not need to set extra configurations, the program does not need to re-write
	 * the methods. It should be noted that, in the rewrite of the setup method,
	 * users need to call the original abstract class in the setup method. That is,
	 * to put super.setup() in the first line of the implementation, to ensure that
	 * the basic parameters of the algorithm is initialized. (non-Javadoc)
	 * 
	 * @see carskit.generic.Recommender#initModel()
	 */

	protected void initModel() throws Exception {
		/**
		 * inizializzando il modello della superclasse vengono inizializzati i parametri
		 * di base
		 */
		super.initModel();
		/**
		 * Impostazione del nome dell'algoritmo
		 */
		this.algoName = "P_RANK_P";
		/**
		 * inizializzazione del grafo simple structure
		 */

		map = new HashMap();
		contesti = new ArrayList<String>();
		pref_c = new ArrayList<String>();

	}

	/**
	 * Per ottenere l'utente rateDao.getUserId(rateDao.getUserIdFromUI(ui)) Per
	 * ottenere l'item rateDao.getItemId(rateDao.getItemIdFromUI(ui)) Per ottenere
	 * il contesto rateDao.getContextConditionId(ctx) Per ottenere il rating singolo
	 * me.get
	 */

	@Override
	protected void buildModel() throws Exception {

		loss = 0;
		for (MatrixEntry me : trainMatrix) {

			// System.out.println(me.get());
			int ui = me.row(); // user-item

			// System.out.println(rateDao.getUserId(rateDao.getUserIdFromUI(ui)));
			int u = rateDao.getUserIdFromUI(ui);

			// System.out.println(rateDao.getItemId(rateDao.getItemIdFromUI(ui)));

			grafo.addVertex("P_" + rateDao.getUserId(rateDao.getUserIdFromUI(ui)));
			// System.out.print("P_"+rateDao.getUserId(rateDao.getUserIdFromUI(ui)));
			


			int j = rateDao.getItemIdFromUI(ui);

			// rating x user x item x context

			double rujc = me.get();

			int ctx = me.column(); // context

			// System.out.println(rateDao.getContextConditionId(ctx));

			String user = rateDao.getUserId(rateDao.getUserIdFromUI(ui));
			String context = rateDao.getContextConditionId(ctx);
			String item = rateDao.getItemId(rateDao.getItemIdFromUI(ui));
			context = rateDao.getContextDimensionId(ctx);

			//System.out.print("User: " + user + " context: " + ctx + " item: " + item + " rate: " + rujc + "\n");

			contesti.add("C_"+ctx);
			int randomID = 0 + (int) (Math.random() * 1000000000);
			//System.out.println("id:" + randomID);

			if(rujc>=4.0) {
			

				grafo.addEdge("PC:" + randomID, new Pair<>("P_" + user, "C_" + ctx), EdgeType.UNDIRECTED);
				

				grafo.addEdge("CL:" + randomID, new Pair<>("C_" + ctx, "L_" + item), EdgeType.UNDIRECTED);

			pref_c.add("C_"+ctx);
			
			if(user_context.containsKey(u)) {
				ArrayList<Integer> contesti_utente_u=user_context.get(u);
				contesti_utente_u.add(ctx);
				user_context.put(u, contesti_utente_u);
			}
			else {
				ArrayList<Integer> contesti_utente_u=new ArrayList<Integer>();
				contesti_utente_u.add(ctx);
				user_context.put(u, contesti_utente_u);
			}
			
			}

		}

		System.out.println(grafo.getEdgeCount());
	}




	public static Function pesaContesti(int c, double weight, double weightOthers) {
		Function f2 = ((Object i) -> {
			if (i.toString().equals("C_"+c) ) {
				//System.out.println("Il contesto "+ rateDao.getContextSituationFromInnerId(c)+ " è stato pesato con "+ weight+"------------------------------");
				return weight;
				}
			else
				//System.out.println("Il contesto "+ rateDao.getContextSituationFromInnerId(Integer.parseInt(i.toString().substring(2)))+ " è stato pesato con "+ weightOthers);
				
				return weightOthers;
		});
		return f2;
	}

	
	
	public static Function pesaItem(int u, int c, double weight, double weightOthers) {

		
				
		Function f3 = ((Object i) -> {			
						for (MatrixEntry me : train) {

							// System.out.println(me.get());
							int ui = me.row(); // user-item

							// System.out.println(rateDao.getUserId(rateDao.getUserIdFromUI(ui)));
							int user = rateDao.getUserIdFromUI(ui);

							// System.out.println(rateDao.getItemId(rateDao.getItemIdFromUI(ui)))
							
							int item = rateDao.getItemIdFromUI(ui);

							int ctx = me.column(); // context
						if (u==user && ctx==c && i.toString().equals("L_"+rateDao.getItemId(item)) ) {
							
							return weight;
					
					}
						}
					

							return weightOthers;
						
					
		
			}
		);
		
		return f3; 
						
	
	}
	
	
	
	public static Function pesaItemContesti(int u, int c, double weightContext, double weightItem, double weightOthers) {

		
		
		Function f4 = ((Object i) -> {			
						for (MatrixEntry me : train) {

							// System.out.println(me.get());
							int ui = me.row(); // user-item

							// System.out.println(rateDao.getUserId(rateDao.getUserIdFromUI(ui)));
							int user = rateDao.getUserIdFromUI(ui);

							// System.out.println(rateDao.getItemId(rateDao.getItemIdFromUI(ui)))
							
							int item = rateDao.getItemIdFromUI(ui);

							int ctx = me.column(); // context
							
							

							if(u==user && ctx==c && i.toString().equals("C_"+ctx)) {
								
								return weightContext;
							}
							
							if (u==user && ctx==c && i.toString().equals("L_"+rateDao.getItemId(item)) ) {
							
								return weightItem;
					
				}
					
						}
						return weightOthers;
			}
		);
		
		return f4; 
						
	
	}
	

	
	public static void runPageRank(int u, int c) {
		double weight_context = 1.0;
		double weight_item=0.7;
		double weight_others = 0.0;
		
		
		System.out.print("Running PageRank for u:"+rateDao.getUserId(rateDao.getUserIdFromUI(u))+" c:"+rateDao.getContextSituationFromInnerId(c)+"\n");
		
	ranker = new PageRankWithPriors(grafo, pesaContesti(c, weight_context, weight_others), 0.3);
	//ranker=new PageRankWithPriors(grafo, pesaItemContesti(u,c, weight_context, weight_item, weight_others), 0.3);
	//ranker=new PageRankWithPriors(grafo, pesaItem(u,c, weight_item, weight_others), 0.3);
		ranker.evaluate();
	}
	

	
	@Override
	
	public double predict(int user, int item, int context) {
		if(grafo.containsVertex("L_" + rateDao.getItemId(rateDao.getItemIdFromUI(item)))) {
		System.out.println("For user" +rateDao.getUserId(rateDao.getUserIdFromUI(user))+ " ranking item "+ rateDao.getItemId(rateDao.getItemIdFromUI(item)) );
		return	(Double) ranker.getVertexScore("L_" + rateDao.getItemId(rateDao.getItemIdFromUI(item)));}
		
		else {
			System.out.println("For user" +rateDao.getUserId(rateDao.getUserIdFromUI(user))+ " item "+ rateDao.getItemId(rateDao.getItemIdFromUI(item))+" not in graph" );
			return	0.0;
			
		}
		
	}

}
