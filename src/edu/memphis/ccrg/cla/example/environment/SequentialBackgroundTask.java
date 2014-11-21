package edu.memphis.ccrg.cla.example.environment;

import edu.memphis.ccrg.lida.framework.FrameworkModule;
import edu.memphis.ccrg.lida.framework.tasks.FrameworkTaskImpl;

/**
 * Updates its associated {@link BitVectorEnvironment}'s current pattern 
 * in a sequential modular fashion.
 * @author Ryan J. McCall
 */
public class SequentialBackgroundTask extends FrameworkTaskImpl {

	private int currentInputIndex;
	private int patternCount;
	private BitVectorEnvironment env;
	
	@Override 
	public void init(){
		//Avoid same-tick scheduling with CLA background tasks
		int tick = getParam("initialRunTick",1);
		setNextTicksPerRun(tick);
	}
	
	@Override
	public void setAssociatedModule(FrameworkModule module, String moduleUsage){
		if(module instanceof BitVectorEnvironment){
			env = (BitVectorEnvironment) module;
			patternCount = env.getPatternCount();
		}
	}
	
	@Override
	protected void runThisFrameworkTask() {
		env.setCurrentPattern(currentInputIndex);	
		currentInputIndex = ++currentInputIndex%patternCount;
	}
}