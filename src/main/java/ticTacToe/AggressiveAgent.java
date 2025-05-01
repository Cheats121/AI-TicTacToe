package ticTacToe;

/**
 * An aggressive agent: if there is a winning move, this agent always plays it. If not, it plays randomly.
 *
 *
 */
public class AggressiveAgent extends Agent {
	
	
	public AggressiveAgent()
	{
		super(new AggressivePolicy());
	}

}
