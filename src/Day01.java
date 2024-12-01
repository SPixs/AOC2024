import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day01 {

	public static void main(String[] args) throws IOException {
		List<String> lines = Files.readAllLines(Path.of("input_day01.txt"));

		// Part 1
		List<Integer> l1 = new ArrayList<Integer>();
		List<Integer> l2 = new ArrayList<Integer>();
		for (String line : lines) {
			l1.add(Integer.parseInt(line.split("\\s+")[0]));
			l2.add(Integer.parseInt(line.split("\\s+")[1]));
		}
		Collections.sort(l1);
		Collections.sort(l2);
		long sum = 0;
		for (int i=0;i<l1.size();i++) {
			sum += Math.abs(l1.get(i) - l2.get(i));
		}
		System.out.println("Result part 1 : " + sum);
		
		// Part 2
		Map<Integer, Long> map = l2.stream().collect(Collectors.groupingBy(Integer::intValue, Collectors.counting()));
		long result = l1.stream().mapToLong(l -> l*map.getOrDefault(l, 0L)).sum();
		System.out.println("Result part 2 : " + result);
	}

}
