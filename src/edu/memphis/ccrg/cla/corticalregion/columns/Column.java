package edu.memphis.ccrg.cla.corticalregion.columns;

import edu.memphis.ccrg.cla.corticalregion.cells.Cell;
import edu.memphis.ccrg.cla.corticalregion.connections.DendriteSegment;

/** 
 * A collection of {@link Cell} objects which are activated by a single proximal {@link DendriteSegment}. 
 * It has an overlap which is  
 * the degree to which the proximal dendrite's synapses are active. 
 * Columns have a boost attribute to ensure that they aren't too inactive. Boost is increased if the column has 
 * a history of inactivity (due to inhibition from other columns). Proximal synapses are also boosted
 * if the column hasn't overlapped well with the input in recent history.
 * Extends {@link Comparable} to help implement column inhibition. 
 * 
 * Should override {@link Object#equals(Object)} and {@link Object#hashCode()}.
 * 
 * @author Ryan J. McCall
 */
public interface Column extends Comparable<Column>, DendriteSegment {
	
	/**
	 * Sets boosted overlap.  
	 * @param o How well column's proximal dendrite overlaps with its receptive field
	 */
	public void setBoostedOverlap(double o);
	/**
	 * Gets boosted overlap.
	 * @return How well column's proximal dendrite overlaps with its receptive field
	 */
	public double getBoostedOverlap();
	
	/**
	 * Sets this column's boost value which is used to modulate overlap.
	 * @param b new boost value
	 */
	public void setBoost(double b);
	/**
	 * Gets boost
	 * @return
	 */
	public double getBoost();
	
	/**
	 * Updates the column's active history based on current activity.
	 * @param isActive true if column is currently active after inhibition step
	 */
	public void updateActiveHistory(boolean isActive);	
	/**
	 * Gets column's average activity.
	 * @return Column's average activity over the last n time steps
	 */
	public double getAverageActivity();
	
	/**
	 * Updates this column's overlap history based on current overlap
	 * @param sufficientOverlap true if column currently overlap sufficiently with its receptive field.
	 */
	public void updateOverlapHistory(boolean sufficientOverlap);
	/**
	 * Gets column's average overlap.
	 * @return Column's average overlap over the last n time steps
	 */
	public double getAverageOverlap();
	
	/**
	 * Gets the column's width position within the cortical region.
	 * @return
	 */
	public int getRegionWidthPosition();
	/**
	 * Sets the column's width position within the cortical region.
	 * @param p
	 */
	public void setRegionWidthPosition(int p);
	/**
	 * Gets the column's height position within the cortical region.
	 * @return
	 */
	public int getRegionHeightPosition();
	/**
	 * Sets the column's height position within the cortical region.
	 * @param p
	 */
	public void setRegionHeightPosition(int p); 
	
	/**
	 * Gets the column's id.
	 * @return
	 */
	public int getId();
	/**
	 * Sets the history size of the column.
	 * @param s the length the recent history the column stores
	 */
	public void setHistorySize(int s);
	/**
	 * @param p the inputHeightPosition to set
	 */
	public void setInputHeightPosition(double p);
	/**
	 * @return the inputHeightPosition
	 */
	public double getInputHeightPosition();
	/**
	 * @param p the inputWidthPosition to set
	 */
	public void setInputWidthPosition(double p);
	/**
	 * @return the inputWidthPosition
	 */
	public double getInputWidthPosition();
}
