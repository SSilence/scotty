package scotty.transformer.impl;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.*;

import java.util.Arrays;

import org.easymock.classextension.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import scotty.crypto.CryptoException;

import static org.junit.Assert.*;

public class DefaultRequestTransformerTest {

	/**
	 * Test merging of two byte arrays
	 */
	@Test
	public void testMerge() {
		byte[] array1 = new byte[] { 'a', 'b', 'c' };
		byte[] array2 = new byte[] { 'd', 'e' };
		byte[] expected = new byte[] { 'd', 'e', 'a', 'b', 'c' };
		byte[] merged = DefaultRequestTransformer.merge(array2, array1);
		assertArrayEquals(expected, merged);
	}
}
