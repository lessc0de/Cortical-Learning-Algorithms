package edu.memphis.ccrg.cla.corticalregion.connections;


import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import edu.memphis.ccrg.cla.corticalregion.connections.Synapse;
import edu.memphis.ccrg.cla.corticalregion.connections.SynapseImpl;

public class SynapseImplTest {

	private static final double epsilon = 10e-9;
	private Synapse s;
	
	@Before
	public void setUp() throws Exception {
		s = new SynapseImpl();
	}
	
	@Test
	public void testUpdatePermanence(){
		assertEquals(0.0,s.getPermanence(),epsilon);
//		assertEquals(0.0, s.updatePermanence(-1),epsilon);
//		assertEquals(0.9, s.updatePermanence(0.9),epsilon);	
//		assertEquals(0.45, s.updatePermanence(-0.45),epsilon);
//		assertEquals(1.0, s.updatePermanence(0.55),epsilon);
//		assertEquals(1.0, s.updatePermanence(0.55),epsilon);
	}
}