import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day22 {

	public static void main(String[] args) throws IOException {
		List<String> secrets = Files.readAllLines(Path.of("input_day22.txt"));
		
		long result = 0;
		for (String secret : secrets) {
			long sum = getSecret(Long.parseLong(secret), 2000);
			result += sum;
		}
		
		// Part 1
		System.out.println("Result part 1 : " + result);
		
		// Part 2
		long[] initSecret = new long[secrets.size()];
		for (int i=0;i<initSecret.length;i++) {
			initSecret[i] = Long.parseLong(secrets.get(i));
		}
		
		Map<List<Integer>, Integer> totalBananaForSequence = new HashMap<List<Integer>, Integer>();
		for (long secret : initSecret) {
			Map<List<Integer>, Integer> bananaForSequence = computeBanana(secret);
			bananaForSequence.forEach((key, value) -> totalBananaForSequence.merge(key, value, Integer::sum));
		}

		result = totalBananaForSequence.values().stream().mapToInt(l -> l).max().orElseThrow();
		
		System.out.println("Result part 2 : " + result);
	}
	
	 private static Map<List<Integer>, Integer> computeBanana(long secret) {
		Map<List<Integer>, Integer> result = new HashMap<List<Integer>, Integer>();
		List<Integer> sequence = new ArrayList<>();
		int price = (int) (secret % 10);
		for (int i=0;i<2000;i++) {
			long newSecret = getNextSecret(secret);
			int newPrice = (int) (newSecret  % 10);
			int delta = newPrice - price;
			sequence.add(delta);
			if (sequence.size() == 4) {
				result.putIfAbsent(new ArrayList<Integer>(sequence), newPrice); // Sell only the first time sequence is encountered
				sequence.remove(0);
			}
			secret = newSecret;
			price = newPrice;
		}
		return result;
	}

	private static long getSecret(long secret, int iterations) {
		for (int i=0;i<iterations;i++) {
			secret = getNextSecret(secret);
		}
		return secret;
	}
	
	private static long getNextSecret(long secret) {
		long value = (secret ^ (secret << 6)) % 16777216l;
		value = (value ^ (value >> 5)) % 16777216l;
		value = (value ^ (value << 11)) % 16777216l;
		return value;
	}

}
