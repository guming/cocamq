package org.jinn.cocamq.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashSet<E> extends AbstractSet<E> implements Set<E>,
		Cloneable, Serializable {
	static final long serialVersionUID = -5024744406713321676L;
	private transient ConcurrentHashMap<E, Object> map;
	private static final Object PRESENT = new Object();

	public ConcurrentHashSet() {
		this.map = new ConcurrentHashMap();
	}

	public ConcurrentHashSet(Collection<? extends E> paramCollection) {
		this.map = new ConcurrentHashMap(Math.max(
				(int) (paramCollection.size() / 0.75F) + 1, 16));
		addAll(paramCollection);
	}

	public ConcurrentHashSet(int paramInt, float paramFloat) {
		this.map = new ConcurrentHashMap(paramInt, paramFloat);
	}

	public ConcurrentHashSet(int paramInt) {
		this.map = new ConcurrentHashMap(paramInt);
	}

	ConcurrentHashSet(int paramInt, float paramFloat, boolean paramBoolean) {
		this.map = new ConcurrentHashMap(paramInt, paramFloat);
	}

	public Iterator<E> iterator() {
		return this.map.keySet().iterator();
	}

	public int size() {
		return this.map.size();
	}

	public boolean isEmpty() {
		return this.map.isEmpty();
	}

	public boolean contains(Object paramObject) {
		return this.map.containsKey(paramObject);
	}

	public boolean add(E paramE) {
		return this.map.put(paramE, PRESENT) == null;
	}

	public boolean remove(Object paramObject) {
		return this.map.remove(paramObject) == PRESENT;
	}

	public void clear() {
		this.map.clear();
	}

	public Object clone() {
		throw new IllegalArgumentException();
	}

	private void writeObject(ObjectOutputStream paramObjectOutputStream)
			throws IOException {
		throw new IllegalArgumentException();
	}

	private void readObject(ObjectInputStream paramObjectInputStream)
			throws IOException, ClassNotFoundException {
		throw new IllegalArgumentException();
	}
}