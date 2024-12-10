import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Day10 {

	
	static int width = 0;
	static int height = 0;
	static int[][] map = null;
	
	public static void main(String[] args) throws IOException {
		List<String> lines = Files.readAllLines(Path.of("input_day10.txt"));

		// BUild map
		width = lines.get(0).length();
		height = lines.size();

		int y = 0;
		map = new int[width][height];
		for (String line : lines) {
			for (int x=0;x<line.length();x++) {
				map[x][y] = Integer.parseInt(String.valueOf(line.charAt(x)));
			}
			y++;
		}

		// Part 1
		int result = 0;
		for (int x=0;x<width;x++) {
			for (y=0;y<height;y++) {
				if (map[x][y] == 0) {
					result += recurseTrailFromHead(x, y);
				}
			}
		}
		
		System.out.println("Result part 1 : " + result);

		// Part 2
		result = 0;
		for (int x=0;x<width;x++) {
			for (y=0;y<height;y++) {
				if (map[x][y] == 0) {
					result += recurseTrailFromHeadPart2(x, y);
				}
			}
		}
		System.out.println("Result part 2 : " + result);
	}

	private static int recurseTrailFromHead(int x, int y) {
		Set<Point2D> topPositions = new HashSet<Point2D>();
		recurseTrail(x, y, -1, topPositions);
		return topPositions.size();
	}

	private static void recurseTrail(int x, int y, int fromHeight, Set<Point2D> topPositions) {
		int toHeight = map[x][y];
		if (fromHeight != toHeight-1) return;
		if (toHeight == 9) {
			topPositions.add(new Point2D(x, y));
			return;
		}

		if (x > 0)  recurseTrail(x-1, y, toHeight, topPositions);
		if (x < width-1) recurseTrail(x+1, y, toHeight, topPositions);
		if (y > 0) recurseTrail(x, y-1, toHeight, topPositions);
		if (y < height-1) recurseTrail(x, y+1, toHeight, topPositions);
		
		return;
	}

	private static int recurseTrailFromHeadPart2(int x, int y) {
		return recurseTrailPart2(x, y, -1);
	}
	
	private static int recurseTrailPart2(int x, int y, int fromHeight) {
		int toHeight = map[x][y];
		if (fromHeight != toHeight-1) return 0;
		if (toHeight == 9) {
			return 1;
		}

		int result = 0;
		if (x > 0) result += recurseTrailPart2(x-1, y, toHeight);
		if (x < width-1) result += recurseTrailPart2(x+1, y, toHeight);
		if (y > 0) result += recurseTrailPart2(x, y-1, toHeight);
		if (y < height-1) result += recurseTrailPart2(x, y+1, toHeight);
		
		return result;
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
