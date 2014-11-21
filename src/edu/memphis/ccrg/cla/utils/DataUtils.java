package edu.memphis.ccrg.cla.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.memphis.ccrg.cla.gui.DataVisualizer;
import edu.memphis.ccrg.cla.logging.ClaMeasure;
import edu.memphis.ccrg.lida.framework.tasks.TaskManager;

public class DataUtils {

	private static final Logger logger = Logger.getLogger(DataUtils.class.getCanonicalName());
	
	/**
	 * For each specified measure, reads specified logFilePath recording all instances of the measure.
	 * Then all instances are written to a file in specified outputDirectory with a file name equal to 
	 * the measure name. 
	 * @param measures
	 * @param inputPath
	 * @param outputDirPath
	 */
	public static void writeMeasures(Object[] measures, List<String> inputSequence, String inputPath,
									 String outputDirPath){
		logger.log(Level.INFO, "Writing measures...");
		File logFile = new File(inputPath);
		if(logFile.exists()){
			File outputDir = new File(outputDirPath);
			if(!outputDir.exists()){
				outputDir.mkdir();
			}else{
				clearDataDirectory(outputDir);
			}			
			for(Object m: measures){
				String measureName = m.toString();
				logger.log(Level.INFO, "Writing measure: {0}",measureName);
				Collection<String> allValues = parseMeasure(logFile, measureName);
				if(allValues != null){
									
					Object[] valueArray = (Object[]) allValues.toArray();
					String measureSubDirPath = outputDirPath + measureName + "/";
					File measureSubDir = new File(measureSubDirPath);
					if(!measureSubDir.exists()){
						measureSubDir.mkdir();
					}
					int sequenceLength = inputSequence.size();
					for(int i = 0; i < sequenceLength; i++){
						String outputPath = measureSubDirPath + inputSequence.get(i) + ".txt";
						Collection<Object> measureValues = new ArrayList<Object>();
						for(int j = i; j < valueArray.length; j+=sequenceLength){
							measureValues.add((String) valueArray[j]);
						}
						writeResults(outputPath, measureValues);
					}
				}
			}
		}else{
			logger.log(Level.WARNING, "Cannot find log file at path: {0}",inputPath);
		}
		logger.log(Level.INFO, "Finished Writing Measures");
	}

	/**
	 * Clears the contents in the specified directory that was originally created by
	 * {@link #writeMeasures(Object[], List, String, String)}.
	 * @param file 
	 */
	public static void clearDataDirectory(File file) {
		if(file != null && file.exists()){
			for(File subFile: file.listFiles()){
				if(subFile.isDirectory()){
					for(File f: subFile.listFiles()){
						f.delete();
					}					
				}
				subFile.delete();
			}
		}
	}
	
	/**
	 * Reads file with specified path and returns a collection of all instances of specified measure's value.
	 * Line format assumed is: "measure-name:value"
	 * @param logFilePath
	 * @param measure
	 * @return
	 */
	public static Collection<String> parseMeasure(File logFile, String measure){
		Collection<String> results = new ArrayList<String>();
		if(measure==null){
			logger.log(Level.SEVERE,"Measure was null.");
			return results;
		}
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(logFile));
			for(String inputLine=br.readLine(); inputLine!= null; inputLine=br.readLine()) {
				String[] splits = inputLine.split(":");
				if (measure.equals(splits[0]) && splits.length>1) {
					results.add(splits[1].trim());
				}
			}
			br.close();
		} catch (FileNotFoundException e) {		
			results = new ArrayList<String>();
			logger.log(Level.WARNING, "Could not find file with path: {1}",
					new Object[]{TaskManager.getCurrentTick(),logFile});
		} catch (IOException e) {
			results = new ArrayList<String>();
			logger.log(Level.WARNING, "IOException reading file with path: {1}",
					new Object[]{TaskManager.getCurrentTick(),logFile});
		}		
		return results;
	}
	
	/**
	 * Writes each element of the results on a separate line to specified outputPath.
	 * @param outputPath
	 * @param results
	 */
	public static void writeResults(String outputPath, Collection<?> results) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath));
			for(Object o: results){
				writer.write(o+"\n");
			}
			writer.close();
		} catch (IOException e) {
			logger.log(Level.WARNING, "IOException writing to path: {1}",
					new Object[]{TaskManager.getCurrentTick(),outputPath});
		}		
	}

	/**
	 * Computes and returns the average of all parseable double values found at specified path or null.
	 * @param path a File path
	 * @return an average
	 */
	public static Double getAverage(String path, int initialCyclesIgnored) {
		Double average = null;
		BufferedReader br = null;
		double parseFailures = 0;
		try {
			br = new BufferedReader(new FileReader(new File(path)));
			String inputLine = br.readLine();
			double sum = 0;
			int goodPoints = 0;
			int ignored = 0;
			while(inputLine != null) {
				if(ignored < initialCyclesIgnored){
					ignored++;
				}else{
					inputLine = inputLine.replaceAll(",","");
					try{
						sum += Double.parseDouble(inputLine);
						goodPoints++;
					}catch(NumberFormatException e){
						parseFailures++;
					}
				}				
				inputLine = br.readLine();
			}
			average = Math.round(sum/goodPoints*1000.0)/1000.0;
			br.close();
		} catch (FileNotFoundException e) {		
			logger.log(Level.WARNING, "Could not find file with path: {1}",
					new Object[]{TaskManager.getCurrentTick(),path});
		} catch (IOException e) {
			logger.log(Level.WARNING, "IOException reading file with path: {1}",
					new Object[]{TaskManager.getCurrentTick(),path});
		}
		if(parseFailures > 0){
			logger.log(Level.WARNING, "For: {0}, parse failures: {1}",
					new Object[]{path,parseFailures});
		}
		return average;		
	}

	/**
	 * Starts the {@link DataVisualizer} GUI using specified data directory.
	 * @param dataDir
	 */
	public static void runDataVisualizer(final String dataDir) {
		Runnable r = new Runnable() {
			public void run() {
				DataVisualizer dv = new DataVisualizer(dataDir);
				dv.setVisible(true);
			}
		};
		java.awt.EventQueue.invokeLater(r);
	}

	/**
	 * For each of the specified measures, compute the averages of the measure for 
	 * each of the input types, and their overall average, and write the results to a separate file
	 * in specified output directory. Thus there will be an average file created/modified for each
	 * specified measure, where each file has inputFileName.size() + 1 average values. 
	 * @param measures
	 * @param inputFileNames
	 * @param dataDirPath
	 * @param outputDir
	 */
	public static void writeMeasureAverages(Collection<ClaMeasure> measures,Collection<String> inputFileNames, 
						String dataDirPath, String outputDir, int initialCyclesIgnored) {
		logger.log(Level.INFO, "Writing measures averages...");
		File outputFile = new File(outputDir);
		if(!outputFile.exists()){
			outputFile.mkdir();
		}
		for(ClaMeasure m: measures){
			String measureDir = dataDirPath+m;
			List<Object> averages = new ArrayList<Object>();
			for(String fileName: inputFileNames){ //For each of the files containing data
				String path = measureDir+"/"+fileName+".txt";			
				double ave = getAverage(path,initialCyclesIgnored);
				averages.add(ave);				
			}
			double sum = 0; //Compute average across files
			for(Object o: averages){
				sum += (Double) o;
			}
			averages.add(sum/averages.size());
			writeResults(outputDir + m +".txt", averages);
		}
		logger.log(Level.INFO, "Finished Writing Measure Averages");
	}
}