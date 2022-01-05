package io.metaloom.utils.hash;

import java.io.File;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

public final class HashUtils {

	private static final char[] hexArray = "0123456789abcdef".toCharArray();

	private static Hasher DEFAULT_HASHER = DefaultHasher.getHasher();

	protected HashUtils() {
	}

	public static String computeChunkHash(File file) {
		return DEFAULT_HASHER.computeChunkHash(file.toPath());
	}

	public static String computeChunkHash(Path path) {
		return DEFAULT_HASHER.computeChunkHash(path.toFile());
	}

	public static String computeMD5Hash(byte[] chunk) throws NoSuchAlgorithmException {
		return DEFAULT_HASHER.computeMD5(chunk);
	}

	public static String computeSHA256(File file) {
		return DEFAULT_HASHER.computeSHA256(file.toPath());
	}

	public static String computeSHA256(Path path) {
		return DEFAULT_HASHER.computeSHA256(path);
	}

	public static String computeSHA512(File file) {
		return DEFAULT_HASHER.computeSHA512(file.toPath());
	}

	public static String computeSHA512(Path path) {
		return DEFAULT_HASHER.computeSHA512(path);
	}

	public static String computeMD5(Path path) {
		return DEFAULT_HASHER.computeMD5(path);
	}

	public static String computeMD5(byte[] data) {
		return DEFAULT_HASHER.computeMD5(data);
	}

	/**
	 * Convert the byte array to a hex formatted string.
	 * 
	 * @param bytes
	 * @return
	 */
	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

}
