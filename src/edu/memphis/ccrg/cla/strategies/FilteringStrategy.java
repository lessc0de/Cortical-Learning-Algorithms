/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.cla.strategies;

import cern.colt.bitvector.BitVector;
import edu.memphis.ccrg.lida.framework.shared.ElementFactory;
import edu.memphis.ccrg.lida.framework.strategies.Strategy;


/**
 * A strategy pattern which filters a {@link BitVector}.
 *  
 * Implementations should add themselves to {@link ElementFactory} via the
 * factoriesData.xml configuration file.
 * 
 * @author Ryan McCall
 * @author Pulin Agrawal
 */
public interface FilteringStrategy extends Strategy {

	/**
	 * Returns a new version of the BitVector based on specified parameters.
	 * @param input a {@link BitVector} to be filtered
	 * @param params optional parameters
	 * @return a filtered version of original {@link BitVector}
	 */
	public BitVector getFilteredVersion(BitVector input, Object... params);
}

