package edu.memphis.ccrg.cla.strategies;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;


import cern.colt.bitvector.BitVector;
import edu.memphis.ccrg.cla.strategies.NoiseFilteringStrategy;
import edu.memphis.ccrg.lida.episodicmemory.sdm.BitVectorUtils;

public class DefaultNoiseStrategyTest {
	
	private NoiseFilteringStrategy strategy;
	
	@Before
	public void setup(){
		strategy = new NoiseFilteringStrategy();
	}
		
	@Test
	public void testAddNoise1() {
		double noise = 0.5;
		int size = 100;
		strategy.setNoiseProportion(noise);
		double trials = 1000;
		for(int i = 0; i < trials; i++){
			BitVector input = BitVectorUtils.getRandomVector(size);
			int card = input.cardinality();
			int negCard = size - card;
			int trueBitsToFlip =  (int)(card*noise);
			int falseBitsToFlip = (int)(negCard*noise);	
			int expectedNoisyCard = (card - trueBitsToFlip) + falseBitsToFlip;
			int expectedNoisyNegCard = (negCard - falseBitsToFlip) + trueBitsToFlip;
			BitVector noisy = strategy.getFilteredVersion(input);
			assertEquals(expectedNoisyCard, noisy.cardinality());
			assertEquals(expectedNoisyNegCard, size - noisy.cardinality());
		}
	}
	
	@Test
	public void testAddNoise2() {
		double noise = 0.0;
		int size = 100;
		strategy.setNoiseProportion(noise);
		double trials = 1000;
		for(int i = 0; i < trials; i++){
			BitVector input = BitVectorUtils.getRandomVector(size);
			int card = input.cardinality();
			int negCard = size - card;
			int trueBitsToFlip =  (int)(card*noise);
			int falseBitsToFlip = (int)(negCard*noise);	
			int expectedNoisyCard = (card - trueBitsToFlip) + falseBitsToFlip;
			int expectedNoisyNegCard = (negCard - falseBitsToFlip) + trueBitsToFlip;
			BitVector noisy = strategy.getFilteredVersion(input);
			assertEquals(expectedNoisyCard, noisy.cardinality());
			assertEquals(expectedNoisyNegCard, size - noisy.cardinality());
			//additional constraint 
			assertEquals(card,noisy.cardinality());
			assertEquals(negCard,size - noisy.cardinality());
			assertTrue(input.equals(noisy));
		}
	}
	
	@Test
	public void testAddNoise3() {
		double noise = -0.0332452435;
		int size = 100;
		strategy.setNoiseProportion(noise);
		double trials = 1000;
		noise = strategy.getNoiseProportion();//actual used
		for(int i = 0; i < trials; i++){
			BitVector input = BitVectorUtils.getRandomVector(size);
			int card = input.cardinality();
			int negCard = size - card;
			int trueBitsToFlip =  (int)(card*noise);
			int falseBitsToFlip = (int)(negCard*noise);	
			int expectedNoisyCard = (card - trueBitsToFlip) + falseBitsToFlip;
			int expectedNoisyNegCard = (negCard - falseBitsToFlip) + trueBitsToFlip;
			BitVector noisy = strategy.getFilteredVersion(input);
			assertEquals(expectedNoisyCard, noisy.cardinality());
			assertEquals(expectedNoisyNegCard, size - noisy.cardinality());
		}
	}
	
	@Test
	public void testAddNoise4() {
		int size = 100;
		double noise = 1.0;
		strategy.setNoiseProportion(noise);
		double trials = 1000;
		for(int i = 0; i < trials; i++){
			BitVector input = BitVectorUtils.getRandomVector(size);
			int card = input.cardinality();
			int negCard = size - card;
			int trueBitsToFlip =  (int)(card*noise);
			int falseBitsToFlip = (int)(negCard*noise);	
			int expectedNoisyCard = (card - trueBitsToFlip) + falseBitsToFlip;
			int expectedNoisyNegCard = (negCard - falseBitsToFlip) + trueBitsToFlip;
			BitVector noisy = strategy.getFilteredVersion(input);
			assertEquals(expectedNoisyCard, noisy.cardinality());
			assertEquals(expectedNoisyNegCard, size - noisy.cardinality());
			//additional constraint of not
			assertEquals(negCard,noisy.cardinality());
			assertEquals(card,size-noisy.cardinality());
			input.not();
			assertTrue(input.equals(noisy));
		}
	}
	
}