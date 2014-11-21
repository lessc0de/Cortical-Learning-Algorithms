package edu.memphis.ccrg.cla.corticalregion.cells;

/**
 * An enumeration of the various possible cell states.
 * @author ryanjmccall
 *
 */
public enum CellState {
	
	/**
	 * Represents the active state of cells at the current processing cycle. 
	 * @see Cell#isActiveCurrently()
	 */
	ActiveCurrent, 
	/**
	 * Represents the active state of cells at the previous processing cycle.
	 * @see Cell#isActivePreviously()
	 */
	ActivePrevious,
	/**
	 * Represents the predicted state of cells at the current processing cycle.
	 * @see Cell#isPredictedCurrently()
	 */
	PredictedCurrent, 
	/**
	 * Represents the predicted state of cells at the previous processing cycle.
	 * @see Cell#isPredictedPreviously()
	 */
	PredictedPrevious,
	/**
	 * Represents the learning state of cells at the current processing cycle.
	 * @see Cell#isLearningCurrently()
	 */
	LearningCurrent,
	/**
	 * Represents the learning state of cells at the previous processing cycle.
	 * @see Cell#isLearningPreviously()
	 */
	LearningPrevious
}