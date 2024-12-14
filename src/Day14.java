import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

public class Day14 {

	public static void main(String[] args) throws IOException {
		List<String> lines = Files.readAllLines(Path.of("input_day14.txt"));

		List<Robot> robots = parse(lines);

		// Part 1
		long result = 0;
		
		for (int i=0;i<100;i++) {
			simulate(robots);
		}
		
		// Size 101x103 (wide, tall)
		long q1Count = robots.stream().filter(r -> r.location.x < 50 && r.location.y < 51).count();
		long q2Count = robots.stream().filter(r -> r.location.x > 50 && r.location.y < 51).count();
		long q3Count = robots.stream().filter(r -> r.location.x < 50 && r.location.y > 51).count();
		long q4Count = robots.stream().filter(r -> r.location.x > 50 && r.location.y > 51).count();
		result = q1Count * q2Count * q3Count * q4Count;
		
		System.out.println("Result part 1 : " + result);

		// Part 2
		robots = parse(lines);
		int steps = 0;
		while(true) {
			if (detectHighDensityZones(robots, 4, 4, 16)) {
				createPNG(robots, "output.png");
				result = steps;
				break;
			}
			simulate(robots);
			steps++;
		}
		System.out.println("Result part 2 : " + result);
	}

	private static void simulate(List<Robot> robots) {
		for (Robot robot : robots) {
			robot.location.x = (robot.location.x + 101 + robot.speed.x) % 101;
			robot.location.y = (robot.location.y + 103 + robot.speed.y) % 103;
		}
	}

	public static class Robot {

		public Point2D location;
		public Point2D speed;

		public Robot(Point2D location, Point2D speed) {
			super();
			this.location = location;
			this.speed = speed;
		}
	}

	private static boolean detectHighDensityZones(List<Robot> robots, int cellWidth, int cellHeight, int densityThreshold) {

        Map<String, Integer> grid = new HashMap<>();

        for (Robot robot : robots) {
            int cellX = robot.location.x / cellWidth;
            int cellY = robot.location.y / cellHeight;
            String key = cellX + "," + cellY;
            grid.put(key, grid.getOrDefault(key, 0) + 1);
        }

        List<String> highDensityCells = grid.entrySet().stream()
                .filter(entry -> entry.getValue() >= densityThreshold)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        return !highDensityCells.isEmpty();
    }
	
	public static List<Robot> parse(List<String> lines) throws IOException {
        List<Robot> robots = new ArrayList<>();
        
        Pattern pattern = Pattern.compile("p=([-]?\\d+),([-]?\\d+)\\s+v=([-]?\\d+),([-]?\\d+)");

        for (String line : lines) {
            Matcher matcher = pattern.matcher(line.trim());
            if (matcher.matches()) {
                try {
                    // Extraire les groupes capturés
                    int pX = Integer.parseInt(matcher.group(1));
                    int pY = Integer.parseInt(matcher.group(2));
                    int vX = Integer.parseInt(matcher.group(3));
                    int vY = Integer.parseInt(matcher.group(4));

                    // Créer les objets Vector et DataPoint
                    Point2D location = new Point2D(pX, pY);
                    Point2D speed = new Point2D(vX, vY);
                    
                    robots.add(new Robot(location, speed));
                    
                } catch (NumberFormatException e) {
                    System.err.println("Erreur de format numérique dans la ligne : " + line);
                }
            } else {
                System.err.println("Format de ligne invalide : " + line);
            }
        }
        
        return robots;
    }
	
	private static void createPNG(List<Robot> robots, String filename) {

        int width = 101;  // Largeur en pixels
        int height = 103; // Hauteur en pixels

        // Créer une image en mémoire
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // Remplir l'arrière-plan en blanc
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, width, height);

        // Définir la couleur des robots
        g2d.setColor(Color.WHITE);

        // Dessiner chaque robot
        for (Robot robot : robots) {
            int x = (int) (robot.location.x);
            int y = (int) (robot.location.y);
            g2d.fillRect(x, y, 1, 1);
        }

        // Libérer les ressources graphiques
        g2d.dispose();

        // Enregistrer l'image en PNG
        try {
            ImageIO.write(image, "png", Path.of(filename).toFile());
        } 
        catch (IOException e) {
        	e.printStackTrace();
        }
    }
	
	private static class Point2D {

		int x, y;

		public Point2D(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + x;
			result = prime * result + y;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Point2D other = (Point2D) obj;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}
	}

}
