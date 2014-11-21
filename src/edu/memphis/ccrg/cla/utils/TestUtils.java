package edu.memphis.ccrg.cla.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import cern.colt.bitvector.BitVector;
import edu.memphis.ccrg.cla.corticalregion.CorticalRegionImpl;
import edu.memphis.ccrg.cla.corticalregion.cells.Cell;
import edu.memphis.ccrg.cla.corticalregion.cells.CellImpl;
import edu.memphis.ccrg.cla.corticalregion.columns.Column;
import edu.memphis.ccrg.cla.corticalregion.connections.DendriteSegment;
import edu.memphis.ccrg.cla.strategies.FilteringStrategy;
import edu.memphis.ccrg.cla.strategies.NoiseFilteringStrategy;

public class TestUtils {

	private static final Logger logger = Logger.getLogger(TestUtils.class.getCanonicalName());
	private static final FilteringStrategy filter = new NoiseFilteringStrategy();

	public static double getStdDev(double ave, Integer[] values) {
		if (values == null) {
			throw new IllegalArgumentException("Values cannot be null.");
		}
		double squaredErrorSum = 0.0;
		for (Integer i : values) {
			squaredErrorSum += Math.pow(ave - i, 2.0);
		}
		return Math.sqrt(squaredErrorSum / values.length);
	}

	/**
	 * Gets the standard deviation of specified values assumed to have specified
	 * average. The algorithm calculates the average sum of squared errors of
	 * the values with the average.
	 * 
	 * @param average
	 * @param values
	 * @return
	 */
	public static double getStdDev(double average, Double[] values) {
		if (values == null) {
			throw new IllegalArgumentException("Values cannot be null.");
		}
		if(values.length == 0){
			return 0;
		}
		double squaredErrorSum = 0.0;
		for (Double d : values) {
			if (d != null) {
				squaredErrorSum += Math.pow(average - d, 2.0);
			} else {
				throw new NullPointerException("A value was null.");
			}
		}
		return Math.sqrt(squaredErrorSum / values.length);
	}

	public static double getStdDev(double average, double[] values) {
		if(values.length == 0){
			return 0.0;
		}
		double squaredErrorSum = 0.0;
		for (double d: values) {
			squaredErrorSum += Math.pow(average - d, 2.0);
		}
		return Math.sqrt(squaredErrorSum / values.length);
	}

	public static double getStdDev(double average, int[] values) {
		if(values.length == 0){
			return 0.0;
		}
		double squaredErrorSum = 0.0;
		for (double d: values) {
			squaredErrorSum += Math.pow(average - d, 2.0);
		}
		return Math.sqrt(squaredErrorSum / values.length);
	}

	public static double getAverage(Double[] values) {
		double sum = 0.0;
		for (Double d : values) {
			sum += d;
		}
		return values.length == 0 ? 0.0 : sum / values.length;
	}

	public static double getAverage(double[] values) {
		double sum = 0.0;
		for (double d: values) {
			sum += d;
		}
		return values.length == 0? 0.0: sum/values.length;
	}
	
	public static double getAverage(List<Double> values) {
		double sum = 0.0;
		for (Double d : values) {
			sum += d;
		}
		return values.size() == 0? 0.0: sum/values.size();
	}

	public static double getAverage(int[] values) {
		double sum = 0.0;
		for(double d: values){
			sum += d;
		}
		return values.length == 0? 0: sum/values.length;
	}

	/**
	 * @param seqLength
	 *            sequence length
	 * @param patternDimensionality
	 *            size of the patterns in the sequence
	 * @param inputActivity
	 *            desired ratio of true bits to pattern dimensionality
	 * @param strategy
	 *            a {@link FilteringStrategy} used to generated the patterns
	 * @return a list of {@link BitVector} object
	 */
	public static List<BitVector> generateSequence(int seqLength, int patternDimensionality, 
												   double inputActivity, FilteringStrategy strategy) {
		List<BitVector> sequence = new ArrayList<BitVector>();
		for (int i = 0; i < seqLength; i++) {
			BitVector pattern = strategy.getFilteredVersion(new BitVector(patternDimensionality), inputActivity);
			sequence.add(pattern);
		}
		return sequence;
	}

