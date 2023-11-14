package io.metaloom.utils.hash.partial;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout.OfByte;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

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

		try (RandomAccessFile rafile = new RandomAccessFile(file, "r")) {
			FileChannel channel = rafile.getChannel();
			long len = channel.size();
			MemorySegment seg = channel.map(FileChannel.MapMode.READ_ONLY, 0, len, Arena.ofConfined());
			long start = 1 * 1024 * chunkSize; // 4 MB
			zeroByteSize=0;
			while (start < len) {
				long remaining = len - start;
				int bufferSize = remaining < chunkSize ? (int) remaining : chunkSize;
				MemorySegment slice = seg.asSlice(start, bufferSize);
				byte[] chunk = slice.toArray(OfByte.JAVA_BYTE);
				if (HashUtils.isZeroChunk(chunk)) {
					zeroByteSize += chunkSize;
				}
				start += bufferSize;
			}
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

		try (RandomAccessFile rafile = new RandomAccessFile(file, "r")) {
			FileChannel fileChannel = rafile.getChannel();

			MemorySegment seg = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size(), Arena.ofConfined());

			long start = 1 * 1024 * CHUNK_SIZE; // 4 MB
			for (long i = start; i + CHUNK_SIZE < file.length(); i += CHUNK_SIZE) {
				MemorySegment slice = seg.asSlice(i, CHUNK_SIZE);
				byte[] chunk = slice.asByteBuffer().array();
				if (!HashUtils.isZeroChunk(chunk)) {
					// We were previously in zero area. This means a new chunk starts
					SegmentHash sh = new SegmentHash(i, CHUNK_SIZE, HashUtils.computeMD5(chunk));
					segmentHashes.add(sh);
				}
				// No need to collect more than 1024 non zero chunks (4 MB)
				if (segmentHashes.size() > 1024) {
					break;
				}
			}

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

	/**
	 * Compare the partial file to the provided file and return a factor that indicates how many chunks match up.
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 */
	public double compareTo(File file) throws IOException, NoSuchAlgorithmException {
		try (RandomAccessFile rafile = new RandomAccessFile(file, "r")) {
			FileChannel fileChannel = rafile.getChannel();

			MemorySegment seg = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size(), Arena.ofConfined());
			long score = 0;
			for (SegmentHash hash : computeHashes()) {
				long start = hash.getStart();
				MemorySegment slice = seg.asSlice(start, hash.getLen());
				byte[] dst = slice.asByteBuffer().array();
				String newHash = HashUtils.computeMD5(dst);
				if (newHash.equals(hash.getHash())) {
					score++;
				}
			}

			System.out.println("Matches: " + score + " out of " + computeHashes().size());
			return (double) score / (double) computeHashes().size();

		}
	}

}
