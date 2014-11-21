package edu.memphis.ccrg.cla.strategies;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import cern.colt.bitvector.BitVector;
import edu.memphis.ccrg.lida.framework.strategies.StrategyImpl;
import edu.memphis.ccrg.lida.framework.tasks.TaskManager;

/**
 * A function to return noisy version of input.
 * 
 * @author Ryan J. McCall
 * @author Pulin Agrawal
 */
public class NoiseFilteringStrategy extends StrategyImpl implements FilteringStrategy {

	private static final Logger logger = Logger
			.getLogger(NoiseFilteringStrategy.class.getCanonicalName());
	private static final double DEFAULT_NOISE = 0.5;
	private double noisePercentage = DEFAULT_NOISE;
	private Random random = new Random(); 
	
	@Override
	public void init() {
		double n = getParam("noiseProportion", DEFAULT_NOISE);
		setNoiseProportion(n);
		
		if(containsParameter("seed")){
			int seed = getParam("seed",-1);
			setSeed(seed);
		}
	}
	public void setSeed(int s){
		if(s != -1){
			random = new Random(s);
		}
	}
	
	/**
	 * Creates and returns a copy of specified vector with exactly 
	 * {@link #noisePercentage} * input.size() number of noisy (flipped) bits.
	 * The number of true bits flipped will be approximately proportional to the number of 
	 * false bits flipped.
	 * 
	 * @param input
	 *            a {@link BitVector}
	 * @return noisy copy of the input {@link BitVector}
	 */
	@Override
	public BitVector getFilteredVersion(BitVector input, Object... params) {
		double percentage = noisePercentage;
		if(params.length == 1){
			percentage = (Double)params[0];
		}
		return auxAddNoise(input, percentage);
	}
	private BitVector auxAddNoise(BitVector input, double percentage){
		if(input == null){
			logger.log(Level.WARNING, "Input vector was null.",TaskManager.getCurrentTick());
			return null;
		}
		
		int inputSize = input.size();
		int cardinality = input.cardinality();
		int negCardinality = inputSize - cardinality;
		int trueBitsToFlip =  (int)(cardinality*percentage);
		int falseBitsToFlip = (int)(negCardinality*percentage);	
		BitVector noisy = input.copy();
		if(percentage > 0.5){
			//do the 'not' and then flip the "complement" of the number of bits to flip for each truth value
			//note because of the 'not' we must assign the new true bit flips based on the "complement" of the false ones
			noisy.not();
			int altTrueBitsToFlip = negCardinality - falseBitsToFlip;
			int altFalseBitsToFlip = cardinality - trueBitsToFlip;
			return auxAddNoise(input, noisy, altTrueBitsToFlip, altFalseBitsToFlip);
		}
		
		while (trueBitsToFlip > 0) {
			int index = random.nextInt(input.size());
			if (input.get(index) && noisy.get(index)) {
				noisy.putQuick(index,false);
				trueBitsToFlip--;
			}
		}
		while (falseBitsToFlip > 0) {
			int index = random.nextInt(input.size());
			if (!input.get(index) && !noisy.get(index)) {
				noisy.putQuick(index,true);
				falseBitsToFlip--;
			}
		}
		return noisy;
	}
	
	private BitVector auxAddNoise(BitVector input, BitVector noisy, int trueBits, int falseBits){
		while (trueBits > 0) {
			int index = random.nextInt(noisy.size());
			if (!input.get(index) && noisy.get(index)) {
				noisy.putQuick(index,false);
				trueBits--;
			}
		}
		while (falseBits > 0) {
			int index = random.nextInt(noisy.size());
			if (input.get(index) && !noisy.get(index)) {
				noisy.putQuick(index,true);
				falseBits--;
			}
		}
		return noisy;
	}
	
	public void setNoiseProportion(double n) {
		if (n >= 0.0 && n <= 1.0) {
			noisePercentage = n;
		}else{
			logger.log(Level.WARNING, "Noise must be a positive real number in [0.0,1.0].",
					TaskManager.getCurrentTick());
		}
	}
	
	public double getNoiseProportion() {
		return noisePercentage;
	}
}