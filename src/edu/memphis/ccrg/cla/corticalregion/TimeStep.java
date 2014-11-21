package edu.memphis.ccrg.cla.corticalregion;

import edu.memphis.ccrg.cla.corticalregion.tasks.FullCorticalRegionTask;


/**
 * Provides a way to mark something as pertaining to the current
 * processing cycle or the previous one.
 * 
 * @see FullCorticalRegionTask
 */
public enum TimeStep{
	
	/**
	 * Signifies the current processing cycle.
	 */
	Current, 
	/**
	 * Signifies the previous processing cycle.
	 */
	Previous
}