package application;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;

/**
 * Sample Skeleton for 'LogIn.fxml' Controller Class
 */

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

import org.json.JSONObject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class ControllerL {

    @FXML 
    private ResourceBundle resources;

    @FXML 
    private URL location;

    @FXML 
    private TextField txtNome; 

    @FXML // fx:id="txtPsw"
    private PasswordField txtPsw; // Value injected by FXMLLoader

    @FXML 
    private Button btnLog; 

    @FXML 
    private Button btnSign;
   
    private Stage stage;

    public void setMainStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    void Login(ActionEvent event) {
        String username = txtNome.getText().trim();
        String password = txtPsw.getText().trim();

        // Controlla se uno dei campi è vuoto
        if (username.isEmpty() || password.isEmpty()) {
            mostraAlert(AlertType.WARNING, "Campi Vuoti", "Inserisci username e password.");
            return;
        }

        try {
            URL url = new URL("http://localhost:8080/LoginServlet/LoginServlet");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");

            // Crea il JSON da inviare
            JSONObject json = new JSONObject();
            json.put("username", username);
            json.put("password", password);

            // Invia il JSON alla servlet
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = json.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Legge la risposta dalla servlet
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }

            JSONObject jsonResponse = new JSONObject(response.toString());

            if (jsonResponse.getString("status").equals("success")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ProgettoTPSI.fxml"));
                Parent secondScene = loader.load();
                
                Controller controller = loader.getController();
                controller.setUtenteCorrente(username);
                
                // Cambia scena
                Stage stage = (Stage) btnSign.getScene().getWindow();
                stage.setScene(new Scene(secondScene));
                stage.show();
            } else {
                mostraAlert(AlertType.ERROR, "Login Fallito", "Username o password errati.");
            }

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            mostraAlert(AlertType.ERROR, "Errore", "Si è verificato un errore durante il login.");
        }
    }

    // Metodo per mostrare gli alert
    private void mostraAlert(AlertType type, String titolo, String messaggio) {
        Alert alert = new Alert(type);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }



    @FXML
    void Sign(ActionEvent event) {
    	try {
            Parent secondScene = FXMLLoader.load(getClass().getResource("SignIn.fxml"));
            stage.getScene().setRoot(secondScene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML 
    void initialize() {
        assert txtNome != null : "fx:id=\"txtNome\" was not injected: check your FXML file 'LogIn.fxml'.";
        assert txtPsw != null : "fx:id=\"txtPsw\" was not injected: check your FXML file 'LogIn.fxml'.";
        assert btnLog != null : "fx:id=\"btnLog\" was not injected: check your FXML file 'LogIn.fxml'.";
        assert btnSign != null : "fx:id=\"btnSign\" was not injected: check your FXML file 'LogIn.fxml'.";

    }
}