package edu.memphis.ccrg.cla.corticalregion;


import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import edu.memphis.ccrg.cla.corticalregion.columns.Column;
import edu.memphis.ccrg.cla.corticalregion.columns.ColumnImpl;

public class ColumnImplTest {
	
	private Column c;
	private static final double epsilon = 10e-9;

	@Before
	public void setUp() throws Exception {
		c = new ColumnImpl();
		c.setHistorySize(5);
	}
	
	@Test
	public void testUpdateActiveHistory() {
		assertEquals(0.0,c.getAverageActivity(),epsilon);
		c.updateActiveHistory(false);
		assertEquals(0.0,c.getAverageActivity(),epsilon);
		c.updateActiveHistory(false);
		c.updateActiveHistory(false);
		c.updateActiveHistory(true);
		c.updateActiveHistory(true);
		assertEquals(0.4,c.getAverageActivity(),epsilon);
		
		//test history bounds
		c.updateActiveHistory(true);
		assertEquals(0.6,c.getAverageActivity(),epsilon);
		c.updateActiveHistory(true);
		assertEquals(0.8,c.getAverageActivity(),epsilon);
		c.updateActiveHistory(true);
		assertEquals(1.0,c.getAverageActivity(),epsilon);
		c.updateActiveHistory(true);
		assertEquals(1.0,c.getAverageActivity(),epsilon);
		
		c.updateActiveHistory(false);
		c.updateActiveHistory(false);
		c.updateActiveHistory(false);
		c.updateActiveHistory(false);
		c.updateActiveHistory(false);
		assertEquals(0.0,c.getAverageActivity(),epsilon);
	}

	@Test
	public void testUpdateOverlapHistory() {
		assertEquals(0.0,c.getAverageOverlap(),epsilon);
		c.updateOverlapHistory(false);
		assertEquals(0.0,c.getAverageOverlap(),epsilon);
		c.updateOverlapHistory(false);
		c.updateOverlapHistory(false);
		c.updateOverlapHistory(true);
		c.updateOverlapHistory(true);
		assertEquals(0.4,c.getAverageOverlap(),epsilon);
		
		//test history bounds
		c.updateOverlapHistory(true);
		assertEquals(0.6,c.getAverageOverlap(),epsilon);
		c.updateOverlapHistory(true);
		assertEquals(0.8,c.getAverageOverlap(),epsilon);
		c.updateOverlapHistory(true);
		assertEquals(1.0,c.getAverageOverlap(),epsilon);
		c.updateOverlapHistory(true);
		assertEquals(1.0,c.getAverageOverlap(),epsilon);
		
		c.updateOverlapHistory(false);
		c.updateOverlapHistory(false);
		c.updateOverlapHistory(false);
		c.updateOverlapHistory(false);
		c.updateOverlapHistory(false);
		assertEquals(0.0,c.getAverageOverlap(),epsilon);
	}
	
	@Test
	public void testCompareTo(){
		c.setBoostedOverlap(5.0);
		Column other = new ColumnImpl();
		
		other.setBoostedOverlap(4.0);
		assertEquals(-1,c.compareTo(other));
		other.setBoostedOverlap(5.0);
		assertEquals(0,c.compareTo(other));
		other.setBoostedOverlap(6.0);
		assertEquals(1,c.compareTo(other));
	}
}