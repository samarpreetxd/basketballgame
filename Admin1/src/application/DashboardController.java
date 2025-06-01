/**
 * Sample Skeleton for 'classificaTpsi.fxml' Controller Class
 */

package application;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ResourceBundle;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class DashboardController {

    @FXML 
    private ResourceBundle resources;

    @FXML 
    private URL location;

    @FXML 
    private TextArea areaPosizione; 

    @FXML 
    private TextArea areaNomeUtente; 

    @FXML 
    private TextArea areaPunteggio;

    @FXML 
    private TextField txtUtenteDaEliminare; 

    @FXML 
    private Button btnElimina;

    @FXML
    void eliminaUtente(ActionEvent event) {
        String username = txtUtenteDaEliminare.getText().trim();
        if (username.isEmpty()) {
            return;
        }
        
        try {
            URL url = new URL("http://localhost:8080/ServletUtente/ServletUtente");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");

            JSONObject json = new JSONObject();
            json.put("username", username);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = json.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }

            JSONObject jsonResponse = new JSONObject(response.toString());
            if (jsonResponse.getString("status").equals("success")) {
            	Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Errore");
                alert.setHeaderText(null);
                alert.setContentText("Utente eliminato con successo.");
                alert.showAndWait();
            	caricaClassifica(); // Ricarica la classifica dopo l'eliminazione
                txtUtenteDaEliminare.setText("");
            } else if (jsonResponse.getString("status").equals("failure")) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Errore");
                alert.setHeaderText(null);
                alert.setContentText("L'utente non esiste nel database.");
                alert.showAndWait();
            }

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML 
    void initialize() {
        assert areaPosizione != null : "fx:id=\"areaPosizione\" was not injected: check your FXML file 'classificaTpsi.fxml'.";
        assert areaNomeUtente != null : "fx:id=\"areaNomeUtente\" was not injected: check your FXML file 'classificaTpsi.fxml'.";
        assert areaPunteggio != null : "fx:id=\"areaPunteggio\" was not injected: check your FXML file 'classificaTpsi.fxml'.";
        assert txtUtenteDaEliminare != null : "fx:id=\"txtUtenteDaEliminare\" was not injected: check your FXML file 'classificaTpsi.fxml'.";
        assert btnElimina != null : "fx:id=\"btnElimina\" was not injected: check your FXML file 'classificaTpsi.fxml'.";
        caricaClassifica();
    }
    
    private void caricaClassifica() {
        try {
            URL url = new URL("http://localhost:8080/GestoreAdmin/GestoreAdmin");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            
            JSONArray classifica = new JSONArray(response.toString());
            StringBuilder posizioni = new StringBuilder();
            StringBuilder nomi = new StringBuilder();
            StringBuilder punteggi = new StringBuilder();
            
            for (int i = 0; i < classifica.length(); i++) {
                JSONObject utente = classifica.getJSONObject(i);
                posizioni.append((i + 1) + "\n");
                nomi.append(utente.getString("username") + "\n");
                punteggi.append(utente.getInt("punteggio") + "\n");
            }
            
            areaPosizione.setText(posizioni.toString());
            areaNomeUtente.setText(nomi.toString());
            areaPunteggio.setText(punteggi.toString());
            
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            areaPosizione.setText("Errore");
            areaNomeUtente.setText("Errore");
            areaPunteggio.setText("Errore");
        }
    }
}