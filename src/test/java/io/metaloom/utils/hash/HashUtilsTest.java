package io.metaloom.utils.hash;

import static org.junit.Assert.assertEquals;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import io.metaloom.utils.hash.impl.FileChannelHasher;
import io.metaloom.utils.hash.impl.MemorySegmentHasher;
import io.metaloom.utils.hash.impl.MmapHasher;

@RunWith(Parameterized.class)
public class HashUtilsTest extends AbstractHasherTest {

	public static final int TEST_RUNS = 1;

	// Parameters
	private final long len;
	private final String sha256;
	private final String sha512;
	private final String chunkHash;
	private final Hasher hasher;

	public HashUtilsTest(Hasher hasher, long len, String sha256, String sha512, String chunkHash) {
		this.hasher = hasher;
		this.len = len;
		this.sha256 = sha256;
		this.sha512 = sha512;
		this.chunkHash = chunkHash;
	}

	@Parameters(name = "{index}: hasher:({0}), filesize({1})")
	public static Collection<Object[]> data() {
		Collection<Object[]> data = new ArrayList<>();
		for (Hasher hasher : Arrays.asList(new MmapHasher(), new MemorySegmentHasher(), new FileChannelHasher())) {
			data.add(new Object[] { hasher, ZERO, ZERO_SHA256, ZERO_SHA512, ZERO_CHUNK_HASH });
			data.add(new Object[] { hasher, MINIMAL, MINIMAL_SHA256, MINIMAL_SHA512, MINIMAL_CHUNK_HASH });
			data.add(new Object[] { hasher, SMALL, SMALL_SHA256, SMALL_SHA512, SMALL_CHUNK_HASH });
			data.add(new Object[] { hasher, LARGE, LARGE_SHA256, LARGE_SHA512, LARGE_CHUNK_HASH });
		}
		return data;

	}

	@Test
	public void testHash512() {
		Path path = createTestFile(len);
		for (int i = 0; i < TEST_RUNS; i++) {
			String hash = hasher.computeSHA512(path);
			assertEquals(sha512, hash);
		}
	}

	@Test
	public void testHash256() {
		Path path = createTestFile(len);
		for (int i = 0; i < TEST_RUNS; i++) {
			String hash = HashUtils.computeSHA256(path);
			assertEquals(sha256, hash);
		}
	}

	@Test
	public void testChunkHash() {
		Path path = createTestFile(len);
		for (int i = 0; i < TEST_RUNS; i++) {
			String hash = HashUtils.computeChunkHash(path);
			assertEquals(chunkHash, hash);
		}
	}

}
