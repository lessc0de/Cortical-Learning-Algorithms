package edu.memphis.ccrg.cla.corticalregion.initialization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;

import edu.memphis.ccrg.cla.corticalregion.cells.Cell;
import edu.memphis.ccrg.cla.corticalregion.columns.Column;
import edu.memphis.ccrg.cla.corticalregion.connections.DendriteSegment;
import edu.memphis.ccrg.cla.corticalregion.connections.Synapse;


public class CorticalRegionFactoryTest {

	private static final double epsilon = 10e-9;
	private static final CorticalRegionFactory factory = CorticalRegionFactory.getInstance();

	private int inputHeight;
	private int inputWidth;
	private double columnsPerInput;
	private int regionHeight;
	private int regionWidth;
	private int columnSize;
	private double distalLearningRadius;
	private double synpaseConnectionThreshold;
	private double permanenceSigma;
	private double proximalSynapseSourceSigma;
	private double proximalSynapsesFactor;
	private int columnHistorySize;
	private int dendritesPerCell;
	private int synapsesPerDistalSegment;
	protected CorticalRegionDef def;
	private String regionType = "default";
	
	private void configureDef(){
		def = new CorticalRegionDef();
		def.setColumnsPerInput(columnsPerInput);
		def.setCellsPerColumn(columnSize);
		def.setSynapseConnectionThreshold(synpaseConnectionThreshold);
		def.setProximalPermanenceSigma(permanenceSigma);
		def.setProximalSynapseSourceSigma(proximalSynapseSourceSigma);
		def.setProximalSynapsesFactor(proximalSynapsesFactor);
		def.setColumnHistorySize(columnHistorySize);
		def.setDendritesPerCell(dendritesPerCell);
		def.setIntialSynapsesPerDistalSegment(synapsesPerDistalSegment);
		def.setDistalLearningRadius(distalLearningRadius);
		factory.addCorticalRegionDef(regionType, def);
	}
	
	@Test
	public void testGetColumns() {
		inputHeight=2;
		inputWidth=3;
		columnsPerInput = 1.0;
		regionHeight=2;
		regionWidth=3;
		columnHistorySize=2;
		synpaseConnectionThreshold=0.1;
		permanenceSigma=0.2;
		proximalSynapseSourceSigma=0.9;
		proximalSynapsesFactor=5;
		configureDef();

		Column cols[][]=factory.getColumns(regionType,inputHeight,inputWidth);
		assertEquals(regionWidth, cols[0].length);
		assertEquals(regionHeight, cols.length);
	}
	
	@Test
	public void testGetColumn() {
		int widthPos=0;
		int heightPos=0;		
		proximalSynapsesFactor = 10;
		inputHeight=5;
		inputWidth=5;
		regionHeight=1;
		regionWidth=1;
		columnHistorySize=2;
		synpaseConnectionThreshold=0.1;
		permanenceSigma=0.2;
		proximalSynapseSourceSigma=0.9;
		configureDef();
	
		Column col=factory.getColumn(def, widthPos, heightPos,inputHeight,inputWidth,regionHeight,regionWidth);
//		assertEquals(proximalSynapsesFactor,col.getPotentialSynapses().size());
		for(Synapse ps: col.getPotentialSynapses()){
			assertNotNull(ps);
		}
	
		proximalSynapsesFactor=0;
		configureDef();
		col=factory.getColumn(def, widthPos, heightPos,inputHeight,inputWidth,regionHeight,regionWidth);
//		assertEquals(proximalSynapsesFactor,col.getPotentialSynapses().size());
		
		proximalSynapsesFactor=-23598938;
		configureDef();
		col=factory.getColumn(def, widthPos, heightPos,inputHeight,inputWidth,regionHeight,regionWidth);
		assertEquals(0,col.getPotentialSynapses().size());
	}
	
	@Test
	public void testGetColumn1() {
		int widthPos=1;
		int heightPos=1;
		inputHeight=6;
		inputWidth=6;
		regionHeight=3;
		regionWidth=2;
		columnHistorySize=2;
		synpaseConnectionThreshold=0.1;
		permanenceSigma=0.2;
		proximalSynapseSourceSigma=0.9;
		proximalSynapsesFactor=10;
		configureDef();
		
		Column col=factory.getColumn(def, widthPos, heightPos,inputHeight,inputWidth,regionHeight,regionWidth);
		
		assertEquals(2,col.getInputHeightPosition(),epsilon);
		assertEquals(3,col.getInputWidthPosition(),epsilon);
	}
	
	@Test
	public void testGetColumn2() {
		int widthPos=1;
		int heightPos=2;		
		inputHeight=6;
		inputWidth=6;
		regionHeight=3;
		regionWidth=2;
		columnHistorySize=2;
		synpaseConnectionThreshold=0.1;
		permanenceSigma=0.2;
		proximalSynapseSourceSigma=0.9;
		proximalSynapsesFactor=10;
		configureDef();
		
		Column col=factory.getColumn(def, heightPos,widthPos,inputHeight,inputWidth,regionHeight,regionWidth);
		
		assertEquals(4,col.getInputHeightPosition(),epsilon);
		assertEquals(heightPos*((double)inputHeight/regionHeight),col.getInputHeightPosition(),epsilon);
		//
		assertEquals(3,col.getInputWidthPosition(),epsilon);
		assertEquals(widthPos*((double)inputWidth/regionWidth),col.getInputWidthPosition(),epsilon);
	}

