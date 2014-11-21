package edu.memphis.ccrg.cla.strategies.experimental;

import java.util.Map;

import edu.memphis.ccrg.lida.framework.strategies.ExciteStrategy;
import edu.memphis.ccrg.lida.framework.strategies.StrategyImpl;

public class ApproxSigmoidExciteStrategy extends StrategyImpl implements ExciteStrategy {

	private double connectionThreshold;
	private static final double DEFAULT_CONNECTED_FACTOR = 10e3;
	private double connectedFactor = DEFAULT_CONNECTED_FACTOR;
	public void setSynapaseConnectionThreshold(double t){
		connectionThreshold = t;
	}
	public double getSynapseConnectionThreshold(){
		return connectionThreshold;
	}
	
	@Override
	public double excite(double current, double excitation, Object... params) {
		double distanceFromThreshold = current - connectionThreshold;		
		if(distanceFromThreshold > 0){
			//if excitation is negative we have to subtract distance
			if(excitation < 0){
				double remainder = excitation*connectedFactor - distanceFromThreshold;
				if(remainder < 0){//todo erro here
					current= connectionThreshold + remainder;//linear decrease
				}else{
					
				}
			}
			
			double update = current + excitation*connectedFactor; 
			if(update < Double.MAX_VALUE){
				current = update;
			}else{
				current = Double.MAX_VALUE;
			}
		}else{
			current+=excitation;
		}
		return current;
	}

	@Override
	public double excite(double current, double excitation,	Map<String, ? extends Object> params) {
		return 0;
	}
}