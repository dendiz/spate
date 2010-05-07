package core;

import java.io.FileInputStream;
import java.lang.reflect.Constructor;
import java.util.Properties;

import spadeseval.evaluator.SpadesEval;
import util.Log;
import bots.IBot;

public class Botpit {
	//Conf stuff
	private static float DEFAULTSTACK = 5000;
	private static float BIGBLINDSIZE = 2;
	private static int MAXRAISES = 4;
	private static float SMALLBLINDSIZE = 1;
	public static boolean LOG = false;
	private static boolean graph = false;
	public int current_hand = -1;
	static int NUMSIMROUNDS = 100000;
	static int NUMPLAYERS = 10;
	static String trace_log_name = "trace.log";
	static boolean TRACE = false;
	///////////////////////////////////////////////////////
	public static int PREFLOP = 0;
	static int FLOP = 1;
	static int TURN = 2;
	public static int RIVER = 3;
	static int SHOWDOWN = 4;
	public static long game_start_time;
	public float pot = 0;
	public int raises = 0;
	public int game_stage;
	int button = 0, current_player;
	public IBot[] players;
	Action[] actions;
	float[] stacks;
	float[] bets;
	public String[] board = new String[5];
	Deck deck;
	Properties config = new Properties();
	GameTracer tracer = new GameTracer(100000, trace_log_name);
	StringBuffer sb = new StringBuffer();
	public Botpit() {
		deck = new Deck();
	}
	private void read_config() {
		try {
			config.load(new FileInputStream("botpit.properties"));
		} catch (Exception e) {
			System.err.println("Couldn't load properties file.\n" + e.getMessage());
			System.exit(1);
		}
		DEFAULTSTACK = Float.valueOf(config.getProperty("stack"));
		BIGBLINDSIZE = Float.valueOf(config.getProperty("bbsize"));
		MAXRAISES = Integer.valueOf(config.getProperty("maxraises"));
		SMALLBLINDSIZE = Float.valueOf(config.getProperty("sbsize"));
		LOG = Boolean.valueOf(config.getProperty("logging"));
		NUMSIMROUNDS = Integer.valueOf(config.getProperty("numrounds"));
		NUMPLAYERS = Integer.valueOf(config.getProperty("numplayers"));
		TRACE = Boolean.valueOf(config.getProperty("tracing"));
		System.out.printf("Sim rounds: %d , num players: %d, trace: %s\n",NUMSIMROUNDS, NUMPLAYERS, TRACE ? "on":"off");
	}
	
	public void run() {
		read_config();
		load_players();
		if (graph) new Graph(this).game_started();
		for (current_hand=0;current_hand<NUMSIMROUNDS;current_hand++) {
			if (current_hand % ((int) (NUMSIMROUNDS / 10)) == 0) System.out.print(".");
			new_hand(current_hand);
			if (pre_flop() || flop() || turn() || river() || showdown()) {
				if (game_stage != SHOWDOWN) steal_pot();
			}
			if (TRACE) tracer.append("[endhand] id: " + current_hand);
		}
		tracer.finish();
		System.out.println("\nStacks:");
		for (int i = 0;i<players.length;i++) {
			System.out.println(players[i].get_name() + " : " + stacks[i]);
		}
		
	}
	
	
	private void steal_pot() {
		for (int i=0;i<actions.length;i++) {
			if (actions[i].type != Action.FOLD) {
				stacks[i] += pot;
				if (LOG) Log.info(players[i].get_name() + " stole the pot");
				if (TRACE) tracer.append("[winner] seat: "+i+" amount: %f"+ pot);
				if (TRACE) {
					for (int j=0;j<players.length;j++) {
						tracer.append("[endstack] seat: "+j+" stack: "+stacks[j]);
					}			
				}
				return;
			}
		}
	}

