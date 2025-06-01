package application;

import java.io.IOException;
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
import java.util.Random;

@WebServlet("/GestoreTiri")
public class GestoreTiri extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Random random = new Random();

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/miodb";
    private static final String JDBC_USER = "asd";
    private static final String JDBC_PASSWORD = "dsa";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String tipoTiroStr = request.getParameter("tipoTiro");

        if (username == null || tipoTiroStr == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametri mancanti");
            return;
        }

        int tipoTiro;
        try {
            tipoTiro = Integer.parseInt(tipoTiroStr);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametro non valido");
            return;
        }

        boolean segnato = false;
        int puntiGuadagnati = 0;

        if (tipoTiro == 1) {
            segnato = random.nextInt(3) < 2; // 66%
            if (segnato) {
                puntiGuadagnati += 1;
            }
        } else if (tipoTiro == 2) {
            segnato = random.nextInt(6) < 3; // 50%
            if (segnato) {
                puntiGuadagnati += 2;
            }
        } else if (tipoTiro == 3) {
            segnato = random.nextInt(9) < 3; // 33%
            if (segnato) {
                puntiGuadagnati += 3;
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Tipo di tiro non valido");
            return;
        }

        int punteggioTotale = aggiornaPunteggio(username, puntiGuadagnati);

        JSONObject json = new JSONObject();
        json.put("tipoTiro", tipoTiro);
        json.put("segnato", segnato);
        json.put("punteggio", punteggioTotale);

        response.setContentType("application/json");
        response.getWriter().write(json.toString());
    }

    private int aggiornaPunteggio(String username, int puntiGuadagnati) {
        int punteggioAttuale = 0;

        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            PreparedStatement stmtFind = conn.prepareStatement("SELECT id FROM utenti WHERE username = ?");
            stmtFind.setString(1, username);
            ResultSet rs = stmtFind.executeQuery();

            if (!rs.next()) {
                return -1;
            }

            int userId = rs.getInt("id");

            PreparedStatement stmtControllo = conn.prepareStatement("SELECT punteggio FROM punteggio WHERE id = ?");
            stmtControllo.setInt(1, userId);
            ResultSet rsPunteggio = stmtControllo.executeQuery();

            if (rsPunteggio.next()) {
                punteggioAttuale = rsPunteggio.getInt("punteggio") + puntiGuadagnati;
                PreparedStatement stmtAggiornato = conn.prepareStatement("UPDATE punteggio SET punteggio = ? WHERE id = ?");
                stmtAggiornato.setInt(1, punteggioAttuale);
                stmtAggiornato.setInt(2, userId);
                stmtAggiornato.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return punteggioAttuale;
    }
}
