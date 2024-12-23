import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.Set;

public class Day23 {

     public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Path.of("input_day23.txt"));

        Map<String, Node> nodeByName = new HashMap<String, Node>();
        
        for (String line : lines) {
			String[] split = line.split("-");
			if (!nodeByName.containsKey(split[0])) {
				nodeByName.put(split[0], new Node(split[0]));
			}
			if (!nodeByName.containsKey(split[1])) {
				nodeByName.put(split[1], new Node(split[1]));
			}
			nodeByName.get(split[0]).addForeignNode(nodeByName.get(split[1]));
			nodeByName.get(split[1]).addForeignNode(nodeByName.get(split[0]));
		}
        Set<Node> allNodes = new HashSet<Node>(nodeByName.values());
        
        Set<Set<Node>> allGroups = new HashSet<Set<Node>>();
        for (Node node : allNodes) {
        	List<Node> keySet = new ArrayList<Node>(node.getAdjacentNodes().keySet());
        	for (int i=0;i<keySet.size()-1;i++) {
            	for (int j=i+1;j<keySet.size();j++) {
	        		Set<Node> group = new HashSet<Day23.Node>();
	        		group.add(node);
	        		if (keySet.get(i).adjacentNodes.containsKey(keySet.get(j))) {
		        		group.add(keySet.get(i));
		        		group.add(keySet.get(j));
		        		allGroups.add(group);
	        		}
            	}
        	}
        }
        
        long result = allGroups.stream().filter(n -> containsT(n)).count();

        // Part 1
        System.out.println("Result part 1 : " + result);

        // Part 2 
        Set<Node> largestGraph = findLargestGraph(allNodes);
        List<String> names = largestGraph.stream().map(n -> n.getName()).sorted().collect(Collectors.toList());
        String password = "";
        for (int i=0;i<names.size();i++) {
        	if (i>0) password += ",";
        	password += names.get(i);
        }
        System.out.println("Result part 2 : " + password);
     }

    private static boolean containsT(Set<Node> group) {
    	for (Node foreignNode : group) {
        	if (foreignNode.getName().startsWith("t")) return true;
    		
    	}
    	return false;
	}

    private static Set<Node> findLargestGraph(Set<Node> allNodes) {
        Set<Node> largestGraph = new HashSet<>();
        bronKerbosch(new HashSet<>(), new HashSet<>(allNodes), new HashSet<>(), largestGraph);
        return largestGraph;
    }

    /**
     * @param r set of nodes in current subgraph 
     * @param p set of nodes that could be added to max subgraph
     * @param x set of nodes already processed or excluded
     * @param largestGraph the largest 'all-connected nodes' subgraph
     */
    private static void bronKerbosch(Set<Node> r, Set<Node> p, Set<Node> x, Set<Node> largestGraph) {
    	// If P (candidates) and X (excluded) are empty, then R is a largest graph
        if (p.isEmpty() && x.isEmpty()) {
            if (r.size() > largestGraph.size()) {
                largestGraph.clear();
                largestGraph.addAll(r);
            }
            return;
        }
        // For each node 'v' in 'p'
        Set<Node> pCopy = new HashSet<>(p);
        for (Node v : pCopy) {
        	// Add 'v' to new 'r'
            Set<Node> newR = new HashSet<>(r);
            newR.add(v);
            // new 'p' = 'p' inter (neighbors('v')) 
            Set<Node> newP = new HashSet<>(p);
            newP.retainAll(v.getAdjacentNodes().keySet());
            // new 'x' = 'x' inter (neighbors('v')) 
            Set<Node> newX = new HashSet<>(x);
            newX.retainAll(v.getAdjacentNodes().keySet());
            bronKerbosch(newR, newP, newX, largestGraph);
            // after recursive call, remove 'v' from 'p', add 'v' to 'x'
            p.remove(v);
            x.add(v);
        }
    }

    public static class Node {
	    
	    private String name;
		Map<Node, Integer> adjacentNodes = new HashMap<>();
		
	    public Node(String name) {
	        this.name = name;
	    }

		public String getName() {
			return name;
		}

	    public void addForeignNode(Node destination) {
	        adjacentNodes.put(destination, 1);
	    }
	 
		public Map<Node, Integer> getAdjacentNodes() {
			return adjacentNodes;
		}

		@Override
		public String toString() {
			return "("+name+")";
		}
	}
}
