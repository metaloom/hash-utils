package io.metaloom.utils.hash;

public class ChunkHash extends AbstractStringHash<ChunkHash> {

	private static final long serialVersionUID = -4774581209430311627L;

	public ChunkHash(String hash) {
		super(hash, 64);
	}

	public static ChunkHash fromString(String hash) {
		if (hash == null) {
			return null;
		}
		return new ChunkHash(hash);
	}
}
