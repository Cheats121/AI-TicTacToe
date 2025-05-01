package ticTacToe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * A Q-Learning agent with a Q-Table, i.e. a table of Q-Values. This table is implemented in the {@link QTable} class.
 * 
 *  The methods to implement are: 
 * (1) {@link QLearningAgent#train}
 * (2) {@link QLearningAgent#extractPolicy}
 * 
 * Your agent acts in a {@link TTTEnvironment} which provides the method {@link TTTEnvironment#executeMove} which returns an {@link Outcome} object, in other words
 * an [s,a,r,s']: source state, action taken, reward received, and the target state after the opponent has played their move. You may want/need to edit
 * {@link TTTEnvironment} - but you probably won't need to. 
 *
 */

public class QLearningAgent extends Agent {
	
	/**
	 * The learning rate, between 0 and 1.
	 */
	double alpha=0.1;
	
	/**
	 * The number of episodes to train for
	 */
	int numEpisodes=10000;
	
	/**
	 * The discount factor (gamma)
	 */
	double discount=0.9;
	
	
	/**
	 * The epsilon in the epsilon greedy policy used during training.
	 */
	double epsilon=0.1;
	
	/**
	 * This is the Q-Table. To get an value for an (s,a) pair, i.e. a (game, move) pair.
	 * 
	 */
	
	QTable qTable=new QTable();
	
	
	/**
	 * This is the Reinforcement Learning environment that this agent will interact with when it is training.
	 * By default, the opponent is the random agent which should make your q learning agent learn the same policy 
	 * as your value iteration and policy iteration agents.
	 */
	TTTEnvironment env=new TTTEnvironment();
	
	
	/**
	 * Construct a Q-Learning agent that learns from interactions with {@code opponent}.
	 * @param opponent the opponent agent that this Q-Learning agent will interact with to learn.
	 * @param learningRate This is the rate at which the agent learns. Alpha from your lectures.
	 * @param numEpisodes The number of episodes (games) to train for
	 */
	public QLearningAgent(Agent opponent, double learningRate, int numEpisodes, double discount)
	{
		env=new TTTEnvironment(opponent);
		this.alpha=learningRate;
		this.numEpisodes=numEpisodes;
		this.discount=discount;
		initQTable();
		train();
	}
	
	/**
	 * Initialises all valid q-values -- Q(g,m) -- to 0.
	 *  
	 */
	
	protected void initQTable()
	{
		List<Game> allGames=Game.generateAllValidGames('X');//all valid games where it is X's turn, or it's terminal.
		for(Game g: allGames)
		{
			List<Move> moves=g.getPossibleMoves();
			for(Move m: moves)
			{
				this.qTable.addQValue(g, m, 0.0);
				//System.out.println("initing q value. Game:"+g);
				//System.out.println("Move:"+m);
			}
			
		}
		
	}
	
	/**
	 * Uses default parameters for the opponent (a RandomAgent) and the learning rate (0.2). Use other constructor to set these manually.
	 */
	public QLearningAgent()
	{
		this(new RandomAgent(), 0.1, 100, 0.9);
		
	}
	
	/*
	 * This method gets the maximim q value for a game state. It takes all the possible moves from the state.
	 * If the list is empty, then we make the q value 0, else we compare and keep updating the max q value. 
	 */
	
	
	/**
	 *  Implement this method. It should play {@code this.numEpisodes} episodes of Tic-Tac-Toe with the TTTEnvironment, updating q-values according 
	 *  to the Q-Learning algorithm as required. The agent should play according to an epsilon-greedy policy where with the probability {@code epsilon} the
	 *  agent explores, and with probability {@code 1-epsilon}, it exploits. 
	 *  
	 *  At the end of this method you should always call the {@code extractPolicy()} method to extract the policy from the learned q-values. This is currently
	 *  done for you on the last line of the method.
	 */
	
	public void train()
	{
		int totalEp = 50000;                                        // Variable for number of episodes. Set to 50000
		initQTable();                                               // Create Q table used for learning
		
		for (int ep = 0; ep < totalEp; ep++) {                      // Loop through each episode
			Game currentState = env.game;                           // Get current state of the environment
			
			while (!currentState.isTerminal()) {                            // loop until current state is terminal
				List<Move> possibleMv = currentState.getPossibleMoves();   // Get possible moves from current state
				Move selectedMv = null;                                   // selected move set to null
				
				if(Math.random() < epsilon) {                             // Check is value is less the exploration rate (Epsilon)
					if(!possibleMv.isEmpty()) {                           // Check if possible moves are available
						int rIndex = new Random().nextInt(possibleMv.size());   // generate a random index within range of possible moves
						selectedMv = possibleMv.get(rIndex);                    // retrieve the move 
					}
				}
				
				else {
					double maxQvalue = Double.NEGATIVE_INFINITY;              // Variable to store Max Q value. Set to neg infinity
					List<Move> maxQmoves = new ArrayList<>();                 // List to store moves by the Max Q value
					
					for (Move move : possibleMv) {                           // Loop through each possible move in the list
						double qValue = qTable.getQValue(currentState, move);  // Get Q	value for the current move and state from Q table
						
						if (qValue > maxQvalue) {                              // Check if Q Value is greater than the current Max Q value
							maxQvalue = qValue;                                // if true update the Max Q value
							maxQmoves.clear();                                 // Clear list
							maxQmoves.add(move);                               // add new move
						}
						
						else if (qValue == maxQvalue) {                       // if Q value is equal to the Max Q value then 
							maxQmoves.add(move);                              // add move to the list
						}
					}
					
					int randomIndex = new Random().nextInt(maxQmoves.size());    // Generate a random index within range of possible moves having Max Q value
					selectedMv = maxQmoves.get(randomIndex);                     // Select a random move from list
				}
				
				Outcome outC = null;                                            // Variable to store result of selected move. Set to null
				try {
					outC = env.executeMove(selectedMv);                        // Execute the selected move and get outcome
				}
				catch (IllegalMoveException err) {                            // case handling if illegal move is played
					err.printStackTrace();
				}
				
				Game sourceSt = outC.s;                                     // Get the source of outcome
				Game targetSt = outC.sPrime;                                // Get the target of outcome
				
				List<Move> trgPosMoves = targetSt.getPossibleMoves();      // Get possible moves from target state
				double maxTrgQValue = Double.NEGATIVE_INFINITY;            // Variable to store Max Q value. Set to neg infinity
				
				for (Move targetMv : trgPosMoves) {                     // Loop through possible moves in target state to find Max Q value   
					double targetQValue = qTable.getQValue(targetSt, targetMv);  // Check if Q Value is greater than the current Max Q value
					if (targetQValue > maxTrgQValue) {                           // if target Q value is greater than Max Q value
						maxTrgQValue = targetQValue;                             // if true then update the Max Q value 
					}
				}
				
				if (targetSt.isTerminal()) {                                    // if target state is terminal
					maxTrgQValue = 0.0;                                         // then set Max target value to 0.0
				}
				
				double newQValue = (((1-alpha)*(qTable.getQValue(sourceSt, selectedMv)))+(alpha*(    // Update Q value using Q learning update
						(outC.localReward)+(discount*maxTrgQValue))));
				
				qTable.addQValue(sourceSt, selectedMv, newQValue);                          // Update Q value in the Q table for the selected move and source state
				currentState = outC.sPrime;                                                 // Move the target state based on outcome
			}
			env.reset();                                                                    // reset environment
		}
		
		
		//--------------------------------------------------------
		//you shouldn't need to delete the following lines of code.
		this.policy=extractPolicy();
		if (this.policy==null)
		{
			System.out.println("Unimplemented methods! First implement the train() & extractPolicy methods");
			//System.exit(1);
		}
	}
	
	/** Implement this method. It should use the q-values in the {@code qTable} to extract a policy and return it.
	 *
	 * @return the policy currently inherent in the QTable
	 */
	public Policy extractPolicy() { 
		Policy extractPol = new Policy();                       // Create a new Policy object
		
		for (Game state : qTable.keySet()) {                   // Iterate through each state in Q table      
			HashMap<Move, Double> aValue = qTable.get(state);  // Get the action Value map for current state
			
			Move bestMove = null;                               // Create variable to find best move. Set to null
			double bValue = Double.NEGATIVE_INFINITY;           // create Variable best Value set to neg infinity
			
			for(Move mv : aValue.keySet()) {                   // Loop through each move in action map
				double val = aValue.get(mv);                   // get value of current move
				if (val > bValue) {                            // if value is greater than current
					bValue = val;                              // then update best value
					bestMove = mv;                             // And best move
				}
			}
			if (bestMove != null) {                            // if best Move is found and not null   
				extractPol.policy.put(state, bestMove);        // then add this state and its best move to the extracted policy
			}
		}
		return extractPol;	                                   // return extracted policy
	}
	
	public static void main(String a[]) throws IllegalMoveException
	{
		//Test method to play your agent against a human agent (yourself).
		QLearningAgent agent=new QLearningAgent();
		
		HumanAgent d=new HumanAgent();
		
		Game g=new Game(agent, d, d);
		g.playOut();	
	}	
}
