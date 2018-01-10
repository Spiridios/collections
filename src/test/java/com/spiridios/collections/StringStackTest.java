/*******************************************************************************
 * Copyright 2018 Micah Lieske.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
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
			StringStack ss = new StringStack(Arrays.asList("a", "b", null, "", "c"));
			assertThat(ss.peek(), is("c"));
			assertThat(ss.pop(), is("c"));
			assertThat(ss.peek(), is(""));
			assertThat(ss.pop(), is(""));
			assertThat(ss.peek(), is(nullValue()));
			assertThat(ss.pop(), is(nullValue()));
			assertThat(ss.peek(), is("b"));
			assertThat(ss.pop(), is("b"));
			assertThat(ss.peek(), is("a"));
			assertThat(ss.pop(), is("a"));
		}
		
		@Test
		public void testToString() {
			StringStack ss = new StringStack(Arrays.asList("a", "b", null, "", "c"));
			assertThat(ss.toString(), is("abc"));
		}
		
		@SuppressWarnings("unlikely-arg-type")
		@Test(expected = ClassCastException.class)
		public void testContainsClassCast() {
			StringStack ss = new StringStack();
			ss.contains(new Integer(0));
		}
		
		@Test
		public void testCopyConstructor() {
			StringStack orig = new StringStack();
			orig.push("a");
			orig.push("b");
			orig.push("c");
			orig.push("d");
			
			StringStack copy = new StringStack(orig);
			
			Iterator<String> origItr = orig.iterator();
			Iterator<String> copyItr = copy.iterator();
			while (origItr.hasNext() && copyItr.hasNext()) {
				assertThat(copyItr.next(), is(origItr.next()));
			}
			
			while (!orig.isEmpty()) {
				String origStr = orig.pop();
				String copyStr = copy.pop();
				assertThat(origStr, is(copyStr));
			}
		}

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
					CollectionFeature.SERIALIZABLE,
					CollectionFeature.FAILS_FAST_ON_CONCURRENT_MODIFICATION,
					CollectionSize.ANY
					).named("StringStack collection tests").createTestSuite();
		}
	}
}
