package io.metaloom.utils.hash.impl;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout.OfByte;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.function.Consumer;
import java.util.function.Function;

import io.metaloom.utils.hash.AbstractHasher;

public class MemorySegmentHasher extends AbstractHasher {

	@Override
	public byte[] hash(Path path, MessageDigest dig, Function<Long, Long> lenModifier) {
		try (RandomAccessFile rafile = new RandomAccessFile(path.toFile(), "r")) {
			FileChannel fileChannel = rafile.getChannel();
			long len = fileChannel.size();

			MemorySegment seg = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, len, Arena.ofConfined());

			long start = 0;
			if (lenModifier != null) {
				len = lenModifier.apply(len);
			}
			int MAX_SIZE = 4096 * 128;
			while (start < len) {
				long remaining = len - start;
				int bufferSize = remaining < MAX_SIZE ? (int) remaining : MAX_SIZE;
				MemorySegment slice = seg.asSlice(start, bufferSize);
				byte[] dst = slice.toArray(OfByte.JAVA_BYTE);
				dig.update(dst);
				start += bufferSize;
			}
			byte[] result = dig.digest();
			return result;
		} catch (Exception e) {
			throw new RuntimeException("Could not compute hash for {" + path + "}", e);
		}
	}

	@Override
	public void readChunks(FileChannel channel, int chunkSize, Consumer<byte[]> chunkReader) throws IOException {
		long len = channel.size();
		MemorySegment seg = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size(), Arena.ofConfined());
		long start = 1 * 1024 * chunkSize; // 4 MB
		while (start < len) {
			long remaining = len - start;
			int bufferSize = remaining < chunkSize ? (int) remaining : chunkSize;
			MemorySegment slice = seg.asSlice(start, bufferSize);
			byte[] dst = slice.toArray(OfByte.JAVA_BYTE);
			chunkReader.accept(dst);
			start += bufferSize;
		}
	}

}
