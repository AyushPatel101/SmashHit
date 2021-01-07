
import java.awt.*;
import javax.swing.*;
import java.util.Iterator;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * @author Ayush Patel
 * @date Originally made May 2018, heavily modified January 2020
 */

public class Game implements MouseMotionListener, SmashHitConstants {

	// GUI
	private JFrame frame;
	// storage for blocks in game
	private ArrayList<HashSet<Block>> blocks;
	private Ball ball;
	private Paddle paddle;
	// used to speed up ball as more blocks broken
	private int gameSpeed;
	private int score;
	// used to keep track of what game screen we are on
	private int screen;

	public Game() {
		frame = new JFrame("Smash Hit");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// make frame using constant window width and height
		frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		frame.setBackground(Color.BLACK);
		// start game on home screen
		screen = HOME_SCREEN;
		// make panel that is updated based on state of game
		MyPanel panel = new MyPanel();
		frame.setLocationRelativeTo(null);
		frame.add(panel);
		frame.setVisible(true);

		// need to add MouseMotionListener to update paddle location
		panel.addMouseMotionListener(this);
		// used to update game state (updates GUI)
		new GameThread().start();

	}

	// pre: none
	// post: initializes instance variables (builds game)
	private void play() {
		gameSpeed = GAME_SPEED;
		// remaking paddle here so starts in middle every round
		paddle = new Paddle();
		score = 0;
		// creating grid of blocks to break (distinct for each game)
		createBlocks();
		// creating new ball for each game
		ball = new Ball();
	}

	// pre: none
	// post: update paddleX based on mouse location
	public void mouseMoved(MouseEvent e) {
		if (screen == PLAY_SCREEN)
			paddle.paddleX = e.getX();
	}

	// pre: none
	// post: update paddleX based on mouse location
	public void mouseDragged(MouseEvent e) {
		mouseMoved(e);
	}

	// pre: none
	// post: use SmashHitConstants to generate the grid of blocks to break
	private void createBlocks() {
		blocks = new ArrayList<>();
		int x = INITIAL_DISPLACEMENT;
		int y = BLOCK_START_Y;
		for (int i = 0; i < BLOCK_ROWS; i++) {
			HashSet<Block> currentRow = new HashSet<Block>();
			// used to slightly displace every other row (pattern)
			if (i % 2 == 0)
				x = INITIAL_DISPLACEMENT / 2;
			else
				x = INITIAL_DISPLACEMENT;
			// randomly generating a color for the whole row of blocks
			Color colorRando = new Color((int) (Math.random() * 255),
					(int) (Math.random() * 255), (int) (Math.random() * 255));

			for (int q = 0; q < BLOCKS_PER_ROW; q++) {
				// adding each block to HashSet storing that row of blocks
				currentRow.add(new Block(colorRando,
						new Rectangle(x, y, BLOCK_WIDTH, BLOCK_HEIGHT)));
				// increasing x coordinate of block to generate a row of blocks
				x += BLOCK_WIDTH + DIS_BETWEEN_BLOCKS;

			}
			// increase y coordinate of block to generate multiple rows of
			// blocks
			y += BLOCK_HEIGHT + DIS_BETWEEN_BLOCKS;
			blocks.add(currentRow);
		}
	}

	public static void main(String args[]) {
		new Game();
	}

	// nested class: essentially a paddle object
	// rather than a separate class, more reasonable to have it as nested class
	// because game handles all of paddles movements anyways
	private class Paddle {
		private int paddleX;
		private int paddleHeight;
		private int paddleWidth;

		public Paddle() {
			paddleX = WINDOW_WIDTH / 2;
			paddleHeight = BALL_DIAMETER;
			paddleWidth = BLOCK_WIDTH;
		}

		// pre: none
		// post: returns a Rectangle representing the hit box of the paddle,
		// based on paddle's location
		private Rectangle getHitbox() {
			return new Rectangle(paddleX - (paddleWidth / 2), PADDLE_Y,
					paddleWidth, paddleHeight);
		}

	}

