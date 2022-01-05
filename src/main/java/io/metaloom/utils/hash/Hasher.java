package io.metaloom.utils.hash;

import java.io.File;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.function.Function;
import static io.metaloom.utils.hash.HashUtils.bytesToHex;

/**
 * A hasher provides methods to hash files and data using various algorithms.
 */
public interface Hasher {

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
	default String computeChunkHash(Path path) {
		return bytesToHex(computeBinChunkHash(path));
	}

	/**
	 * Compute a chunk hash of the file. The used hasher will only utilize the first 5MB or 0.05% of the provided file to compute the hash. The idea behind this
	 * method is to provide a quicker way to hash a file with the mentioned trade of.
	 * 
	 * @param file
	 * @return
	 */
	default byte[] computeBinChunkHash(File file) {
		return computeBinChunkHash(file.toPath());
	}

	/**
	 * Compute a chunk hash of the file. The used hasher will only utilize the first 5MB or 0.05% of the provided file to compute the hash. The idea behind this
	 * method is to provide a quicker way to hash a file with the mentioned trade of.
	 * 
	 * @param path
	 * @return
	 */
	byte[] computeBinChunkHash(Path path);

	/**
	 * Compute the MD5 checksum of the data.
	 * 
	 * @param data
	 * @return
	 */
	String computeMD5(byte[] data);

	/**
	 * Compute the MD5 checksum.
	 * 
	 * @param file
	 * @return
	 */
	default String computeMD5(File file) {
		return computeMD5(file.toPath());
	}

	/**
	 * Compute the MD5 checksum.
	 * 
	 * @param path
	 * @return
	 */
	default String computeMD5(Path path) {
		return bytesToHex(computeBinMD5(path));
	}

	/**
	 * Compute the MD5 checksum.
	 * 
	 * @param file
	 * @return
	 */
	default byte[] computeBinMD5(File file) {
		return computeBinMD5(file.toPath());
	}

	/**
	 * Compute the MD5 checksum.
	 * 
	 * @param path
	 * @return
	 */
	byte[] computeBinMD5(Path path);

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
	 * Computes the SHA256 checksum.
	 * 
	 * @param file
	 * @return
	 */
	default byte[] computeBinSHA256(File file) {
		return computeBinSHA256(file.toPath());
	}

	/**
	 * Computes the SHA256 checksum.
	 * 
	 * @param path
	 * @return
	 */
	default String computeSHA256(Path path) {
		return bytesToHex(computeBinSHA256(path));
	}

	/**
	 * Compute the SHA256 checksum.
	 * 
	 * @param path
	 * @return
	 */
	byte[] computeBinSHA256(Path path);

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
	default String computeSHA512(Path path) {
		return bytesToHex(computeBinSHA512(path));
	}

	/**
	 * Compute the SHA512 checksum.
	 * 
	 * @param file
	 * @return
	 */
	default byte[] computeBinSHA512(File file) {
		return computeBinSHA512(file.toPath());
	}

	/**
	 * Compute the SHA512 checksum.
	 * 
	 * @param path
	 * @return
	 */
	byte[] computeBinSHA512(Path path);

	/**
	 * Hash implementation to be used to process the file.
	 * 
	 * @param path
	 *            Path of the file that should be processed.
	 * @param md
	 *            Actual digest to generate SHA256, SHA512
	 * @param lenModifier
	 *            Modifier which can be used to reduce the length of the data being hashed.
	 * @return Hashsum in binary form
	 */
	byte[] hash(Path path, MessageDigest md, Function<Long, Long> lenModifier);

}
