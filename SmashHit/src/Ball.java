import java.awt.*;

/**
 * @author Ayush Patel
 * @date Originally made May 2018, heavily modified January 2020
 */

public class Ball implements SmashHitConstants {

	// Coordinates of ball
	private int X;
	private int Y;

	// Direction (speed) of ball
	private double speedX;
	private double speedY;

	public Ball() {
		// sets initial X cord to middle of screen (in terms of x cord)
		X = WINDOW_WIDTH / 2;
		// sets initial Y cord to right below blocks
		Y = TOTAL_BLOCK_HEIGHT + BLOCK_HEIGHT + BALL_DIAMETER;
		// Initializes ball speed based on constant
		speedX = BALL_SPEED;
		speedY = BALL_SPEED;
	}

	// pre: none
	// post: returns hit box of ball, based on cords and dimensions of ball
	private Rectangle getHitbox() {
		return new Rectangle(X, Y, BALL_DIAMETER, BALL_DIAMETER / 2);
	}

	// pre: none
	// post: returns the new screen and updates ball coordinates
	public int move() {
		// updates ball cords based on speed of ball
		X += speedX;
		Y += speedY;
		// if ball hits right or left edge of window, reverse the ball x speed
		// (bounce ball off edge)
		if (X <= 0)
			speedX *= -1;
		else if (X >= WINDOW_WIDTH - (BALL_DIAMETER / 2))
			speedX *= -1;
		// if ball hits top of window, reverse the ball y speed (bounce ball off
		// edge)
		if (Y <= 0)
			speedY *= -1;
		// else if, ball is below paddle, then player has lost, so set screen to
		// end game screen
		else if (Y >= PADDLE_Y)
			return END_SCREEN;
		// if got here, then game is still going, so return play screen
		return PLAY_SCREEN;
	}

	// pre: none
	// post: reverses ball y speed if it has collided with block. returns true
	// if ball collided with block, false otherwise
	public boolean isCollided(Rectangle block) {
		// uses rectangle intersect method to determine if ball has collided
		// with block
		boolean collision = getHitbox().intersects(block);
		// if ball is has collided, reverse its y speed (bounce)
		if (collision)
			speedY *= -1;
		return collision;
	}

	// pre: none
	// post: returns x cord of ball
	public int getX() {
		return X;
	}

	// pre: none
	// post: returns y cord of ball
	public int getY() {
		return Y;
	}
}