	/**
	 * Returns a rounded version of the specified double to 3 significant
	 * figures after the decimal.
	 * 
	 * @param d
	 * @return
	 */
	public static double round(double d) {
		return TestUtils.round(d, 3);
	}

	/**
	 * Returns a rounded version of the specified double. Intended for I/O
	 * purpose.
	 * 
	 * @param d
	 *            a double to be rounded
	 * @param places
	 *            number of decimal places to keep
	 * @return rounded double
	 */
	public static double round(double d, int places) {
		double factor = Math.pow(10, places);
		return Math.floor(d * factor) / factor;
	}

	/**
	 * @param input
	 * @param prediction
	 * @param predictionThreshold
	 * @return
	 * @throws IllegalArgumentException
	 *             if the size of the input and prediction are unequal.
	 */
	public static double getDifference(BitVector input, double[] prediction, double predictionThreshold) {
		if (input.size() != prediction.length) {
			logger.log(Level.INFO, 
					"Cannot calculate difference because of unequal sizes, the input has size: {0} and the prediction has size: {1}",
					new Object[] { input.size(), prediction.length });
			throw new IllegalArgumentException();
		}
		double differenceSum = 0.0;
		for (int i = 0; i < prediction.length; i++) {
			if (input.getQuick(i)) {
				if (prediction[i] <= predictionThreshold) { // False negative
					differenceSum++;
				}
			} else {
				if (prediction[i] > predictionThreshold) { // False positive
					differenceSum++;
				}
			}

		}
		return differenceSum / prediction.length;
	}

	public static double getCardinalityRatio(BitVector input, BitVector prediction) {
		if (input.size() != prediction.size()) {
			logger.log(Level.INFO,
					"Cannot calculate cardinality ratio because of unequal sizes, the input has size: {0} and the prediction has size: {1}",
					new Object[] { input.size(), prediction.size() });
			throw new IllegalArgumentException();
		}
		return (double) prediction.cardinality() / input.cardinality();
	}

	public static double getDifference(BitVector b1, BitVector b2) {
		if (b1.size() != b1.size()) {
			logger.log(Level.INFO, "BitVector inputs must have equal size but are: {0} and {1}", 
					new Object[] { b1.size(), b2.size() });
			throw new IllegalArgumentException();
		}
		double differenceSum = 0.0;
		for (int i = 0; i < b1.size(); i++) {
			if (b1.getQuick(i)) {
				if (!b2.getQuick(i)) {
					differenceSum++;
				}
			} else if (b2.getQuick(i)) {
				differenceSum++;
			}
		}
		return b1.size() == 0 ? 0.0 : differenceSum / b1.size();
	}

	public static double getDifference(Collection<Column> cols1, Collection<Column> cols2, int setSize) {
		double differenceSum = 0.0;
		for (Column c: cols1) {
			if (!cols2.contains(c)) {
				differenceSum++;
			}
		}
		for (Column c: cols2) {
			if (!cols1.contains(c)) {
				differenceSum++;
			}
		}
		return setSize==0? 0.0: differenceSum/setSize;
	}
	
	/**
	 * Gets the precision of a predicted set of columns and the actual set of active columns.
	 * Precision is: truePositives / (truePositives + falsePositives), if (truePositives + falsePositives)!=0
	 * and 1.0 otherwise 
	 * @param actual
	 * @param predicted
	 * @return
	 */
	public static double getPrecision(Collection<Column> actual, Collection<Column> predicted) {
		double truePositives = 0;
		int falsePositives = 0;
		for(Column c: predicted){
			if(actual.contains(c)){
				truePositives++;
			}else{
				falsePositives++;
			}
		}
		double denominator = truePositives+falsePositives;
		return denominator==0? 1.0: truePositives/denominator;
	}

