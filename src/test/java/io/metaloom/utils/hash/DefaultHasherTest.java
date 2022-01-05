package io.metaloom.utils.hash;

import static org.junit.Assert.assertEquals;

import java.nio.file.Path;

import org.junit.Test;

/**
 * Run Test with
 * 
 * <pre>
 *  --add-exports=java.base/sun.nio.ch=ALL-UNNAMED --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.lang.reflect=ALL-UNNAMED --add-opens=java.base/java.io=ALL-UNNAMED --add-exports=jdk.unsupported/sun.misc=ALL-UNNAMED
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

}
