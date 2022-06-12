package application;
	
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import SocketClient.Client;
import application.Bricks.Brick;
import application.Bricks.BrickFactory;
import application.Bricks.BrickType;
import application.JsonParser.JsonParser;
import application.JsonParser.JsonTest;
import application.Player.Player;
import application.Ball.Ball;
import application.Spectator.Spectator;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * Main function of the game, works as the game loop and parents all asignable instances.
 * @author Jose A.
 */
public class Main extends Application {
	
	//Game constants
	private static final Integer WIDTH = 800;
	private static final Integer HEIGHT = 600;
	private static final Integer PLAYERY = 500;
	private static final Integer PLAYERHEIGHT = 5;
	private static final Integer BALLSPAWNX = WIDTH/2;
	private static final Integer BALLSPAWNY = 350;
	private static final Integer DEFAULTPOINTSBRICK = 100;
	
	//Game variables
	private Integer[][] matrix = new Integer[8][14];
	private Integer lives = 0;
	private Integer ballQuantity = 0;
	private Integer ballSpeed = 0;
	private Integer racketLenght = 0;
	private Integer racketPosition = 0;
	private Integer level = 0;
	private Integer score = 0;
	private Integer greenBrickValue = 0;
	private Integer yellowBrickValue = 0;
	private Integer orangeBrickValue = 0;
	private Integer redBrickValue = 0;
	
	//Reference matrix, works as a flag.
	private Integer[][] prevMatrix = new Integer[8][14];
	
	Integer jsonDebug = 1;
	
	boolean gameOver = false;
	
	static Client cliente = new Client("Init");
	static Spectator spect = new Spectator();

	//Main window
	Stage window;
	Scene menuScene;
	BorderPane root = new BorderPane();
	
	//Game elements, calls the classes and instances new objects
	private Player player; 
	private ArrayList<Ball> balls = new ArrayList<Ball>();
	private ArrayList<Brick> bricks = new ArrayList<Brick>();
	
	//Creates the brick factory, meant to create all the bricks for the game. 
	private BrickFactory factory = new BrickFactory();
	
	//Labels to show score, lives and current level.
	Text scoreLabel = new Text("Score: " + Integer.toString(this.score));
	Text livesLabel = new Text("Lives: " + Integer.toString(this.lives));
	Text levelLabel = new Text("Level: " + Integer.toString(this.level));
	
	//Game JSON parser
	JsonParser parser = new JsonParser();
	
	/**
	 * Creates the content of the window
	 * @return Parent type object (JavaFX)
	 */
	private Parent createContentGame() {
		
		root = new BorderPane();
		HBox stats = new HBox(20);
		stats.getChildren().addAll(scoreLabel, livesLabel, levelLabel);
		root.setTop(stats);
		root.setPrefSize(WIDTH, HEIGHT);
		root.getChildren().add(player);
		spawnBall(ballQuantity);
		
		//Game's main loop, update() is called to update all instances in the window.
		AnimationTimer timer = new AnimationTimer() {
			public void handle(long now) {
				update();
				if(gameOver) {
					stop();
				}
			}
		};
		
		timer.start();	
		createMatrix();
		return root;
	}
	
	/**
	 * Creates the content of the menu window of the game.
	 * @return Vbox (JavaFX), pane window for the menu.
	 */
	private Parent createContentMenu() {
		
		VBox menu = new VBox(20);
		menu.setAlignment(Pos.CENTER);
		Button gameButton = new Button("Play");
		gameButton.setOnAction(e ->{
			
			gameOver = false;
			setupGame();
			Scene gameScene = new Scene(createContentGame());

			gameScene.setOnKeyPressed(f ->{
				switch(f.getCode()) {
				 	case A:
				  		player.moveLeft();
				  		break;
				  	case D: 
				  		player.moveRight();
				  		break;
				  	case C:
				  		cliente.setSentence("Lost Life");
				  		break;
				  	case P:
				  		this.jsonDebug *= -1;
				  		break;
				  }
			  });
			
			window.setScene(gameScene);
		});
		
		Button loadButton = new Button("Load Game");
		loadButton.setOnAction(e ->{				  		
			cliente.setSentence("Init2");
		});
		
		Button spectButton = new Button("Spectate");
		spectButton.setOnAction(e ->{				  		
			spect.display("Prueba", "Ventana de prueba");
			spect.start();
		});
		
		menu.setPrefSize(WIDTH, HEIGHT);
		menu.getChildren().addAll(gameButton, loadButton, spectButton);
		return menu;
	}
	
