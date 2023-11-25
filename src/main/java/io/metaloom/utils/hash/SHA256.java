package io.metaloom.utils.hash;

public class SHA256 extends AbstractStringHash<SHA256> {

	private static final long serialVersionUID = -7411099443567585090L;

	public SHA256(String hash) {
		super(hash, 64);
	}

	public static SHA256 fromString(String hash) {
		if (hash == null) {
			return null;
		}
		return new SHA256(hash);
	}

}
