
/***************************************************************************************
  CS540 - Section 2
  Homework Assignment 2: Game Playing

  TakeStones.java
  This is the main class that implements functions for Take Stones playing!
  ---------
  	*Free to modify anything in this file, except the class name 
  	You are required:
  		- To keep the class name as TakeStones for testing
  		- Not to import any external libraries
  		- Not to include any packages 
	*Notice: To use this file, you should implement 4 methods below.

	@author: TA 
	@date: Feb 2017
*****************************************************************************************/

import java.util.ArrayList;

public class TakeStones {

	final int WIN_SCORE = 100; // score of max winning game
	final int LOSE_SCORE = -100;// score of max losing game
	final int INFINITY = 1000; // infinity constant

	/**
	 * Class constructor.
	 */
	public TakeStones() {
	};

	/**
	 * This method is used to generate a list of successors
	 * 
	 * @param state
	 *            This is the current game state
	 * @return ArrayList<Integer> This is the list of state's successors
	 */
	public ArrayList<Integer> generate_successors(GameState state) {
		// the last move
		int lastMove = state.get_last_move();
		// game size
		int size = state.get_size();
		// list of successors
		ArrayList<Integer> successors = new ArrayList<Integer>();
		if (lastMove == -1) {
			for (int i = 1; i < size / 2.0; i++) {
				if (i % 2 == 1)
					successors.add(i);
			}
			return successors;
		}
		for (int i = 1; i <= size; i++) {
			if (state.get_stone(i)) {
				if (lastMove % i == 0 || i % lastMove == 0)
					successors.add(i);
			}
		}
		return successors;
	}

	/**
	 * This method is used to evaluate a game state based on the given heuristic
	 * function
	 * 
	 * @param state
	 *            This is the current game state
	 * @return int This is the static score of given state
	 */
	public int evaluate_state(GameState state) {
		// if stone 1 is still available, score is 0
		if (state.get_stone(1))
			return 0;
		int lastMove = state.get_last_move();
		ArrayList<Integer> successors = generate_successors(state);
		int count = 0;
		if (1 == lastMove) {
			if (successors.size() % 2 == 0)
				return -5;
			else
				return 5;
		} else if (Helper.is_prime(lastMove)) {
//			for (Integer integer : successors)
//				if (integer % lastMove == 0)
//					count++;
			for (int i = 1; i <= state.get_size(); i++)
				if (state.get_stone(i))
					if(i % lastMove == 0)
						count++;
			if (count % 2 == 0)
				return -7;
			else
				return 7;
		} else {
			int prime = Helper.get_largest_prime_factor(lastMove);
//			for (Integer integer : successors)
//				if (integer % prime == 0)
//					count++;
			for (int i = 1; i <= state.get_size(); i++)
				if (state.get_stone(i))
					if(i % prime == 0)
						count++;
			if (count % 2 == 0)
				return -6;
			else
				return 6;
		}
	}

	/**
	 * This method is used to get the best next move from the current state
	 * 
	 * @param state
	 *            This is the current game state
	 * @param depth
	 *            Current depth of search
	 * @param maxPlayer
	 *            True if player is Max Player; Otherwise, false
	 * @return int This is the number indicating chosen stone
	 */
	public int get_next_move(GameState state, int depth, boolean maxPlayer) {
		int move = -1; // the best next move
		int alpha = -INFINITY; // initial value of alpha
		int beta = INFINITY; // initial value of alpha

		// Getting successors of the given state
		ArrayList<Integer> successors = generate_successors(state);
		// Check if depth is 0 or it is terminal state
		if (0 == depth || 0 == successors.size()) {
			state.log();
			Helper.log_alphabeta(alpha, beta);
			return move;
		}
		if (maxPlayer) {
			int temp = -INFINITY;
			for (Integer integer : successors) {
				GameState successor = new GameState(state);
				successor.remove_stone(integer);
				int value = alphabeta(successor, depth - 1, alpha, beta, !maxPlayer);
				if (value > temp) {
					move = integer;
					temp = value;
				} else if (value == temp) {
					move = Math.min(move, integer);
				}
				if (value >= beta)
					return move;
				alpha = Math.max(alpha, value);
			}
		} else {
			int temp = INFINITY;
			for (Integer integer : successors) {
				GameState successor = new GameState(state);
				successor.remove_stone(integer);
				int value = alphabeta(successor, depth - 1, alpha, beta, !maxPlayer);
				if (value < temp) {
					move = integer;
					temp = value;
				} else if (value == temp) {
					move = Math.min(move, integer);
				}
				if (value < alpha)
					return move;
				beta = Math.min(beta, value);
			}
		}
		state.log();
		Helper.log_alphabeta(alpha, beta);
		return move;
	}

