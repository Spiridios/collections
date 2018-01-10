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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * StringStack is a stack optimized for working with Strings, especially the toString() operation.
 * You may want to use this when navigating tree-like structures such as XML, JSON, file systems, etc 
 * to track and render your current path efficiently.
 * 
 * Null values and empty strings are allowed in the stack. The toString operation will render nothing in
 * their place, but iterating or popping will return nulls and empty strings for their appropriate entries.
 * 
 * @author Micah Lieske
 */
public class StringStack implements Collection<String>, Serializable {
	// TODO: Element separator E.G. "/" for paths. Probably need LEADING, TAILING and SURROUND as options.

	/**
	 * Iterator that Returns elements in LIFO (stack) order.
	 * Repeatedly calling next() gives an equivalent order as if repeatedly calling pop();
	 */
	private class LIFOIterator implements Iterator<String> {
		private int currentElementIndexIndex;
		private int expectedModificationCount;
		private boolean nextCalled = false;

		private LIFOIterator() {
			this.expectedModificationCount = modificationCount;
			this.currentElementIndexIndex = elementIndices.size();
		}

		private Integer getEndIndex() {
			Integer endIdx = null;
			for (int i = currentElementIndexIndex + 1; i < elementIndices.size(); i++) {
				endIdx = elementIndices.get(i);
				if (endIdx != null) {
					return endIdx;
				}
			}
			return elementBuffer.length();
		}

		public boolean hasNext() {
			return currentElementIndexIndex > 0;
		}

		public String next() {
			if (currentElementIndexIndex <= 0) {
				throw new NoSuchElementException();
			}
			if (modificationCount != expectedModificationCount) {
				throw new ConcurrentModificationException();
			}

			nextCalled = true;
			currentElementIndexIndex--;
			Integer startIdx = elementIndices.get(currentElementIndexIndex);
			Integer endIdx = getEndIndex();
			if (startIdx == null) {
				return null;
			} else {
				String element = elementBuffer.substring(startIdx, endIdx);
				return element;
			}
		}

		public void remove() {
			if (!nextCalled) {
				throw new IllegalStateException("next() hasn't been called");
			}
			if (modificationCount != expectedModificationCount) {
				throw new ConcurrentModificationException();
			}
			modificationCount++;
			expectedModificationCount = modificationCount;
			nextCalled = false;
			Integer startIdx = elementIndices.get(currentElementIndexIndex);
			Integer endIdx = getEndIndex();
			elementIndices.remove(currentElementIndexIndex);

			if (startIdx != null) {
				Integer diff = endIdx - startIdx;
				if (diff > 0) {
					for (int i = currentElementIndexIndex; i < elementIndices.size(); i++) {
						Integer idx = elementIndices.get(i); 
						if (idx != null) {
							elementIndices.set(i, idx - diff);
						}
					}
					elementBuffer.delete(startIdx, endIdx);
				}
			}
		}
	}

	private static final long serialVersionUID = -6797711532702199014L;
	
	/**
	 * Count of modifications made to this Stack (for ConcurrentModdificationException purposes)
	 */
	private transient int modificationCount = 0;

	/**
	 * The string data of the string elements in this stack.
	 */
	private StringBuilder elementBuffer;

	/**
	 * Index of the start of the string, null if the string's value is null.
	 */
	private ArrayList<Integer> elementIndices;

	/**
	 * Constructs and empty StringStack
	 */
	public StringStack() {
		clear();
	}
	
	/**
	 * Constructs a new StringStack that contains the elements of the given collection.
	 * This is equivalent to creating an empty StringStack and calling addAll(collection);
	 * @param collection The Collection to copy
	 */
	public StringStack(Collection<? extends String> collection) {
		this();
		addAll(collection);
	}
	
	/**
	 * Constructs a new StringStack that is a deep copy of the given StringStack
	 * @param toCopy The StringStack to copy
	 */
	public StringStack(StringStack toCopy) {
		this.elementIndices = new ArrayList<Integer>(toCopy.elementIndices);
		this.elementBuffer = new StringBuilder(toCopy.elementBuffer);
	}

	public boolean add(String e) {
		push(e);
		return true;
	}

	/**
	 * Adds all of the elements in the specified collection to this collection. Items are {@link StringStack#push pushed}
	 * onto the stack in the specified collection's iterator order. This means the last item returned by specified collection's
	 * iterator will be the first item returned from {@link StringStack#pop peek}/{@link StringStack#pop pop}.
	 * @param c collection containing elements to be added to this collection
	 * @return true if this collection changed as a result of the call
	 */
	public boolean addAll(Collection<? extends String> c) {
		if (c == null) {
			throw new NullPointerException("c cannot be null");
		}

		boolean changed = false;
		for (String e : c) {
			changed |= add(e);
		}
		return changed;
	}

