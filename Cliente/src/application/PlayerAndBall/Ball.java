package application.PlayerAndBall;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
/**
 * Class for the ball of the game.
 * @author eduar
 *
 */
public class Ball extends Circle{

	private Integer speed;
	public Integer dirX = 1;
	public Integer dirY = 1;
	
	public Ball(Integer posX, Integer posY, Integer radius, Integer speed, Color color) {
		super(posX, posY, radius, color);
		
		this.speed = speed;
		
		setCenterX(posX);
		setCenterY(posY);
		
	}
	/**
	 * Method that checks the boundaries of the window.
	 * @param windowWidth
	 */
	private void checkBoundsWindow(Integer windowWidth) {
		
		//Collision
		if(!(0 < (int) this.getCenterX() && (int) this.getCenterX() < windowWidth)) {
			dirX *= -1;
		}
		
		if(this.getCenterY() <= 0) {
			changeDirY();
		}
		
	}
	/**
	 * Move the ball in the x axis., checks if its going to collide with the border
	 * @param winWidth
	 */
	public void moveX(Integer winWidth) {
		checkBoundsWindow(winWidth);
		setCenterX(getCenterX() + this.speed * dirX);
	}
	/**
	 * Move the ball in the y axis., checks if its going to collide with the border
	 * @param winWidt
	 */
	public void moveY(Integer winWidth) {
		
		setCenterY(getCenterY() + this.speed * dirY);
	}
	/**
	 * Change the direction in the y axis.
	 */
	public void changeDirY() {
		this.dirY *= -1;
	}
	
	public void IncreaseSpeed(Integer inc) {
		this.speed += inc;
	}

	public Integer getSpeed() {
		return this.speed;
	}
	
	public void setSpeed(Integer speed) {
		this.speed = speed;
	}
}
