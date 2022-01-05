package io.metaloom.utils.hash;

import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.metaloom.utils.ConvertUtils;

public abstract class AbstractHasher implements Hasher {

	public static final Logger log = LoggerFactory.getLogger(HashUtils.class);

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
	public String computeChunkHash(Path path) {
		long MINIMUM_CHUNK_HASH_SIZE = 5 * 1024 * 1024; // 5 MB
		double MINIMUM_PERCENT = 0.05;

		return hash(path, md256, len -> {
			long size = len;
			if (log.isDebugEnabled()) {
				log.debug("Size: " + ConvertUtils.toHumanSize(size));
			}
			len = (long) (len * MINIMUM_PERCENT);
			if (log.isDebugEnabled()) {
				log.debug("Len: " + ConvertUtils.toHumanSize(len));
			}
			// If the minimum via percent
			if (len < MINIMUM_CHUNK_HASH_SIZE) {
				len = MINIMUM_CHUNK_HASH_SIZE;
				// Cap check
				if (len > size) {
					len = size;
				}
			}
			if (log.isDebugEnabled()) {
				log.debug("Len:" + ConvertUtils.toHumanSize(len));
			}
			return len;

		});
	}

	@Override
	public String computeSHA256(Path path) {
		return hash(path, md256, null);
	}

	@Override
	public String computeSHA512(Path path) {
		return hash(path, md512, null);
	}

	@Override
	public String computeMD5(byte[] data) throws NoSuchAlgorithmException {
		md5.update(data);
		return getHash(md5);
	}

	private String getHash(MessageDigest md) {
		byte[] hashBytes = md.digest();
		StringBuilder sb = new StringBuilder(2 * hashBytes.length);
		for (byte b : hashBytes) {
			sb.append(String.format("%02x", b & 0xff));
		}
		return sb.toString();
	}

	@Override
	public abstract String hash(Path path, MessageDigest md, Function<Long, Long> lenModifier);

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
