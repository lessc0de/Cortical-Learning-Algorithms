package edu.memphis.ccrg.cla.corticalregion.tests;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import cern.colt.bitvector.BitVector;
import edu.memphis.ccrg.cla.corticalregion.CorticalRegionImpl;
import edu.memphis.ccrg.cla.corticalregion.columns.Column;
import edu.memphis.ccrg.cla.corticalregion.initialization.CorticalRegionDef;
import edu.memphis.ccrg.cla.corticalregion.initialization.CorticalRegionFactory;
import edu.memphis.ccrg.cla.strategies.FilteringStrategy;
import edu.memphis.ccrg.cla.utils.TestUtils;
import edu.memphis.ccrg.lida.framework.initialization.AgentStarter;
import edu.memphis.ccrg.lida.framework.initialization.ConfigUtils;
import edu.memphis.ccrg.lida.framework.initialization.FactoriesDataXmlLoader;
import edu.memphis.ccrg.lida.framework.initialization.GlobalInitializer;
import edu.memphis.ccrg.lida.framework.shared.ElementFactory;

/**
 * Provides basic skeleton to create and run cortical region tests.
 * Individual test should subclass this skeleton.
 * 
 * @author Ryan J. McCall
 */
public class RegionTestSkeleton {
	
	private static final int DEFAULT_SEED = 1984; 
	protected static final boolean IS_LEARNING = true;
	protected static final double PREDICTION_THRESHOLD = 0.0;
	
	protected static CorticalRegionFactory regionfactory = CorticalRegionFactory.getInstance();
	protected static final String defName = "default";
	protected CorticalRegionImpl currentRegion;
	protected Map<String, Object> params = new HashMap<String, Object>();
	protected boolean isLoggingOn;
	protected int inputSignalDimensionality;
	protected double inputProjectionFactor = 1;	
	protected double regionProjectionRatio;
	protected int regionHeight;
	protected int regionWidth;
	protected int cellsPerColumn = 1;
	protected double distalLearningRadius;
	protected double permanenceSigma = 0.02;
	protected double receptiveFieldSigma;
	protected double receptiveFieldFactor;
	protected double receptiveFieldRandomness = 0.05;
	protected int columnHistorySize = 1000;
	protected int dendritesPerCell;
	protected int initialSynapsesPerDistalSegment;
	protected double columnOverlapThreshold=0.01;
	protected double localColumnActivity;	
	protected String boostStrategyName = "defaultBoostStrategy";
	protected String exciteStrategyName = "defaultClaExcite";
	protected String decayStrategyName = "defaultClaDecay";
	protected String lcaUpdateStrategyName = "noUpdateStrategy";
	protected int distalDendriteActivationThreshold = 1;
	protected int segmentLearningThreshold = 1;
	protected double initialDistalSynapsePermanence = 0.1;
	protected int maxNewDistalSynapsesPerUpdate = 10;
	protected int maxDistalSynapsesPerSegment;
	protected double minColumnActivityPercentage=0.01;
	protected double synapseConnectionThreshold=0.5;
	protected FilteringStrategy noiseFilteringStrategy;
	protected double structuralPredictionThreshold;
	protected double inhibitionRadiusChangeTolerance;
	protected double proximalPermanenceIncrement=0.1;
	protected double proximalPermanenceDecrement=-0.1;
	protected double distalPermanenceIncrement=0.1;
	protected double distalPositivePermanenceDecrement=-0.1;
	protected double distalNegativePermanenceDecrement=-0.1;
	protected double predictedColumnOverlap=0.0;
	protected int predictionOrderLimit = 1;
	
	public RegionTestSkeleton() {
		Properties properties = ConfigUtils.loadProperties(AgentStarter.DEFAULT_PROPERTIES_PATH);
		FactoriesDataXmlLoader.loadFactoriesData(properties);
		noiseFilteringStrategy = (FilteringStrategy)ElementFactory.getInstance().getStrategy("noiseFilteringStrategy");
	}

