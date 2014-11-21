package edu.memphis.ccrg.cla.corticalregion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import cern.colt.bitvector.BitVector;
import edu.memphis.ccrg.cla.corticalregion.cells.Cell;
import edu.memphis.ccrg.cla.corticalregion.cells.CellState;
import edu.memphis.ccrg.cla.corticalregion.columns.Column;
import edu.memphis.ccrg.cla.corticalregion.columns.ColumnImpl;
import edu.memphis.ccrg.cla.corticalregion.connections.DendriteSegment;
import edu.memphis.ccrg.cla.corticalregion.connections.DendriteSegmentImpl;
import edu.memphis.ccrg.cla.corticalregion.connections.SegmentUpdate;
import edu.memphis.ccrg.cla.corticalregion.connections.SegmentUpdateImpl;
import edu.memphis.ccrg.cla.corticalregion.connections.SegmentUpdateType;
import edu.memphis.ccrg.cla.corticalregion.connections.Synapse;
import edu.memphis.ccrg.cla.corticalregion.connections.SynapseImpl;
import edu.memphis.ccrg.cla.corticalregion.initialization.CorticalRegionDef;
import edu.memphis.ccrg.cla.corticalregion.initialization.CorticalRegionFactory;
import edu.memphis.ccrg.cla.utils.ClaUtils;
import edu.memphis.ccrg.cla.utils.TestUtils;
import edu.memphis.ccrg.lida.framework.initialization.AgentStarter;
import edu.memphis.ccrg.lida.framework.initialization.ConfigUtils;
import edu.memphis.ccrg.lida.framework.initialization.FactoriesDataXmlLoader;
import edu.memphis.ccrg.lida.framework.initialization.GlobalInitializer;

public class CorticalRegionImplTest {

	private static final double epsilon = 10e-9;
	private CorticalRegionImpl region;
	private Map<String, Object> params;
	private int inputSignalDimensionality = 1;

	@BeforeClass
	public static void setUpBeforeClass() {
		Properties properties = ConfigUtils
				.loadProperties(AgentStarter.DEFAULT_PROPERTIES_PATH);
		FactoriesDataXmlLoader.loadFactoriesData(properties);
	}

	@Before
	public void setUp() throws Exception {
		region = new CorticalRegionImpl();
		params = new HashMap<String, Object>();
		params.put("inputProjectionFactor", 1.0);
		params.put("regionProjectionFactor", 1.0);
		params.put("cellsPerColumn", 2);
		params.put("synapseConnectionThreshold", 0.2);
		params.put("proximalPermanenceIncrement",1.0);
		params.put("proximalPermanenceDecrement",-1.0);
		params.put("distalPermanenceIncrement",1.0);
		params.put("distalPositivePermanenceDecrement",-1.0);
		params.put("distalNegativePermanenceDecrement",-1.0);
		params.put("inhibitionRadiusChangeTolerance",0.0);
		params.put("predictedColumnOverlap",0.0);
		// <!-- Spatial pooler parameters -->
		params.put("proximalSynapsesFactor", 10.0);
		params.put("columnOverlapThreshold", 0.1);
		params.put("localColumnActivity", 10.0d);
		params.put("minColumnActivityPercentage", 0.01);
		params.put("columnHistorySize", 100);
		// <!-- Temporal pooler parameters -->
		params.put("distalDendriteActivationThreshold", 15);
		params.put("segmentLearningThreshold", 12);
		params.put("initialDistalSynapsePermanence", 0.1);
		params.put("maxNewDistalSynapsesPerUpdate", 5);
		params.put("distalLearningRadius", 10.0);
		// <!-- other params -->
		params.put("boostStrategyName", "DefaultBoostStrategy");
		params.put("exciteStrategyName", "defaultClaExcite");
		params.put("decayStrategyName", "defaultClaDecay");
		params.put("proximalSynapseSourceSigma", 1.5);
		params.put("proximalPermanenceSigma", 0.02);
		params.put("initialInhibitionRadius", 8.0);
		params.put("dendritesPerCell", 20);
		params.put("initialDistalSynapsesPerSegment", 10);
		params.put("initialSpatialPermanenceSigma", 0.02);
		params.put("negativePermanenceDecrement", -100.0);
	}

	public void initRegion() {
		GlobalInitializer.getInstance().setAttribute(
				"inputSignalDimensionality", inputSignalDimensionality);
		GlobalInitializer.getInstance().setAttribute("structuralPredictionThreshold",0.0);
		region.init(params);
		String name = "default";
		CorticalRegionDef def = new CorticalRegionDef(name);
		def.init(params);
		CorticalRegionFactory.getInstance().addCorticalRegionDef(name, def);
		region.initCorticalRegion(name, false);
	}

	@Test
	public void testCalculateInputColumnOverlap() {
		inputSignalDimensionality = 4;
		params.put("proximalSynapsesPerColumn", 10);
		params.put("columnOverlapThreshold", 0.1);
		initRegion();

		BitVector input = new BitVector(4);
		input.put(0, false);
		input.put(1, true);
		input.put(2, false);
		input.put(3, true);

		int historySize = 10;
		Column col = new ColumnImpl(historySize);
		col.setRegionHeightPosition(0);
		col.setRegionWidthPosition(0);

		Synapse s = new SynapseImpl();
		s.setSourceHeight(0);
		s.setSourceWidth(0);
		col.addPotentialSynapse(s);
		col.addConnectedSynapse(s);
		s = new SynapseImpl();
		s.setSourceHeight(0);
		s.setSourceWidth(1);
		col.addPotentialSynapse(s);
		col.addConnectedSynapse(s);
		s = new SynapseImpl();
		s.setSourceHeight(1);
		s.setSourceWidth(0);
		col.addPotentialSynapse(s);
		col.addConnectedSynapse(s);
		s = new SynapseImpl();
		s.setSourceHeight(1);
		s.setSourceWidth(1);
		col.addPotentialSynapse(s);
		col.addConnectedSynapse(s);

		Column[][] cols = { { col } };
		region.setColumns(cols);
		region.setInput(input);
		region.performSpatialPooling();

		assertEquals(0.5, col.getBoostedOverlap(), epsilon);
	}

	@Test
	public void testGetKthOverlap() {
		initRegion();
		region.setLocalColumnActivity(3.0);

		List<Column> columns = new ArrayList<Column>();
		int numCols = 10;
		for (int i = 0; i < numCols; i++) {
			Column c = new ColumnImpl();
			c.setBoostedOverlap(i);
			columns.add(c);
		}
		Column c = region.getKthColumn(columns, numCols);
		assertEquals(7, c.getBoostedOverlap(), epsilon);//3rd most active
	}
	
	@Test
	public void testGetKthOverlapA() {
		initRegion();
		region.setLocalColumnActivity(3.0);

		List<Column> columns = new ArrayList<Column>();
		double numCols = 10;
		for (int i = 0; i < numCols; i++) {
			Column c = new ColumnImpl();
			c.setBoostedOverlap(i);
			columns.add(c);
		}
		Column c = region.getKthColumn(columns, numCols*2); //columns is half the possible
		assertEquals(8, c.getBoostedOverlap(), epsilon);//2rd most active
		
		c = region.getKthColumn(columns, numCols*3); //columns is half the possible
		assertEquals(9, c.getBoostedOverlap(), epsilon);//1st most active
	}

	@Test
	public void testGetKthOverlap0() {
		initRegion();
		region.setLocalColumnActivity(3.0);

		List<Column> columns = new ArrayList<Column>();
		// No columns
		Column c = region.getKthColumn(columns, 10);
		assertNull(c);
	}

	@Test
	public void testGetKthOverlap1() {
		initRegion();
		region.setLocalColumnActivity(3.0);

		List<Column> columns = new ArrayList<Column>();
		// No columns
		Column c = region.getKthColumn(columns, 10);
		assertNull(c);
	}
	
	@Test
	public void testGetKthOverlap2() {
		initRegion();
		region.setLocalColumnActivity(3.0);

		List<Column> columns = new ArrayList<Column>();
		int numCols = 10;
		for (int i = 0; i < numCols; i++) {
			Column c = new ColumnImpl();
			c.setBoostedOverlap(i);
			columns.add(c);
		}
		// 0 neighbors columns
		Column c = region.getKthColumn(columns, 0);
		assertNull(c);
	}

