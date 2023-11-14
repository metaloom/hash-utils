package io.metaloom.utils.hash;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

public class DefaultHasherTest extends AbstractHasherTest {

	@Test
	public void testBasics() {
		Path file = createTestFile(LARGE);
		Hasher hasher = DefaultHasher.getHasher();
		String h1 = hasher.computeSHA512(file);
		assertEquals(LARGE_SHA512, h1);
		String h2 = HashUtils.computeSHA512(file);
		assertEquals(h1, h2);
	}

	@Test
	public void testVeryLargeFile() {
		Path file = createTestFile(VERY_LARGE);
		Hasher hasher = DefaultHasher.getHasher();
		String h1 = hasher.computeSHA512(file);
		assertEquals(VERY_LARGE_SHA512, h1);
	}

	@Test
	public void testHash512RandomFile() {
		// time(len, "chunkHash", hasher, () -> {
		int len = LARGE;
		for (int i = 0; i < 10; i++) {
			Path path = createTestFile(len++, 0);
			Hasher hasher = DefaultHasher.getHasher();
			String hash = hasher.computeSHA512(path);
			assertNotNull(hash);
		}
		// });
	}

}
