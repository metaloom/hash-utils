package io.metaloom.utils.hash.impl;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.function.Function;

import io.metaloom.utils.hash.AbstractHasher;

public class MemorySegmentHasher extends AbstractHasher {

	private static final int MAX_BUFFER_SIZE = 4096 * 128;

	@Override
	public byte[] hash(Path path, MessageDigest dig, Function<Long, Long> lenModifier) {
		try (RandomAccessFile rafile = new RandomAccessFile(path.toFile(), "r"); FileChannel fileChannel = rafile.getChannel()) {
			long len = fileChannel.size();
			if (lenModifier != null) {
				len = lenModifier.apply(len);
			}
			readChunks(fileChannel, 0, len, MAX_BUFFER_SIZE, chunk -> {
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
