package application.PlayerAndBall;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

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
	
	private void checkBoundsWindow(Integer windowWidth) {
		
		//Choca con borde
		if(!(0 < (int) this.getCenterX() && (int) this.getCenterX() < windowWidth)) {
			dirX *= -1;
		}
		
		if(this.getCenterY() <= 0) {
			changeDirY();
		}
		
	}
	
	public void moveX(Integer winWidth) {
		checkBoundsWindow(winWidth);
		setCenterX(getCenterX() + this.speed * dirX);
	}
	
	public void moveY(Integer winWidth) {
		
		setCenterY(getCenterY() + this.speed * dirY);
	}
	
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