	/**
	 * Should be called after setting up parameters and before running test.
	 * Sets up a single cortical region of the default type: {@link CorticalRegionImpl}.
	 */
	protected void setupSingleRegion() {		
		setupSingleRegion(CorticalRegionImpl.class);
	}	
	protected CorticalRegionImpl setupSingleRegion(Class<?> regionClass){
		initDef();			
		try {
			currentRegion = (CorticalRegionImpl) regionClass.newInstance();
			initRegion(currentRegion);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return currentRegion;
	}
	protected void initRegion(CorticalRegionImpl r){
		r.init(params);
		r.initCorticalRegion(defName,false);
	}

	protected void initDef() {
		regionfactory.setRandom(DEFAULT_SEED);
		GlobalInitializer gi = GlobalInitializer.getInstance(); 
		gi.setAttribute("inputSignalDimensionality",inputSignalDimensionality);	
		CorticalRegionDef def = new CorticalRegionDef(defName);
		params.clear();
		// -- Process --
		params.put("loggingOn",isLoggingOn);		
		params.put("structuralPredictionThreshold", structuralPredictionThreshold);
		// -- Spatial pooler parameters --
		params.put("inputProjectionFactor", inputProjectionFactor);
		params.put("regionProjectionFactor", regionProjectionRatio);
		params.put("proximalPermanenceSigma", permanenceSigma);
		params.put("proximalSynapseSourceSigma", receptiveFieldSigma);
		params.put("proximalSynapsesFactor", receptiveFieldFactor);
		params.put("receptiveFieldRandomness", receptiveFieldRandomness);
		params.put("columnOverlapThreshold", columnOverlapThreshold);
		params.put("localColumnActivity", localColumnActivity);
		params.put("minColumnActivityPercentage", minColumnActivityPercentage);
		params.put("columnHistorySize", columnHistorySize);
		params.put("boostStrategyName", boostStrategyName);
		params.put("inhibitionRadiusChangeTolerance", inhibitionRadiusChangeTolerance);
		// -- Temporal pooler parameters --		
		params.put("cellsPerColumn", cellsPerColumn);
		params.put("dendritesPerCell", dendritesPerCell);
		params.put("initialDistalSynapsesPerSegment", initialSynapsesPerDistalSegment);
		params.put("distalDendriteActivationThreshold", distalDendriteActivationThreshold);
		params.put("initialDistalSynapsePermanence", initialDistalSynapsePermanence);
		params.put("segmentLearningThreshold", segmentLearningThreshold);
		params.put("distalLearningRadius", distalLearningRadius);
		params.put("maxNewDistalSynapsesPerUpdate", maxNewDistalSynapsesPerUpdate);		
		params.put("maxDistalSynapsesPerSegment",maxDistalSynapsesPerSegment);
		// -- Other parameters --
		params.put("exciteStrategyName", exciteStrategyName);
		params.put("decayStrategyName", decayStrategyName);
		params.put("lcaUpdateStrategyName", lcaUpdateStrategyName);
		params.put("synapseConnectionThreshold", synapseConnectionThreshold);
		params.put("proximalPermanenceIncrement",proximalPermanenceIncrement);
		params.put("proximalPermanenceDecrement",proximalPermanenceDecrement);
		params.put("distalPermanenceIncrement",distalPermanenceIncrement);
		params.put("distalPositivePermanenceDecrement", distalPositivePermanenceDecrement);
		params.put("distalNegativePermanenceDecrement", distalNegativePermanenceDecrement);
		params.put("predictedColumnOverlap",predictedColumnOverlap);
		params.put("predictionOrderLimit", predictionOrderLimit);
		def.init(params);
		regionfactory.addCorticalRegionDef(defName, def);
	}
	protected void printSpatialParameters(){
		System.out.println("\n***Spatial Pooler Parameters***");
		System.out.println("Receptive field Sigma=" + receptiveFieldSigma);
		System.out.println("Receptive field scaling=" + receptiveFieldFactor);
		System.out.println("Receptive field randomness=" + receptiveFieldRandomness);
		System.out.println("Region Projection Factor=" + regionProjectionRatio);
		System.out.println("Column Overlap Threshold="+ columnOverlapThreshold);
		System.out.println("Local Column Activity=" + localColumnActivity);
		System.out.println("Min Column Activity %=" + minColumnActivityPercentage);
		System.out.println("Column History Size="+ columnHistorySize);	
		System.out.println("ProximalPermanenceSigma="+ permanenceSigma);	
		System.out.println("InhibitionRadiusChangeTolerance="+ inhibitionRadiusChangeTolerance);
		System.out.println("StructuralPredictionThreshold="+structuralPredictionThreshold);
	}
	protected void printTemporalParameters(){
		System.out.println("\n***Temporal Pooler Parameters***");
		System.out.println("CellsPerColumn="+ cellsPerColumn);
		System.out.println("DendritesPerCell="+ dendritesPerCell);
		System.out.println("InitialDistalSynapsesPerSegment="+ initialSynapsesPerDistalSegment);
		System.out.println("DistalDendriteActivationThreshold="+ distalDendriteActivationThreshold);
		System.out.println("SegmentLearningThreshold="+ segmentLearningThreshold);
		System.out.println("DistalLearningRadius="+ distalLearningRadius);
		System.out.println("MaxNewDistalSynapsesPerUpdate="+ maxNewDistalSynapsesPerUpdate);
		System.out.println("MaxDistalSynapsesPerSegment="+maxDistalSynapsesPerSegment);
		System.out.println("InitialDistalSynapsePermanence="+ initialDistalSynapsePermanence);
	}
	protected void printOtherParameters(){
		System.out.println("\n***Other Parameters***");
		System.out.println("SynapseConnectionThreshold="+ synapseConnectionThreshold);
		System.out.println("ProximalPermanenceIncrement="+proximalPermanenceIncrement);
		System.out.println("ProximalPermanenceDecrement="+proximalPermanenceDecrement);
		System.out.println("DistalPermanenceIncrement="+distalPermanenceIncrement);
		System.out.println("DistalPositivePermanenceDecrement="+ distalPositivePermanenceDecrement);
		System.out.println("DistalNegativePermanenceDecrement="+ distalNegativePermanenceDecrement);
		System.out.println("PredictedColumnOverlap="+predictedColumnOverlap);
		System.out.println("PredictionOrderLimit="+predictionOrderLimit);
		System.out.println();
	}

	/**
	 * Runs specified number of trials where an input of fixed activity
	 * percentage is randomly generated. Then the spatial pooler is run
	 * specified number of developmentPresentations after which the region's
	 * active columns is counted for that trials. Logs the average and std dev
	 * of column activity across all trials.
	 * 
	 * @param inputGenerations
	 * 
	 * @param inputSize
	 * 
	 * @param noiseFilteringStrategy
	 * 
	 * @param inputActivityPercentage
	 * 
	 * @param developmentalPresentations
	 */
	protected double[] runRandomInputTrials(int trials, int inputSize,double inputActivityPercentage,
											int developmentalPresentations, int requiredStability) {
		BitVector input = new BitVector(inputSize);
		double activePercentageSum = 0;
		Double[] activePercentageValues = new Double[trials];
		double inhibitionRadiusSum = 0;
		Double[] inhibitionRadiusValues = new Double[trials];
		double dataScaling = 100.0; //for percentages
		for (int i = 0; i < trials; i++) {//loop for random trials
			setupSingleRegion();
			input.clear();
			input = noiseFilteringStrategy.getFilteredVersion(input, inputActivityPercentage);
			runSpatialPooler(input, developmentalPresentations, requiredStability,true);
			activePercentageValues[i] = dataScaling*currentRegion.getActiveColumns().size()/currentRegion.getColumnCount();
			activePercentageSum += activePercentageValues[i];
			inhibitionRadiusValues[i] = currentRegion.getInhibitionRadius();
			inhibitionRadiusSum += inhibitionRadiusValues[i];
		}
		
		double inputActivity = TestUtils.round(dataScaling * inputActivityPercentage, 1);
		double activityCount = TestUtils.round(inputActivityPercentage* inputSize);
		double aveActivity = activePercentageSum/trials;
		double activityStdDev = TestUtils.getStdDev(aveActivity,activePercentageValues);
		//round these two
		aveActivity = TestUtils.round(aveActivity,2);
		activityStdDev = TestUtils.round(activityStdDev,7);		
		double aveInhRadius = TestUtils.round(inhibitionRadiusSum / trials, 2);
		double aveStdDev = TestUtils.round(TestUtils.getStdDev(
				inhibitionRadiusSum / trials, inhibitionRadiusValues), 7);
		return new double[] {inputActivity, activityCount, aveActivity,
				activityStdDev, aveInhRadius, aveStdDev};
	}
	protected void runSpatialPooler(BitVector input, int cycles, int requiredStability, boolean isLearning) {
		int stableCounter = 0;
		int recordedValue = -1;
		for (int i = 0; i < cycles; i++) {
			currentRegion.setupForCycle(input);
			currentRegion.performSpatialPooling();
			if(isLearning){
				currentRegion.performSpatialLearning();
			}
			int activeColumnCount = currentRegion.getActiveColumns().size();
			if(activeColumnCount==recordedValue){
				if(++stableCounter==requiredStability){
					break;
				}				
			}else{
				recordedValue=activeColumnCount;
				stableCounter=0;
			}
		}
	}
	protected void runCorticalRegion(List<BitVector> inputs, int cycles, boolean isLearning){
		int inputIndex = 0;
		for(int i = 0; i < cycles; i++){
			BitVector b = inputs.get(inputIndex);
			inputIndex = (inputIndex+1)%inputs.size();
			runCorticalRegion(b,isLearning);
		}
	}
	protected void runCorticalRegion(BitVector b, int cycles, boolean isLearning){
		for(int i = 0; i < cycles; i++){
			runCorticalRegion(b,isLearning);
		}
	}
	protected void runCorticalRegion(BitVector b, boolean isLearning) {
		currentRegion.setupForCycle(b);
		currentRegion.performSpatialPooling();
		if(isLearning){
			currentRegion.performSpatialLearning();
		}
		currentRegion.updateActiveCells();
		currentRegion.updatePredictiveCells();
		currentRegion.processStructuralPrediction(); 
		currentRegion.computeOutput();
		if(isLearning){
			currentRegion.performTemporalLearning();
		}
	}

	/**
	 * Creates and returns an array of {@link Column} objects according to specified parameters.
	 * @param cpi
	 * @param pssSigma
	 * @param psFactor
	 * @param inputHeight
	 * @param inputWidth
	 * @return
	 */
	protected Column[][] generateColumns(double cpi, double pssSigma, double psFactor,
										 int inputHeight, int inputWidth) {
		regionProjectionRatio = cpi;
		receptiveFieldSigma = pssSigma;
		receptiveFieldFactor = psFactor;
		initDef();
		return regionfactory.getColumns(defName,inputHeight,inputWidth);
	}	
	
	protected Collection<Column> getSdr(BitVector bv, int cycles, int stableCycles) {
		runSpatialPooler(bv,cycles,stableCycles,true);
		return currentRegion.getActiveColumns();
	}
	protected BitVector getInput(int dimensionality, double activity) {
		return noiseFilteringStrategy.getFilteredVersion(new BitVector(dimensionality),activity);
	}
}