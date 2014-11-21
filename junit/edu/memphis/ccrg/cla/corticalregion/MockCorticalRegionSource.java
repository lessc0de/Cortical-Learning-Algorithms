package edu.memphis.ccrg.cla.corticalregion;

import cern.colt.bitvector.BitVector;
import edu.memphis.ccrg.lida.framework.FrameworkModuleImpl;

public class MockCorticalRegionSource extends FrameworkModuleImpl implements CorticalRegionBottomUpSource {
	
	public int inputHeight;
	public int inputWidth;
	@Override
	public int getOutputDimensionality() {
		return inputHeight*inputWidth;
	}
	@Override
	public void decayModule(long arg0) {
	}
	@Override
	public BitVector getOutputSignal() {
		return null;
	}
	@Override
	public void setReceivedStructuralPrediction(Object prediction) {
	}
}