	private boolean pre_flop() {
		if (LOG) Log.info(" * Preflop");
		raises = 0;
		if (TRACE) tracer.append("[stage] preflop");
		game_stage = PREFLOP;
		actions[get_sb_seat()] = Action.smallblind(SMALLBLINDSIZE);
		if (TRACE) tracer.append("[action] seat: "+get_sb_seat()+"type: post_smallblind");
		
		actions[get_bb_seat()] = Action.bigblind(BIGBLINDSIZE);
		if (TRACE) tracer.append("[action] seat: "+get_bb_seat()+" type: post_bigblind");

		stacks[get_sb_seat()] -= SMALLBLINDSIZE;
		if (TRACE) tracer.append("[stack] seat: "+get_sb_seat()+"amount: "+stacks[get_sb_seat()]);
		
		stacks[get_bb_seat()] -= BIGBLINDSIZE;
		if (TRACE) tracer.append("[stack] seat: "+get_bb_seat()+"amount: "+stacks[get_bb_seat()]);

		
		bets[get_sb_seat()] += SMALLBLINDSIZE;
		if (TRACE) tracer.append("[bet] seat: "+get_sb_seat()+" amount: "+bets[get_sb_seat()]);
		
		bets[get_bb_seat()] += BIGBLINDSIZE;
		if (TRACE) tracer.append("[bet] seat: "+get_bb_seat()+" amount: "+bets[get_bb_seat()]);
		
		pot += SMALLBLINDSIZE + BIGBLINDSIZE;
		if (TRACE) tracer.append("[pot] amount: "+pot);
		
		for (IBot p : players) {
			String c1 = deck.deal();
			String c2 = deck.deal();
			if (LOG) Log.debug(p.get_name()+ " hole cards:" + c1 + " " + c2);
			p.hole_cards(c1,c2);
			if (TRACE) tracer.append(String.format("[holecard] seat: "+p.get_seat()+" c1: "+c1+" c2: "+ c2));
		}
		
		playout_hand();
		return is_noshowdown();
	}

	private void log_board() {
		StringBuffer sb = new StringBuffer();
		for (int i=0;i<board.length;i++) { if (board[i]!=null)sb.append(board[i]+ " ");}
		if (LOG) Log.debug("Board cards: "+sb.toString());		
	}
	private boolean flop() {
		if (LOG) Log.debug(" * Flop");
		if (TRACE) tracer.append("[stage] flop");
		game_stage = FLOP;
		raises = 0;
		for (int i=0;i<3;i++) board[i] = deck.deal();
		sb.append(board[0]);
		sb.append(" ");
		sb.append(board[1]);
		sb.append(" ");
		sb.append(board[2]);
		if (TRACE) tracer.append("[board] "+sb.toString()+" __ __");
		log_board();
		playout_hand();
		return is_noshowdown();
	}
	private boolean river() {
		if (LOG) Log.debug(" * River");
		if (TRACE) tracer.append("[stage] river");
		game_stage = RIVER;
		raises = 0;
		board[4] = deck.deal();
		sb.append(" ");
		sb.append(board[4]);
		if (TRACE) tracer.append("[board] "+sb.toString());
		sb.setLength(0);
		log_board();
		playout_hand();
		return is_noshowdown();
	}
	
	private boolean turn() {
		if (LOG) Log.debug(" * Turn");
		if (TRACE) tracer.append("[stage] turn");
		game_stage = TURN;
		raises = 0;
		board[3] = deck.deal();
		sb.append(" ");
		sb.append(board[3]);
		if (TRACE) tracer.append("[board] "+sb.toString()+" __");
		log_board();
		playout_hand();
		return is_noshowdown();
	}
	
	private boolean showdown() {
		if (LOG) Log.debug(" * showdown");
		game_stage = SHOWDOWN;
		if (TRACE) tracer.append("[stage] showdown");
		String[][] pockets = new String[players.length][]; 
		for (int i=0;i<players.length;i++) {
			pockets[i] = players[i].get_cards();
		}
		int[] winners = SpadesEval.winners(pockets, board);
		for (int i:winners) {
			stacks[i] += (pot/winners.length);
			if (LOG) Log.info(players[i].get_name() + " won pot of " + (pot/winners.length));
			if (TRACE) tracer.append("[winner] seat: "+i+" amount: "+(pot/winners.length));
		}
		if (TRACE) {
			for (int i=0;i<players.length;i++) {
				tracer.append(String.format("[endstack] seat: "+i+" stack: "+stacks[i]));
			}			
		}
		
		return is_noshowdown();
	}

