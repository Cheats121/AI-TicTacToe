package ticTacToe;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
/**
 * A policy iteration agent. You should implement the following methods:
 * (1) {@link PolicyIterationAgent#evaluatePolicy}: this is the policy evaluation step from your lectures
 * (2) {@link PolicyIterationAgent#improvePolicy}: this is the policy improvement step from your lectures
 * (3) {@link PolicyIterationAgent#train}: this is a method that should runs/alternate (1) and (2) until convergence. 
 * 
 * NOTE: there are two types of convergence involved in Policy Iteration: Convergence of the Values of the current policy, 
 * and Convergence of the current policy to the optimal policy.
 * The former happens when the values of the current policy no longer improve by much (i.e. the maximum improvement is less than 
 * some small delta). The latter happens when the policy improvement step no longer updates the policy, i.e. the current policy 
 * is already optimal. The algorithm should stop when this happens.
 * 
 * 
 *
 */
public class PolicyIterationAgent extends Agent {

	/**
	 * This map is used to store the values of states according to the current policy (policy evaluation). 
	 */
	HashMap<Game, Double> policyValues=new HashMap<Game, Double>();
	
	/**
	 * This stores the current policy as a map from {@link Game}s to {@link Move}. 
	 */
	HashMap<Game, Move> curPolicy=new HashMap<Game, Move>();
	
	double discount=0.9;
	
	/**
	 * The mdp model used, see {@link TTTMDP}
	 */
	TTTMDP mdp;
	
	/**
	 * loads the policy from file if one exists. Policies should be stored in .pol files directly under the project folder.
	 */
	public PolicyIterationAgent() 
	{
		super();
		this.mdp=new TTTMDP();
		initValues();
		initRandomPolicy();
		train();	
	}
	
	
	/**
	 * Use this constructor to initialise your agent with an existing policy
	 * @param p
	 */
	public PolicyIterationAgent(Policy p) 
	{
		super(p);	
	}

	/**
	 * Use this constructor to initialise a learning agent with default MDP paramters (rewards, transitions, etc) as specified in 
	 * {@link TTTMDP}
	 * @param discountFactor
	 */
	public PolicyIterationAgent(double discountFactor) 
	{	
		this.discount=discountFactor;
		this.mdp=new TTTMDP();
		initValues();
		initRandomPolicy();
		train();
	}
	/**
	 * Use this constructor to set the various parameters of the Tic-Tac-Toe MDP
	 * @param discountFactor
	 * @param winningReward
	 * @param losingReward
	 * @param livingReward
	 * @param drawReward
	 */
	public PolicyIterationAgent(double discountFactor, double winningReward, double losingReward, double livingReward, double drawReward)
	{
		this.discount=discountFactor;
		this.mdp=new TTTMDP(winningReward, losingReward, livingReward, drawReward);
		initValues();
		initRandomPolicy();
		train();
	}
	/**
	 * Initialises the {@link #policyValues} map, and sets the initial value of all states to 0 
	 * (V0 under some policy pi ({@link #curPolicy} from the lectures). Uses {@link Game#inverseHash} and {@link Game#generateAllValidGames(char)} to do this. 
	 * 
	 */
	public void initValues()
	{
		List<Game> allGames=Game.generateAllValidGames('X');//all valid games where it is X's turn, or it's terminal.
		for(Game g: allGames)
			this.policyValues.put(g, 0.0);	
	}
		
	/**
	 *  You should implement this method to initially generate a random policy, i.e. fill the {@link #curPolicy} for every state. Take care that the moves you choose
	 *  for each state ARE VALID. You can use the {@link Game#getPossibleMoves()} method to get a list of valid moves and choose 
	 *  randomly between them. 
	 */
	public void initRandomPolicy() {
	    for (Game state : policyValues.keySet()) {                                                // Iterate through all states in PolicyValues
	        List<Move> possibleMoves = state.getPossibleMoves();                                  // get possibleMoves for the current state
	        Random rand = new Random();                                                           // create random numbers object

	        for (int i = 0; i < possibleMoves.size(); i++) {                                      // Iterate over the possibleMoves for current state
	            int randomIndex = rand.nextInt(possibleMoves.size());                             // Generate a random move from the list of possibleMoves
	            curPolicy.put(state, possibleMoves.get(randomIndex));                             // Set the move to be current state within map
	        }
	    }
	}
	
	/*
	 * This method calculates the transition value based on the formula below (MDP).
	 */
	public double calcTransitionVal(TransitionProb t)
	{
		return t.prob * (t.outcome.localReward + (discount * policyValues.get(t.outcome.sPrime))); // The formula calculates the value of the transition by considering
                                                                                                   // the probability of the transition (t.prob) 
	}
	
