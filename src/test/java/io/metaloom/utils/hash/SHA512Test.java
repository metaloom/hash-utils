package io.metaloom.utils.hash;

public class SHA512Test extends AbstractHashTest<SHA512> {

	String validHash = "";

	@Override
	protected String hashA() {
		return "91e6e6749fb5a0e2e9f3f8c27831d7c9aaa5d49e9e0c89f95dad1fb0b7ea99fe90d9b098b35b0b8f4e317e563e1a29bd647ad48288d90a2268d2faae295d2d94";
	}

	@Override
	protected String hashB() {
		return "91e6e6749fb5a0e2e9f3f8c27831d7c9aaa5d49e9e0c89f95dad1fb0b7ea99fe90d9b098b35b0b8f4e317e563e1a29bd647ad48288d90a2268d2faae295d2d95";
	}

	@Override
	protected SHA512 fromString(String hash) {
		return SHA512.fromString(hash);
	}

}