	@Test
	public void testGetKthOverlap3() {
		initRegion();
		region.setLocalColumnActivity(10.0);

		List<Column> columns = new ArrayList<Column>();
		int numCols = 10;
		for (int i = 0; i < numCols; i++) {
			Column c = new ColumnImpl();
			c.setBoostedOverlap(i);
			columns.add(c);
		}
		Column c = region.getKthColumn(columns, numCols);
		assertNotNull(c);
		assertEquals(0.0, c.getBoostedOverlap(), epsilon);//weakest column is kth
	}

	@Test
	public void testCalculateInterColumnInhibition() {
		inputSignalDimensionality = 4;
		params.put("regionProjectionFactor", 1.0);
		params.put("localColumnActivity", 2.0);
		initRegion();

		Column[][] cols = region.getColumns();
		Synapse s = new SynapseImpl();
		s.setSourceHeight(1000);
		s.setSourceWidth(1000);
		cols[0][0].addPotentialSynapse(s);
		cols[0][0].addConnectedSynapse(s);
		cols[1][0].addPotentialSynapse(s);
		cols[1][0].addConnectedSynapse(s);
		cols[0][1].addPotentialSynapse(s);
		cols[0][1].addConnectedSynapse(s);
		cols[1][1].addPotentialSynapse(s);
		cols[1][1].addConnectedSynapse(s);
		BitVector v = new BitVector(inputSignalDimensionality);
		region.setupForCycle(v);
		
		cols[0][0].setBoostedOverlap(3);
		cols[0][1].setBoostedOverlap(2);
		cols[1][0].setBoostedOverlap(1);
		cols[1][1].setBoostedOverlap(0);
		Collection<Column> collCols = new ArrayList<Column>();
		for (Column[] a : Arrays.asList(cols)) {
			collCols.addAll(Arrays.asList(a));
		}
		region.calculateInterColumnInhibition(collCols);
		Collection<Column> columnCollection = region.getActiveColumns();
		assertEquals(2, columnCollection.size());
		for (Column c : columnCollection) {
			assertTrue(c.getBoostedOverlap() >= 2);
		}
	}

	@Test
	public void testCalculateInterColumnInhibition1() {
		inputSignalDimensionality = 4;
		params.put("regionProjectionFactor", 1.0);
		params.put("localColumnActivity", 1.0);
		initRegion();

		Column[][] cols = region.getColumns();
		Synapse s = new SynapseImpl();
		s.setSourceHeight(1000);
		s.setSourceWidth(1000);
		cols[0][0].addPotentialSynapse(s);
		cols[0][0].addConnectedSynapse(s);
		cols[1][0].addPotentialSynapse(s);
		cols[1][0].addConnectedSynapse(s);
		cols[0][1].addPotentialSynapse(s);
		cols[0][1].addConnectedSynapse(s);
		cols[1][1].addPotentialSynapse(s);
		cols[1][1].addConnectedSynapse(s);
		BitVector v = new BitVector(inputSignalDimensionality);
		region.setupForCycle(v);
		
		cols[0][0].setBoostedOverlap(3);
		cols[0][1].setBoostedOverlap(2);
		cols[1][0].setBoostedOverlap(1);
		cols[1][1].setBoostedOverlap(0);
		Collection<Column> collCols = new ArrayList<Column>();
		for (Column[] a : Arrays.asList(cols)) {
			collCols.addAll(Arrays.asList(a));
		}
		region.calculateInterColumnInhibition(collCols);
		Collection<Column> columnCollection = region.getActiveColumns();
		assertEquals(1, columnCollection.size());
	}

	@Test
	public void testCalculateInterColumnInhibition2() {
		inputSignalDimensionality = 4;
		params.put("localColumnActivity", 5.0d);
		params.put("columnsPerInput", 1.0);
		initRegion();
		region.setInhibitionRadius(1);

		Column[][] cols = { { new ColumnImpl(), new ColumnImpl() },
				{ new ColumnImpl(), new ColumnImpl() } };
		cols[0][0].setBoostedOverlap(3);
		cols[0][1].setBoostedOverlap(2);
		cols[1][0].setBoostedOverlap(0.1);
		cols[1][1].setBoostedOverlap(0);// won't consider columns with 0 overlap
		Collection<Column> collCols = new ArrayList<Column>();
		for (Column[] a : Arrays.asList(cols)) {
			collCols.addAll(Arrays.asList(a));
		}

		region.setColumns(cols);
		region.calculateInterColumnInhibition(collCols);
		Collection<Column> columnCollection = region.getActiveColumns();
		assertEquals(0, columnCollection.size());
	}

	@Test
	public void testGetNeighbors() {
		inputSignalDimensionality = 100;
		params.put("columnsPerInput", 1.0);
		initRegion();
		Column[][] regColumns = region.getColumns();
		double inhibitionRadius = 8.0;
		Column c = new ColumnImpl(0, 0);
		int size = ClaUtils.getNeighbors(c, inhibitionRadius, regColumns).size();
		assertEquals(58, size);

		c = new ColumnImpl(9, 9);
		size = ClaUtils.getNeighbors(c, inhibitionRadius, regColumns).size();
		assertEquals(58, size);

		c = new ColumnImpl(0, 9);
		size = ClaUtils.getNeighbors(c, inhibitionRadius, regColumns).size();
		assertEquals(58, size);

		c = new ColumnImpl(9, 0);
		size = ClaUtils.getNeighbors(c, inhibitionRadius, regColumns).size();
		assertEquals(58, size);

		c = new ColumnImpl(4, 4);
		size = ClaUtils.getNeighbors(c, inhibitionRadius, regColumns).size();
		assertEquals(100, size);
	}

	@Test
	public void testGetNeighbors1() {
		int width = 15;
		int height = 15;
		params.put("regionWidth", width);
		params.put("regionHeight", height);
		initRegion();
		double radius = height / 2 - 1.0;
		Column c = new ColumnImpl(height / 2, width / 2);

		int size = ClaUtils.getNeighbors(c, radius, region.getColumns()).size();
		double expected = Math.PI * radius * radius;
		assertTrue(expected > size);
	}

	@Test
	public void testPerformSpatialLearning() {
		inputSignalDimensionality = 4;
		params.put("regionWidth", 1);
		params.put("regionHeight", 1);
		params.put("exciteStrategyName", "defaultClaExcite");
		initRegion();

		BitVector input = new BitVector(4);
		input.put(0, true);
		input.put(1, true);
		input.put(2, false);
		input.put(3, true);

		region.setInput(input);
		int v = 0;
		Synapse[] synapses = { new SynapseImpl(), new SynapseImpl(),
				new SynapseImpl(), new SynapseImpl() };

		Column cols[][] = { { new ColumnImpl() } };
		for (Synapse syn : synapses) {
			syn.setSourceHeight(v % 2);
			syn.setSourceWidth((v / 2) % 2);
			v++;
			cols[0][0].addPotentialSynapse(syn);
			syn.setPermanence(0.5);
		}
		assertEquals(4, cols[0][0].getPotentialSynapseCount());
		Collection<Column> collColumns = new ArrayList<Column>();
		collColumns.add(cols[0][0]);
		region.setActiveColumns(collColumns);
		region.performSpatialLearning();

		double result[] = { 0.55, 0.55, 0.45, 0.55 };
		Collection<Synapse> syns = cols[0][0].getPotentialSynapses();
		assertEquals(4, syns.size());
		for (Synapse syn : syns) {
			assertEquals(result[syn.getSourceHeightPos() * 2
					+ syn.getSourceWidthPos()], syn.getPermanence(), epsilon);
		}
	}

