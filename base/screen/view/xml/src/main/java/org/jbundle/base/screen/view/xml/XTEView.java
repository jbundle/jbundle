/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.view.xml;

/**
 * @(#)ScreenField.java   0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 tourapp.com. All Rights Reserved.
 *      don@tourgeek.com
 */
import java.io.PrintWriter;

import org.jbundle.base.field.HtmlField;
import org.jbundle.base.field.XMLPropertiesField;
import org.jbundle.base.field.XmlField;
import org.jbundle.base.screen.model.ScreenField;
import org.jbundle.model.DBException;
import org.jbundle.model.screen.ScreenComponent;


/**
 * ScreenField - This is the information which tells the system about a field on the
 *  screen... where it is, what type it is, and the file and field position.
 */
public class XTEView extends XEditText
{

    /**
     * Constructor.
     */
    public XTEView()
    {
        super();
    }
    /**
     * Constructor.
     */
    public XTEView(ScreenField model,boolean bEditableControl)
    {
        this();
        this.init(model, bEditableControl);
    }
    /**
     * Constructor.
     */
    public void init(ScreenComponent model, boolean bEditableControl)
    {
        super.init(model, bEditableControl);
    }
    /**
     * Free.
     */
    public void free()
    {
        super.free();
    }
    /**
     * display this field in html input format.
     */
    public void printInputControl(PrintWriter out, String strFieldDesc, String strFieldName, String strSize, String strMaxSize, String strValue, String strControlType, String strFieldType)
    {
        out.println(" <xfm:" + strControlType + " xform=\"form1\" ref=\"" + strFieldName + "\" cols=\"50\" rows=\"6\" type=\"" + strFieldType + "\">");
        out.println("   <xfm:caption>" + strFieldDesc + "</xfm:caption>");
        out.println(" </xfm:" + strControlType + ">");
    }
    /**
     * Print this field's data in XML format.
     * @return true if default params were found for this form.
     * @param out The http output stream.
     * @exception DBException File exception.
     */
    public boolean printData(PrintWriter out, int iPrintOptions)
    {
        if ((this.getScreenField().getConverter().getField() instanceof XmlField)
            || (this.getScreenField().getConverter().getField() instanceof HtmlField)
            || (this.getScreenField().getConverter().getField() instanceof XMLPropertiesField))
        {
            boolean bFieldsFound = false;
            String strFieldName = this.getScreenField().getSFieldParam();
            // Do NOT encode the data!
            String strFieldData = this.getScreenField().getSFieldValue(true, false);
            out.println("    <" + strFieldName + '>' + strFieldData + "</" + strFieldName + '>');
            return bFieldsFound;
        }
        else
            return super.printData(out, iPrintOptions);
    }
}
