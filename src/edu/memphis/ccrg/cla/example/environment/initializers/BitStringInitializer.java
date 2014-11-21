package edu.memphis.ccrg.cla.example.environment.initializers;

import java.util.Map;

import edu.memphis.ccrg.cla.example.environment.BitVectorEnvironment;
import edu.memphis.ccrg.lida.framework.Agent;
import edu.memphis.ccrg.lida.framework.initialization.FullyInitializable;
import edu.memphis.ccrg.lida.framework.initialization.Initializer;

public class BitStringInitializer implements Initializer {

	/**
	 * Usage: <param name="input">bit,bit,bit,bit;bit,bit,bit,bit</param>
	 */
	@Override
	public void initModule(FullyInitializable module, Agent agent,
			Map<String, ?> params) {
		BitVectorEnvironment environment = (BitVectorEnvironment) module;
		String input = (String) params.get("input");
		if(input != null){
			environment.parsePattern(input);
		}
	}

}
