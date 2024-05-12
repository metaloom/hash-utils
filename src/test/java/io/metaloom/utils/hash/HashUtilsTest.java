package io.metaloom.utils.hash;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

public class HashUtilsTest {

	public static final SHA512 HASH = SHA512.fromString(
		"e7c22b994c59d9cf2b48e549b1e24666636045930d3da7c1acb299d1c3b7f931f94aae41edda2c2b207a36e10f8bcb8d45223e54878f5b316e7ce3b6bc019629");

	@Test
	public void testSegmentPath() throws IOException {
		Path dest = Paths.get("target/test/e7c2");
		if (Files.exists(dest)) {
			Files.delete(dest);
		}
		Path folderPath = HashUtils.segmentPath(Paths.get("target/test"), HASH);
		assertEquals(folderPath, dest);
		assertFalse(Files.exists(dest));
	}

	@Test
	public void testIsFullZeroChunk() {
		ByteBuffer chunk = ByteBuffer.allocate(4096);
		assertTrue(HashUtils.isFullZeroChunk(chunk, 4096));
	}

	@Test
	public void testcountZeroChunks() {
		ByteBuffer buffer = ByteBuffer.allocate(4096);
		assertEquals(1, HashUtils.countZeroChunks(buffer, 4096));

		buffer = ByteBuffer.allocate(8192);
		assertEquals(2, HashUtils.countZeroChunks(buffer, 4096));

		buffer = ByteBuffer.allocate(8096 + 4095);
		assertEquals(2, HashUtils.countZeroChunks(buffer, 4096));
	}

	@Test
	public void testIsNoFullZeroChunk() {
		ByteBuffer chunk = ByteBuffer.allocate(4096);
		chunk.putInt(4096 - Integer.BYTES, 255);
		assertFalse(HashUtils.isFullZeroChunk(chunk, 4096));
	}
}
