package io.metaloom.utils.hash;

import java.io.Serializable;

public abstract class AbstractByteHash<T> implements Comparable<T>, Serializable {

	private final byte[] hash;
	private final int len;
	
	public AbstractByteHash(byte[] hash, int len) {
		this.hash = hash;
		this.len = len;
		validate();
	}

	private void validate() {
		if (hash == null || hash.length != len) {
			throw new RuntimeException("Invalid hash len detected");
		}
	}

	@Override
	public int hashCode() {
		return hash.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return toString().equals(obj.toString());
	}

	@Override
	public int compareTo(T o) {
		return toString().compareTo(o.toString());
	}

	@Override
	public String toString() {
		return HashUtils.bytesToHex(hash);
	}
}
