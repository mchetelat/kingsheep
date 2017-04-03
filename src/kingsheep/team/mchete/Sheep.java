package kingsheep.team.mchete;

import kingsheep.Simulator;
import kingsheep.Type;

public class Sheep extends McheteCreature {

	private char[] objectives = { 'r', 'g' };

	public Sheep(Type type, Simulator parent, int playerID, int x, int y) {
		super(type, parent, playerID, x, y);
	}

	private void fleeFromWolf() {
		move = getAction(objectives, true);
	}

	protected void think(Type map[][]) {
		this.map = map;

		if (alive && !noMoreFoodAvailable) {
			move = getAction(objectives, false);

			if (move == Move.WAIT) {
				noMoreFoodAvailable = true;
				fleeFromWolf();
			}

			if (!getSquareFromMove(move).isSquareSafe()) {
				fleeFromWolf();
			}
		} else {
			fleeFromWolf();
		}
	}
}