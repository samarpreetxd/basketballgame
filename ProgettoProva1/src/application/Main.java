package src.application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	@Override
	public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ProgettoTPSI.fxml"));
        Parent root = loader.load();

        Controller controller = loader.getController();
        controller.setMainStage(stage);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Cambio Pagina JavaFX");
        stage.show();
    }
	
	public static void main(String[] args) {
		launch(args);
	}
}
