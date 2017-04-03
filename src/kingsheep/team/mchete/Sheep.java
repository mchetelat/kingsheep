package kingsheep.team.mchete;

import kingsheep.Simulator;
import kingsheep.Type;

public class Sheep extends McheteCreature {

	private boolean noMoreFoodAvailable = false;

	public Sheep(Type type, Simulator parent, int playerID, int x, int y) {
		super(type, parent, playerID, x, y);
	}

	private void fleeFromHungryWolf(Type map[][]) {
		char[] objectives = { '.' };
		move = getAction(map, objectives, true);

		while (!isSquareSafe(map, move)) {
			move = getRandomMove();
		}
	}

	protected void think(Type map[][]) {
		char[] objectives = { 'g', 'r' };

		if (alive && !noMoreFoodAvailable) {
			move = getAction(map, objectives);

			if (!isSquareSafe(map, move)) {
				fleeFromHungryWolf(map);
			}

			if (move == Move.WAIT) {
				noMoreFoodAvailable = true;
				fleeFromHungryWolf(map);
			}
		} else {
			fleeFromHungryWolf(map);
		}

		if (move == null) {
			move = Move.WAIT;
		}
	}
}