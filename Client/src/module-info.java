module Client {
	requires javafx.controls;
	requires com.google.gson;
	
	opens application to javafx.graphics, javafx.fxml;
}
