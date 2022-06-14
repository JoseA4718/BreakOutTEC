package application.SpectClient;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Spect extends Thread{
	//Constantes del juego
	private static final Integer WIDTH = 800;
	private static final Integer HEIGHT = 600;
	private static final Integer PLAYERY = 500;
	
	Integer score = 0;
	Text scoreLabel = new Text("Score: " + Integer.toString(this.score));
	//Text livesLabel = new Text("Lives: " + Integer.toString(this.lives));
	//Text levelLabel = new Text("Level: " + Integer.toString(this.level));

	public void display(String title, String message) {
		
		Stage window = new Stage();
		
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle(title);
		
		Label label = new Label();
		label.setText(message);
		Button confirmationButton = new Button("OK");
		confirmationButton.setOnAction(e -> window.close());
		
		BorderPane layout = new BorderPane();
		layout.setTop(scoreLabel);;
		layout.setMinSize(WIDTH, HEIGHT);

		Scene scene = new Scene(layout);
		window.setScene(scene);
		window.show();
		
	}
	
	public void run() {
		while(true) {
			esperar(1);
			this.score += 100;
			scoreLabel.setText("Score: " + Integer.toString(this.score));
		}
	}
	
	public static void esperar(int segundos){
        try {
            Thread.sleep(segundos * 1000);
         } catch (Exception e) {
            System.out.println(e);
         }
    }
}
