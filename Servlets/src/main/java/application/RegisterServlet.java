package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/miodb";
    private static final String JDBC_USER = "asd";
    private static final String JDBC_PASSWORD = "dsa";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JSONObject jsonResponse = new JSONObject();
        PrintWriter out = response.getWriter();//scrive risposta al client

        try {
            BufferedReader reader = request.getReader();
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            if (sb.length() == 0) {//se richiesta vuota --> errore
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Richiesta vuota");
                out.print(jsonResponse.toString());
                out.flush();//per scrivere i dati immediatamente e svuotare il buffer
                return;
            }

            JSONObject jsonRequest = new JSONObject(sb.toString());
            String username = jsonRequest.getString("username");
            String password = jsonRequest.getString("password");

            if (username.isEmpty() || password.isEmpty()) {
                jsonResponse.put("status", "failure");
                jsonResponse.put("message", "Username e password obbligatori");
            } else {
                Class.forName("com.mysql.cj.jdbc.Driver");//carica il driver del db
                try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {

                    PreparedStatement checkStmt = conn.prepareStatement("SELECT id FROM utenti WHERE username = ?");
                    checkStmt.setString(1, username);//controllo se l'utente è gia registrato
                    ResultSet rs = checkStmt.executeQuery();

                    if (rs.next()) {
                        jsonResponse.put("status", "failure");
                        jsonResponse.put("message", "Username già in uso");
                    } else {
                        // Inserisce l'utente
                        PreparedStatement stmt = conn.prepareStatement("INSERT INTO utenti (username, password) VALUES (?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
                        stmt.setString(1, username);
                        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
                        stmt.setString(2, hashedPassword);
                        int rowsInserted = stmt.executeUpdate();

                        if (rowsInserted > 0) {
                            ResultSet generatedKeys = stmt.getGeneratedKeys();
                            if (generatedKeys.next()) {
                                int userId = generatedKeys.getInt(1);

                                // Inizializza il punteggio dell'utente a 0
                                PreparedStatement scoreStmt = conn.prepareStatement("INSERT INTO punteggio (id, punteggio) VALUES (?, 0)");
                                scoreStmt.setInt(1, userId);
                                scoreStmt.executeUpdate();
                            }

                            jsonResponse.put("status", "success");
                            jsonResponse.put("message", "Registrazione completata");
                        } else {
                            jsonResponse.put("status", "failure");
                            jsonResponse.put("message", "Errore nella registrazione");
                        }
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
