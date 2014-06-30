import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.util.Random;
import java.util.Timer;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.*;
import java.awt.event.*;
public class Snake extends JFrame
{
	private static final long serialVersionUID = 5566322968333965313L;
	final public static JFrame frame = new JFrame("Snake");
	public static Game game = new Game();
	public static void createWindow()
	{
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});
		frame.add(game);
		frame.pack();
		frame.setSize(500, 500);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	public static void main(String[] args)
	{
		createWindow();
	}
}
class Game extends JPanel
{
	private static final long serialVersionUID = 2998443088652816629L;
	public static int boardSize = 15;
	public static int snakeSize, score, points;
	public static int gameSpeed = 400;
	public static int scoreModifier = 1;
	public static int pointsModifier = 1;
	public static int[][] snake = new int[(boardSize*boardSize)+1][2];
	public static int[][] apples = new int[10][2];
	public static int numApples = 1;
	public boolean go = true;
	public boolean gameOver = false;
	public static boolean paused = false;
	int direction = 2;
	Thread thread;
	static Timer timer = new Timer();
	public static boolean showUpgradeScreen = false;
	public static boolean showScoreBoard = false;
	public static int upgradeChoicePos = 0;
	public static String[] upgrades = {"Speed Increase 10 points", "Speed Decrease 10 points", "Points x2 25 points", "Add Apple 50 points", "Score x2 75 points"};
	public static String[][] highScores = {{"A Hacker", "99999"}, {"Expert", "100"}, {"Medium", "50"}, {"Novice", "20"}, {"Stephen", "1"}};
	Runnable move = new Runnable()
	{
		public void run() 
		{
			try
			{
				while(!gameOver)
				{
					if(go && !paused) move();
					else go = true;
					Thread.sleep(gameSpeed);
				}
			}
			catch (InterruptedException e) {e.printStackTrace();}
			for(int x = 0; x < 5; x++)
			{
				if(highScores[x][1] != null && !highScores[x][1].equals("") && (score) > Integer.parseInt(highScores[x][1]))
				{
					highScorePrompt((score), x);
					break;
				}
			}
			Object[] options = { "Yes", "No", "Cancel" };
			int choice = -1;
			if(gameOver) choice = JOptionPane.showOptionDialog(Snake.frame, "Game Over! Your score was " + (score) + ". Play again?", "Game Over", 1, 3, null, options, options[2]);
			if(choice == 0) newGame();
		}
	};
	public Game()
	{
		addKeyListener(new AL());
		setFocusable(true);
		for(int x = 0; x < boardSize*boardSize; x++)
		{
			snake[x][0] = -1;
			snake[x][1] = -1;
		}
		snake[0][0] = (boardSize/2);
		snake[0][1] = (boardSize/2);
		snake[1][0] = (boardSize/2);
		snake[1][1] = (boardSize/2)+1;
		snake[2][0] = (boardSize/2);
		snake[2][1] = (boardSize/2)+2;
		snakeSize = 3;
		gameSpeed = 400;
		numApples = 1;
		points = 0;
		scoreModifier = 1;
		pointsModifier = 1;
		score = 0;
		upgradeChoicePos = 0;
		eatApple(0);
		thread = new Thread(move);
		thread.start();
		repaint();
	}
	public void newGame()
	{
		for(int x = 0; x < boardSize*boardSize; x++)
		{
			snake[x][0] = -1;
			snake[x][1] = -1;
		}
		snake[0][0] = boardSize/2;
		snake[0][1] = boardSize/2;
		snake[1][0] = (boardSize/2);
		snake[1][1] = (boardSize/2)+1;
		snake[2][0] = (boardSize/2);
		snake[2][1] = (boardSize/2)+2;
		snakeSize = 3;
		go = true;
		gameOver = false;
		direction = 2;
		gameSpeed = 400;
		upgradeChoicePos = 0;
		pointsModifier = 1;
		score = 0;
		numApples = 1;
		points = 0;
		scoreModifier = 1;
		eatApple(0);
		thread = new Thread(move);
		thread.start();
		repaint();
	}
	public void highScorePrompt(int score, int pos)
	{
		String name = JOptionPane.showInputDialog(null, "New high score! If you would like to record this high score, please input a name:");
		if(name != null && !name.equals("")) addToHighScores(name, score, pos);
	}
	public void addToHighScores(String name, int s, int pos)
	{
		for(int x = 3; x >= pos; x--)
		{
			highScores[x+1][0] = highScores[x][0];
			highScores[x+1][1] = highScores[x][1];
		}
		highScores[pos][0] = name;
		highScores[pos][1] = s + "";
	}
	public boolean legalMove(int x, int y)
	{
		if(snakeOverLap(x, y) || x > boardSize-1 || x < 0 || y > boardSize-1 || y < 0) return false;
		return true;
	}
	public void move()
	{
		if(!gameOver)
		{
			for(int x = snakeSize-1; x >= 0; x--)
			{
				snake[x+1][0] = snake[x][0];
				snake[x+1][1] = snake[x][1];
			}
			if(direction == 0 && legalMove(snake[0][0], snake[0][1]+1)) snake[0][1] += 1;
			else if(direction == 1 && legalMove(snake[0][0]+1, snake[0][1])) snake[0][0] += 1;
			else if(direction == 2 && legalMove(snake[0][0], snake[0][1]-1)) snake[0][1] -= 1;
			else if(direction == 3 && legalMove(snake[0][0]-1, snake[0][1])) snake[0][0] -= 1;
			else gameOver = true;
			boolean a = false;
			for(int x = 0; x < numApples; x++)
			{
				if(snake[0][0] == apples[x][0] && snake[0][1] == apples[x][1])
				{
					snakeSize++;
					score += scoreModifier;
					points += pointsModifier;
					eatApple(x);
					a = true;
				}
			}
			if(a)
			{
				snake[snakeSize+1][0] = -1;
				snake[snakeSize+1][1] = -1;
			}
		}
		if(snakeSize == (boardSize*boardSize)-1) gameOver = true;
		repaint();
	}
	public static void eatApple(int pos)
	{
		Random rand = new Random();
		int x = rand.nextInt(boardSize);
		int y = rand.nextInt(boardSize);
		while(snakeOverLap(x, y))
		{
			x = rand.nextInt(boardSize);
			y = rand.nextInt(boardSize);
		}
		apples[pos][0] = x;
		apples[pos][1] = y;
	}
	public static boolean snakeOverLap(int x, int y)
	{
		for(int i = 0; i < snakeSize; i++) if(snake[i][0] == x && snake[i][1] == y) return true;
		return false;
	}
	public void paint(Graphics g)
	{
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		int h = this.getHeight();
		int w = this.getWidth();
		g2d.setFont(new Font("Arial", Font.BOLD, (h < w) ? h/25 : w/25));
		g2d.setColor(new Color(110, 110, 110));
        FontMetrics fm = g2d.getFontMetrics();
        Rectangle2D r = fm.getStringBounds("Score: " + (score), g2d);
        g2d.drawString("Score: " + (score), (this.getWidth() - (int) r.getWidth()) / 2, this.getHeight() - 2);
		g2d.setFont(new Font("Arial", Font.BOLD, (h < w) ? h/40 : w/40));
		fm = g2d.getFontMetrics();
        r = fm.getStringBounds("Press 'Space' to pause, 'H' for High Scores, 'U' for upgrade screen.", g2d);
        g2d.setColor(new Color(50, 50, 50, 80));
        g2d.drawString("Press 'Space' to pause, 'H' for High Scores, 'U' for upgrade screen.", (this.getWidth() - (int) r.getWidth()) / 2, (int) r.getHeight());
        g2d.setColor(Color.BLACK);
        for(int x = 0; x < numApples; x++)
        {
        	g2d.drawRect((apples[x][0]*(w/boardSize)) + (w)/(boardSize*8), (apples[x][1]*(h/boardSize)) + (h)/(boardSize*8), (w*3)/(boardSize*4), (h*3)/(boardSize*4));
        }
		for(int x = 0; x < snakeSize; x++)
		{
			g2d.fillRect((snake[x][0]*(w/boardSize)) + (w)/(boardSize*8), (snake[x][1]*(h/boardSize)) + (h)/(boardSize*8), (w*3)/(boardSize*4), (h*3)/(boardSize*4));
		}
		if(paused)
		{
			g2d.setColor(new Color(100, 100, 100, 50));
			g2d.fillRect(0, 0, w, h);
			g2d.setColor(Color.GRAY);
			g2d.fillRect((w/2)-((w*2)/40), (h/2)-(h/10), w/30, h/10);
			g2d.fillRect((w/2)+(w/160), (h/2)-(h/10), w/30, h/10);
			g2d.setColor(Color.BLACK);
		}
		if(showScoreBoard)
		{
			g2d.setColor(Color.GRAY);
			g2d.fillRect(w/8, h/8, (w*3)/4, (h*3)/4);
			g2d.setColor(Color.BLACK);
			g2d.drawRect(w/8, h/8, (w*3)/4, (h*3)/4);
			g2d.drawLine(w/8, (h*2)/8, (w/8)+((w*3)/4), (h*2)/8);
			g2d.setFont(new Font("Arial", Font.BOLD, (h < w) ? h/15 : w/15));
	        fm = g2d.getFontMetrics();
	        r = fm.getStringBounds("High Scores", g2d);
			g2d.drawString("High Scores", (this.getWidth() - (int) r.getWidth()) / 2, (h/5));
			g2d.setFont(new Font("Arial", Font.BOLD, (h < w) ? h/20 : w/20));
			g2d.drawString("Name", this.getWidth()/5 + ((h < w) ? h/25 : w/25)*2, (13*h)/40);
			g2d.drawString("Score", (this.getWidth()*13)/20, (13*h)/40);
			g2d.setColor(Color.WHITE);
			for(int i = 0; i < 5; i++)
			{
				if(highScores[i][0] != null && !highScores[i][0].equals("") && highScores[i][1] != null && !highScores[i][1].equals(""))
				{
			        String name = (i+1) + ".  " + ((highScores[i][0].length() < 10) ? highScores[i][0] : highScores[i][0].substring(0, 11));
			        fm = g2d.getFontMetrics();
			        r = fm.getStringBounds(name, g2d);
			        int y = ((((i*2)+8)*h)/20);
			        g2d.drawString(name, this.getWidth()/5, y);
			        g2d.drawString(zeros(highScores[i][1]), (this.getWidth()*13)/20, y);
				}
			}
			g2d.setColor(Color.BLACK);
		}
		if(showUpgradeScreen)
		{
			g2d.setColor(Color.GRAY);
			g2d.fillRect(w/8, h/8, (w*3)/4, (h*3)/4);
			g2d.setFont(new Font("Arial", Font.BOLD, (h < w) ? h/25 : w/25));
			g2d.setColor(new Color(50, 50, 50));
	        fm = g2d.getFontMetrics();
	        r = fm.getStringBounds("Points: " + points, g2d);
			g2d.drawString("Points: " + points, (this.getWidth() - (int) r.getWidth()) / 2, (13*h)/40);
			g2d.setColor(Color.BLACK);
			g2d.drawRect(w/8, h/8, (w*3)/4, (h*3)/4);
			g2d.drawLine(w/8, (h*2)/8, (w/8)+((w*3)/4), (h*2)/8);
			g2d.setFont(new Font("Arial", Font.BOLD, (h < w) ? h/15 : w/15));
	        fm = g2d.getFontMetrics();
	        r = fm.getStringBounds("Upgrades", g2d);
			g2d.drawString("Upgrades", (this.getWidth() - (int) r.getWidth()) / 2, (h/5));
			g2d.setFont(new Font("Arial", Font.BOLD, (h < w) ? h/35 : w/35));
			g2d.setColor(new Color(50, 50, 50));
	        fm = g2d.getFontMetrics();
	        r = fm.getStringBounds("Hit 'Enter' to choose your upgrade.", g2d);
			g2d.drawString("Hit 'Enter' to choose your upgrade.", (this.getWidth() - (int) r.getWidth()) / 2, (h*7/8-5));
			g2d.setFont(new Font("Arial", Font.BOLD, (h < w) ? h/20 : w/20));
			g2d.setColor(Color.WHITE);
			for(int i = 0; i < upgrades.length; i++)
			{
				fm = g2d.getFontMetrics();
				r = fm.getStringBounds(upgrades[i], g2d);
				int y = ((((i*2)+8)*h)/20);
				g2d.drawString(upgrades[i], (int)(this.getWidth()/2 - (r.getWidth()/2)), y);
				if(i == upgradeChoicePos) g2d.drawRect((int)(this.getWidth()/8+5), (int)(y-r.getHeight()+5), (int)((this.getWidth()*3/4)-10), (int)r.getHeight());
			}
			g2d.setColor(Color.BLACK);
		}
	}
	public static String zeros(String inp)
	{
		String returnable = "";
		for(int x = 0; x < 5-inp.length(); x++)
		{
			returnable += "0";
		}
		returnable += inp;
		return returnable;
	}
	public static void chooseUpgrade(int pos)
	{
		if(pos == 0 && points >= 10)
		{
			if(gameSpeed > 100)
			{
				gameSpeed -= 100;
				points -= 10;
			}
		}
		else if(pos == 1 && points >= 10)
		{
			gameSpeed += 100;
			points -= 10;
		}
		else if(pos == 2 && points >= 25)
		{
			pointsModifier *= 2;
			points -= 25;
		}
		else if(pos == 3 && points >= 50)
		{
			if(numApples < 10)
			{
				numApples++;
				eatApple(numApples-1);
				points -= 50;
			}
		}
		else if(pos == 4 && points >= 75)
		{
			scoreModifier *= 2;
			points -= 75;
		}
	}
	private class AL extends KeyAdapter 
	{
		public void keyPressed(KeyEvent e)
		{
			int key = e.getKeyCode();
			if(!showUpgradeScreen)
			{
				if(key == KeyEvent.VK_DOWN && direction != 2 && !paused)
				{
					direction = 0;
				}
				else if(key == KeyEvent.VK_RIGHT && direction != 3 && !paused)
				{
					direction = 1;
				}
				else if(key == KeyEvent.VK_UP && direction != 0 && !paused)
				{
					direction = 2;
				}
				else if(key == KeyEvent.VK_LEFT && direction != 1 && !paused)
				{
					direction = 3;
				}
				if(key == KeyEvent.VK_SPACE || key == KeyEvent.VK_P) paused = (paused) ? false : true;
			}
			else
			{
				if(key == KeyEvent.VK_ENTER) chooseUpgrade(upgradeChoicePos);
				if(key == KeyEvent.VK_UP)
				{
					upgradeChoicePos--;
					if(upgradeChoicePos < 0) upgradeChoicePos = upgrades.length-1;
				}
				if(key == KeyEvent.VK_DOWN)
				{
					upgradeChoicePos++;
					if(upgradeChoicePos > upgrades.length-1) upgradeChoicePos = 0;
				}
			}
			if(key == KeyEvent.VK_H)
			{
				showScoreBoard = (showScoreBoard) ? false : true;
				if(showScoreBoard) if(showUpgradeScreen) showUpgradeScreen = false;
				paused = (showScoreBoard) ? true : false;
			}
			if(key == KeyEvent.VK_U)
			{
				showUpgradeScreen = (showUpgradeScreen) ? false : true;
				if(showUpgradeScreen) if(showScoreBoard) showScoreBoard = false;
				paused = (showUpgradeScreen) ? true : false;
			}
			if(key == KeyEvent.VK_N) newGame(); 
			repaint();
		}
	}
}
