package io.metaloom.utils.hash;

/**
 * Binary backed MD5 hash representation.
 */
public class MD5Bin extends AbstractByteHash<MD5Bin> {

	public MD5Bin(byte[] hash) {
		super(hash, 16);
	}

	private static final long serialVersionUID = -8868627270170641940L;

	public static MD5Bin fromString(String hash) {
		if (hash == null) {
			return null;
		}
		return new MD5Bin(HashUtils.hexToBytes(hash));
	}

	public static MD5Bin from(byte[] hash) {
		if (hash == null) {
			return null;
		}
		return new MD5Bin(hash);
	}

}
