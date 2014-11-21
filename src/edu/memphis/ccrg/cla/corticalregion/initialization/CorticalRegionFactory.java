package edu.memphis.ccrg.cla.corticalregion.initialization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.memphis.ccrg.cla.corticalregion.cells.Cell;
import edu.memphis.ccrg.cla.corticalregion.cells.CellImpl;
import edu.memphis.ccrg.cla.corticalregion.columns.Column;
import edu.memphis.ccrg.cla.corticalregion.columns.ColumnImpl;
import edu.memphis.ccrg.cla.corticalregion.connections.DendriteSegment;
import edu.memphis.ccrg.cla.corticalregion.connections.DendriteSegmentImpl;
import edu.memphis.ccrg.cla.corticalregion.connections.Synapse;
import edu.memphis.ccrg.cla.corticalregion.connections.SynapseImpl;
import edu.memphis.ccrg.lida.framework.initialization.GlobalInitializer;
import edu.memphis.ccrg.lida.framework.initialization.InitializableImpl;
import edu.memphis.ccrg.lida.framework.tasks.TaskManager;

public class CorticalRegionFactory extends InitializableImpl {

	private static final Logger logger = Logger.getLogger(CorticalRegionFactory.class.getCanonicalName());
	private static final CorticalRegionFactory instance = new CorticalRegionFactory();
	private static final int DEFAULT_RANDOM_TRIES = 1000;
	private Map<String,CorticalRegionDef> corticalRegionDefs = new HashMap<String,CorticalRegionDef>();
	private Random random = new Random();
	
	private CorticalRegionFactory() {		
		Object o = GlobalInitializer.getInstance().getAttribute("randomizationSeed");
		if(o != null && o instanceof Integer){
			setRandom((Integer)o);
		}
	}
	public void setRandom(int s) {
		random = new Random(s);
	}
	public Random getRandom() {
		return random;
	}

	/**
	 * Gets the only instance of this class. (Singleton pattern)
	 * @return a {@link CorticalRegionFactory}
	 */
	public static CorticalRegionFactory getInstance() {
		return instance;
	}
	
	public void addCorticalRegionDef(String name, CorticalRegionDef def){
		corticalRegionDefs.put(name, def);
	}
	public CorticalRegionDef getCorticalRegionDef(String defName) {
		return corticalRegionDefs.get(defName);
	}

	/**
	 * Generates and returns a matrix of Column with specified attributes.
	 * 
	 * @param inputHeight
	 *            height of region input
	 * @param inputWidth
	 *            width of region input
	 * @param synapsePerColumn
	 *            number of synapses on column's proximal dendrite
	 * @param permanenceThreshold
	 *            connection threshold on synapse permanence
	 * @param permanenceSigma
	 *            governs dispersion of randomly generated permanence values
	 * @param synapseSigma
	 *            governs dispersion of the randomly generated input sources or
	 *            proximal synapses
	 * @return a matrix of columns with specified attributes
	 */
	public Column[][] getColumns(String regionType, int inputHeight, int inputWidth) {
		CorticalRegionDef def = corticalRegionDefs.get(regionType);		
		Column[][] columnArray = null;
		if(def != null){
			double columnsPerInput = def.getRegionProjectionFactor();
			int regionHeight=(int) Math.ceil(Math.sqrt(columnsPerInput)*inputHeight);
			int regionWidth=(int) Math.ceil(Math.sqrt(columnsPerInput)*inputWidth);
			columnArray = new Column[regionHeight][regionWidth];
			for (int i = 0; i < regionHeight; i++) {
				for (int j = 0; j < regionWidth; j++) {
					columnArray[i][j] = getColumn(def, i, j, inputHeight, inputWidth, regionHeight, regionWidth);
				}
			}
		}else{
			logger.log(Level.WARNING,"Cortical Region def {1} does not exist in this factory",
					new Object[]{TaskManager.getCurrentTick(),regionType});
		}
		return columnArray;
	}
	
	/**
	 * Gets an estimate of the radius of the receptive field of columns produced by the specified {@link CorticalRegionDef}.
	 * @param regionType
	 * @return an approximate radius of the columns' receptive field
	 */
	public double getEstimatedReceptiveFieldRadius(String regionType){
		CorticalRegionDef def = corticalRegionDefs.get(regionType);		
		double radius = -1;
		if(def != null){
			double proximalSynapseFactor=def.getProximalSynapsesFactor();
			double pssSigma=def.getProximalSynapseSourceSigma();
			double sigmaSquared = Math.pow(pssSigma, 2.0);
			radius = Math.sqrt(2*sigmaSquared*Math.log(proximalSynapseFactor/(Math.PI*sigmaSquared)));
		}
		return radius;
	}

