package application.Bricks;

import javafx.scene.paint.Color;
/**
 * Normal brick class.
 * @author eduar
 *
 */
public class NormalBrick extends Brick{

	public NormalBrick(Integer x, Integer y, Integer w, Integer h, Integer points, Color color) {
		super(x, y, w, h, points, color);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String performAction() {
		// TODO Auto-generated method stub
		return "NormalBrick";
	}

}
