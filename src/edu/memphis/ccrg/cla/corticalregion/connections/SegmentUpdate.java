package edu.memphis.ccrg.cla.corticalregion.connections;

import java.util.Collection;

/**
 * A potential update to a {@link DendriteSegment} and its {@link Synapse} objects. 
 * If learning is deemed applicable this object is unpacked during the learning algorithm.
 * @author Ryan J. McCall
 */
public interface SegmentUpdate {
	
	/**
	 * Gets the {@link DendriteSegment} to be updated.
	 * @return
	 */
	public DendriteSegment getDendriteSegment();
	
	/**
	 * Sets the {@link DendriteSegment} to possibly be updated.
	 * @param s
	 */
	public void setDendriteSegment(DendriteSegment ds);

	/**
	 * Sets the {@link SegmentUpdateType} of this segment.
	 * @param t the type
	 */
	public void setUpdateType(SegmentUpdateType t);
	public SegmentUpdateType getUpdateType();
	
	/**
	 * Gets the active {@link Synapse} objects to be updated.
	 * @return
	 */
	public Collection<Synapse> getActiveSynapses();
	
	/**
	 * Sets the active {@link Synapse} objects to possibly be updated.
	 * @param synapses
	 */
	public void setActiveSynapses(Collection<Synapse> s);

	/**
	 * Sets the prediction time, that is, the number of time steps in the future that the update's
	 * segment will be predicting for if the update is performed.
	 * @param o number of time steps
	 */
	public void setPredictionTime(int o);
	public int getPredictionOrder();
	
}
