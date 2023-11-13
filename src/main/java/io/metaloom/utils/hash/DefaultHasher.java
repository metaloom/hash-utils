package io.metaloom.utils.hash;

import io.metaloom.utils.hash.impl.MemorySegmentHasher;

public final class DefaultHasher {

	private static final Hasher INSTANCE = new MemorySegmentHasher();

	public static Hasher getHasher() {
		return INSTANCE;
	}
}
