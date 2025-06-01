package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/GestoreAdmin")
public class GestoreAdmin extends HttpServlet {//questa classe gestisce la classifica
    private static final long serialVersionUID = 1L;
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        JSONArray classifica = new JSONArray();
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/miodb", "asd", "dsa");
            
            String query = "SELECT utenti.username, punteggio.punteggio FROM utenti, punteggio WHERE utenti.id = punteggio.id ORDER BY punteggio.punteggio DESC";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                JSONObject utente = new JSONObject();
                utente.put("username", rs.getString("username"));
                utente.put("punteggio", rs.getInt("punteggio"));
                classifica.put(utente);
            }
            
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        
        response.getWriter().write(classifica.toString());
    }
}
