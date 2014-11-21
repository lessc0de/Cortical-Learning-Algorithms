package edu.memphis.ccrg.cla.corticalregion.connections;

/**
 * The representation of a possible connection between a source  and a {@link DendriteSegment}.
 * The scalar attribute <i>permanence</i> (and the permanence threshold) determines whether the {@link Synapse} is connected.
 * The if a {@link Synapse} is connected and its source becomes active it will "pass activation" to its {@link DendriteSegment}
 * 
 * @author Ryan J. McCall
 */
public interface Synapse {
	
	/**
	 * Sets height position (i) of synapse's input.
	 * @param p source's height position
	 */
	public void setSourceHeight(int p);
	public int getSourceHeightPos();
	/**
	 * Sets width position (j) of synapse's input.
	 * @param p source's width position
	 */
	public void setSourceWidth(int p);
	public int getSourceWidthPos();	
	/**
	 * Sets column position (k) of synapse's input.
	 * @param p source's column position
	 */
	public void setSourceColumn(int p);
	public int getSourceColumn();
	
	/**
	 * Sets permanence.
	 * @param p
	 */
	public void setPermanence(double p);
	public double getPermanence();
}
