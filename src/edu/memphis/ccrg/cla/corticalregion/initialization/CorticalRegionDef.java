package edu.memphis.ccrg.cla.corticalregion.initialization;

import edu.memphis.ccrg.cla.corticalregion.CorticalRegionImpl;
import edu.memphis.ccrg.cla.corticalregion.connections.DendriteSegment;
import edu.memphis.ccrg.cla.corticalregion.connections.Synapse;
import edu.memphis.ccrg.lida.framework.initialization.InitializableImpl;

/**
 * A partial definition of a {@link CorticalRegionImpl}.
 * @author Ryan J. McCall
 * @see CorticalRegionImpl#init()
 */
public class CorticalRegionDef extends InitializableImpl {
	
	public static final int DEFAULT_DISTAL_DENDRITE_ACTIVATION_THRESHOLD = 15;	
	private static final double DEFAULT_REGION_PROJECTION_FACTOR = 1.0;	
	private static final int DEFAULT_CELLS_PER_COLUMN = 4;
	public static final double DEFAULT_SYNAPSE_CONNECTION_THRESHOLD = 0.5;
	private static final double DEFAULT_PROXIMAL_SYNAPSES_FACTOR = 10;
	public static final double DEFAULT_INITIAL_DISTAL_SYNAPSE_PERMANENCE = 0.1;
	public static final double DEFAULT_DISTAL_LEARNING_RADIUS = 10;
	private static final int DEFAULT_INITIAL_DENDRITES_PER_CELL = 20;
	private static final int DEFAULT_INITIAL_DISTAL_SYNAPSES = 16;
	private static final int DEFAULT_COLUMN_HISTORY_SIZE = 100;	
	private static final double DEFAULT_PROXIMAL_SYNAPSE_SOURCE_SIGMA = 0.9;
	private static final double DEFAULT_PROXIMAL_PERMANENCE_SIGMA = 0.02;
	private static final double DEFAULT_RECEPTIVE_FIELD_RANDOMNESS = 0.0;
	
	/* 
	 * The name by which specific instances are referred to in the CorticalRegionFactory.
	 */
	private String name;
	private double regionProjectionFactor;
	private int cellsPerColumn;
	private double synapseConnectionThreshold;
	private double proximalPermanenceSigma;
	private double proximalSynapseSourceSigma;
	private double proximalSynapsesFactor;
	private int columnHistorySize;
	private int dendritesPerCell;
	private int intialSynapsesPerDistalSegment;
	private double distalLearningRadius;
	private double initialDistalSynapsePermanence;
	private double receptiveFieldRandomness;

	public CorticalRegionDef(){
	}
	public CorticalRegionDef(String n){
		name = n;
	}
	
