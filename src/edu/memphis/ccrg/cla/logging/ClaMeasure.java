package edu.memphis.ccrg.cla.logging;

public enum ClaMeasure {
	
	/*
	 * Cells
	 */	
	CellsActivePercentage, CellsAllPredictedPercentage, CellsAllLateralPredictedPercentage, Cells1OrderLateralPredictedPercentage, CellsStructurallyPredictedPercentage,
	/*
	 * Correctly predicting cells 
	 */
	CellsTruePositive,CellsTruePositiveActiveSegment,CellsTruePositiveSequenceSegment,
	CellsTruePositiveLearningPredicted,
	/*
	 * Columns
	 */
	_ColumnActivity,ColumnsInhibitionRadius,ColumnsAveOverlapHistory, ColumnsAveActivityHistory,
	ColumnsAveBoost, ColumnsAveBoostedOverlap, ColumnsMaxBoost, ColumnsWithBoostedSynapses, AvgOverThresholdOverlapPercentage,
	PredictedColumnOverlap,
	/* 
	 * Column predictions
	 */
	_Column1OrderPrediction_FScore, _Column1OrderPrediction_Precision,_Column1OrderPrediction_Recall, 		
	/*
	 * DistalSynapses
	 */
	DistalSynapsesPotential, DistalSynapsesConnected, DistalSynapseConnectionRate, 
	DistalSynapsesPotentialPerSegment, DistalSynapsesConnectedPerSegment,
	DistalSynapsesAdded, DistalSynapsesRemoved,
	/*
	 * Learning Cells
	 */
	LearningCellsPredicted, LearningCellsChosen, LearningCellsFailed1StepSegmentSelection, 
	/*
	 * Proximal synapses
	 */
	ProximalSynapseConnectivity,
	/*
	 * Potential Segment updates
	 */
	QueuedUpdates, 
	QueuedUpdates_ActivePrediction, QueuedUpdates_ExtendingPrediction, 
	/*
	 * Performed segment updates
	 */
	PerformedUpdates, 
	PerformedUpdates_New1Order, PerformedUpdates_ActivePrediction, PerformedUpdates_ExtendingPrediction, 
	/*
	 * Number of positive versus negative updates 
	 */
//	PerformedUpdates_New1OrderPositive,PerformedUpdates_New1OrderNegative,
//	PerformedUpdates_ActivePredictionPositive,PerformedUpdates_ActivePredictionNegative,
//	PerformedUpdates_HighOrderPositive,PerformedUpdates_HighOrderNegative,
	_PerformedUpdates_AvePredictionOrder
}
