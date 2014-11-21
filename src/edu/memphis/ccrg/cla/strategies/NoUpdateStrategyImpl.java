package edu.memphis.ccrg.cla.strategies;

import edu.memphis.ccrg.lida.framework.strategies.StrategyImpl;

public class NoUpdateStrategyImpl extends StrategyImpl implements ParameterUpdateStrategy {

	@Override
	public double getUpdate(double currentValue, Object... params) {
		return currentValue;
	}

}
