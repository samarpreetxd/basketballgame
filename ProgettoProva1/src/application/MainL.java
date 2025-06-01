package src.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainL extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("LogIn.fxml"));
        Parent root = loader.load();

        ControllerL controller = loader.getController();
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
