import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Day16 {

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

        Direction turnClockwise() {
            return values()[(ordinal() + 1) % values().length];
        }

        Direction turnCounterclockwise() {
            return values()[(ordinal() - 1 + values().length) % values().length];
        }
    }

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Path.of("input_day16.txt"));

        // Construction de la carte
        width = lines.get(0).length();
        height = lines.size();
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

        // Part 1 (path with lowest score)
        Result result = findShortestPathCost();
        System.out.println("Result part 1 : " + result.minCost);

        // Part 2 (all squares belonging to paths with lowest score)
        Set<Point2D> shortestPathNodes = getAllShortestPathPositions(result);
        System.out.println("Result part 2 : " + shortestPathNodes.size()); 
    }

    private static Result findShortestPathCost() {
        PriorityQueue<Position> queue = new PriorityQueue<>(Comparator.comparingInt(p -> p.score));
        Position startPos = new Position(startLocation, Direction.RIGHT, 0);
        queue.add(startPos);

        Map<Position, Integer> bestScores = new HashMap<>();
        Map<Position, Set<Position>> parents = new HashMap<>();

        int minCost = Integer.MAX_VALUE;
        Position endPosFound = null;

        while (!queue.isEmpty()) {
            Position current = queue.poll();

            // Ignore path with higher score
            if (bestScores.containsKey(current) && bestScores.get(current) < current.score) {
                continue;
            }
            bestScores.put(current, current.score);

            // Path to exit with a lower score found
            if (current.location.equals(endLocation) && current.score < minCost) {
                minCost = current.score;
                endPosFound = current;
            }

            for (Position next : getNextPositions(current)) {
            	
                int nextScore = next.score;
                Integer bestScoreForNext = bestScores.get(next);

                if (bestScoreForNext == null || bestScoreForNext > nextScore) {
                    // New best path
                    queue.add(next);
                    // Reset parents
                    bestScores.put(next, nextScore);
                    parents.put(next, new HashSet<>(Collections.singleton(current)));
                } else if (bestScoreForNext == nextScore) {
                    // Another best path found (same score), append parent
                    parents.get(next).add(current);
                }
            }
        }

        return new Result(minCost, bestScores, parents, endPosFound);
    }

    private static Set<Point2D> getAllShortestPathPositions(Result result) {
    	Set<Point2D> pathPoints = new HashSet<Point2D>();
    	recurse(pathPoints, result.endPos, result);
    	return pathPoints;
    }

    private static void recurse(Set<Point2D> pathPoints, Position position, Result result) {
    	pathPoints.add(position.location);
    	Set<Position> set = result.parents.get(position);
    	if (set != null)  {
	    	for (Position position2 : set) {
				recurse(pathPoints, position2, result);
			}
    	}
	}

	private static List<Position> getNextPositions(Position position) {
        List<Position> result = new ArrayList<>();

        // Forward
        Point2D nextLocation = position.direction.getNextLocation(position.location);
        if (isInBounds(nextLocation) && map[nextLocation.x][nextLocation.y] != '#') {
            result.add(new Position(nextLocation, position.direction, position.score + 1));
        }

        // Turn clockwise
        Direction right = position.direction.turnClockwise();
        result.add(new Position(position.location, right, position.score + 1000));

        // Turn counter clockwise
        Direction left = position.direction.turnCounterclockwise();
        result.add(new Position(position.location, left, position.score + 1000));

        return result;
    }

    private static boolean isInBounds(Point2D p) {
        return p.x >= 0 && p.x < width && p.y >= 0 && p.y < height;
    }

    private static class Position {
        Point2D location;
        Direction direction;
        int score;

        Position(Point2D location, Direction direction, int score) {
            this.location = location;
            this.direction = direction;
            this.score = score;
        }

        @Override
        public int hashCode() {
            return Objects.hash(location, direction);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Position)) return false;
            Position other = (Position) obj;
            return Objects.equals(location, other.location) && direction == other.direction;
        }
        
        @Override
        public String toString() {
        	return location + " " + direction + " (" + score + ")";
        }
    }

    private static class Point2D {
        int x, y;

        Point2D(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x,y);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Point2D)) return false;
            Point2D other = (Point2D) obj;
            return x == other.x && y == other.y;
        }

        @Override
        public String toString() {
            return "(" + x + "," + y + ")";
        }
    }

    private static class Result {
        int minCost;
        Map<Position, Integer> bestScores;
        Map<Position, Set<Position>> parents;
        Position endPos;

        Result(int minCost, Map<Position, Integer> bestScores, Map<Position, Set<Position>> parents, Position endPos) {
            this.minCost = minCost;
            this.bestScores = bestScores;
            this.parents = parents;
            this.endPos = endPos;
        }
    }
}
