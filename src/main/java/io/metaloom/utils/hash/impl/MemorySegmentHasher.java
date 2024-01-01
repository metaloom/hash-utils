package io.metaloom.utils.hash.impl;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout.OfByte;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.function.Function;

import io.metaloom.utils.hash.AbstractHasher;

public class MemorySegmentHasher extends AbstractHasher {

	@Override
	public byte[] hash(Path path, MessageDigest dig, Function<Long, Long> lenModifier) {
		try (RandomAccessFile rafile = new RandomAccessFile(path.toFile(), "r")) {
			FileChannel fileChannel = rafile.getChannel();
			long len = fileChannel.size();
			if (lenModifier != null) {
				len = lenModifier.apply(len);
			}
			int MAX_BUFFER_SIZE = 4096 * 128;
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
	public void readChunks(FileChannel channel, long start, long len, int chunkSize, Function<byte[], Boolean> chunkReader) throws IOException {
		MemorySegment seg = channel.map(FileChannel.MapMode.READ_ONLY, 0, len, Arena.ofConfined());
		while (start < len) {
			long remaining = len - start;
			int bufferSize = remaining < chunkSize ? (int) remaining : chunkSize;
			MemorySegment slice = seg.asSlice(start, bufferSize);
			byte[] dst = slice.toArray(OfByte.JAVA_BYTE);
			if(!chunkReader.apply(dst)) {
				break;
			}
			start += bufferSize;
		}
	}

}
