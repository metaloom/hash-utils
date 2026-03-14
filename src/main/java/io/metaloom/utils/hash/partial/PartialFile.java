package io.metaloom.utils.hash.partial;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.metaloom.utils.hash.HashUtils;
import io.metaloom.utils.hash.MD5;

public class PartialFile {

	public static final Logger log = LoggerFactory.getLogger(PartialFile.class);

	private final File file;
	private long size;
	private long nZeroChunks = -1;
	private List<SegmentHash> segmentHashes = new ArrayList<>();

	public final int CHUNK_SIZE = 4096;

	public PartialFile(Path path) {
		this.file = path.toFile();
		this.size = file.length();
	}

	public long computeZeroChunkCount() throws NoSuchAlgorithmException, IOException {
		return computeZeroChunkCount(CHUNK_SIZE, 0);
	}

	public long computeZeroChunkCount(int chunkSize, int limit) throws NoSuchAlgorithmException, IOException {
		// Return the cached result
		if (nZeroChunks != -1) {
			return nZeroChunks;
		}

		try {
			nZeroChunks = HashUtils.computeZeroChunkCount(file.toPath(), limit);
		} catch (Exception e) {
			log.error("Failed to compute zero chunk count", e);
			// In case of error we return 0
			nZeroChunks = 0;
		}

		return nZeroChunks;
	}

	/**
	 * Collect 4MB of chunk data from the partial file. Only non-zero chunks get picked.
	 * 
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	public List<SegmentHash> computeHashes() throws NoSuchAlgorithmException, IOException {
		if (!segmentHashes.isEmpty()) {
			return segmentHashes;
		}

		long size = file.length();
		if (size < 10 * 1024 * 1024) {
			if (log.isWarnEnabled()) {
				log.warn("File smaller than 10MB");
			}
			return segmentHashes;
		}

		try (RandomAccessFile rafile = new RandomAccessFile(file, "r");
			FileChannel channel = rafile.getChannel()) {

			long fileSize = channel.size();
			long start = 1L * 1024 * CHUNK_SIZE; // 4 MB
			int mapSize = CHUNK_SIZE * 1024; // 4 MB mapping window
			boolean done = false;
			for (long mapOffset = start; mapOffset < fileSize && !done; mapOffset += mapSize) {
				long mapLen = Math.min(mapSize, fileSize - mapOffset);
				try (Arena arena = Arena.ofConfined()) {
					MemorySegment seg = channel.map(FileChannel.MapMode.READ_ONLY, mapOffset, mapLen, arena);
					for (long off = 0; off + CHUNK_SIZE <= mapLen; off += CHUNK_SIZE) {
						MemorySegment slice = seg.asSlice(off, CHUNK_SIZE);
						ByteBuffer chunk = slice.asByteBuffer();
						if (!HashUtils.isFullZeroChunk(chunk, CHUNK_SIZE)) {
							chunk.position(0);
							SegmentHash sh = new SegmentHash(mapOffset + off, CHUNK_SIZE, HashUtils.computeMD5(chunk));
							segmentHashes.add(sh);
						}
						// No need to collect more than 1024 non zero chunks (4 MB)
						if (segmentHashes.size() > 1024) {
							done = true;
							break;
						}
					}
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
		try (RandomAccessFile rafile = new RandomAccessFile(file, "r");
			FileChannel fileChannel = rafile.getChannel()) {

			long score = 0;
			for (SegmentHash hash : computeHashes()) {
				long start = hash.getStart();
				try (Arena arena = Arena.ofConfined()) {
					MemorySegment seg = fileChannel.map(FileChannel.MapMode.READ_ONLY, start, hash.getLen(), arena);
					ByteBuffer chunk = seg.asByteBuffer();
					MD5 newHash = HashUtils.computeMD5(chunk);
					if (newHash.equals(hash.getHash())) {
						score++;
					}
				}
			}

			if (log.isDebugEnabled()) {
				log.debug("Matches: " + score + " out of " + computeHashes().size());
			}
			return (double) score / (double) computeHashes().size();

		}
	}

}
