package edu.memphis.ccrg.cla.corticalregion.tasks;

import java.util.logging.Level;
import java.util.logging.Logger;

import cern.colt.bitvector.BitVector;
import edu.memphis.ccrg.cla.corticalregion.CorticalRegionBottomUpSource;
import edu.memphis.ccrg.cla.corticalregion.CorticalRegionImpl;
import edu.memphis.ccrg.lida.framework.FrameworkModule;
import edu.memphis.ccrg.lida.framework.tasks.FrameworkTaskImpl;
import edu.memphis.ccrg.lida.framework.tasks.TaskManager;

/**
 * This task runs the "main loop" or one processing cycle of a
 * {@link CorticalRegionImpl}.
 * 
 * @author Ryan J. McCall
 */
public class FullCorticalRegionTask extends FrameworkTaskImpl {
	
	private static final Logger logger = Logger.getLogger(FullCorticalRegionTask.class.getCanonicalName());
	private static final int timeReportFrequency=1000;
	protected boolean isLearningOn;
	protected boolean isLoggingOn;
	protected boolean isDecayOn;
	protected CorticalRegionBottomUpSource bottomUpSource;
	protected CorticalRegionImpl processingRegion;
	
	@Override
	public void init() {
		super.init();
		int tpr = getParam("backgroundTicksPerRun",2);
		setTicksPerRun(tpr);
		int initialTick = getParam("initialRunTick",1);
		setNextTicksPerRun(initialTick);
		isLearningOn = getParam("learningOn", false);
		isLoggingOn = getParam("loggingOn", false);
		isDecayOn = getParam("decayOn", false);
	}

	@Override
	public void setAssociatedModule(FrameworkModule m, String usage) {
		if ("input".equalsIgnoreCase(usage)) {
			if (m instanceof CorticalRegionBottomUpSource) {
				bottomUpSource = (CorticalRegionBottomUpSource) m;
			} else {
				logger.log(Level.WARNING,
						"Cannot associate module with usage {1}", new Object[] {
								TaskManager.getCurrentTick(), usage });
			}
		} else if ("processing".equalsIgnoreCase(usage)) {
			if (m instanceof CorticalRegionImpl) {
				processingRegion = (CorticalRegionImpl) m;
			} else {
				logger.log(Level.WARNING,
						"Cannot associate module with usage {1}", new Object[] {
								TaskManager.getCurrentTick(),usage});
			}
		}
	}
//	private static final double divisor=10e3;
	@Override
	public void runThisFrameworkTask() {
		if(TaskManager.getCurrentTick() % timeReportFrequency == 0){
			logger.log(Level.INFO,"Running cycle: {0} of {1}",
					new Object[]{TaskManager.getCurrentTick(),TaskManager.getShutdownTick()});
//			logger.log(Level.INFO, "Setup: " + setup/count/divisor);
//			logger.log(Level.INFO, "Spatial-Pooler: " + spatialPooling/count/divisor);
//			logger.log(Level.INFO, "Temporal-Pooler: " + temporalPooling/count/divisor);
//			logger.log(Level.INFO, "TopDown: " + topdown/count/divisor);
		}
		runSpatialPooler();
		
//		long start = 0;
//		long finish = 0;
//		start = System.nanoTime();
		runTemporalPooler();
//		finish = System.nanoTime();
//		temporalPooling+=(finish-start);
		
//		start = System.nanoTime();
		sendStructuralPrediction();
//		finish = System.nanoTime();
//		topdown+=(finish-start);
		
		if(isDecayOn){
			processingRegion.decay();
		}
		if (isLoggingOn) {
			processingRegion.logRegionState();
		}
//		count++;
	}
//	private long start;
//	private long finish;
//	private long setup;
//	private long spatialPooling;
//	private long topdown;
//	private long temporalPooling;
//	private int count;
	protected void runSpatialPooler() {
		BitVector input = (BitVector) bottomUpSource.getOutputSignal();

//		start = System.nanoTime();		
		processingRegion.setupForCycle(input);		
//		finish = System.nanoTime();
//		setup+=finish-start;	
		
//		start = System.nanoTime();
		processingRegion.performSpatialPooling();		
		if(isLearningOn){
			processingRegion.performSpatialLearning();
//			finish = System.nanoTime();		
//			spatialPooling+=finish-start;
		}
	}
	protected void runTemporalPooler() {
		processingRegion.performTemporalPooling();
		if(isLearningOn){
			processingRegion.performTemporalLearning();
		}
	}
	protected void sendStructuralPrediction() {
		BitVector prediction = processingRegion.getGeneratedStructuralPrediction();
		bottomUpSource.setReceivedStructuralPrediction(prediction);
	}
}