package io.metaloom.utils.hash;

import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.function.Function;

public abstract class AbstractHasher implements Hasher {

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
	public String computeMD5(byte[] data) {
		md5.update(data);
		return getHash(md5);
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

	private String getHash(MessageDigest md) {
		byte[] hashBytes = md.digest();
		StringBuilder sb = new StringBuilder(2 * hashBytes.length);
		for (byte b : hashBytes) {
			sb.append(String.format("%02x", b & 0xff));
		}
		return sb.toString();
	}

	@Override
	public abstract byte[] hash(Path path, MessageDigest md, Function<Long, Long> lenModifier);

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
