package io.metaloom.utils.hash;

public class MD5BinTest extends AbstractHashTest<MD5Bin> {

	@Override
	protected String hashA() {
		return "905bbc1db51c00b5946a680583a76d18";
	}

	@Override
	protected String hashB() {
		return "905bbc1db51c00b5946a680583a76d19";
	}

	@Override
	protected MD5Bin fromString(String hash) {
		return MD5Bin.fromString(hash);
	}
}
