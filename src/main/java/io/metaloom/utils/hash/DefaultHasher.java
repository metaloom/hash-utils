package io.metaloom.utils.hash;

import io.metaloom.utils.hash.impl.MmapHasher;

public final class DefaultHasher {

	private static final Hasher INSTANCE = new MmapHasher();

	public static Hasher getHasher() {
		return INSTANCE;
	}
}
