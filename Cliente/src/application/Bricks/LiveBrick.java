package application.Bricks;

import javafx.scene.paint.Color;
/**
 * Brick that gives an extra life.
 * @author eduar
 *
 */
public class LiveBrick extends Brick{

	public LiveBrick(Integer x, Integer y, Integer w, Integer h, Integer points,  Color color) {
		super(x, y, w, h, points,  color);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public String performAction() {
		return "LiveBrick";
	}
}
