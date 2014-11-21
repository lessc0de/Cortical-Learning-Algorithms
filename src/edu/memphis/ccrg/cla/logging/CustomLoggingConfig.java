package edu.memphis.ccrg.cla.logging;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import edu.memphis.ccrg.lida.framework.initialization.ConfigUtils;

/**
 * A simple utility to configure the Level of individual loggers.
 * Tries to read configuration from default path: "configs/logging.properties".
 * @author Ryan J. McCall
 * @see LogManager
 */
public class CustomLoggingConfig {

	private static final Logger configLogger = Logger.getLogger(CustomLoggingConfig.class.getCanonicalName());
	private static final String DEFAULT_LOGGING_PROPERTIES_PATH = "configs/logging.properties";
	public static String loggingPropPath = DEFAULT_LOGGING_PROPERTIES_PATH;
	
	public CustomLoggingConfig(){
		Properties p = ConfigUtils.loadProperties(loggingPropPath);
		if(p != null){
			for(Object key: p.keySet()){
				Logger logger = Logger.getLogger((String) key);
				if(logger != null){
					String value = p.getProperty((String) key);
					try{					
						Level level = Level.parse(value);
						logger.setLevel(level);
					}catch(Exception e){
						configLogger.log(Level.WARNING, "Could not parse {0} to a Level.",value);
					}
				}
			}
		}
	}

}