import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class Day12 {

	static int width = 0;
	static int height = 0;
	static char[][] map = null;
	
	public static void main(String[] args) throws IOException {
		List<String> lines = Files.readAllLines(Path.of("input_day12.txt"));
		
		Set<Point2D> toProcess = new HashSet<Point2D>();
		
		// BUild map
		width = lines.get(0).length();
		height = lines.size();

		int y = 0;
		map = new char[width][height];
		for (String line : lines) {
			for (int x=0;x<line.length();x++) {
				map[x][y] = line.charAt(x);
				toProcess.add(new Point2D(x, y));
			}
			y++;
		}
			
		// Part 1
		long result = 0;
		
		Set<Point2D> toProcessBackup = new HashSet<Day12.Point2D>(toProcess);
		while (!toProcess.isEmpty()) {
			Iterator<Point2D> iterator = toProcess.iterator();
			Point2D next = iterator.next();
			iterator.remove();
			Set<Point2D> processed = new HashSet<Point2D>();
			result += processRegion(next, processed);
			toProcess.removeAll(processed);
		}
		
		System.out.println("Result part 1 : " + result);

		// Part 2
		result = 0;
		toProcess = new HashSet<Day12.Point2D>(toProcessBackup);
		while (!toProcess.isEmpty()) {
			Iterator<Point2D> iterator = toProcess.iterator();
			Point2D next = iterator.next();
			iterator.remove();
			Set<Point2D> processed = new HashSet<Point2D>();
			result += processRegionPart2(next, processed);
			toProcess.removeAll(processed);
		}
		System.out.println("Result part 2 : " + result);
	}
	
	private static long processRegion(Point2D next, Set<Point2D> processed) {
		AtomicInteger area = new AtomicInteger();
		AtomicInteger edges = new AtomicInteger();
		
		recurse(map[next.x][next.y], next, area, edges, processed);
		return area.get() * edges.get();
	}

	private static long processRegionPart2(Point2D next, Set<Point2D> processed) {
		AtomicInteger area = new AtomicInteger();
		
		recurse(map[next.x][next.y], next, area, new AtomicInteger(), processed);
		
		int minX = processed.stream().mapToInt(p -> p.x).min().orElseThrow();
		int maxX = processed.stream().mapToInt(p -> p.x).max().orElseThrow();
		int minY = processed.stream().mapToInt(p -> p.y).min().orElseThrow();
		int maxY = processed.stream().mapToInt(p -> p.y).max().orElseThrow();
		
		int edges = 0;

		for (int y=minY;y<=maxY;y++) {
			boolean flagUp = false;
			boolean flagDown = false;
			for (int x=minX;x<=maxX;x++) {
				Point2D p = new Point2D(x, y);
				if (processed.contains(p)) {
					if (!flagUp) {
						if(y==minY || (!processed.contains(new Point2D(x, y-1)))) {
							flagUp = true;
							edges++;
						}
					}
					else {
						if (y != minY && processed.contains(new Point2D(x, y-1))) { flagUp = false; }
					}
					if (!flagDown) {
						if(y==maxY || (!processed.contains(new Point2D(x, y+1)))) {
							flagDown = true;
							edges++;
						}
					}
					else {
						if (y != maxY && processed.contains(new Point2D(x, y+1))) { flagDown = false; }
					}
				}
				else {
					flagUp = flagDown = false;
				}
			}
		}
		for (int x=minX;x<=maxX;x++) {
			boolean flagLeft = false;
			boolean flagRight = false;
			for (int y=minY;y<=maxY;y++) {
				Point2D p = new Point2D(x, y);
				if (processed.contains(p)) {
					if (!flagLeft) {
						if(x==minX || (!processed.contains(new Point2D(x-1, y)))) {
							flagLeft = true;
							edges++;
						}
					}
					else {
						if (x != minX && processed.contains(new Point2D(x-1, y))) { flagLeft = false; }
					}
					if (!flagRight) {
						if(x==maxX || (!processed.contains(new Point2D(x+1, y)))) {
							flagRight = true;
							edges++;
						}
					}
					else {
						if (x != maxX && processed.contains(new Point2D(x+1, y))) { flagRight = false; }
					}
				}
				else {
					flagLeft = flagRight = false;
				}
			}
		}

		return area.get() * edges;
	}

	private static void recurse(char type, Point2D next, AtomicInteger area, AtomicInteger edges, Set<Point2D> processed) {
		if (next.x < 0 || next.x >= width || next.y < 0 || next.y > height) return;
		if (map[next.x][next.y] != type) return;
		if (processed.contains(next)) return;
		
		area.incrementAndGet();
		processed.add(next);
		
		if (next.y > 0) {
			char up = map[next.x][next.y-1];
			if (up != type) {
				edges.incrementAndGet();
			}
			else {
				recurse(type, new Point2D(next.x, next.y-1), area, edges, processed);
			}
		}
		else {
			edges.incrementAndGet();
		}

		if (next.y < height-1) {
			char down = map[next.x][next.y+1];
			if (down != type) {
				edges.incrementAndGet();
			}
			else {
				recurse(type, new Point2D(next.x, next.y+1), area, edges, processed);
			}
		}
		else {
			edges.incrementAndGet();
		}

		if (next.x > 0) {
			char left = map[next.x-1][next.y];
			if (left != type) {
				edges.incrementAndGet();
			}
			else {
				recurse(type, new Point2D(next.x-1, next.y), area, edges, processed);
			}
		}
		else {
			edges.incrementAndGet();
		}

		if (next.x < width-1) {
			char right = map[next.x+1][next.y];
			if (right != type) {
				edges.incrementAndGet();
			}
			else {
				recurse(type, new Point2D(next.x+1, next.y), area, edges, processed);
			}
		}
		else {
			edges.incrementAndGet();
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
}
