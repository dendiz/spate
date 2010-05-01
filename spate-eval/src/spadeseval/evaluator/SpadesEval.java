package spadeseval.evaluator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SpadesEval {
	private static int table[] = null;
	private static final int WIN = 0;
	private static final int TIE = 2;
	private static final int NUM_MONTE_CARLO = 16000; //use 19 ms time on my amd xp3200 4 core
	private static final boolean DEBUG = false;
	/**
	 * implement an EV calculation using Monte Carlo
	 * @param pockets
	 * @param board
	 * @return
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static float[] ev(String[][] pockets, String[] board) throws FileNotFoundException, IOException, ClassNotFoundException {
		int[] virgindeck = new int[52];
		int[][] res = new int[pockets.length][3];
		int[] reducedDeck = null;
		
		//create a new deck
		for (int k=0;k<52;k++) virgindeck[k] = k+1;
		
		
		//clear the deck from player pocket cards
		reducedDeck = virgindeck;
		for (String[] playercards : pockets)
			for (String card: playercards)
				for (int i = 0;i<reducedDeck.length;i++)
					if (!card.equals("__") && reducedDeck[i] == toIndex(card)) reducedDeck = remove_at(reducedDeck, i);
		
		//clear the deck from board cards
		for (String card : board)
			for (int i = 0;i<reducedDeck.length;i++)
				if (!card.equals("__") && reducedDeck[i] == toIndex(card)) reducedDeck = remove_at(reducedDeck, i);
			
		double t1 = System.currentTimeMillis();
		for (int n=0;n<NUM_MONTE_CARLO;n++) {
			int[] gamedeck = new int[reducedDeck.length];
			System.arraycopy(reducedDeck, 0, gamedeck, 0, reducedDeck.length);
			
			int[][] pocketsArr = new int[pockets.length][2];

			//fill place holders in pocket cards with random cards.
			for (int k=0;k<pockets.length;k++) {
				String[] playercards = pockets[k];
				for (int j=0;j<playercards.length;j++) {
					String card = playercards[j];
					if (!card.equals("__")) pocketsArr[k][j] = toIndex(card);
					else {
						int randcard = (int)Math.floor(Math.random() * gamedeck.length);
						pocketsArr[k][j] = gamedeck[randcard];
						gamedeck = remove_at(gamedeck, randcard);
					}
				}
			}
			//copy the given board to the board array
			int[] boardArr = new int[5];
			for (int k=0;k<board.length;k++) boardArr[k] = toIndex(board[k]);
			
			//add any missing cards to the board
			for(int k=board.length;k<5;k++) {
				int randcard = (int)Math.floor(Math.random() * gamedeck.length);
				boardArr[k] = gamedeck[randcard];
				gamedeck = remove_at(gamedeck, randcard);
			}
			
			int[] winners = winners(pocketsArr, boardArr);
			if (winners.length == 1) res[winners[0]][WIN]++;
			else for (int w : winners) res[w][TIE]++;
		}
		float[] ev = new float[pockets.length];
		for (int k=0;k<pockets.length;k++) {
			ev[k] = (res[k][WIN] + (res[k][TIE]/2)) / (float) NUM_MONTE_CARLO;
		}
		if(DEBUG) debugEv(pockets, board, t1, ev);
		return ev;
	}

	private static void debugEv(String[][] pockets,
			String[] board, double t1, float[] ev) {
		System.out.print("pockets:");
		for (String[] pc:pockets){
			System.out.print("[");
			for (String c:pc){
				System.out.print(c+" ");
			}
			System.out.print("],");
		}
		System.out.print("board:");
		System.out.print("[");
		for(String b: board) {
			System.out.print(b+" ");
		}
		System.out.print("]");
		System.out.println("");
		System.out.println("MC time (ms):" + (System.currentTimeMillis() - t1));
		System.out.println("Ev results:");
		for (float f:ev) System.out.println(f);
	}
	
	private static int[] remove_at(int[] arr, int pos) {
		int[] res = new int[arr.length-1];
		int j = 0;
		for (int i=0;i<arr.length;i++) {
			if (i == pos) continue;
			res[j++] = arr[i];
		}
		return res;
	}
	
	public static int toIndex(String card) {
		char rank = card.charAt(0);
		char suit = card.charAt(1);
		char[] ranks = new char[]{'2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K', 'A'};
		char[] suits = new char[]{'c', 'd', 'h', 's'};
		
		int rankIdx;
		for (rankIdx=0;rankIdx<14;rankIdx++) {
			if (rankIdx == 13) throw new RuntimeException("Couldn't find rank " + rank + " in ranks.");
			if (rank == ranks[rankIdx]) break;
		}
		
		int suitIdx;
		for (suitIdx=0;suitIdx<5;suitIdx++) {
			if (suitIdx == 4) throw new RuntimeException("Couldn't find suit " + suit + " in suits.");
			if (suit == suits[suitIdx]) break;
		}

		return (4 * rankIdx + suitIdx) + 1; //2p2 indexes start from 1, so +1
	}



	public static int rank(String[] hand) {
		int[] handarray = make_array(hand);
		return rank(handarray);
	}
	
	private static int rank(int[] handarray) {
		if (handarray.length < 5) throw new RuntimeException("invalid hand size.");
		if (table == null) init_rank_table();
		int p = 53;
		for (int i=0;i<handarray.length;i++) p = table[p + handarray[i]];
		return p;
	}
	private static int[] make_array(String[] hand) {
		int[] handarray = new int[hand.length];
		int handarrIdx = 0;

		for (String card : hand) {
			handarray[handarrIdx++] = toIndex(card); 
		}
		return handarray;
	}

	public static void init_rank_table() {
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream("handRanks.dat"));
			table = (int[]) ois.readObject();
		} catch (Exception e) {
			System.err.println("Hand Ranks file not found. Generating new one. " + e.getMessage());
			Generator.generateTables();
			ObjectOutputStream oos;
			try {
				oos = new ObjectOutputStream(new FileOutputStream("handRanks.dat"));
				oos.writeObject(Generator.handRanks);
			} catch (Exception e1) {
				System.err.println("Hand Ranks file not saved to disk." + e.getMessage());
			} 
			table = Generator.handRanks;
		}
	}
	
	private static int[] winners(int[][] pockets, int[] board) {
		int[] scores = new int[pockets.length];
		/*
		 * merge the pocket cards and board and evaluate the hand rank.
		 */
		for (int i = 0; i < pockets.length;i++) {
			int[] hand = new int[7];
			for (int k=0;k<pockets[i].length;k++) hand[k] = pockets[i][k];
			for (int k=2;k<board.length+2;k++) hand[k] = board[k-2];
			scores[i] = rank(hand);
		}
		
		/*
		 * find the max score.
		 */
		int maxscore = 0;
		for (int i=0; i<scores.length;i++) if (scores[i]>maxscore) maxscore = scores[i];
		
		/*
		 * get the number of winner to init the winners array.
		 */
		int numwinners = 0;
		for (int i=0;i<scores.length;i++) if (scores[i] == maxscore) numwinners++;
		int[] winners = new int[numwinners];
		/*
		 * check the scores and declare the winners
		 */
		for (int i=0,j=0;i<scores.length;i++) if (scores[i] == maxscore) winners[j++] = i;
		
		return winners;
		
	}
	/**
	 * check the given board cards and pockets cards and declare the winners. Take into consideration draws. 
	 * @param pockets
	 * @param board
	 * @return the index of the winner pocket cards. 
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static int[] winners(String[][] pockets, String[] board) {
		int[][] pocketArr = new int[pockets.length][2];
		for (int k=0;k<pockets.length;k++) {
			String[] pcards = pockets[k];
			pocketArr[k][0] = toIndex(pcards[0]);
			pocketArr[k][1] = toIndex(pcards[1]);
		}
		int[] boardArr = new int[board.length];
		for (int k=0;k<board.length;k++) {
			String c = board[k];
			boardArr[k] = toIndex(c);
		}
		return winners(pocketArr, boardArr);
	}	
}
