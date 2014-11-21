package edu.memphis.ccrg.cla.corticalregion.initialization;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.memphis.ccrg.cla.corticalregion.CorticalRegionImpl;
import edu.memphis.ccrg.lida.framework.Agent;
import edu.memphis.ccrg.lida.framework.FrameworkModule;
import edu.memphis.ccrg.lida.framework.initialization.FullyInitializable;
import edu.memphis.ccrg.lida.framework.initialization.Initializer;
import edu.memphis.ccrg.lida.framework.shared.ElementFactory;
import edu.memphis.ccrg.lida.framework.tasks.FrameworkTask;

/**
 * Creates or reuses a {@link CorticalRegionDef} and 
 * initializes a {@link CorticalRegionImpl} based on the def.
 * Also creates and initializes specified CorticalRegion background task 
 * to run the main processing cycle for the region.
 * @author Ryan J. McCall
 */
public class CorticalRegionInitializer implements Initializer {
	
	private static final Logger logger = Logger.getLogger(CorticalRegionInitializer.class.getCanonicalName());
	private static final ElementFactory elementFactory = ElementFactory.getInstance();
	private static final CorticalRegionFactory regionFactory = CorticalRegionFactory.getInstance();
	private static int defNameHelp = 0;
	
	@Override
	public void initModule(FullyInitializable m, Agent agent, Map<String, ?> params) {
		CorticalRegionImpl corticalRegion = (CorticalRegionImpl) m;
		String defName = (String) params.get("corticalRegionDefName");
		if(defName==null){
			defName = CorticalRegionImpl.class.getSimpleName()+defNameHelp++;
			logger.log(Level.WARNING,"Parameter corticalRegionDefName not specified. Using generated name: {0}.",
					defName);			
		}
		CorticalRegionDef regionDef = regionFactory.getCorticalRegionDef(defName); 
		if(regionDef == null){	
			logger.log(Level.INFO, "Creating new CorticalRegionDef: {0}", defName);
			regionDef = new CorticalRegionDef(defName);
			regionDef.init(params); //THIS IS WHY I NEED TO USE THE INITIALIZER, PARAMS NEED SENT TO DEF
			regionFactory.addCorticalRegionDef(defName,regionDef);	
		}
		corticalRegion.initCorticalRegion(defName,true);
		//Cortical Region Task
		String taskType = (String) params.get("backgroundTaskType");
		String moduleName = (String) params.get("sourceModuleName");
		FrameworkModule sourceModule = agent.getSubmodule(moduleName);
		if(taskType != null && sourceModule != null){
			FrameworkTask t = elementFactory.getFrameworkTask(taskType,params);			
			t.setAssociatedModule(sourceModule,"input");
			t.setAssociatedModule(corticalRegion,"processing");
			((FrameworkModule)corticalRegion).getAssistingTaskSpawner().addTask(t);
		}else{
			logger.log(Level.WARNING,"Could not find task type or source module {0} for region {1}",
					new Object[]{moduleName,m});
		}
		logger.log(Level.INFO,"\n");
	}
}