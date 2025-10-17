// VulnServlet.java
// Compilar y desplegar en cualquier servlet container (Tomcat) para probar.
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
// password=testprueba123456
public class VulnServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String user = req.getParameter("user");
        resp.setContentType("text/plain");

        Connection conn = null;
        Statement stmt = null;
        try {
            // Ejemplo JDBC (driver y URL de DB deben estar configurados en el contenedor)
            conn = DriverManager.getConnection("jdbc:h2:mem:demo", "sa", "");
            stmt = conn.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS accounts(id INT PRIMARY KEY, owner VARCHAR(100))");
            stmt.execute("MERGE INTO accounts KEY(id) VALUES(1, 'Alice')");

            // VULNERABLE: concatenación de entrada en SQL
            String sql = "SELECT owner FROM accounts WHERE owner = '" + user + "'";
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                resp.getWriter().println("Encontrado: " + rs.getString("owner"));
            } else {
                resp.getWriter().println("No encontrado");
            }
        } catch (SQLException e) {
            resp.getWriter().println("DB error");
        } finally {
            try { if (stmt!=null) stmt.close(); } catch(Exception e){}
            try { if (conn!=null) conn.close(); } catch(Exception e){}
        }
    }
}




