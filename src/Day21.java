import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

public class Day21 {

    private enum Direction {
        UP('^'), RIGHT('>'), DOWN('v'), LEFT('<');

        private char c;

        Direction(char c) {
            this.c = c;
        }

        char toChar() {
            return c;
        }
    }

    private static Node numericKeypadA;
    private static Node numericKeypad0;
    private static Node numericKeypad1;
    private static Node numericKeypad2;
    private static Node numericKeypad3;
    private static Node numericKeypad4;
    private static Node numericKeypad5;
    private static Node numericKeypad6;
    private static Node numericKeypad7;
    private static Node numericKeypad8;
    private static Node numericKeypad9;

    private static Node directionalKeypadA;
    private static Node directionalKeypadUp;
    private static Node directionalKeypadDown;
    private static Node directionalKeypadLeft;
    private static Node directionalKeypadRight;

	private static HashSet<Node> numericNodes;
	private static HashSet<Node> directionalNodes;

    public static void main(String[] args) throws IOException {

        // =================== Init numeric pad nodes ===================
    	
        numericKeypadA = new Node('A');
        numericKeypad0 = new Node('0');
        numericKeypad1 = new Node('1');
        numericKeypad2 = new Node('2');
        numericKeypad3 = new Node('3');
        numericKeypad4 = new Node('4');
        numericKeypad5 = new Node('5');
        numericKeypad6 = new Node('6');
        numericKeypad7 = new Node('7');
        numericKeypad8 = new Node('8');
        numericKeypad9 = new Node('9');

        numericKeypadA.addForeignNode(numericKeypad0, Direction.LEFT);
        numericKeypadA.addForeignNode(numericKeypad3, Direction.UP);
        numericKeypad0.addForeignNode(numericKeypad2, Direction.UP);
        numericKeypad0.addForeignNode(numericKeypadA, Direction.RIGHT);
        numericKeypad1.addForeignNode(numericKeypad4, Direction.UP);
        numericKeypad1.addForeignNode(numericKeypad2, Direction.RIGHT);
        numericKeypad2.addForeignNode(numericKeypad1, Direction.LEFT);
        numericKeypad2.addForeignNode(numericKeypad5, Direction.UP);
        numericKeypad2.addForeignNode(numericKeypad3, Direction.RIGHT);
        numericKeypad2.addForeignNode(numericKeypad0, Direction.DOWN);
        numericKeypad3.addForeignNode(numericKeypad6, Direction.UP);
        numericKeypad3.addForeignNode(numericKeypad2, Direction.LEFT);
        numericKeypad3.addForeignNode(numericKeypadA, Direction.DOWN);
        numericKeypad4.addForeignNode(numericKeypad7, Direction.UP);
        numericKeypad4.addForeignNode(numericKeypad5, Direction.RIGHT);
        numericKeypad4.addForeignNode(numericKeypad1, Direction.DOWN);
        numericKeypad5.addForeignNode(numericKeypad4, Direction.LEFT);
        numericKeypad5.addForeignNode(numericKeypad8, Direction.UP);
        numericKeypad5.addForeignNode(numericKeypad6, Direction.RIGHT);
        numericKeypad5.addForeignNode(numericKeypad2, Direction.DOWN);
        numericKeypad6.addForeignNode(numericKeypad5, Direction.LEFT);
        numericKeypad6.addForeignNode(numericKeypad9, Direction.UP);
        numericKeypad6.addForeignNode(numericKeypad3, Direction.DOWN);
        numericKeypad7.addForeignNode(numericKeypad8, Direction.RIGHT);
        numericKeypad7.addForeignNode(numericKeypad4, Direction.DOWN);
        numericKeypad8.addForeignNode(numericKeypad7, Direction.LEFT);
        numericKeypad8.addForeignNode(numericKeypad5, Direction.DOWN);
        numericKeypad8.addForeignNode(numericKeypad9, Direction.RIGHT);
        numericKeypad9.addForeignNode(numericKeypad8, Direction.LEFT);
        numericKeypad9.addForeignNode(numericKeypad6, Direction.DOWN);

        // Collect all numeric pad nodes
        numericNodes = new HashSet<>();
        numericNodes.add(numericKeypadA);
        numericNodes.add(numericKeypad0);
        numericNodes.add(numericKeypad1);
        numericNodes.add(numericKeypad2);
        numericNodes.add(numericKeypad3);
        numericNodes.add(numericKeypad4);
        numericNodes.add(numericKeypad5);
        numericNodes.add(numericKeypad6);
        numericNodes.add(numericKeypad7);
        numericNodes.add(numericKeypad8);
        numericNodes.add(numericKeypad9);

        // =================== Init directional pad nodes ===================
        
        directionalKeypadA = new Node('A');
        directionalKeypadUp = new Node('^');
        directionalKeypadDown = new Node('v');
        directionalKeypadLeft = new Node('<');
        directionalKeypadRight = new Node('>');

        directionalKeypadA.addForeignNode(directionalKeypadUp, Direction.LEFT);
        directionalKeypadA.addForeignNode(directionalKeypadRight, Direction.DOWN);
        directionalKeypadUp.addForeignNode(directionalKeypadA, Direction.RIGHT);
        directionalKeypadUp.addForeignNode(directionalKeypadDown, Direction.DOWN);
        directionalKeypadLeft.addForeignNode(directionalKeypadDown, Direction.RIGHT);
        directionalKeypadDown.addForeignNode(directionalKeypadLeft, Direction.LEFT);
        directionalKeypadDown.addForeignNode(directionalKeypadRight, Direction.RIGHT);
        directionalKeypadDown.addForeignNode(directionalKeypadUp, Direction.UP);
        directionalKeypadRight.addForeignNode(directionalKeypadA, Direction.UP);
        directionalKeypadRight.addForeignNode(directionalKeypadDown, Direction.LEFT);
        
        // // Collect all directional pad nodes
        directionalNodes = new HashSet<Node>();
        directionalNodes.add(directionalKeypadA);
        directionalNodes.add(directionalKeypadUp);
        directionalNodes.add(directionalKeypadDown);
        directionalNodes.add(directionalKeypadLeft);
        directionalNodes.add(directionalKeypadRight);

        // =================== Solve both parts ===================
        
        List<String> codes = Files.readAllLines(Path.of("input_day21.txt"));
        
        // Part 1
        System.out.println("Result part 1 : " + count(codes, 1));
        
        // Part 2 
        System.out.println("Result part 2 : " + count(codes, 24));

    }

