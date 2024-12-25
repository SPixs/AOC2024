import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
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

public class Day25 {

     public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Path.of("input_day25.txt"));

        List<List<Integer>> keys = new ArrayList<>();
        List<List<Integer>> locks = new ArrayList<>();
        
        boolean parsing = false;
        boolean isKey = false;
        List<Integer> current = new ArrayList<>(Arrays.asList(0,0,0,0,0));
        int linesCount = 0;
        for (String line : lines) {
			if (line.isBlank()) {
				linesCount = 0;
				if (isKey) keys.add(current);
				else { locks.add(current); }
				current = new ArrayList<>(Arrays.asList(0,0,0,0,0));
				parsing = false;
			}
			else {
				if (!parsing) {
					isKey = !line.equals("#####");
					parsing = true;
				}
				else {
					if (linesCount < 5) {
						for (int i=0;i<5;i++) {
							if (line.charAt(i) == '#') current.set(i, current.get(i)+1);
						}
						Thread.yield();
					}
					linesCount++;
				}
			}
		}
        if (parsing) {
        	if (isKey) keys.add(current);
			else { locks.add(current); }
        }

        long result = 0;
        for (List<Integer> key : keys) {
        	for (List<Integer> lock : locks) {
        		result += doesFit(key, lock) ? 1 : 0;
        	}
        }

        // Part 1
        System.out.println("Result part 1 : " + result); 
     }

	private static boolean doesFit(List<Integer> key, List<Integer> lock) {
		for (int i=0;i<5;i++) {
			if (key.get(i) + lock.get(i) > 5) return false;
		}
		return true;
	}

    
}
