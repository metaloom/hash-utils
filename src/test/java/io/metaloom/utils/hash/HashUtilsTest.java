package io.metaloom.utils.hash;

import static io.metaloom.utils.hash.HashUtils.bytesToHex;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import io.metaloom.utils.hash.impl.FileChannelHasher;
import io.metaloom.utils.hash.impl.MemorySegmentHasher;

public class HashUtilsTest extends AbstractHasherTest {

	public static final int TEST_RUNS = 5;

	private static Stream<Arguments> hashes() {
		Collection<Arguments> data = new ArrayList<>();
		for (Hasher hasher : Arrays.asList(new MemorySegmentHasher(), new FileChannelHasher())) {
			data.add(arguments(hasher, ZERO, ZERO_MD5, ZERO_SHA256, ZERO_SHA512, ZERO_CHUNK_HASH));
			data.add(arguments(hasher, MINIMAL, MINIMAL_MD5, MINIMAL_SHA256, MINIMAL_SHA512, MINIMAL_CHUNK_HASH));
			data.add(arguments(hasher, SMALL, SMALL_MD5, SMALL_SHA256, SMALL_SHA512, SMALL_CHUNK_HASH));
			data.add(arguments(hasher, LARGE, LARGE_MD5, LARGE_SHA256, LARGE_SHA512, LARGE_CHUNK_HASH));
		}
		return data.stream();
	}

	@ParameterizedTest
	@MethodSource("hashes")
	public void testHashMD5(Hasher hasher, long len, String md5, String sha256, String sha512, String chunkHash) {
		Path path = createTestFile(len);
		// time(len,"md5", hasher, () -> {
		for (int i = 0; i < TEST_RUNS; i++) {
			assertEquals(md5, hasher.computeMD5(path), "Hashsum mismatch for " + path);
		}
		// });
		assertEquals(md5, bytesToHex(hasher.computeBinMD5(path.toFile())));
		assertEquals(md5, bytesToHex(hasher.computeBinMD5(path)));
		assertEquals(md5, hasher.computeMD5(path.toFile()));
	}

	@ParameterizedTest
	@MethodSource("hashes")
	public void testHash256(Hasher hasher, long len, String md5, String sha256, String sha512, String chunkHash) {
		Path path = createTestFile(len);
		// time(len, "sha256", hasher, () -> {
		for (int i = 0; i < TEST_RUNS; i++) {
			assertEquals(sha256, hasher.computeSHA256(path), "Hashsum mismatch for " + path);
		}
		// });
		assertEquals(sha256, bytesToHex(hasher.computeBinSHA256(path.toFile())));
		assertEquals(sha256, bytesToHex(hasher.computeBinSHA256(path)));
		assertEquals(sha256, hasher.computeSHA256(path.toFile()));
	}

	@ParameterizedTest
	@MethodSource("hashes")
	public void testHash512(Hasher hasher, long len, String md5, String sha256, String sha512, String chunkHash) {
		Path path = createTestFile(len);
		time(len, "sha512", hasher, () -> {
			for (int i = 0; i < TEST_RUNS; i++) {
				assertEquals(sha512, hasher.computeSHA512(path), "Hashsum mismatch for " + path);
			}
		});
		assertEquals(sha512, bytesToHex(hasher.computeBinSHA512(path.toFile())));
		assertEquals(sha512, bytesToHex(hasher.computeBinSHA512(path)));
		assertEquals(sha512, hasher.computeSHA512(path.toFile()));
	}

	@ParameterizedTest
	@MethodSource("hashes")
	public void testChunkHash(Hasher hasher, long len, String md5, String sha256, String sha512, String chunkHash) {
		Path path = createTestFile(len);
		// time(len, "chunkHash", hasher, () -> {
		for (int i = 0; i < TEST_RUNS; i++) {
			assertEquals(chunkHash, hasher.computeChunkHash(path), "Hashsum mismatch for " + path);
		}
		// });
		assertEquals(chunkHash, bytesToHex(hasher.computeBinChunkHash(path.toFile())));
		assertEquals(chunkHash, bytesToHex(hasher.computeBinChunkHash(path)));
		assertEquals(chunkHash, hasher.computeChunkHash(path.toFile()));
	}

}
