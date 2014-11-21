package edu.memphis.ccrg.cla.strategies;

import java.util.logging.Level;
import java.util.logging.Logger;

import edu.memphis.ccrg.lida.framework.strategies.StrategyImpl;
import edu.memphis.ccrg.lida.framework.tasks.TaskManager;

/**
 * A function to calculate a boost value.
 * @author Ryan J. McCall
 */
public class DefaultBoostStrategy extends StrategyImpl implements BoostStrategy {

	private static final Logger logger = Logger.getLogger(DefaultBoostStrategy.class.getCanonicalName());
	private static final double DEFAULT_M = 10;
	private double m = DEFAULT_M;
	
	@Override
	public void init() {
		m = getParam("m", DEFAULT_M);
		if(m < 0){
			logger.log(Level.WARNING, "Slope parameter for boost function should be positive.",
					TaskManager.getCurrentTick());
		}
	}

	/* 
	 * If column's average activity is above the minimum then no boost (boost value = 1)
	 * else boost value grows linearly with the difference.
	 * 
	 * Case 1: columnActivity >= minAverageColumnActivity
	 * 		Column has been sufficiently active; no boosting, that is, return a boost value of 1.
	 * Case 2: columnActivity < minAverageColumnActivity
	 * 		Column has NOT been sufficiently active;
	 * 		Increase boost linearly based on the slope parameter 'm'. 
	 * 		(Impl. note: difference will be negative so subtracting it is correct.)
	 */
	@Override
	public double getBoostValue(double averageColumnActivity, double minAverageColumnActivity){
		double difference = averageColumnActivity - minAverageColumnActivity;
		return (difference >= 0)? 1.0: (1.0-difference*m);
	}

	public void setSlope(double s) {
		m = s;
	}
}
