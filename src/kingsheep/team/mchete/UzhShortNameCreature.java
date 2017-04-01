package kingsheep.team.mchete;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import kingsheep.Creature;
import kingsheep.Simulator;
import kingsheep.Type;

public abstract class UzhShortNameCreature extends Creature {

	private Square goal;

	private Type map[][];

	private Map<Square, Double> objectives;

	LinkedHashSet<Square> path;

	private Square root;

	public UzhShortNameCreature(Type type, Simulator parent, int playerID, int x, int y) {
		super(type, parent, playerID, x, y);
	}

	private Square evaluateNextGoal() {
		Square ret = null;

		for (Entry<Square, Double> objective : objectives.entrySet()) {
			path.clear();
			goal = objective.getKey();
			root.aStarSearch();

			/**
			 * Value the path to a square containing a rhubarb 5 times better
			 * than the path to a square containing grass
			 **/
			if (objective.getKey().type == Type.GRASS) {
				objectives.put(objective.getKey(), ((double) 1 / (double) (path.size() - 1)));
			} else if (objective.getKey().type == Type.RHUBARB) {
				objectives.put(objective.getKey(), ((double) 5 / (double) (path.size() - 1)));
			}
		}

		List<Square> sortedObjectives = objectives.entrySet().stream()
				.sorted(Map.Entry.<Square, Double>comparingByValue().reversed()).map(Map.Entry::getKey)
				.collect(Collectors.toList());

		if (sortedObjectives.size() > 0) {
			ret = sortedObjectives.get(0);
		}

		return ret;
	}

	public String getNickname() {
		return "BigKingSheepXXL";
	}

	// i = y, j = x
	protected Move getAction(Type map[][], char[] objectives) {
		this.map = map;
		this.objectives = new LinkedHashMap<>();
		path = new LinkedHashSet<>();
		root = new Square(map[y][x], x, y);

		scanMapForObjectives(objectives);
		goal = evaluateNextGoal();
		path.clear();

		if (goal != null) {
			root.aStarSearch();
		}

		return getNextMove();
	}

	private Move getNextMove() {
		Move ret = Move.WAIT;
		for (Square nextSquare : path) {
			if (nextSquare.gotHereFrom != null && nextSquare.gotHereFrom.x == this.x
					&& nextSquare.gotHereFrom.y == this.y) {
				ret = nextSquare.howToGetHere;
			}
		}
		return ret;
	}

	private void scanMapForObjectives(char[] objectives) {
		for (int i = 0; i < this.map.length - 1; i++) {
			for (int j = 0; j < this.map[0].length - 1; j++) {
				for (char entry : objectives) {
					if (this.map[i][j].equals(Type.getType(entry))) {
						this.objectives.put(new Square(Type.getType(entry), j, i), (double) 1000);
					}
				}
			}
		}
	}

	class Square {

		Set<Square> closedSet;

		Map<Square, Integer> fScore;

		private Square gotHereFrom;

		Map<Square, Integer> gScore;

		private Move howToGetHere;

		Set<Square> openSet;

		Type type;

		private int x, y;

		protected Square(Type type, int x, int y) {
			this.type = type;
			this.x = x;
			this.y = y;
		}

		protected Square(Type type, int x, int y, Move howToGetHere, Square gotHereFrom) {
			this.gotHereFrom = gotHereFrom;
			this.howToGetHere = howToGetHere;
			this.type = type;
			this.x = x;
			this.y = y;
		}

		private void addNeighbourToListIfAccessible(List<Square> accessibleNeighbourSquares, Square neighbour) {
			if (neighbour.isSquareVisitable()) {
				accessibleNeighbourSquares.add(neighbour);
			}
		}

		protected void aStarSearch() {
			closedSet = new HashSet<>();
			fScore = new LinkedHashMap<>();
			gotHereFrom = null;
			gScore = new HashMap<>();
			howToGetHere = null;
			openSet = new HashSet<>();

			openSet.add(this);
			gScore.put(this, 0);
			fScore.put(this, getHeuristicCostEstimate(this, goal));

			while (!openSet.isEmpty()) {

				List<Square> sortedSquaresfScore = fScore.entrySet().stream()
						.sorted(Map.Entry.<Square, Integer>comparingByValue()).map(Map.Entry::getKey)
						.collect(Collectors.toList());

				Square current = null;

				for (Square entry : sortedSquaresfScore) {
					// System.out.print(entry + " fScore: " +
					// fScore.get(entry));
					if (isSetContainsSquare(openSet, entry)) {
						// System.out.println(" -> taken!");
						// System.out.println("");
						current = entry;
						break;
					}
					// System.out.println("");
				}

				if (current != null && current.isGoalReached()) {
					// System.out.println("OBJECTIVE_REACHED");
					current.reconstructPath();
					break;
				}

				openSet.remove(current);
				closedSet.add(current);

				for (Square neighbour : getAccessibleNeighbourSquares(current, current.x, current.y, current)) {

					if (!isSetContainsSquare(closedSet, neighbour)) {

						int tentative_gScore = gScore.get(current) + getHeuristicCostEstimate(current, neighbour);

						if (!isSetContainsSquare(openSet, neighbour)) {
							openSet.add(neighbour);
						}

						if (gScore.get(neighbour) == null || tentative_gScore < gScore.get(neighbour)) {
							gScore.put(neighbour, tentative_gScore);
							fScore.put(neighbour, gScore.get(neighbour) + getHeuristicCostEstimate(neighbour, goal));
						}
					}
				}
			}
		}

		private List<Square> getAccessibleNeighbourSquares(Square origin, int xPos, int yPos, Square current) {
			List<Square> accessibleNeighbourSquares = new ArrayList<Square>();
			try {
				addNeighbourToListIfAccessible(accessibleNeighbourSquares, new Square(map[yPos - 1][xPos],
						origin.getXCoordinate(), origin.getYCoordinate() - 1, Move.UP, current));
				addNeighbourToListIfAccessible(accessibleNeighbourSquares, new Square(map[yPos + 1][xPos],
						origin.getXCoordinate(), origin.getYCoordinate() + 1, Move.DOWN, current));
				addNeighbourToListIfAccessible(accessibleNeighbourSquares, new Square(map[yPos][xPos + 1],
						origin.getXCoordinate() + 1, origin.getYCoordinate(), Move.RIGHT, current));
				addNeighbourToListIfAccessible(accessibleNeighbourSquares, new Square(map[yPos][xPos - 1],
						origin.getXCoordinate() - 1, origin.getYCoordinate(), Move.LEFT, current));
			} catch (ArrayIndexOutOfBoundsException e) {
				// do not add since this square is outside of the map
			}
			return accessibleNeighbourSquares;
		}

		private int getHeuristicCostEstimate(Square start, Square goal) {
			return Math.abs(goal.x - start.x) + Math.abs(goal.y - start.y);
		}

		protected int getXCoordinate() {
			return x;
		}

		protected int getYCoordinate() {
			return y;
		}

		private boolean isGoalReached() {
			if (goal.x == this.x && goal.y == this.y) {
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

		private void reconstructPath() {
			path.add(this);
			if (this.gotHereFrom != null) {
				this.gotHereFrom.reconstructPath();
			}
		}

		private boolean isSetContainsSquare(Set<Square> set, Square square) {
			boolean ret = false;
			for (Square entry : set) {
				if (entry.x == square.x && entry.y == square.y) {
					ret = true;
				}
			}
			return ret;
		}

		@Override
		public String toString() {
			return getXCoordinate() + "_" + getYCoordinate();
		}
	}
}
