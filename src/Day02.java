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

public class Day02 {

	public static void main(String[] args) throws IOException {
		List<String> lines = Files.readAllLines(Path.of("input_day02.txt"));

		// Part 1
		List<Report> reports = new ArrayList<Report>();
		for (String line : lines) {
			String[] levelsStrings = line.split("\\s+");
			Report report = new Report(Arrays.stream(levelsStrings).map(l -> Integer.parseInt(l)).collect(Collectors.toList()));
			reports.add(report);
		}
		
		long safeCount = reports.stream().filter(r -> r.isSafe(false)).count();
		System.out.println("Result part 1 : " + safeCount);
		
		// Part 2
		safeCount = reports.stream().filter(r -> r.isSafe(true)).count();
		System.out.println("Result part 2 : " + safeCount);
	}
	
	private static class Report {

		private boolean valid;
		private boolean increasing;
		private List<Integer> levels;

		public Report(List<Integer> levels) {
			this.levels = levels;
			valid = true;
			if (levels.get(0) == levels.get(1)) { valid = false; return; }
			increasing = levels.get(0) < levels.get(1);
			if (Math.abs(levels.get(1) - levels.get(0)) > 3) { valid = false; return; }

			int index = 1;
			while (index < levels.size()-1) {
				int delta = levels.get(index+1) - levels.get(index);
				if ((Math.abs(delta) > 3) || (Math.signum(delta) * (increasing ? 1 : -1) != 1)) { valid = false; return; }
				index++;
			}
		}

		public boolean isSafe(boolean part2) {
			if (valid) return true;
			if (part2) {
				for (int i=0;i<levels.size();i++) {
					List<Integer> newLevels = new ArrayList<Integer>(levels);
					newLevels.remove(i);
					Report newReport = new Report(newLevels);
					if (newReport.isSafe(false)) return true;
				}
			}
			return false;
		}
		
	}

}
