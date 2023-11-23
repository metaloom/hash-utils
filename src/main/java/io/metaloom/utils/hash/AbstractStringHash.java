package io.metaloom.utils.hash;

import java.io.Serializable;

public abstract class AbstractStringHash<T> implements Comparable<T>, Serializable {

	private final String hash;

	public AbstractStringHash(String hash) {
		this.hash = hash;
	}

	@Override
	public int hashCode() {
		return hash.hashCode();
	}

	@Override
	public String toString() {
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		return hash.equals(obj);
	}

	@Override
	public int compareTo(T o) {
		return toString().compareTo(o.toString());
	}

}
