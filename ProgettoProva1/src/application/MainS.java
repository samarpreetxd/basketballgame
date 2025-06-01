package src.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainS extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("SignIn.fxml"));
        Parent root = loader.load();

        ControllerS controller = loader.getController();
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