	private static long count(List<String> codes, int depth) {
		long complexity;
		complexity = 0;
        for (String code : codes) {

            String bestMove = null;
        	List<String> allMoves = getAllMovesForNumericPad(code);
			bestMove = selectLowest(allMoves);
			
			long result = 0;
			for (int i=0;i<bestMove.length();i++) {
				char from = i == 0 ? 'A' : bestMove.charAt(i-1);
				String path = "" + from + bestMove.charAt(i);
				result += solve(path, depth);
			}
						
	        complexity += result * Integer.parseInt(code.substring(0, code.indexOf("A")));
        }
		return complexity;
	}
    
    // 029A: <vA<AA>>^AvAA<^A>A<v<A>>^AvA^A<vA>^A<v<A>^A>AAvA^A<v<A>A>^AAAvA<^A>A
    //       <vA <A A >>^A vA A <^A >A <v<A >>^A vA ^A <vA >^A <v<A >^A >A A vA ^A <v<A >A >^A A A vA <^A >A
    //         v  < <    A  > >   ^  A    <    A  >  A   v   A    <   ^  A A  >  A    <  v   A A A  >   ^  A
    //         v<<A >>^A <A >A vA <^A A >A <vA A A >^A
    //            <    A  ^  A  >   ^ ^  A   v v v   A
    //            <A ^A >^^A vvvA
    //             0  2    9    A

    private static Map<String, Long> cache = new HashMap<String, Long>();
    
	private static long solve(String path, int depth) {
		String key = path + ":" + depth;
		Long value = cache.get(key);
		if (value != null) { return value; }
			
		String moves = getShortestMovesForDirectionalPad(path);
		if (depth == 0) return moves.length();
		long result = 0;
		
		for (int i=0;i<moves.length();i++) {
			char from = i == 0 ? 'A' : moves.charAt(i-1);
			String newPath = "" + from + moves.charAt(i);
			result += solve(newPath, depth-1);
		}
		
		cache.put(key, result);
		return result;
	}