	private boolean is_noshowdown() {
		int cnt= 0;
		for (int i=0;i<actions.length;i++) {
			if (actions[i].type == Action.FOLD) cnt++;
		}
		return cnt == players.length-1;
	}

	private void playout_hand() {
		current_player = get_first_player();
		if (game_stage!=PREFLOP) for (int i=0;i<bets.length;i++) bets[i] = 0;
		set_waiting();
		while(!is_betting_over()) {
			if (actions[current_player].type == Action.FOLD) {
				if (LOG) Log.info(players[current_player].get_name() + " had folded, skipping");
				if (TRACE) tracer.append("[action] seat: "+current_player+" type: alreadyfold");
				inc_player();
				continue;
			}
			Action ac = players[current_player].get_action();
			interprete_action(ac);
			if (LOG) Log.debug(players[current_player].get_name() + " -> " + Action.get_string(ac.type) + " " + get_log_bets());
			actions[current_player] = ac;
			for (IBot p : players) p.player_action(ac);
			inc_player();
		}
	}

	private String get_log_bets() {
		StringBuffer sb = new StringBuffer();
		sb.append("Bets: ");
		for (int i = 0;i<bets.length;i++) {
			sb.append(players[i].get_name());
			sb.append(": ");
			sb.append(bets[i]);
			sb.append(" ");
		}
		sb.append(" Pot:"); 
		sb.append(pot);
		return  sb.toString();
	}

	private final void inc_player() {
		current_player = (current_player + 1) % NUMPLAYERS;
	}


	private void interprete_action(Action ac) {
		if (ac.type == Action.RAISE) {
			raises++;
			float amount = ac.tocall + ac.amount; 
			pot += amount;
			bets[current_player] += amount;
			stacks[current_player] -= amount;
		}
		if (ac.type == Action.BET) {
			pot += ac.amount;
			bets[current_player] += ac.amount;
			stacks[current_player] -= ac.amount;
		}
		
		if (ac.type == Action.CALL) {
			pot += ac.tocall;
			bets[current_player] += ac.tocall;
			stacks[current_player] -= ac.tocall;
		}
		if (TRACE) {
			tracer.append("[action] seat: "+current_player+" type: "+ Action.get_string(ac.type));
			tracer.append("[stack] seat: "+ current_player+" amount: "+ stacks[current_player]);
			tracer.append("[bet] seat: "+ current_player+" amount: "+ bets[current_player]);
			tracer.append("[pot] amount: "+ pot);
		}
	}

	private boolean is_betting_over() {
		if (is_noshowdown()) return true;
		
		int numchecksfolds = 0;
		for(int i=0;i<actions.length;i++) {
			if (actions[i].type == Action.CHECK || actions[i].type == Action.FOLD)
				numchecksfolds++;
		}
		if (numchecksfolds == players.length) return true;
		
		int numwaiting = 0;
		for(int i=0;i<actions.length;i++) {
			if (actions[i].type == Action.WAITING || actions[i].type == Action.BIGBLIND ||
					actions[i].type == Action.SMALLBLIND) {
				numwaiting++;
			}
		}
		if (numwaiting > 0) return false;
		
		int numnotfolded = 0;
		for(int i=0;i<actions.length;i++) {
			if (actions[i].type != Action.FOLD) numnotfolded++;
		}
		int[] notfolded = new int[numnotfolded];
		for(int i=0,j=0;i<actions.length;i++) {
			if (actions[i].type != Action.FOLD) {
				notfolded[j++] = i;
			}
		}
		
		float[] fbets = new float[numnotfolded];
		for(int i=0,j=0;i<notfolded.length;i++) {
			fbets[j++] = bets[notfolded[i]];
		}
		
		float maxbet = 0;
		for (int i=0;i<fbets.length;i++) if (fbets[i] > maxbet) maxbet = fbets[i];
		
		int numover = 0;
		for (int i=0;i<fbets.length;i++) if (fbets[i] != maxbet) return false;
		if (numover != 0) return false;
		return true;
	}

