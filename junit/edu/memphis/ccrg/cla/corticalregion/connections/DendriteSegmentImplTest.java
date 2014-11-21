package edu.memphis.ccrg.cla.corticalregion.connections;


import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class DendriteSegmentImplTest {

	private DendriteSegment ds;
	private Synapse s1;
	private Synapse s2;
	private Synapse s3;
	
	@Before
	public void setUp() throws Exception {
		ds = new DendriteSegmentImpl();
		s1 = new SynapseImpl();
		s2 = new SynapseImpl();
		s3 = new SynapseImpl();
	}
	
	@Test
	public void testAddConnectedSynapse() {
		ds.addConnectedSynapse(s1);
		assertEquals(0,ds.getConnectedSynapses().size());
		ds.addPotentialSynapse(s2);
		assertEquals(0,ds.getConnectedSynapses().size());
		ds.addConnectedSynapse(s1);
		assertEquals(0,ds.getConnectedSynapses().size());
		ds.addConnectedSynapse(s2);
		assertEquals(1,ds.getConnectedSynapses().size());
	}
	
	@Test
	public void testRemovePotentialSynapse() {
		ds.addPotentialSynapse(s1);
		ds.addPotentialSynapse(s2);
		ds.addConnectedSynapse(s1);
		ds.addConnectedSynapse(s2);
		
		assertEquals(2,ds.getPotentialSynapses().size());
		assertEquals(2,ds.getConnectedSynapses().size());
		
		ds.removePotentialSynapse(s1);
		
		assertEquals(1,ds.getPotentialSynapses().size());
		assertEquals(1,ds.getConnectedSynapses().size());
		
		ds.removePotentialSynapse(s2);
		
		assertEquals(0,ds.getPotentialSynapses().size());
		assertEquals(0,ds.getConnectedSynapses().size());
		
		ds.removePotentialSynapse(s3);
		
		assertEquals(0,ds.getPotentialSynapses().size());
		assertEquals(0,ds.getConnectedSynapses().size());
	}

	@Test
	public void testRemoveConnectedSynapse() {
		ds.addPotentialSynapse(s1);
		ds.addConnectedSynapse(s1);
		//
		ds.removeConnectedSynapse(s1);
		//
		assertEquals(1,ds.getPotentialSynapses().size());
		assertEquals(0,ds.getConnectedSynapses().size());
	}
}