	/**
	 * Updates all element is the window in real-time
	 */
	private void update() {
		
		//If the matrix changes, deletes the old matrix, asigns the new matrix and reconstructs it.
		if(checkMatrixChange()) {
			System.out.print("Matrix changed");
			clearBricks();
			createMatrix();
		}
		
		//Game over case, if lives < 0, the game ends. 
		if(this.lives < 0 && !gameOver) {
			gameOver = true;
			clearBricks();
			clearBalls();
			cliente.stop();
			Alert.display("You Lost", "Level lost, try again.");
			window.setScene(menuScene);
			System.out.println("LOST");
		}
		
		//If all blocks are destroyed, player wins.
		/*
		if(this.bricks.size() <= 0 && !gameOver) {
			clearBalls();
			gameOver = true;
			AlertBox.display("Felicidades", "Ganaste el nivel");
			window.setScene(menuScene);
			System.out.println("Ganó");
		}*/
		
		//Window boundary check for ball to bounce.
		for(int j = 0; j < balls.size(); j++){
			
			Ball ball = balls.get(j);
			ball.moveX(WIDTH);
			ball.moveY(WIDTH);
			
			//If ball surpasses racket y.pos, player looses a life.
			if(ball.getCenterY() > PLAYERY) {
				root.getChildren().remove(balls.get(j));
				balls.remove(j);
				this.lives--;
		  		cliente.setSentence("Lost Life");
				if(this.lives >= 0) {
					spawnBall(1);
				}
			}
			
			//Case when the ball hits the racket.
			if(player.getBoundsInParent().intersects(ball.getBoundsInParent())) {
				ball.changeDirY();
			}
			
			//Checks if fall hits any brick, depending on the type of brick runs its action.
			for(int i = 0; i < bricks.size(); i++) {
				
				if(ball.getBoundsInParent().intersects(bricks.get(i).getBoundsInParent())) {
					String action = bricks.get(i).performAction();
					Integer points = bricks.get(i).getPoints();
					brickAction(action, points);
					root.getChildren().remove(bricks.get(i));
					System.out.println(bricks.get(i).row + ", " + bricks.get(i).col);
					String action2 = "Broke "+Integer.toString(bricks.get(i).row) + " " +Integer.toString(bricks.get(i).col);
					cliente.setSentence(action2);
					//cliente.setSentence("Broke 6 6");
					bricks.remove(i);
					ball.changeDirY();
				}
			}
			
		}
		
		//Update JSON variables
		setJsonVariables();
		
		//Updates window labels 
		scoreLabel.setText("Score: " + Integer.toString(this.score));
		livesLabel.setText("Lives: " + Integer.toString(this.lives));
		levelLabel.setText("Level: " + Integer.toString(this.level));
	}
	
	/**
	 * Function that creates a matrix, then parses it depending on the values of each brick
	 * 
	 */
	public void createMatrix() {
	
		//Creates brick matrix
		for(int y = 0; y < 8; y++) {
			for(int x = 0; x < 14; x++) {
				
				BrickType type = BrickType.REGULAR;
				Integer points = 100;
				Color color = Color.GRAY;
			
				if(this.matrix[y][x] == 0) {
					continue;
				}
				
				if(this.matrix[y][x] < 5 && this.matrix[y][x] != 0) {
					type = BrickType.REGULAR;
					color = Color.GRAY;
					points = DEFAULTPOINTSBRICK;
				}
				
				if(this.matrix[y][x] == 5) {
					type = BrickType.LIFE;
					color = Color.GREEN;
					points = this.greenBrickValue;
				}
				
				if(this.matrix[y][x] == 6) {
					type = BrickType.BALL;
					color = Color.YELLOW;
					points = this.yellowBrickValue;
				}
				
				if(this.matrix[y][x] == 7) {
					type = BrickType.MOREVEL;
					color = Color.ORANGE;
					points = this.orangeBrickValue;
				}
				
				if(this.matrix[y][x] == 8) {
					type = BrickType.LESSVEL;
					color = Color.PURPLE;
					points = this.orangeBrickValue;
				}
				
				if(this.matrix[y][x] == 9) {
					type = BrickType.DOUBLESIZE;
					color = Color.RED;
					points = this.redBrickValue;
				}
				if(this.matrix[y][x] == 10) {
					type = BrickType.MIDSIZE;
					color = Color.BLACK;
					points = this.redBrickValue;
				}
				
				Brick brick = factory.getBrick(type, WIDTH/14 * x, y * 20 + y + 100 , WIDTH/14 - 1, 20, points, color);
				brick.row = y;
				brick.col = x;
				bricks.add(brick);
				root.getChildren().add(brick);
			}
		}
	}
	/**
	 * Prints the matrix of bricks
	 * @return printed brick matrix
	 */
	public void printMat(Integer[][] mat) {
		for(int y = 0; y < 8; y++) {
			for(int x = 0; x < 14; x++) {
				System.out.print(mat[y][x]);
			}
		}
	}
	
	/**
	 * Checks if the matrix in the JSON changes, if it does, applies that same change to the window.
	 * @return boolean, if the JSON matrix changes or not.
	 */
	private Boolean checkMatrixChange() {
		
		JsonTest json = parser.deserializeJson(cliente.getJsonReceived());

		if (Arrays.deepEquals(json.matrix, this.matrix)){
			update();
			  return false;
		}
		else {
			this.matrix = json.matrix;
			update();
			return true;
		}
	}
	
