package edu.memphis.ccrg.cla.example.environment.initializers;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import cern.colt.bitvector.BitVector;
import edu.memphis.ccrg.cla.example.environment.BitVectorEnvironment;
import edu.memphis.ccrg.cla.utils.MiscUtils;
import edu.memphis.ccrg.lida.framework.Agent;
import edu.memphis.ccrg.lida.framework.initialization.FullyInitializable;
import edu.memphis.ccrg.lida.framework.initialization.GlobalInitializer;
import edu.memphis.ccrg.lida.framework.initialization.Initializer;

/**
 * Initializes a {@link BitVectorEnvironment} by 
 * parsing a {@link String} of letters into {@link BitVector} patterns.
 * 
 * @author Ryan J. McCall
 */
public class LetterStringInitializer implements Initializer {

	private static final Logger logger = Logger
			.getLogger(LetterStringInitializer.class.getCanonicalName());
		
	private static final String DEFAULT_FONT_NAME = "Times New Roman";
	private static final int DEFAULT_FONT_STYLE = Font.PLAIN;
	private static final double DEFAULT_FONT_POINT_CONVERSION = 0.75; 

	/**
	 * Useage: <param name="input">letter;letter;letter;letter</param>
	 */
	@Override
	public void initModule(FullyInitializable module, Agent agent,
			Map<String, ?> params) {
		BitVectorEnvironment environment = (BitVectorEnvironment) module;
		String input = (String) params.get("input");
		if(input != null){ 	//Get font attributes
			String fontName = (String) params.get("fontName");
			if(fontName == null){
				fontName = DEFAULT_FONT_NAME;
			}
			Integer fontStyle = (Integer)params.get("fontStyle");
			if(fontStyle == null){
				fontStyle = DEFAULT_FONT_STYLE;
			}
			Double fontPointConversion = (Double)params.get("fontPointConversion");
			if(fontPointConversion == null){
				fontPointConversion = DEFAULT_FONT_POINT_CONVERSION;
			}
			int inputDim = (Integer)GlobalInitializer.getInstance().getAttribute("inputSignalDimensionality");
			int inputSizeRoot = (int)Math.sqrt(inputDim);
			int pointSize = (int) (fontPointConversion*inputSizeRoot);
			Font font = new Font(fontName,fontStyle,pointSize);//create font
			logger.log(Level.INFO, "Font requested: {0}. Font loaded: {1}",
						new Object[]{fontName,font.getFontName()});
			

			AffineTransform trans = new AffineTransform();
			//Could parse a transform for each pattern
//			AffineTransform trans = AffineTransform.getScaleInstance(scaling, scaling);
			FontRenderContext context = new FontRenderContext(trans, true, false);
			String[] pattern = input.split("\\;");
			for (String s : pattern) {
				GlyphVector gv = font.createGlyphVector(context, s.trim());
				BitVector bv = MiscUtils.convert(gv,inputSizeRoot,inputSizeRoot);
				environment.addPattern(bv,s);
			}
			logger.log(Level.INFO,"\n");
		}
	}
}