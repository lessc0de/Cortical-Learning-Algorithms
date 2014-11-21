package edu.memphis.ccrg.cla.corticalregion.tests;


import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.HashSet;

import org.junit.Test;

import edu.memphis.ccrg.cla.corticalregion.columns.Column;
import edu.memphis.ccrg.cla.corticalregion.columns.ColumnImpl;
import edu.memphis.ccrg.cla.utils.TestUtils;

public class TestUtilsTest {

	private static final double epsilon = 10e-9;
	
	@Test
	public void testGetComparisonScores(){		
		Collection<Column> source = new HashSet<Column>();
		Column c0 = new ColumnImpl();
		Column c1 = new ColumnImpl();
		Column c2= new ColumnImpl();
		Column c3 = new ColumnImpl();
		source.add(c0);
		source.add(c1);
		source.add(c2);
		source.add(c3);
		assertEquals(4,source.size());
		Collection<Column> original = source;
		Collection<Column> noisy = source;
		double[] scores = TestUtils.getErrorCounts(original, noisy);		
		assertEquals(0.0,scores[0],epsilon);
		assertEquals(0.0,scores[1],epsilon);
		assertEquals(0.0,scores[2],epsilon);
	}
	
	@Test
	public void testGetComparisonScores1(){		
		Collection<Column> source = new HashSet<Column>();
		Column c0 = new ColumnImpl();
		Column c1 = new ColumnImpl();
		Column c2= new ColumnImpl();
		Column c3 = new ColumnImpl();
		source.add(c0);
		source.add(c1);
		source.add(c2);
		Collection<Column> original = new HashSet<Column>(source);		
		source.remove(c0);
		source.add(c3);
		Collection<Column> noisy = source;

		assertEquals(3,original.size());
		assertEquals(3,noisy.size());
		double[] scores = TestUtils.getErrorCounts(original, noisy);
		assertEquals(2, scores[1]+scores[2], epsilon);
		assertEquals(1, scores[1],epsilon);
		assertEquals(1, scores[2], epsilon);
	}

}
