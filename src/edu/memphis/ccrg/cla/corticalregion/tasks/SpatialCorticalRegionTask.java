package edu.memphis.ccrg.cla.corticalregion.tasks;

import edu.memphis.ccrg.cla.corticalregion.CorticalRegionImpl;


/**
 * This task runs the "main loop" or one processing cycle of a
 * {@link CorticalRegionImpl}.
 * 
 * @author Ryan J. McCall
 */
public class SpatialCorticalRegionTask extends FullCorticalRegionTask {
	
	/**
	 * Even if this task's cortical region is not performing temporal pooling
	 * there could still be a superior cortical region sending a structural prediction.
	 * In that case these two method should be called.
	 */
	protected void runTemporalPooler() {
		processingRegion.processStructuralPrediction();
		processingRegion.computeOutput();
	}
}