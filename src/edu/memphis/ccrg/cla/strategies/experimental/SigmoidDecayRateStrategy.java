package edu.memphis.ccrg.cla.strategies.experimental;

import java.util.Map;

import edu.memphis.ccrg.lida.framework.initialization.Initializable;
import edu.memphis.ccrg.lida.framework.strategies.DecayStrategy;
import edu.memphis.ccrg.lida.framework.strategies.StrategyImpl;

/**
 * A decay strategy which decreases a value according to a sigmoid decay rate, that is, 
 * the amount decayed is based on the inverse sigmoid of the value. The sigmoid curve can be configured.
 * The decay function used is linear; namely, subtraction.
 * In short:
 * value -= 1-1/(1+Math.exp(-a*(value-c)));
 * 
 * @author Ryan J. McCall
 *
 */
public class SigmoidDecayRateStrategy extends StrategyImpl implements DecayStrategy {
	
	private static final double DEFAULT_A = 1.0;
	private double a = DEFAULT_A;
	
	private static final double DEFAULT_C = 0.0;
	private double c = DEFAULT_C;	
	
	/**
	 * If this method is overridden, this init() must be called first! i.e. super.init();
	 * Will set parameters with the following names:<br/><br/>
     * 
     * <b>a</b>scaling parameter on the input<br/>
     * <b>c</b>translation parameter on the input<br/>
     * If either parameter is not specified its default value will be used.
     * 
     * @see Initializable
	 */
	@Override
	public void init() {
		a = getParam("a", DEFAULT_A);
		c = getParam("c", DEFAULT_C);
	}

	@Override
	public double decay(double value, long ticks, Object... params) {	
		double aa = a;
		double cc = c;
		if(params.length == 2){
			aa = (Double) params[0];
			cc = (Double) params[1];
		}
		return auxDecay(value, ticks, aa, cc);
	}

	@Override
	public double decay(double value, long ticks, Map<String,? extends Object> params) {
		double aa = a;
		double cc = c;
		if(params != null && params.containsKey("a") && params.containsKey("c")){
			aa = (Double) params.get("a");
			cc = (Double) params.get("c");
		}
		return auxDecay(value, ticks, aa, cc);
	}
	
	private double auxDecay(double value, long ticks, double a, double c){
		for(int i = 0; i < ticks; i++){
			value -= 1-1/(1+Math.exp(-a*(value-c)));
		}
		return value;
	}
}