package edu.memphis.ccrg.cla.corticalregion.connections;

import java.util.Collection;
import java.util.Map;

import cern.colt.bitvector.BitVector;

public interface ProximalDendriteSegment {

	public void setInputSpaceSize(int s);

	public int getPotentialSynapseCount();

	public Collection<Integer> getPotentialSynapses();

	public void removePotentialSynapse(int pos);

	public void addPotentialSynapse(int pos, float p);

	public int getConnectedSynapseCount();

	public BitVector getConnectedSynapses();

	public void removeConnectedSynapse(int pos);

	public void addConnectedSynapse(int pos);

	public Map<Integer, Float> getPotentialPermanences();
}
