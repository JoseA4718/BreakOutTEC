package application.Bricks;

import javafx.scene.paint.Color;
/**
 * Brick that cuts the size of the racket in half.
 * @author eduar
 *
 */
public class RacketMidSizeBrick extends Brick{

	public RacketMidSizeBrick(Integer x, Integer y, Integer w, Integer h, Integer points, Color color) {
		super(x, y, w, h, points, color);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public String performAction() {
		return "MidSizeBrick";
	}
}
