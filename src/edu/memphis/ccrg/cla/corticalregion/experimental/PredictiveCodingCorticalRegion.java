package edu.memphis.ccrg.cla.corticalregion.experimental;

import edu.memphis.ccrg.cla.corticalregion.CorticalRegionImpl;
import edu.memphis.ccrg.cla.corticalregion.columns.Column;
import edu.memphis.ccrg.cla.corticalregion.connections.Synapse;

/**
 * @author Ryan J. McCall
 */
public class PredictiveCodingCorticalRegion extends CorticalRegionImpl {
	
	/*
	 * The amount of overlap given to predicted columns in addition to whatever
	 * bottom-up overlap they may receive
	 */
	protected double predictedColumnOverlap;
	private static final double DEFAULT_PREDICTED_COLUMN_OVERLAP = 0.0;
	
	protected void initParameters() {
		super.initParameters();
		predictedColumnOverlap = getParam("predictedColumnOverlap", DEFAULT_PREDICTED_COLUMN_OVERLAP);		
	}
	
	/**
	 * Computes bottom-up overlap, contributing to activity score, based on inputError. 
	 * Also factors in whether the column was predicted when computing the column 
	 * "activity score."
	 */
	@Override
	public void performSpatialPooling() {
		double activityScore = 0;
		double boostedActivity = 0;
		for (Column[] columnArray: columns) {
			for (Column col: columnArray) {				
				boostedActivity = 0;
				if(col.getPotentialSynapseCount()>0){
					activityScore = 0;
					if (onePreviousPredictedColumns.contains(col)) {
						activityScore = predictedColumnOverlap; //Use of predictedColumnOverlap
					}else{
						for (Synapse s: col.getConnectedSynapses()) { //Uses input error
							if (inputError.get(s.getSourceHeightPos()*bottomUpInputSizeSqrt+s.getSourceWidthPos())){ 
								activityScore++;
							}
						}
						activityScore /= col.getPotentialSynapseCount();
					}
					if (activityScore > columnOverlapThreshold) { 
						boostedActivity = activityScore*col.getBoost();
						overlappingColumns.add(col);
					}
				}
				col.setBoostedOverlap(boostedActivity);
			}
		}
		calculateInterColumnInhibition(overlappingColumns);
	}
	
	/**
	 * Goes through all proximal synapses and performs updates based on bottom-up prediction error.
	 * Updates all columns' boost scores.
	 * False negative, strengthen synapse to reduce future prediction error
	 * False positive, should reduce synapse but likely by a lesser amount than the increment.
	 */
	@Override
	public void performSpatialLearning() {
		cycleSpatialSynapticChanges = 0;
		for(Column[] columnArray: columns){ 
			for(Column c: columnArray){ 
				for (Synapse s: c.getPotentialSynapses()) {
					int inputIndex = s.getSourceHeightPos()*bottomUpInputSizeSqrt + s.getSourceWidthPos();					
					if(inputError.get(inputIndex)){  //false negative error
						updatePermanence(s, c, proximalPermanenceIncrement);
						cycleSpatialSynapticChanges++;
					}
					if(generatedStructuralPrediction.get(inputIndex) && !bottomUpInput.get(inputIndex)){
						updatePermanence(s, c, proximalPermanenceDecrement);
						cycleSpatialSynapticChanges++;
					}
				}
				updateColumnBoosting(c);
			}
		}		
	}
	
}