	/**
	 * Generates and returns a single column.
	 * 
	 * @param columnRegionWidth
	 *            column's width position in region
	 * @param columnRegionHeight
	 *            column's height position in region
	 * @param inputHeight
	 *            height of region input
	 * @param inputWidth
	 *            width of region input
	 * @param synapsePerColumn
	 *            number of synapses on column's proximal dendrite
	 * @param permanenceThreshold
	 *            connection threshold on synapse permanence
	 * @param permanenceSigma
	 *            governs dispersion of randomly generated permanence values
	 * @param synapseSigma
	 *            governs dispersion of the randomly generated input sources or
	 *            proximal synapses
	 * @return a new {@link Column} with specified attributes
	 */
	Column getColumn(CorticalRegionDef def, int columnRegionHeight, int columnRegionWidth, 
					 int inputHeight, int inputWidth, int regionHeight, int regionWidth){
		
		ColumnImpl column = new ColumnImpl();
		column.setRegionHeightPosition(columnRegionHeight);
		column.setRegionWidthPosition(columnRegionWidth);
		// column input position is the region position * number of inputs per column
		// factoring in matrix coordinates (starting at 0,0) where the center is in the middle (+ 0.5)
		double heightRatio = (double)inputHeight/regionHeight;
		double widthRatio = (double)inputWidth/regionWidth;
		double columnInputHeight = columnRegionHeight*heightRatio;
		double columnInputWidth = columnRegionWidth*widthRatio;
		column.setInputHeightPosition(columnInputHeight);
		column.setInputWidthPosition(columnInputWidth);
		//def params
		double sourceSigma=def.getProximalSynapseSourceSigma();	
		double proximalSynapseFactor=def.getProximalSynapsesFactor();
		double synapseConnectionThreshold=def.getSynapseConnectionThreshold();
		double permanenceSigma=def.getProximalPermanenceSigma();
		int columnHistorySize=def.getColumnHistorySize();						
		column.setHistorySize(columnHistorySize);		
		double randomSynapseRatio = def.getReceptiveFieldRandomness();
		addProximalSynapses(column,inputHeight,inputWidth,
							proximalSynapseFactor,sourceSigma, randomSynapseRatio,
							synapseConnectionThreshold,permanenceSigma);
		return column;
	}
	
	public void addProximalSynapses(Column c, int heightBound, int widthBound,
							double synapseFactor, double rfSigma, double randomSynapseRatio,  
							double synapseConnectionThreshold, double permanenceSigma){
		double boundingRadius = 2*rfSigma+1; //Based on rfSigma devise an approximate bound on the mass of the PDF
		double heightPosition = c.getInputHeightPosition();
		double widthPosition = c.getInputWidthPosition();
		// Calculate height bounds of the square containing the circle.
		int heightMin = (int)(heightPosition - boundingRadius);
		if (heightMin < 0) {
			heightMin = 0;
		}
		int heightMax = (int)(heightPosition + boundingRadius);
		if (heightMax > heightBound - 1) {
			heightMax = heightBound - 1;
		}
		// Calculate width bounds of the square containing the circle.
		int widthMin = (int)(widthPosition - boundingRadius);
		if (widthMin < 0) {
			widthMin = 0;
		}
		int widthMax = (int)(widthPosition + boundingRadius);
		if (widthMax > widthBound - 1) {
			widthMax = widthBound - 1;
		}
		double boundingRadiusSquared = Math.pow(boundingRadius,2.0);
		double sigmaSquared = Math.pow(rfSigma,2.0);
		double twoSigmaSquared = 2*sigmaSquared;		
		double normalization = synapseFactor/(twoSigmaSquared*Math.PI);
		for (int i = heightMin; i <= heightMax; i++) {
			for (int j = widthMin; j <= widthMax; j++) {
				double distanceSquared = Math.pow(i-heightPosition,2) + Math.pow(j-widthPosition,2);
				if(distanceSquared <= boundingRadiusSquared){
					double synapses = Math.round(Math.exp(-distanceSquared/twoSigmaSquared)*normalization);
					for(int s = 0; s < synapses; s++){
						Synapse syn = getProximalSynapse(i,j,synapseConnectionThreshold,permanenceSigma);
						c.addPotentialSynapse(syn);
						if (syn.getPermanence() >= synapseConnectionThreshold) {
							c.addConnectedSynapse(syn);
						}
					}					
				}
			}
		}

		int randomSynapses = (int)Math.ceil(c.getPotentialSynapseCount()*randomSynapseRatio);
		for(int i = 0; i < randomSynapses; i++){
			int h = (int) (c.getInputHeightPosition() + rfSigma*random.nextGaussian());
			if(h < 0 || h >= heightBound){
				continue;
			}			
			int w = (int) (c.getInputWidthPosition() + rfSigma*random.nextGaussian());
			if(w < 0 || w >= widthBound){
				continue;
			}	
			Synapse syn = getProximalSynapse(h,w,synapseConnectionThreshold,permanenceSigma);
			c.addPotentialSynapse(syn);
			if (syn.getPermanence() >= synapseConnectionThreshold) {
				c.addConnectedSynapse(syn);
			}
		}
	}
	private Synapse getProximalSynapse(int h, int w, double synapseConnectionThreshold, double permanenceSigma) {
		double p = nextGaussianInRange(synapseConnectionThreshold,permanenceSigma,0.0,1.0,DEFAULT_RANDOM_TRIES);
		return getSynapse(h, w, SynapseImpl.POSITION_UNDEFINED, p);
	}

