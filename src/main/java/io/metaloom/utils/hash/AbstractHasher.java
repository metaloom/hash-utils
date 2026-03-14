package io.metaloom.utils.hash;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractHasher implements Hasher {

	private static Logger log = LoggerFactory.getLogger(AbstractHasher.class);

	public static MessageDigest md5;
	public static MessageDigest md256;
	public static MessageDigest md512;

	static {
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		try {
			md256 = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		try {
			md512 = MessageDigest.getInstance("SHA-512");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	@Override
	public MD5 computeMD5(ByteBuffer buffer) {
		md5.update(buffer);
		return MD5.fromString(getHashAsHex(md5));
	}

	@Override
	public byte[] computeBinMD5(Path path) {
		return hash(path, md5, null);
	}

	@Override
	public byte[] computeBinSHA256(Path path) {
		return hash(path, md256, null);
	}

	@Override
	public byte[] computeBinSHA512(Path path) {
		return hash(path, md512, null);
	}

	@Override
	public byte[] computeBinChunkHash(Path path) {
		long MINIMUM_CHUNK_HASH_SIZE = 5 * 1024 * 1024; // 5 MB
		double MINIMUM_PERCENT = 0.05;

		return hash(path, md256, len -> {
			long size = len;
			len = (long) (len * MINIMUM_PERCENT);
			// If the minimum via percent
			if (len < MINIMUM_CHUNK_HASH_SIZE) {
				len = MINIMUM_CHUNK_HASH_SIZE;
				// Cap check
				if (len > size) {
					len = size;
				}
			}
			return len;

		});
	}

	@Override
	public int computeZeroChunkCount(Path path, int limit) throws IOException {
		final int chunkSize = HashUtils.DEFAULT_ZERO_CHUNK_SIZE;
		final int readBufferSize = chunkSize * 1024;
		int nZeroChunks = 0;
		try (RandomAccessFile rafile = new RandomAccessFile(path.toFile(), "r");
			FileChannel channel = rafile.getChannel()) {
			long start = 1L * 1024 * chunkSize; // 4 MB
			long len = channel.size();
			if (start >= len) {
				return 0;
			}

			long mappableLen = len - start;
			long fullChunkBytes = (mappableLen / chunkSize) * chunkSize;
			if (fullChunkBytes == 0) {
				return 0;
			}

			for (long blockStart = 0; blockStart < fullChunkBytes; blockStart += readBufferSize) {
				long blockLen = Math.min(readBufferSize, fullChunkBytes - blockStart);
				int blockZeroChunks = 0;
				try (Arena arena = Arena.ofConfined()) {
					MemorySegment segment = channel.map(FileChannel.MapMode.READ_ONLY, start + blockStart, blockLen, arena);
					for (long chunkStart = 0; chunkStart < blockLen; chunkStart += chunkSize) {
						if (isFullZeroChunk(segment, chunkStart, chunkSize)) {
							blockZeroChunks++;
						}
					}
				}
				nZeroChunks += blockZeroChunks;
				if (limit > 0 && nZeroChunks >= limit) {
					break;
				}
			}
			if (log.isTraceEnabled()) {
				log.trace("Found " + nZeroChunks + " zero chunks");
			}
		}
		return nZeroChunks;
	}

	private boolean isFullZeroChunk(MemorySegment segment, long start, int chunkSize) {
		long end = start + chunkSize;
		long i = start;
		for (; i + Long.BYTES <= end; i += Long.BYTES) {
			if (segment.get(ValueLayout.JAVA_LONG, i) != 0L) {
				return false;
			}
		}
		for (; i < end; i++) {
			if (segment.get(ValueLayout.JAVA_BYTE, i) != 0) {
				return false;
			}
		}
		return true;
	}

	private String getHashAsHex(MessageDigest md) {
		byte[] hashBytes = md.digest();
		return HashUtils.bytesToHex(hashBytes);
	}

	@Override
	public abstract byte[] hash(Path path, MessageDigest md, Function<Long, Long> lenModifier);

	@Override
	public abstract void readChunks(FileChannel channel, long start, long len, int chunkSize, Function<ByteBuffer, Boolean> chunkData)
		throws IOException;

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
