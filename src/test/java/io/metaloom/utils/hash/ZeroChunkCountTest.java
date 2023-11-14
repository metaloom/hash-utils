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

	public static final int TEST_RUNS = 5;
	public static final int EMPTY = 0;
	public static final int ONE_CHUNK = 1 * 4096;
	public static final int TEN_CHUNKS = 10 * 4096;
	public static final int ONE_K_CHUNKS = 1024 * 4096;
	public static final int ONE_K_OVER_CHUNKS = 1024 * 4096 + 1;

	private static Stream<Arguments> hashes() {
		Collection<Arguments> data = new ArrayList<>();
		for (Hasher hasher : Arrays.asList(new MemorySegmentHasher(), new FileChannelHasher())) {
			data.add(arguments(hasher, ZERO, 20, 0));
			data.add(arguments(hasher, ONE_CHUNK, 20, 0));
			data.add(arguments(hasher, TEN_CHUNKS, 20, 0));
			data.add(arguments(hasher, ONE_K_CHUNKS, 2, 3));
			data.add(arguments(hasher, ONE_K_OVER_CHUNKS, 2, 2 - 1));
		}
		return data.stream();
	}

	@ParameterizedTest
	@MethodSource("hashes")
	public void testNoZeroChunkCount(Hasher hasher, long len, int nZeroChunks) throws IOException, NoSuchAlgorithmException {
		Path path = createTestFile(len, 0);
		long count = new PartialFile(path).computeZeroChunkCount();
		// time(len, "chunkHash", hasher, () -> {
		// for (int i = 0; i < TEST_RUNS; i++) {
		assertEquals(0, count);
		assertEquals(0, hasher.computeZeroChunkCount(path));
		// }
		// });
	}

	@ParameterizedTest
	@MethodSource("hashes")
	public void testZeroChunkCount(Hasher hasher, long len, int nZeroChunk, int expectedZeroChunks) throws IOException, NoSuchAlgorithmException {
		Path path = createTestFile(len, nZeroChunk);
		long count = new PartialFile(path).computeZeroChunkCount();
		// time(len, "chunkHash", hasher, () -> {
		// for (int i = 0; i < TEST_RUNS; i++) {
		int nZeroBytes = hasher.computeZeroChunkCount(path);
		assertEquals(count, nZeroBytes);
		assertEquals(expectedZeroChunks, nZeroBytes != 0 ? nZeroBytes / 4096 : 0,
			"The hasher did not return the expected amount of zero chunks");
		// }
		// });
	}

}
