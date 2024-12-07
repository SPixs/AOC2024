import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day07 {

	public static class Equation {

		private long result;
		private long[] values;

		public Equation(long result, long[] values) {
			this.result = result;
			this.values = values;
		}

		public long getResult() {
			return result;
		}

		public boolean canBeSolved(boolean part2) {
			long accumulator = values[0];
			return part2 ? recursePart2(result, accumulator, 1) : recurse(result, accumulator, 1);
		}

		private boolean recurse(long result, long accumulator, int index) {
			if (index >= values.length) return result == accumulator;
			return recurse(result, accumulator + values[index], index+1) || recurse(result, accumulator * values[index], index+1);
		}

		private boolean recursePart2(long result, long accumulator, int index) {
			if (index >= values.length) return result == accumulator;
			boolean valid = recursePart2(result, accumulator + values[index], index+1) || recursePart2(result, accumulator * values[index], index+1) ;
			String concatenate = String.valueOf(accumulator) + values[index];
			valid |= recursePart2(result, Long.valueOf(concatenate), index+1);
			return valid;
		}
	}

	public static void main(String[] args) throws IOException {
		List<String> lines = Files.readAllLines(Path.of("input_day07.txt"));

		List<Equation> equations = new ArrayList<Equation>();
		for (String line : lines) {
			String[] split = line.split(":");
			long result = Long.parseLong(split[0]);
			long[] values = Arrays.stream(split[1].trim().split(" ")).mapToLong(v -> Long.valueOf(v)).toArray();
			equations.add(new Equation(result, values));
		}
		
		// Part 1
		long result = equations.stream().filter(e -> e.canBeSolved(false)).mapToLong(e -> e.getResult()).sum();
		System.out.println("Result part 1 : " + result);

		// Part 2
		result = equations.stream().filter(e -> e.canBeSolved(true)).mapToLong(e -> e.getResult()).sum();
		System.out.println("Result part 2 : " + result);
	}
}