	// nested class: allows for frame to update repeatedly (essentially stop
	// motion animation)
	private class GameThread extends Thread {
		public void run() {
			while (true) {
				try {
					// adding delay to gameThread to essentially slow ball down
					// based on val of gameSpeed
					Thread.sleep(gameSpeed);
					// only updating frame when on game screen (optimization)
					if (screen == PLAY_SCREEN)
						// updating frame (essentially stop motion animation)
						frame.repaint();

				} catch (Exception e) {
					throw new IllegalStateException("gamethread error");
				}
			}
		}
	}

	// nested class: panel that is updated based on what screen game is on
	// more reasonable to have as nested class because panel draws based on
	// state of game
	private class MyPanel extends JPanel {

		// have to store scoreLabel in order to delete when game screen is
		// redrawn
		JLabel scoreLabel;

		// pre: screen is a valid screen
		// post: creates appropriate panel based on screen
		public void paintComponent(Graphics g) {
			// paints screen based on screen game is on
			if (Game.this.screen == HOME_SCREEN)
				paintHomeScreen();
			else if (Game.this.screen == PLAY_SCREEN)
				paintGameScreen(g);
			else if (Game.this.screen == END_SCREEN)
				paintEndGameScreen();
			else
				throw new IllegalStateException("Screen cannot be drawn");
		}

		// pre: none
		// post: updates panel with new game screen
		private void paintGameScreen(Graphics g) {

			//ball cords determine if game is over or not
			Game.this.screen = ball.move();
			//if game screen changes (to end game), repaint the frame
			if (Game.this.screen != PLAY_SCREEN)
				Game.this.frame.repaint();
			//else, continue drawing game screen
			else {
				// Repainting background
				this.setBackground(Color.BLACK);

				//removing old score label
				if (scoreLabel != null)
					this.remove(scoreLabel);
				// Displaying game score
				JLabel scoreL = new JLabel("Score: " + Game.this.score,
						JLabel.CENTER);
				scoreL.setFont(new Font("Verdana", 1, BLOCK_START_Y / 4));
				scoreL.setLocation(0, 0);
				scoreL.setSize(WINDOW_WIDTH - BLOCK_START_Y / 4,
						WINDOW_HEIGHT / 10);
				scoreL.setForeground(Color.WHITE);
				scoreL.setVisible(true);
				this.add(scoreL);
				scoreLabel = scoreL;
				// check if ball hit paddle
				Game.this.ball.isCollided(Game.this.paddle.getHitbox());
				Iterator<HashSet<Block>> iter = Game.this.blocks.iterator();
				// traversing through block 2D ArrayList to check if block is
				// colliding with ball, if not collided, then draw that block
				while (iter.hasNext()) {
					Iterator<Block> iter2 = iter.next().iterator();
					while (iter2.hasNext()) {
						Block block = iter2.next();
						Rectangle hitbox = block.getHitbox();
						// if ball collided with block..
						if (Game.this.ball.isCollided(hitbox)) {
							// update score
							Game.this.score++;
							// decrease gameSpeed (decrease sleep on GameThread)
							// if
							// # of blocks in a row have been popped
							if (Game.this.score % BLOCK_ROWS == 0)
								// make sure gameSpeed doesn't go below (not
								// possible for sleep in GameThread)
								Game.this.gameSpeed = 1 > Game.this.gameSpeed - 1 ? 1
										: Game.this.gameSpeed - 1;
							// remove block hit by ball
							iter2.remove();
						} else {
							// Filling blocks, based on color of block stored
							g.setColor(block.getColor());
							g.fillRect(hitbox.x, hitbox.y, hitbox.width,
									hitbox.height);

							// Outlining blocks
							g.setColor(Color.WHITE);
							g.draw3DRect(hitbox.x, hitbox.y, hitbox.width,
									hitbox.height, true);
						}
					}
				}
				// Displaying paddle
				g.setColor(Color.WHITE);
				g.fillRect(
						Game.this.paddle.paddleX
								- Game.this.paddle.paddleWidth / 2,
						PADDLE_Y, Game.this.paddle.paddleWidth,
						Game.this.paddle.paddleHeight);

				// Displaying ball
				g.setColor(Color.RED);
				g.fillOval(Game.this.ball.getX(), Game.this.ball.getY(),
						BALL_DIAMETER, BALL_DIAMETER);

			}
		}

