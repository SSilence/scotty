package scotty.ui;

import org.junit.Before;
import org.junit.Test;

public class SystrayManagerTest {
	private SystrayManager underTest;

	@Before
	public void setup() throws Exception {

	}

	@Test
	public void testSetRunning_isSupportedIsFalse_Transition() {
		underTest = new SystrayManager() {
			@Override
			public boolean isSupported() {
				return false;
			}
		};
		underTest.setRunning(true);
		underTest.setRunning(false);
		underTest.setRunning(true);
	}

	@Test
	public void testSetRunning_When_IsSupported_False() {
		underTest = new SystrayManager() {
			@Override
			public boolean isSupported() {
				return false;
			}
		};
		underTest.setRunning(true);
	}

	@Test
	public void testSetTooltip_When_IsSupported_False() {
		underTest = new SystrayManager() {
			@Override
			public boolean isSupported() {
				return false;
			}
		};
		underTest.setTooltip("asdf");
	}
}
