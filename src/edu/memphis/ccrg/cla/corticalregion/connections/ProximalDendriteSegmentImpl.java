package edu.memphis.ccrg.cla.corticalregion.connections;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import cern.colt.bitvector.BitVector;

//Keep this method in mind for traversing the on bits of BitVectors:
//public int indexOfFromTo(int from, int to, boolean state)
public class ProximalDendriteSegmentImpl implements ProximalDendriteSegment{

	private BitVector connectedSynapses;  //a subset of potential
	private Map<Integer, Float> potentialPermanences = new HashMap<Integer, Float>(); 
	
	@Override
	public void setInputSpaceSize(int s){
		connectedSynapses = new BitVector(s); 
	}

	@Override
	public void addConnectedSynapse(int pos) {
		connectedSynapses.put(pos, true);
	}
	@Override
	public void removeConnectedSynapse(int pos) {
		connectedSynapses.put(pos, false);
	}
	@Override
	public BitVector getConnectedSynapses() {
		return connectedSynapses;
	}
	@Override
	public int getConnectedSynapseCount() {
		return connectedSynapses.cardinality();
	}
	
	@Override
	public void addPotentialSynapse(int pos, float p) {
		potentialPermanences.put(pos, p);
	}
	@Override
	public void removePotentialSynapse(int pos) {
		potentialPermanences.remove(pos);
	}
	@Override
	public Collection<Integer> getPotentialSynapses() {
		return potentialPermanences.keySet();
	}
	@Override
	public int getPotentialSynapseCount() {
		return potentialPermanences.size();
	}
	@Override
	public Map<Integer, Float> getPotentialPermanences(){
		return potentialPermanences;
	}
}