	/**
	 * The following {@link CorticalRegionDef} parameters may be specified in the agent xml file: <br/>
	 * <b>regionProjectionFactor</b> (double) - number of columns that will be created per input dimension
	 * (including both width and height).<br/>
	 * <b>cellsPerColumn</b> (int) - the number of cells in each column.<br/>	
	 * <b>synapseConnectionThreshold</b> (double) - Amount of permanence needed for a {@link Synapse} to be considerd connected.<br/>
	 * <b>proximalPermanenceSigma</b> (double) - the variance of a Gaussian distribution used to generate random initial permanence values.<br/>	 
	 * <b>proximalSynapseSourceSigma</b> (double) - the variance of a Gaussian distribution used to generate the input position of proximal synapses.<br/>
	 * proximalSynapseThickness
	 * <b>receptiveFieldRandomness</b> (double) - amount of randomly generated proximal synapses per column as a percentage of the number of deterministically generated synapses.<br/> 
	 * 
	 * <b>columnHistorySize</b> (int) - Length of the history kept on column's activity and overlap.<br/>
	 * <h1>Temporal Pooler</h1>
	 * <b>dendritesPerCell</b> (int) - Initial number of distal {@link DendriteSegment}s on each cell.<br/>
	 * <b>distalDendriteActivationThreshold</b> (int) - Number of active distal synapses required to active their distal {@link DendriteSegment}.
	 * <b>initialDistalSynapsesPerSegment</b> (int) - The initial number of distal synapses per segment.<br/> 
	 * <b>distalLearningRadius</b> (double) - Radius used to gather neighboring Cells to be used as the sources of distal synapses.<br/>
	 * <b>initialDistalSynapsePermanence</b> (double) - Initial permanence value for new distal synapses.<br/> 
	 */
	@Override 
	public void init(){
	    regionProjectionFactor = getParam("regionProjectionFactor",CorticalRegionDef.DEFAULT_REGION_PROJECTION_FACTOR);
		cellsPerColumn = getParam("cellsPerColumn",DEFAULT_CELLS_PER_COLUMN);
		synapseConnectionThreshold = getParam("synapseConnectionThreshold",DEFAULT_SYNAPSE_CONNECTION_THRESHOLD);		
		proximalPermanenceSigma = getParam("proximalPermanenceSigma",DEFAULT_PROXIMAL_PERMANENCE_SIGMA);
		proximalSynapseSourceSigma = getParam("proximalSynapseSourceSigma",DEFAULT_PROXIMAL_SYNAPSE_SOURCE_SIGMA);
		proximalSynapsesFactor = getParam("proximalSynapsesFactor",DEFAULT_PROXIMAL_SYNAPSES_FACTOR);
		receptiveFieldRandomness = getParam("receptiveFieldRandomness",DEFAULT_RECEPTIVE_FIELD_RANDOMNESS);
		
		columnHistorySize = getParam("columnHistorySize", DEFAULT_COLUMN_HISTORY_SIZE);
		dendritesPerCell = getParam("dendritesPerCell",DEFAULT_INITIAL_DENDRITES_PER_CELL);
		intialSynapsesPerDistalSegment = getParam("initialDistalSynapsesPerSegment",DEFAULT_INITIAL_DISTAL_SYNAPSES);
		distalLearningRadius = getParam("distalLearningRadius",DEFAULT_DISTAL_LEARNING_RADIUS);
		initialDistalSynapsePermanence = getParam("initialDistalSynapsePermanence",DEFAULT_INITIAL_DISTAL_SYNAPSE_PERMANENCE);
	}

	public void setName(String n){
		name = n;
	}
	
	public String getName(){
		return name;
	}

	public double getRegionProjectionFactor() {
		return regionProjectionFactor;
	}

	public double getSynapseConnectionThreshold() {
		return synapseConnectionThreshold;
	}

	public double getProximalPermanenceSigma() {
		return proximalPermanenceSigma;
	}

	public double getProximalSynapseSourceSigma() {
		return proximalSynapseSourceSigma;
	}

	public double getProximalSynapsesFactor() {
		return proximalSynapsesFactor;
	}
	public void setProximalSynapsesFactor(double s) {
		proximalSynapsesFactor = s;
	}

	public int getColumnHistorySize() {
		return columnHistorySize;
	}

	public int getDendritesPerCell() {
		return dendritesPerCell;
	}

	public int getIntialSynapsesPerDistalSegment() {
		return intialSynapsesPerDistalSegment;
	}

	public double getDistalLearningRadius() {
		return distalLearningRadius;
	}

	public double getInitialDistalSynapsePermanence() {
		return initialDistalSynapsePermanence;
	}

	public int getCellsPerColumn() {
		return cellsPerColumn;
	}

	public void setDistalLearningRadius(double r) {
		distalLearningRadius = r;
	}

	public void setColumnsPerInput(double f) {
		regionProjectionFactor = f;
	}

	public void setCellsPerColumn(int c) {
		cellsPerColumn = c;
	}

	public void setSynapseConnectionThreshold(double t) {
		synapseConnectionThreshold = t;
	}

	public void setProximalPermanenceSigma(double s) {
		proximalPermanenceSigma = s;
	}

	public void setProximalSynapseSourceSigma(double s) {
		proximalSynapseSourceSigma = s;
	}

	public void setColumnHistorySize(int s) {
		columnHistorySize = s;
	}

	public void setDendritesPerCell(int d) {
		dendritesPerCell = d;
	}

	public void setIntialSynapsesPerDistalSegment(int s) {
		intialSynapsesPerDistalSegment = s;
	}

	public void setInitialDistalSynapsePermanence(double p) {
		initialDistalSynapsePermanence = p;
	}
	public void setReceptiveFieldRandomness(double r){
		receptiveFieldRandomness = r;
	}
	public double getReceptiveFieldRandomness(){
		return receptiveFieldRandomness;
	}
	
	@Override
	public String toString(){
		return name;
	}
}