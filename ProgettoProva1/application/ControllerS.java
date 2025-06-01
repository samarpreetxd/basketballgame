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
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class ControllerS {

    @FXML
    private TextField txtNome; 

    @FXML // fx:id="txtPsw"
    private PasswordField txtPsw; // Value injected by FXMLLoader

    @FXML
    private Button btnSign; 

    private Stage stage;

    public void setMainStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    void Sign(ActionEvent event) {
        String username = txtNome.getText();
        String password = txtPsw.getText();

        if (username.isEmpty() || password.isEmpty()) {
            mostraMessaggio("Errore", "Inserisci username e password!");
            return;
        }

        try {
            URL url = new URL("http://localhost:8080/RegisterServlet/RegisterServlet");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            // Crea il JSON
            JSONObject json = new JSONObject();
            json.put("username", username);
            json.put("password", password);

            // Invia il JSON alla servlet
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = json.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Legge la risposta
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }

            // Parsing JSON
            JSONObject jsonResponse = new JSONObject(response.toString());
            if (jsonResponse.getString("status").equals("success")) {
                
                // Cambia scena e passa l'utente a Controller
                cambiaScena(username);
            } else {
            	Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Registrazione");
                alert.setHeaderText(null);
                alert.setContentText("Registrazione non avvenuta, ritenta");
                alert.showAndWait();
            }

            conn.disconnect();
        } catch (Exception e) {
            System.out.println("Errore di connessione o JSON!");
            e.printStackTrace();
        }
    }

    private void cambiaScena(String username) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ProgettoTPSI.fxml"));
            Parent secondScene = loader.load();
            
            // Ottieni il Controller della nuova scena e imposta l'utente
            Controller controller = loader.getController();
            controller.setUtenteCorrente(username);
            
            // Cambia scena
            Stage stage = (Stage) btnSign.getScene().getWindow();
            stage.setScene(new Scene(secondScene));
            stage.show();
        } catch (Exception e) {
            System.out.println("Errore nel cambio scena!");
            e.printStackTrace();
        }
    }

    private void mostraMessaggio(String titolo, String messaggio) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }

    @FXML
    void initialize() {
        assert txtNome != null : "fx:id=\"txtNome\" non è stato iniettato!";
        assert txtPsw != null : "fx:id=\"txtPsw\" was not injected: check your FXML file 'SignIn.fxml'.";
        assert btnSign != null : "fx:id=\"btnSign\" non è stato iniettato!";
    }
}