	@Test
	public void testUpdateColumnBoosting() {
		params.put("columnOverlapThreshold", 1.0);
		params.put("synapseConnectionThresholdPercentage", 0.1);
		params.put("averageColumnActivityPercentage", 0.1);
		initRegion();
		int historySize = 10;
		Column[][] cols = {{ new ColumnImpl(historySize), new ColumnImpl(historySize) },
					{ new ColumnImpl(historySize), new ColumnImpl(historySize) } };
		cols[0][0].updateActiveHistory(true);
		cols[0][0].updateActiveHistory(false);
		cols[1][0].updateActiveHistory(false);
		cols[0][1].updateActiveHistory(false);
		cols[1][1].updateActiveHistory(false);

		Collection<Column> collCols = new ArrayList<Column>();
		collCols.add(cols[0][0]);
		collCols.add(cols[1][0]);
		collCols.add(cols[0][1]);
		collCols.add(cols[1][1]);
		region.setActiveColumns(collCols);

		cols[1][1].setBoostedOverlap(2);
		cols[1][1].updateOverlapHistory(false);
		region.updateColumnBoosting(cols[1][1]);

		assertEquals(1.0, cols[1][1].getBoost(), epsilon);
		assertEquals(0.5, cols[1][1].getAverageOverlap(), epsilon);
	}

	@Test
	public void testBoostProximalSynapses1() {
		params.put("columnOverlapThreshold", 1);
		params.put("synapseConnectionThresholdPercentage", 0.1);
		initRegion();

		Column c = new ColumnImpl();
		c.setHistorySize(1);
		Synapse s = new SynapseImpl();
		s.setPermanence(0.123);
		c.addPotentialSynapse(s);

		c.setBoostedOverlap(2.0);
		double minAverageColumnActivity = 0.0;
		region.boostProxmialSynapses(c, minAverageColumnActivity);
		assertEquals(1.0, c.getAverageOverlap(), epsilon);
		assertEquals(0.123, s.getPermanence(), epsilon);
	}

	@Test
	public void testBoostProximalSynapses2() {
		params.put("columnOverlapThreshold", 1.0);
		params.put("synapseConnectionThreshold", 0.5);
		params.put("synapseConnectionThresholdPercentage", 0.1);
		initRegion();

		Column c = new ColumnImpl();
		c.setHistorySize(10);
		Synapse s = new SynapseImpl();
		s.setPermanence(0.123);
		c.addPotentialSynapse(s);
		c.setBoostedOverlap(0.0);

		double minAverageColumnActivity = 0.5;
		region.boostProxmialSynapses(c, minAverageColumnActivity);
		assertEquals(0.0, c.getAverageOverlap(), epsilon);
		assertEquals(0.123 + 0.05, s.getPermanence(), epsilon);
	}

	@Test
	public void testUpdatePermanence() {
		params.put("synapseConnectionThreshold", 0.5);
		initRegion();

		double p = 0.49;
		double m = 0.05;//lazy
		Synapse s = new SynapseImpl();
		DendriteSegment ds = new DendriteSegmentImpl();
		ds.addPotentialSynapse(s);
		region.updatePermanence(s, ds, p);
		assertEquals(p*m, s.getPermanence(), epsilon);
		assertEquals(0, ds.getConnectedSynapses().size());
	}

	@Test
	public void testUpdatePermanence1() {
		params.put("synapseConnectionThreshold", 0.5);
		initRegion();

		double amount = 21.0;
//		double m = 0.05;//lazy
		Synapse s = new SynapseImpl();
		DendriteSegment ds = new DendriteSegmentImpl();
		ds.addPotentialSynapse(s);
		region.updatePermanence(s, ds, amount);
		assertEquals(1.0, s.getPermanence(), epsilon);
		assertEquals(1, ds.getConnectedSynapses().size());
	}

	@Test
	public void testUpdatePermanence2() {
		params.put("synapseConnectionThreshold", 0.5);
		initRegion();

		double amount = 100;
		Synapse s = new SynapseImpl();
		DendriteSegment ds = new DendriteSegmentImpl();
		ds.addPotentialSynapse(s);
		region.updatePermanence(s, ds, amount);
		assertEquals(1.0, s.getPermanence(), epsilon);
		assertEquals(1, ds.getConnectedSynapses().size());
	}

	@Test
	public void testUpdatePermanence3() {
		params.put("synapseConnectionThreshold", 0.5);
		initRegion();

		double amount = -10;
		Synapse s = new SynapseImpl();
		DendriteSegment ds = new DendriteSegmentImpl();
		ds.addPotentialSynapse(s);
		ds.addConnectedSynapse(s);
		region.updatePermanence(s, ds, amount);
		assertEquals(0.0, s.getPermanence(), epsilon);
		assertEquals(0, ds.getConnectedSynapses().size());
		assertEquals(0, ds.getPotentialSynapses().size());
	}

	@Test
	public void testUpdatePermanence4() {
		params.put("synapseConnectionThreshold", 0.5);
		initRegion();

		Synapse s = new SynapseImpl();
		DendriteSegment ds = new DendriteSegmentImpl();
		ds.addPotentialSynapse(s);
		ds.addConnectedSynapse(s);
		double m = 0.05;
		double amount = 10.1; // Fix these m's
		region.updatePermanence(s, ds, amount);
		assertEquals(amount*m, s.getPermanence(), epsilon);
		assertEquals(1, ds.getConnectedSynapses().size());
		assertEquals(1, ds.getPotentialSynapses().size());

		s.setPermanence(0.5);
		region.updatePermanence(s, ds, -1);
		assertEquals(0.5-0.05, s.getPermanence(), epsilon);
		assertEquals(0, ds.getConnectedSynapses().size());
		assertEquals(1, ds.getPotentialSynapses().size());
	}

	@Test
	public void testUpdatePermanence5() {
		params.put("synapseConnectionThreshold", 0.5);
		initRegion();

		Synapse s = new SynapseImpl();
		DendriteSegment ds = new DendriteSegmentImpl();
		ds.addPotentialSynapse(s);
		region.updatePermanence(s, ds, -1);
		assertEquals(0.0, s.getPermanence(), epsilon);
		assertEquals(0, ds.getConnectedSynapses().size());
		assertEquals(0, ds.getPotentialSynapses().size());
	}

	@Test
	public void testGetAverageConnectedSynapseDistance() {
		inputSignalDimensionality = 100;
		params.put("regionProjectionFactor", 1.0);
		initRegion();

		Column c = region.getColumn(1, 1);//only test 0,0
		for (Synapse s : c.getConnectedSynapses()) {
			c.removeConnectedSynapse(s);
		}
		assertEquals(0, region.getAverageConnectedSynapseDistance(c), epsilon);
		// verifying col's input positions
		assertEquals(1, c.getInputHeightPosition(), epsilon);
		assertEquals(1, c.getInputWidthPosition(), epsilon);
		Synapse s = new SynapseImpl();
		s.setSourceHeight(2);
		s.setSourceWidth(2);
		c.addPotentialSynapse(s);
		c.addConnectedSynapse(s);
		assertEquals(Math.sqrt(2), region
				.getAverageConnectedSynapseDistance(c), epsilon);

		s = new SynapseImpl();
		s.setSourceHeight(3);
		s.setSourceWidth(1);
		c.addPotentialSynapse(s);
		c.addConnectedSynapse(s);
		assertEquals((Math.sqrt(2) + 2) / 2, region
				.getAverageConnectedSynapseDistance(c), epsilon);
	}

	// temporal pooler
	@Test
	public void testGetActiveSynapses() {
		DendriteSegment ds = new DendriteSegmentImpl();
		Cell[][][] cells = TestUtils.getCells(2,2,2);
		cells[0][0][0].setActiveCurrently(true);
		cells[1][1][0].setActiveCurrently(true);
		cells[0][0][1].setActiveCurrently(false);
		cells[1][1][1].setActiveCurrently(true);
		region.setCells(cells);
		// connected and active input
		Synapse s1 = new SynapseImpl();
		s1.setSourceHeight(0);
		s1.setSourceWidth(0);
		s1.setSourceColumn(0);
		ds.addPotentialSynapse(s1);
		ds.addConnectedSynapse(s1);
		// connected and active input
		Synapse s2 = new SynapseImpl();
		s2.setSourceHeight(1);
		s2.setSourceWidth(1);
		s2.setSourceColumn(0);
		ds.addPotentialSynapse(s2);
		ds.addConnectedSynapse(s2);
		// connected but input not active
		Synapse s3 = new SynapseImpl();
		s3.setSourceHeight(0);
		s3.setSourceWidth(0);
		s3.setSourceColumn(1);
		ds.addPotentialSynapse(s3);
		ds.addConnectedSynapse(s3);
		// active input but connected
		Synapse s4 = new SynapseImpl();
		s4.setSourceHeight(1);
		s4.setSourceWidth(1);
		s4.setSourceColumn(1);
		ds.addPotentialSynapse(s4);

		Collection<Synapse> actives = region.getActiveSynapses(ds.getConnectedSynapses(), 
																CellState.ActiveCurrent);
		assertEquals(2, actives.size());
		assertTrue(actives.contains(s1));
		assertTrue(actives.contains(s2));
		assertFalse(actives.contains(s3));
		assertFalse(actives.contains(s4));
	}

