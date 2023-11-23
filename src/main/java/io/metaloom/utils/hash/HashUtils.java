package io.metaloom.utils.hash;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

public final class HashUtils {

	private static final char[] hexArray = "0123456789abcdef".toCharArray();

	private static Hasher DEFAULT_HASHER = DefaultHasher.getHasher();

	public static final int DEFAULT_ZERO_CHUNK_SIZE = 4096;

	protected HashUtils() {
	}

	public static String computeChunkHash(File file) {
		return DEFAULT_HASHER.computeChunkHash(file.toPath());
	}

	public static String computeChunkHash(Path path) {
		return DEFAULT_HASHER.computeChunkHash(path);
	}

	public static byte[] computeBinChunkHash(Path path) {
		return DEFAULT_HASHER.computeBinChunkHash(path);
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

	public static byte[] computeBinSHA256(Path path) {
		return DEFAULT_HASHER.computeBinSHA256(path);
	}

	public static String computeSHA512(File file) {
		return DEFAULT_HASHER.computeSHA512(file.toPath());
	}

	public static String computeSHA512(Path path) {
		return DEFAULT_HASHER.computeSHA512(path);
	}

	public static byte[] computeBinSHA512(Path path) {
		return DEFAULT_HASHER.computeBinSHA512(path);
	}

	public static String computeMD5(Path path) {
		return DEFAULT_HASHER.computeMD5(path);
	}

	public static byte[] computeBinMD5(Path path) {
		return DEFAULT_HASHER.computeBinMD5(path);
	}

	public static String computeMD5(byte[] data) {
		return DEFAULT_HASHER.computeMD5(data);
	}

	public static int computeZeroChunkCount(Path path) throws IOException {
		return DEFAULT_HASHER.computeZeroChunkCount(path);
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

	/**
	 * Convert the hex encoded string to an byte array
	 * 
	 * @param hex
	 * @return
	 */
	public static byte[] hexToBytes(String hex) {
		int len = hex.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
				+ Character.digit(hex.charAt(i + 1), 16));
		}
		return data;
	}

	/**
	 * Check if the provided data consists of zeros.
	 * 
	 * @param chunk
	 * @return
	 */
	public static boolean isFullZeroChunk(byte[] chunk) {
		if (chunk.length != DEFAULT_ZERO_CHUNK_SIZE) {
			return false;
		}
		for (int i = 0; i < chunk.length; i++) {
			if (chunk[i] != 0) {
				return false;
			}
		}
		return true;
	}

	public static Path segmentPath(Path basePath, SHA512 hash) {
		String prefix = hash.toString().substring(0, 4);
		return basePath.resolve(prefix);
	}
}
