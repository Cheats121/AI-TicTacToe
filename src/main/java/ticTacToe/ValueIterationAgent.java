package ticTacToe;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A Value Iteration Agent, only very partially implemented. The methods to implement are: 
 * (1) {@link ValueIterationAgent#iterate}
 * (2) {@link ValueIterationAgent#extractPolicy}
 * 
 * You may also want/need to edit {@link ValueIterationAgent#train} - feel free to do this, but you probably won't need to.
 *
 *
 */
public class ValueIterationAgent extends Agent 
{

	/**
	 * This map is used to store the values of states
	 */
	Map<Game, Double> valueFunction=new HashMap<Game, Double>();
	
	/**
	 * the discount factor
	 */
	double discount=0.9;
	
	/**
	 * the MDP model
	 */
	TTTMDP mdp=new TTTMDP();
	
	/**
	 * the number of iterations to perform - feel free to change this/try out different numbers of iterations
	 */
	int k=10;
	
	
	/**
	 * This constructor trains the agent offline first and sets its policy
	 */
	public ValueIterationAgent()
	{
		super();
		mdp=new TTTMDP();
		this.discount=0.9;
		initValues();
		train();
	}
	
	
	/**
	 * Use this constructor to initialise your agent with an existing policy
	 * @param p
	 */
	public ValueIterationAgent(Policy p) 
	{
		super(p);	
	}

	public ValueIterationAgent(double discountFactor) 
	{	
		this.discount=discountFactor;
		mdp=new TTTMDP();
		initValues();
		train();
	}
	
	/**
	 * Initializes the {@link ValueIterationAgent#valueFunction} map, and sets the initial value of all states to 0 
	 * (V0 from the lectures). Uses {@link Game#inverseHash} and {@link Game#generateAllValidGames(char)} to do this. 
	 * 
	 */
	public void initValues()
	{		
		List<Game> allGames=Game.generateAllValidGames('X');//all valid games where it is X's turn, or it's terminal.
		for(Game g: allGames)
			this.valueFunction.put(g, 0.0);		
	}
	
	
	
	public ValueIterationAgent(double discountFactor, double winReward, double loseReward, double livingReward, double drawReward)
	{
		this.discount=discountFactor;
		mdp=new TTTMDP(winReward, loseReward, livingReward, drawReward);
	}
	
	/*
	 * This method calculates the transition value based on the formula below (MDP).
	 */
	public double calcTransitionVal(TransitionProb t)
	{
		return t.prob * (t.outcome.localReward + (discount * valueFunction.get(t.outcome.sPrime)));   // The formula calculates the value of the transition by considering
		                                                                                              // the probability of the transition (t.prob) 
	}
	
	
	/*
	 * Performs {@link #k} value iteration steps. After running this method, the {@link ValueIterationAgent#valueFunction} map should contain
	 * the (current) values of each reachable state. You should use the {@link TTTMDP} provided to do this.
	 */
	public void iterate() {
	    double qValue;                                                                             // Variable to store the calculated Q value
	    double maxQ;                                                                              // Variable to store the maximum Q value

	    for (int iteration = 0; iteration < k; iteration++) {                                     // Loop through k number of transitions
	        for (Game state : valueFunction.keySet()) {                                           // Loop through all states in the valueFunction set
	            List<Move> possibleMoves = state.getPossibleMoves();                              // Initializing a list with all possible moves for the current state

	            maxQ = state.isTerminal() ? 0 : -1000000;                                         // In the terminal state, set maxQ value to 0, else set it to a very low number

	            for (Move move : possibleMoves) {                                                 // Loops through all possibleMoves for the current state
	                List<TransitionProb> transitions = mdp.generateTransitions(state, move);     

	                qValue = calculateTotalTransitionValue(transitions);                           // Calculates the new qValue for current state

	                if (qValue > maxQ)                                                             // If qValue is greater than the maximum q value set that to new qValue
	                    maxQ = qValue;                                                            
	            }

	            valueFunction.replace(state, maxQ);                                                //Update qValue
	        }
	    }
	}

	private double calculateTotalTransitionValue(List<TransitionProb> transitions) {                //Method to accumulate the value at each transition
	    double totalTransValue = 0;

	    for (TransitionProb transition : transitions) {                                             // Loop through all possible transitions
	    	totalTransValue += calcTransitionVal(transition);                                       // Calculate the new qValue
	    }

	    return totalTransValue;                                                                     //return totalTransValue
	}
	
	/**This method should be run AFTER the train method to extract a policy according to {@link ValueIterationAgent#valueFunction}
	 * You will need to do a single step of expectimax from each game (state) key in {@link ValueIterationAgent#valueFunction} 
	 * to extract a policy.
	 * 
	 * @return the policy according to {@link ValueIterationAgent#valueFunction}
	 */
	public Policy extractPolicy() {
	    double maxQ;                                                                                // Variable to store the calculated Q value
	    double qVal;                                                                               // Variable to store the maximum Q value
	    Policy policy = new Policy();                                                             // Create a new policy

	    for (Game state : valueFunction.keySet()) {                                              // Loop through all states in the valueFunction set
	        List<Move> possibleMoves = state.getPossibleMoves();                                // Initializing a list with all possible moves for the current state

	        maxQ = state.isTerminal() ? 0 : -1000000;                                           // In the terminal state, set maxQ value to 0, else set it to a very low number

	        for (Move move : possibleMoves) {                                                 // Loops through all possibleMoves for the current states
	            List<TransitionProb> transitions = mdp.generateTransitions(state, move);     // Initialize a list of transitions
	            qVal = calculateTotalTransitionValue(transitions);                           // Calculates the new qValue for current state

	            if (qVal > maxQ) {                                                          // If the q value is greater than the max q value
	                maxQ = qVal;                                                            // Set the new q value as the max q value
	                policy.policy.put(state, move);                                         // Set the policy for the game
	            }
	        }
	    }
	    return policy;                                                                      // Return policy
	}
	/**
	 * This method solves the mdp using your implementation of {@link ValueIterationAgent#extractPolicy} and
	 * {@link ValueIterationAgent#iterate}. 
	 */
	public void train()
	{
		/**
		 * First run value iteration
		 */
		this.iterate();
		/**
		 * now extract policy from the values in {@link ValueIterationAgent#valueFunction} and set the agent's policy 
		 *  
		 */
		
		super.policy=extractPolicy();
		
		if (this.policy==null)
		{
			System.out.println("Unimplemented methods! First implement the iterate() & extractPolicy() methods");
			//System.exit(1);
		}
	}

	public static void main(String a[]) throws IllegalMoveException
	{
		//Test method to play the agent against a human agent.
		ValueIterationAgent agent=new ValueIterationAgent();
		HumanAgent d=new HumanAgent();
		
		Game g=new Game(agent, d, d);
		g.playOut();		
	}
}
