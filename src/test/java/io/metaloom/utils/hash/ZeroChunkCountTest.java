package io.metaloom.utils.hash;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import io.metaloom.utils.hash.impl.FileChannelHasher;
import io.metaloom.utils.hash.impl.MemorySegmentHasher;
import io.metaloom.utils.hash.partial.PartialFile;

public class ZeroChunkCountTest extends AbstractHasherTest {

	public static final int NO_ZERO_CHUNKS = 0;
	public static final int EMPTY = 0;
	public static final int NO_LIMIT = 0;
	public static final int ONE_CHUNK = 1 * 4096;
	public static final int TEN_CHUNKS = 10 * 4096;
	public static final int ONE_K_CHUNKS = 1024 * 4096;
	public static final int ONE_K_OVER_CHUNKS = 1024 * 4096 + 1;

	private static Stream<Arguments> hashes() {
		Collection<Arguments> data = new ArrayList<>();
		for (Hasher hasher : Arrays.asList(new MemorySegmentHasher(), new FileChannelHasher())) {
			data.add(arguments(hasher));
		}
		return data.stream();
	}

	@ParameterizedTest
	@MethodSource("hashes")
	public void testNoZeroChunkCount(Hasher hasher) throws IOException, NoSuchAlgorithmException {
		assertZeroCountCheck(hasher, createTestFile(EMPTY, 0), NO_ZERO_CHUNKS, NO_LIMIT);
		assertZeroCountCheck(hasher, createTestFile(ONE_CHUNK, 0), NO_ZERO_CHUNKS, NO_LIMIT);
		assertZeroCountCheck(hasher, createTestFile(TEN_CHUNKS, 0), NO_ZERO_CHUNKS, NO_LIMIT);
		assertZeroCountCheck(hasher, createTestFile(ONE_K_CHUNKS, 0), NO_ZERO_CHUNKS, NO_LIMIT);
		assertZeroCountCheck(hasher, createTestFile(ONE_K_OVER_CHUNKS, 0), NO_ZERO_CHUNKS, NO_LIMIT);
	}

	@ParameterizedTest
	@MethodSource("hashes")
	public void testZeroChunkCount(Hasher hasher) throws IOException, NoSuchAlgorithmException {
		assertZeroCountCheck(hasher, createTestFile(EMPTY, 3), 0, NO_LIMIT);
		assertZeroCountCheck(hasher, createTestFile(ONE_CHUNK, 3), 0, NO_LIMIT);
		assertZeroCountCheck(hasher, createTestFile(TEN_CHUNKS, 3), 0, NO_LIMIT);
		assertZeroCountCheck(hasher, createTestFile(ONE_K_CHUNKS, 3), 3, NO_LIMIT);

		// We only expect two complete zero chunks since the initial data is offset and thus the first read zero chunk does not fully contain zeros.
		assertZeroCountCheck(hasher, createTestFile(ONE_K_OVER_CHUNKS, 3), 2, NO_LIMIT);
	}

	@ParameterizedTest
	@MethodSource("hashes")
	public void testZeroChunkCountLimit(Hasher hasher) throws NoSuchAlgorithmException, IOException {
		assertZeroCountCheck(hasher, createTestFile(ONE_K_CHUNKS, 3), 3, 1);
	}

	private void assertZeroCountCheck(Hasher hasher, Path path, int expectedZeroChunks, int limit) throws NoSuchAlgorithmException, IOException {
		assertEquals(expectedZeroChunks, hasher.computeZeroChunkCount(path, limit), "The hasher did not return the expected amount of zero chunks");
		long count = new PartialFile(path).computeZeroChunkCount(4096, limit);
		assertEquals(expectedZeroChunks, count);
	}

}
