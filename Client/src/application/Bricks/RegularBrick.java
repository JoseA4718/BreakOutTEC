package application.Bricks;

import javafx.scene.paint.Color;

public class RegularBrick extends Brick{

	public RegularBrick(Integer x, Integer y, Integer w, Integer h, Integer points, Color color) {
		super(x, y, w, h, points, color);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String performAction() {
		// TODO Auto-generated method stub
		return "RegularBrick";
	}

}
