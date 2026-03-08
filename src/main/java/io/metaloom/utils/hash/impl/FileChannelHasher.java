package io.metaloom.utils.hash.impl;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
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
			long len = fileChannel.size();
			if (lenModifier != null) {
				len = lenModifier.apply(len);
			}
			int maxSize = 4096 * 128;
			readChunks(fileChannel, 0, len, maxSize, chunk -> {
				dig.update(chunk);
				return true;
			});
			return dig.digest();
		} catch (Exception e) {
			throw new RuntimeException("Could not compute hash for {" + path + "}", e);
		}

	}

	@Override
	public void readChunks(FileChannel channel, long start, long len, int chunkSize, Function<ByteBuffer, Boolean> chunkReader) throws IOException {
		if (chunkSize <= 0) {
			throw new IllegalArgumentException("chunkSize must be larger than 0");
		}
		long cursor = Math.max(0, start);
		long end = Math.min(len, channel.size());
		ByteBuffer buffer = ByteBuffer.allocate(chunkSize);
		while (cursor < end) {
			int bufferSize = (int) Math.min((long) chunkSize, end - cursor);
			buffer.clear();
			buffer.limit(bufferSize);
			int read = channel.read(buffer, cursor);
			if (read <= 0) {
				break;
			}
			buffer.flip();
			if (!chunkReader.apply(buffer)) {
				break;
			}
			cursor += read;
		}

	}

}
