package io.metaloom.utils.hash.impl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.function.Function;

import io.metaloom.utils.hash.AbstractHasher;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;

/**
 * Hasher implementation which is based on the jdk.incubator.foreign.MemorySegment#mapFile method. Using this hasher requires the user to enable the
 * jdk.incubator.foreign module.
 */
public final class MemorySegmentHasher extends AbstractHasher {

	@Override
	public byte[] hash(Path path, MessageDigest dig, Function<Long, Long> lenModifier) {
		try {
			long size = Files.size(path);
			if (size == 0) {
				byte[] result = dig.digest();
				return result; 
			}

			try (var scope = ResourceScope.newConfinedScope()) {
				MemorySegment segment = MemorySegment.mapFile(path,
					0,
					size,
					FileChannel.MapMode.READ_ONLY,
					scope);
				segment.isReadOnly();

				ByteBuffer buffer = segment.asByteBuffer();

				long start = 0;
				long len = Files.size(path);
				if (lenModifier != null) {
					len = lenModifier.apply(len);
				}
				int MAX_SIZE = 4096 * 128;
				while (start < len) {
					long remaining = len - start;
					int bufferSize = remaining < MAX_SIZE ? (int) remaining : MAX_SIZE;
					byte[] dst = new byte[bufferSize];
					buffer.get(dst);
					dig.update(dst);
					start += bufferSize;
				}
				byte[] result = dig.digest();
				return result;
			}
		} catch (IOException e) {
			throw new RuntimeException("Could not compute hash for {" + path + "}", e);
		}

	}

}
