package edu.memphis.ccrg.cla.corticalregion.connections;

/**
 * The default implementation of {@link Synapse}.
 * @author Ryan J. McCall
 *
 */
public class SynapseImpl implements Synapse {

	/**
	 * Signifies a position attribute as undefined (has no useful value). 
	 */
	public static final int POSITION_UNDEFINED = -1;
	private static int idCounter = 0;
	private int id = idCounter++;
	private double permanence;
	/*
	 * Position of source in cortical region's height dimension
	 */
	private int sourceHeightPosition = POSITION_UNDEFINED;
	/*
	 * Position of source in cortical region's width dimension
	 */
	private int sourceWidthPosition = POSITION_UNDEFINED;
	/*
	 * Position of source in cortical region's column dimension
	 */
	private int sourceColumnPosition = POSITION_UNDEFINED;	

	@Override
	public double getPermanence() {
		return permanence;
	}
	@Override
	public void setPermanence(double p) {
		permanence = p;
	}
	
	@Override
	public int getSourceHeightPos() {
		return sourceHeightPosition;
	}

	@Override
	public void setSourceHeight(int p) {
		sourceHeightPosition = p;
	}

	@Override
	public int getSourceWidthPos() {
		return sourceWidthPosition;
	}
	
	@Override
	public void setSourceWidth(int p) {
		sourceWidthPosition = p;
	}
	
	@Override
	public int getSourceColumn() {
		return sourceColumnPosition;
	}

	@Override
	public void setSourceColumn(int p) {
		sourceColumnPosition = p;
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof SynapseImpl){
			return id == ((SynapseImpl)o).id;
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		return id;
	}
}