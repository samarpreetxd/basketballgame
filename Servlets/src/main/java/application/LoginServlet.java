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

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Legge il JSON inviato dal client
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        JSONObject jsonRequest = new JSONObject(sb.toString());
        String username = jsonRequest.getString("username");
        String password = jsonRequest.getString("password");

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/miodb", "asd", "dsa");

            // Seleziona solo la password hashata dal database
            PreparedStatement stmt = conn.prepareStatement("SELECT password FROM utenti WHERE username=?");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHashedPassword = rs.getString("password");

                // Verifica la password con BCrypt
                if (BCrypt.checkpw(password, storedHashedPassword)) {
                    jsonResponse.put("status", "success");
                    jsonResponse.put("message", "Login riuscito");
                } else {
                    jsonResponse.put("status", "failure");
                    jsonResponse.put("message", "Username o password errati");
                }
            } else {
                jsonResponse.put("status", "failure");
                jsonResponse.put("message", "Username o password errati");
            }

            conn.close();
        } catch (Exception e) {
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Errore nel server: " + e.getMessage());
            e.printStackTrace();
        }

        out.print(jsonResponse.toString());
        out.flush();
    }
}
