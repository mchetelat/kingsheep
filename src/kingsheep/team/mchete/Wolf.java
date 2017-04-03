package kingsheep.team.mchete;

import kingsheep.*;

public class Wolf extends McheteCreature {

	public Wolf(Type type, Simulator parent, int playerID, int x, int y) {
		super(type, parent, playerID, x, y);
	}

	protected void think(Type map[][]) {
		this.map = map;
		char[] objectives = { '3' };

		if (alive) {
			move = getAction(objectives);
		}
	}
}