	/**
	 * Performs policy evaluation steps until the maximum change in values is less than {@code delta}, in other words
	 * until the values under the currrent policy converge. After running this method, 
	 * the {@link PolicyIterationAgent#policyValues} map should contain the values of each reachable state under the current policy. 
	 * You should use the {@link TTTMDP} {@link PolicyIterationAgent#mdp} provided to do this.
	 *
	 * @param delta
	 */
	protected void evaluatePolicy(double delta)
	{
		double maxQValue = -1000000;				//Variable to store max Q value													 
		double qValue = 0;					        //Variable to store current Q value															
		
		do																		
		{
			for(Game state : curPolicy.keySet())	// Loop through all states in the current policy												
			{
				if(state.isTerminal())				// if terminal set maxQValue to 0												
					maxQValue = 0;																			
				
				qValue = 0;							// set current qvalue to 0												
				Move movePolicy = curPolicy.get(state);     // Move being done by current state and policy 										
				
				for(TransitionProb trans : mdp.generateTransitions(state, movePolicy))	// Iterate through transition for the current and policy
				{
					qValue += calcTransitionVal(trans);									// Update the current value based on transition				
				}
				
				double preQValue = policyValues.get(state);						       // Get the previous Q value of the state				
				policyValues.put(state, qValue);									   // Update the current Q value				
				
				maxQValue = Math.abs(preQValue - qValue);							  // Calculate the change						
			}
		} while(maxQValue > delta);													  // Keep looping until maxQValue is less than Delta			
	}
		
	
	
	/**This method should be run AFTER the {@link PolicyIterationAgent#evaluatePolicy} train method to improve the current policy according to 
	 * {@link PolicyIterationAgent#policyValues}. You will need to do a single step of expectimax from each game (state) key in {@link PolicyIterationAgent#curPolicy} 
	 * to look for a move/action that potentially improves the current policy. 
	 * 
	 * @return true if the policy improved. Returns false if there was no improvement, i.e. the policy already returned the optimal actions.
	 */
	protected boolean improvePolicy()
	{
		boolean changePolicy = false;		// Flag set to default False														
		double maxQValue;                   // Variable to store Max Q value
		double qValue;						// Variable to store the calculated Q value													
		
		for(Game state : policyValues.keySet())	 // Loop through all states in the Policy Values												
		{
			if(state.isTerminal())				// Check if current state is terminal	 												
				maxQValue = 0;					// if it is then set maxQValue to 0															
			else																					
				maxQValue = -1000000;			// else set maxQValue to very low value														
			
			List<Move> posMoves = state.getPossibleMoves();	// get a list of possible moves for current state												
			for(Move move : posMoves)																
			{
				List<TransitionProb> transList = mdp.generateTransitions(state, move);		// Generate a list of transition probabilities		
				
				qValue = 0;	                                                                // Initialize QValue to 0 for current move                      																		
				
				for(TransitionProb trans : transList)										
				{
					qValue += calcTransitionVal(trans);									// Calculate the Q value for the current transition and add it						
				}
				
				if(qValue > maxQValue)																
				{
					maxQValue = qValue;												// Update maxQValue with new value				
					curPolicy.put(state, move);										// Update the current policy with the new best move					
					changePolicy = true;											// Change the default flag to indicate change			
				}
			}
		}
		
		return changePolicy;						                               // Return changePolicy												
	}
	
	/**
	 * The (convergence) delta
	 */
	double delta=0.1;
	
	/**
	 * This method should perform policy evaluation and policy improvement steps until convergence (i.e. until the policy
	 * no longer changes), and so uses your 
	 * {@link PolicyIterationAgent#evaluatePolicy} and {@link PolicyIterationAgent#improvePolicy} methods.
	 */
	public void train() {
		
	    initValues();                                // Initialize values and set up a random policy
	    initRandomPolicy();

	    do {
	        // Evaluate the current policy
	        evaluatePolicy(delta);

	        // Create a copy of the current policy
	        HashMap<Game, Move> prePolicy = new HashMap<>(this.curPolicy);   // Create hashmap of current Policy

	        // Improve the current policy
	        if (!improvePolicy()) {                                          // if policy does not improve
	            break;                                                       // then break
	        }

	        // Check if the policy remains unchanged after improvement
	        if (curPolicy.equals(prePolicy)) {                               // Check if the policy remains unchanged
	            break;                                                      // then break
	        }
	    } while (true);                                                    // Continue looping while true

	    // Create a new policy object with the final policy
	    policy = new Policy();                                            // create new new policy object
	    policy.policy = curPolicy;                                        // final Policy
	}
	
	public static void main(String[] args) throws IllegalMoveException
	{
		/**
		 * Test code to run the Policy Iteration Agent against a Human Agent.
		 */
		PolicyIterationAgent pi=new PolicyIterationAgent();
		
		HumanAgent h=new HumanAgent();
		
		Game g=new Game(pi, h, h);
		
		g.playOut();		
	}
	

}
