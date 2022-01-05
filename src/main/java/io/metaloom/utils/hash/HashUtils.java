package io.metaloom.utils.hash;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indeed.util.mmap.MMapBuffer;

import de.jotschi.utils.ConvertUtils;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;

public class HashUtils {

	public static final Logger log = LoggerFactory.getLogger(HashUtils.class);

	protected static final char[] hexArray = "0123456789abcdef".toCharArray();

	public static MessageDigest md256;
	public static MessageDigest md512;

	static {
		try {
			md256 = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		try {
			md512 = MessageDigest.getInstance("SHA-512");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	public static String computeHash(byte[] chunk) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(chunk);
		return getHash(md);
	}

	public static String computeMD5Hash(byte[] chunk) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(chunk);
		return getHash(md);
	}

	public static String computeSHA256(Path path) {
		return fileChannelHash(path, md256, null);
	}

	public static String computeSHA256viaDirectMemoryMmap(Path path) {
		return jniMMappedHash(path, md256, null);
	}

	public static String compute2SHA256viaFileChannel(Path path) {
		return fileChannelHash(path, md256, null);
	}

	public static String computeSHA256ViaMemorySegment(Path path) {
		return jvmMemorySegmentHash(path, md256, null);
	}

	public static String computeSHA512viaDirectMemoryMmap(Path path) {
		return jniMMappedHash(path, md512, null);
	}

	public static String compute2SHA512viaFileChannel(Path path) {
		return fileChannelHash(path, md512, null);
	}

	public static String computeSHA512ViaMemorySegment(Path path) {
		return jvmMemorySegmentHash(path, md512, null);
	}

	public static String computeChunkHash(Path path) {
		long MINIMUM_CHUNK_HASH_SIZE = 5 * 1024 * 1024; // 5 MB
		double MINIMUM_PERCENT = 0.05;

		return jniMMappedHash(path, md256, len -> {
			long size = len;
			if (log.isDebugEnabled()) {
				log.debug("Size:" + ConvertUtils.toHumanSize(size));
			}
			len = (long) (len * MINIMUM_PERCENT);
			if (log.isDebugEnabled()) {
				log.debug("Len:" + ConvertUtils.toHumanSize(len));
			}
			// If the minimum via percent
			if (len < MINIMUM_CHUNK_HASH_SIZE) {
				len = MINIMUM_CHUNK_HASH_SIZE;
				// Cap check
				if (len > size) {
					len = size;
				}
			}
			if (log.isDebugEnabled()) {
				log.debug("Len:" + ConvertUtils.toHumanSize(len));
			}
			return len;

		});
	}

	/**
	 * Convert the byte array to a hex formatted string.
	 * 
	 * @param bytes
	 * @return
	 */
	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	private static String getHash(MessageDigest md) {
		byte[] hashBytes = md.digest();
		StringBuilder sb = new StringBuilder(2 * hashBytes.length);
		for (byte b : hashBytes) {
			sb.append(String.format("%02x", b & 0xff));
		}
		return sb.toString();
	}

	private static String jniMMappedHash(Path path, MessageDigest dig, Function<Long, Long> lenModifier) {
		try (MMapBuffer buffer = new MMapBuffer(
			path,
			FileChannel.MapMode.READ_ONLY,
			ByteOrder.LITTLE_ENDIAN)) {

			int MAX_SIZE = 4096 * 128;
			long start = 0;
			long len = Files.size(path);
			if (lenModifier != null) {
				len = lenModifier.apply(len);
			}
			while (start < len) {
				long remaining = len - start;
				int bufferSize = remaining < MAX_SIZE ? (int) remaining : MAX_SIZE;
				byte[] dst = new byte[bufferSize];
				buffer.memory().getBytes(start, dst);
				dig.update(dst);
				start += bufferSize;
			}
			byte[] result = dig.digest();
			return bytesToHex(result);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Could not compute hash for {" + path + "}", e);
		}
	}

	public static String jvmMemorySegmentHash(Path path, MessageDigest dig, Function<Long, Long> lenModifier) {
		try {
			long size = Files.size(path);
			if (size == 0) {
				byte[] result = dig.digest();
				return HashUtils.bytesToHex(result);
			}

			try (var scope = ResourceScope.newConfinedScope()) {
				MemorySegment segment = MemorySegment.mapFile(path,
					0,
					size,
					FileChannel.MapMode.READ_ONLY,
					scope);
				segment.isReadOnly();

				ByteBuffer buffer = segment.asByteBuffer();

				long start = 0;
				long len = Files.size(path);
				if (lenModifier != null) {
					len = lenModifier.apply(len);
				}
				int MAX_SIZE = 4096 * 128;
				while (start < len) {
					long remaining = len - start;
					int bufferSize = remaining < MAX_SIZE ? (int) remaining : MAX_SIZE;
					byte[] dst = new byte[bufferSize];
					buffer.get(dst);
					dig.update(dst);
					start += bufferSize;
				}
				byte[] result = dig.digest();
				return HashUtils.bytesToHex(result);
			}
		} catch (IOException e) {
			throw new RuntimeException("Could not compute hash for {" + path + "}", e);
		}

	}

	private static String fileChannelHash(Path path, MessageDigest dig, Function<Long, Long> lenModifier) {

		try (RandomAccessFile rafile = new RandomAccessFile(path.toFile(), "r")) {
			FileChannel fileChannel = rafile.getChannel();

			MappedByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());

			long start = 0;
			long len = Files.size(path);
			if (lenModifier != null) {
				len = lenModifier.apply(len);
			}
			int MAX_SIZE = 4096 * 128;
			while (start < len) {
				long remaining = len - start;
				int bufferSize = remaining < MAX_SIZE ? (int) remaining : MAX_SIZE;
				byte[] dst = new byte[bufferSize];
				buffer.get(dst);
				dig.update(dst);
				start += bufferSize;
			}
			byte[] result = dig.digest();
			return HashUtils.bytesToHex(result);

		} catch (Exception e) {
			throw new RuntimeException("Could not compute hash for {" + path + "}", e);
		}

	}

}
