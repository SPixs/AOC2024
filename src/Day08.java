import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Day08 {

	public static void main(String[] args) throws IOException {
		List<String> lines = Files.readAllLines(Path.of("input_day08.txt"));

		// BUild map
		int width = lines.get(0).length();
		int height = lines.size();

		Map<Character, Set<Point2D>> antennaLocations = new HashMap<Character, Set<Point2D>>();
		
		int y = 0;
		char[][] map = new char[width][height];
		for (String line : lines) {
			for (int x=0;x<line.length();x++) {
				map[x][y] = line.charAt(x);
				if (map[x][y] != '.') {
					Set<Point2D> locations = antennaLocations.getOrDefault(map[x][y], new HashSet<Point2D>());
					locations.add(new Point2D(x, y));
					antennaLocations.put(map[x][y], locations);
				}
			}
			y++;
		}

		// Part 1
		Set<Point2D> antinodesLocations = new HashSet<Day08.Point2D>();
		for (Set<Point2D> locations : antennaLocations.values()) {
			ArrayList<Point2D> locationsList = new ArrayList<Point2D>(locations);
			for (int i=0;i<locationsList.size();i++) {
				Point2D antenna = locationsList.get(i);
				for (int j=0;j<locationsList.size();j++) {
					if (i != j) {
						Point2D otherAntenna = locationsList.get(j);
						int dx = antenna.x - otherAntenna.x;
						int dy = antenna.y - otherAntenna.y;
						int newX = antenna.x + dx;
						int newY = antenna.y + dy;
						if (newX >= 0 && newX < width && newY >= 0  && newY < height) {
							antinodesLocations.add(new Point2D(newX, newY));
						}
					}
				}
			}
		}
		System.out.println("Result part 1 : " + antinodesLocations.size());

		// Part 2
		antinodesLocations.clear();
		for (Set<Point2D> locations : antennaLocations.values()) {
			ArrayList<Point2D> locationsList = new ArrayList<Point2D>(locations);
			for (int i=0;i<locationsList.size();i++) {
				Point2D antenna = locationsList.get(i);
				for (int j=0;j<locationsList.size();j++) {
					if (i != j) {
						antinodesLocations.add(antenna);
						Point2D otherAntenna = locationsList.get(j);
						int dx = antenna.x - otherAntenna.x;
						int dy = antenna.y - otherAntenna.y;
						int newX = antenna.x + dx;
						int newY = antenna.y + dy;
						while (newX >= 0 && newX < width && newY >= 0  && newY < height) {
							antinodesLocations.add(new Point2D(newX, newY));
							newX += dx;
							newY += dy;
						}
					}
				}
			}
		}
		
		System.out.println("Result part 2 : " + antinodesLocations.size());
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
}