	@Test
	public void testGetActiveSegment() {
		params.put("distalDendriteActivationThreshold", 1);
		initRegion();
		Collection<DendriteSegment> c = new ArrayList<DendriteSegment>();
		Cell[][][] cells = TestUtils.getCells(2,2,2);
		region.setCells(cells);

		DendriteSegment ds = new DendriteSegmentImpl();
		Synapse s1 = new SynapseImpl();
		s1.setSourceHeight(0);
		s1.setSourceWidth(0);
		s1.setSourceColumn(0);
		ds.addPotentialSynapse(s1);

		DendriteSegment ds2 = new DendriteSegmentImpl();
		c.add(ds);
		c.add(ds2);
		assertNull(region.getBest1OrderPredictingSegment(c));
	}

	@Test
	public void testGetActiveSegment1() {
		params.put("distalDendriteActivationThreshold", 0);
		initRegion();
		Collection<DendriteSegment> c = new ArrayList<DendriteSegment>();
		
		Cell[][][] cells = TestUtils.getCells(2,2,2);
		cells[0][0][0].setActivePreviously(true);
		cells[1][1][1].setActivePreviously(true);
		region.setCells(cells);

		Synapse s1 = new SynapseImpl();
		s1.setSourceHeight(0);
		s1.setSourceWidth(0);
		s1.setSourceColumn(0);

		DendriteSegment ds = new DendriteSegmentImpl();
		ds.addPotentialSynapse(s1);
		ds.addConnectedSynapse(s1);

		DendriteSegment ds2 = new DendriteSegmentImpl();
		c.add(ds);
		c.add(ds2);

		assertEquals(ds, region.getBest1OrderPredictingSegment(c));
	}

	@Test
	public void testGetActiveSegment2() {
		// two segments, one with 2 non-predicting synapses and the other with 1.
		params.put("distalDendriteActivationThreshold", 1);
		initRegion();
		Collection<DendriteSegment> c = new ArrayList<DendriteSegment>();
		Cell[][][] cells = TestUtils.getCells(2,2,2);
		cells[0][0][0].setActivePreviously(true);
		cells[1][1][1].setActivePreviously(true);
		region.setCells(cells);

		Synapse s1 = new SynapseImpl();
		s1.setSourceHeight(0);
		s1.setSourceWidth(0);
		s1.setSourceColumn(0);

		Synapse s2 = new SynapseImpl();
		s2.setSourceHeight(1);
		s2.setSourceWidth(1);
		s2.setSourceColumn(1);

		DendriteSegment ds = new DendriteSegmentImpl();
		ds.addPotentialSynapse(s1);
		ds.addConnectedSynapse(s1);
		ds.addPotentialSynapse(s2);
		ds.addConnectedSynapse(s2);

		DendriteSegment ds2 = new DendriteSegmentImpl();
		ds.addPotentialSynapse(s1);
		ds.addConnectedSynapse(s1);

		c.add(ds);
		c.add(ds2);

		assertEquals(ds, region.getBest1OrderPredictingSegment(c));
	}

	@Test
	public void testGetActiveSegment3() {
		params.put("distalDendriteActivationThreshold", 1);
		initRegion();
		Collection<DendriteSegment> c = new ArrayList<DendriteSegment>();
		
		Cell[][][] cells = TestUtils.getCells(2,2,2);
		cells[0][0][0].setActivePreviously(true);
		cells[1][1][1].setActivePreviously(true);
		cells[1][0][1].setActivePreviously(true);
		region.setCells(cells);

		Synapse s1 = new SynapseImpl();
		s1.setSourceHeight(0);
		s1.setSourceWidth(0);
		s1.setSourceColumn(0);

		Synapse s2 = new SynapseImpl();
		s2.setSourceHeight(1);
		s2.setSourceWidth(1);
		s2.setSourceColumn(1);

		Synapse s3 = new SynapseImpl();
		s3.setSourceHeight(1);
		s3.setSourceWidth(0);
		s3.setSourceColumn(1);

		DendriteSegment ds = new DendriteSegmentImpl();
		ds.setPredictionTime(100);
		ds.addPotentialSynapse(s1);
		ds.addConnectedSynapse(s1);
		ds.addPotentialSynapse(s2);
		ds.addConnectedSynapse(s2);
		ds.addPotentialSynapse(s3);
		ds.addConnectedSynapse(s3);

		DendriteSegment ds2 = new DendriteSegmentImpl();
		ds2.setPredictionTime(1);
		ds2.addPotentialSynapse(s1);
		ds2.addConnectedSynapse(s1);
		ds2.addPotentialSynapse(s2);
		ds2.addConnectedSynapse(s2);

		c.add(ds);
		c.add(ds2);

		assertEquals(ds2, region.getBest1OrderPredictingSegment(c));

		DendriteSegment ds3 = new DendriteSegmentImpl();
		ds3.addPotentialSynapse(s3);
		ds3.addConnectedSynapse(s3);
		c.add(ds3);

		assertEquals(ds2, region.getBest1OrderPredictingSegment(c));
	}

	@Test
	public void testGetBestPredictedCell() {
		int height = 1;
		int width = 1;
		int columnSize = 3;
		int threshold = 1;
		inputSignalDimensionality = 1;
		params.put("columnsPerInput", 1.0);
		params.put("regionHeight", height);
		params.put("regionWidth", width);
		params.put("cellsPerColumn", columnSize);
		params.put("segmentLearningThreshold", threshold);
		initRegion();
		
		Cell[][][] cells = TestUtils.getCells(height,width,columnSize);
		cells[0][0][0].setActivePreviously(true);
		cells[0][0][1].setActivePreviously(true);
		region.setCells(cells);

		Synapse s1 = new SynapseImpl();
		s1.setSourceHeight(0);
		s1.setSourceWidth(0);
		s1.setSourceColumn(0);
		Synapse s2 = new SynapseImpl();
		s2.setSourceHeight(0);
		s2.setSourceWidth(0);
		s2.setSourceColumn(1);

		DendriteSegment ds = new DendriteSegmentImpl();
		ds.addPotentialSynapse(s1);
		ds.addConnectedSynapse(s1);
		ds.addPotentialSynapse(s2);
		ds.addConnectedSynapse(s2);

		DendriteSegment ds1 = new DendriteSegmentImpl();
		ds1.addPotentialSynapse(s1);
		ds1.addConnectedSynapse(s1);

		Collection<DendriteSegment> segments = new ArrayList<DendriteSegment>();
		region.setCell(0, 0, 0, segments);
		segments = new ArrayList<DendriteSegment>();
		segments.add(ds1);
		region.setCell(0, 0, 1, segments);
		segments = new ArrayList<DendriteSegment>();
		segments.add(ds1);
		segments.add(ds);
		region.setCell(0, 0, 2, segments);

		Object[] results = region.getBestPredictedCell(0, 0);
		assertEquals(2, results[0]);
		assertEquals(ds, results[1]);
	}

	@Test
	public void testGetBestPredictedCell1() {
		int height = 1;
		int width = 1;
		int columnSize = 3;
		int threshold = 0;
		params.put("regionHeight", height);
		params.put("regionWidth", width);
		params.put("cellsPerColumn", columnSize);
		params.put("segmentLearningThreshold", threshold);
		initRegion();

		Cell[][][] cells = TestUtils.getCells(height,width,columnSize);
		cells[0][0][0].setActivePreviously(true);
		cells[0][0][1].setActivePreviously(true);
		region.setCells(cells);

		Synapse s1 = new SynapseImpl();
		s1.setSourceHeight(0);
		s1.setSourceWidth(0);
		s1.setSourceColumn(0);
		Synapse s2 = new SynapseImpl();
		s2.setSourceHeight(0);
		s2.setSourceWidth(0);
		s2.setSourceColumn(1);

		DendriteSegment ds1 = new DendriteSegmentImpl();
		ds1.addPotentialSynapse(s1);
		ds1.addConnectedSynapse(s1);

		Collection<DendriteSegment> segments = new ArrayList<DendriteSegment>();
		region.setCell(0, 0, 0, segments);
		segments = new ArrayList<DendriteSegment>();
		segments.add(ds1);
		region.setCell(0, 0, 1, segments);
		segments = new ArrayList<DendriteSegment>();
		region.setCell(0, 0, 2, segments);

		Object[] results = region.getBestPredictedCell(0, 0);
		assertEquals(1, results[0]);
		assertEquals(ds1, results[1]);
	}

