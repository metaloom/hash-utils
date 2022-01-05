package io.metaloom.utils.hash;

import static org.junit.Assert.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import io.metaloom.utils.hash.impl.MemorySegmentHasher;

/**
 * Run Test with
 * 
 * <pre>
 *  --add-modules jdk.incubator.foreign --add-exports=java.base/sun.nio.ch=ALL-UNNAMED --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.lang.reflect=ALL-UNNAMED --add-opens=java.base/java.io=ALL-UNNAMED --add-exports=jdk.unsupported/sun.misc=ALL-UNNAMED
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
	public void testHugeFile() {
		String file = "/dev/zero";
		String hash = new MemorySegmentHasher().computeSHA512(Paths.get(file));
		System.out.println("Hash: " + hash);
	}

}
