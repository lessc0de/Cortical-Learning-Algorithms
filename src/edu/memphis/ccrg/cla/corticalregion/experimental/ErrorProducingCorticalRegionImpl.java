package edu.memphis.ccrg.cla.corticalregion.experimental;

import java.util.ArrayList;
import java.util.List;

import edu.memphis.ccrg.cla.corticalregion.CorticalRegionImpl;
import edu.memphis.ccrg.cla.corticalregion.columns.Column;
import edu.memphis.ccrg.cla.corticalregion.connections.Synapse;

public class ErrorProducingCorticalRegionImpl extends CorticalRegionImpl {

	/**
	 * Prevents laterally predicted columns from participating in the inter-column inhibition.
	 * For the rest of the columns it performs the overlap score computation the same is in {@link CorticalRegionImpl#performSpatialPooling()}   
	 */
	public void performSpatialPooling() {
		List<Column> overlappingColumns = new ArrayList<Column>();
		for (Column[] columnArray : columns) {
			for (Column c: columnArray) {
				double overlapCount = 0;
				double overlapPercentage = 0.0;
				if (!onePreviousPredictedColumns.contains(c)){ //effectively skips predicted columns
					for (Synapse s: c.getConnectedSynapses()) {
						int inputInd = s.getSourceHeightPos()*bottomUpInputSizeSqrt + s.getSourceWidthPos();
						if (bottomUpInput.get(inputInd)) {
							overlapCount++;
						}
					}
					if (c.getPotentialSynapseCount() != 0) {
						overlapPercentage = overlapCount/c.getPotentialSynapseCount();
					}
				}
				double boostedOverlap = 0.0;
				if (overlapPercentage >= columnOverlapThreshold) { //Boosted overlap is 0.0 unless overlap is over threshold
					boostedOverlap = overlapPercentage*c.getBoost();
					overlappingColumns.add(c);
				}
				c.setBoostedOverlap(boostedOverlap);
			}
		}
		if (lcaUpdateStrategy != null) {
			localColumnActivity = lcaUpdateStrategy.getUpdate(localColumnActivity);
		}
		calculateInterColumnInhibition(overlappingColumns);
	}

}