	@Test
	public void testGetBestPredictedCell2() {
		int height = 1;
		int width = 1;
		int columnSize = 3;
		int threshold = 1;
		params.put("regionHeight", height);
		params.put("regionWidth", width);
		params.put("cellsPerColumn", columnSize);
		params.put("segmentLearningThreshold", threshold);
		initRegion();

		Cell[][][] cells = TestUtils.getCells(height,width,columnSize);
		cells[0][0][0].setActivePreviously(true);
		cells[0][0][1].setActivePreviously(true);
		region.setCells(cells);
		
		Synapse s1 = new SynapseImpl();
		s1.setSourceHeight(0);
		s1.setSourceWidth(0);
		s1.setSourceColumn(0);
		Synapse s2 = new SynapseImpl();
		s2.setSourceHeight(0);
		s2.setSourceWidth(0);
		s2.setSourceColumn(1);

		DendriteSegment ds1 = new DendriteSegmentImpl();
		ds1.addPotentialSynapse(s1);
		ds1.addConnectedSynapse(s1);

		Collection<DendriteSegment> segments = new ArrayList<DendriteSegment>();
		region.setCell(0, 0, 0, segments);
		segments = new ArrayList<DendriteSegment>();
		region.setCell(0, 0, 1, segments);
		segments = new ArrayList<DendriteSegment>();
		region.setCell(0, 0, 2, segments);

		Object[] results = region.getBestPredictedCell(0, 0);
		assertEquals(-1, results[0]);
		assertNull(results[1]);
	}

	@Test
	public void testGetCellMinSegments() {
		int height = 1;
		int width = 1;
		int columnSize = 3;
		int threshold = 1;
		inputSignalDimensionality = 1;
		params.put("columnsPerInput", 1.0);
		params.put("regionHeight", height);
		params.put("regionWidth", width);
		params.put("cellsPerColumn", columnSize);
		params.put("segmentLearningThreshold", threshold);
		initRegion();

		Cell[][][] cells = TestUtils.getCells(height,width,columnSize);
		cells[0][0][0].setActivePreviously(true);
		cells[0][0][1].setActivePreviously(true);
		region.setCells(cells);

		Synapse s1 = new SynapseImpl();
		s1.setSourceHeight(0);
		s1.setSourceWidth(0);
		s1.setSourceColumn(0);
		Synapse s2 = new SynapseImpl();
		s2.setSourceHeight(0);
		s2.setSourceWidth(0);
		s2.setSourceColumn(1);

		DendriteSegment ds = new DendriteSegmentImpl();
		ds.addPotentialSynapse(s1);
		ds.addConnectedSynapse(s1);
		ds.addPotentialSynapse(s2);
		ds.addConnectedSynapse(s2);
		DendriteSegment ds1 = new DendriteSegmentImpl();
		ds1.addPotentialSynapse(s1);
		ds1.addConnectedSynapse(s1);
		DendriteSegment ds2 = new DendriteSegmentImpl();

		Collection<DendriteSegment> segments = new ArrayList<DendriteSegment>();
		segments.add(ds);
		segments.add(ds2);
		region.setCell(0, 0, 0, segments);

		segments = new ArrayList<DendriteSegment>();
		segments.add(ds1);
		segments.add(ds2);
		region.setCell(0, 0, 1, segments);

		segments = new ArrayList<DendriteSegment>();
		segments.add(ds1);
		segments.add(ds);
		segments.add(ds2);
		region.setCell(0, 0, 2, segments);

		Object[] results = region.getCellMinSegments(0, 0);
		assertEquals(0, results[0]);
		assertEquals(ds, results[1]);
	}

	@Test
	public void testGetCellMinSegments1() {
		int height = 1;
		int width = 1;
		int columnSize = 3;
		int threshold = 1;
		inputSignalDimensionality = 1;
		params.put("columnsPerInput", 1.0);
		params.put("regionHeight", height);
		params.put("regionWidth", width);
		params.put("cellsPerColumn", columnSize);
		params.put("segmentLearningThreshold", threshold);
		initRegion();

		Cell[][][] cells = TestUtils.getCells(height,width,columnSize);
		cells[0][0][0].setActivePreviously(true);
		cells[0][0][1].setActivePreviously(true);
		region.setCells(cells);

		Synapse s1 = new SynapseImpl();
		s1.setSourceHeight(0);
		s1.setSourceWidth(0);
		s1.setSourceColumn(0);
		Synapse s2 = new SynapseImpl();
		s2.setSourceHeight(0);
		s2.setSourceWidth(0);
		s2.setSourceColumn(1);

		DendriteSegment ds = new DendriteSegmentImpl();
		ds.addPotentialSynapse(s1);
		ds.addConnectedSynapse(s1);
		ds.addPotentialSynapse(s2);
		ds.addConnectedSynapse(s2);
		DendriteSegment ds1 = new DendriteSegmentImpl();
		ds1.addPotentialSynapse(s1);
		ds1.addConnectedSynapse(s1);
		DendriteSegment ds2 = new DendriteSegmentImpl();

		Collection<DendriteSegment> segments = new ArrayList<DendriteSegment>();
		segments.add(ds2);
		region.setCell(0, 0, 0, segments);

		segments = new ArrayList<DendriteSegment>();
		region.setCell(0, 0, 1, segments);

		segments = new ArrayList<DendriteSegment>();
		segments.add(ds1);
		region.setCell(0, 0, 2, segments);

		Object[] results = region.getCellMinSegments(0, 0);
		assertEquals(1, results[0]);
		assertEquals(null, results[1]);
	}

	@Test
	public void testGetBestMatchingSegment() {
		int threshold = 0;
		params.put("segmentLearningThreshold", threshold);
		initRegion();

		Cell[][][] cells = TestUtils.getCells(2,2,2);
		cells[0][0][0].setActivePreviously(true);
		region.setCells(cells);
		
		Collection<DendriteSegment> segments = new ArrayList<DendriteSegment>();
		DendriteSegment ds = new DendriteSegmentImpl();
		segments.add(ds);
		DendriteSegment ds2 = new DendriteSegmentImpl();
		Synapse s = new SynapseImpl();
		s.setSourceColumn(0);
		s.setSourceHeight(0);
		s.setSourceWidth(0);
		ds2.addPotentialSynapse(s);
		ds2.addConnectedSynapse(s);
		segments.add(ds2);

		assertEquals(ds2, region.getBestPreviousActiveSegment(segments));
	}

	@Test
	public void testGetBestMatchingSegment1() {
		int threshold = 1;
		params.put("segmentLearningThreshold", threshold);
		initRegion();
		
		Cell[][][] cells = TestUtils.getCells(2,2,2);
		cells[0][0][0].setActivePreviously(true);
		region.setCells(cells);

		Collection<DendriteSegment> segments = new ArrayList<DendriteSegment>();
		assertNull(region.getBestPreviousActiveSegment(segments));
	}

	@Test
	public void testGetBestMatchingSegment2() {
		int threshold = 1;
		params.put("segmentLearningThreshold", threshold);
		initRegion();

		Cell[][][] cells = TestUtils.getCells(2,2,2);
		region.setCells(cells);
		
		Collection<DendriteSegment> segments = new ArrayList<DendriteSegment>();
		// Dendrite segment whose synapses have no activity
		DendriteSegment ds2 = new DendriteSegmentImpl();
		Synapse s = new SynapseImpl();
		s.setSourceColumn(0);
		s.setSourceHeight(0);
		s.setSourceWidth(0);
		ds2.addPotentialSynapse(s);
		ds2.addConnectedSynapse(s);
		segments.add(ds2);

		assertNull(region.getBestPreviousActiveSegment(segments));
	}