	public static double getPrecision(BitVector actual, BitVector prediction) {
		BitVector truePositives = actual.copy();
		truePositives.and(prediction);
		int totalPredictions = prediction.cardinality();
		return totalPredictions==0? 1.0: (double)truePositives.cardinality()/totalPredictions;
	}
	public static double getRecall(BitVector actual, BitVector prediction) {
		BitVector truePositives = actual.copy();
		truePositives.and(prediction);
		int totalActives = actual.cardinality();
		return totalActives==0? 1.0: (double)truePositives.cardinality()/totalActives;
	}

	/**
	 * Gets the recall of a predicted set of columns and the actual set of active columns.
	 * Recall is: truePositives / (truePositives + falseNegatives), if (truePositives + falseNegatives)!=0
	 * and 1.0 otherwise
	 * @param actual
	 * @param predicted
	 * @return
	 */
	public static double getRecall(Collection<Column> actual, Collection<Column> predicted) {
		double truePositives = 0;
		int falseNegatives = 0;
		for(Column c: actual){
			if(predicted.contains(c)){
				truePositives++;
			}else{
				falseNegatives++;
			}
		}
		double denominator = truePositives+falseNegatives;
		return denominator==0? 1.0: truePositives/denominator;
	}
	
	/**
	 * F-beta measure the effectiveness of retrieval (prediction) with respect to a user who attaches
	 * 'beta' times as much importance to recall as precision.
	 * @param beta the relative importance of recall to precision, must be a positive real number
	 * @param truePositives
	 * @param falsePositives
	 * @param falseNegatives
	 * @return f-beta score
	 */
	public static double getFBetaScore(double beta, double truePositives, double falsePositives, double falseNegatives){
		if(beta <= 0){
			throw new IllegalArgumentException("Beta must be a positive real number");
		}
		if(truePositives < 0 || falsePositives < 0 || falseNegatives < 0){
			throw new IllegalArgumentException("Accuracy tallies cannot be negative");
		}
		//precision
		double denominator = truePositives+falsePositives;
		double precision = denominator==0? 1.0: truePositives/denominator;
		//recall
		denominator = truePositives+falseNegatives;
		double recall = denominator==0? 1.0: truePositives/denominator;
		//f-beta
		double betaSquared = beta*beta;
		denominator = betaSquared*precision+recall;
		return denominator==0? 0.0: (betaSquared+1)*precision*recall/denominator;
	}
	
	public static double getFBetaScore(double beta, double precision, double recall){
		double betaSquared = beta*beta;
		double denominator = betaSquared*precision+recall;
		return denominator==0? 0.0: (betaSquared+1)*precision*recall/denominator;
	}
	
	public static double getFBetaScore(double beta, Collection<Column> actualColumns, Collection<Column> predictedColumns) {
		double precision = getPrecision(actualColumns, predictedColumns);
		double recall = getRecall(actualColumns, predictedColumns);
		return getFBetaScore(beta, precision, recall);		
	}
	
	public static double getFBetaScore(double beta, BitVector actual, BitVector prediction) {
		double precision = getPrecision(actual, prediction);
		double recall = getRecall(actual, prediction);
		return getFBetaScore(beta, precision, recall);	
	}

	public static double getAverageSynapsesPerColumn(CorticalRegionImpl region) {
		double s = 0.0;
		Column[][] cols = region.getColumns();
		for(Column[] cArray: cols){
			for(Column c: cArray){
				s += c.getPotentialSynapseCount();
			}
		}
		return s/(cols.length*cols[0].length);
	}

