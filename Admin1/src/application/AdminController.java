package application;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
import javafx.stage.Stage;

public class AdminController {

    @FXML
    private Button btnLogin;

    @FXML
    private PasswordField pwdPassword;

    @FXML
    private TextField txtNomeUtente;
    
    private Stage stage;

    public void setMainStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    void login(ActionEvent event) {
        String username = txtNomeUtente.getText().trim();
        String password = pwdPassword.getText().trim();
        
        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Errore", "I campi non possono essere vuoti.", Alert.AlertType.ERROR);
            return;
        }
        
        try {
            URL url = new URL("http://localhost:8080/LoginAdmin/LoginAdmin");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");

            JSONObject json = new JSONObject();
            json.put("username", username);
            json.put("password", password);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = json.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                br.close();
                
                JSONObject jsonResponse = new JSONObject(response.toString());
                conn.disconnect();
                
                if (jsonResponse.getString("status").equals("success")) {
                    showAlert("Successo", jsonResponse.getString("message"), Alert.AlertType.INFORMATION);
                    apriDashboard();
                } else {
                    showAlert("Errore", jsonResponse.getString("message"), Alert.AlertType.ERROR);
                }
            } else {
                showAlert("Errore", "Risposta del server non valida.", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            showAlert("Errore", "Errore nella connessione al server: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
    
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void apriDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("classificaTpsi.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            
            Stage dashboardStage = new Stage();
            dashboardStage.setScene(scene);
            dashboardStage.setTitle("Dashboard");
            dashboardStage.show();
            
            if (this.stage != null) {
                this.stage.close(); // Chiude la finestra di login
            }
        } catch (Exception e) {
            showAlert("Errore", "Impossibile aprire la Dashboard.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
}
