package edu.memphis.ccrg.cla.corticalregion.columns;

import edu.memphis.ccrg.cla.corticalregion.connections.DendriteSegment;
import edu.memphis.ccrg.cla.corticalregion.connections.DendriteSegmentImpl;

/**
 * The default implementation of {@link Column}. It is a {@link DendriteSegmentImpl}
 * although conceptually it may be conceived of having a proximal {@link DendriteSegmentImpl}.
 * @author Ryan J. McCall
 */
public class ColumnImpl extends DendriteSegmentImpl implements Column, DendriteSegment {

	private static int idCounter = 0;
	private static final double DEFAULT_BOOST = 1.0;
	private static final int DEFAULT_MAX_HISTORY_SIZE = 1;
	
	private int id = idCounter++;
	private int regionWidthPosition;
	private int regionHeightPosition;
	private double inputWidthPosition;
	private double inputHeightPosition;
	private double boost = DEFAULT_BOOST;
	private double boostedOverlap;
	private int overlapHistorySize;
	private int activeHistorySize;
	private int maxHistorySize = DEFAULT_MAX_HISTORY_SIZE;
	private double winningHistorySum;
	private int winningHistoryIndex; 
	private boolean[] winningHistory;	
	private double competingHistorySum;
	private int competingHistoryIndex;
	private boolean[] competingHistory;	

	public ColumnImpl() {
		setHistorySize(maxHistorySize);
	}
	
	/**
	 * Intended for testing only.
	 * @param i region height position
	 * @param j region width position
	 */
	public ColumnImpl(int i, int j) {
		regionHeightPosition = i;
		regionWidthPosition = j;
	}	
	/**
	 * Intended for testing only.
	 * @param s max history size
	 */
	public ColumnImpl(int s){
		setHistorySize(s);
	}

	@Override
	public void setHistorySize(int s) {
		if(s > 0){
			maxHistorySize = s;
			winningHistory = new boolean[maxHistorySize];
			competingHistory = new boolean[maxHistorySize];
		}
	}

	@Override
	public int getRegionWidthPosition() {
		return regionWidthPosition;
	}
	
	@Override
	public int getRegionHeightPosition() {
		return regionHeightPosition;
	}
	
	@Override
	public void setRegionWidthPosition(int p) {
		regionWidthPosition = p;
	}
	
	@Override
	public void setRegionHeightPosition(int p) {
		regionHeightPosition = p;
	}

	@Override
	public double getBoost() {
		return boost;
	}

	@Override
	public void setBoost(double b) {
		boost = b;
	}

	@Override
	public double getBoostedOverlap() {
		return boostedOverlap;
	}
	
	@Override
	public void setBoostedOverlap(double o){
		boostedOverlap = o;
	}

	@Override
	public synchronized void updateActiveHistory(boolean isActive) {
		if(winningHistory[winningHistoryIndex]){//existing value is true
			if(!isActive){//new value is false
				winningHistory[winningHistoryIndex]=isActive;
				winningHistorySum--;
			}
		}else if(isActive){
			winningHistory[winningHistoryIndex]=isActive;
			winningHistorySum++;
		}
		if(activeHistorySize<maxHistorySize){
			activeHistorySize++;
		}
		winningHistoryIndex = (winningHistoryIndex+1)%maxHistorySize;
	}
	@Override
	public double getAverageActivity() {
		return (activeHistorySize==0)?0.0:winningHistorySum/activeHistorySize;
	}
	
	@Override
	public synchronized void updateOverlapHistory(boolean isOverlap) {
		if(competingHistory[competingHistoryIndex]){//existing value is true
			if(!isOverlap){//new value is false
				competingHistory[competingHistoryIndex]=isOverlap;
				competingHistorySum--;
			}
		}else if(isOverlap){
			competingHistory[competingHistoryIndex]=isOverlap;
			competingHistorySum++;
		}
		if(overlapHistorySize<maxHistorySize){
			overlapHistorySize++;
		}
		competingHistoryIndex = (competingHistoryIndex+1)%maxHistorySize;
	}
	@Override
	public double getAverageOverlap() {
		return (overlapHistorySize==0)?0.0:competingHistorySum/overlapHistorySize;
	}		

	@Override
	public void setInputHeightPosition(double p) {
		inputHeightPosition = p;
	}

	@Override
	public double getInputHeightPosition() {
		return inputHeightPosition;
	}

	@Override
	public void setInputWidthPosition(double p) {
		inputWidthPosition = p;
	}

	@Override
	public double getInputWidthPosition() {
		return inputWidthPosition;
	}

	@Override
	public int compareTo(Column c) {
		//Overlap is used to compare. Columns with greater overlap means higher order or priority.
		return (int)Math.signum(c.getBoostedOverlap() - getBoostedOverlap());
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof ColumnImpl){
			return id == ((ColumnImpl)obj).id;
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		return id;
	}
	
	@Override
	public int getId() {
		return id;
	}
}