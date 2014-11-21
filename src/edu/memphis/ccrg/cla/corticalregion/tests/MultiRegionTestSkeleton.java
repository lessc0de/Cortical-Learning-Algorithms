package edu.memphis.ccrg.cla.corticalregion.tests;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.memphis.ccrg.cla.corticalregion.CorticalRegion;
import edu.memphis.ccrg.cla.corticalregion.CorticalRegionImpl;

public class MultiRegionTestSkeleton extends RegionTestSkeleton {

	private static final Logger logger = Logger.getLogger(RegionTestSkeleton.class.getCanonicalName());

	protected Map<Integer,CorticalRegionImpl> corticalRegionMap = new HashMap<Integer,CorticalRegionImpl>();
	
	/**
	 * Based on the current parameters, creates and appends a new {@link CorticalRegionImpl} to the {@link #corticalRegionMap}.
	 * Should be called after setting up parameters and before running test.
	 * @return the index which can be used to later access the region created by this operation.
	 */
	protected int createRegionLevel(Class<?> regionClass) {		
		int newRegionIndex = corticalRegionMap.size();
		addNewRegion(newRegionIndex, regionClass);
		return newRegionIndex;
	}
	
	/**
	 * Creates the region at specified index. If there is an existing region, it is replaced. 
	 * @param i the index
	 */
	protected void addNewRegion(int i, Class<?> regionClass){
		if(i < 0){
			logger.log(Level.WARNING,"Index: {0} is out of bounds.",i);
		}else{			
			CorticalRegionImpl r = setupSingleRegion(regionClass);
			if(r != null){
				corticalRegionMap.put(i, r);
			}
		}
	}
	
	/**
	 * Sets currentRegion.
	 * @param i the region's index
	 * @see RegionTestSkeleton
	 */
	protected void setCurrentRegion(int i){
		currentRegion = getRegion(i);
	}
	
	protected CorticalRegionImpl getRegion(int i){
		CorticalRegionImpl r = null;
		if(0 <= i && i < corticalRegionMap.size()){
			r = corticalRegionMap.get(i);
		}else{
			logger.log(Level.WARNING,"Index: {0} is out of bounds.",i);
		}
		return r;
	}

	/**
	 * Clears the *state* of all regions.
	 */
	protected void clearRegionsState() {
		for(CorticalRegionImpl r: corticalRegionMap.values()){
			r.clearRegionState();
		}
	}
	
	/**
	 * Removes all {@link CorticalRegion} objects.
	 */
	protected void removeAllRegions(){
		corticalRegionMap.clear();
	}

}