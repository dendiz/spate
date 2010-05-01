package spadeseval.evaluator;

import junit.framework.TestCase;

import org.junit.Test;

public class TestHandRank extends TestCase {

	@Test
	public void testRank2() {
		
		String[] nuttin = new String[]{"As", "Ah", "Kc", "Th", "Kd", "2h", "2c"};
		String[] pair = new String[]{"Kh", "Ks", "Kc", "Th", "Kd", "2h", "2c"};
		try {
			
			assertTrue(SpadesEval.rank(nuttin) < SpadesEval.rank(pair));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	@Test
	public void testRank() {
		
		
		String[] nuttin = new String[]{"2h", "Qh", "Js", "9h", "3d", "7s", "8c"};
		String[] pair = new String[]{"Jh", "Qh", "Js", "9h", "3d", "7s", "8c"};
		String[] twopair = new String[]{"Jh", "7c", "Js", "9h", "3d", "7s", "8c"};
		String[] set = new String[]{"Kh", "Kc", "Ks", "9h", "3d", "7s", "8c"};
		String[] straight = new String[]{"Jh", "Tc", "9s", "9h", "3d", "7s", "8c"};
		String[] flush = new String[]{"Jh", "Th", "2h", "9h", "3d", "7h", "8c"};
		String[] full = new String[]{"Jh", "Js", "Qh", "Qd", "Qc", "Ks", "8c"};
		String[] four = new String[]{"Jh", "Js", "Qh", "Qd", "Qc", "Qs", "8c"};
		String[] sflush = new String[]{"Kh", "Qh", "Jh", "Th", "9h", "7s", "8c"};
		assertTrue( SpadesEval.rank(nuttin) < SpadesEval.rank(pair));
		assertTrue( SpadesEval.rank(nuttin) < SpadesEval.rank(twopair));
		assertTrue( SpadesEval.rank(nuttin) < SpadesEval.rank(set));
		assertTrue( SpadesEval.rank(nuttin) < SpadesEval.rank(straight));
		assertTrue( SpadesEval.rank(nuttin) < SpadesEval.rank(flush));
		assertTrue( SpadesEval.rank(nuttin) < SpadesEval.rank(full));
		assertTrue( SpadesEval.rank(nuttin) < SpadesEval.rank(four));
		assertTrue( SpadesEval.rank(nuttin) < SpadesEval.rank(sflush));

		assertTrue( SpadesEval.rank(pair) < SpadesEval.rank(twopair));
		assertTrue( SpadesEval.rank(pair) < SpadesEval.rank(set));
		assertTrue( SpadesEval.rank(pair) < SpadesEval.rank(straight));
		assertTrue( SpadesEval.rank(pair) < SpadesEval.rank(flush));
		assertTrue( SpadesEval.rank(pair) < SpadesEval.rank(full));
		assertTrue( SpadesEval.rank(pair) < SpadesEval.rank(four));
		assertTrue( SpadesEval.rank(pair) < SpadesEval.rank(sflush));

		assertTrue( SpadesEval.rank(twopair) < SpadesEval.rank(set));
		assertTrue( SpadesEval.rank(twopair) < SpadesEval.rank(straight));
		assertTrue( SpadesEval.rank(twopair) < SpadesEval.rank(flush));
		assertTrue( SpadesEval.rank(twopair) < SpadesEval.rank(full));
		assertTrue( SpadesEval.rank(twopair) < SpadesEval.rank(four));
		assertTrue( SpadesEval.rank(twopair) < SpadesEval.rank(sflush));


		assertTrue( SpadesEval.rank(set) < SpadesEval.rank(straight));
		assertTrue( SpadesEval.rank(set) < SpadesEval.rank(flush));
		assertTrue( SpadesEval.rank(set) < SpadesEval.rank(full));
		assertTrue( SpadesEval.rank(set) < SpadesEval.rank(four));
		assertTrue( SpadesEval.rank(set) < SpadesEval.rank(sflush));

		assertTrue( SpadesEval.rank(straight) < SpadesEval.rank(flush));
		assertTrue( SpadesEval.rank(straight) < SpadesEval.rank(full));
		assertTrue( SpadesEval.rank(straight) < SpadesEval.rank(four));
		assertTrue( SpadesEval.rank(straight) < SpadesEval.rank(sflush));


		assertTrue( SpadesEval.rank(flush) < SpadesEval.rank(full));
		assertTrue( SpadesEval.rank(flush) < SpadesEval.rank(four));
		assertTrue( SpadesEval.rank(flush) < SpadesEval.rank(sflush));

		assertTrue( SpadesEval.rank(full) < SpadesEval.rank(four));
		assertTrue( SpadesEval.rank(full) < SpadesEval.rank(sflush));			
		
	}

}
