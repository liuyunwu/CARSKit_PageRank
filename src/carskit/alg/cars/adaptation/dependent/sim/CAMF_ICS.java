package carskit.alg.cars.adaptation.dependent.sim;

import carskit.alg.cars.adaptation.dependent.CAMF;
import carskit.data.setting.Configuration;
import carskit.data.structure.SparseMatrix;
import carskit.generic.ContextRecommender;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import happy.coding.math.Randoms;
import librec.data.DenseMatrix;
import librec.data.DenseVector;
import librec.data.MatrixEntry;
import librec.data.SymmMatrix;

import java.util.ArrayList;
import java.util.List;



/**
 * Yong Zheng, Bamshad Mobasher, Robin Burke. "Similarity-Based Context-aware Recommendation", Proceedings of the 16th International Conference on Web Information System Engineering (WISE), pp. 431-447
 */

public class CAMF_ICS extends CAMF{
	int po=0;

    public CAMF_ICS(SparseMatrix trainMatrix, SparseMatrix testMatrix, int fold) {
        super(trainMatrix, testMatrix, fold);
        this.algoName = "CAMF_ICS";
        // it is an algorithm for top-N recommendations, since the predicted score is used to rank the items. The predicted score is not guaranteed to stay in the original rating scale.
        isRankingPred = true;
        po=0;
    }

    protected void initModel() throws Exception {

        super.initModel();
        if(isRankingPred==false) {
            P.init(1,0.1);
            Q.init(1,0.1);
        }else {
            P.init();
            Q.init();
        }


        ccMatrix_ICS=new SymmMatrix(numConditions);
        for(int i=0;i<numConditions;++i)
            for(int j=0;j<numConditions;++j) 
                ccMatrix_ICS.set(i,j,1.0);
                
    }
/**
 * P matrice righe=utenti e colonne=context condition
 * Q è una matrice righe=item colonne=context condition
 * EmptyContextConditions.get(i) restituisce il context id dei na 
 */
    @Override
    protected double predict(int u, int j, int c) throws Exception {
        double pred=DenseMatrix.rowMult(P, u, Q, j);
      	//System.out.println("--------------------------------------------------------------- "+Q.toString());
        List<Integer> conditions=getConditions(c);
        for(int i=0;i<conditions.size();++i) {
        //	System.out.println(rateDao.getContextConditionId(conditions.get(i)));
            pred=pred*ccMatrix_ICS.get(conditions.get(i), EmptyContextConditions.get(i));
          	//System.out.println(rateDao.getContextConditionId(conditions.get(i))) ;
            }
      	
       // System.out.println(pred +" al turno "+po++);
      	
        return pred;
    }
/**
 *Per ottenere l'id dell'utente bisogna fare  rateDao.getUserId(rateDao.getUserIdFromUI(ui))
 *Per ottenere l'id degli item bisogna fare   rateDao.getItemId(rateDao.getItemIdFromUI(ui))
 *Per ottenere il contesto bisogna fare		rateDao.getContextConditionId(ctx)
 */
    @Override
    protected void buildModel() throws Exception {

        for (int iter = 1; iter <= numIters; iter++) {

            loss = 0;
            for (MatrixEntry me : trainMatrix) {

                int ui = me.row(); // user-item
              	//System.out.println(rateDao.getUserId(rateDao.getUserIdFromUI(ui)));
                int u= rateDao.getUserIdFromUI(ui);
               //System.out.println(rateDao.getItemId(rateDao.getItemIdFromUI(ui)));
                int j= rateDao.getItemIdFromUI(ui);
                int ctx = me.column(); // context
             //   System.out.println(rateDao.getContextConditionId(ctx));
                double rujc = me.get();

                HashBasedTable<Integer, Integer, Double> toBeUpdated = HashBasedTable.create();
                double simc=1.0;
                double dotRating=DenseMatrix.rowMult(P, u, Q, j);
               // System.out.println(DenseMatrix.rowMult(P, u, Q, j));

                double pred=dotRating;
                List<Integer> conditions=getConditions(ctx);
                for(int i=0;i<conditions.size();++i) {
                    int index1=conditions.get(i);
                    int index2=EmptyContextConditions.get(i);
                    double sim=1.0;
                    if(index1!=index2) {
                        sim = ccMatrix_ICS.get(index1, index2);
                        toBeUpdated.put(index1,index2,sim);
                        simc*=sim;
                    }
                    loss += regC * sim * sim;
                    pred = pred * sim;
                }

                double euj = rujc - pred;

                loss += euj * euj;

                // update similarity values
                if(toBeUpdated.size()>0) {
                    for (int index1 : toBeUpdated.rowKeySet())
                        for (int index2 : toBeUpdated.row(index1).keySet()) {

                            double update = toBeUpdated.get(index1, index2);
                            update += lRate * (euj * dotRating * simc / update - regC * update);
                            ccMatrix_ICS.set(index1, index2, update);
                        }
                }

                // update user and item vectors

                for (int f = 0; f < numFactors; f++) {
                    double puf = P.get(u, f);
                    double qjf = Q.get(j, f);

                    double delta_u = euj * qjf * simc- regU * puf;
                    double delta_j = euj * puf * simc - regI * qjf;

                    P.add(u, f, lRate * delta_u);
                    Q.add(j, f, lRate * delta_j);

                    loss += regU * puf * puf + regI * qjf * qjf;
                }

            }
            loss *= 0.5;

            if (isConverged(iter))
                 break;

        }// end of training

    }
}
