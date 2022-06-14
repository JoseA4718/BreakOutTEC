package application;	
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import ClientSocket.Cliente;
import application.Bricks.Brick;
import application.Bricks.BrickFactory;
import application.Bricks.BrickType;
import application.Parser.JsonParser;
import application.Parser.JsonTestClass;
import application.PlayerAndBall.Ball;
import application.PlayerAndBall.Player;
import application.SpectClient.Spect;
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
import javafx.scene.text.TextAlignment;

public class Main extends Application {
	
	//Constantes del juego
	private static final Integer WIDTH = 800;
	private static final Integer HEIGHT = 600;
	private static final Integer PLAYERY = 500;
	private static final Integer PLAYERHEIGHT = 5;
	private static final Integer BALLSPAWNX = WIDTH/2;
	private static final Integer BALLSPAWNY = 350;
	private static final Integer DEFAULTPOINTSBRICK = 100;
	
	//variables
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
	
	//Matriz de referencia, para saber si cambia
	private Integer[][] prevMatrix = new Integer[8][14];
	
	//Debug de los json
	Integer jsonDebug = 1;
	
	boolean gameOver = false;
	
	static Cliente cliente = new Cliente("Init");
	static Spect spect = new Spect();

	//Ventana principal
	Stage window;
	Scene menuScene;
	BorderPane root = new BorderPane();

	
	//Elementos del juego
	private Player player; 
	private ArrayList<Ball> balls = new ArrayList<Ball>();
	private ArrayList<Brick> bricks = new ArrayList<Brick>();
	
	//Factor�a de bricks
	private BrickFactory factory = new BrickFactory();
	
	//WIdgets de estadisticas del juego
	
	Text scoreLabel = new Text("Score: " + Integer.toString(this.score));
	Text livesLabel = new Text("Lives: " + Integer.toString(this.lives));
	Text levelLabel = new Text("Level: " + Integer.toString(this.level));
	
	//Parser del juego en formato JSON
	JsonParser parser = new JsonParser();
	
	/*Funci�n que crea el contenido de la ventana en ese momento
	 * Entradas: -
	 * Salidas: Objeto tipo Parent (JavaFX)
	 * Restricciones: -
	 */

