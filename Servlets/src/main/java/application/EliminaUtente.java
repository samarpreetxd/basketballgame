package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

@WebServlet("/ServletUtente")
public class EliminaUtente extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/miodb";
    private static final String JDBC_USER = "asd";
    private static final String JDBC_PASSWORD = "dsa";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();

        try {
            BufferedReader reader = request.getReader();
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            if (sb.length() == 0) {
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Richiesta vuota");
                out.print(jsonResponse.toString());
                out.flush();
                return;
            }

            JSONObject jsonRequest = new JSONObject(sb.toString());
            String username = jsonRequest.getString("username");

            if (username.isEmpty()) {
                jsonResponse.put("status", "failure");
                jsonResponse.put("message", "Username obbligatorio");
            } else {
                Class.forName("com.mysql.cj.jdbc.Driver");
                try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
                    // Elimina l'utente dalla tabella punteggio (chiave esterna)
                    PreparedStatement deleteScoreStmt = conn.prepareStatement("DELETE FROM punteggio WHERE id IN (SELECT id FROM utenti WHERE username = ?)");
                    deleteScoreStmt.setString(1, username);
                    deleteScoreStmt.executeUpdate();
                    
                    // Elimina l'utente dalla tabella utenti
                    PreparedStatement deleteUserStmt = conn.prepareStatement("DELETE FROM utenti WHERE username = ?");
                    deleteUserStmt.setString(1, username);
                    int rowsDeleted = deleteUserStmt.executeUpdate();

                    if (rowsDeleted > 0) {
                        jsonResponse.put("status", "success");
                        jsonResponse.put("message", "Utente eliminato con successo");
                    } else {
                        jsonResponse.put("status", "failure");
                        jsonResponse.put("message", "Utente non trovato");
                    }
                }
            }
        } catch (Exception e) {
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Errore nel server: " + e.getMessage());
            e.printStackTrace();
        }

        out.print(jsonResponse.toString());
        out.flush();
    }
}