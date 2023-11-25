package io.metaloom.utils.hash;

public class ChunkHashTest extends AbstractHashTest<ChunkHash> {

	@Override
	protected String hashA() {
		return "41e705d6dc411b7444d760ceba3765d3d47397dbef3deb3260a86f0eecb41e94";
	}

	@Override
	protected String hashB() {
		return "41e705d6dc411b7444d760ceba3765d3d47397dbef3deb3260a86f0eecb41e95";
	}

	@Override
	protected ChunkHash fromString(String hash) {
		return ChunkHash.fromString(hash);
	}

}
