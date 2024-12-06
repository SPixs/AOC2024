import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.ToIntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day06 {

	private enum Direction {
		UP(0,-1), RIGHT(1,0), DOWN(0,1), LEFT(-1,0);
		
		private int dx;
		private int dy;

		Direction(int dx, int dy) {
			this.dx = dx;
			this.dy = dy;
		}

		/**
		 * @return PI/2 clockwise next direction
		 */
		public Direction next() {
			return Direction.values()[(ordinal() + 1) % Direction.values().length];
		}

		/**
		 * @param location
		 * @return the next location in the current direction
		 */
		Point2D getNextLocation(Point2D location) {
			return new Point2D(location.x+dx, location.y+dy);
		}
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
	}
	
	private static class Agent {
		
		Point2D location = null;
		Direction direction = null;
		
		public Agent(Point2D location, Direction direction) {
			this.location = location;
			this.direction = direction;
		}

		public Agent(Agent agent) {
			this.location = agent.location;
			this.direction = agent.direction;
		}

		public boolean isInMap(char[][] map) {
			return isInMap(map, location);
		}

		public boolean isInMap(char[][] map, Point2D location) {
			return location.x >= 0 && location.y >= 0 && location.x < map.length && location.y < map[0].length;
		}

		public Point2D getLocation() {
			return location;
		}

		/**
		 * Simulation step in the map (advance in current direction or turn until next location is free from obstacles)
		 * @param map
		 */
		public void step(char[][] map) {
			Point2D nextStep = direction.getNextLocation(location);
			while (isInMap(map, nextStep) && map[nextStep.x][nextStep.y] == '#') {
				direction = direction.next();
				nextStep = direction.getNextLocation(location);
			}
			location = nextStep;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((direction == null) ? 0 : direction.hashCode());
			result = prime * result + ((location == null) ? 0 : location.hashCode());
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
			Agent other = (Agent) obj;
			if (direction != other.direction)
				return false;
			if (location == null) {
				if (other.location != null)
					return false;
			} else if (!location.equals(other.location))
				return false;
			return true;
		}
	}
	
	/**
	 * @param map
	 * @param agent
	 * @return true if during simulation a (location, direction) is occurring twice
	 */
	public static boolean isLooping(char[][] map, Agent agent) {
		Set<Agent> visitedLocations = new HashSet<Agent>();
		agent = new Agent(agent.location, agent.direction);
		while (agent.isInMap(map)) {
			if (visitedLocations.contains(agent)) {
				return true;
			}
			visitedLocations.add(new Agent(agent.location, agent.direction));
			agent.step(map);
		}
		return false;
	}
	
	public static void main(String[] args) throws IOException {
		List<String> lines = Files.readAllLines(Path.of("input_day06.txt"));

		// BUild map
		int width = lines.get(0).length();
		int height = lines.size();

		int y = 0;
		Agent agent = null;
		char[][] map = new char[width][height];
		for (String line : lines) {
			for (int x=0;x<line.length();x++) {
				map[x][y] = line.charAt(x);
				if (map[x][y] == '^') {
					agent = new Agent(new Point2D(x, y), Direction.UP);
				}
			}
			y++;
		}
		
		// Part 1
		Agent startingAgent = new Agent(agent);
		Set<Point2D> visitedLocations = new HashSet<Point2D>();
		while (agent.isInMap(map)) {
			visitedLocations.add(agent.getLocation());
			agent.step(map);
		}
		System.out.println("Result part 1 : " + visitedLocations.size());

		// Part 2
		int count = 0;
		agent = new Agent(startingAgent);
		if (isLooping(map, agent)) count++;
		for (int x=0;x<map.length;x++) {
			for (y=0;y<map[0].length;y++) {
				agent = new Agent(startingAgent);
				if (map[x][y] != '#' && map[x][y] != '^') {
					map[x][y] = '#';
					if (isLooping(map, agent)) count++;
					map[x][y] = 0;
				}
			}
		}
		System.out.println("Result part 2 : " + count);
	}
}