	@Test
	public void testGetBestMatchingSegment3() {
		int threshold = 1;
		params.put("segmentLearningThreshold", threshold);
		initRegion();

		Cell[][][] cells = TestUtils.getCells(2,2,2);
		cells[0][0][0].setActivePreviously(true);
		cells[1][1][1].setActivePreviously(true);
		region.setCells(cells);
		
		Collection<DendriteSegment> segments = new ArrayList<DendriteSegment>();
		Synapse s = new SynapseImpl();
		s.setSourceColumn(0);
		s.setSourceHeight(0);
		s.setSourceWidth(0);
		Synapse s2 = new SynapseImpl();
		s2.setSourceColumn(1);
		s2.setSourceHeight(1);
		s2.setSourceWidth(1);

		DendriteSegment ds = new DendriteSegmentImpl();
		ds.addPotentialSynapse(s);
		ds.addConnectedSynapse(s);

		DendriteSegment ds2 = new DendriteSegmentImpl();
		ds2.addPotentialSynapse(s);
		ds2.addConnectedSynapse(s);
		ds2.addPotentialSynapse(s2);
		ds2.addConnectedSynapse(s2);

		segments.add(ds);
		segments.add(ds2);

		assertEquals(ds2, region.getBestPreviousActiveSegment(segments));
	}

	@Test
	public void testGetBestMatchingSegment4() {
		int threshold = 1;
		params.put("segmentLearningThreshold", threshold);
		initRegion();

		Cell[][][] cells = TestUtils.getCells(2,2,2);
		cells[0][0][0].setActivePreviously(true);
		cells[1][1][1].setActivePreviously(true);
		region.setCells(cells);
		
		Collection<DendriteSegment> segments = new ArrayList<DendriteSegment>();
		Synapse s = new SynapseImpl();
		s.setSourceColumn(0);
		s.setSourceHeight(0);
		s.setSourceWidth(0);
		Synapse s2 = new SynapseImpl();
		s2.setSourceColumn(1);
		s2.setSourceHeight(1);
		s2.setSourceWidth(1);

		DendriteSegment ds = new DendriteSegmentImpl();
		ds.addPotentialSynapse(s);
		ds.addConnectedSynapse(s);
		// ds2 wins despite only having active *potential* synapses
		DendriteSegment ds2 = new DendriteSegmentImpl();
		ds2.addPotentialSynapse(s);
		ds2.addPotentialSynapse(s2);

		segments.add(ds);
		segments.add(ds2);

		assertEquals(ds2, region.getBestPreviousActiveSegment(segments));
	}

	@Test
	public void testStoreSegmentUpdate() {
		inputSignalDimensionality = 1;
		params.put("inputProjectionFactor", 1.0);
		params.put("regionProjectionFactor", 4.0); //2x2 columns
		int columnSize = 2;
		params.put("cellsPerColumn", columnSize);
		params.put("predictionOrderLimit", 5);
		initRegion();

		DendriteSegment seg = new DendriteSegmentImpl();
		int h = 1; //cell coordinates
		int w = 1;
		int c = 1;
		
		Cell[][][] cells = TestUtils.getCells(2,2,2);
		cells[0][0][0].setActiveCurrently(true);
		region.setCells(cells);
		
		Synapse s = new SynapseImpl();
		s.setSourceColumn(0);
		s.setSourceHeight(0);
		s.setSourceWidth(0);
		seg.addPotentialSynapse(s);
		seg.addConnectedSynapse(s);

		int predictionOrder = 3;
		SegmentUpdate u = region.storeSegmentUpdate(SegmentUpdateType.ACTIVE_PREDICTION, seg, 
													predictionOrder,predictionOrder,
													h, w, c);
		List<Collection<SegmentUpdate>> updates = region.getPotentialSegmentUpdates();
		Collection<SegmentUpdate> nextUpdates = updates.get(predictionOrder);

		assertEquals(u, nextUpdates.iterator().next());
		assertEquals(1, nextUpdates.size());
		assertEquals(seg, u.getDendriteSegment());
		assertEquals(1, u.getActiveSynapses().size());
		assertEquals(s, u.getActiveSynapses().iterator().next());
	}

	@Test
	public void testStoreSegmentUpdate1() {
		inputSignalDimensionality = 1;
		params.put("inputProjectionFactor", 1.0);
		params.put("regionProjectionFactor", 4.0); //2x2 columns
		int columnSize = 2;
		params.put("cellsPerColumn", columnSize);
		params.put("predictionOrderLimit", 5);
		initRegion();

		DendriteSegment seg = new DendriteSegmentImpl();
		int h = 1;
		int w = 1;
		int c = 1;
		
		Cell[][][] cells = TestUtils.getCells(2,2,2);
		cells[0][0][0].setActivePreviously(true);
		region.setCells(cells);
		
		Synapse s = new SynapseImpl();
		s.setSourceColumn(0);
		s.setSourceHeight(0);
		s.setSourceWidth(0);
		seg.addPotentialSynapse(s);
		seg.addConnectedSynapse(s);

		int predictionOrder = 1;
		SegmentUpdate u = region.storeSegmentUpdate(SegmentUpdateType.NEW_FIRST_ORDER, seg, 
													predictionOrder, 0, 
													h, w, c);
		List<Collection<SegmentUpdate>> allUpdates = region.getPotentialSegmentUpdates();
		Collection<SegmentUpdate> updates = allUpdates.get(0);
		
		assertEquals(u, updates.iterator().next());
		assertEquals(1, updates.size());
		assertEquals(seg, u.getDendriteSegment());
		assertEquals(1, u.getActiveSynapses().size());
		assertEquals(s, u.getActiveSynapses().iterator().next());
	}
	
	@Test
	public void testStoreSegmentUpdate2() {
		inputSignalDimensionality = 1;
		params.put("inputProjectionFactor", 1.0);
		params.put("regionProjectionFactor", 4.0); //2x2 columns
		int columnSize = 2;
		params.put("cellsPerColumn", columnSize);
		int predictionOrderLimit = 5;
		params.put("predictionOrderLimit", predictionOrderLimit);
		initRegion();

		DendriteSegment seg = new DendriteSegmentImpl();
		int h = 1;
		int w = 1;
		int c = 1;
		
		Cell[][][] cells = TestUtils.getCells(2,2,2);
		cells[0][0][0].setActivePreviously(true);
		region.setCells(cells);
		
		Synapse s = new SynapseImpl();
		s.setSourceColumn(0);
		s.setSourceHeight(0);
		s.setSourceWidth(0);
		seg.addPotentialSynapse(s);
		seg.addConnectedSynapse(s);

		int minPredictionOrder = 1;
		SegmentUpdate u = region.storeSegmentUpdate(SegmentUpdateType.EXTENDING_PREDICTION, seg, 
													minPredictionOrder+1, minPredictionOrder, 
													h, w, c);
		List<Collection<SegmentUpdate>> allUpdates = region.getPotentialSegmentUpdates();
		Collection<SegmentUpdate> updates = allUpdates.get(1);
		
		assertEquals(u, updates.iterator().next());
		assertEquals(1, updates.size());
		assertEquals(seg, u.getDendriteSegment());
		assertEquals(1, u.getActiveSynapses().size());
		assertEquals(s, u.getActiveSynapses().iterator().next());
		//also that other positions are empty
		assertEquals(0, allUpdates.get(0).size());
		assertEquals(0, allUpdates.get(2).size());
		assertEquals(0, allUpdates.get(3).size());
		assertEquals(0, allUpdates.get(4).size());
		assertEquals(predictionOrderLimit+1, allUpdates.size());
	}

