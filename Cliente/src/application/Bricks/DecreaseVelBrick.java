package application.Bricks;

import javafx.scene.paint.Color;
/**
 * Type of brick that decreases the speed of the ball.
 * @author eduar
 *
 */
public class DecreaseVelBrick extends Brick{
	
	public DecreaseVelBrick(Integer x, Integer y, Integer w, Integer h, Integer points, Color color) {
		super(x, y, w, h, points, color);
		
	}
	
	@Override
	public String performAction() {
		return "DecreaseVelBrick";
	}
}
