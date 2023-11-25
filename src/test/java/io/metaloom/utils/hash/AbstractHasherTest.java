package io.metaloom.utils.hash;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.nio.file.Path;
import java.util.Random;

public abstract class AbstractHasherTest {

	private static final Random RANDOM = new Random();

	public static final int ZERO = 0;
	public static final MD5 ZERO_MD5 = MD5.fromString("d41d8cd98f00b204e9800998ecf8427e");
	public static final SHA256 ZERO_SHA256 = SHA256.fromString("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855");
	public static final SHA512 ZERO_SHA512 = SHA512.fromString("cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e");
	public static final ChunkHash ZERO_CHUNK_HASH = ChunkHash.fromString("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855");

	public static final int MINIMAL = 1;
	public static final MD5 MINIMAL_MD5 = MD5.fromString("02129bb861061d1a052c592e2dc6b383");
	public static final SHA256 MINIMAL_SHA256 = SHA256.fromString("4b68ab3847feda7d6c62c1fbcbeebfa35eab7351ed5e78f4ddadea5df64b8015");
	public static final SHA512 MINIMAL_SHA512 = SHA512.fromString("3173f0564ab9462b0978a765c1283f96f05ac9e9f8361ee1006dc905c153d85bf0e4c45622e5e990abcf48fb5192ad34722e8d6a723278b39fef9e4f9fc62378");
	public static final ChunkHash MINIMAL_CHUNK_HASH = ChunkHash.fromString("4b68ab3847feda7d6c62c1fbcbeebfa35eab7351ed5e78f4ddadea5df64b8015");

	public static final int SMALL = 20;
	public static final MD5 SMALL_MD5 = MD5.fromString("2e5892bbb905119aae6dfd3365bd90e5");
	public static final SHA256 SMALL_SHA256 = SHA256.fromString("0214f8ff8f7361495befb1e395fab0da8b29388248719b3fb03de39e3ad8f0f0");
	public static final SHA512 SMALL_SHA512 = SHA512.fromString("7a323f5559552f78d9dbe6084a3c7e994e1d8c343034e3709be233822c51ec60a96e630f38f438d79b468b62d0c725bdd0042674950ca24e6dc3a57d96532012");
	public static final ChunkHash SMALL_CHUNK_HASH = ChunkHash.fromString("0214f8ff8f7361495befb1e395fab0da8b29388248719b3fb03de39e3ad8f0f0");

	public static final int LARGE = 1024 * 1024 * 200; // 1MB + 200bytes
	public static final MD5 LARGE_MD5 = MD5.fromString("640c25f033f345cd7bf6d0610a2495df");
	public static final SHA256 LARGE_SHA256 = SHA256.fromString("504e8fd570a2bb5c761721084325c99308b49a7ad9a04abb08e9ff3dd4480076");
	public static final SHA512 LARGE_SHA512 = SHA512.fromString("89b00a8dbc605d105b13a18d45f69f040212c1bdfb1bb99db5b565a6852e60d1b2b70a7216f4082715f3e00bcc8caf4d6d6f62ba2cce39e3281f549e755cec93");
	public static final ChunkHash LARGE_CHUNK_HASH = ChunkHash.fromString("100673eb17f1299baa15a606947144eeb5b031a55c6e80f01ba6feaf35a46011");

	public static final long VERY_LARGE = 6L * 1024L * 1024L * 1024L + 128L; // 6 GB
	public static final SHA512 VERY_LARGE_SHA512 = SHA512.fromString("0880956965eb338f58569f411ebb69530c2e1eb17571c9ef7a96e2c4b7590a2c8b73a9ca5a538d4d063a5fa2215baf604e8bd45cc70fb067a870130abff46ef3");

	protected Path createTestFile(long nRandomBytes, int zeroChunkCount) {
		try {
			File file = new File("target/chunk_test_" + nRandomBytes + "_" + zeroChunkCount);
			if (file.exists()) {
				return file.toPath();
			}
			int chunkSize = 4096;
			int bufferSize = 4096;
			try (FileOutputStream fos = new FileOutputStream(file)) {
				long nBuffers = nRandomBytes / bufferSize;
				int remaining = (int) (nRandomBytes % bufferSize);
				for (int nBuffer = 0; nBuffer <= nBuffers; nBuffer++) {
					byte[] chunk = randomBytes(bufferSize);
					fos.write(chunk);
				}
				if (remaining != 0) {
					byte[] chunk = randomBytes(remaining);
					fos.write(chunk);
				}

				for (int nChunk = 0; nChunk < zeroChunkCount; nChunk++) {
					byte[] chunk = zeroChunk(chunkSize);
					fos.write(chunk);
				}
			}
			return file.toPath();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private byte[] zeroChunk(int chunkSize) {
		byte[] data = new byte[chunkSize];
		for (int i = 0; i < data.length; i++) {
			data[i] = 0;
		}
		return data;
	}

	private byte[] randomBytes(int chunkSize) {
		byte[] data = new byte[chunkSize];
		RANDOM.nextBytes(data);
		return data;
	}

	protected Path createTestFile(long size) {
		try {
			File file = new File("target/test_" + size);
			if (file.exists()) {
				return file.toPath();
			}
			String pattern = "X";
			if (size > 16) {
				size = size / 16;
				pattern = "ABCDEFGHABCDEFGH";
			}
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
				for (long i = 0; i < size; i++) {
					writer.append(pattern);
				}
			}
			return file.toPath();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected void time(long len, String prefix, Hasher hasher, Runnable code) {
		ThreadMXBean tm = ManagementFactory.getThreadMXBean();
		long before = tm.getThreadCpuTime(Thread.currentThread().threadId());
		code.run();
		long after = tm.getThreadCpuTime(Thread.currentThread().threadId());
		long delta = after - before;
		System.out.println(prefix + "@" + len + " > " + hasher + "\ttook: " + delta / 1000 + " ms");
	}
}