	/**
	 * Generates and returns a proximal synapse with random initial permanence
	 * and random source position in the region's input.
	 * @param columnInputHeight
	 *            associated column's height position in region
	 * @param columnInputWidth
	 *            associated column's width position in region
	 * 
	 * @return
	 */
	Synapse getProximalSynapse(double columnInputHeight, double columnInputWidth, int inputHeight, int inputWidth,
							   double synapseConnectionThreshold, double permanenceSigma, double sourceSigma) {
		Synapse s = new SynapseImpl();
		double p = nextGaussianInRange(synapseConnectionThreshold,permanenceSigma,0.0,1.0,DEFAULT_RANDOM_TRIES);
		s.setPermanence(p);
		double randomHeightInput = nextRoundedGaussianInRange(columnInputHeight,sourceSigma,0,inputHeight-1,DEFAULT_RANDOM_TRIES);
		double randomWidthInput = nextRoundedGaussianInRange(columnInputWidth,sourceSigma,0,inputWidth-1,DEFAULT_RANDOM_TRIES);
	//	int randomHeightInput = (int)Math.round(nextGaussianInRange(columnInputHeight,sourceSigma,0,inputHeight-1,DEFAULT_RANDOM_TRIES));
	//	int randomWidthInput = (int)Math.round(nextGaussianInRange(columnInputWidth,sourceSigma,0,inputWidth-1,DEFAULT_RANDOM_TRIES));
		s.setSourceHeight((int) randomHeightInput);
		s.setSourceWidth((int) randomWidthInput);
		return s;
	}
	
	/**
	 * Gets a synapse with specified input source coordinates and permanence.
	 * @param h source cell height position
	 * @param w source cell width position
	 * @param c source cell column position
	 * @param p permanence
	 * @return a new {@link Synapse}
	 */
	public Synapse getSynapse(int h, int w, int c, double p){
		Synapse s = new SynapseImpl();
		s.setSourceHeight(h);
		s.setSourceWidth(w);
		s.setSourceColumn(c);
		s.setPermanence(p);
		return s;
	}

	double nextRoundedGaussianInRange(double mu, double sigma, double minBound, double maxBound, double maxAttempts) {
		int tries = 0;
		double pos = minBound-1;
		while (pos<minBound || pos>maxBound) {
			tries++;
			if (tries < maxAttempts) {
				pos = Math.round(mu+sigma*random.nextGaussian());
			} else {
				pos = Math.round(mu);
				logger.log(Level.WARNING,
							"Could not get random input position. Center {1}, input size {2}, sigma {3}.",
							new Object[]{TaskManager.getCurrentTick(),mu,maxBound,sigma});
				break;
			}
		}
		return pos;
	}

