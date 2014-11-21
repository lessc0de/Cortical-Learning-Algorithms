package edu.memphis.ccrg.cla.example.environment.initializers;

import java.util.Map;

import cern.colt.bitvector.BitVector;
import edu.memphis.ccrg.cla.example.environment.BitVectorEnvironment;
import edu.memphis.ccrg.cla.utils.TestUtils;
import edu.memphis.ccrg.lida.framework.Agent;
import edu.memphis.ccrg.lida.framework.initialization.FullyInitializable;
import edu.memphis.ccrg.lida.framework.initialization.GlobalInitializer;
import edu.memphis.ccrg.lida.framework.initialization.Initializer;

public class RandomInputInitializer implements Initializer {

	@Override
	public void initModule(FullyInitializable module, Agent arg1,
			Map<String, ?> params) {
		BitVectorEnvironment environment = (BitVectorEnvironment) module;
		int inputs = (Integer) params.get("uniqueInputs");
		int size = (Integer) GlobalInitializer.getInstance().getAttribute("inputSignalDimensionality");
		for(int i = 0; i < inputs; i++){
			BitVector bv = TestUtils.getRandomVector(size, 0.05);
			environment.addPattern(bv, i+"");
		}
	}
}