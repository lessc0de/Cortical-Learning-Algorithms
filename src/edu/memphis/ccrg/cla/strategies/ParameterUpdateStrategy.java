package edu.memphis.ccrg.cla.strategies;

import edu.memphis.ccrg.lida.framework.strategies.Strategy;

/**
 * A {@link Strategy} for updating a parameter value.
 * @author Ryan J. McCall
 */
public interface ParameterUpdateStrategy extends Strategy {
	
	/**
	 * Gets an updated value based on the current specified parameter value and optional parameters.
	 * @param currentValue current parameter value
	 * @param params optional parameters for the calculation
	 * @return a new parameter value
	 */
	public double getUpdate(double currentValue, Object... params);

}
