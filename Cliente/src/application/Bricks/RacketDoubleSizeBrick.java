package application.Bricks;

import javafx.scene.paint.Color;

public class RacketDoubleSizeBrick extends Brick{
	
	public RacketDoubleSizeBrick(Integer x, Integer y, Integer w, Integer h, Integer points, Color color) {
		super(x, y, w, h, points, color);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public String performAction() {
		return "RacketDoubleSizeBrick";
	}
}
