package io.metaloom.utils.hash.impl;

import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.function.Function;

import io.metaloom.utils.hash.AbstractHasher;

/**
 * Hasher implementation which uses the {@link FileChannel} to map regions to memory.
 */
public class FileChannelHasher extends AbstractHasher {

	@Override
	public byte[] hash(Path path, MessageDigest dig, Function<Long, Long> lenModifier) {

		try (RandomAccessFile rafile = new RandomAccessFile(path.toFile(), "r")) {
			FileChannel fileChannel = rafile.getChannel();

			MappedByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());

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
		} catch (Exception e) {
			throw new RuntimeException("Could not compute hash for {" + path + "}", e);
		}

	}

}