	/**
	 * Returns a BitVector of specified size and cardinalityRatio. The true bits
	 * are uniformly spaced throughout the vector's dimensions.
	 * @param size the size of the generated {@link BitVector}
	 * @param cardinalityRatio the ratio of true bits to vector size
	 * @return a {@link BitVector} with evenly spaced true bits.
	 */
	public static BitVector getUniformPattern(int size, double cardinalityRatio) {
		if(cardinalityRatio < 0 || cardinalityRatio > 1){
			throw new IllegalArgumentException("Cardinality ratio must be in the closed interval, [0,1].");
		}
		BitVector b = new BitVector(size);		
		int cardinality = (int)(size*cardinalityRatio);
		//on average how frequently should a bit be true to achieve desired cardinality
		double trueBitFrequency = (double)size/cardinality; 		
		double frequencyCounter = trueBitFrequency;
		for(int i = 0; i < size; i++){
			frequencyCounter--;
			if(frequencyCounter <= 0.0){
				b.set(i);
				frequencyCounter += trueBitFrequency; //+= ensures remainder is considered
			}
		}
		return b;
	}

	/**
	 * Returns the average of the standard deviations of the various dimension in the specified patterns.
	 * @param patterns a collection of BitVectors
	 * @return
	 * @throws IllegalArgumentException if patterns is null or empty, or if pattern size is not 
	 * uniform across all patterns.
	 */
	public static double[] getAverageDimensionStdDev(List<BitVector> patterns) {
		if(patterns == null || patterns.size() == 0){
			throw new IllegalArgumentException("List was null");
		}
		int patternSize = patterns.get(0).size();
		for(BitVector b: patterns){
			if(b.size() != patternSize){
				throw new IllegalArgumentException("Pattern size is not uniform across patterns.");
			}
		}
		//Compute average usage across each dimension.
		double[] dimensionAverages = new double[patternSize];
		for(BitVector b: patterns){
			for(int i = 0; i < patternSize; i++){
				if(b.getQuick(i)){
					dimensionAverages[i]++;
				}
			}
		}
		int patternCount = patterns.size();
		for(int i = 0; i < patternSize; i++){ 
			dimensionAverages[i] /= patternCount; //Normalize by pattern count
		}
		//Compute std devs
		double[] stdDevs = new double[patternSize];
		for(BitVector b: patterns){
			for(int i = 0; i < patternSize; i++){
				double diff = b.getQuick(i)? 1-dimensionAverages[i]: dimensionAverages[i];
				stdDevs[i] += Math.pow(diff,2.0);
			}
		}
		for(int i = 0; i < patternSize; i++){
			stdDevs[i] = Math.sqrt(stdDevs[i]/patternCount);
		}
		double aveStdDev = getAverage(stdDevs);
		double stdDevStdDev = getStdDev(aveStdDev, stdDevs);
		return new double[]{aveStdDev,stdDevStdDev};
	}
	
	public static double[] getAverageSdrStdDev(List<Collection<Column>> sdrs, Column[][] columns){
		//Compute usage counts for all columns
		double[][] averageUsage = new double[columns.length][columns[0].length];
		for(Collection<Column> sdr: sdrs){
			for(Column c: sdr){
				averageUsage[c.getRegionHeightPosition()][c.getRegionWidthPosition()]++;
			}
		}
		//Normalize the usage count values
		int sdrCount = sdrs.size();
		for(int i=0; i<averageUsage.length; i++){
			for(int j=0; j<averageUsage[0].length; j++){
				averageUsage[i][j] /= sdrCount;
			}
		}
		//Compute the Std Dev in each column's usage
		double[][] stdDevs = new double[columns.length][columns[0].length];
		for(Collection<Column> sdr: sdrs){
			for(int i=0; i<averageUsage.length; i++){
				for(int j=0; j<averageUsage[0].length; j++){
					double diff = sdr.contains(columns[i][j])? 1-averageUsage[i][j]: averageUsage[i][j]; 
					stdDevs[i][j] += Math.pow(diff, 2.0);
				}
			}
		}
		for(int i=0; i<averageUsage.length; i++){
			for(int j=0; j<averageUsage[0].length; j++){
				stdDevs[i][j] = Math.sqrt(stdDevs[i][j]/sdrCount);
			}
		}
		//Summarize the Usage Std Devs into an average with its own Std Dev
		double[] values = get1DArray(stdDevs);
		double average = getAverage(values);
		return new double[]{average, getStdDev(average, values)};
	}
	