	private static String selectLowest(List<String> allMoves) {
		Thread.yield();
    	final int minLengthNumeric = allMoves.stream().mapToInt(s -> s.length()).min().orElseThrow();
		allMoves = allMoves.stream().filter(s -> s.length() == minLengthNumeric).collect(Collectors.toList());
		Map<String, Integer> turnsMap = allMoves.stream().collect(Collectors.toMap(s -> s, s -> countTurns(s)));
		int minTurns = turnsMap.values().stream().mapToInt(v -> v).min().orElseThrow();
		allMoves = turnsMap.keySet().stream().filter(moves -> turnsMap.get(moves).equals(minTurns)).collect(Collectors.toList());
		
		for (int i=0;i<minLengthNumeric;i++) {
			final int index = i;
			List<String> bestMoves = allMoves.stream().filter(s -> s.charAt(index) == '<').collect(Collectors.toList());
			if (bestMoves.isEmpty()) {
				bestMoves = allMoves.stream().filter(s -> s.charAt(index) == '^').collect(Collectors.toList());
			}
			if (bestMoves.isEmpty()) {
				bestMoves = allMoves.stream().filter(s -> s.charAt(index) == 'v').collect(Collectors.toList());
			}
			if (bestMoves.isEmpty()) {
				bestMoves = allMoves.stream().filter(s -> s.charAt(index) == '>').collect(Collectors.toList());
			}
			if (bestMoves.isEmpty()) {
				bestMoves = allMoves;
			}
			allMoves = bestMoves;
			if (allMoves.size() == 1) {
				return allMoves.get(0);
			}
		}
		return allMoves.get(0);
	}

	private static int countTurns(String s) {
		int result = 0;
		char c = s.charAt(0);
		for (int i=1;i<s.length();i++) {
			char nextC = s.charAt(i);
			if (nextC != 'A') {
				if (nextC != c && c != 'A') result++;
			}
			c = nextC;
		}
		return result;
	}

    // ========================================= Dijkstra (monochemin) ================================

    private static List<String> getAllMovesForNumericPad(String code) {
        Node currentNode = numericKeypadA;
        ArrayList<String> result = new ArrayList<String>();
        for (int i = 0; i < code.length(); i++) {
            char c = code.charAt(i);
            Node targetNode = getNumericPadNode(c);
            List<List<Node>> allPaths = getAllShortestPaths(currentNode, targetNode, numericNodes);
            List<String> newMoves = new ArrayList<String>();
            Node savedCurrentNode = currentNode;
            for (List<Node> list : allPaths) {
            	currentNode = savedCurrentNode;
            	String resultMoves = "";
            	for (int j = 0; j < list.size() - 1; j++) {
                    Node nextNode = list.get(j + 1);
                    Direction direction = currentNode.getDirection(nextNode);
                    resultMoves += direction.toChar();
                    currentNode = nextNode;
                }
            	resultMoves += 'A';
            	newMoves.add(resultMoves);
			}
            
            // merge
            if (result.isEmpty()) { result.addAll(newMoves); }
            else {
            	 ArrayList<String> newResult = new ArrayList<String>();
            	for(String prefix : result) {
            		for (String suffix : newMoves) {
            			newResult.add(prefix + suffix);
            		}
            	}
            	result = newResult;
            }
        }

        return result;
    }

    private static String getShortestMovesForDirectionalPad(String code) {

    	Node currentNode = getDirectionalPadNode(code.charAt(0));
        
    	String result = "";
        for (int i = 1; i < code.length(); i++) {
            char c = code.charAt(i);
            Node targetNode = getDirectionalPadNode(c);
            List<List<Node>> allPaths = getAllShortestPaths(currentNode, targetNode, directionalNodes);
            List<String> newMoves = new ArrayList<String>();
            Node savedCurrentNode = currentNode;
            for (List<Node> list : allPaths) {
            	currentNode = savedCurrentNode;
            	String resultMoves = "";
            	for (int j = 0; j < list.size() - 1; j++) {
                    Node nextNode = list.get(j + 1);
                    Direction direction = currentNode.getDirection(nextNode);
                    resultMoves += direction.toChar();
                    currentNode = nextNode;
                }
            	resultMoves += 'A';
            	newMoves.add(resultMoves);
			}
            result += selectLowest(newMoves);
        }

        return result;
    }
    
    private static Node getNumericPadNode(char c) {
        switch (c) {
            case 'A': return numericKeypadA;
            case '0': return numericKeypad0;
            case '1': return numericKeypad1;
            case '2': return numericKeypad2;
            case '3': return numericKeypad3;
            case '4': return numericKeypad4;
            case '5': return numericKeypad5;
            case '6': return numericKeypad6;
            case '7': return numericKeypad7;
            case '8': return numericKeypad8;
            case '9': return numericKeypad9;
        }
        throw new IllegalStateException();
    }

