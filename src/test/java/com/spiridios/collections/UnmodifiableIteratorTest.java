package com.spiridios.collections;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

public class UnmodifiableIteratorTest {

	@Test
	public void testIteration() {
		List<Integer> list = Arrays.asList(0,1,2,3,4,5,6,7,8,9,10);
		int expectedI = 0;
		for (Integer i : list) {
			assertThat(i,is(expectedI));
			expectedI++;
		}
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testRemove() {
		List<Integer> list = Arrays.asList(0,1,2,3,4,5,6,7,8,9,10);
		Iterator<Integer> itr = list.iterator();
		itr.next();
		itr.remove();
	}
}
