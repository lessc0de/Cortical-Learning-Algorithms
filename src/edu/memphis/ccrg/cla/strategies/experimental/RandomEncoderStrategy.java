package edu.memphis.ccrg.cla.strategies.experimental;

import java.util.Random;

import cern.colt.bitvector.BitVector;
import edu.memphis.ccrg.lida.framework.strategies.StrategyImpl;
//  EncoderStrategy
public class RandomEncoderStrategy extends StrategyImpl {

//	private final int SEED=-500; //  Shouldn't matter how the scrambler scrambles. The algorithm should be robust to this.
	//  only 1 '*'
	/**        
	 * Number of replacements that should be performed
	 */
	private final double DEFAULT_PERCENT_REPLACEMENTS=1;//  also static
	int encoding[][];//  explain your indexing scheme (encodement)
	//  all fields here should be private 
	int noofReplacements; //  'num' is fine
	double percentReplacements;
	int size; //  inputSize
	Random rnd = new Random();
	
	//  non-default constructors are best omitted in the framework
	//The factory will use Class.forName which uses the default. Value that need initialized
	// are handled in the init() method.
	
	public RandomEncoderStrategy(){		
	}
	/**
	 * Initializes the scrambler by generating pairs of positions that will be swapped.  
	 * The number of pairs generated is the DEFAULT_PERCENT_REPLACEMENTS times the size.
	 * 
	 * @param size It is the dimension of the word that will be encoded
	 */
	public RandomEncoderStrategy(int size) {
		percentReplacements=DEFAULT_PERCENT_REPLACEMENTS;
//		init(size);
	}
	
	/**
	 * Initializes the scrambler by generating pairs of positions that will be swapped.  
	 * The number of pairs generated is the prcntRpl times the size.
	 * 
	 * @param size It is the dimension of the word that will be encoded
	 * @param prcntRpl It is the percentage position replacements that will occur in the word of dimension size
	 */
	public RandomEncoderStrategy(int size,double prcntRpl) {
		percentReplacements=prcntRpl;
//		init(size);
	}
	
	@Override
	public void init(){ //  this method is inherited
		//  another parameter which determines how far from source you can choose the target
		percentReplacements = getParam("percentReplacements",DEFAULT_PERCENT_REPLACEMENTS);
		
	}
	
	public void generateEncoding(int s){
		size = s;
		noofReplacements=(int) (size*percentReplacements);
		encoding=new int[noofReplacements][2];
		
		for(int i=0;i<noofReplacements;i++){
			int source=rnd.nextInt(size); 			
			int target=rnd.nextInt(size);
			encoding[i][0]=source;
			encoding[i][1]=target;
		}
	}
	
	/**
	 * Encodes a {@link BitVector BitVector} word and returns the encoded {@link BitVector BitVector}.
	 * It is done by performing percentReplacement times size number of swapping of position values.
	 *  
	 * @param bv BitVector that is to be encoded
	 */
	public BitVector encode(BitVector bv) {
		BitVector copy = bv.copy();
		for (int i = 0; i < noofReplacements; i++) {
			boolean temp = copy.get(encoding[i][1]);
			copy.putQuick(encoding[i][1],copy.get(encoding[i][0]));
			copy.putQuick(encoding[i][0], temp);
		}
		return copy;
	}

	public BitVector decode(BitVector bv) { //  remove params
		BitVector copy=bv.copy();
		for(int i=noofReplacements-1; i>=0; i--){
			boolean temp=copy.get(encoding[i][1]);
			copy.putQuick(encoding[i][1], copy.get(encoding[i][0]));
			copy.putQuick(encoding[i][0], temp);
		}
		return copy;
	}
	//  extract interface
	public int[] decodePrediction(int[] prediction) {		//  remove params
		//  this method should also copy, at least for consistency
		for(int i=noofReplacements-1; i>=0; i--){
			int temp=prediction[encoding[i][1]];
			prediction[encoding[i][1]]= prediction[encoding[i][0]];
			prediction[encoding[i][0]]= temp;
		}
		return prediction;
	}
//	
//	public BitVector[] getLikelyWords(BitVector prediction, Object... params) {
//		
//		return null;
//	}
//
//	public double[] getLikelihood(BitVector Prediction, Object... params) {
//		//   Auto-generated method stub
//		return null;
//	}
}