	/**
	 * Cleans the list of balls, destroy the ball widgets from the window.
	 */
	private void clearBalls() {
		for(int i = 0; i < balls.size(); i++) {
			root.getChildren().remove(balls.get(i));
		}
		balls.clear();
		update();
	}
	
	/**
	 * Cleans the list of bricks, destroy the brick widgets from the window.
	 */
	private void clearBricks() {
		for(int i = 0; i < bricks.size(); i++) {
			root.getChildren().remove(bricks.get(i));
		}
		bricks.clear();
		update();
	}
	
	/**
	 * Spawns the balls in the game
	 * Restrictions: Must be a positive integer
	 * @param quantity: number of balls to spawn
	 */
	private void spawnBall(Integer quantity) {
		for(int i = 0; i < quantity; i++) {
			Ball ball = new Ball(BALLSPAWNX, BALLSPAWNY, 15, ballSpeed, Color.BLACK);
			balls.add(ball);
			root.getChildren().add(ball);
			ballQuantity++;
			update();
		}
	}
	
	/**
	 * Function that changes the game variables when a specified brick is broken.
	 * @param action: function of the brick
	 * @param points: points awarded by the brick
	 * Restrictions: Must be a valid brick case, points must be a positive real number. 
	 */
	public void brickAction(String action, Integer points) {
		System.out.println(action);
		score += points;
		switch(action) {
			case "NormalBrick":
				break;
			case "LiveBrick":
				this.lives++;
				System.out.println(this.lives);
				break;
			case "Ballbrick":
				if (balls.size() < 3) {
					spawnBall(1);
					System.out.println("Create ball");
				}
				break;
			case "DecreaseVelBrick":
				balls.forEach(b ->{
					if(b.getSpeed() > 1) {
						b.IncreaseSpeed(-1);
					}
				});
				ballSpeed--;
				break;
			case "IncreaseVelBrick":
				System.out.print("Aumenta vel bola");
				balls.forEach(b ->{
					if(b.getSpeed() > 1) {
						b.IncreaseSpeed(1);
					}
				});
				ballSpeed++;
				break;
			case "RacketDoubleSizeBrick":
				racketLenght *= 2;
				player.setWidth(racketLenght);
				break;
			case "MidSizeBrick":
				racketLenght /= 2;
				player.setWidth(racketLenght);
				break;
		}
		update();
	}
	
	/**
	 * Prepares the game before showing it to the user.
	 */
	public void setupGame() {
		
		JsonTest json = parser.deserializeJson(cliente.getJsonReceived());
		
		this.matrix = json.matrix;
		this.lives = json.lives;
		
		this.ballQuantity = json.ballQuantity;
		this.ballSpeed = json.ballSpeed;

		this.racketLenght = json.racketLenght;
		this.racketPosition = json.racketPosition;
		
		this.level = json.level;
		this.score = json.score;
		this.greenBrickValue = json.greenBrickValue;
		this.yellowBrickValue = json.yellowBrickValue;
		this.orangeBrickValue = json.orangeBrickValue;
		this.redBrickValue = json.redBrickValue;
		
		Font font = Font.font("Brush Script MT", FontWeight.BOLD, FontPosture.REGULAR, 35);
	    scoreLabel.setFont(font);
	    livesLabel.setFont(font);
	    levelLabel.setFont(font);
	    scoreLabel.setFill(Color.BROWN);
	    livesLabel.setFill(Color.BLUE);
	    levelLabel.setFill(Color.ORANGE);
	    
		player = new Player(racketPosition, PLAYERY, racketLenght, PLAYERHEIGHT, "player", Color.BLUE);
	}
	
	/**
	 * Assigns the variables depending on the upcoming JSON from the server.
	 */
	public void setJsonVariables() {
		
		JsonTest json = parser.deserializeJson(cliente.getJsonReceived());
		
		this.lives = json.lives;
		
		this.ballQuantity = json.ballQuantity;
		this.ballSpeed = json.ballSpeed;
		
		balls.forEach(b->{
			b.setSpeed(this.ballSpeed);
		});

		this.racketLenght = json.racketLenght;
		this.racketPosition = json.racketPosition;
		
		this.level = json.level;
		this.score = json.score;
		this.greenBrickValue = json.greenBrickValue;
		this.yellowBrickValue = json.yellowBrickValue;
		this.orangeBrickValue = json.orangeBrickValue;
		this.redBrickValue = json.redBrickValue;
	}
	
	/**
	 * Function that works as a predefined sleep.
	 * @param seconds: quantity of seconds to make the sleep.
	 */
	public static void timeWait(int seconds){
        try {
            Thread.sleep(seconds * 500);
         } catch (Exception e) {
            System.out.println(e);
         }
    }
	
	/**
	 * Initializer for the game, creates the game menu.
	 * Graphic void, no regular entry.
	 */
	@Override
	public void start(Stage primaryStage) throws IOException {
		cliente.start();

		window = primaryStage;
		  
		menuScene = new Scene(createContentMenu()); 
		
		window.setScene(menuScene);
		window.setTitle("breakOutTec");
		window.show();
	}
	
	/**
	 * Main function, starts the program.
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		launch(args);
	}
}