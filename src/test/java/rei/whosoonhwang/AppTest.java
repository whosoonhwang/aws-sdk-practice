package rei.whosoonhwang;

import static org.junit.Assert.*;
import org.junit.Test;

public class AppTest {
	@Test
	public void sumTest() {
		final int a=1, b=2;
		final int expected = 3;
		final int result = a+b;
		
		System.out.println(result);
		assertEquals(result, expected);
	}
}
