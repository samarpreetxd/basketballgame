package src.application;

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
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ControllerL {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="txtNome"
    private TextField txtNome; // Value injected by FXMLLoader

    @FXML // fx:id="txtPsw"
    private TextField txtPsw; // Value injected by FXMLLoader

    @FXML // fx:id="btnLog"
    private Button btnLog; // Value injected by FXMLLoader

    @FXML // fx:id="btnSign"
    private Button btnSign; // Value injected by FXMLLoader
   
    private Stage stage;

    public void setMainStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    void Login(ActionEvent event) {
    	String username = txtNome.getText(); // Modifica con un utente esistente
    	String password = txtPsw.getText(); // Modifica con la password corretta

        try {
            URL url = new URL("http://localhost:8080/LoginApp/LoginServlet");
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

            // Converte la risposta JSON
            JSONObject jsonResponse = new JSONObject(response.toString());
            if (jsonResponse.getString("status").equals("success")) {
            	try {
                    Parent secondScene = FXMLLoader.load(getClass().getResource("ProgettoTPSI.fxml"));
                    stage.getScene().setRoot(secondScene);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Login fallito.");
            }


            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert txtNome != null : "fx:id=\"txtNome\" was not injected: check your FXML file 'LogIn.fxml'.";
        assert txtPsw != null : "fx:id=\"txtPsw\" was not injected: check your FXML file 'LogIn.fxml'.";
        assert btnLog != null : "fx:id=\"btnLog\" was not injected: check your FXML file 'LogIn.fxml'.";
        assert btnSign != null : "fx:id=\"btnSign\" was not injected: check your FXML file 'LogIn.fxml'.";

    }
}