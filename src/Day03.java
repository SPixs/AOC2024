import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day03 {

	public static void main(String[] args) throws IOException {
		String line = Files.readString(Path.of("input_day03.txt"));

		String regex = "mul\\((\\d{1,3}),(\\d{1,3})\\)";
		Pattern pattern = Pattern.compile(regex);

		int result = processLine(pattern, line);
		System.out.println("Result part 1 : " + result);

		// Part 2
		result = 0;
		while (line.length() > 0) {
			int endIndex = line.indexOf("don't()");
			if (endIndex < 0) endIndex = line.length();
			String toProcess = line.substring(0, endIndex);
			result += processLine(pattern, toProcess);
			line = (endIndex == line.length()) ? "" : line.substring(endIndex+7);
			line = line.indexOf("do()") < 0 ? "" : line.substring(line.indexOf("do()"));
		}

		System.out.println("Result part 2 : " + result);
	}

	private static int processLine(Pattern pattern, String line) {
		int value = 0;
		Matcher matcher = pattern.matcher(line);
		while (matcher.find()) {
			String x = matcher.group(1);
			String y = matcher.group(2);
			value += Integer.parseInt(x) * Integer.parseInt(y);
		}
		return value;
	}
}
