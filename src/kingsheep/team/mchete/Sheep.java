package kingsheep.team.mchete;

import kingsheep.Simulator;
import kingsheep.Type;

public class Sheep extends McheteCreature {

	public Sheep(Type type, Simulator parent, int playerID, int x, int y) {
		super(type, parent, playerID, x, y);
	}

	private void fleeFromWolf() {
		char[] objectives = { '.' };
		move = getAction(objectives, true);
	}

	protected void think(Type map[][]) {
		this.map = map;
		char[] objectives = { 'g', 'r' };

		if (alive && !noMoreFoodAvailable) {
			move = getAction(objectives);
			System.out.println(move);

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