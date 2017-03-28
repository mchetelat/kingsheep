package kingsheep.team.mchete;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import kingsheep.Creature;
import kingsheep.Simulator;
import kingsheep.Type;

/**
 * Created by kama on 04.03.16.
 */
public abstract class UzhShortNameCreature extends Creature {
	private Type map[][];
	private List<Square> objectives;

	public UzhShortNameCreature(Type type, Simulator parent, int playerID, int x, int y) {
		super(type, parent, playerID, x, y);
	}

	public String getNickname() {
		return "BigKingSheepXXL";
	}

	// i = y, j = x
	protected void getAction(Type map[][]) {
		this.map = map;
		objectives = new ArrayList<>();

		for (int i = 0; i < this.map.length - 1; i++) {
			for (int j = 0; j < this.map[0].length - 1; j++) {
				if (this.map[i][j].equals(Type.GRASS)) {
					objectives.add(new Square(Type.GRASS, j, i));
				}
				if (this.map[i][j].equals(Type.RHUBARB)) {
					objectives.add(new Square(Type.RHUBARB, j, i));
				}
			}
		}
		Square root = new Square(map[y][x], x, y);
		Square testGoal = new Square(map[y][x], 4, 2);
		Move move = root.aStarSearch(testGoal);
	}

	class Square {
		Type type;
		private int x, y;
		List<Square> openSet = new ArrayList<Square>();
		List<Square> closedSet = new ArrayList<Square>();
		Map<Square, Square> cameFrom = new HashMap<>();
		HashMap<Square, Integer> gScore = new HashMap<Square, Integer>();
		TreeMap<Integer, Square> fScore = new TreeMap<Integer, Square>();

		protected Square(Type type, int x, int y) {
			this.type = type;
			this.x = x;
			this.y = y;
			this.gScore.put(this, 100000);
		}

		protected Move aStarSearch(Square goal) {
			openSet.add(this);
			gScore.put(this, 0);
			fScore.put(getHeuristicCostEstimate(goal), this);

			while (!openSet.isEmpty()) {
				Square current = fScore.firstEntry().getValue();
				System.out.println(current.x + " || " + current.y + " -> fScore: " + fScore.firstEntry().getKey());

				if (current != null && current.isSquareContainingObjective()) {
					System.out.println("GOAL!!!");
					return null;
				}

				openSet.remove(current);
				closedSet.add(current);

				for (Square neighbour : getAccessibleNeighbourSquares(current, x, y)) {
					if (closedSet.contains(neighbour)) {
						break;
					}

					int tentative_gScore = gScore.get(current) + 1;

					if (!openSet.contains(neighbour)) {
						openSet.add(neighbour);
					} else if (tentative_gScore >= gScore.get(neighbour)) {
						break;
					}
					// cameFrom[neighbor] := current
					gScore.put(neighbour, tentative_gScore);
					fScore.put(gScore.get(neighbour) + getHeuristicCostEstimate(neighbour, goal), neighbour);
				}

				for (Square square : openSet) {
					System.out.println("openSet: ");
					System.out.println(square.x + " || " + square.y);
				}

				for (Square entry : fScore.values()) {
					System.out.println("fScore: ");
					System.out.println(entry.x + " || " + entry.y);
				}
			}
			return null;
		}

		private int getHeuristicCostEstimate(Square goal) {
			return getHeuristicCostEstimate(this, goal);
		}

		private int getHeuristicCostEstimate(Square start, Square goal) {
			return Math.abs(goal.x - start.x) + Math.abs(goal.y - start.y);
		}

		private List<Square> getAccessibleNeighbourSquares(Square origin, int xPos, int yPos) {
			List<Square> accessibleNeighbourSquares = new ArrayList<Square>();
			addNeighbourToListIfAccessible(accessibleNeighbourSquares,
					new Square(map[yPos - 1][xPos], origin.getXCoordinate(), origin.getYCoordinate() - 1));
			addNeighbourToListIfAccessible(accessibleNeighbourSquares,
					new Square(map[yPos + 1][xPos], origin.getXCoordinate(), origin.getYCoordinate() + 1));
			addNeighbourToListIfAccessible(accessibleNeighbourSquares,
					new Square(map[yPos][xPos + 1], origin.getXCoordinate() + 1, origin.getYCoordinate()));
			addNeighbourToListIfAccessible(accessibleNeighbourSquares,
					new Square(map[yPos][xPos - 1], origin.getXCoordinate() - 1, origin.getYCoordinate() + 1));

			return accessibleNeighbourSquares;
		}

		private void addNeighbourToListIfAccessible(List<Square> accessibleNeighbourSquares, Square neighbour) {
			if (neighbour.isSquareVisitable()) {
				accessibleNeighbourSquares.add(neighbour);
			}
		}

		private boolean isSquareContainingObjective() {
			if (x == 4 && y == 2)
				;
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
