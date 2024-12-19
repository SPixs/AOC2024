import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Day19 {

	public static void main(String[] args) throws IOException {
		List<String> lines = Files.readAllLines(Path.of("input_day19.txt"));

		List<String> towels = Arrays.stream(lines.get(0).split(",")).map(s -> s.trim()).collect(Collectors.toList());
		List<String> onsens = new ArrayList<String>();
		for (int i=2;i<lines.size();i++) {
			onsens.add(lines.get(i).trim());
		}
		
		HashMap<String, Boolean> designCache = new HashMap<String, Boolean>();
		long result = onsens.stream().filter(onsen -> canBeDesigned(onsen, towels, designCache)).count();

		// Part 1
		System.out.println("Result part 1 : " + result);

		// Part 2
		HashMap<String, Long> countCache = new HashMap<String, Long>();
		result = onsens.stream().mapToLong(onsen -> { return countDesignsWithCache(onsen, towels, countCache); }).sum();
		System.out.println("Result part 2 : " + result);
	}
	
	private static long countDesignsWithCache(String onsen, List<String> towels, Map<String, Long> countCache) {
		if (onsen.isEmpty()) return 1L;
		if (countCache.containsKey(onsen)) {
			return countCache.get(onsen);
		}
		long count = 0;
		for (String towel : towels) {
			if (onsen.startsWith(towel)) {
				long tmpCount = countDesignsWithCache(onsen.substring(towel.length()), towels, countCache);
				if (tmpCount > 0) {
					count += tmpCount;
				}
			}
		}
		countCache.put(onsen, count);
		return count;
	}

	private static boolean canBeDesigned(String onsen, List<String> towels, HashMap<String, Boolean> designCache) {
		if (designCache.containsKey(onsen)) return designCache.get(onsen);
		if (onsen.isEmpty())  
			return true;
		for (String towel : towels) {
			if (onsen.startsWith(towel) && canBeDesigned(onsen.substring(towel.length()), towels, designCache)) {
				designCache.put(onsen, true);
				return true;
			}
		}
		designCache.put(onsen, false);
		return false;
	}
}
