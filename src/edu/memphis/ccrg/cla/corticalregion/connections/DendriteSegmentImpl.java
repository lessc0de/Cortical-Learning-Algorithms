package edu.memphis.ccrg.cla.corticalregion.connections;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.memphis.ccrg.lida.framework.tasks.TaskManager;

/**
 * Default implementation of {@link DendriteSegment}.
 * @author Ryan J. McCall
 */
public class DendriteSegmentImpl implements DendriteSegment {
	
	private static final Logger logger = Logger.getLogger(DendriteSegmentImpl.class.getCanonicalName());
	public static final int FIRST_ORDER_PREDICTION_TIME = 1;
	private Set<Synapse> potentialSynapses = Collections.newSetFromMap(new ConcurrentHashMap<Synapse,Boolean>());
	private Set<Synapse> connectedSynapses = Collections.newSetFromMap(new ConcurrentHashMap<Synapse,Boolean>()); 
	
	/*
	 * The number of ticks in the future 
	 * that this segment's sink is expected to become active from feed-forward activity. 
	 * Number of ticks is relative to the segment's activation and is not a specific time
	 */
	private int predictionTime = FIRST_ORDER_PREDICTION_TIME;
	
	@Override
	public void addPotentialSynapse(Synapse s) {
		potentialSynapses.add(s);
	}

	@Override
	public void addPotentialSynapses(Collection<Synapse> potential) {
		for(Synapse s: potential){
			addPotentialSynapse(s);
		}		
	}
	
	@Override
	public void removePotentialSynapse(Synapse s) {
		potentialSynapses.remove(s);
		connectedSynapses.remove(s);
	}
	
	@Override
	public Collection<Synapse> getPotentialSynapses() {
		return potentialSynapses;
	}
	
	@Override
	public void addConnectedSynapse(Synapse s) {
		if(potentialSynapses.contains(s)){
			connectedSynapses.add(s);
		}else{
			logger.log(Level.WARNING, "Synapse {1} does not belong to this segment and thus cannot add it as connected",
					new Object[]{TaskManager.getCurrentTick(),s});
		}
	}

	@Override
	public boolean removeConnectedSynapse(Synapse s) {
		return connectedSynapses.remove(s);
	}
	
	@Override
	public Collection<Synapse> getConnectedSynapses() {
		return connectedSynapses;
	}

	@Override
	public int getPredictionTime() {
		return predictionTime;
	}

	@Override
	public void setPredictionTime(int t) {
		predictionTime = t;
	}

	@Override
	public boolean isPredictingForNextStep() {
		return predictionTime == FIRST_ORDER_PREDICTION_TIME;
	}

	@Override
	public int getPotentialSynapseCount() {
		return potentialSynapses.size();
	}

	@Override
	public int getConnectedSynapseCount() {
		return connectedSynapses.size();
	}
}