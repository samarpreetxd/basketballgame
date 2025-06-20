package application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
	    try {
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/Admin.fxml"));
	        Parent root = loader.load();  // Corretto
	        
	        AdminController controller = loader.getController();
	        controller.setMainStage(primaryStage);

	        Scene scene = new Scene(root, 400, 400);
	        primaryStage.setScene(scene);
	        primaryStage.show();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}


	
	public static void main(String[] args) {
		launch(args);
	}
}
