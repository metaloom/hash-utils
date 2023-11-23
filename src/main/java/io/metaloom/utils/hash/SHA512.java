package io.metaloom.utils.hash;

public class SHA512 extends AbstractStringHash<SHA512> {

	private static final long serialVersionUID = -4774581209430311627L;

	public SHA512(String hash) {
		super(hash);
	}

	public static SHA512 fromString(String hash) {
		return new SHA512(hash);
	}
}
