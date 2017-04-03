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

public abstract class McheteCreature extends Creature {

	protected Square badWolf;

	private Square goal;

	protected Square greedySheep;

	protected Type map[][];

	protected boolean noMoreFoodAvailable = false;

	private Map<Square, Integer> objectives;

	private LinkedHashSet<Square> path;

	private Square root;

	public McheteCreature(Type type, Simulator parent, int playerID, int x, int y) {
		super(type, parent, playerID, x, y);
	}

	private Square evaluateNextGoal() {
		Square ret = null;
		List<Square> sortedObjectives = new ArrayList<>();

		for (Entry<Square, Integer> objective : objectives.entrySet()) {
			goal = objective.getKey();
			root.aStarSearch();

			objectives.put(objective.getKey(), (path.size() - 1));
		}

		sortedObjectives = objectives.entrySet().stream().sorted(Map.Entry.<Square, Integer>comparingByValue())
				.map(Map.Entry::getKey).collect(Collectors.toList());

		if (sortedObjectives.size() > 0) {
			ret = sortedObjectives.get(0);
		}

		return ret;
	}

	// i = y, j = x
	protected Move getAction(char[] objectives, boolean fleeMode) {
		this.objectives = new LinkedHashMap<>();
		root = new Square(map[y][x], x, y);
		path = new LinkedHashSet<>();
		scanMapForItems(objectives);

		if (!fleeMode) {
			goal = evaluateNextGoal();
		} else {
			goal = badWolf;
		}

		if (goal != null) {
			root.aStarSearch();
		}

		return getNextMove(fleeMode);
	}

	private Move getNextMove(boolean fleeMode) {
		Move ret = Move.WAIT;

		if (!fleeMode) {
			for (Square nextSquare : path) {
				if (nextSquare.gotHereFrom != null && nextSquare.gotHereFrom.x == this.x
						&& nextSquare.gotHereFrom.y == this.y) {
					ret = nextSquare.howToGetHere;
				}
			}
		} else {
			Square currentLocation = new Square(this.type, x, y);
			int pathLengthToBadWolf = path.size();

			for (Square neighbour : currentLocation.getAccessibleNeighbourSquares()) {
				neighbour.aStarSearch();

				if (path.size() > pathLengthToBadWolf) {
					ret = neighbour.howToGetHere;
				}
			}
		}

		return ret;
	}

	public String getNickname() {
		return "BigKingSheepXXL";
	}

	protected Square getSquareFromMove(Move move) {
		int x, y;

		if (move == Move.UP) {
			x = this.x;
			y = this.y - 1;
		} else if (move == Move.DOWN) {
			x = this.x;
			y = this.y + 1;
		} else if (move == Move.LEFT) {
			x = this.x - 1;
			y = this.y;
		} else if (move == Move.RIGHT) {
			x = this.x + 1;
			y = this.y;
		} else {
			x = this.x;
			y = this.y;
		}

		if (isCoordinateValid(x, y)) {
			return new Square(map[y][x], x, y);
		} else {
			return null;
		}
	}

	public boolean isCoordinateValid(int x, int y) {
		boolean ret = false;
		try {
			@SuppressWarnings("unused")
			Type type = map[y][x];
			ret = true;
		} catch (ArrayIndexOutOfBoundsException e) {
			// do nothing
		}
		return ret;
	}

	protected void scanMapForItems(char[] objectives) {
		for (int i = 0; i < this.map.length; i++) {
			for (int j = 0; j < this.map[0].length; j++) {
				for (char entry : objectives) {
					if (this.map[i][j].equals(Type.getType(entry))) {
						this.objectives.put(new Square(Type.getType(entry), j, i), 1000);
					} else if (this.map[i][j].equals(Type.getType('4'))) {
						badWolf = new Square(Type.getType('4'), j, i);
					} else if (this.map[i][j].equals(Type.getType('3'))) {
						greedySheep = new Square(Type.getType('3'), j, i);
					}
				}
			}
		}
	}

	class Square {

		private Set<Square> closedSet;

		private Map<Square, Integer> fScore;

		private Square gotHereFrom;

		private Map<Square, Integer> gScore;

		private Move howToGetHere;

		private Set<Square> openSet;

		private Type type;

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

		private void aStarSearch() {
			closedSet = new HashSet<>();
			fScore = new LinkedHashMap<>();
			gScore = new HashMap<>();
			openSet = new HashSet<>();
			path.clear();

			openSet.add(this);
			gScore.put(this, 0);
			fScore.put(this, getHeuristicCostEstimate(this, goal));

			while (!openSet.isEmpty()) {

				List<Square> sortedSquaresfScore = fScore.entrySet().stream()
						.sorted(Map.Entry.<Square, Integer>comparingByValue()).map(Map.Entry::getKey)
						.collect(Collectors.toList());

				Square current = null;

				for (Square entry : sortedSquaresfScore) {
					if (isSetContainsSquare(openSet, entry)) {
						current = entry;
						break;
					}
				}

				if (current != null && current.isGoalReached()) {
					current.reconstructPath();
					break;
				}

				openSet.remove(current);
				closedSet.add(current);

				for (Square neighbour : current.getAccessibleNeighbourSquares()) {

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

		private List<Square> getAccessibleNeighbourSquares() {
			List<Square> accessibleNeighbourSquares = new ArrayList<Square>();

			if (isCoordinateValid(x, y - 1)) {
				addNeighbourToListIfAccessible(accessibleNeighbourSquares,
						new Square(map[y - 1][x], getXCoordinate(), getYCoordinate() - 1, Move.UP, this));
			}

			if (isCoordinateValid(x, y + 1)) {
				addNeighbourToListIfAccessible(accessibleNeighbourSquares,
						new Square(map[y + 1][x], getXCoordinate(), getYCoordinate() + 1, Move.DOWN, this));
			}

			if (isCoordinateValid(x + 1, y)) {
				addNeighbourToListIfAccessible(accessibleNeighbourSquares,
						new Square(map[y][x + 1], getXCoordinate() + 1, getYCoordinate(), Move.RIGHT, this));
			}

			if (isCoordinateValid(x - 1, y)) {
				addNeighbourToListIfAccessible(accessibleNeighbourSquares,
						new Square(map[y][x - 1], getXCoordinate() - 1, getYCoordinate(), Move.LEFT, this));
			}

			return accessibleNeighbourSquares;
		}

		private int getHeuristicCostEstimate(Square start, Square goal) {
			return Math.abs(goal.x - start.x) + Math.abs(goal.y - start.y);
		}

		protected Move getHowToGetHere() {
			return howToGetHere;
		}

		protected int getXCoordinate() {
			return x;
		}

		protected int getYCoordinate() {
			return y;
		}

		private boolean isDeadly() {
			boolean ret = false;
			if (type == Type.WOLF2) {
				ret = true;
			}
			return ret;
		}

		private boolean isGoalReached() {
			if (goal.x == this.x && goal.y == this.y) {
				return true;
			}
			return false;
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

		protected boolean isSquareSafe() {
			if (isSquareVisitable() && !isDeadly()) {
				return true;
			}
			return false;
		}

		private boolean isSquareVisitable() {
			if (type == Type.FENCE) {
				return false;
			}

			return true;
		}

		private void reconstructPath() {
			path.add(this);
			if (this.gotHereFrom != null) {
				this.gotHereFrom.reconstructPath();
			}
		}

		@Override
		public String toString() {
			return getXCoordinate() + "_" + getYCoordinate();
		}
	}
}