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

import java.util.Iterator;

/**
 * An iterator wrapper that converts a standard iterator into one that doesn't support the remove operation.
 * Useful if you're implementing Iterable but just passing the iterator implementation to an underlying
 * collection that you do not want modified. This is cheaper than creating an UnmodifiableCollection
 * from the underlying collection just to get an iterator.
 */
public class UnmodifiableIterator<T> implements Iterator<T> {
	private Iterator<T> iterator;
	
	public UnmodifiableIterator(Iterator<T> iterator) {
		this.iterator = iterator;
	}
	
	public boolean hasNext() {
		return iterator.hasNext();
	}

	public T next() {
		return iterator.next();
	}

	public void remove() {
		throw new UnsupportedOperationException("remove");
	}
}
