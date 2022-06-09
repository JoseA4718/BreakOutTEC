package application.JsonParser;

public class JsonTest {
	
	public Integer[][] matrix = new Integer[8][14];
	public Integer lives;
	public Integer ballQuantity;
	public Integer ballSpeed;
	public Integer[] ballPosition = new Integer[2];
	public Integer racketLenght;
	public Integer racketPosition;
	public Integer level;
	public Integer score;
	public Integer greenBrickValue;
	public Integer yellowBrickValue;
	public Integer orangeBrickValue;
	public Integer redBrickValue;
	
	public JsonTest(Integer[][] matrix, Integer lives, Integer ballQuantity, Integer ballSpeed,
			Integer[] ballPosition, Integer racketLenght, Integer racketPosition, Integer level, Integer score,
			Integer greenBrickValue, Integer yellowBrickValue, Integer orangeBrickValue, Integer redBrickValue) {
		super();
		this.matrix = matrix;
		this.lives = lives;
		this.ballQuantity = ballQuantity;
		this.ballSpeed = ballSpeed;
		this.ballPosition = ballPosition;
		this.racketLenght = racketLenght;
		this.racketPosition = racketPosition;
		this.level = level;
		this.score = score;
		this.greenBrickValue = greenBrickValue;
		this.yellowBrickValue = yellowBrickValue;
		this.orangeBrickValue = orangeBrickValue;
		this.redBrickValue = redBrickValue;
	}
	
	
	
}