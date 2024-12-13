import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Day13 {

	public static void main(String[] args) throws IOException {
		List<String> lines = Files.readAllLines(Path.of("input_day13.txt"));
		
        List<ClawConfiguration> configurations = parse(lines);
        
       
		// Part 1
		long result = 0;
		for (ClawConfiguration configuration : configurations) {
			result += computeTokens(configuration);
		}
		System.out.println("Result part 1 : " + result); // 29147 (false)

		for (ClawConfiguration configuration : configurations) {
			configuration.prizeX += 10000000000000L;
			configuration.prizeY += 10000000000000L;
		}
		
		// Part 2
		result = 0;
		for (ClawConfiguration configuration : configurations) {
			result += computeTokensPart2(configuration);
		}
		
		System.out.println("Result part 2 : " + result); 
	}
	
	private static long computeTokensPart2(ClawConfiguration configuration) {
		
		long[] v1 = new long[] { configuration.buttonAX, configuration.buttonAY };
		long[] v2 = new long[] { configuration.buttonBX, configuration.buttonBY };
		long[] v3 = new long[] { configuration.prizeX, configuration.prizeY };
		long[] solveForAB = solve(v1, v2, v3);
		if (solveForAB == null) return 0;
		
		long a = solveForAB[0];
		long b = solveForAB[1];
		long finalX = configuration.buttonAX * a + configuration.buttonBX * b;
		long finalY = configuration.buttonAY * a + configuration.buttonBY * b;
		
		long dx = finalX - configuration.prizeX;
		long dy = finalY - configuration.prizeY;
		
		if (dx != 0 || dy != 0) { 
			throw new IllegalStateException();
		}
		
		return (long) (a*3+b);
	}
	
	public static long[] solve(long[] V1, long[] V2, long[] V3) {

        long determinant = V1[0] * V2[1] - V1[1] * V2[0];

        if (determinant == 0) {
        	throw new IllegalStateException();
        } else {
            // Use Cramer
        	long a = (V3[0] * V2[1] - V3[1] * V2[0]);
        	if (a % determinant != 0) return null;
        	a /= determinant;

        	long b = (V1[0] * V3[1] - V1[1] * V3[0]);
        	if (b % determinant != 0) return null;
        	b /= determinant;

            return new long[]{a, b};
        }
    }
	
	private static long computeTokens(ClawConfiguration configuration) {
		int minToken = Integer.MAX_VALUE;
		for (int a=0;a<100;a++) {
			for (int b=0;b<100;b++) {
				int finalX = configuration.buttonAX * a + configuration.buttonBX * b;
				int finalY = configuration.buttonAY * a + configuration.buttonBY * b;
				int cost = 3 * a + b;
				if (finalX == configuration.prizeX && finalY == configuration.prizeY) {
					minToken = Math.min(minToken, cost);
				}
			}
		}
		return minToken == Integer.MAX_VALUE ? 0 : minToken;
	}

	private static class ClawConfiguration {
	    private int buttonAX, buttonAY, buttonBX, buttonBY;
	    private long prizeX, prizeY;

	    // Constructeur pour initialiser les valeurs
	    public ClawConfiguration(int buttonAX, int buttonAY, int buttonBX, int buttonBY, int prizeX, int prizeY) {
	        this.buttonAX = buttonAX;
	        this.buttonAY = buttonAY;
	        this.buttonBX = buttonBX;
	        this.buttonBY = buttonBY;
	        this.prizeX = prizeX;
	        this.prizeY = prizeY;
	    }
	}

	public static List<ClawConfiguration> parse(List<String> lines) throws IOException {

		 int buttonAX = 0;
         int buttonAY = 0;

         int buttonBX = 0;
         int buttonBY = 0;

         int prizeX = 0;
         int prizeY = 0;
         
		List<ClawConfiguration> configurations = new ArrayList<>();
		for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) {
            	configurations.add(new ClawConfiguration(buttonAX, buttonAY, buttonBX, buttonBY, prizeX, prizeY));
                continue;
            }

            // Récupérer les valeurs pour Button A
            if (line.startsWith("Button A")) {
                buttonAX = Integer.parseInt(line.split("X\\+")[1].split(",")[0].trim());
                buttonAY = Integer.parseInt(line.split("Y\\+")[1].split(",")[0].trim());
            }
            
            if (line.startsWith("Button B")) {
                buttonBX = Integer.parseInt(line.split("X\\+")[1].split(",")[0].trim());
                buttonBY = Integer.parseInt(line.split("Y\\+")[1].split(",")[0].trim());
            }
           
            if (line.startsWith("Prize")) {
                prizeX = Integer.parseInt(line.split("X=")[1].split(",")[0].trim());
                prizeY = Integer.parseInt(line.split("Y=")[1].trim());
            }
        }
		configurations.add(new ClawConfiguration(buttonAX, buttonAY, buttonBX, buttonBY, prizeX, prizeY));
		
        return configurations;
    }

}
