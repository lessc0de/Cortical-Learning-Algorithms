/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.cla.strategies;

import edu.memphis.ccrg.lida.framework.shared.ElementFactory;
import edu.memphis.ccrg.lida.framework.strategies.Strategy;

/**
 * A strategy pattern for updating a boost value.
 *  
 * Implementations should add themselves to {@link ElementFactory} via the
 * factoriesData.xml configuration file.
 * 
 * @author Ryan J. McCall
 */
public interface BoostStrategy extends Strategy{

	/**
	 * Returns a boost value for a Column based on specified parameters.
	 * @param columnAverageActivity the average activity of the Column whose boost value is being calculated
	 * @param minAverageColumnActivity the minimum average activity a Column can have without being boosted 
	 * @return an updated boost value for the Column
	 */
	public double getBoostValue(double columnAverageActivity, double minAverageColumnActivity);

}

