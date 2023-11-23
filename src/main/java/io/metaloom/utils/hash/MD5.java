package io.metaloom.utils.hash;

public class MD5 extends AbstractStringHash<MD5> {

	private static final long serialVersionUID = 7488509115689279385L;

	public MD5(String hash) {
		super(hash);
	}

	public static MD5 fromString(String hash) {
		return new MD5(hash);
	}
}