	@Test
	public void testGetProximalSynapse() {
		double widthPos=0;
		double heightPos=0;		
		inputHeight=5;
		inputWidth=5;
		regionHeight=1;
		regionWidth=1;
		synpaseConnectionThreshold=0.1;
		permanenceSigma=0.2;
		proximalSynapseSourceSigma=0.9;
		configureDef();
		
		Synapse s= factory.getProximalSynapse(heightPos, widthPos,inputHeight,inputWidth,
				synpaseConnectionThreshold, permanenceSigma, proximalSynapseSourceSigma);
		assertNotNull(s);
	}

	@Test
	public void testGetRandomInputPosition() {
		int regionSize = 20;
		int inputSize = 300;
		double ratio = (double)inputSize/regionSize;
		double centerPosition = 10*ratio;
		proximalSynapseSourceSigma = 5;
		configureDef();
		double tests = 300000.0;
		double p = -1;
		double pSum = 0.0;
		double epsilon = 1;
		for(int i = 0; i < tests; i++){
			p = factory.nextRoundedGaussianInRange(centerPosition, proximalSynapseSourceSigma,0,inputSize-1,1000);
			pSum += p;
			assertTrue(p >= 0);
			assertTrue(p <= inputSize);
		}
		double ave = pSum/tests; 
		assertTrue(ave < centerPosition + epsilon);
		assertTrue(ave > centerPosition - epsilon);
	}

	@Test
	public void testGetRandomPermanence() {
		synpaseConnectionThreshold = 0.5;
		permanenceSigma = 4;
		configureDef();
		double p = -1;	
		double pSum = 0.0;
		double tests = 100000.0;
		double epsilon = 0.01;
		for(int i = 0; i < tests; i++){
			p = factory.nextGaussianInRange(synpaseConnectionThreshold,permanenceSigma,0.0,1.0,1000);
			assertTrue(p >= 0.0);
			assertTrue(p <= 1.0);
			pSum += p;
		}
		assertTrue(pSum/tests < synpaseConnectionThreshold + epsilon);
		assertTrue(pSum/tests > synpaseConnectionThreshold - epsilon);
	}

	@Test
	public void testGetCells() {		
		regionHeight = 2;
		regionWidth = 3;
		columnSize = 4;
		dendritesPerCell = 5;
		synapsesPerDistalSegment = 5;
		distalLearningRadius = 5;
		configureDef();
		
		Cell[][][] cells = factory.getCells(regionType,regionHeight, regionWidth);
		assertEquals(regionHeight, cells.length);
		assertEquals(regionWidth, cells[0].length);
		assertEquals(columnSize, cells[0][0].length);
		assertEquals(dendritesPerCell, cells[0][0][0].getDendriteSegments().size());
		for(int i = 0; i < cells.length; i++){
			for(int j = 0; j < cells[0].length; j++){
				for(int k = 0; k < cells[0][0].length; k++){
					for(DendriteSegment ds: cells[i][j][k].getDendriteSegments()){
						assertEquals(synapsesPerDistalSegment,ds.getPotentialSynapses().size());
					}					
				}
			}
		}
	}

	@Test
	public void testGetCell() {
		int cellHeight = 5;
		int cellWidth = 5;	
		regionHeight = 10;
		regionWidth = 10;
		columnSize = 4;	
		dendritesPerCell = 1000;
		synapsesPerDistalSegment = 1;
		distalLearningRadius = 5;
		double initialDistalSynapsePermanence = 0.5;
		configureDef();
		Cell c = factory.getCell(cellHeight, cellWidth,regionHeight, regionWidth, columnSize,
				dendritesPerCell, synapsesPerDistalSegment, distalLearningRadius, initialDistalSynapsePermanence);
		assertEquals(dendritesPerCell, c.getDendriteSegments().size());
		for(DendriteSegment ds: c.getDendriteSegments()){
			assertNotNull(ds);
		}
		//
		dendritesPerCell = 0;
		configureDef();
		c = factory.getCell(cellHeight, cellWidth,regionHeight, regionWidth, columnSize,
				dendritesPerCell, synapsesPerDistalSegment, distalLearningRadius, initialDistalSynapsePermanence);
		assertEquals(dendritesPerCell, c.getDendriteSegments().size());		
		//
		dendritesPerCell = -23598938;
		configureDef();
		c = factory.getCell(cellHeight, cellWidth,regionHeight, regionWidth, columnSize,
				dendritesPerCell, synapsesPerDistalSegment, distalLearningRadius, initialDistalSynapsePermanence);
		assertEquals(0, c.getDendriteSegments().size());
	}
	
	@Test
	public void testGetNewDistalSynapses(){
		int cellHeight = 5;
		int cellWidth = 5;	
		regionHeight = 10;
		regionWidth = 10;
		columnSize = 4;	
		synapsesPerDistalSegment = 10;
		distalLearningRadius = 5;
		synpaseConnectionThreshold = 0.1;
		permanenceSigma = 2;
		double initialDistalSynapsePermanence = 0.5;
		configureDef();
		Collection<Synapse> synapses = factory.getDistalSynapses(synapsesPerDistalSegment,
				cellHeight, cellWidth,regionHeight, regionWidth, columnSize,distalLearningRadius,initialDistalSynapsePermanence);
		assertEquals(synapsesPerDistalSegment,synapses.size());
		for(Synapse s: synapses){
			assertTrue(s.getSourceColumn() >= 0);
			assertTrue(s.getSourceColumn() < columnSize);
			assertTrue(s.getSourceHeightPos() >= 0);
			assertTrue(s.getSourceHeightPos() < regionHeight);
			assertTrue(s.getSourceWidthPos() >= 0);
			assertTrue(s.getSourceWidthPos() < regionWidth);
			assertEquals(initialDistalSynapsePermanence,s.getPermanence(),epsilon);
			if(s.getPermanence() > synpaseConnectionThreshold){
				assertTrue(synapses.contains(s));
			}
		}
	}

}