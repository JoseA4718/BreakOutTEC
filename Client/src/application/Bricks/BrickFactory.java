package application.Bricks;

import javafx.scene.paint.Color;

public class BrickFactory {

	public Brick getBrick(BrickType type, Integer x, Integer y, Integer w, Integer h, Integer points, Color color) {
		
		switch(type) {
			case NORMAL:
				return new RegularBrick(x, y, w, h, points, color);
			case LIFE:
				return new LifeBrick( x,  y,  w,  h,  points,   color);
			case BALL:
				return new BallBrick( x,  y,  w,  h,  points,   color);
			case DOUBLESIZE:
				return new DoubleRacketBrick( x,  y,  w,  h,  points,   color);
			case MIDSIZE:
				return new HalfRacketBrick( x,  y,  w,  h,  points,   color);
			case INCVEL:
				return new MoreVelocityBrick( x,  y,  w,  h,  points,   color);
			case DECVEL:
				return new LessVelocityBrick( x,  y,  w,  h,  points,   color);
		}
		
		return null;
		
	}
	
}