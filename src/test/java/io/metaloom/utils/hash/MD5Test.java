package io.metaloom.utils.hash;

public class MD5Test extends AbstractHashTest<MD5> {

	@Override
	protected String hashA() {
		return "905bbc1db51c00b5946a680583a76d18";
	}

	@Override
	protected String hashB() {
		return "905bbc1db51c00b5946a680583a76d19";
	}

	@Override
	protected MD5 fromString(String hash) {
		return MD5.fromString(hash);
	}

}
