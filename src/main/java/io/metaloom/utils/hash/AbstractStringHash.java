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
		if (hash == null) {
			throw new RuntimeException("Invalid hash detected got null");
		} else if (hash.length() != len) {
			throw new RuntimeException("Invalid hash len detected got " + hash.length() + ", expected: " + len);
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

	/**
	 * Return a truncated 6 char long string representation of the hash.
	 * 
	 * @return
	 */
	public String shortHash() {
		if (hash == null) {
			return null;
		} else {
			return hash.substring(0, 6);
		}
	}

	@Override
	public String toString() {
		return hash;
	}
}
