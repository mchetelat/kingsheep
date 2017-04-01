package kingsheep.team.mchete;

import kingsheep.Simulator;
import kingsheep.Type;

public class Sheep extends McheteCreature {
	
	Move lastMove;
	
	private boolean noMoreFoodAvailable = false;

	public Sheep(Type type, Simulator parent, int playerID, int x, int y) {
		super(type, parent, playerID, x, y);
	}

	private void fleeFromHungryWolf(Type map[][]) {
		move = lastMove;
	}

	protected void think(Type map[][]) {
		char[] objectives = { 'g', 'r' };

		if (alive && !noMoreFoodAvailable) {
			move = getAction(map, objectives);

			if (move == Move.WAIT) {
				noMoreFoodAvailable = true;
				fleeFromHungryWolf(map);
			}
			// Check if hungry wolf is on square
		} else {
			fleeFromHungryWolf(map);
		}
		
		lastMove = move;
	}
}