	@Test
	public void testAdaptSegmentUpdatesPositive() {
		int height = 2;
		int width = 2;
		int columnSize = 2;
		params.put("regionHeight", height);
		params.put("regionWidth", width);
		params.put("cellsPerColumn", columnSize);
		params.put("exciteStrategyName", "defaultClaExcite");// 0.05 slope
		initRegion();

		Synapse s = new SynapseImpl();
		s.setPermanence(0.5);
		s.setSourceColumn(0);
		s.setSourceHeight(0);
		s.setSourceWidth(0);
		Synapse s2 = new SynapseImpl();
		s2.setPermanence(0.5);
		s2.setSourceColumn(1);
		s2.setSourceHeight(1);
		s2.setSourceWidth(1);
		Synapse s3 = new SynapseImpl();
		s3.setSourceColumn(1);
		s3.setSourceHeight(0);
		s3.setSourceWidth(1);
		Collection<Synapse> activeSynapses = new ArrayList<Synapse>();
		activeSynapses.add(s2);
		activeSynapses.add(s3);

		DendriteSegment ds = new DendriteSegmentImpl();
		ds.addPotentialSynapse(s);// will be negatively reinforced
		ds.addPotentialSynapse(s2);// will be positively reinforced

		SegmentUpdate u = new SegmentUpdateImpl();
		u.setPredictionTime(1);
		u.setDendriteSegment(ds);
		u.setActiveSynapses(activeSynapses);
		region.performPositiveSegmentUpdate(u, region.getColumn(0, 0));

		assertTrue(ds.isPredictingForNextStep());
		assertEquals(0.45, s.getPermanence(), epsilon);
		assertEquals(0.55, s2.getPermanence(), epsilon);
	}

	@Test
	public void testAdaptSegmentUpdatesPositive1() {
		int columnSize = 2;		
		params.put("inputProjectionFactor",1.0);
		params.put("regionProjectionFactor",4.0);		
		params.put("cellsPerColumn",columnSize);
		params.put("maxNewDistalSynapsesPerUpdate",1);
		params.put("distalLearningRadius",2.0);
		initRegion();

		Map<Column,Integer> indices = new HashMap<Column,Integer>();
		indices.put(region.getColumn(0, 0), 1);
		indices.put(region.getColumn(1, 0), 1);
		indices.put(region.getColumn(0, 1), 1);
		indices.put(region.getColumn(1, 1), 1);
		region.setLearningCells(indices);
		
		SegmentUpdate u = new SegmentUpdateImpl();
		u.setUpdateType(SegmentUpdateType.NEW_FIRST_ORDER);
		Collection<Synapse> activeSynapses = new ArrayList<Synapse>();
		u.setActiveSynapses(activeSynapses);
		DendriteSegment ds = new DendriteSegmentImpl();
		u.setDendriteSegment(ds);
		region.performPositiveSegmentUpdate(u, region.getColumn(0, 0));
		assertEquals(1, ds.getPotentialSynapses().size());
	}

	@Test
	public void testAdaptSegmentUpdatesNegative() {
		int height = 2;
		int width = 2;
		int columnSize = 2;
		params.put("regionHeight", height);
		params.put("regionWidth", width);
		params.put("cellsPerColumn", columnSize);
		params.put("distalNegativePermanenceDecrement", -2.0);
		params.put("exciteStrategyName", "defaultClaExcite");// 0.05 slope
		initRegion();

		Synapse s = new SynapseImpl();
		s.setPermanence(0.5);
		s.setSourceColumn(0);
		s.setSourceHeight(0);
		s.setSourceWidth(0);
		Synapse s2 = new SynapseImpl();
		s2.setPermanence(0.5);
		s2.setSourceColumn(1);
		s2.setSourceHeight(1);
		s2.setSourceWidth(1);
		Collection<Synapse> activeSynapses = new ArrayList<Synapse>();
		activeSynapses.add(s2);

		DendriteSegment ds = new DendriteSegmentImpl();
		ds.addPotentialSynapse(s);
		ds.addPotentialSynapse(s2);

		SegmentUpdate u = new SegmentUpdateImpl();
		u.setPredictionTime(1);
		u.setDendriteSegment(ds);
		u.setActiveSynapses(activeSynapses);
		region.performNegativeSegmentUpdate(u, region.getColumn(0, 0));

		assertTrue(ds.isPredictingForNextStep());
		assertEquals(0.5, s.getPermanence(), epsilon);
		assertEquals(0.4, s2.getPermanence(), epsilon);
		assertEquals(2, ds.getPotentialSynapses().size());
	}

	@Test
	public void testGetRandomLocalLearningCells() {
		double radius = 2;
		int height = 3;
		int width = 3;
		inputSignalDimensionality = 9;
		int columnSize = 2;
		int maxSynapsesPerSegment = height * width;
		params.put("distalLearningRadius", radius);
		params.put("regionHeight", height);
		params.put("regionWidth", width);
		params.put("cellsPerColumn", columnSize);
		params.put("maxNewDistalSynapsesPerUpdate", maxSynapsesPerSegment);
		initRegion();
		
		Map<Column, Integer> learningCells = new HashMap<Column, Integer>();
		learningCells.put(region.getColumn(0, 0), 0);
		learningCells.put(region.getColumn(0, 1), 1);
		learningCells.put(region.getColumn(0, 2), 1);
		learningCells.put(region.getColumn(1, 0), 1);
		learningCells.put(region.getColumn(1, 1), 1);
		learningCells.put(region.getColumn(1, 2), 1);
		learningCells.put(region.getColumn(2, 0), 1);
		learningCells.put(region.getColumn(2, 1), 1);
		learningCells.put(region.getColumn(2, 2), 1);		
		region.setLearningCells(learningCells);
		int maxSynapses = 3;
		int i = 0;
		int j = 0;
		Column c = region.getColumn(i, j);
		Collection<Synapse> synapses = region.getNewDistalSynapses(maxSynapses, c);
		assertEquals(maxSynapses, synapses.size());
		for (Synapse p : synapses) {
			assertTrue(p.getSourceHeightPos() >= 0);
			assertTrue(p.getSourceHeightPos() <= height);
			assertTrue(p.getSourceWidthPos() >= 0);
			assertTrue(p.getSourceWidthPos() <= width);
			if (p.getSourceWidthPos() == 0 && p.getSourceHeightPos() == 0) {
				assertEquals(0, p.getSourceColumn());
			} else {
				assertEquals(1, p.getSourceColumn());
			}
		}
	}

	@Test
	public void testGetRandomLocalLearningCells0() {
		double radius = 2;
		int height = 3;
		int width = 3;
		inputSignalDimensionality = 9;
		int columnSize = 2;
		int maxSynapsesPerSegment = height * width;
		params.put("distalLearningRadius", radius);
		params.put("regionHeight", height);
		params.put("regionWidth", width);
		params.put("cellsPerColumn", columnSize);
		params.put("maxNewDistalSynapsesPerUpdate", maxSynapsesPerSegment);
		initRegion();

		Map<Column, Integer> learningCells = new HashMap<Column, Integer>();
		learningCells.put(region.getColumn(0, 0), 0);
		learningCells.put(region.getColumn(0, 1), 1);
		learningCells.put(region.getColumn(0, 2), 1);
		learningCells.put(region.getColumn(1, 0), 1);
		learningCells.put(region.getColumn(1, 1), 1);
		learningCells.put(region.getColumn(1, 2), 1);
		learningCells.put(region.getColumn(2, 0), 1);
		learningCells.put(region.getColumn(2, 1), 1);
		learningCells.put(region.getColumn(2, 2), 1);		
		region.setLearningCells(learningCells);
		int maxSynapses = height * width;
		int i = 0;
		int j = 0;
		Column c = region.getColumn(i, j);
		Collection<Synapse> synapses = region.getNewDistalSynapses(maxSynapses, c);
		assertEquals(5, synapses.size());
		// doesn't get maxSynapses because it is larger than the number of
		// columns with the circle
		for (Synapse p : synapses) {
			assertTrue(p.getSourceHeightPos() >= 0);
			assertTrue(p.getSourceHeightPos() <= height);
			assertTrue(p.getSourceWidthPos() >= 0);
			assertTrue(p.getSourceWidthPos() <= width);
			if (p.getSourceWidthPos() == 0 && p.getSourceHeightPos() == 0) {
				assertEquals(0, p.getSourceColumn());
			} else {
				assertEquals(1, p.getSourceColumn());
			}
		}
	}

