package io.metaloom.utils.hash;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.nio.file.Path;

public abstract class AbstractHasherTest {

	public static final int ZERO = 0;
	public static final String ZERO_SHA256 = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
	public static final String ZERO_SHA512 = "cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e";
	public static final String ZERO_CHUNK_HASH = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
	public static final String ZERO_MD5 = "d41d8cd98f00b204e9800998ecf8427e";

	public static final int MINIMAL = 1;
	public static final String MINIMAL_MD5 = "02129bb861061d1a052c592e2dc6b383";
	public static final String MINIMAL_SHA256 = "4b68ab3847feda7d6c62c1fbcbeebfa35eab7351ed5e78f4ddadea5df64b8015";
	public static final String MINIMAL_SHA512 = "3173f0564ab9462b0978a765c1283f96f05ac9e9f8361ee1006dc905c153d85bf0e4c45622e5e990abcf48fb5192ad34722e8d6a723278b39fef9e4f9fc62378";
	public static final String MINIMAL_CHUNK_HASH = "4b68ab3847feda7d6c62c1fbcbeebfa35eab7351ed5e78f4ddadea5df64b8015";

	public static final int SMALL = 20;
	public static final String SMALL_MD5 = "1cb17f857d5b4bab8268d22f376ce61c";
	public static final String SMALL_SHA256 = "0400a7657decac81974d4e96db9fc8c84df4bb0db0fb454909ecb4b93ffd654b";
	public static final String SMALL_SHA512 = "60285b36eac3782ac220ddd227fdd23af194783046494251a4c40d0d7445fa205fc6a32f072d4fc4e6603fcde4b521d8f93854d4acd2b13d9d1e8868cdd867ef";
	public static final String SMALL_CHUNK_HASH = "0400a7657decac81974d4e96db9fc8c84df4bb0db0fb454909ecb4b93ffd654b";

	public static final int LARGE = 1024 * 1024 * 200;
	public static final String LARGE_MD5 = "4ffb188aec3a9596d49d7ad307d2c47d";
	public static final String LARGE_SHA256 = "1cf6b3be283d1022c26426d4f5d13f5303d044126cd76c5fa0e834d837bdb0ca";
	public static final String LARGE_SHA512 = "32698caf5f32c2e4d6b7ad4ca851e0f5e130bf054231eaa7345a27c31776ffb039f2a0070a78cca0ff82b7bb6303fdaa0aa2bce580101c7eadf9bae6adf6e29c";
	public static final String LARGE_CHUNK_HASH = "5449ecf9b06ad82bf75a0f1de67f1d8b40142c416dc93039c84a3ae6a98bb2cb";

	protected Path createTestFile(long size) {
		try {
			File file = new File("target/test_" + size);
			if (file.exists()) {
				return file.toPath();
			}
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
				for (long i = 0; i < size; i++) {
					writer.append("X");
				}
			}
			return file.toPath();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected void time(long len, String prefix, Hasher hasher, Runnable code) {
		ThreadMXBean tm = ManagementFactory.getThreadMXBean();
		long before = tm.getThreadCpuTime(Thread.currentThread().getId());
		code.run();
		long after = tm.getThreadCpuTime(Thread.currentThread().getId());
		long delta = after - before;
		System.out.println(prefix + "@" + len + ">" + hasher + "\ttook: " + delta / 1000 + " ms");
	}
}
