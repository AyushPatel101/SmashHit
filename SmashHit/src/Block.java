import java.awt.*;

/**
 * @author Ayush Patel
 * @date Originally made May 2018, heavily modified January 2020
 */

public class Block {

	private Color color;
	private Rectangle hit;

	public Block(Color color, Rectangle r) {
		this.color = color;
		this.hit = r;
	}

	// pre: none
	// post: returns color of Block
	public Color getColor() {
		return color;
	}

	// pre: none
	// post: returns hit box of block
	public Rectangle getHitbox() {
		return hit;
	}
}
