package edu.memphis.ccrg.cla.corticalregion.cells;

import java.util.Collection;

import edu.memphis.ccrg.cla.corticalregion.connections.DendriteSegment;

public class CellImpl implements Cell {
	
	private Collection<DendriteSegment> segments;	
	private boolean isActiveCurrently;
	private boolean isActivePreviously;
	private boolean isPredictedCurrently;
	private boolean isPredictedPreviously;
	private boolean isLearningCurrently;
	private boolean isLearningPreviously;
	private boolean isStructurallyPredicted;
	
	@Override
	public void advanceTime(){
		isActivePreviously=isActiveCurrently;
		isActiveCurrently=false;
		isPredictedPreviously=isPredictedCurrently;
		isPredictedCurrently=false;
		isLearningPreviously=isLearningCurrently;
		isLearningCurrently=false;
		isStructurallyPredicted=false;
	}
	
	@Override
	public void clearState(){
		isActiveCurrently=false;
		isActivePreviously=false;
		isPredictedCurrently=false;
		isPredictedPreviously=false;
		isLearningCurrently=false;
		isLearningPreviously=false;
		isStructurallyPredicted=false;
	}

	@Override
	public Collection<DendriteSegment> getDendriteSegments() {
		return segments;
	}
	@Override
	public void setDendriteSegments(Collection<DendriteSegment> s) {
		segments = s;
	}
	@Override
	public synchronized void setActiveCurrently(boolean b) {
		isActiveCurrently = b;
	}
	@Override
	public boolean isActiveCurrently() {
		return isActiveCurrently;
	}
	@Override
	public synchronized void setActivePreviously(boolean b) {
		isActivePreviously = b;
	}
	@Override
	public boolean isActivePreviously() {
		return isActivePreviously;
	}
	@Override
	public synchronized void setPredictedCurrently(boolean b) {
		isPredictedCurrently = b;
	}
	@Override
	public boolean isPredictedCurrently() {
		return isPredictedCurrently;
	}
	@Override
	public synchronized void setPredictedPreviously(boolean b) {
		isPredictedPreviously = b;
	}
	@Override
	public boolean isPredictedPreviously() {
		return isPredictedPreviously;
	}
	@Override
	public synchronized void setLearningCurrently(boolean b) {
		isLearningCurrently = b;
	}
	@Override
	public boolean isLearningCurrently() {
		return isLearningCurrently;
	}
	@Override
	public synchronized void setLearningPreviously(boolean b) {
		isLearningPreviously = b;
		
	}
	@Override
	public boolean isLearningPreviously() {
		return isLearningPreviously;
	}
	@Override
	public synchronized void setStructurallyPredicted(boolean b) {
		isStructurallyPredicted = b;
	}
	@Override
	public boolean isStructurallyPredicted() {
		return isStructurallyPredicted;
	}

	@Override
	public int getDendriteSegmentCount() {
		return segments.size();
	}
}