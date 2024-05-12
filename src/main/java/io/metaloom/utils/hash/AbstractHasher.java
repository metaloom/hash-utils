package io.metaloom.utils.hash;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.atomic.AtomicInteger;
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
		AtomicInteger nZeroChunks = new AtomicInteger(0);
		try (RandomAccessFile rafile = new RandomAccessFile(path.toFile(), "r")) {
			FileChannel channel = rafile.getChannel();
			long start = 1 * 1024 * chunkSize; // 4 MB
			long len = channel.size();
			readChunks(channel, start, len, chunkSize * 128, buffer -> {
				int current = nZeroChunks.addAndGet(HashUtils.countZeroChunks(buffer, chunkSize));
				if (log.isTraceEnabled()) {
					log.trace("Found " + current + " zero chunks");
				}
				if (limit > 0 && current >= limit) {
					return false;
				}
				return true;
			});
		}
		return nZeroChunks.get();
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
