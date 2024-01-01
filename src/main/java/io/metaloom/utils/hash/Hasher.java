package io.metaloom.utils.hash;

import static io.metaloom.utils.hash.HashUtils.bytesToHex;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.function.Function;

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
	default ChunkHash computeChunkHash(File file) {
		return computeChunkHash(file.toPath());
	}

	/**
	 * Compute a chunk hash of the file. The used hasher will only utilize the first 5MB or 0.05% of the provided file to compute the hash. The idea behind this
	 * method is to provide a quicker way to hash a file with the mentioned trade of.
	 * 
	 * @param path
	 * @return
	 */
	default ChunkHash computeChunkHash(Path path) {
		return ChunkHash.fromString(bytesToHex(computeBinChunkHash(path)));
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
	MD5 computeMD5(byte[] data);

	/**
	 * Compute the MD5 checksum.
	 * 
	 * @param file
	 * @return
	 */
	default MD5 computeMD5(File file) {
		return computeMD5(file.toPath());
	}

	/**
	 * Compute the MD5 checksum.
	 * 
	 * @param path
	 * @return
	 */
	default MD5 computeMD5(Path path) {
		return MD5.fromString(bytesToHex(computeBinMD5(path)));
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
	default SHA256 computeSHA256(File file) {
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
	default SHA256 computeSHA256(Path path) {
		return SHA256.fromString(bytesToHex(computeBinSHA256(path)));
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
	default SHA512 computeSHA512(File file) {
		return computeSHA512(file.toPath());
	}

	/**
	 * Computes the SHA512 checksum.
	 * 
	 * @param path
	 * @return
	 */
	default SHA512 computeSHA512(Path path) {
		return SHA512.fromString(bytesToHex(computeBinSHA512(path)));
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

	default int computeZeroChunkCount(Path path) throws IOException {
		return computeZeroChunkCount(path, 0);
	}

	/**
	 * Compute the amount of zero byte chunks (4kb) that could be found in the given file. The computation will be stopped when the limit of zero chunk has been
	 * reached. This can be used to speedup the processing.
	 * 
	 * @param path
	 * @param limit
	 * @return
	 */
	int computeZeroChunkCount(Path path, int limit) throws IOException;

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

	/**
	 * Chunk reader implementation which can be used to parse chunks of a file.
	 * 
	 * @param channel
	 *            Channel from which the chunks will be read
	 * @param start
	 *            Start offset from which to start reading
	 * @param len
	 *            Length of bytes to read
	 * @param chunkSize
	 *            Size of the chunks being read in bytes
	 * @param chunkData
	 *            Function which can process each chunk. Return true to process the next chunk. Otherwise the process will be stopped.
	 */
	void readChunks(FileChannel channel, long start, long len, int chunkSize, Function<byte[], Boolean> chunkData) throws IOException;

}
