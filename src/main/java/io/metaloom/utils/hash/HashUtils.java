package io.metaloom.utils.hash;

import java.io.File;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

public final class HashUtils {

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

}