	/**
	 * Returns a random value normally distributed. 
	 * @return a random value
	 */
	double nextGaussianInRange(double mu, double sigma, double minBound, double maxBound, double maxAttempts) {
		int attempts = 0;
		double nextValue = minBound-1;
		while (nextValue < minBound || nextValue > maxBound) {
			if (attempts < maxAttempts) {
				nextValue = mu + sigma*random.nextGaussian();			
			}else{
				nextValue = mu;
				logger.log(Level.WARNING,
						   "Could not generate a value from a Gaussian with mean: {1} and sigma: {2} in range [{3},{4}] after {5} tries.",
							new Object[]{TaskManager.getCurrentTick(),mu,sigma,minBound,maxBound,maxAttempts});
				break;
			}
			attempts++;
		}
		return nextValue;
	}

	/**
	 * Generates and returns a 3D array of {@link Collection}s of
	 * {@link DendriteSegment}.
	 * 
	 * @return a new array of "cells"
	 */
	public Cell[][][]  getCells(String regionType, int regionHeight, int regionWidth) {
		CorticalRegionDef def = corticalRegionDefs.get(regionType);		
		int cellsPerCol=def.getCellsPerColumn();		
		int dendritesPerCell=def.getDendritesPerCell();
		int initSynapsesPerDistSegment=def.getIntialSynapsesPerDistalSegment();
		double distalLearningRadius=def.getDistalLearningRadius(); 
		double initialDistalSynapsePermanence = def.getInitialDistalSynapsePermanence();
		Cell[][][] regionCells = new Cell[regionHeight][regionWidth][cellsPerCol];
		for (int i = 0; i < regionHeight; i++) {
			for (int j = 0; j < regionWidth; j++) {
				for (int k = 0; k < cellsPerCol; k++) {
					regionCells[i][j][k] = getCell(i, j, regionHeight, regionWidth, cellsPerCol,
												dendritesPerCell,initSynapsesPerDistSegment,
												distalLearningRadius,initialDistalSynapsePermanence);
				}
			}
		}
		return regionCells;
	}

	/**
	 * Returns a new Cell with specified number of new dendrite segments.
	 * 
	 * @param cellHeight
	 *            height position of cell
	 * @param cellWidth
	 *            width position of cell
	 * @return collection of new segments
	 */
	Cell getCell(int cellHeight, int cellWidth, int regionHeight, int regionWidth, int columnSize,
										int dendritesPerCell, int intialSynapsesPerDistalSegment, double distalLearningRadius, 
										double initialDistalSynapsePermanence) {
		Cell cell = new CellImpl();
		Collection<DendriteSegment> segments = new ArrayList<DendriteSegment>();
		for (int i = 0; i < dendritesPerCell; i++) {
			DendriteSegment ds = new DendriteSegmentImpl();
			Collection<Synapse> potential = getDistalSynapses(intialSynapsesPerDistalSegment, 
					cellHeight, cellWidth, regionHeight, regionWidth, columnSize, distalLearningRadius,initialDistalSynapsePermanence);
			ds.addPotentialSynapses(potential);
			segments.add(ds);
		}
		cell.setDendriteSegments(segments);
		return cell;
	}
	
	/**
	 * Gets specified number of distal synapses each with randomized input coordinates.
	 * The input source coordinates are chosen from a Gaussian distribution about
	 *  the coordinate (centerHeight,centerWidth).
	 * The column dimension is simply chosen randomly.
	 * @param count
	 * @param centerHeight
	 * @param centerWidth
	 * @param regionHeight
	 * @param regionWidth
	 * @param columnSize
	 * @param radius
	 * @param perm
	 * @return
	 */
	public Collection<Synapse> getDistalSynapses(int count, int centerHeight, int centerWidth,
			int regionHeight, int regionWidth, int columnSize, double radius, double perm){
		Collection<Synapse> newSynapses = new HashSet<Synapse>();
		for (int a = 0; a < count; a++) {
			int heightRandom = (int)nextRoundedGaussianInRange(centerHeight, radius, 0, regionHeight-1, DEFAULT_RANDOM_TRIES);
			int widthRandom  = (int)nextRoundedGaussianInRange(centerWidth, radius, 0, regionWidth-1, DEFAULT_RANDOM_TRIES);
			int colRandom = (int)(Math.random()*columnSize);
			Synapse s = getSynapse(heightRandom,widthRandom,colRandom,perm);
			newSynapses.add(s);
		}
		return newSynapses;
	}
}