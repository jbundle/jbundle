package org.jbundle.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This is a convenience class, so users don't have to remember the path to RemoteSessionServer.
 * Note: DO NOT reference this class as it doesn't have an OSGi home
 */
public class Servlet extends org.jbundle.base.screen.control.servlet.html.HTMLServlet
{
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException
	{
		super.doGet(req, res);
	}

}
