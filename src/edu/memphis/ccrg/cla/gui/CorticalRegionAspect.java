package edu.memphis.ccrg.cla.gui;

import edu.memphis.ccrg.cla.corticalregion.CorticalRegion;

/**
 * An enum that defines the various aspects of the {@link CorticalRegion}, which 
 * can be visualized in a GUI panel. 
 * @author Ryan J. McCall
 */
public enum CorticalRegionAspect {
	
	OverlappingColumns, ActiveColumns, CurrentPredictedColumns, OnePreviousPredictedColumns, 
	AverageColumnActivity, AverageColumnOverlap, ColumnBoost,
	ActiveCurrentlyCells, ActivePreviouslyCells,
	PredictedCurrentlyCells, PredictedPreviouslyCells,
	LearningCurrentlyCells, LearningPreviouslyCells,
	StructurallyPredictedCells

}
