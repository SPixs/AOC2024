import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class Day20 {

    static int width = 0;
    static int height = 0;
    static char[][] map = null;
    static Point2D startLocation = null;
    static Point2D endLocation = null;

    private enum Direction {
        UP(0, -1), RIGHT(1, 0), DOWN(0, 1), LEFT(-1, 0);

        final int dx;
        final int dy;

        Direction(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }

        Point2D getNextLocation(Point2D location) {
            return new Point2D(location.x + dx, location.y + dy);
        }
    }

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Path.of("input_day20.txt"));

        // Create map
        height = lines.size();
        width = lines.get(0).length();
        map = new char[width][height];

        for (int y = 0; y < height; y++) {
            String line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                map[x][y] = c;
                if (c == 'S') startLocation = new Point2D(x, y);
                if (c == 'E') endLocation = new Point2D(x, y);
            }
        }

        // Compute distance matrix with Floyd-Warshall
        int nodes = width * height;
        int INF = Integer.MAX_VALUE / 2; 
        int[][] distancesMatrix = new int[nodes][nodes];

        // Init matrix
        for (int i = 0; i < nodes; i++) {
            Arrays.fill(distancesMatrix[i], INF);
            distancesMatrix[i][i] = 0; // Distance to itself
        }

        // Init distances to direct neighbours
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Point2D location = new Point2D(x, y);
				if (isPassable(location)) {
                    int currentId = y * width + x;
                    for (Direction dir : Direction.values()) {
                        Point2D neighbor = dir.getNextLocation(location);
                        if (isInBounds(neighbor) && isPassable(neighbor)) {
                            int neighborId = neighbor.y * width + neighbor.x;
                            distancesMatrix[currentId][neighborId] = 1; 
                        }
                    }
                }
            }
        }

        // Use dynamic programming to compute all shortest distance pairs
        for (int tk = 0; tk < nodes; tk++) {
            // Print progress
            if (tk % 100 == 0) {
                System.out.println("Processing node " + tk + " / " + nodes);
            }
            final int k = tk;
            IntStream.range(0, nodes).parallel().forEach(i -> {
            	if (distancesMatrix[i][k] < INF) { 
                    for (int j = i + 1; j < nodes; j++) { 
                        if (distancesMatrix[k][j] < INF) {
                            if (distancesMatrix[i][j] > distancesMatrix[i][k] + distancesMatrix[k][j]) {
                            	// distance between 2 points is symetric
                                distancesMatrix[i][j] = distancesMatrix[i][k] + distancesMatrix[k][j];
                                distancesMatrix[j][i] = distancesMatrix[i][j]; 
                            }
                        }
                    }
                }
            });
        }

        // Compute entry and exit indexes
        int startIndex = startLocation.y * width + startLocation.x;
        int endIndex = endLocation.y * width + endLocation.x;

        int shortestDistance = distancesMatrix[startIndex][endIndex];
        System.out.println("Shortest distance " + shortestDistance);
        
        int total = countCheats(distancesMatrix, endIndex, startIndex, shortestDistance, 2);
        System.out.println("Result part 1 : " + total);
        
        total = countCheats(distancesMatrix, endIndex, startIndex, shortestDistance, 20);
        System.out.println("Result part 2 : " + total);
    }

	private static int countCheats(int[][] dist, int endId, int startId, int shortestDistance, int cheatDuration) {
		int total = 0;
        for (int i=0;i<dist.length;i++) {
            for (int j=0;j<dist.length;j++) {
            	int x1 = i % width;
            	int y1 = i / width;
            	int x2 = j % width;
            	int y2 = j / width;
            	int manhattanDistance = Math.abs(x2-x1) + Math.abs(y2-y1);
            	int saved = shortestDistance - (manhattanDistance + dist[startId][i] + dist[j][endId]);
            	if (manhattanDistance <= cheatDuration && saved >= 100) {
            		total++;
            	}
            }
        }
		return total;
	}

    private static boolean isInBounds(Point2D p) {
        return p.x >= 0 && p.x < width && p.y >= 0 && p.y < height;
    }

    private static boolean isPassable(Point2D p) {
        char c = map[p.x][p.y];
        return c == '.' || c == 'S' || c == 'E';
    }

    private static class Point2D {
        int x, y;

        Point2D(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (!(obj instanceof Point2D))
                return false;
            Point2D other = (Point2D) obj;
            return x == other.x && y == other.y;
        }

        @Override
        public String toString() {
            return "(" + x + "," + y + ")";
        }
    }
}
