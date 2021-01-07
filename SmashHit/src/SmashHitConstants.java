import java.awt.Toolkit;

/**
 * @author Ayush Patel
 * @date January 2020
 */

public interface SmashHitConstants {
	// GUI dimensions
	public static final int WINDOW_WIDTH = Toolkit.getDefaultToolkit()
			.getScreenSize().width * 3 / 4;;
	public static final int WINDOW_HEIGHT = Toolkit.getDefaultToolkit()
			.getScreenSize().height * 3 / 4;;

	// State of game
	public static final int HOME_SCREEN = 0;
	public static final int PLAY_SCREEN = 1;
	public static final int END_SCREEN = 2;
	public static final int GAME_SPEED = 7;

	// Initial Paddle setup
	public static final int PADDLE_Y = WINDOW_HEIGHT - WINDOW_HEIGHT / 10;

	// Initial block setup
	public static final int BLOCK_ROWS = 8;
	public static final int BLOCKS_PER_ROW = 9;
	public static final int DIS_BETWEEN_BLOCKS = WINDOW_WIDTH / 100;
	public static final int INITIAL_DISPLACEMENT = DIS_BETWEEN_BLOCKS * 3;
	public static final int BLOCK_WIDTH = (WINDOW_WIDTH
			- (INITIAL_DISPLACEMENT * 2)
			- (BLOCKS_PER_ROW * DIS_BETWEEN_BLOCKS)) / BLOCKS_PER_ROW;
	public static final int BLOCK_START_Y = WINDOW_HEIGHT / 10;
	public static final int TOTAL_BLOCK_HEIGHT = (WINDOW_HEIGHT
			- (WINDOW_HEIGHT - PADDLE_Y) - WINDOW_HEIGHT / 3);
	public static final int BLOCK_HEIGHT = (TOTAL_BLOCK_HEIGHT
			- (WINDOW_HEIGHT / 10) - (BLOCK_ROWS * DIS_BETWEEN_BLOCKS))
			/ BLOCK_ROWS;

	// Initial Ball setup
	public static final int BALL_SPEED = 2;
	public static final int BALL_DIAMETER = BLOCK_HEIGHT / 2;
}
