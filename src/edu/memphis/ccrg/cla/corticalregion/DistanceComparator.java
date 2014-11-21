package edu.memphis.ccrg.cla.corticalregion;

import java.util.Comparator;

import edu.memphis.ccrg.cla.corticalregion.columns.Column;

/**
 * Compares two columns according to their distance from the column position of a third column.
 * @author Ryan J. McCall
 */
public class DistanceComparator implements Comparator<Column> {
	
	private int height;
	private int width;

	public void setColumnPosition(int h, int w) {
		height = h;
		width = w;
	}

	@Override
	public int compare(Column c1, Column c2) {
		//Compute the distance squared for both columns c1 and c2.
		double d1 = Math.pow(c1.getRegionHeightPosition()-height,2) + Math.pow(c1.getRegionWidthPosition()-width,2); 
		double d2 = Math.pow(c2.getRegionHeightPosition()-height,2) + Math.pow(c2.getRegionWidthPosition()-width,2);
		return (int)Math.signum(d1-d2);
	}
}