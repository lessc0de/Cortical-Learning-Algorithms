package edu.memphis.ccrg.cla.corticalregion.connections;

import java.util.Collection;

/**
 * A simulated dendrite which has a collection of potential synapses. Of these, some may be connected to the 
 * dendrite, that is, have sufficiently high synaptic permanence.
 * @author Ryan J. McCall
 */
public interface DendriteSegment {
	
	/**
	 * Adds specified synapse as a potential synapse.
	 * @param s
	 */
	public void addPotentialSynapse(Synapse s);
	
	/**
	 * Adds specified synapse as a potential synapses.
	 * @param potential
	 */
	public void addPotentialSynapses(Collection<Synapse> potential);
	
	/**
	 * Removes specified potential synapse.
	 * @param s
	 * @return
	 */
	public void removePotentialSynapse(Synapse s);
	
	/**
	 * Adds specified synapse as a connected synapse.
	 * @param s
	 */
	public void addConnectedSynapse(Synapse s);
	
	/**
	 * Removes specified connected synapse.
	 * @param s
	 * @return
	 */
	public boolean removeConnectedSynapse(Synapse s);
	
	/**
	 * All potential synapses of this dendrite segment either connected or not.
	 * @return all the synapses of the segment
	 */
	public Collection<Synapse> getPotentialSynapses();
	
	/**
	 * The subset of the potential synapses which are connected.
	 * @return the Synapses over the connected threshold
	 */
	public Collection<Synapse> getConnectedSynapses();

	/**
	 * Gets the number of connected synapses.
	 * @return
	 */
	public int getConnectedSynapseCount();
	/**
	 * Gets the number of potential synapses.
	 * @return
	 */
	public int getPotentialSynapseCount();
	
	/**
	 * Gets prediction time, the number of ticks in the future 
	 * that this segment's sink is expected to become active from feed-forward activity. 
	 * Number of ticks is relative to the segment's activation and is not a specific time.
	 * @return number of ticks in the future when the sink is expected to be active
	 */
	public int getPredictionTime();
	
	/**
	 * Sets prediction time, the number of ticks in the future 
	 * that this segment's sink is expected to become active from feed-forward activity. 
	 * Number of ticks is relative to the segment's activation and is not a specific time. 
	 * @param t number of ticks in the future when the sink is expected to be active
	 */
	public void setPredictionTime(int t);
	
	/**
	 * Convenience method which returns whether the {@link DendriteSegment} is 
	 * predicting for the next time step, that is, its prediction time is 1.
	 * @return true if the segment is predicting sink activity on the next time step
	 */
	public boolean isPredictingForNextStep();
}