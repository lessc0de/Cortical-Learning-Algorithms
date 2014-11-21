package edu.memphis.ccrg.cla.corticalregion;

import edu.memphis.ccrg.cla.corticalregion.cells.Cell;
import edu.memphis.ccrg.cla.corticalregion.columns.Column;
import edu.memphis.ccrg.lida.framework.FrameworkModule;

/**
 * A CorticalRegion contains a collection of {@link Column} objects and corresponding {@link Cell} objects.
 * @author Ryan J. McCall
 */
public interface CorticalRegion extends FrameworkModule {
	
	public void performSpatialPooling();
	public void performSpatialLearning();

	public void performTemporalPooling();
	public void performTemporalLearning();

	public void processStructuralPrediction();
	public Object getGeneratedStructuralPrediction();
	
	/**
	 * Gets the number of columns in the region.
	 * @return
	 */
	public int getColumnCount();
	
	/**
	 * Gets the number of cells per column throughout the region.
	 * @return
	 */
	public int getCellsPerColumn();
}