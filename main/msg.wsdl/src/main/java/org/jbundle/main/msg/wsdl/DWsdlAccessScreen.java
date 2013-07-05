/**
 * @(#)DWsdlAccessScreen.
 * Copyright © 2013 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.msg.wsdl;

import java.util.*;

import org.jbundle.base.db.*;
import org.jbundle.thin.base.util.*;
import org.jbundle.thin.base.db.*;
import org.jbundle.base.db.event.*;
import org.jbundle.base.db.filter.*;
import org.jbundle.base.field.*;
import org.jbundle.base.field.convert.*;
import org.jbundle.base.field.event.*;
import org.jbundle.base.screen.model.*;
import org.jbundle.base.screen.model.util.*;
import org.jbundle.base.model.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.jbundle.base.screen.view.data.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import org.jbundle.base.screen.control.servlet.*;

/**
 *  DWsdlAccessScreen - .
 */
public class DWsdlAccessScreen extends DDataAccessScreen
{
    public static final String WSDL_VERSION = "wsdlversion";
    /**
     * Default constructor.
     */
    public DWsdlAccessScreen()
    {
        super();
    }
    /**
     * DWsdlAccessScreen Method.
     */
    public DWsdlAccessScreen(ScreenField model, boolean bEditableControl)
    {
        this();
        this.init(model, bEditableControl);
    }
    /**
     * Initialize class fields.
     */
    public void init(ScreenField model, boolean bEditableControl)
    {
        super.init(model, bEditableControl);
    }
    /**
     * Process an HTML get or post.
     * You must override this method.
     * @param req The servlet request.
     * @param res The servlet response object.
     * @exception ServletException From inherited class.
     * @exception IOException From inherited class.
     */
    public void sendData(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        res.setContentType("text/xml");
        PrintWriter out = res.getWriter();
        this.printXML(req, out);
        out.flush();
    }
    /**
     * PrintXML Method.
     */
    public void printXML(HttpServletRequest req, PrintWriter out)
    {
        ServletTask task = (ServletTask)this.getTask();
        Map<String,Object> properties = task.getRequestProperties(task.getServletRequest(), true);
        
        CreateWSDL wsdl = null;
        if ("2.0".equalsIgnoreCase(this.getProperty(WSDL_VERSION)))
            wsdl = new CreateWSDL20(task, null, properties);    // Default
        else    // 1.1 Is the default for now
            wsdl = new CreateWSDL11(task, null, properties);
        Object data = wsdl.createMarshallableObject();
        String xml = wsdl.getXML(data);
        out.println(xml);
        wsdl.free();
    }

}
