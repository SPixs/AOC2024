import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day05 {

	public static void main(String[] args) throws IOException {
		List<String> lines = Files.readAllLines(Path.of("input_day05.txt"));

		Map<Integer, Set<Integer>> order = new HashMap<Integer, Set<Integer>>();
		List<List<Integer>> updates = new ArrayList<List<Integer>>();
		for (String line : lines) {
			if (line.contains("|")) {
				String[] split = line.split("\\|");
				Set<Integer> pagesAfter = order.getOrDefault(Integer.parseInt(split[0]), new HashSet<Integer>());
				pagesAfter.add(Integer.parseInt(split[1]));
				order.put(Integer.parseInt(split[0]), pagesAfter);
			}
			else if (line.contains(",")) {
				updates.add(Arrays.stream(line.split(",")).map(i -> Integer.valueOf(i)).collect(Collectors.toList()));
			}
		}
		
		int result = 0;
		for(List<Integer> update : updates) {
			List<Integer> sorted = getSorted(order, update);
			if (sorted.equals(update)) {
				result += update.get(update.size()/2);
			}
		}
		
		// Part 1
		System.out.println("Result part 1 : " + result);

		// Part 2
		result = 0;
		for(List<Integer> update : updates) {
			List<Integer> sorted = getSorted(order, update);
			if (!sorted.equals(update)) {
				result += sorted.get(sorted.size()/2);
			}
		}
		
		System.out.println("Result part 2 : " + result);
	}

	private static List<Integer> getSorted(Map<Integer, Set<Integer>> order, List<Integer> update) {
		List<Integer> sorted = new ArrayList<Integer>(update);
		Collections.sort(sorted, new Comparator<Integer>() {
			public int compare(Integer o1, Integer o2) {
				if (order.containsKey(o1) && order.get(o1).contains(o2)) return -1;
				else if (order.containsKey(o2) && order.get(o2).contains(o1)) return 1;
				return 0;
			}
		});
		return sorted;
	}
}