	public static double[] getAverageBitVectorStdDev(List<BitVector> vectors) {
		int bitVectorDimensions = vectors.get(0).size();
		int end = bitVectorDimensions-1;
		int vectorCount = vectors.size();
		double[] averageUsage = new double[bitVectorDimensions];
		for(BitVector bv: vectors){
			int i = bv.indexOfFromTo(0,end,true);
			while(i != -1){	
				averageUsage[i]++;
				if(i==end){
					break;
				}else{
					i=bv.indexOfFromTo(i+1,end,true);
				}
			}
		}
		for(int i=0; i<averageUsage.length; i++){
			averageUsage[i] /= vectorCount;
		}
		double[] stdDevs = new double[bitVectorDimensions];
		for(BitVector bv: vectors){
			for(int i=0; i<averageUsage.length; i++){
				double diff = bv.get(i)? 1-averageUsage[i]: averageUsage[i]; 
				stdDevs[i] += Math.pow(diff, 2.0);
			}
		}
		for(int i=0; i<averageUsage.length; i++){
			stdDevs[i] = Math.sqrt(stdDevs[i]/vectorCount);
		}
		double ave = getAverage(stdDevs);
		double stdDev = getStdDev(ave, stdDevs);
		return new double[]{ave,stdDev};
	}

	public static double[] get1DArray(double[][] vals) {
		int rows = vals.length;
		int cols = vals[0].length;
	    double[] result = new double[rows*cols];
	    for (int i=0; i<rows; i++){
	        System.arraycopy(vals[i], 0, result, i*cols, cols);    
	    }
	    return result;
	}

	public static Cell[][][] getCells(int h, int w, int c) {
		Cell[][][] cells = new Cell[h][w][c];
		for(int i = 0; i < h; i++){
			for(int j = 0; j < w; j++){
				for(int k = 0; k < c; k++){
					cells[i][j][k] = new CellImpl();
					cells[i][j][k].setDendriteSegments(new ArrayList<DendriteSegment>());
				}
			}
		}
		return cells;
	}

	public static double getConnectedProximalSynapseRate(Column[][] columns) {
		double connected = 0.0;
		double potential = 0.0;
		for(Column[] ca: columns){
			for(Column c: ca){
				connected += c.getConnectedSynapseCount();
				potential += c.getPotentialSynapseCount();
			}
		}
		return potential==0.0? 0.0: connected/potential;
	}
	
	public static BitVector getRandomVector(int size, double trueRatio) {
		return filter.getFilteredVersion(new BitVector(size),trueRatio);
	}
	
	public static double getHammingDistance(Collection<Column> collOne, Collection<Column> collTwo) {		
		int falseNegatives = 0;
		int falsePositives = 0;
		for(Column c: collOne){
			if(!collTwo.contains(c)){
				falseNegatives++;
			}
		}
		for(Column c: collTwo){
			if(!collOne.contains(c)){
				falsePositives++;
			}
		}
		
		return falseNegatives+falsePositives;
	}

	public static double[] getErrorCounts(Collection<Column> actual, Collection<Column> predicted) {
		double truePositives = 0;
		double falseNegatives = 0;
		double falsePositives = 0;
		for(Column c: actual){
			if(predicted.contains(c)){
				truePositives++;
			}else{
				falseNegatives++;
			}
		}
		for(Column c: predicted){
			if(!actual.contains(c)){
				falsePositives++;
			}
		}
		return new double[]{truePositives,falseNegatives,falsePositives};
	}

}
