package application.Bricks;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public abstract class Brick extends Rectangle {
	
	private Integer points;
	public Integer row;
	public Integer col;
	
	public Brick(Integer x, Integer y, Integer w, Integer h, Integer points, Color color) {
		
		super(w,h,color);
		this.points = points;
		
		setTranslateX(x);
		setTranslateY(y);
		
	}
	public abstract String performAction();
	
	public Integer getPoints() {
		return this.points;
	}
	
}