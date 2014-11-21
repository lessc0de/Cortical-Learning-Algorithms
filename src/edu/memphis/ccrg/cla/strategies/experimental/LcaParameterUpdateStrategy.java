package edu.memphis.ccrg.cla.strategies.experimental;

import edu.memphis.ccrg.cla.strategies.ParameterUpdateStrategy;
import edu.memphis.ccrg.lida.framework.strategies.StrategyImpl;

public class LcaParameterUpdateStrategy extends StrategyImpl implements
		ParameterUpdateStrategy {
	
//	private static final double lcaApproximationCurveChange = 3.0;

	@Override
	public double getUpdate(double currentValue, Object... params) {
//		if (inhibitionRadius >= lcaApproximationCurveChange) {
//			localColumnActivity = 0.9697408423296855 * inhibitionRadius - 1.4764993321899789;
//		} else { // quadratic fit was better for small inhibition radii
//			localColumnActivity = -0.468979 * Math.pow(inhibitionRadius, 2) + 2.19293 * inhibitionRadius - 0.786611;
//		}
//		localColumnActivity = 4.46529 - 7.57543 * (double) inputError.cardinality() / inputError.size();// regression
//		localColumnActivity = 4.8 - 4.0 * (double) inputError.cardinality() / inputError.size();
//		logger.log(Level.INFO, "{1}: inhibitionRadius: {2} localColumnActivity updated: {3}", new Object[] { TaskManager.getCurrentTick(), this,
//				inhibitionRadius, localColumnActivity });
		return currentValue;
	}
	
}
