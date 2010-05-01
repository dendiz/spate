package spadeseval.evaluator;

import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

public class TestWinners {

	@Test
	public void testWinners1() throws FileNotFoundException, IOException, ClassNotFoundException {
		String[] h1 = new String[]{"As","Ah"}; 
		String[] h2 = new String[]{"Ad","Kh"};
		String[] board = new String[]{"Jc","Th","3d", "2h", "2c"};
		String[][] pockets = new String[2][];
		pockets[0] = h1;
		pockets[1] = h2;		
		int[] winners = SpadesEval.winners(pockets, board);
		assertTrue(winners[0] == 0);
		assertTrue(winners.length == 1);
		
	}

	@Test
	public void testWinners2() throws FileNotFoundException, IOException, ClassNotFoundException {
		String[] h1 = new String[]{"As","Ah"}; 
		String[] h2 = new String[]{"Ad","Kh"};
		String[] board = new String[]{"Kc","Th","Kd", "2h", "2c"};
		String[][] pockets = new String[2][];
		pockets[0] = h1;
		pockets[1] = h2;		
		
		int[] winners = SpadesEval.winners(pockets, board);
		assertTrue(winners[0] == 1);
		assertTrue(winners.length == 1);
	}
	
	@Test
	public void testWinners3() throws FileNotFoundException, IOException, ClassNotFoundException {
		String[] h1 = new String[]{"As","Ah"}; 
		String[] h2 = new String[]{"Ad","Ac"};
		String[] board = new String[]{"Kc","Th","Kd", "2h", "2c"};
		String[][] pockets = new String[2][];
		pockets[0] = h1;
		pockets[1] = h2;		
		
		int[] winners = SpadesEval.winners(pockets, board);
		assertTrue(winners[0] == 0);
		assertTrue(winners[1] == 1);
		assertTrue(winners.length == 2);
	}
	
	@Test
	public void testWinners4() throws FileNotFoundException, IOException, ClassNotFoundException {
		String[] h1 = new String[]{"As","Ah"}; 
		String[] h2 = new String[]{"Ad","Ac"};
		String[] board = new String[]{"Kc","Th","Kd", "2h", "2c"};
		String[][] pockets = new String[2][];
		pockets[0] = h1;
		pockets[1] = h2;		

		for (int i=0; i<10000;i++){
			int[] winners = SpadesEval.winners(pockets, board);
			assertTrue(winners[0] == 0);
			assertTrue(winners[1] == 1);
			assertTrue(winners.length == 2);
		}
	}

}
