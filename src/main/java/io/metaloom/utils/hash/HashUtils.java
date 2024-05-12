package io.metaloom.utils.hash;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;

public final class HashUtils {

	private static final char[] hexArray = "0123456789abcdef".toCharArray();

	private static Hasher DEFAULT_HASHER = DefaultHasher.getHasher();

	public static final int DEFAULT_ZERO_CHUNK_SIZE = 4096;

	protected HashUtils() {
	}

	// Chunk
	public static ChunkHash computeChunkHash(File file) {
		return computeChunkHash(file.toPath());
	}

	public static ChunkHash computeChunkHash(Path path) {
		return DEFAULT_HASHER.computeChunkHash(path);
	}

	public static byte[] computeBinChunkHash(Path path) {
		return DEFAULT_HASHER.computeBinChunkHash(path);
	}

	// SHA256

	public static SHA256 computeSHA256(File file) {
		return computeSHA256(file.toPath());
	}

	public static SHA256 computeSHA256(Path path) {
		return DEFAULT_HASHER.computeSHA256(path);
	}

	public static byte[] computeBinSHA256(Path path) {
		return DEFAULT_HASHER.computeBinSHA256(path);
	}

	// SHA512

	public static SHA512 computeSHA512(File file) {
		return computeSHA512(file.toPath());
	}

	public static SHA512 computeSHA512(Path path) {
		return DEFAULT_HASHER.computeSHA512(path);
	}

	public static byte[] computeBinSHA512(Path path) {
		return DEFAULT_HASHER.computeBinSHA512(path);
	}

	// MD5

	public static MD5 computeMD5(File file) {
		return computeMD5(file.toPath());
	}

	public static MD5 computeMD5(Path path) {
		return DEFAULT_HASHER.computeMD5(path);
	}

	public static MD5 computeMD5(byte[] data) {
		return DEFAULT_HASHER.computeMD5(data);
	}

	public static MD5 computeMD5(ByteBuffer buffer) {
		return DEFAULT_HASHER.computeMD5(buffer);
	}

	public static byte[] computeBinMD5(Path path) {
		return DEFAULT_HASHER.computeBinMD5(path);
	}

	// Zero Count

	public static int computeZeroChunkCount(Path path) throws IOException {
		return computeZeroChunkCount(path, 0);
	}

	public static int computeZeroChunkCount(Path path, int limit) throws IOException {
		return DEFAULT_HASHER.computeZeroChunkCount(path, limit);
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

		/*
		 * StringBuilder sb = new StringBuilder(2 * hashBytes.length); for (byte b : hashBytes) { sb.append(String.format("%02x", b & 0xff)); } return
		 * sb.toString();
		 */
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
	 * Slice the given buffer into chunks and test each one individually for zeros. Returns the total count of found zero chunks.
	 * 
	 * @param buffer
	 * @param chunkSize
	 * @return
	 */
	public static int countZeroChunks(ByteBuffer buffer, int chunkSize) {
		int zeroChunks = 0;
		while (buffer.remaining() >= chunkSize) {
			ByteBuffer slice = buffer.slice(buffer.position(), chunkSize);
			// Advance the buffer position
			buffer.position(buffer.position() + slice.remaining());
			if (HashUtils.isFullZeroChunk(slice, 4096)) {
				zeroChunks++;
			}
		}
		// Ignore the remaining bytes since it does not fill a full chunk
		return zeroChunks;
	}

	/**
	 * Check if the provided data consists of zeros.
	 * 
	 * @param chunk
	 * @param chunkSize
	 * @return
	 */
	public static boolean isFullZeroChunk(ByteBuffer chunk, int chunkSize) {
		if (chunk.remaining() != chunkSize) {
			return false;
		}
		// chunk.slice(0, chunkSize);
		while (chunk.hasRemaining()) {
			if (chunk.get() != 0) {
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
