package io.metaloom.utils.hash;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class MD5 extends AbstractStringHash<MD5> {

	private static final long serialVersionUID = 7488509115689279385L;

	public MD5(String hash) {
		super(hash, 32);
	}

	public static MD5 fromString(String hash) {
		if (hash == null) {
			return null;
		}
		return new MD5(hash);
	}

	public static MD5 fromBuffer(ByteBuffer b) {
		String hash = new String(b.array(), Charset.defaultCharset());
		return fromString(hash);
	}
}
