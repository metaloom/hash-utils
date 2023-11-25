package io.metaloom.utils.hash;

import java.io.Serializable;

public abstract class AbstractStringHash<T> implements Comparable<T>, Serializable {

	private final String hash;
	private final int len;

	public AbstractStringHash(String hash, int len) {
		this.hash = hash;
		this.len = len;
		validate();
	}

	private void validate() {
		if (hash == null || hash.length() != len) {
			throw new RuntimeException("Invalid hash len detected");
		}
	}

	@Override
	public int hashCode() {
		return hash.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return hash.equals(obj.toString());
	}

	@Override
	public int compareTo(T o) {
		return toString().compareTo(o.toString());
	}

	@Override
	public String toString() {
		return hash;
	}
}
