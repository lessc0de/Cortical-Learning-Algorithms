package edu.memphis.ccrg.cla.strategies;

import java.util.logging.Level;
import java.util.logging.Logger;

import cern.colt.bitvector.BitVector;
import edu.memphis.ccrg.lida.framework.strategies.StrategyImpl;

public class SpatialPredictiveCodingStrategy extends StrategyImpl implements FilteringStrategy {

	private static final Logger logger = Logger.getLogger(SpatialPredictiveCodingStrategy.class.getCanonicalName());
	private static final double DEFAULT_PREDICTION_THRESHOLD = 0.5;
	private double predictionThreshold;
	
	@Override
	public void init(){
		predictionThreshold = getParam("predictionThreshold",DEFAULT_PREDICTION_THRESHOLD);
	}
	
	@Override
	public BitVector getFilteredVersion(BitVector input, Object... params) {
		int sqrt = (int) Math.sqrt(input.size());
		return get2DResidual(input, sqrt, sqrt, predictionThreshold);
	}
	private BitVector get2DResidual(BitVector original, int height, int width, double threshold){
		if(original.size() != height*width){
			logger.log(Level.SEVERE, "Cannot compute the 2D residual since the input vector's dimensionality do not match specified dimensions.");
			return null;
		}		
		BitVector result = new BitVector(original.size());
		for(int i=0; i<height; i++){
			for(int j=0; j < width; j++){
				int neighbors = 0;
				double nbrSum = 0;
				int currentIndex = i*width+j;
				if(i > 0){
					if(original.get(currentIndex-width)){
						nbrSum++;
					}
					neighbors++;
				}
				if(i < height-1){
					if(original.get(currentIndex+width)){
						nbrSum++;
					}
					neighbors++;
				}				
				if(j > 0){
					neighbors++;
					if(original.get(currentIndex-1)){
						nbrSum++;
					}
					if(i > 0){
						if(original.get(currentIndex-width-1)){
							nbrSum++;
						}
						neighbors++;
					}
					if(i < height-1){
						if(original.get(currentIndex+width-1)){
							nbrSum++;
						}
						neighbors++;
					}
				}		
				if(j < width-1){
					neighbors++;
					if(original.get(currentIndex+1)){
						nbrSum++;
					}
					if(i> 0){
						if(original.get(currentIndex-width+1)){
							nbrSum++;
						}
						neighbors++;
					}
					if(i< height-1){
						if(original.get(currentIndex+width+1)){
							nbrSum++;
						}
						neighbors++;
					}
				}				
				int currentBit = original.get(currentIndex)? 1: 0;
				if(Math.abs(currentBit-nbrSum/neighbors) >= threshold){
					result.put(currentIndex,true);
				}					
			}
		}
		return result;
	}	
}