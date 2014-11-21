package edu.memphis.ccrg.cla.corticalregion;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import edu.memphis.ccrg.cla.corticalregion.columns.Column;
import edu.memphis.ccrg.cla.corticalregion.columns.ColumnImpl;

public class DistanceComparatorTest {

	@Test
	public void testCompare() {
		DistanceComparator comparator = new DistanceComparator();
		comparator.setColumnPosition(0,0);
		
		Column c1 = new ColumnImpl();
		c1.setRegionHeightPosition(0);
		c1.setRegionWidthPosition(0);
		
		Column c2 = new ColumnImpl();
		c2.setRegionHeightPosition(4);
		c2.setRegionWidthPosition(2);
		
		assertEquals(-1,comparator.compare(c1, c2));
		assertEquals(1,comparator.compare(c2, c1));
		
		c1.setRegionHeightPosition(5);
		c1.setRegionWidthPosition(5);		
		c2.setRegionHeightPosition(5);
		c2.setRegionWidthPosition(5);
		
		assertEquals(0,comparator.compare(c1, c2));
		assertEquals(0,comparator.compare(c2, c1));
		
		c1.setRegionHeightPosition(1);
		c1.setRegionWidthPosition(5);		
		c2.setRegionHeightPosition(1);
		c2.setRegionWidthPosition(2);
		
		assertEquals(1,comparator.compare(c1, c2));
		assertEquals(-1,comparator.compare(c2, c1));
	}
	
	@Test
	public void testCompareSort() {
		DistanceComparator comparator = new DistanceComparator();
		comparator.setColumnPosition(0,0);
		
		Column c1 = new ColumnImpl();
		c1.setRegionHeightPosition(1);
		c1.setRegionWidthPosition(3);
		
		Column c2 = new ColumnImpl();
		c2.setRegionHeightPosition(4);
		c2.setRegionWidthPosition(2);
		
		List<Column> cols = new ArrayList<Column>();
		cols.add(c1);
		cols.add(c2);
		Collections.sort(cols, comparator);
		assertEquals(c1,cols.get(0));
		assertEquals(c2,cols.get(1));
	}

}