	private void set_waiting() {
		for (int i=0;i<actions.length;i++) {
			if (actions[i].type != Action.FOLD) actions[i] = Action.waiting();
		}
	}

	private int get_first_player() {
		int delta = game_stage == PREFLOP ? 3 : 1;
		return (button + delta) % NUMPLAYERS;
	}

	private int get_sb_seat() {
		return (button + 1) % players.length;
	}

	private int get_bb_seat() {
		return (button + 2) % players.length;
	}

	private void new_hand(int i) {
		if (LOG) Log.info(" *** new Hand "+ i + " ****");
		if (TRACE) tracer.append("[hand] id: "+ i);
		if (TRACE) tracer.append("[players] total: "+ NUMPLAYERS);
		pot = 0;
		for (int j=0;j<board.length;j++) board[j] = null;
		raises = 0;
		inc_button();
		deck.reset();
		deck.shuffle();
		for (IBot b : players) {
			actions[b.get_seat()] = Action.waiting();
			bets[b.get_seat()] = 0;
			b.new_hand();
		}
		if (LOG) Log.info("dealer is bot" + players[button].get_name());
	}

	private void inc_button() {
		button = (button + 1) % NUMPLAYERS;
	}

	private void load_players() {
		players = new IBot[NUMPLAYERS];
		stacks = new float[NUMPLAYERS];
		bets = new float[NUMPLAYERS];
		actions = new Action[NUMPLAYERS];
		for (int i=0;i<NUMPLAYERS;i++) {
			try {
				Class<?> botclass = Class.forName(config.getProperty("players."+i));
				Constructor<?> c = botclass.getConstructor(new Class[]{Botpit.class, Integer.TYPE, String.class});
				Object o = c.newInstance(new Object[]{this, new Integer(i), config.getProperty("players."+i)});
				if (!(o instanceof IBot)) {
					System.err.println("Bots class must implement the interface IBot. Offending player number:"+i);
					System.exit(1);
				}
				players[i] = (IBot) o;
				//new CallBot(this, i, "Callbot"+i);
				players[i].game_start();
				stacks[i] = DEFAULTSTACK;
				
			} catch (Exception e) {
				System.err.println("Couldn't load bot class "+config.getProperty("player."+i) + " for player " + i +
						"Exiting. Reason:" + e.getMessage());
				e.printStackTrace();
				System.exit(1);
			}
		}
		
	}

	public float get_to_call(int seat) {
		float maxbet = 0;
		for (int i=0;i<bets.length;i++) {
			if (bets[i] > maxbet) maxbet = bets[i];
		}
		return maxbet - bets[seat];
	}
	
	public float get_bb_size() {
		return BIGBLINDSIZE;
	}
	
	public float get_min_raise(int seat) {
		if (game_stage <= FLOP) return raises < MAXRAISES ? BIGBLINDSIZE : 0;
		return raises < MAXRAISES ? 2 * BIGBLINDSIZE : 0;
		
	}
	
	public static void main(String[] args) {
		System.out.println("Botpit v2j");
		System.out.println("Command args:");
		for (String s:args) {
			System.out.println("args" + s);
		}
		if (args[0].equals("-g")) graph = true;
		game_start_time = System.currentTimeMillis();
		Botpit bp = new Botpit();
		bp.run();
		System.out.println("Duration (ms):" + (System.currentTimeMillis() - game_start_time));
	}

	public int get_num_active() {
		int num = 0;
		for (int i=0;i<actions.length;i++) {
			if (actions[i].type != Action.FOLD) num++;
		}
		return num;
	}
}
