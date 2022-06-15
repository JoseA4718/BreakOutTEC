package application.Bricks;

import javafx.scene.paint.Color;
/**
 * Class for the brick with the power up of giving a ball when broken.
 * @author eduar
 *
 */
public class BallBrick extends Brick{

	public BallBrick(Integer x, Integer y, Integer w, Integer h, Integer points, Color color) {
		super(x, y, w, h, points, color);
		
	}
	
	@Override
	public String performAction() {
		return "Ballbrick";
	}
}

