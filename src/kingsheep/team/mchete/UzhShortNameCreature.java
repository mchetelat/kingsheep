package kingsheep.team.mchete;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kingsheep.Creature;
import kingsheep.Simulator;
import kingsheep.Type;

/**
 * Created by kama on 04.03.16.
 */
public abstract class UzhShortNameCreature extends Creature {
	private Type map[][];
	private Type objective[];

	public UzhShortNameCreature(Type type, Simulator parent, int playerID, int x, int y) {
		super(type, parent, playerID, x, y);
	}

	public String getNickname() {
		return "BigKingSheepXXL";
	}

	protected Move getAction(Type map[][], char[] objective) {
		this.map = map;
		this.objective = new Type[objective.length];
		for (int i = 0; i < objective.length; ++i) {
			this.objective[i] = Type.getType(objective[i]);
		}

		Square root = new Square(map[y][x], x, y, this.objective, null, null);
		return root.aStarSearchUntilObjectiveIsReached();
	}

	class Square {
		Type type;
		private int x, y;
		private Type objective[];
		private Move howToGetHere;
		private Square gotHereFrom;
		List<Square> openSet = new ArrayList<Square>();
		List<Square> closedSet = new ArrayList<Square>();
		Map<Square, Square> cameFrom = new HashMap<>();
		Map<Square, Integer> gScore = new HashMap<>();
		Map<Square, Integer> fScore = new HashMap<>();
		
		protected Square(Type type, int x, int y, Type objective[], Move howToGetHere, Square gotHereFrom) {
			this.type = type;
			this.x = x;
			this.y = y;
			this.objective = objective;
			this.howToGetHere = howToGetHere;
			this.gotHereFrom = gotHereFrom;
		}

		protected Move aStarSearchUntilObjectiveIsReached() {
			openSet.add(this);
			gScore.put(this, 0);
//			fScore.put(this, getHeuristicCostEstimate(this, ))
			
			while (!openSet.isEmpty()) {
				if (this.isSquareContainingObjective()) {
					return null;
				}
				openSet.remove(this);
				closedSet.add(this);
				
				for (Square neighbour : getAccessibleNeighbourSquares(this, x, y)) {
					if (closedSet.contains(neighbour)) {
						break;
					}
					
					
				}
			}

			return null;
		}

		private void printList(List<Square> list) {
			int counter = 1;
			for (Square entry : list) {
				System.out.println("Entry " + counter + ": " + entry.x + " || " + entry.y);
				counter++;
			}
		}

		private List<Square> getAccessibleNeighbourSquares(Square origin, int xPos, int yPos) {
			List<Square> accessibleNeighbourSquares = new ArrayList<Square>();
			addNeighbourToListIfAccessible(accessibleNeighbourSquares, new Square(map[yPos - 1][xPos],
					origin.getXCoordinate(), origin.getYCoordinate() - 1, objective, Move.UP, this));
			addNeighbourToListIfAccessible(accessibleNeighbourSquares, new Square(map[yPos + 1][xPos],
					origin.getXCoordinate(), origin.getYCoordinate() + 1, objective, Move.DOWN, this));
			addNeighbourToListIfAccessible(accessibleNeighbourSquares, new Square(map[yPos + 1][xPos],
					origin.getXCoordinate(), origin.getYCoordinate() + 1, objective, Move.DOWN, this));
			addNeighbourToListIfAccessible(accessibleNeighbourSquares, new Square(map[yPos + 1][xPos],
					origin.getXCoordinate(), origin.getYCoordinate() + 1, objective, Move.DOWN, this));

			return accessibleNeighbourSquares;
		}

		private void addNeighbourToListIfAccessible(List<Square> accessibleNeighbourSquares, Square neighbour) {
			if (neighbour.isSquareVisitable()) {
				accessibleNeighbourSquares.add(neighbour);
			}
		}

		private boolean isSquareContainingObjective() {
			for (int i = 0; i < objective.length; i++) {
				if (type == objective[i]) {
					return true;
				}
			}
			return false;
		}

		private boolean isSquareVisitable() {
			if (type == Type.FENCE) {
				return false;
			}

			return true;
		}

		protected int getXCoordinate() {
			return x;
		}

		protected int getYCoordinate() {
			return y;
		}
	}
}
