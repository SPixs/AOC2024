import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Day11 {

	public static void main(String[] args) throws IOException {
		List<String> lines = Files.readAllLines(Path.of("input_day11.txt"));

		List<Long> input = Arrays.stream(lines.get(0).split(" ")).map(n -> Long.valueOf(n))
				.collect(Collectors.toList());

		// Part 1
		long result = computeResult(input, 25);
		System.out.println("Result part 1 : " + result);

		// Part 2
		result = computeResult(input, 75);
		System.out.println("Result part 2 : " + result);
	}

	private static long computeResult(List<Long> input, int iterations) {
		Map<Long, Long> occurrences = countOccurrences(input);
		for (int i = 0; i < iterations; i++) {
			occurrences = process(occurrences);
		}
		return occurrences.values().stream().mapToLong(Long::longValue).sum();
	}

	// Turn a list of number into a frequency map
	private static Map<Long, Long> countOccurrences(List<Long> input) {
		return input.stream().collect(Collectors.groupingBy(n -> n, Collectors.counting()));
	}

	// One step of simulation. Process a list of numbers (given as an frequency map)
	private static Map<Long, Long> process(Map<Long, Long> occurrences) {
		Map<Long, Long> result = new HashMap<Long, Long>();
		for (Long number : occurrences.keySet()) {
			List<Long> process = process(number);
			for (Long value : process) {
				long count = result.getOrDefault(value, 0L);
				result.put(value, count+occurrences.get(number));
			}
		}
		return result;
	}
	
	// Apply rules to a single number
	private static List<Long> process(long number) {
		if (number == 0)
			return Collections.singletonList(1L);

		String numStr = String.valueOf(Math.abs(number));
		int len = numStr.length();

		if (len % 2 == 0) {
			long n1 = Long.parseLong(numStr.substring(0, len / 2));
			long n2 = Long.parseLong(numStr.substring(len / 2));
			return Arrays.asList(n1, n2);
		}

		return Collections.singletonList(number * 2024);
	}

}
