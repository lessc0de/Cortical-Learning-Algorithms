package edu.memphis.ccrg.cla.example.environment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import cern.colt.bitvector.BitVector;
import edu.memphis.ccrg.cla.corticalregion.CorticalRegionBottomUpSource;
import edu.memphis.ccrg.cla.strategies.FilteringStrategy;
import edu.memphis.ccrg.lida.environment.Environment;
import edu.memphis.ccrg.lida.environment.EnvironmentImpl;
import edu.memphis.ccrg.lida.framework.initialization.GlobalInitializer;
import edu.memphis.ccrg.lida.framework.shared.ElementFactory;
import edu.memphis.ccrg.lida.framework.tasks.TaskManager;

/**
 * An {@link Environment} which has at any tick a possibly noisy {@link BitVector} state.
 * Several possible underlying {@link BitVector} patterns may be possible.  
 * 
 * @author Ryan J. McCall
 *
 */
public class BitVectorEnvironment extends EnvironmentImpl implements CorticalRegionBottomUpSource {

	private static final Logger logger = Logger.getLogger(BitVectorEnvironment.class.getCanonicalName());
	/*
	 * BitVector patterns displayed by this environment. 
	 */
	private List<BitVector> patterns = new ArrayList<BitVector>();
	/*
	 * A map storing String representations for each bitvector pattern. 
	 */
	private Map<BitVector, String> patternStringMap = new HashMap<BitVector, String>();	
	/*
	 * Pattern currently being displayed 
	 */
	protected BitVector currentPattern;
	/*
	 * Dimensionality of all patterns displayed by this environment.
	 */
	protected int patternDimensionality;
	protected int patternDimensionalityRoot;
	private boolean isFiltered;
	private FilteringStrategy filteringStrategy;	
	protected BitVector currentStructuralPrediction;
	
	@Override
	public void init(){
		GlobalInitializer gi = GlobalInitializer.getInstance();
		patternDimensionality = (Integer) gi.getAttribute("inputSignalDimensionality");	
		currentPattern = new BitVector(patternDimensionality);
		currentStructuralPrediction = new BitVector(patternDimensionality);
		patternDimensionalityRoot = (int) Math.ceil(Math.sqrt(patternDimensionality));	
		isFiltered=getParam("isFiltered", false);
		if(isFiltered){
			String strategyName = getParam("filteringStrategyName","");
			filteringStrategy = (FilteringStrategy)ElementFactory.getInstance().getStrategy(strategyName);
			if(filteringStrategy == null){
				isFiltered = false;
			}
		}		
	}

	/**
	 * Adds specified {@link BitVector} to the list of possible
	 * patterns.
	 * @param bv
	 */
	public boolean addPattern(BitVector bv) {
		boolean res = false;
		if(bv.size() == patternDimensionality){
			res = patterns.add(bv);
			if(res){
				BitVector actual = bv;
				if(isFiltered){
					actual = filteringStrategy.getFilteredVersion(bv);
				}
				logger.log(Level.INFO, "Pattern will have {0}% true bits",
									100.0*actual.cardinality()/actual.size());
			}
		}else{
			logger.log(Level.WARNING,"Cannot add pattern of size {1}. Requires size of {2}",
					new Object[]{TaskManager.getCurrentTick(),bv.size(),patternDimensionality});
		}
		return res;
	}

	/**
	 * Adds specified pattern with specified label.
	 * @param bv a {@link BitVector} pattern
	 * @param s a {@link String} label.
	 */
	public void addPattern(BitVector bv, String s) {
		logger.log(Level.INFO, "Adding pattern: {0}", s.trim()); 
		if(addPattern(bv)){
			patternStringMap.put(bv,s.trim());
		}
	}
	
