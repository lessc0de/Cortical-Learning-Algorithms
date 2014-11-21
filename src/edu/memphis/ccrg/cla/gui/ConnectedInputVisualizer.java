package edu.memphis.ccrg.cla.gui;
import java.awt.Image;
import edu.memphis.ccrg.cla.gui.InputVisualizer;

/**
 * To visualize the reconstruction of the selected columns from the 
 * {@link ColumnPicker ColumnPicker} Panel.
 * 
 * Click on refresh to see the reconstruction 
 * 
 * @author Pulin Agrawal
 *
 */
public class ConnectedInputVisualizer extends InputVisualizer{
	public void refresh() {
    	Object aspectActivity = ColumnPicker.aspectActivity;
    	if(aspectActivity != null){
    		renderedImage = renderArray((boolean[][]) aspectActivity);
	        if(renderedImage!=null){
	            scaledRenderedImage = renderedImage.getScaledInstance((int)(scaledWidth*zoom), (int)(scaledHeight*zoom), Image.SCALE_SMOOTH);
	        }
	        renderPanel.revalidate();
	        renderPanel.repaint();
    	}
    }
}
