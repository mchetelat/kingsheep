package kingsheep.team.mchete;

import kingsheep.Simulator;
import kingsheep.Type;

public class Sheep extends UzhShortNameCreature {
	private boolean noMoreFoodAvailable = false;

	public Sheep(Type type, Simulator parent, int playerID, int x, int y) {
		super(type, parent, playerID, x, y);
	}

	protected void think(Type map[][]) {

		// if (alive && !noMoreFoodAvailable) {
		// char[] objectives = new char[2];
		// objectives[0] = 'r';
		// objectives[1] = 'g';

		 getAction(map);
		// if (move == null) {
		// move = Move.WAIT;
		// }
		//
		// if (move == Move.WAIT) {
		// noMoreFoodAvailable = true;
		// // fleeFromBadWolf(map);
		// }

		move = Move.WAIT;
	}
}