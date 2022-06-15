package application.Bricks;

import javafx.scene.paint.Color;
/**
 * Factory pattern class
 * @author eduar
 *
 */
public class BrickFactory {
	/**
	 * Function that fabricates a brick object
	 * @param type type of brick
	 * @param x x pos in the UI
	 * @param y y pos in the UI
	 * @param w width
	 * @param h height
	 * @param points points that the brick gives
	 * @param color color of the brick
	 * @return a brick object with the given values.
	 */
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
