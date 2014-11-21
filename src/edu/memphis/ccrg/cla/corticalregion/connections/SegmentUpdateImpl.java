package edu.memphis.ccrg.cla.corticalregion.connections;

import java.util.Collection;


/**
 * Default implementation of {@link SegmentUpdate}. Assumes cell position is a 3-tuple, and that
 * dendrite segments do not know their associated cell.
 * @author ryanjmccall
 */
public class SegmentUpdateImpl implements SegmentUpdate {

	private SegmentUpdateType type = SegmentUpdateType.UNSPECIFIED;
	private DendriteSegment dendriteSegment;
	private int predictionOrder;	
	private Collection<Synapse> activeSynapses;
	private int cellHeight;
	private int cellWidth;
	private int cellColumn;
	
	public int getCellHeight() {
		return cellHeight;
	}
	public void setCellHeight(int h) {
		cellHeight = h;
	}
	public int getCellWidth() {
		return cellWidth;
	}
	public void setCellWidth(int w) {
		cellWidth = w;
	}
	public int getCellColumn() {
		return cellColumn;
	}
	public void setCellColumn(int c) {
		cellColumn = c;
	}
	public void setCellLocation(int h, int w, int c) {
		cellHeight = h;
		cellWidth = w;
		cellColumn = c;		
	}
	
	@Override
	public DendriteSegment getDendriteSegment() {
		return dendriteSegment;
	}

	@Override
	public void setDendriteSegment(DendriteSegment s) {
		dendriteSegment = s;
	}
	@Override
	public Collection<Synapse> getActiveSynapses() {
		return activeSynapses;
	}

	@Override
	public void setActiveSynapses(Collection<Synapse> s) {
		activeSynapses = s;
	}

	@Override
	public SegmentUpdateType getUpdateType() {
		return type;
	}

	@Override
	public void setUpdateType(SegmentUpdateType t) {
		type = t;
	}

	@Override
	public void setPredictionTime(int o) {
		predictionOrder = o;
	}

	@Override
	public int getPredictionOrder() {
		return predictionOrder;
	}
}