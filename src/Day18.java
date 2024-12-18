import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Day18 {

    static int width = 0;
    static int height = 0;
    static Node[][] map = null;

     public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Path.of("input_day18.txt"));

        // Construction de la carte
        width = 71;
        height = 71;
        int steps = 1024;
        map = new Node[width][height];

        for (int y=0;y<height;y++) {
        	for (int x=0;x<width;x++) {
        		map[x][y] = new Node('.', x, y);
        	}
        }

        for (int i=0;i<steps;i++) {
        	int x = Integer.parseInt(lines.get(i).split(",")[0]);
        	int y = Integer.parseInt(lines.get(i).split(",")[1]);
        	map[x][y] = new Node('#', x, y);
        }
        
        Node startNode = map[0][0];
		Node endNode = map[width-1][height-1];
		
		for (int x=0;x<width;x++) {
			for (int y=0;y<height;y++) {
				Node node = map[x][y];
				
				if (x < width - 1 && map[x+1][y].name != '#') node.addForeignNode(map[x+1][y], 1);
				if (x > 0 && map[x-1][y].name != '#') node.addForeignNode(map[x-1][y], 1);
				if (y < height - 1 && map[x][y+1].name != '#') node.addForeignNode(map[x][y+1], 1);
				if (y > 0 && map[x][y-1].name != '#') node.addForeignNode(map[x][y-1], 1);
			}
		}
		
		calculateShortestPathFromSource(startNode);
		
        // Part 1
        System.out.println("Result part 1 : " + endNode.getDistance());

        // Part 2 
        String result = "not found";
        for (int y=0;y<height;y++) {
        	for (int x=0;x<width;x++) {
        		map[x][y] = new Node('.', x, y);
        	}
        }
        
        // reset initial graph (without 'bytes')
        startNode = map[0][0];
        endNode = map[width-1][height-1];
        
        for (int y=0;y<height;y++) {
        	for (int x=0;x<width;x++) {
        		Node node = map[x][y];
 				
 				if (x < width - 1) node.addForeignNode(map[x+1][y], 1);
 				if (x > 0) node.addForeignNode(map[x-1][y], 1);
 				if (y < height - 1) node.addForeignNode(map[x][y+1], 1);
 				if (y > 0) node.addForeignNode(map[x][y-1], 1);
        	}
        }
        
        // remove edges for new incoming 'byte', until end is no more reachable
        for (int i=0;i<lines.size();i++) {
        	
         	int x = Integer.parseInt(lines.get(i).split(",")[0]);
         	int y = Integer.parseInt(lines.get(i).split(",")[1]);
         	Node newByteNode = map[x][y];
        	
        	 for (y=0;y<height;y++) {
        		 for (x=0;x<width;x++) {
             		map[x][y].distance = Integer.MAX_VALUE;
             		Map<Node, Integer> adjacentNodes = map[x][y].adjacentNodes;
             		map[x][y].adjacentNodes.remove(newByteNode);
             		Set<Node> keySet = adjacentNodes.keySet();
        			for (Node node : keySet) {
        				adjacentNodes.put(node, 1);
        			}
             	}
             }
        	
     		calculateShortestPathFromSource(startNode);
     		if (endNode.getDistance() == Integer.MAX_VALUE) {
     			result = lines.get(i);
     	        break;
     		}
        }

        System.out.println("Result part 2 : " + result);
     }

    // ========================================= Dijkstra ================================	

	public static class Graph {
	
	    private Set<Node> nodes = new HashSet<>();
	    
	    public void addNode(Node nodeA) {
	        nodes.add(nodeA);
	    }
	}
	
	public static class Node {
	    
	    private char name;
		private Integer distance = Integer.MAX_VALUE;
	    private List<Node> shortestPath = new LinkedList<>();
		Map<Node, Integer> adjacentNodes = new HashMap<>();
		
		private int x;
		private int y;
		
	    public Node(char c, int x, int y) {
	        this.name = c;
	        this.x = x;
	        this.y = y;
	    }

		public char getName() {
			return name;
		}

	    public List<Node> getShortestPath() {
			return shortestPath;
		}

		public void reset() {
			distance = Integer.MAX_VALUE;
			Set<Node> keySet = adjacentNodes.keySet();
			for (Node node : keySet) {
				adjacentNodes.put(node, 1);
			}
		}

		public void setShortestPath(List<Node> shortestPath) {
			this.shortestPath = shortestPath;
		}

	    public void setDistance(Integer distance) {
			this.distance = distance;
		}

	    public void addForeignNode(Node destination, int distance) {
	        adjacentNodes.put(destination, distance);
	    }
	 
		public Map<Node, Integer> getAdjacentNodes() {
			return adjacentNodes;
		}

		public int getDistance() {
			return distance;
		}
		
		@Override
		public String toString() {
			return "("+x+","+y+")";
		}
	}
	
	public static void calculateShortestPathFromSource(Node source) {
	    
		source.setDistance(0);
	
	    Set<Node> settledNodes = new HashSet<>();
	    Set<Node> unsettledNodes = new HashSet<>();
	
	    unsettledNodes.add(source);
	
	    while (unsettledNodes.size() != 0) {
	        Node currentNode = getLowestDistanceNode(unsettledNodes);
	        unsettledNodes.remove(currentNode);
	        for (Entry<Node, Integer> adjacencyPair : currentNode.getAdjacentNodes().entrySet()) {
	            Node adjacentNode = adjacencyPair.getKey();
	            Integer edgeWeight = adjacencyPair.getValue();
	            if (!settledNodes.contains(adjacentNode)) {
	                calculateMinimumDistance(adjacentNode, edgeWeight, currentNode);
	                unsettledNodes.add(adjacentNode);
	            }
	        }
	        settledNodes.add(currentNode);
	    }
	}
	
	private static Node getLowestDistanceNode(Set<Node> unsettledNodes) {
		return unsettledNodes.stream().min((n1, n2) -> {
			return Integer.compare(n1.getDistance(), n2.getDistance());
		}).orElse(null);
	}
	
	public static void calculateMinimumDistance(Node evaluationNode,  Integer edgeWeigh, Node sourceNode) {
	    Integer sourceDistance = sourceNode.getDistance();
	    if (sourceDistance + edgeWeigh < evaluationNode.getDistance()) {
	        evaluationNode.setDistance(sourceDistance + edgeWeigh);
	        LinkedList<Node> shortestPath = new LinkedList<>(sourceNode.getShortestPath());
	        shortestPath.add(sourceNode);
	        evaluationNode.setShortestPath(shortestPath);
	    }
	}

}
