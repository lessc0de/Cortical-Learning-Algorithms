package edu.memphis.ccrg.cla.corticalregion;


/**
 * A wrapper for all of the logging statistics recorded in the {@link CorticalRegionImpl}.
 * @author Ryan J. McCall
 */
class CorticalRegionStatistics {
	
	/*
	 * among columns, max boost 
	 */
	double maxBoostInCycle;
	double columnsWithBoostedSynapses;
	/*
	 * Total number of cells correctly predicting bottom up input for their
	 * column.
	 */
	double correctlyPredictingCells;
	/*
	 * Total number of cells correctly predicting bottom up with sufficiently
	 * predicting segment.
	 */
	double activePredictingSegment;
	/*
	 * Total number of cells correctly predicting bottom up with sufficiently
	 * predicting sequence segment.
	 */
	double activePredictingSequenceSegments;
	/*
	 * Total number of cells correctly predicting bottom up with sufficiently
	 * predicting sequence segment and predicted by a sufficient number of
	 * learning cells
	 */
	double learningActivePredictingSequenceSegments;
	/*
	 * Number of learning cells that had to be chosen i.e. no column cell was
	 * sufficiently predicted by other learning cells.
	 */
	double chosenLearningCellCount;
	/*
	 * Number of times that the creation of a sequence SegmentUpdate failed this
	 * cycle.
	 */
	double failedSequenceSegmentUpdates;
	int distalSynapsesAdded;
	int distalSynapsesRemoved;	
	double oneOrderPositiveUpdates;
	double oneOrderNegativeUpdates;
	double activePredictionPositiveUpdates;
	double activePredictionNegativeUpdates;
	double higherOrderPositiveUpdates;
	double higherOrderNegativeUpdates;
	double predictionOrderSum;
	double avgOverThresholdOverlapPercentage;
	double predictedColumnOverlap;
	double laterallyPredictedCells;
	double oneOrderPredictedCells;
	
	/**
	 * Reset all the statistics to their starting values.
	 */
	void clearStatistics() {
		maxBoostInCycle = 1.0;
		columnsWithBoostedSynapses = 0;
		chosenLearningCellCount = 0;
		failedSequenceSegmentUpdates = 0;
		correctlyPredictingCells = 0;
		activePredictingSegment = 0;
		activePredictingSequenceSegments = 0;
		learningActivePredictingSequenceSegments = 0;
		distalSynapsesAdded = 0;
		distalSynapsesRemoved = 0;
		oneOrderPositiveUpdates=0;
		oneOrderNegativeUpdates=0;
		activePredictionPositiveUpdates=0;
		activePredictionNegativeUpdates=0;
		higherOrderPositiveUpdates=0;
		higherOrderNegativeUpdates=0;
		predictionOrderSum = 0;
		avgOverThresholdOverlapPercentage = 0;
		predictedColumnOverlap=0;
		laterallyPredictedCells=0;
		oneOrderPredictedCells=0;
	}
}