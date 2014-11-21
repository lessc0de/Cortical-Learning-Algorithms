package edu.memphis.ccrg.cla.strategies;

import edu.memphis.ccrg.cla.strategies.experimental.SigmoidDecayRateStrategy;
import edu.memphis.ccrg.lida.framework.strategies.DecayStrategy;
//If used this strategy will need proper JUnit tests.
public class DecayRateTest {
	
	public static void main(String[] args) {
		new DecayRateTest().test();
	}
	
	public void test(){
		double a = 22;
		for(int i = 0; i < 15; i++){
			decayIt(i*0.05,a);
		}
	}
	
	private DecayStrategy ds = new SigmoidDecayRateStrategy();
	
	public void decayIt(double activation, double a){
		int decays = 0;
		double value = activation;
		while(value > 0){
			value = ds.decay(value, 1L, a,0.0);
			decays++;
		}
		System.out.println("Value: " + Math.floor(activation*100)/100 + " Decays: " + decays);
	}
	
	public void decay(double activation, double a){
		double amt = ds.decay(activation, 1L, a,0.0);
		System.out.println("Value: " + activation + " Decay Amount: " + (1-amt));
	}
}