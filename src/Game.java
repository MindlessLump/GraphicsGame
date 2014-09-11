import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Label;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

class Game {
	static protected Label label;
	static protected JFrame frame;
	
	public void init() {
		
	}
	
	public static void main(String args[]) {
		frame = new JFrame("GraphicsGame");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GraphicsPanel graphicsPanel = new GraphicsPanel();
		graphicsPanel.setBackground(Color.WHITE);
		frame.add(graphicsPanel);
		frame.setSize(1280, 720);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	public static int getRandom(int bounds) {
		Random r = new Random();
		return r.nextInt(bounds);
	}
}

class GraphicsPanel extends JPanel {
	private static final long serialVersionUID = 1L; //To suppress warnings
	public int[][] boids = new int[256][3];
	long nextSecond = System.currentTimeMillis() + 1000;
	int framesInLastSecond = 0;
	int framesInCurrentSecond = 0;
	long startTime = 0;
	long stopTime = 0;
	long[] times = new long[200];
	
	public void paintComponent(Graphics g) {
		startTime = System.currentTimeMillis();
		super.paintComponent(g);
		
		//Draw the boids the first time
		if(boids[0][0] == 0) {
			g.setColor(Color.BLACK);
			for(int j = 0; j < boids.length; j++) {
				int x = Game.getRandom(getWidth()-1);
				int y = Game.getRandom(getHeight()-1);
				g.fillRect(x, y, 4, 4);
				boids[j][0] = j; //The first column has the boid number
				boids[j][1] = x; //The second column has the x-position of the boid
				boids[j][2] = y; //The third column has the y-position of the boid
			}
		}
		
		//Draw the boids therafter
		else {
			g.setColor(Color.BLACK);
			for(int j = 0; j < 64; j++) {
				g.fillRect(boids[j][1], boids[j][2], 4, 4);
			}
		}
		
		//Move the boids according to the laws of flock movement
		int[] change = new int[3];
		for(int j = 0; j < boids.length; j++) {
			change = moveBoid(boids[j]);
		}
		
		//Move the boids randomly
		for(int j = 0; j < boids.length; j++) {
			int direction = Game.getRandom(4);
			int[] info = boids[j];
			switch(direction) {
			case 0: //North
				boids[j][1] = info[1];
				boids[j][2] = info[2] + 1;
				break;
			case 1: //East
				boids[j][1] = info[1] + 1;
				boids[j][2] = info[2];
				break;
			case 2: //South
				boids[j][1] = info[1];
				boids[j][2] = info[2] - 1;
				break;
			case 3: //West
				boids[j][1] = info[1] - 1;
				boids[j][2] = info[2];
				break;
			default:
				boids[j][1] = info[1];
				boids[j][2] = info[2];
			}
			//Adjust to ensure the boid is within the screen boundaries
			if(boids[j][1] < 2)
				boids[j][1] = 2;
			else if(boids[j][1] > getWidth() - 2)
				boids[j][1] = getWidth() - 2;
			if(boids[j][2] < 2)
				boids[j][2] = 2;
			else if(boids[j][2] > getHeight() - 2)
				boids[j][1] = getHeight() - 2;
		}
		
		//Calculate FPS and average time per second
		long currentTime = System.currentTimeMillis();
		stopTime = System.currentTimeMillis();
		long difference = stopTime - startTime;
		times[framesInCurrentSecond] = difference;
		if(currentTime > nextSecond) {
			for(int j = 0; j < framesInCurrentSecond; j++) {
				
			}
			
			nextSecond += 1000;
			framesInLastSecond = framesInCurrentSecond;
			framesInCurrentSecond = 0;
		}
		framesInCurrentSecond++;
		
		g.setColor(Color.BLACK);
		g.setFont(new Font("Lucida Console", Font.BOLD, 16));
		g.drawString(framesInLastSecond + " FPS", 0, 13);		
		
		repaint();
	}

	public int[] moveBoid(int[] boid) {
		int[] change = new int[3];
		
		//Get a short list of nearby boids
		int[][] nearBoids = new int[boids.length][3];
		int near = 0;
		for(int j = 0; j < boids.length; j++) {
			int boidX = boids[j][1];
			int boidY = boids[j][2];
			if((Math.abs(boid[1] - boidX) < 64) && (Math.abs(boid[2] - boidY) < 64)) {
				nearBoids[near] = boids[j];
				near++;
			}
		}
		
		//Factor in separation
		for(int j = 0; j < nearBoids.length; j++) {
			double dist = Math.sqrt(Math.pow(boid[1] - nearBoids[j][1], 2) + Math.pow(boid[2] - nearBoids[j][2], 2));
			if(dist < 8) {
				change[1] = (int) -(boid[1] - nearBoids[j][1]) / 2; //Pointing away from the other boid
				change[2] = (int) -(boid[2] - nearBoids[j][2]) / 2;
			}
		}
		
		return boid;
	}
}