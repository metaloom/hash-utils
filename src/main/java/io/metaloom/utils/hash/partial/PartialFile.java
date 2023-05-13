package io.metaloom.utils.hash.partial;

import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import com.indeed.util.mmap.MMapBuffer;

import io.metaloom.utils.hash.HashUtils;

public class PartialFile {

	private final File file;
	private long size;
	private long zeroByteSize = -1;
	private List<SegmentHash> segmentHashes = new ArrayList<>();

	public final int CHUNK_SIZE = 4096;

	public PartialFile(Path path) {
		this.file = path.toFile();
		this.size = file.length();
	}

	public long computeZeroChunkCount() throws NoSuchAlgorithmException, IOException {
		return computeZeroChunkCount(CHUNK_SIZE);
	}

	public long computeZeroChunkCount(int chunkSize) throws NoSuchAlgorithmException, IOException {
		if (zeroByteSize != -1) {
			return zeroByteSize;
		}

		MMapBuffer buffer = new MMapBuffer(
			file,
			FileChannel.MapMode.READ_ONLY,
			ByteOrder.LITTLE_ENDIAN);

		try {
			long start = 1 * 1024 * chunkSize; // 4 MB
			for (long i = start; i + chunkSize < file.length(); i += chunkSize) {
				byte[] chunk = new byte[chunkSize];
				buffer.memory().getBytes(i, chunk);
				if (isZeroChunk(chunk)) {
					zeroByteSize += chunkSize;
				}
			}
		} finally {
			buffer.close();
		}
		if (zeroByteSize == -1) {
			zeroByteSize = 0;
		}

		return zeroByteSize;
	}

	public List<SegmentHash> computeHashes() throws NoSuchAlgorithmException, IOException {
		if (!segmentHashes.isEmpty()) {
			return segmentHashes;
		}

		long size = file.length();
		if (size < 10 * 1024 * 1024) {
			System.out.println("File smaller than 10MB");
			return segmentHashes;
		}

		MMapBuffer buffer = new MMapBuffer(
			file,
			FileChannel.MapMode.READ_ONLY,
			ByteOrder.LITTLE_ENDIAN);

		try {
			long start = 1 * 1024 * CHUNK_SIZE; // 4 MB
			for (long i = start; i + CHUNK_SIZE < file.length(); i += CHUNK_SIZE) {
				byte[] chunk = new byte[CHUNK_SIZE];
				buffer.memory().getBytes(i, chunk);
				if (!isZeroChunk(chunk)) {
					// We were previously in zero area. This means a new chunk starts
					SegmentHash sh = new SegmentHash(i, CHUNK_SIZE, HashUtils.computeMD5(chunk));
					segmentHashes.add(sh);
				}
				// No need to collect more than 1024 non zero chunks (4 MB)
				if (segmentHashes.size() > 1024) {
					break;
				}
			}
		} finally {
			buffer.close();
		}
		return segmentHashes;
	}

	public File file() {
		return file;
	}

	public long size() {
		return size;
	}

	public String name() {
		return file.getName();
	}

	@Override
	public String toString() {
		return file.getAbsolutePath() + " size: " + size;
	}

	private static boolean isZeroChunk(byte[] chunk) {
		for (int i = 0; i < chunk.length; i++) {
			if (chunk[i] != 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Compare the partial file to the provided file and return a factor that indicates how many chunks match up.
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 */
	public double compareTo(File file) throws IOException, NoSuchAlgorithmException {
		MMapBuffer buffer = new MMapBuffer(
			file,
			FileChannel.MapMode.READ_ONLY,
			ByteOrder.LITTLE_ENDIAN);
		try {
			long score = 0;
			for (SegmentHash hash : computeHashes()) {
				long start = hash.getStart();
				byte[] dst = new byte[hash.getLen()];
				buffer.memory().getBytes(start, dst);
				String newHash = HashUtils.computeMD5(dst);
				if (newHash.equals(hash.getHash())) {
					score++;
				}
			}

			System.out.println("Matches: " + score + " out of " + computeHashes().size());
			return (double) score / (double) computeHashes().size();
		} finally {
			buffer.close();
		}
	}

}