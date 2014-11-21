package edu.memphis.ccrg.cla.utils;


import java.util.ArrayList;
import java.util.List;

import cern.colt.bitvector.BitVector;
import edu.memphis.ccrg.cla.corticalregion.CorticalRegionImpl;
import edu.memphis.ccrg.cla.corticalregion.columns.Column;
import edu.memphis.ccrg.cla.corticalregion.connections.Synapse;

/**
 * Static utilities for the {@link CorticalRegionImpl} class.
 * @author Ryan J. McCall
 */
public class ClaUtils {
	
	/**
	 * Returns a set of parameters the will generate a bivariate Gaussian with effective radius
	 * 'desiredRf'. That is, synapse will be generated at a max of 'desiredRf' from the column's center.
	 * Two values are returned:
	 * In index 0, the proximalSynapseSourceSigma, which is set to 'desiredRf' for this method.
	 * In index 1, the proximalSynapseFactor, which is set to PI sqrt(e)desiredRf^2 
	 * @param desiredRf
	 * @return one possible configuration of the parameters
	 */
	public static double[] getColumnReceptiveFieldParams(double desiredRf){
		return new double[]{desiredRf, Math.PI*Math.sqrt(Math.E)*Math.pow(desiredRf,2.0)};
	}
	
	/**
	 * Returns an array whose index is an input's index and each entry represents 
	 * the number of potential synapses with pre-synaptic connection to that input.
	 * @param regionColumns
	 * @param inputWidth The synapses are assumed to be connected to a square-shaped input space having this side length
	 * @return
	 */
	public static int[] getInputCoverage(Column[][] regionColumns, int inputWidth) {
		int[] inputCoverage = new int[inputWidth*inputWidth];
		for (Column[] columnArray : regionColumns) {
			for (Column column : columnArray) {
				for(Synapse s: column.getPotentialSynapses()){
					inputCoverage[s.getSourceHeightPos()*inputWidth+s.getSourceWidthPos()]++;
				}
			}
		}
		return inputCoverage;
	}
	
	public static BitVector getFalseNegativeError(BitVector state, BitVector prediction){
		BitVector error = state.copy();
		error.andNot(prediction);
		return error;
	}
	public static BitVector getFalsePositiveError(BitVector state, BitVector prediction) {
		BitVector error = prediction.copy();
		error.andNot(state);
		return error;
	}
	
	/**
	 * Gets all Columns within radius r of the specified {@link Column} in the 
	 * array of columns. The result will include the specified column.
	 */
	public static List<Column> getNeighbors(Column c, double r, Column[][] columns) {
		int heightPosition = c.getRegionHeightPosition();
		int widthPosition = c.getRegionWidthPosition();
		// Calculate height bounds of the square containing the circle.
		int heightMin = (int) (heightPosition - r);
		if (heightMin < 0) {
			heightMin = 0;
		}
		int heightMax = (int) (heightPosition + r);
		if (heightMax > columns.length - 1) {
			heightMax = columns.length - 1;
		}
		// Calculate width bounds of the square containing the circle.
		int widthMin = (int) (widthPosition - r);
		if (widthMin < 0) {
			widthMin = 0;
		}
		int widthMax = (int) (widthPosition + r);
		if (widthMax > columns[0].length - 1) {
			widthMax = columns[0].length - 1;
		}
		// Gather all columns within radius of the column's position
		double radiusSquared = Math.pow(r, 2);
		List<Column> neighbors = new ArrayList<Column>();
		for (int i = heightMin; i <= heightMax; i++) {
			for (int j = widthMin; j <= widthMax; j++) {
				double distance = Math.pow(i - heightPosition, 2) +
								  Math.pow(j - widthPosition, 2);
				if (distance <= radiusSquared) {
					neighbors.add(columns[i][j]);
				}
			}
		}
		return neighbors;
	}
}
