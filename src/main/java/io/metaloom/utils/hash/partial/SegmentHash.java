package io.metaloom.utils.hash.partial;

import io.metaloom.utils.hash.MD5;

public class SegmentHash {

	private final long start;
	private final int len;
	private final MD5 hash;

	public SegmentHash(long start, int len, MD5 hash) {
		this.start = start;
		this.len = len;
		this.hash = hash;
	}

	public MD5 getHash() {
		return hash;
	}

	public int getLen() {
		return len;
	}

	public long getStart() {
		return start;
	}

}
