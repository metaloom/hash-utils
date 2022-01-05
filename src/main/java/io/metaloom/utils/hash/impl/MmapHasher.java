package io.metaloom.utils.hash.impl;

import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.function.Function;

import com.indeed.util.mmap.MMapBuffer;

import io.metaloom.utils.hash.AbstractHasher;

public class MmapHasher extends AbstractHasher {

	@Override
	public byte[] hash(Path path, MessageDigest dig, Function<Long, Long> lenModifier) {
		try (MMapBuffer buffer = new MMapBuffer(
			path,
			FileChannel.MapMode.READ_ONLY,
			ByteOrder.LITTLE_ENDIAN)) {

			int MAX_SIZE = 4096 * 128;
			long start = 0;
			long len = Files.size(path);
			if (lenModifier != null) {
				len = lenModifier.apply(len);
			}
			while (start < len) {
				long remaining = len - start;
				int bufferSize = remaining < MAX_SIZE ? (int) remaining : MAX_SIZE;
				byte[] dst = new byte[bufferSize];
				buffer.memory().getBytes(start, dst);
				dig.update(dst);
				start += bufferSize;
			}
			byte[] result = dig.digest();
			return result;
		} catch (IOException e) {
			throw new RuntimeException("Could not compute hash for {" + path + "}", e);
		}
	}

}
