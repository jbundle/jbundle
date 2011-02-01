package example1.impl;

import example1.UserAuthService;

import javax.servlet.ServletException;
// import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

// @WebServlet(urlPatterns = "/registration1")
public class RegistrationServlet extends HttpServlet
{
    UserAuthService userAuthService;

    public void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, java.io.IOException
    {
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        out.println("<HTML> <HEAD> <TITLE> Registration " +
                "</TITLE> </HEAD> <BODY BGCOLOR=white>");

        String name = req.getParameter("name");
        String password = req.getParameter("password");
        try
        {

            if (userAuthService.register(name, password)) {
                out.println("Registered " + name);
            } else {
                out.println("Failed to register " + name);
            }
        }
        catch (Exception e)
        {
            out.println("Failed to register " + name);
        }
        out.println("</BODY> </HTML> ");

    }
}
