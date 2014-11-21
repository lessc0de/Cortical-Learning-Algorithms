package edu.memphis.ccrg.cla.corticalregion;


/**
 * A source of input for a {@link CorticalRegion}. {@link CorticalRegion} is a bottom-up source, 
 * but other things can be as well.
 * @author Ryan J. McCall
 */
public interface CorticalRegionBottomUpSource {
	
	/**
	 * Gets the current output of this bottom up source.
	 * @return the source's output
	 */
	public Object getOutputSignal();
	
	/**
	 * Gets the size of the source's output.
	 * @return
	 */
	public int getOutputDimensionality();
	
	/**
	 * Set the received structural (top-down) prediction of the signal.
	 * @param p prediction 
	 */
	public void setReceivedStructuralPrediction(Object p);
}