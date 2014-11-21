package edu.memphis.ccrg.cla.corticalregion;

import cern.colt.bitvector.BitVector;
import edu.memphis.ccrg.lida.framework.ModuleListener;

/**
 * A listener of a {@link CorticalRegion}.
 * @author Ryan J. McCall
 *
 */
public interface CorticalRegionListener extends ModuleListener {

	/**
	 * Receive the output of a {@link CorticalRegion}.
	 * @param v A {@link BitVector} representing the region's output.
	 */
	public void receiveRegionOutput(BitVector v);	
}
