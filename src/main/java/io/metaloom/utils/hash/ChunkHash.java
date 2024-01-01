package io.metaloom.utils.hash;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

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

	public static ChunkHash fromBuffer(ByteBuffer b) {
		String hash = new String(b.array(), Charset.defaultCharset());
		return fromString(hash);
	}
}
