/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.app.test.manual.json;

import java.io.*;

import javax.servlet.http.*;
import javax.servlet.*;

import org.json.JSONException;
import org.json.JSONObject;

public class HelloServlet extends HttpServlet {
  public void doGet (HttpServletRequest req,
                     HttpServletResponse res)
    throws ServletException, IOException
  {
    PrintWriter out = res.getWriter();
    
    try {

    String strX = req.getParameter("x");
    String strY = req.getParameter("y");
    String strName = req.getParameter("name");

    JSONObject jsonX = new JSONObject(strX);
    String wife = jsonX.getString("wife");

//    out.println("Hello, " + strX + " world! " + strY);
    
    JSONObject jsonObj = new JSONObject();
        jsonObj.put("bookId", new Integer(23));
        jsonObj.put("title", strY);
        jsonObj.put("isbn", wife);
        jsonObj.put("author", strName);

        out.println(jsonObj.toString());
    } catch (JSONException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }

    
    out.close();
  }
}
