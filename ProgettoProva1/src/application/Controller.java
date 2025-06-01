package src.application;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.PauseTransition;


import org.json.JSONObject;

public class Controller {

    @FXML
    private ResourceBundle resources;
    @FXML
    private Button btn1, btn2, btn3;
    @FXML
    private ImageView imgDentro, imgFuori;

    private final String SERVLET_URL = "http://localhost:8080/GestoreTiri/GestoreTiri?tipoTiro=";
    private Stage stage;

    public void setMainStage(Stage stage) {
        this.stage = stage;
    }
    @FXML
    void tira1(ActionEvent event) {
        eseguiTiro(1);
    }

    @FXML
    void tira2(ActionEvent event) {
        eseguiTiro(2);
    }

    @FXML
    void tira3(ActionEvent event) {
        eseguiTiro(3);
    }

    private void eseguiTiro(int tipoTiro) {
        Task<Void> task = new Task() {
            @Override
            protected Void call() throws Exception {
                try {
                    URL url = new URL(SERVLET_URL + tipoTiro);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");

                    int responseCode = conn.getResponseCode();
                    System.out.println("HTTP Response Code: " + responseCode);

                    if (responseCode != 200) {
                        updateMessage("Errore nella richiesta");
                        return null;
                    }

                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String response = in.readLine();
                    in.close();

                    System.out.println("Risposta dalla servlet: " + response);

                    JSONObject jsonResponse = new JSONObject(response);
                    boolean segnato = jsonResponse.getBoolean("segnato");

                    updateMessage(segnato ? "Canestro!" : "Sbagliato!");

                } catch (Exception e) {
                    e.printStackTrace();
                    updateMessage("Errore di connessione");
                }

                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                if (getMessage().equals("Canestro!")) {
                    imgDentro.setFitWidth(601);
                    imgDentro.setFitHeight(406);
                } else if (getMessage().equals("Sbagliato!")) {
                    imgFuori.setFitWidth(601);
                    imgFuori.setFitHeight(397);
                }

                // Dopo 2 secondi, resetta le immagini
                PauseTransition pause = new PauseTransition(Duration.seconds(2));
                pause.setOnFinished(event -> {
                    imgDentro.setFitWidth(1);
                    imgDentro.setFitHeight(1);
                    imgFuori.setFitWidth(1);
                    imgFuori.setFitHeight(1);
                });
                pause.play();
            }

        };


        new Thread(task).start();
    }
}
