import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Day15 {

	static int width = 0;
	static int height = 0;
	static char[][] map = null;
	static Point2D robotLocation = null;
	static String moves = "";
	
	private enum Direction {
		UP(0,-1), RIGHT(1,0), DOWN(0,1), LEFT(-1,0);
		
		private int dx;
		private int dy;

		Direction(int dx, int dy) {
			this.dx = dx;
			this.dy = dy;
		}

		/**
		 * @param location
		 * @return the next location in the current direction
		 */
		Point2D getNextLocation(Point2D location) {
			return new Point2D(location.x+dx, location.y+dy);
		}
	}
	
	public static void main(String[] args) throws IOException {
		List<String> lines = Files.readAllLines(Path.of("input_day15.txt"));
		
		// Build map
		width = lines.get(0).length();
		height = lines.size();

		int y = 0;
		map = new char[width][height];
		for (String line : lines) {
			if (line.contains("#") || line.contains("O") || line.contains(".")) {
				for (int x=0;x<line.length();x++) {
					map[x][y] = line.charAt(x);
					if (map[x][y] == '@') {
						robotLocation = new Point2D(x, y);
					}
				}
				y++;
			}
			else if (line.contains("^") || line.contains("<") || line.contains(">") || line.contains("v")) {
				moves += line.trim();
			}
		}
		height = y;
			
		// Part 1
		long result = 0;
		simulatePart1();
		for (y=0;y<height;y++) {
			for (int x=0;x<width;x++) {
				if (map[x][y] == 'O') {
					result += x + 100 * y;
				}
			}
		}
		System.out.println("Result part 1 : " + result);

		// Part 2
		// Rebuild map
		y = 0;
		width *= 2;
		map = new char[width][height];
		for (String line : lines) {
			if (line.contains("#") || line.contains("O") || line.contains(".")) {
				for (int x=0;x<line.length();x++) {
					char c = line.charAt(x);
					switch (c) {
						case '#':
							map[x*2][y] = '#';
							map[x*2+1][y] = '#';
							break;
						case 'O':
							map[x*2][y] = '[';
							map[x*2+1][y] = ']';
							break;
						case '.':
							map[x*2][y] = '.';
							map[x*2+1][y] = '.';
							break;
						case '@':
							map[x*2][y] = '@';
							map[x*2+1][y] = '.';
							robotLocation = new Point2D(x*2, y);
							break;
						default:
							throw new IllegalStateException();
					}
				}
				y++;
			}
		}
		
		result = 0;
		simulatePart2();
		for (y=0;y<height;y++) {
			for (int x=0;x<width;x++) {
				if (map[x][y] == '[') {
					result += x + 100 * y;
				}
			}
		}
		System.out.println("Result part 2 : " + result);
	}
	
	private static void simulatePart1() {
		for (char move : moves.toCharArray()) {
			switch (move) {
				case '>': move(robotLocation, Direction.RIGHT); break;
				case 'v': move(robotLocation, Direction.DOWN); break;
				case '<': move(robotLocation, Direction.LEFT); break;
				case '^': move(robotLocation, Direction.UP); break;
				default: throw new IllegalArgumentException();
			}
		}
	}
	
	private static void simulatePart2() {
		for (char move : moves.toCharArray()) {
			switch (move) {
				case '>': moveHorizontally(robotLocation, Direction.RIGHT); break;
				case 'v': moveVertically(robotLocation, Direction.DOWN); break;
				case '<': moveHorizontally(robotLocation, Direction.LEFT); break;
				case '^': moveVertically(robotLocation, Direction.UP); break;
				default: throw new IllegalArgumentException();
			}
		}
	}

	private static boolean moveHorizontally(Point2D location, Direction direction) {
		boolean isRobot = location.equals(robotLocation);
		Point2D next = direction.getNextLocation(location);
		char nextChar = map[next.x][next.y];
		if (nextChar == '.') {
			map[next.x][next.y] = map[location.x][location.y];
			map[location.x][location.y] = '.';
			if (isRobot) robotLocation = next;
			return true;
		}
		else if (nextChar == '[' || nextChar == ']') {
			if (moveHorizontally(next, direction)) {
				map[next.x][next.y] = map[location.x][location.y];
				map[location.x][location.y] = '.';
				if (isRobot) robotLocation = next;
				return true;
			}
			return false;
		}
		else if (nextChar == '#') {
			return false;
		}
		throw new IllegalStateException();
	}
	
	private static boolean canMoveVertically(Point2D location, Direction direction) {
		Point2D next = direction.getNextLocation(location);
		char nextChar = map[next.x][next.y];
		if (nextChar == '.') { return true; }
		if  (nextChar == '[') {
			return canMoveVertically(next, direction) && canMoveVertically(Direction.RIGHT.getNextLocation(next), direction);
		}
		if  (nextChar == ']') {
			return canMoveVertically(next, direction) && canMoveVertically(Direction.LEFT.getNextLocation(next), direction);
		}
		else if (nextChar == '#') {
			return false;
		}
		throw new IllegalStateException();
	}
	
	private static boolean moveVertically(Point2D location, Direction direction) {
		boolean isRobot = location.equals(robotLocation);
		Point2D next = direction.getNextLocation(location);
		char nextChar = map[next.x][next.y];
		if (nextChar == '[') {
			if (canMoveVertically(location, direction)) {
				Point2D otherNext = Direction.RIGHT.getNextLocation(next);

				moveVertically(next, direction);
				moveVertically(otherNext, direction);
				
				map[next.x][next.y] = map[location.x][location.y];
				map[location.x][location.y] = '.';
				if (isRobot) robotLocation = next;
				return true;
			}
			return false;
		}
		if (nextChar == ']') {
			if (canMoveVertically(location, direction)) {
				Point2D otherNext = Direction.LEFT.getNextLocation(next);

				moveVertically(next, direction);
				moveVertically(otherNext, direction);
				
				map[next.x][next.y] = map[location.x][location.y];
				map[location.x][location.y] = '.';
				if (isRobot) robotLocation = next;
				return true;
			}
			return false;
		}
		if (nextChar == '.') {
			map[next.x][next.y] = map[location.x][location.y];
			map[location.x][location.y] = '.';
			if (isRobot) robotLocation = next;
			return true;
		}
		else if (nextChar == '#') {
			return false;
		}
		throw new IllegalStateException();
	}

	private static boolean move(Point2D location, Direction direction) {
		boolean isRobot = location.equals(robotLocation);
		Point2D next = direction.getNextLocation(location);
		char nextChar = map[next.x][next.y];
		if (nextChar == '.') {
			map[next.x][next.y] = map[location.x][location.y];
			map[location.x][location.y] = '.';
			if (isRobot) robotLocation = next;
			return true;
		}
		else if (nextChar == 'O') {
			if (move(next, direction)) {
				map[next.x][next.y] = map[location.x][location.y];
				map[location.x][location.y] = '.';
				if (isRobot) robotLocation = next;
				return true;
			}
			return false;
		}
		else if (nextChar == '#') {
			return false;
		}
		throw new IllegalStateException();
	}
	
	public static void dumpMap() {
		for (int y=0;y<height;y++) {
			for (int x=0;x<width;x++) {
				System.out.print(map[x][y]);
			}
			System.out.println();
		}
		System.out.println("Robot location " + robotLocation);
	}

	private static class Point2D {
		
		int x, y;
		
		public Point2D(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
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
			Point2D other = (Point2D) obj;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}
		
		@Override
		public String toString() {
			return "("+x+","+y+")";
		}
	}
}
