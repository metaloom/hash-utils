package io.metaloom.utils.hash;

import java.io.File;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.function.Function;

/**
 * A hasher provides methods to hash files and data using various algorithms.
 */
public interface Hasher {

	static final char[] hexArray = "0123456789abcdef".toCharArray();

	/**
	 * Compute a chunk hash of the file. The used hasher will only utilize the first 5MB or 0.05% of the provided file to compute the hash. The idea behind this
	 * method is to provide a quicker way to hash a file with the mentioned trade of.
	 * 
	 * @param file
	 * @return
	 */
	default String computeChunkHash(File file) {
		return computeChunkHash(file.toPath());
	}

	/**
	 * Compute a chunk hash of the file. The used hasher will only utilize the first 5MB or 0.05% of the provided file to compute the hash. The idea behind this
	 * method is to provide a quicker way to hash a file with the mentioned trade of.
	 * 
	 * @param path
	 * @return
	 */
	String computeChunkHash(Path path);

	/**
	 * Compute the MD5 checksum of the data.
	 * 
	 * @param data
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	String computeMD5(byte[] data) throws NoSuchAlgorithmException;

	/**
	 * Computes the SHA256 checksum.
	 * 
	 * @param file
	 * @return
	 */
	default String computeSHA256(File file) {
		return computeSHA256(file.toPath());
	}

	/**
	 * Computes the SHA256 checksum
	 * 
	 * @param path
	 * @return
	 */
	String computeSHA256(Path path);

	/**
	 * Computes the SHA512 checksum.
	 * 
	 * @param file
	 * @return
	 */
	default String computeSHA512(File file) {
		return computeSHA512(file.toPath());
	}

	/**
	 * Computes the SHA512 checksum.
	 * 
	 * @param path
	 * @return
	 */
	String computeSHA512(Path path);

	/**
	 * Hash implementation to be used to process the file.
	 * 
	 * @param path
	 *            Path of the file that should be processed.
	 * @param md
	 *            Actual digest to generate SHA256, SHA512
	 * @param lenModifier
	 *            Modifier which can be used to reduce the length of the data being hashed.
	 * @return
	 */
	String hash(Path path, MessageDigest md, Function<Long, Long> lenModifier);

	/**
	 * Convert the byte array to a hex formatted string.
	 * 
	 * @param bytes
	 * @return
	 */
	static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}
}