	@Test
	public void testGetRandomLocalLearningCells1() {
		double radius = 1;
		int height = 3;
		int width = 3;
		inputSignalDimensionality = 9;
		int columnSize = 2;
		params.put("distalLearningRadius", radius);
		params.put("regionHeight", height);
		params.put("regionWidth", width);
		params.put("cellsPerColumn", columnSize);
		initRegion();
		
		Map<Column, Integer> learningCells = new HashMap<Column, Integer>();
		learningCells.put(region.getColumn(0, 0), 0);
		learningCells.put(region.getColumn(0, 1), 0);
		learningCells.put(region.getColumn(0, 2), 0);
		learningCells.put(region.getColumn(1, 0), 1);
		learningCells.put(region.getColumn(1, 1), 1);
		learningCells.put(region.getColumn(1, 2), 1);
		learningCells.put(region.getColumn(2, 0), 1);
		learningCells.put(region.getColumn(2, 1), 1);
		learningCells.put(region.getColumn(2, 2), 1);	
		region.setLearningCells(learningCells);

		int maxSynapses = height * width;
		int i = height - 1;
		int j = width - 1;
		Column c = region.getColumn(i, j);
		Collection<Synapse> synapses = region.getNewDistalSynapses(maxSynapses, c);
		assertEquals(2, synapses.size());
		for (Synapse p : synapses) {
			assertTrue(p.getSourceHeightPos() >= height - 1 - radius);
			assertTrue(p.getSourceHeightPos() <= height - 1);
			assertTrue(p.getSourceWidthPos() >= width - 1 - radius);
			assertTrue(p.getSourceWidthPos() <= width - 1);
			if (p.getSourceWidthPos() == 0) {
				assertEquals(0, p.getSourceColumn());
			} else {
				assertEquals(1, p.getSourceColumn());
			}
		}
	}

	@Test
	public void testGetRandomLocalLearningCells2() {
		double radius = 3;
		int height = 5;
		int width = 5;
		inputSignalDimensionality = 25;
		int columnSize = 3;
		int maxSynapsesPerSegment = height * width;
		params.put("distalLearningRadius", radius);
		params.put("regionHeight", height);
		params.put("regionWidth", width);
		params.put("cellsPerColumn", columnSize);
		params.put("maxNewDistalSynapsesPerUpdate", maxSynapsesPerSegment);
		initRegion();

		Map<Column, Integer> learningCells = new HashMap<Column, Integer>();
		learningCells.put(region.getColumn(0, 0), 0);
		learningCells.put(region.getColumn(0, 1), 0);
		learningCells.put(region.getColumn(0, 2), 0);
		learningCells.put(region.getColumn(0, 3), 0);
		learningCells.put(region.getColumn(0, 4), 0);		
		learningCells.put(region.getColumn(1, 0), 0);
		learningCells.put(region.getColumn(1, 1), 0);
		learningCells.put(region.getColumn(1, 2), 0);
		learningCells.put(region.getColumn(1, 3), 0);
		learningCells.put(region.getColumn(1, 4), 0);		
		learningCells.put(region.getColumn(2, 0), 0);
		learningCells.put(region.getColumn(2, 1), 0);
		learningCells.put(region.getColumn(2, 2), 0);
		learningCells.put(region.getColumn(2, 3), 0);
		learningCells.put(region.getColumn(2, 4), 0);		
		learningCells.put(region.getColumn(3, 0), 0);
		learningCells.put(region.getColumn(3, 1), 0);
		learningCells.put(region.getColumn(3, 2), 0);
		learningCells.put(region.getColumn(3, 3), 0);
		learningCells.put(region.getColumn(3, 4), 0);		
		learningCells.put(region.getColumn(4, 0), 0);
		learningCells.put(region.getColumn(4, 1), 0);
		learningCells.put(region.getColumn(4, 2), 0);
		learningCells.put(region.getColumn(4, 3), 0);
		learningCells.put(region.getColumn(4, 4), 0);				
		region.setLearningCells(learningCells);
		
		int maxSynapses = height * width;
		int i = height / 2;
		int j = width / 2;
		Column c = region.getColumn(i, j);
		Collection<Synapse> synapses = region.getNewDistalSynapses(maxSynapses, c);
		assertEquals(24, synapses.size());
		for (Synapse p : synapses) {
			assertTrue(p.getSourceHeightPos() >= i - radius);
			assertTrue(p.getSourceHeightPos() <= i + radius);
			assertTrue(p.getSourceWidthPos() >= j - radius);
			assertTrue(p.getSourceWidthPos() <= j + radius);
			assertEquals(0, p.getSourceColumn());
		}
	}

	@Test
	public void testGetRandomLocalLearningCells4() {
		int i = 0;
		int j = 0;
		initRegion();
		Column c = region.getColumn(i, j);
		Collection<Synapse> synapses = region.getNewDistalSynapses(-5, c);
		assertEquals(0, synapses.size());
	}

	@Test
	public void testGetRandomLocalLearningCells5() {
		inputSignalDimensionality = 25;
		params.put("columnsPerInput", 1.0);
		params.put("distalLearningRadius", 3.0);
		int maxSynapsesPerSegment = 20;
		params.put("maxNewDistalSynapsesPerUpdate", maxSynapsesPerSegment);		
		initRegion();		
		Map<Column, Integer> learningCells = new HashMap<Column, Integer>();
		learningCells.put(region.getColumn(0, 0), 0);
		learningCells.put(region.getColumn(0, 1), 0);
		learningCells.put(region.getColumn(0, 2), 0);
		learningCells.put(region.getColumn(0, 3), 0);
		learningCells.put(region.getColumn(0, 4), 0);		
		learningCells.put(region.getColumn(1, 0), 0);
		learningCells.put(region.getColumn(1, 1), 0);
		learningCells.put(region.getColumn(1, 2), 0);
		learningCells.put(region.getColumn(1, 3), 0);
		learningCells.put(region.getColumn(1, 4), 0);		
		learningCells.put(region.getColumn(2, 0), 0);
		learningCells.put(region.getColumn(2, 1), 0);
		learningCells.put(region.getColumn(2, 2), 0);
		learningCells.put(region.getColumn(2, 3), 0);
		learningCells.put(region.getColumn(2, 4), 0);		
		learningCells.put(region.getColumn(3, 0), 0);
		learningCells.put(region.getColumn(3, 1), 0);
		learningCells.put(region.getColumn(3, 2), 0);
		learningCells.put(region.getColumn(3, 3), 0);
		learningCells.put(region.getColumn(3, 4), 0);		
		learningCells.put(region.getColumn(4, 0), 0);
		learningCells.put(region.getColumn(4, 1), 0);
		learningCells.put(region.getColumn(4, 2), 0);
		learningCells.put(region.getColumn(4, 3), 0);
		learningCells.put(region.getColumn(4, 4), 0);				
		region.setLearningCells(learningCells);
		
		int activeSynapses = 10;
		Column c = region.getColumn(2, 2);
		Collection<Synapse> synapses = region.getNewDistalSynapses(activeSynapses,c);
		assertEquals(maxSynapsesPerSegment - activeSynapses, synapses.size());
	}

	@Test
	public void testGetRandomLocalLearningCells6() {
		int activeSynapses = 0;
		int i = 0;
		int j = 0;
		int maxSynapsesPerSegment = 0;
		params.put("maxNewDistalSynapsesPerUpdate", maxSynapsesPerSegment);
		initRegion();
		Column c = region.getColumn(i, j);
		Collection<Synapse> positions = region.getNewDistalSynapses(
				activeSynapses, c);
		assertEquals(0, positions.size());
	}

//	@Test
//	public void testProcessStructuralPrediction() {
		// inputSource.inputHeight = 2;
		// inputSource.inputWidth = 3;
		// params.put("columnsPerInput", 1.0);
		// int columnSize = 4;
		// params.put("cellsPerColumn",columnSize);
		// initRegion();
		// boolean[][][] curActiveState = new
		// boolean[inputSource.inputHeight][inputSource.inputWidth][columnSize];
		// boolean[][][] curPredictiveState = new
		// boolean[inputSource.inputHeight][inputSource.inputWidth][columnSize];
		// curActiveState[1][2][3] = true;
		// curActiveState[0][1][2] = true;
		// curPredictiveState[0][0][0] = true;
		// curPredictiveState[0][1][2] = true;
		// region.setCurrentActiveState(curActiveState);
		// region.setCurrentPredictiveState(curPredictiveState);
		// BitVector res = region.getOutput();
		// for(int i = 0; i < res.size(); i++){
		// if(i == 0 || i == 14 || i == 23){
		// assertTrue(res.get(i));
		// }else{
		// assertFalse(res.get(i));
		// }
		// }
//	}

}