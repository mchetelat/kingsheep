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

	private Square badWolf;

	private Square goal;

	protected Type map[][];

	protected boolean noMoreFoodAvailable = false;

	private Map<Square, Integer> objectives;

	private LinkedHashSet<Square> path;

	private Square root;

	private Map<Square, Square> cameFrom;

	private Set<Square> closedSet;

	private Map<Square, Integer> fScore;

	private Map<Square, Integer> gScore;

	private Set<Square> openSet;

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
			for (Square entry : path) {
				if (entry.x - 1 == this.x && entry.y == this.y) {
					return Move.RIGHT;
				} else if (entry.x + 1 == this.x && entry.y == this.y) {
					return Move.LEFT;
				} else if (entry.x == this.x && entry.y - 1 == this.y) {
					return Move.DOWN;
				} else if (entry.x == this.x && entry.y + 1 == this.y) {
					return Move.UP;
				}
			}
		} else {
			Square currentLocation = new Square(this.type, x, y);
			int pathLengthToBadWolf = path.size();

			for (Square neighbour : currentLocation.getAccessibleNeighbourSquares()) {
				neighbour.aStarSearch();

				if (path.size() > pathLengthToBadWolf) {
					if (neighbour.x - 1 == this.x && neighbour.y == this.y) {
						return Move.RIGHT;
					} else if (neighbour.x + 1 == this.x && neighbour.y == this.y) {
						return Move.LEFT;
					} else if (neighbour.x == this.x && neighbour.y - 1 == this.y) {
						return Move.DOWN;
					} else if (neighbour.x == this.x && neighbour.y + 1 == this.y) {
						return Move.UP;
					}
				}
			}
		}

		return ret;
	}

	public String getNickname() {
		return "BigKingSheepXXL";
	}

	protected Move getRandomMove() {
		int t = (int) (Math.random() * 4);

		switch (t) {
		case 0:
			return Move.UP;
		case 1:
			return Move.DOWN;
		case 2:
			return Move.LEFT;
		case 3:
			return Move.RIGHT;
		default:
			return Move.WAIT;
		}
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

	private boolean isCoordinateValid(int x, int y) {
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

	private void scanMapForItems(char[] objectives) {
		for (int i = 0; i < this.map.length; i++) {
			for (int j = 0; j < this.map[0].length; j++) {
				for (char entry : objectives) {
					if (this.map[i][j].equals(Type.getType(entry))) {
						this.objectives.put(new Square(Type.getType(entry), j, i), 1000);
					} else if (this.map[i][j].equals(Type.getType('4'))) {
						badWolf = new Square(Type.getType('4'), j, i);
					}
				}
			}
		}
	}

	class Square {

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((type == null) ? 0 : type.hashCode());
			result = prime * result + x;
			result = prime * result + y;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Square other = (Square) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (type != other.type)
				return false;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}

		private Type type;

		private int x;

		private int y;

		protected Square(Type type, int x, int y) {
			this.type = type;
			this.x = x;
			this.y = y;
		}

		private void addNeighbourToListIfAccessible(List<Square> accessibleNeighbourSquares, Square neighbour) {
			if (neighbour.type != Type.FENCE) {
				accessibleNeighbourSquares.add(neighbour);
			}
		}

		private void aStarSearch() {
			cameFrom = new LinkedHashMap<>();
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
					reconstructPath(current);
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

						if (gScore.get(neighbour) == null ? true : tentative_gScore < gScore.get(neighbour)) {
							cameFrom.put(neighbour, current);
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
						new Square(map[y - 1][x], getXCoordinate(), getYCoordinate() - 1));
			}

			if (isCoordinateValid(x, y + 1)) {
				addNeighbourToListIfAccessible(accessibleNeighbourSquares,
						new Square(map[y + 1][x], getXCoordinate(), getYCoordinate() + 1));
			}

			if (isCoordinateValid(x + 1, y)) {
				addNeighbourToListIfAccessible(accessibleNeighbourSquares,
						new Square(map[y][x + 1], getXCoordinate() + 1, getYCoordinate()));
			}

			if (isCoordinateValid(x - 1, y)) {
				addNeighbourToListIfAccessible(accessibleNeighbourSquares,
						new Square(map[y][x - 1], getXCoordinate() - 1, getYCoordinate()));
			}

			return accessibleNeighbourSquares;
		}

		private int getHeuristicCostEstimate(Square start, Square goal) {
			return Math.abs(goal.x - start.x) + Math.abs(goal.y - start.y);
		}

		private McheteCreature getOuterType() {
			return McheteCreature.this;
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

		private boolean isNeighbourSquaresSafe() {
			boolean ret = true;
			for (Square neighbour : getAccessibleNeighbourSquares()) {
				if (neighbour.isDeadly()) {
					ret = false;
				}
			}
			return ret;
		}

		private boolean isSetContainsSquare(Set<Square> set, Square square) {
			boolean ret = false;
			for (Square entry : set) {
				if (entry.equals(square)) {
					ret = true;
				}
			}
			return ret;
		}

		protected boolean isSquareSafe() {
			boolean ret = false;
			if (isSquareVisitable() && !isDeadly()) {
				ret = isNeighbourSquaresSafe();
			}
			return ret;
		}

		private boolean isSquareVisitable() {
			boolean ret = true;
			if (type == Type.FENCE || type == Type.WOLF1 || type == Type.SHEEP2) {
				ret = false;
			}
			return ret;
		}

		private void reconstructPath(Square current) {
			path.add(current);
			for (Entry<Square, Square> entry : cameFrom.entrySet()) {
				if (entry.getKey().equals(current)) {
					reconstructPath(entry.getValue());
				}
			}
		}

		@Override
		public String toString() {
			return getXCoordinate() + "_" + getYCoordinate();
		}
	}
}