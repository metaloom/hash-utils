package io.metaloom.utils.hash;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
	public void testObjectEquals() {
		T h1 = fromString(hashA());
		T h2 = fromString(hashA());
		assertTrue(Objects.equals(h1, h2));
	}

	@Test
	public void testMapContains() {
		T h1 = fromString(hashA());
		T h2 = fromString(hashA());
		Map<T, T> map = new HashMap<>();
		map.put(h1, h1);
		assertTrue(map.containsKey(h1));
		assertTrue(map.containsKey(h2));

		assertTrue(map.containsValue(h1));
		assertTrue(map.containsValue(h2));
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
