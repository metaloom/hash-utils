package io.metaloom.utils.hash;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Run Test with
 * 
 * <pre>
 *  --add-exports=java.base/sun.nio.ch=ALL-UNNAMED --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.lang.reflect=ALL-UNNAMED --add-opens=java.base/java.io=ALL-UNNAMED --add-exports=jdk.unsupported/sun.misc=ALL-UNNAMED  --add-opens=java.base/java.io=util.mmap
 * </pre>
 *
 */
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
	@Disabled
	public void testVeryLargeFile() {
		Path file = Paths.get("PATH_TO_LARGE_FILE");
		Hasher hasher = DefaultHasher.getHasher();
		String h1 = hasher.computeSHA512(file);
		System.out.println(h1);
		String h2 = HashUtils.computeSHA512(file);
		assertEquals(h1, h2);
	}

	@Test
	public void testHash512RandomFile() {
		// time(len, "chunkHash", hasher, () -> {
		int len = LARGE;
		for (int i = 0; i < 10; i++) {
			Path path = createTestFile(len++, 0);
			Hasher hasher = DefaultHasher.getHasher();
			String hash = hasher.computeSHA512(path);
			System.out.println(len + " " + hash);
			assertNotNull(hash);
		}
		// });
	}

}
