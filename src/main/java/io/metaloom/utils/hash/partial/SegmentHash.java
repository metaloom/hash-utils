package io.metaloom.utils.hash.partial;

public class SegmentHash {

	private final long start;
	private final int len;
	private final String hash;

	public SegmentHash(long start, int len, String hash) {
		this.start = start;
		this.len = len;
		this.hash = hash;
	}

	public String getHash() {
		return hash;
	}

	public int getLen() {
		return len;
	}

	public long getStart() {
		return start;
	}

}