	private Parent createContentGame() {
		
		root = new BorderPane();
		
		HBox stats = new HBox(20);
		
		stats.getChildren().addAll(scoreLabel, livesLabel, levelLabel);
		
		root.setTop(stats);
		
		root.setPrefSize(WIDTH, HEIGHT);
		
		root.getChildren().add(player);

		spawnBall(ballQuantity);
		
		
		//Se comporta como un mainloop del juego, update se usa como la funci�n que actualiza
		//el juego
		
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
	
	/*
	 * Crea el contenido de la ventana men� del juego
	 * Entradas: -
	 * Saldias: -
	 * REstricciones: -
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
		
		Button spectButton = new Button("Spect");
		spectButton.setOnAction(e ->{				  		
			spect.display("Prueba", "Ventana de prueba");
			spect.start();
		});
		
		menu.setPrefSize(WIDTH, HEIGHT);
		
		menu.getChildren().addAll(gameButton, loadButton, spectButton);
		
		return menu;
		
	}
	
	/*Funci�n que se encarga de actualizar elementos del juego en tiempo real
	 * Entrads: -
	 * Salidas: -
	 * Restricciones: -
	 */
	
	private void update() {
		
		//System.out.print(this.score);
		//Si la matriz cambia, borra la matriz antigua, asigna la nueva matriz y vuelve a construirla
		if(checkMatrixChange()) {
			System.out.print("Matrix changed");
			clearBricks();
			createMatrix();
		}
		
		//Si lives < 0, el juego termina
		if(this.lives < 0 && !gameOver) {
			gameOver = true;
			clearBricks();
			clearBalls();
			cliente.stop();
			AlertBox.display("Derrota", "Perdiste el nivel");
			window.setScene(menuScene);
			System.out.println("Perdi�");
		}
		
		//Si rompe todos los bloques, gana el nivel
		if(this.bricks.size() <= 0 && !gameOver) {
			clearBalls();
			gameOver = true;
			AlertBox.display("Felicidades", "Ganaste el nivel");
			window.setScene(menuScene);
			System.out.println("Gan�");
		}

		//check if balls bounds on window
		for(int j = 0; j < balls.size(); j++){
			
			Ball ball = balls.get(j);
			ball.moveX(WIDTH);
			ball.moveY(WIDTH);
			
			
			//Si alguna bola pasa por debajo de la posici�n Y de la raqueta
			
			if(ball.getCenterY() > PLAYERY) {
				root.getChildren().remove(balls.get(j));
				balls.remove(j);
				this.lives--;
		  		cliente.setSentence("Lost Life");
				if(this.lives >= 0) {
					spawnBall(1);
				}
			}
			
			//Si la bola choca con el jugador, osea la raqueta
			if(player.getBoundsInParent().intersects(ball.getBoundsInParent())) {
				ball.changeDirY();
			}
			
			//Revisar alguna la bola choca contra alg�n ladrillo
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
		
		//Actualizar variables respecto al json
		setVairablesWithJson();
		
		scoreLabel.setText("Score: " + Integer.toString(this.score));
		livesLabel.setText("Lives: " + Integer.toString(this.lives));
		levelLabel.setText("Level: " + Integer.toString(this.level));
		
		
				
		
		//clearBricks();
		
	}
	
	/*
	 * Funci�n que se encarga de crear una matriz, la parsea dependiendo de los valores de cada ladrillo
	 * Entradas: -
	 * Salidas: -
	 * Restricciones: -
	 */
	
	public void createMatrix() {
	
		//Crea la matriz de bloques
		for(int y = 0; y < 8; y++) {
			for(int x = 0; x < 14; x++) {
				
				BrickType type = BrickType.NORMAL;
				Integer points = 100;
				Color color = Color.BLACK;
				
				//System.out.print(matrix[y][x]);
				if(this.matrix[y][x] == 0) {
					continue;
				}
				
				if(this.matrix[y][x] < 5 && this.matrix[y][x] != 0) {
					type = BrickType.NORMAL;
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
					type = BrickType.INCVEL;
					color = Color.ORANGE;
					points = this.orangeBrickValue;
				}
				
				if(this.matrix[y][x] == 8) {
					type = BrickType.DECVEL;
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
				
				//Integer iInteger = new Integer(iInt);
				Brick brick = factory.getBrick(type, WIDTH/14 * x, y * 20 + y + 100 , WIDTH/14 - 1, 20, points, color);
				brick.row = y;
				brick.col = x;
				bricks.add(brick);
				root.getChildren().add(brick);
			}
		}
	}
	
	public void printMat(Integer[][] mat) {
		for(int y = 0; y < 8; y++) {
			for(int x = 0; x < 14; x++) {
				System.out.print(mat[y][x]);
			}
		}
	}
	
	/*
	 * Funci�n que revisa si la matriz cambia, respecto al json para poder cambiarla en la interfaz
	 * Entradas: -
	 * Salidas; -
	 * Restricciones: -
	 */
	
	private Boolean checkMatrixChange() {
		
		JsonTestClass json = parser.deserializeJson(cliente.getJsonReceived());

		if (Arrays.deepEquals(json.matrix, this.matrix)){
			  return false;
		}
		else {
			this.matrix = json.matrix;
			return true;
		}
	}
	

	/*
	 * Funci�n que se encarga de limpiar la lista de bolas, adem�s de destruir los widgets tipo bola de la interfaz
	 * Entradas: -
	 * Salidas: -
	 * Restricciones: -
	 */
	
	
	private void clearBalls() {
		for(int i = 0; i < balls.size(); i++) {
			root.getChildren().remove(balls.get(i));
		}
		balls.clear();
		
	}
	
	/*
	 * Funci�n que se encarga de limpiar la lista de ladrillos, adem�s de destruir los widgets tipo ladrillo de la interfaz
	 * Entradas: -
	 * Salidas: -
	 * Restricciones: -
	 */
	
	private void clearBricks() {
		for(int i = 0; i < bricks.size(); i++) {
			root.getChildren().remove(bricks.get(i));
		}
		bricks.clear();
	}
	
	/*
	 * Funci�n que se encarga de spawnear las bolas en el juego
	 * Entradas: quantity: cantidad de bolas a spawnear en el juego
	 * Salidas: -
	 * Restricciones: quantity deber�a ser positivo
	 */
	
	private void spawnBall(Integer quantity) {
		for(int i = 0; i < quantity; i++) {
			Ball ball = new Ball(BALLSPAWNX, BALLSPAWNY, 15, ballSpeed, Color.AQUA);
			balls.add(ball);
			root.getChildren().add(ball);
			ballQuantity++;
		}
	}
	
	/*
	 * Funci�n que se encarga decambiar variables del juego cuando un bloque se rompe
	 * Entradas: action: funci�n del bloque en el juego, points: los puntos que da el bloque
	 * Salidas: -
	 * Restricciones: action debe ser alg�n caso, points deber�a ser positivo 
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
				update();
				break;
			case "Ballbrick":
				if (balls.size() < 3) {
					spawnBall(1);
					System.out.println("crea bola");
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
		
	}
	
	
	/*
	 * funci�n para preparar el juego, antes de mostrarlo al usuario en la interfaz
	 * Entradas: -
	 * Salidas: -
	 * Restricciones: -
	 */
	
	public void setupGame() {
		
		JsonTestClass json = parser.deserializeJson(cliente.getJsonReceived());
		
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
		
		//Estilizar labels del juego
		Font font = Font.font("Brush Script MT", FontWeight.BOLD, FontPosture.REGULAR, 35);
	    scoreLabel.setFont(font);
	    livesLabel.setFont(font);
	    levelLabel.setFont(font);
	    //Filling color to the label
	    scoreLabel.setFill(Color.BLACK);
	    livesLabel.setFill(Color.BLACK);
	    levelLabel.setFill(Color.BLACK);
	    
		player = new Player(racketPosition, PLAYERY, racketLenght, PLAYERHEIGHT, "player", Color.BLUE);
		
	}
	
	
	
	
	/*
	 *Funci�n que se encarga de asignar las variables dependiendo del json
	 *Entradas: -
	 *Salidas: -
	 *Restricciones: - 
	 */
	
	public void setVairablesWithJson() {
		
		JsonTestClass json = parser.deserializeJson(cliente.getJsonReceived());
		
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
	
	public static void esperar(int segundos){
        try {
            Thread.sleep(segundos * 500);
         } catch (Exception e) {
            System.out.println(e);
         }
    }
	
	/*
	 * Funci�n inicializadora del juego, crea el men� delm juego
	 * Entradas: -
	 * Salidas: -
	 * Restricciones: -
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
	
	
	//Funci�n main, lo primero que se ejecuta del programa
	public static void main(String[] args) throws IOException {
		launch(args);
	}
}
