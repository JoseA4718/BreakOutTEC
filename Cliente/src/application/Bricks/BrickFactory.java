package application.Bricks;

import javafx.scene.paint.Color;

public class BrickFactory {

	public Brick getBrick(BrickType type, Integer x, Integer y, Integer w, Integer h, Integer points, Color color) {
		
		switch(type) {
			case NORMAL:
				return new NormalBrick(x, y, w, h, points, color);
			case LIFE:
				return new LiveBrick( x,  y,  w,  h,  points,   color);
			case BALL:
				return new BallBrick( x,  y,  w,  h,  points,   color);
			case DOUBLESIZE:
				return new RacketDoubleSizeBrick( x,  y,  w,  h,  points,   color);
			case MIDSIZE:
				return new RacketMidSizeBrick( x,  y,  w,  h,  points,   color);
			case INCVEL:
				return new IncreaseVelBrick( x,  y,  w,  h,  points,   color);
			case DECVEL:
				return new DecreaseVelBrick( x,  y,  w,  h,  points,   color);
		}
		
		return null;
		
	}
	
}