	/**
	 * Parses an pattern String into a {@link BitVector} and adds it to the list of 
	 * patterns. String must be of the form "bit,bit,bit;bit,bit,bit", where
	 * ';' specifies individual patterns.
	 * @param s
	 */
	public void parsePattern(String s){
		if(s != null){
			String[] patterns = s.split("\\;");
			for (String pattern: patterns) {
				logger.log(Level.INFO, "Loading pattern: {0}", pattern.trim());
				String[] bits = pattern.trim().split("\\,");
				List<Integer> parsedBits = new ArrayList<Integer>();
				for (String b: bits) {
					try {
						parsedBits.add(Integer.parseInt(b.trim()));
					} catch (NumberFormatException e) {
						logger.log(Level.WARNING, "Could not parse bit: {0}", b);
					}
				}
				BitVector bv = new BitVector(parsedBits.size());
				for (int i = 0; i < parsedBits.size(); i++) {
					// non-zero values mapped to 'true'; 0 to 'false'
					bv.putQuick(i, parsedBits.get(i) != 0); 
				}
				addPattern(bv);
			}
		}
	}

	/**
	 * Sets the current state of the environment to the input pattern stored
	 * at specified index
	 * @param i pattern index
	 */
	public void setCurrentPattern(int i) {
		if(i >= 0 && i < patterns.size()){
			BitVector bv = patterns.get(i);
			synchronized(this){				
				if(isFiltered){
					currentPattern = filteringStrategy.getFilteredVersion(bv);
				}else{
					currentPattern = bv;
				}
			}
			if(logger.isLoggable(Level.FINE)){
				String label = patternStringMap.get(bv);
				double activity = 100.0*currentPattern.cardinality()/currentPattern.size();
				logger.log(Level.FINE, "Displaying: {1} having {2}% true bits",
					new Object[]{TaskManager.getCurrentTick(),label,activity});
			}
		}else{
			logger.log(Level.WARNING, "Specified pattern index {1} is out of bounds.",
					new Object[]{TaskManager.getCurrentTick(),i});
		}
	}
	
	@Override
	public synchronized void setReceivedStructuralPrediction(Object p) {
		currentStructuralPrediction = (BitVector) p;
	}

	public int getPatternCount() {
		return patterns.size();
	}	
	
	@Override
	public Object getState(Map<String, ?> params) {
		return currentPattern;
	}
	

	@Override
	public BitVector getOutputSignal() {
		return (BitVector)getState(null);
	}
	
	@Override
	public int getOutputDimensionality() {
		return patternDimensionality;
	}	
	
	/**
	 * This implementation accepts 1 String parameter. The value can be:
	 * 1. "input" - the current input to the network
	 * 2. "predicted" - the network's prediction of the next input
	 * 3. "height" - the height dimension of the input
	 * 4. "width" - the width dimension of the input
	 */
	@Override
	public Object getModuleContent(Object... params){
		if(params != null && params.length > 0){
			if(params[0] instanceof String){
				String contentType = (String)params[0];
				if("height".equalsIgnoreCase(contentType)){
					return patternDimensionalityRoot;
				}else if("width".equalsIgnoreCase(contentType)){
					return patternDimensionalityRoot;
				}	
			}else if(params[0] instanceof EnvironmentState){
				EnvironmentState contentType = (EnvironmentState) params[0];
				if(contentType == EnvironmentState.inputContent){
					boolean[][] content = new boolean[patternDimensionalityRoot][patternDimensionalityRoot];
					for(int i = 0; i < patternDimensionalityRoot; i++){
						for(int j = 0; j < patternDimensionalityRoot; j++){
							content[i][j] = currentPattern.getQuick(i*patternDimensionalityRoot+j);
						}
					}
					return content;
				}else if(contentType == EnvironmentState.predictionContent){				
					boolean[][] content = new boolean[patternDimensionalityRoot][patternDimensionalityRoot];
					for(int i = 0; i < patternDimensionalityRoot; i++){
						for(int j = 0; j < patternDimensionalityRoot; j++){
							content[i][j] = currentStructuralPrediction.get(i*patternDimensionalityRoot+j);
						}
					}
					return content;
				}
			}
		}
		return null;
	}

	@Override
	public void processAction(Object o) {
	}
	@Override
	public void resetState() {
	}
}