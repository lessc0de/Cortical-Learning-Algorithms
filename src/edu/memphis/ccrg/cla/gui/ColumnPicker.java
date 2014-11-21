package edu.memphis.ccrg.cla.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.event.MouseInputListener;

import edu.memphis.ccrg.cla.corticalregion.connections.Synapse;
import edu.memphis.ccrg.cla.gui.CorticalRegionAspect;
import edu.memphis.ccrg.cla.gui.CorticalRegionVisualizer;

/**
 * This class is a JPanel that can be added to LIDA GUI for picking columns for which
 * you want to see the reconstruction. Columns can be selected by selecting an aspect or
 * clicking on the respective column to select or deselect it. 
 * 
 * The reconstruction is seen on the {@link ConnectedInputVisualizer ConnectedInputVisualizer} panel
 * on refreshing this panel.
 * 
 * @author Pulin Agrawal
 *
 */
public class ColumnPicker extends CorticalRegionVisualizer implements MouseInputListener,ItemListener{
	public static int callCount=0;
	static boolean aspectActivity[][];
	static boolean selectedColumns[][];
	boolean propertyChanged=true;
	protected void initComponents() {
		super.initComponents();
		addMouseListener(this);
		//  Auto-generated method stub
	}
	 private BufferedImage render2DBooleanArray(boolean[][] array) {
		   if(callCount==0){
			   selectedColumns=new boolean[array.length][array[0].length];
			   callCount++;
		   }
	        Graphics2D g = renderedImage.createGraphics();
	        g.setColor(offColor);
	        g.fillRect(0, 0, array.length*CELL_SIZE, array[0].length*CELL_SIZE);
	        for (int i = 0; i < array.length; i++) {
	            for (int j = 0; j < array[0].length; j++) {
	            	if(selectedColumns[i][j]){
	            		g.setColor(onColor);
	            		g.fillRect(i*CELL_SIZE, j*CELL_SIZE, CELL_SIZE, CELL_SIZE);
	            	}
	         	    g.setColor(Color.black);
	            	g.drawRect(i*CELL_SIZE, j*CELL_SIZE, CELL_SIZE, CELL_SIZE);
	            }
	        }
	        return renderedImage;
	    }

	@Override
	public void mouseClicked(MouseEvent arg0) {
		int scaleX=renderPanel.getX();
		int scaleY=renderPanel.getY();
		int xPos=arg0.getX()-scaleX-((renderPanel.getWidth() - scaledRenderedImage.getWidth(null)) / 2); 
		int yPos=arg0.getY()-scaleY-((renderPanel.getHeight() - scaledRenderedImage.getHeight(null)) / 2);
		double xFactor=((double)region.getRegionWidth())/scaledRenderedImage.getWidth(null);
		double yFactor=((double)region.getRegionHeight())/scaledRenderedImage.getHeight(null);
		try{
		selectedColumns[(int) (xPos*xFactor)][(int) (yPos*yFactor)]=!selectedColumns[(int) (xPos*xFactor)][(int) (yPos*yFactor)];
		refresh();		
		}catch(ArrayIndexOutOfBoundsException e){
			
		}
		refreshAspectActivity();
	}
	
	protected void refreshAspectActivity(){
		aspectActivity=new boolean[region.getBottomUpInputSizeSqrt()][region.getBottomUpInputSizeSqrt()];
		for(int i=0;i<selectedColumns.length;i++)
			for(int j=0;j<selectedColumns[0].length;j++){
				if(selectedColumns[i][j]){
					for(Synapse s:region.getColumns()[i][j].getConnectedSynapses()){
						aspectActivity[s.getSourceHeightPos()][s.getSourceWidthPos()]=true;
					}
						
				}
			}
	}
	
	public void refresh() {
		CorticalRegionAspect aspect = (CorticalRegionAspect) aspectComboBox.getSelectedItem();	
		aspectComboBox.addItemListener(this);
    	Object aspectActivity = region.getModuleContent(aspect);
    	if(aspectActivity != null){
    		if(aspectActivity instanceof boolean[][]){
    			if(propertyChanged){
    				selectedColumns=copyArray((boolean[][])aspectActivity);
    				propertyChanged=false;
    			}
    			renderedImage = render2DBooleanArray((boolean[][]) aspectActivity);
    			auxRefresh();
    		}
    	}
    	refreshAspectActivity();
    }
	
	protected boolean[][] copyArray(boolean[][] aspectActivity){
		boolean[][] returnArray=new boolean[aspectActivity.length][aspectActivity[0].length];
		for(int i=0;i<aspectActivity.length;i++)
			for(int j=0;j<aspectActivity[0].length;j++)
			returnArray[i][j]=aspectActivity[i][j];
		return returnArray;
	}
	@Override
	public void mouseEntered(MouseEvent arg0) {
		//  Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent arg0) {
		//  Auto-generated method stub
		
	}
	@Override
	public void mousePressed(MouseEvent arg0) {
		//  Auto-generated method stub
		
	}
	@Override
	public void mouseReleased(MouseEvent arg0) {
		//  Auto-generated method stub
		
	}
	@Override
	public void mouseDragged(MouseEvent arg0) {
		//  Auto-generated method stub
		
	}
	@Override
	public void mouseMoved(MouseEvent arg0) {
		//  Auto-generated method stub
		
	}
	@Override
	public void itemStateChanged(ItemEvent arg0) {
		propertyChanged=true;
	}
}
