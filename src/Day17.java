import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Day17 {

	public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Path.of("input_day17.txt"));

        Cpu cpu = new Cpu();
        
        String ramAsText = null;
        int[] ram = null;
        
        for (String line : lines) {
			if (line.contains("Register")) {
				String[] split = line.split(":");
				String register = split[0].split(" " )[1];
				long value = Long.parseLong(split[1].trim());
				switch (register) {
					case "A": cpu.setA(value); break;
					case "B": cpu.setB(value); break;
					case "C": cpu.setC(value); break;
					default: throw new IllegalStateException();
				}
			}
			if (line.contains("Program")) {
				ramAsText = line.split(":")[1].trim();
				String[] ramText = ramAsText.split(",");
				ram = new int[ramText.length];
				for (int i=0;i<ram.length;i++) { ram[i] = Integer.parseInt(ramText[i]); }
			}
		}
        
        long savedB = cpu.getB();
        long savedC = cpu.getC();
        
        StringBuffer output = new StringBuffer();
        boolean halt = false;
        while (!halt) {
        	halt = cpu.step(ram, output);
        }
        
        // Part 1
        System.out.println("Result part 1 : " + output);

		// Part 2
        long startTime = System.nanoTime();
        long a = 0;
        if (!getOutput(cpu, ram, savedB, savedC, a).equals(ramAsText)) {
        	a = 1;

        	while (true) {
				String outputProgram = getOutput(cpu, ram, savedB, savedC, a);
				if (outputProgram.equals(ramAsText)) {
					break;
				}
				if (outputProgram.length() < ramAsText.length() - 4) {
					a *= 2;
				}
				else if (outputProgram.length() < ramAsText.length() - 2) {
					a *= 1.0001;
				}
				else if (outputProgram.length() < ramAsText.length()) {
					a *= 1.00001;
				}
				else if (outputProgram.length() == ramAsText.length()) {
					if (!tailEqual(ramAsText, outputProgram, 2)) {
						a += 1000000000000l;
					}
					else if (!tailEqual(ramAsText, outputProgram, 4)) {
						a += 100000000000l;
					}
					else if (!tailEqual(ramAsText, outputProgram, 6)) {
						a += 10000000000l;
					}
					else if (!tailEqual(ramAsText, outputProgram, 8)) {
						a += 1000000000l;
					}
					else if (!tailEqual(ramAsText, outputProgram, 10)) {
						a += 100000000l;
					}
					else if (!tailEqual(ramAsText, outputProgram, 14)) {
						a += 10000000l;
					}
					else if (!tailEqual(ramAsText, outputProgram, 24)) {
						a += 1000l;
					}
					else if (!tailEqual(ramAsText, outputProgram, 26)) {
						a += 10l;
					}
					else if (!tailEqual(ramAsText, outputProgram, 28)) {
						a += 5;
					}
					else {
						a++;
					}
				}
				else if (outputProgram.length() > ramAsText.length()) {
					throw new IllegalStateException();
				}
			}
        }
        
        long endTime = System.nanoTime() - startTime;
        
		// 190384609508367
		System.out.println("Result part 2 : " + a + " in " + (endTime / 1e6) + "ms"); 
    }

	private static boolean tailEqual(String ramAsText, String outputProgram, int length) {
		return outputProgram.substring(outputProgram.length()-length).equals(ramAsText.substring(ramAsText.length()-length));
	}

	private static String getOutput(Cpu cpu, int[] ram, long savedB, long savedC, long a) {
		StringBuffer output;
		boolean halt;
		cpu.reset();
		cpu.setB(savedB);
		cpu.setC(savedC);
		cpu.setA(a);

		output = new StringBuffer();
		halt = false;
		while (!halt) {
			halt = cpu.step(ram, output);
		}
		String outputProgram = output.toString();
		return outputProgram;
	}
	
    public static class Cpu {

    	private int pc = 0;
    	
		private long c;
		private long b;
		private long a;
		
		private boolean firstOutput = true;

		public void setA(long value) {
			this.a = value;
		}

		public long getC() {
			return c;
		}

		public long getB() {
			return b;
		}

		public void reset() {
			pc = 0;
			firstOutput = true;
		}

		public boolean step(int[] ram, StringBuffer output) {
			if (pc >= ram.length) return true;
			
			int opcode = ram[pc];
			int operand = ram[pc+1];
			processInstruction(opcode, operand, output);
			return false;
		}

		private long getComoboValue(int operand) {
			switch (operand) {
				case 0:
				case 1:
				case 2:
				case 3: return operand;
				case 4: return a;
				case 5: return b;
				case 6: return c;
				case 7: 
				default:
					throw new IllegalArgumentException();
			}
		}
		
		private void processInstruction(int opcode, int operand, StringBuffer output) {
			switch (opcode) {
				case 0: doAdv(operand); break; 
				case 1: doBxl(operand); break; 
				case 2: doBst(operand); break; 
				case 3: doJnz(operand); break; 
				case 4: doBxc(operand); break; 
				case 5: doOut(operand, output); break; 
				case 6: doBdv(operand); break; 
				case 7: doCdv(operand); break; 
			}
		}

		private void doAdv(int operand) {
			long numerator = a;
			long denumerator = (long)Math.pow(2, getComoboValue(operand));
			a = numerator / denumerator;		
			pc += 2;
		}
		
		private void doBxl(int operand) {
			b = b ^ operand;
			pc += 2;
		}

		private void doBst(int operand) {
			b = getComoboValue(operand) % 8;
			pc += 2;
		}

		private void doJnz(int operand) {
			if (a == 0) {
				pc += 2;
				return;
			}
			else {
				pc = operand;
			}
		}
		
		private void doBxc(int operand) {
			b = b ^ c;
			pc += 2;
		}

		private void doOut(int operand, StringBuffer output) {
			if (!firstOutput) {
				output.append(",");
			}
			output.append(getComoboValue(operand) % 8);
			firstOutput = false;
			pc += 2;
		}

		private void doBdv(int operand) {
			long numerator = a;
			long denumerator = (long)Math.pow(2, getComoboValue(operand));
			b = numerator / denumerator;		
			pc += 2;
		}

		private void doCdv(int operand) {
			long numerator = a;
			long denumerator = (long)Math.pow(2, getComoboValue(operand));
			c = numerator / denumerator;		
			pc += 2;
		}


		public void setB(long value) {
			this.b = value;
		}

		public void setC(long value) {
			this.c = value;
		}
	}


}
