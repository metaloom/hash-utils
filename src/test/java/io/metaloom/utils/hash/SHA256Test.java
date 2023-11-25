package io.metaloom.utils.hash;

public class SHA256Test extends AbstractHashTest<SHA256> {

	@Override
	public String hashA() {
		return "014191961f0d167b74dde7d5885fb799770ad7e3786ba744a08f2a1fc23dbe15";
	}

	@Override
	public String hashB() {
		return "014191961f0d167b74dde7d5885fb799770ad7e3786ba744a08f2a1fc23dbe16";
	}

	@Override
	protected SHA256 fromString(String hex) {
		return SHA256.fromString(hex);
	}

}
