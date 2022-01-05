package io.metaloom.utils.hash;

import io.metaloom.utils.hash.impl.FileChannelHasher;

public final class DefaultHasher {

	public static Hasher getHasher() {
		return new FileChannelHasher();
	}
}