    private static Node getDirectionalPadNode(char c) {
        switch (c) {
            case 'A': return directionalKeypadA;
            case '<': return directionalKeypadLeft;
            case '>': return directionalKeypadRight;
            case '^': return directionalKeypadUp;
            case 'v': return directionalKeypadDown;
        }
        throw new IllegalStateException();
    }

    public static class Node {

        private char name;
        private Integer distance = Integer.MAX_VALUE;
        private List<Node> shortestPath = new LinkedList<>();

        Map<Node, Direction> adjacentNodesDirection = new HashMap<>();
        Map<Node, Integer> adjacentNodes = new HashMap<>();

        public Node(char c) {
            this.name = c;
        }

        public Direction getDirection(Node nextNode) {
            return adjacentNodesDirection.get(nextNode);
        }

        public char getName() {
            return name;
        }

        public List<Node> getShortestPath() {
            return shortestPath;
        }

        public void setShortestPath(List<Node> shortestPath) {
            this.shortestPath = shortestPath;
        }

        public void setDistance(Integer distance) {
            this.distance = distance;
        }

        public void addForeignNode(Node destination, Direction direction) {
            adjacentNodesDirection.put(destination, direction);
            adjacentNodes.put(destination, 1);
        }

        public Map<Node, Integer> getAdjacentNodes() {
            return adjacentNodes;
        }

        public int getDistance() {
            return distance;
        }

        @Override
        public String toString() {
            return "(" + name + ")";
        }
    }

    // ========================== NOUVELLE PARTIE : TOUS LES CHEMINS MINIMAUX =========================

    /**
     * Reset distance and shortest path of all the given nodes
     */
    private static void resetAllNodes(Set<Node> allNodes) {
        for (Node node : allNodes) {
            node.setDistance(Integer.MAX_VALUE);
            node.getShortestPath().clear();
        }
    }

    /**
     * BFS to build a list of parents leading to minimal distance.
     */
    private static Map<Node, List<Node>> buildParentsMapBFS(Node source, Set<Node> allNodes) {
        resetAllNodes(allNodes);

        source.setDistance(0);
        Map<Node, List<Node>> parentsMap = new HashMap<>();
        for (Node node : allNodes) {
            parentsMap.put(node, new LinkedList<>());
        }

        LinkedList<Node> queue = new LinkedList<>();
        queue.add(source);

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            int currentDist = current.getDistance();

            for (Entry<Node, Integer> adjacencyPair : current.getAdjacentNodes().entrySet()) {
                Node neighbor = adjacencyPair.getKey();
                int edgeWeight = adjacencyPair.getValue(); // = 1

                if (currentDist + edgeWeight < neighbor.getDistance()) {
                    neighbor.setDistance(currentDist + edgeWeight);
                    parentsMap.get(neighbor).clear();
                    parentsMap.get(neighbor).add(current);
                    queue.add(neighbor);
                } else if (currentDist + edgeWeight == neighbor.getDistance()) {
                    // Chemin équivalent -> on ajoute un parent
                    parentsMap.get(neighbor).add(current);
                }
            }
        }

        return parentsMap;
    }

    /**
     * Recursive backtracking to get all shortest paths from source to current.
     */
    private static void backtrackAllPaths(Node current, Node source,
                                          Map<Node, List<Node>> parentsMap,
                                          LinkedList<Node> path,
                                          List<List<Node>> allPaths) {

        if (current.equals(source)) {
            path.addFirst(current);
            allPaths.add(new ArrayList<>(path));
            path.removeFirst();
            return;
        }
        for (Node parent : parentsMap.get(current)) {
            path.addFirst(current);
            backtrackAllPaths(parent, source, parentsMap, path, allPaths);
            path.removeFirst();
        }
    }

    /**
     * @return all shortest paths from 'source' to 'targer'
     */
    public static List<List<Node>> getAllShortestPaths(Node source, Node target, Set<Node> allNodes) {
        Map<Node, List<Node>> parentsMap = buildParentsMapBFS(source, allNodes);

        List<List<Node>> allPaths = new ArrayList<>();
        // Si la cible est inaccessible, la distance restera Integer.MAX_VALUE
        if (target.getDistance() == Integer.MAX_VALUE) {
            return allPaths;
        }
        LinkedList<Node> path = new LinkedList<>();
        backtrackAllPaths(target, source, parentsMap, path, allPaths);

        return allPaths;
    }
}
