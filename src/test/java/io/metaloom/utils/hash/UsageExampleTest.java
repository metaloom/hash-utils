package io.metaloom.utils.hash;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

public class UsageExampleTest {

	@Test
	public void testMD5Usage() {
		// SNIPPET START md5.example
		Path path = Paths.get("pom.xml");
		String hash = HashUtils.computeMD5(path);
		byte[] binHash = HashUtils.computeBinMD5(path);
		// SNIPPET END md5.example

		assertNotNull(hash);
	}

	@Test
	public void testSHA256Usage() {
		// SNIPPET START sha256.example
		Path path = Paths.get("pom.xml");
		String hash = HashUtils.computeSHA256(path);
		byte[] binHash = HashUtils.computeBinSHA256(path);
		// SNIPPET END sha256.example
		assertNotNull(hash);
	}

	@Test
	public void testSHA512Usage() {
		// SNIPPET START sha512.example
		Path path = Paths.get("pom.xml");
		String hash = HashUtils.computeSHA512(path);
		byte[] binHash = HashUtils.computeBinSHA512(path);
		// SNIPPET END sha512.example
		assertNotNull(hash);

	}

	@Test
	public void testChunkHashUsage() {
		// SNIPPET START chunk.example
		Path path = Paths.get("pom.xml");
		String hash = HashUtils.computeChunkHash(path);
		byte[] binHash = HashUtils.computeBinChunkHash(path);
		// SNIPPET END chunk.example
		assertNotNull(hash);
	}

	@Test
	public void testZeroChunkCountUsage() throws IOException {
		// SNIPPET START zero.example
		Path path = Paths.get("pom.xml");
		int count = HashUtils.computeZeroChunkCount(path);
		// SNIPPET END zero.example
		assertEquals(0, count);
	}
}