	public void clear() {
		elementBuffer = new StringBuilder();
		elementIndices = new ArrayList<Integer>();
		modificationCount++;
	}

	public boolean contains(Object o) {
		String eToFind = (String) o; // will throw contract classcast if o is not string
		for (String e : this) {
			if ((o == null && e == null) || (e != null && e.equals(eToFind))) {
				return true;
			}
		}
		return false;
	}

	public boolean containsAll(Collection<?> c) {
		if (c == null) {
			throw new NullPointerException("c cannot be null");
		}

		for (Object element : c) {
			if (!contains(element)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		StringStack other = (StringStack) obj;
		if (elementBuffer == null) {
			if (other.elementBuffer != null) {
				return false;
			}
		} else if (!elementBuffer.toString().equals(other.elementBuffer.toString())) {
			return false;
		}
		if (elementIndices == null) {
			if (other.elementIndices != null) {
				return false;
			}
		} else if (!elementIndices.equals(other.elementIndices)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((elementBuffer == null) ? 0 : elementBuffer.toString().hashCode());
		result = prime * result + ((elementIndices == null) ? 0 : elementIndices.hashCode());
		return result;
	}

	public boolean isEmpty() {
		return elementIndices.isEmpty();
	}

	/**
	 * Returns items in LIFO order. The order returned is the same order as repeatedly calling {@link StringStack#pop pop}.
	 */
	public Iterator<String> iterator() {
		return new LIFOIterator();
	}

	/**
	 * Returns the most recently added element of the Stack without removing it from the stack.
	 * @return The most recently added element.
	 * @throws NoSuchElementException when there aren't any elements to return.
	 */
	public String peek() throws NoSuchElementException {
		if (isEmpty()) {
			throw new NoSuchElementException(getClass().getName() + " is empty");
		}

		Integer index = elementIndices.get(elementIndices.size() - 1);
		return index == null ? null : elementBuffer.substring(index);
	}

	/**
	 * Removes and returns the most recently added element of the Stack.
	 * @return The most recently added element.
	 * @throws NoSuchElementException when there aren't any elements to return.
	 */
	public String pop() throws NoSuchElementException {
		if (isEmpty()) {
			throw new NoSuchElementException(getClass().getName() + " is empty");
		}

		modificationCount++;
		Integer index = elementIndices.remove(elementIndices.size() - 1);
		if (index == null) {
			return null;
		} else {
			String value = elementBuffer.substring(index);
			elementBuffer.delete(index, elementBuffer.length());
			return value;
		}
	}

	/**
	 * Adds an element to the stack.
	 * @param e The element to add.
	 * @return The element that was added.
	 */
	public String push(String e) {
		if (e != null) {
			elementIndices.add(elementBuffer.length());
			elementBuffer.append(e);
		} else {
			elementIndices.add(null);
		}
		modificationCount++;
		return e;
	}

	public boolean remove(Object o) {
		for (Iterator<String> itr = this.iterator(); itr.hasNext(); ) {
			String s = itr.next();
			if ((o == null && s == null) || (s != null && s.equals(o))) {
				itr.remove();
				return true;
			}
		}
		return false;
	}

	public boolean removeAll(Collection<?> c) {
		if (c == null) {
			throw new NullPointerException("c cannot be null");
		}

		boolean changed = false;
		for (Object o : c) {
			changed |= remove(o);
		}
		return changed;
	}

	public boolean retainAll(Collection<?> c) {
		if (c == null) {
			throw new NullPointerException("c cannot be null");
		}

		boolean changed = false;
		for (Iterator<String> itr = this.iterator(); itr.hasNext(); ) {
			String s = itr.next();
			if (!c.contains(s)) {
				itr.remove();
				changed = true;
			}
		}
		return changed;
	}

	public int size() {
		return elementIndices.size();
	}

	public Object[] toArray() {
		return toArray(new Object[size()]);
	}

	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] a) {
		T[] array = a;
		if (array.length < this.size()) {
			array = (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size());
		}

		int idx = 0;
		for (String string : this) {
			array[idx] = (T) string;
			idx++;
		}

		if (array.length > this.size()) {
			array[size()] = null;
		}

		return array;
	}

	@Override
	public String toString() {
		// This creates a new string each time. Might be worth caching it.
		return elementBuffer.toString();
	}
	
}