		// pre: none
		// post: updates panel with home screen
		private void paintHomeScreen() {

			this.setBackground(Color.BLACK);

			// Displaying header
			JLabel header = new JLabel("SMASH HIT", JLabel.CENTER);
			header.setSize(WINDOW_WIDTH / 3, WINDOW_HEIGHT / 3);
			header.setFont(new Font("Verdana", 1,
					header.getSize().width / header.getText().length()));
			header.setLocation(WINDOW_WIDTH / 2 - header.getSize().width / 2,
					WINDOW_HEIGHT / 2 - header.getSize().height / 2);
			header.setForeground(Color.WHITE);
			this.add(header);

			// Displaying play button
			JButton playButton = new JButton("PLAY");
			playButton.setSize(header.getSize().width / 2,
					2 * header.getSize().width / 2 / header.getText().length());
			playButton.setFont(new Font("Verdana", 1,
					playButton.getSize().width / header.getText().length()));
			playButton.setLocation(
					WINDOW_WIDTH / 2 - playButton.getSize().width / 2,
					header.getLocation().y + header.getSize().height
							- playButton.getSize().height / 2);
			playButton.setForeground(Color.BLACK);
			playButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// if play button is clicked, go back to play screen but
					// also rebuild game (play method)
					Game.this.screen = PLAY_SCREEN;
					Game.this.play();
					//remove components of home screen
					Game.MyPanel.this.removeAll();
					Game.this.frame.repaint();
				}
			});
			this.add(playButton);
		}

		// pre: none
		// post: updates panel with new game screen
		private void paintEndGameScreen() {
			this.setBackground(Color.BLACK);

			// Displaying header
			JLabel header = new JLabel("GAME OVER", JLabel.CENTER);
			header.setSize(WINDOW_WIDTH / 3, WINDOW_HEIGHT / 3);
			header.setFont(new Font("Verdana", 1,
					header.getSize().width / header.getText().length()));
			header.setLocation(WINDOW_WIDTH / 2 - header.getSize().width / 2,
					WINDOW_HEIGHT / 2 - header.getSize().height / 2);
			header.setForeground(Color.WHITE);
			this.add(header);

			// Displaying play button
			JButton playButton = new JButton("PLAY AGAIN");
			playButton.setSize(header.getSize().width / 2,
					2 * header.getSize().width / 2 / header.getText().length());
			playButton.setFont(new Font("Verdana", 1,
					playButton.getSize().width / header.getText().length()));
			playButton.setLocation(
					WINDOW_WIDTH / 2 - playButton.getSize().width / 2,
					header.getLocation().y + header.getSize().height
							- playButton.getSize().height / 2);
			playButton.setForeground(Color.BLACK);
			playButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// if play button is clicked, go back to play screen but
					// also rebuild game (play method)
					Game.this.screen = PLAY_SCREEN;
					Game.this.play();
					//remove components of end screen
					Game.MyPanel.this.removeAll();
					Game.this.frame.repaint();
				}
			});
			this.add(playButton);

			// Displaying game score
			JLabel jlabel = new JLabel("Score: " + Game.this.score,
					JLabel.CENTER);
			jlabel.setFont(new Font("Verdana", 1, BLOCK_START_Y / 4));
			jlabel.setLocation(0, 0);
			jlabel.setSize(WINDOW_WIDTH - BLOCK_START_Y / 4,
					WINDOW_HEIGHT / 10);
			jlabel.setForeground(Color.WHITE);
			this.add(jlabel);

		}
	}

}
