package application;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.PauseTransition;
import org.json.JSONObject;

public class Controller {
    @FXML
    private Button btn1, btn2, btn3;
    @FXML
    private ImageView imgDentro, imgFuori, imgCampo;
    private int maxTiri = 0;
    private final String SERVLET_URL = "http://localhost:8080/GestoreTiri/GestoreTiri";
    private Stage stage;
    private String utenteCorrente; // Utente loggato

    public void setMainStage(Stage stage) {
        this.stage = stage;
    }

    public void setUtenteCorrente(String utente) {
        this.utenteCorrente = utente;
        System.out.println("Utente loggato: " + utenteCorrente);
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
        if (utenteCorrente == null) {
        	Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Gioco");
            alert.setHeaderText(null);
            alert.setContentText("Utente non loggato!!!");
            alert.showAndWait();
            btn1.setDisable(true);
            btn3.setDisable(true);
            btn2.setDisable(true);
            return;
        }
        if(maxTiri > 4) {
        	Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Gioco");
            alert.setHeaderText(null);
            alert.setContentText("Hai raggiunto il massimo di tiri");
            alert.showAndWait();
            btn1.setDisable(true);
            btn3.setDisable(true);
            btn2.setDisable(true);
            return;
        }
        maxTiri++;
        Task<Void> task = new Task() {
            @Override
            protected Void call() {
                try {
                    String requestUrl = SERVLET_URL + "?tipoTiro=" + tipoTiro + "&username=" + utenteCorrente;
                    System.out.println("Invio richiesta a: " + requestUrl);

                    URL url = new URL(requestUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");

                    int responseCode = conn.getResponseCode();
                    System.out.println("HTTP Response Code: " + responseCode);

                    if (responseCode != 200) {
                        System.out.println("Errore: risposta non valida dalla servlet!");
                        return null;
                    }

                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String response = in.readLine();
                    in.close();

                    JSONObject jsonResponse = new JSONObject(response);
                    boolean segnato = jsonResponse.getBoolean("segnato");

                    System.out.println(segnato ? "Punto segnato" : "Punto non segnato");

                    updateMessage(segnato ? "Canestro!" : "Sbagliato!");

                } catch (Exception e) {
                    System.out.println("Errore di connessione con la servlet!");
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                if (getMessage().equals("Canestro!")) {
                    imgDentro.setFitWidth(601);
                    imgDentro.setFitHeight(406);
                    imgCampo.setFitHeight(1);
                    imgCampo.setFitWidth(1);
                } else if (getMessage().equals("Sbagliato!")) {
                    imgFuori.setFitWidth(601);
                    imgFuori.setFitHeight(397);
                    imgCampo.setFitHeight(1);
                    imgCampo.setFitWidth(1);
                }

                // Dopo 2 secondi, resetta le immagini
                PauseTransition pause = new PauseTransition(Duration.seconds(2));
                pause.setOnFinished(event -> {
                    imgDentro.setFitWidth(1);
                    imgDentro.setFitHeight(1);
                    imgFuori.setFitWidth(1);
                    imgFuori.setFitHeight(1);
                    imgCampo.setFitWidth(874);
                    imgCampo.setFitHeight(401);
                });
                pause.play();
            }
        };

        new Thread(task).start();
    }
    @FXML 
    void initialize() {
    	assert imgCampo != null : "fx:id=\"imgCampo\" was not injected: check your FXML file 'ProgettoTPSI.fxml'.";
        assert btn3 != null : "fx:id=\"btn3\" was not injected: check your FXML file 'ProgettoTPSI.fxml'.";
        assert btn2 != null : "fx:id=\"btn2\" was not injected: check your FXML file 'ProgettoTPSI.fxml'.";
        assert btn1 != null : "fx:id=\"btn1\" was not injected: check your FXML file 'ProgettoTPSI.fxml'.";
        assert imgDentro != null : "fx:id=\"imgDentro\" was not injected: check your FXML file 'ProgettoTPSI.fxml'.";
        assert imgFuori != null : "fx:id=\"imgFuori\" was not injected: check your FXML file 'ProgettoTPSI.fxml'.";

    }
}
