import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Day09 {

	public static void main(String[] args) throws IOException {
		List<String> lines = Files.readAllLines(Path.of("input_day09.txt"));

		String input = lines.get(0);
		
		// Part 1
		long checksum = computePart1(input);
		System.out.println("Result part 1 : " + checksum); // 6341711060162
		
		// Part 2
		checksum = computePart2(input);
		System.out.println("Result part 2 : " + checksum); // 9819042852363 too high
	}

	private static long computePart1(String input) {
		List<Integer> disk = new ArrayList<Integer>();
		int fileId = 0;
		for (int i=0;i<input.length();i+=2) {
			int blocks = Integer.parseInt(String.valueOf(input.charAt(i)));
			for (int b=0;b<blocks;b++) { disk.add(fileId); }
			if (i+1 < input.length()) {
				int free = Integer.parseInt(String.valueOf(input.charAt(i+1)));
				for (int f=0;f<free;f++) { disk.add(null); }
			}
			fileId++;
		}

		int leftIndex = 0;
		int rightIndex = disk.size()-1;
		while (rightIndex > leftIndex) {
			Integer fileIdToMove = disk.get(rightIndex);
			while (fileIdToMove == null) { 
				disk.remove(rightIndex);
				fileIdToMove = disk.get(--rightIndex); 
			}
			boolean moved = false;
			for (int i=leftIndex;i<rightIndex && !moved;i++) {
				if (disk.get(i) == null) {
					disk.set(i, fileIdToMove);
					leftIndex = i;
					disk.remove(rightIndex);
					moved = true;
				}
			}
			rightIndex--;
		}
		
		long checksum = 0;
		for (int i=0;i<disk.size();i++) {
			checksum += i*disk.get(i);
		}
		return checksum;
	}
	
	private static long computePart2(String input) {
		long checksum;
		List<File> allFiles = new ArrayList<File>();
		for (int i=0;i<input.length();i+=2) {
			int blocks = Integer.parseInt(String.valueOf(input.charAt(i)));
			int free = 0;
			if (i+1 < input.length()) {
				free = Integer.parseInt(String.valueOf(input.charAt(i+1)));
			}
			File file = new File(i / 2, blocks, free);
			allFiles.add(file);
		}

		int fileIdToMove = allFiles.size() - 1;
		
		int indexOfFileToMove = 0;
		for (int i=0;i<allFiles.size();i++) {
			if (allFiles.get(i).id == fileIdToMove) {
				indexOfFileToMove = i;
				break;
			}
		}
		
		while (fileIdToMove >= 0) {
			File fileToMove = allFiles.get(indexOfFileToMove);
			boolean moved = false;
			for (int i=0;i<allFiles.size() && !moved;i++) {
				File file = allFiles.get(i);
				if (file.id == fileIdToMove) break;
				if (file.freeBlocks >= fileToMove.usedBlocks) {
					allFiles.remove(indexOfFileToMove);
					allFiles.get(indexOfFileToMove-1).freeBlocks += (fileToMove.usedBlocks+fileToMove.freeBlocks);
					allFiles.add(i+1, new File(fileToMove.id, fileToMove.usedBlocks, file.freeBlocks - fileToMove.usedBlocks));
					file.freeBlocks = 0;
					moved = true;
				}
			}
			
			fileIdToMove--;
			indexOfFileToMove = 0;
			for (int i=0;i<allFiles.size();i++) {
				if (allFiles.get(i).id == fileIdToMove) {
					indexOfFileToMove = i;
					break;
				}
			}
		}
		
		checksum=0;
		int blockIndex = 0;
		for (File file : allFiles) {
			for (int i=0;i<file.usedBlocks;i++) {
				checksum += blockIndex * file.id;
				blockIndex++;
			}
			blockIndex += file.freeBlocks;
		}
		return checksum;
	}
	
	private static class File {
		int id;
		int usedBlocks;
		int freeBlocks;
		
		public File(int fileId, int blocks, int free) {
			this.id = fileId;
			this.usedBlocks = blocks;
			this.freeBlocks = free;
		}

	}

}
