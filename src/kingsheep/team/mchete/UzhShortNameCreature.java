package kingsheep.team.mchete;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import kingsheep.Creature;
import kingsheep.Simulator;
import kingsheep.Type;

/**
 * Created by kama on 04.03.16.
 */
public abstract class UzhShortNameCreature extends Creature {
	private Type map[][];
	private List<Square> objectives;
	private Square testGoal;

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
		testGoal = new Square(map[y][x], 9, 7);
		Move move = root.aStarSearch();
	}

	class Square {
		Type type;
		private int x, y;
		Set<Square> openSet = new HashSet<>();
		Set<Square> closedSet = new HashSet<>();
		Map<Square, Square> cameFrom = new HashMap<>();
		Map<Square, Integer> gScore = new HashMap<>();
		Map<Square, Integer> fScore = new LinkedHashMap<>();

		protected Square(Type type, int x, int y) {
			this.type = type;
			this.x = x;
			this.y = y;
		}

		protected Move aStarSearch() {
			openSet.add(this);
			gScore.put(this, 0);
			fScore.put(this, getHeuristicCostEstimate(this, testGoal));

			while (!openSet.isEmpty()) {

				List<Square> sortedSquaresfScore = fScore.entrySet().stream()
						.sorted(Map.Entry.<Square, Integer>comparingByValue()).map(Map.Entry::getKey)
						.collect(Collectors.toList());

				Square current = null;

				for (Square entry : sortedSquaresfScore) {
					System.out.print(entry + " fScore: " + fScore.get(entry));
					if (setContainsSquare(openSet, entry)) {
						System.out.println(" -> taken!");
						current = entry;
						break;
					}
					System.out.println("");
				}

				if (current != null && current.isSquareContainingObjective()) {
					System.out.println("GOAL!!!");
					return null;
				}

				openSet.remove(current);
				closedSet.add(current);

				for (Square neighbour : getAccessibleNeighbourSquares(current, current.x, current.y)) {

					if (!setContainsSquare(closedSet, neighbour)) {

						int tentative_gScore = gScore.get(current) + getHeuristicCostEstimate(current, neighbour);

						if (!setContainsSquare(openSet, neighbour)) {
							openSet.add(neighbour);
						}

						if (gScore.get(neighbour) == null || tentative_gScore < gScore.get(neighbour)) {
							// cameFrom[neighbor] := current
							gScore.put(neighbour, tentative_gScore);
							fScore.put(neighbour,
									gScore.get(neighbour) + getHeuristicCostEstimate(neighbour, testGoal));
						}
					}
				}

				System.out.println("");
			}

			return null;
		}

		private boolean setContainsSquare(Set<Square> set, Square square) {
			boolean ret = false;
			for (Square entry : set) {
				if (entry.x == square.x && entry.y == square.y) {
					ret = true;
				}
			}
			return ret;
		}

		private int getHeuristicCostEstimate(Square start, Square goal) {
			return Math.abs(goal.x - start.x) + Math.abs(goal.y - start.y);
		}

		private List<Square> getAccessibleNeighbourSquares(Square origin, int xPos, int yPos) {
			List<Square> accessibleNeighbourSquares = new ArrayList<Square>();
			try {
				addNeighbourToListIfAccessible(accessibleNeighbourSquares,
						new Square(map[yPos - 1][xPos], origin.getXCoordinate(), origin.getYCoordinate() - 1));
				addNeighbourToListIfAccessible(accessibleNeighbourSquares,
						new Square(map[yPos + 1][xPos], origin.getXCoordinate(), origin.getYCoordinate() + 1));
				addNeighbourToListIfAccessible(accessibleNeighbourSquares,
						new Square(map[yPos][xPos + 1], origin.getXCoordinate() + 1, origin.getYCoordinate()));
				addNeighbourToListIfAccessible(accessibleNeighbourSquares,
						new Square(map[yPos][xPos - 1], origin.getXCoordinate() - 1, origin.getYCoordinate()));
			} catch (ArrayIndexOutOfBoundsException e) {
				// do not add since it is outside of the map
			}
			return accessibleNeighbourSquares;
		}

		private void addNeighbourToListIfAccessible(List<Square> accessibleNeighbourSquares, Square neighbour) {
			if (neighbour.isSquareVisitable()) {
				accessibleNeighbourSquares.add(neighbour);
			}
		}

		private boolean isSquareContainingObjective() {
			if (testGoal.x == this.x && testGoal.y == this.y) {
				return true;
			}
			return false;
		}

		private boolean isSquareVisitable() {
			if (type != Type.FENCE) {
				return true;
			}

			return false;
		}

		protected int getXCoordinate() {
			return x;
		}

		protected int getYCoordinate() {
			return y;
		}

		public String toString() {
			return getXCoordinate() + " || " + getYCoordinate();
		}
	}
}
