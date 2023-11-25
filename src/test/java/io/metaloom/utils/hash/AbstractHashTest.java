package io.metaloom.utils.hash;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public abstract class AbstractHashTest<T extends Comparable<T>> {

	@Test
	public void testNull() {
		assertNull(fromString(null));
	}

	@Test
	public void testValidHash() {
		T hash = fromString(hashA());
		assertNotNull(hash);
		assertEquals(hashA(), hash.toString());
		assertNotNull(fromString(hash.toString()));
	}

	@Test
	public void testInvalidHash() {
		assertThrows(RuntimeException.class, () -> {
			fromString("INVALID");
		});
	}

	@Test
	public void testEquals() {
		T h1 = fromString(hashA());
		T h2 = fromString(hashA());
		assertTrue(h1.equals(h2));

		T h3 = fromString(hashB());
		assertFalse(h1.equals(h3));
	}

	@Test
	public void testCompare() {
		T h1 = fromString(hashA());
		T h2 = fromString(hashA());
		assertTrue(h1.compareTo(h2) == 0);
	}

	protected abstract T fromString(String hex);

	protected abstract String hashA();

	protected abstract String hashB();

}
