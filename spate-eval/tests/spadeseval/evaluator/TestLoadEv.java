package spadeseval.evaluator;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

public class TestLoadEv {

	@Test
	public void testEv() throws FileNotFoundException, IOException, ClassNotFoundException {
//		ArrayList<String> h1 = make_list("As, Ah");
//		ArrayList<String> h2 = make_list("Ad, Ac");
//		ArrayList<String> board = make_list("Kc, Th, Kd");
//		
//		ArrayList<ArrayList<String>> pockets = new ArrayList<ArrayList<String>>();
//		pockets.add(h1);
//		pockets.add(h2);
		String[] h1 = new String[]{"As","Ah"}; 
		String[] h2 = new String[]{"Ad","Ac"};
		String[] board = new String[]{"Kc","Th","Kd"};
		String[][] pockets = new String[2][];
		pockets[0] = h1;
		pockets[1] = h2;		
		SpadesEval.init_rank_table();
		
		double total = 0;
		for (int i=0;i<1000;i++) {
			long t1 = System.currentTimeMillis();
			SpadesEval.ev(pockets, board);
			total += System.currentTimeMillis() - t1;
		}
		System.out.println("total (ms):" + total);
		System.out.println("Avg (ms):" + (total/1000));
	}

}
