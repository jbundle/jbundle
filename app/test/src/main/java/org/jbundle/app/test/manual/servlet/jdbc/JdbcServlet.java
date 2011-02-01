package org.jbundle.app.test.manual.servlet.jdbc;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

public class JdbcServlet extends HttpServlet {

  public void doGet (HttpServletRequest req,
                     HttpServletResponse res)
    throws ServletException, IOException
  {
    PrintWriter out = res.getWriter();
    
    String strX = req.getParameter("x");
    String strY = req.getParameter("y");
    String strName = req.getParameter("name");

//    out.println("Hello, " + strX + " world! " + strY);
    out.println("<html>");

    out.println("<head>");
    out.println("</head>");
    out.println("<body>");
    
    out.println("Hello");

    
    try {
    /*
     * Create a JNDI Initial context to be able to
     *  lookup  the DataSource
     *
     * In production-level code, this should be cached as
     * an instance or static variable, as it can
     * be quite expensive to create a JNDI context.
     *
     * Note: This code only works when you are using servlets
     * or EJBs in a J2EE application server. If you are
     * using connection pooling in standalone Java code, you
     * will have to create/configure datasources using whatever
     * mechanisms your particular connection pooling library
     * provides.
     */

    InitialContext ctx = new InitialContext();

     /*
      * Lookup the DataSource, which will be backed by a pool
      * that the application server provides. DataSource instances
      * are also a good candidate for caching as an instance
      * variable, as JNDI lookups can be expensive as well.
      */

    DataSource ds = 
      (DataSource)ctx.lookup("jdbc/test");

    /*
     * The following code is what would actually be in your
     * Servlet, JSP or EJB 'service' method...where you need
     * to work with a JDBC connection.
     */

    Connection conn = null;
    Statement stmt = null;

    try {
        conn = ds.getConnection();

        /*
         * Now, use normal JDBC programming to work with
         * MySQL, making sure to close each resource when you're
         * finished with it, which allows the connection pool
         * resources to be recovered as quickly as possible
         */

        stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select * from TestTable");
        while (rs.next())
        {
            String name = rs.getString("TestName");
            out.println("Success" + name);
        }

        stmt.close();
        stmt = null;

        conn.close();
        conn = null;
    } finally {
        /*
         * close any jdbc instances here that weren't
         * explicitly closed during normal code path, so
         * that we don't 'leak' resources...
         */

        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException sqlex) {
                // ignore -- as we can't do anything about it here
            }

            stmt = null;
        }

        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException sqlex) {
                // ignore -- as we can't do anything about it here
            }

            conn = null;
        }
    }

    } catch (Exception ex) {
        ex.printStackTrace();
    }
  
    out.println("</body>");
    
    out.println("</html>");
    
    
    out.close();
  }
}