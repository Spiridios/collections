package com.spiridios.collections;

import com.google.common.collect.testing.CollectionTestSuiteBuilder;
import com.google.common.collect.testing.TestStringCollectionGenerator;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;

//import static org.junit.Assert.*;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;

import org.hamcrest.core.IsNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import junit.framework.TestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ StringStackTest.MyTests.class, StringStackTest.GuavaTests.class

})
public class StringStackTest {

	public static class MyTests {

		@Test
		public void testPeekPop() {
			StringStack ss = new StringStack();
		}
		
		@Test
		public void testToString() {
			StringStack ss = new StringStack();
		}
		
		@SuppressWarnings("unlikely-arg-type")
		@Test(expected = ClassCastException.class)
		public void testContainsClassCast() {
			StringStack ss = new StringStack();
			ss.contains(new Integer(0));
		}
		
		@Test
		public void testAddAllNull() {
			StringStack ss = new StringStack();
			ss.addAll(Arrays.asList("A","B", null, "C"));
			
			Iterator<String> itr = ss.iterator();
			assertThat(itr.next(), is("C"));
			assertThat(itr.next(), is((String)null));
			assertThat(itr.next(), is("B"));
			assertThat(itr.next(), is("A"));
		}

		// Push null - can push empty and null strings and pop them back off
		//
		
		// Should create performance tests comparing to Deque<String>

	}

	public static class GuavaTests {
		public static TestSuite suite() {
			return CollectionTestSuiteBuilder.using(new TestStringCollectionGenerator() {
				@Override
				public List<String> order(List<String> insertionOrder) {
					// Reverse the list since stack is LIFO iteration.
					List<String> ordered = new LinkedList<String>();
					for (String string : insertionOrder) {
						ordered.add(0, string);
					}
					return ordered;
				}

				@Override
				protected Collection<String> create(String[] arg0) {
					StringStack ss = new StringStack();
					for (String string : arg0) {
						ss.push(string);
					}
					return ss;
				}
			}).withFeatures(
					CollectionFeature.ALLOWS_NULL_QUERIES,
					CollectionFeature.ALLOWS_NULL_VALUES,
					CollectionFeature.KNOWN_ORDER,
					CollectionFeature.NON_STANDARD_TOSTRING,
					CollectionFeature.GENERAL_PURPOSE,
					CollectionSize.ANY
					).named("StringStack collection tests").createTestSuite();
		}
	}
}
