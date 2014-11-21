package edu.memphis.ccrg.cla.corticalregion.cells;

import java.util.Collection;

import edu.memphis.ccrg.cla.corticalregion.connections.DendriteSegment;

public interface Cell {

	public Collection<DendriteSegment> getDendriteSegments();
	public void setDendriteSegments(Collection<DendriteSegment> s);
	public int getDendriteSegmentCount();
	
	public void setActiveCurrently(boolean b);
	public boolean isActiveCurrently();
	
	public void setActivePreviously(boolean b);
	public boolean isActivePreviously();
	
	public void setPredictedCurrently(boolean b);
	public boolean isPredictedCurrently();
	
	public void setPredictedPreviously(boolean b);
	public boolean isPredictedPreviously();
	
	public void setLearningCurrently(boolean b);
	public boolean isLearningCurrently();
	
	public void setLearningPreviously(boolean b);
	public boolean isLearningPreviously();

	public void setStructurallyPredicted(boolean b);
	public boolean isStructurallyPredicted();
	
	public void clearState();
	public void advanceTime();
}
