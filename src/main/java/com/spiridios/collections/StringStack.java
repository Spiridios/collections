package com.spiridios.collections;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

// TODO: concurrentModdificationException, clone?
public class StringStack implements Collection<String>, Serializable, Cloneable {
	private static final long serialVersionUID = -6797711532702199014L;
	private transient int modificationCount = 0;
	private StringBuffer elementBuffer;

	/**
	 * Index of the start of the string, null if the string's value is null.
	 */
	private ArrayList<Integer> elementIndices;

	private class StringStackIterator implements Iterator<String> {
		private int currentElementIndexIndex;
		private int expectedModificationCount;
		private boolean nextCalled = false;

		private StringStackIterator() {
			this.expectedModificationCount = modificationCount;
			this.currentElementIndexIndex = elementIndices.size();
		}

		public boolean hasNext() {
			return currentElementIndexIndex > 0;
		}

		public String next() {
			if (currentElementIndexIndex <= 0) {
				throw new NoSuchElementException();
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
	}

	public StringStack() {
		clear();
	}

	public int size() {
		return elementIndices.size();
	}

	public boolean isEmpty() {
		return elementIndices.isEmpty();
	}

	public boolean contains(Object o) {
		String eToFind = (String) o; // will throw classcast if o is not string
		for (String e : this) {
			if ((e == null && o == null) || (e != null && e.equals(eToFind))) {
				return true;
			}
		}
		return false;
	}

	public Iterator<String> iterator() {
		return new StringStackIterator();
	}

	// TODO: Contract order
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

	public boolean add(String e) {
		push(e);
		return true;
	}

	public boolean remove(Object o) {
		for (Iterator<String> itr = this.iterator(); itr.hasNext(); ) {
			String s = itr.next();
			if ((s == null && o == null) || (s != null && s.equals(o))) {
				itr.remove();
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

	public boolean addAll(Collection<? extends String> c) {
		boolean changed = false;
		for (String e : c) {
			changed |= add(e);
		}
		return changed;
	}

	public boolean removeAll(Collection<?> c) {
		boolean changed = false;
		for (Object o : c) {
			changed |= remove(o);
		}
		return changed;
	}

	public boolean retainAll(Collection<?> c) {
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

	public void clear() {
		elementBuffer = new StringBuffer();
		elementIndices = new ArrayList<Integer>();
		modificationCount = 0;
	}

	public String push(String e) {
		if (e != null) {
			elementIndices.add(elementBuffer.length());
			elementBuffer.append(e);
		} else {
			// throw new IllegalArgumentException("Null strings are not supported");
			elementIndices.add(null);
		}
		modificationCount++;
		return e;
	}

	public String peek() {
		if (isEmpty()) {
			throw new NoSuchElementException(getClass().getName() + " is empty");
		}

		Integer index = elementIndices.get(elementIndices.size() - 1);
		return index == null ? null : elementBuffer.substring(index);
	}

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((elementBuffer == null) ? 0 : elementBuffer.toString().hashCode());
		result = prime * result + ((elementIndices == null) ? 0 : elementIndices.hashCode());
		return result;
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
	
}