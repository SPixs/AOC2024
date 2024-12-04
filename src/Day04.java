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

public class Day04 {

	public static void main(String[] args) throws IOException {
		List<String> lines = Files.readAllLines(Path.of("input_day04.txt"));

		// BUild map
		int width = lines.get(0).length();
		int height = lines.size();

		int y = 0;
		char[][] map = new char[width][height];
		for (String line : lines) {
			for (int x=0;x<line.length();x++) {
				map[x][y] = line.charAt(x);
			}
			y++;
		}
		
		// Part 1
		long result = computePart1(map);
		System.out.println("Result part 1 : " + result);

		// Part 2
		result = computePart2(map);
		System.out.println("Result part 2 : " + result);
	}

	private static long computePart1(char[][] map) {
		int result = 0;
		int[][] direction = new int[][] { {0, -1}, { 1, -1}, {1, 0}, { 1, 1}, {0, 1}, { -1, 1}, { -1, 0}, { -1, -1}};
		for (int x=0;x<map.length;x++) {
			for (int y=0;y<map[0].length;y++) {
				for (int[] tmpDirection : direction) {
					result += check(map, x, y, tmpDirection) ? 1 : 0;
				}
			}
		}
		return result;
	}

	private static char[] word = "XMAS".toCharArray();

	private static boolean check(char[][] map, int x, int y, int[] tmpDirection) {
		for (int i=0;i<word.length;i++) {
			if (x >= 0 && x < map.length && y >= 0 && y < map[0].length) {
				char c = map[x][y];
				if (c != word[i]) return false;
				x += tmpDirection[0];
				y += tmpDirection[1];
			}
			else {
				return false;
			}
		}
		return true;
	}

	private static long computePart2(char[][] map) {
		int result = 0;
		int[][][] direction = new int[][][] { 
			{{1, -1, (int)'S'}, {1, 1, (int)'S'}, {-1, 1, (int)'M'}, {-1, -1, (int)'M'}, {0, 0, (int)'A'}}, 
			{{1, -1, (int)'M'}, {1, 1, (int)'S'}, {-1, 1, (int)'S'}, {-1, -1, (int)'M'}, {0, 0, (int)'A'}}, 
			{{1, -1, (int)'M'}, {1, 1, (int)'M'}, {-1, 1, (int)'S'}, {-1, -1, (int)'S'}, {0, 0, (int)'A'}}, 
			{{1, -1, (int)'S'}, {1, 1, (int)'M'}, {-1, 1, (int)'M'}, {-1, -1, (int)'S'}, {0, 0, (int)'A'}}
		};
		for (int x=0;x<map.length;x++) {
			for (int y=0;y<map[0].length;y++) {
				for (int[][] pattern : direction) {
					boolean found = true;
					for (int[] tmpDirectionWithLetter : pattern) {
						found &= checkPart2(map, x, y, tmpDirectionWithLetter);
					}
					if (found) result++;
				}
			}
		}
		return result;
	}

	private static boolean checkPart2(char[][] map, int x, int y, int[] tmpDirectionWithLetter) {
		x += tmpDirectionWithLetter[0];
		y += tmpDirectionWithLetter[1];
		if (x >= 0 && x < map.length && y >= 0 && y < map[0].length) {
			return map[x][y] == tmpDirectionWithLetter[2]; 
		}
		else {
			return false;
		}
	}
}
