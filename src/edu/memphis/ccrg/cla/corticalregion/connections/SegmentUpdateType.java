package edu.memphis.ccrg.cla.corticalregion.connections;

/**
 * This enum defines the various kinds of {@link SegmentUpdate} that are possible.
 * @author Ryan J. McCall
 */
public enum SegmentUpdateType {
	
	/**
	 * Reinforcement and learning new synapses for a DendriteSegment that 
	 * could have predicted an active column 1 time step ahead of time.
	 */
	NEW_FIRST_ORDER,
	
	/**
	 * An update to a segment that was predicting at a particular time. 
	 */
	ACTIVE_PREDICTION,
	
	/**
	 * Reinforcement and learning new synapses for a DendriteSegment that 
	 * could have predicted an active column 2 time steps ahead of time.
	 */
	EXTENDING_PREDICTION, 	
	
	/**
	 * The default type of a new {@link SegmentUpdateImpl}.
	 */
	UNSPECIFIED	
}