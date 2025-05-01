package ticTacToe;


/**
 * A random agent, i.e. ones with a {@link RandomPolicy} that picks a move randomly from all available moves with equal probability.  
 *
 *
 */
public class RandomAgent extends Agent {

	

	public RandomAgent() {
		super();
		policy=new RandomPolicy();
		
	}
	
	

}