	/**
	 * This method is used to implement alpha-beta pruning for both 2 players
	 * 
	 * @param state
	 *            This is the current game state
	 * @param depth
	 *            Current depth of search
	 * @param alpha
	 *            Current Alpha value
	 * @param beta
	 *            Current Beta value
	 * @param maxPlayer
	 *            True if player is Max Player; Otherwise, false
	 * @return int This is the number indicating score of the best next move
	 */
	public int alphabeta(GameState state, int depth, int alpha, int beta, boolean maxPlayer) {
		// score of the best next move
		int v = INFINITY;
		// generate successors of current state
		ArrayList<Integer> successors = generate_successors(state);
		// check if the game reach terminal state (no successor)
		if (successors.size() == 0) {
			// Max's turn to play
			if (maxPlayer) {
				state.log();
				Helper.log_alphabeta(alpha, beta);
				return LOSE_SCORE;
			} else {
				// Min's turn to play
				state.log();
				Helper.log_alphabeta(alpha, beta);
				return WIN_SCORE;
			}
		}
		if (depth == 0) {
			state.log();
			Helper.log_alphabeta(alpha, beta);
			return evaluate_state(state);
		}

		if (maxPlayer) {
			v = -INFINITY;
			for (Integer integer : successors) {
				GameState successor = new GameState(state);
				successor.remove_stone(integer);
				v = Math.max(v, alphabeta(successor, depth - 1, alpha, beta, !maxPlayer));
				if (v >= beta) {
					state.log();
					Helper.log_alphabeta(alpha, beta);
					return v;
				}
					
				alpha = Math.max(alpha, v);
			}
		} else {
			v = INFINITY;
			for (Integer integer : successors) {
				GameState successor = new GameState(state);
				successor.remove_stone(integer);
				v = Math.min(v, alphabeta(successor, depth - 1, alpha, beta, !maxPlayer));
				if (v <= alpha) {
					state.log();
					Helper.log_alphabeta(alpha, beta);
					return v;
				}
				beta = Math.min(beta, v);
			}
		}
		// print state and alpha, beta before return
		state.log();
		Helper.log_alphabeta(alpha, beta);
		return v;
	}

	/**
	 * This is the main method which makes use of addNum method.
	 * 
	 * @param args
	 *            A sequence of integer numbers, including the number of stones,
	 *            the number of taken stones, a list of taken stone and search
	 *            depth
	 * @return Nothing.
	 * @exception IOException
	 *                On input error.
	 * @see IOException
	 */
	public static void main(String[] args) {
		try {
			// read input from command line
			// the number of stones
			int n = Integer.parseInt(args[0]);
			// the number of taken stones
			int nTaken = Integer.parseInt(args[1]);

			// initialize the game state
			GameState state = new GameState(n);
			int stone;
			for (int i = 0; i < nTaken; i++) {
				stone = Integer.parseInt(args[i + 2]);
				state.remove_stone(stone);
			}

			// search depth
			int depth = Integer.parseInt(args[nTaken + 2]);
			// process for depth being 0
			if (0 == depth)
				depth = n + 1;

			// takeStones object
			TakeStones player = new TakeStones();
			// detect current player
			boolean maxPlayer = (0 == (nTaken % 2));

			// get next move
			int move = player.get_next_move(state, depth, maxPlayer);
			// remove the chosen stone out of the board
			state.remove_stone(move);

			// print Solution
			System.out.println("NEXT MOVE");
			state.log();

		} catch (Exception e) {
			System.out.println("Invalid input");
		}
	}
}