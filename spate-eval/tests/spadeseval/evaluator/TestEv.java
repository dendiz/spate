package spadeseval.evaluator;

import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

public class TestEv {
	
	@Test
	public void testEv5() throws FileNotFoundException, IOException, ClassNotFoundException {
		String[] h1 = new String[]{"As","Ah"}; 
		String[] h2 = new String[]{"Ad","Ac"};
		String[] board = new String[]{"Kc","Th","Kd"};
		String[][] pockets = new String[2][];
		pockets[0] = h1;
		pockets[1] = h2;
		float[] ev = SpadesEval.ev(pockets, board);
		assertTrue(ev[0] <= 1.0);
		assertTrue(ev[1] <= 1.0);
		assertTrue(ev[0] >= 0.0);
		assertTrue(ev[1] >= 0.0);
	}
	
	
	@Test
	public void testEv4() throws FileNotFoundException, IOException, ClassNotFoundException {
//		ArrayList<String> h1 = make_list("As, Ah");
//		ArrayList<String> h2 = make_list("Ad, Ac");
//		ArrayList<String> board = make_list("Kc, Th, Kd, 2h");
//		
//		ArrayList<ArrayList<String>> pockets = new ArrayList<ArrayList<String>>();
//		pockets.add(h1);
//		pockets.add(h2);
		String[] h1 = new String[]{"As","Ah"}; 
		String[] h2 = new String[]{"Ad","Ac"};
		String[] board = new String[]{"Kc","Th","Kd","2d"};
		String[][] pockets = new String[2][];
		pockets[0] = h1;
		pockets[1] = h2;		
		float[] ev = SpadesEval.ev(pockets, board);
		assertTrue(ev[0] <= 1.0);
		assertTrue(ev[1] <= 1.0);
		assertTrue(ev[0] >= 0.0);
		assertTrue(ev[1] >= 0.0);
		
	}
	
	
	@Test
	public void testEv3() throws FileNotFoundException, IOException, ClassNotFoundException {
//		ArrayList<String> h1 = make_list("As, Ah");
//		ArrayList<String> h2 = make_list("Ad, Ac");
//		ArrayList<String> board = make_list("Kc, Th, Kd, 2h, 2c");
//		
//		ArrayList<ArrayList<String>> pockets = new ArrayList<ArrayList<String>>();
//		pockets.add(h1);
//		pockets.add(h2);
		String[] h1 = new String[]{"As","Ah"}; 
		String[] h2 = new String[]{"Ad","Ac"};
		String[] board = new String[]{"Kc","Th","Kd","2h","2c"};
		String[][] pockets = new String[2][];
		pockets[0] = h1;
		pockets[1] = h2;		
		float[] ev = SpadesEval.ev(pockets, board);
		assertTrue(ev[0] <= 1.0);
		assertTrue(ev[1] <= 1.0);
		assertTrue(ev[0] >= 0.0);
		assertTrue(ev[1] >= 0.0);
		assertTrue(ev[0]-ev[1] < 2.0); //ev1 and ev2 should be very close
	}
	
	@Test
	public void testEv() throws FileNotFoundException, IOException, ClassNotFoundException {
//		ArrayList<String> h1 = make_list("As, Ah");
//		ArrayList<String> h2 = make_list("Kh, Ks");
//		ArrayList<String> board = make_list("Kc, Th, Kd, 2h, 2c");
//		
//		ArrayList<ArrayList<String>> pockets = new ArrayList<ArrayList<String>>();
//		pockets.add(h1);
//		pockets.add(h2);
		String[] h1 = new String[]{"As","Ah"}; 
		String[] h2 = new String[]{"Kh","Ks"};
		String[] board = new String[]{"Kc","Th","Kd","2h","2c"};
		String[][] pockets = new String[2][];
		pockets[0] = h1;
		pockets[1] = h2;		

		float[] ev = SpadesEval.ev(pockets, board);
		assertTrue(ev[0] <= 1.0);
		assertTrue(ev[1] <= 1.0);
		assertTrue(ev[0] >= 0.0);
		assertTrue(ev[1] >= 0.0);
		assertTrue(ev[1] == 1.0);
		assertTrue(ev[0] == 0.0); //can't beat quad kings
	}
	
	@Test
	public void testEv2() throws FileNotFoundException, IOException, ClassNotFoundException {
//		ArrayList<String> h1 = make_list("As, Ah");
//		ArrayList<String> h2 = make_list("__, Ad");
//		ArrayList<String> h3 = make_list("__, Ks");
//		ArrayList<String> board = make_list("Kc, Th, Kd, 2h, 2c");
//		
//		ArrayList<ArrayList<String>> pockets = new ArrayList<ArrayList<String>>();
//		pockets.add(h1);
//		pockets.add(h2);
//		pockets.add(h3);
		String[] h1 = new String[]{"As","Ah"}; 
		String[] h2 = new String[]{"__","Ad"};
		String[] h3 = new String[]{"__","Ks"};
		String[] board = new String[]{"Kc","Th","Kd","2h","2c"};
		String[][] pockets = new String[3][];
		pockets[0] = h1;
		pockets[1] = h2;
		pockets[2] = h3;
		
		float[] ev = SpadesEval.ev(pockets, board);
		assertTrue(ev[0] <= 1.0);
		assertTrue(ev[1] <= 1.0);
		assertTrue(ev[0] >= 0.0);
		assertTrue(ev[1] >= 0.0);
	}

}
