package edu.memphis.ccrg.cla.example.environment.initializers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import cern.colt.bitvector.BitVector;
import edu.memphis.ccrg.cla.example.environment.BitVectorEnvironment;
import edu.memphis.ccrg.lida.framework.Agent;
import edu.memphis.ccrg.lida.framework.initialization.FullyInitializable;
import edu.memphis.ccrg.lida.framework.initialization.Initializer;
import edu.memphis.ccrg.lida.framework.tasks.TaskManager;

/**
 * Parses a file of form: bit,bit,bit,bit;bit,bit,bit,bit creating {@link BitVector}s
 * delineated by ';'. 
 * @author Ryan J. McCall
 *
 */
public class FileInitializer implements Initializer {

	private static final Logger logger = Logger
			.getLogger(FileInitializer.class.getCanonicalName());

	@Override
	public void initModule(FullyInitializable module, Agent agent,
			Map<String, ?> params) {
		BitVectorEnvironment environment = (BitVectorEnvironment) module;
		String path = (String) params.get("fileName");
		if(path != null){
			BufferedReader br = null;
			StringBuilder input = new StringBuilder(100);
			try {
				br=new BufferedReader(new FileReader(new File(path)));
				String inputLine = "";
				while(true){
					try {
						inputLine = br.readLine();
						if(inputLine == null){
							break;
						}
						input.append(inputLine);
					} catch (IOException e) {
						logger.log(Level.WARNING,"IO Exception reading file: {1}",
								new Object[]{TaskManager.getCurrentTick(),path});
					}
				}
			} catch (FileNotFoundException e) {
				logger.log(Level.WARNING,"Could not find file at path: {1}",
						new Object[]{TaskManager.getCurrentTick(),path});
			}
			environment.parsePattern(input.toString());
		}
	}

}
