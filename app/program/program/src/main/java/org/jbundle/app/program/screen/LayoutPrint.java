/**
 * @(#)LayoutPrint.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.screen;

import java.awt.*;
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
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import java.io.*;
import org.jbundle.app.program.db.*;

/**
 *  LayoutPrint - Prints this layout in idl Corba format.
 */
public class LayoutPrint extends Object
{
    /**
     * Default constructor.
     */
    public LayoutPrint()
    {
        super();
    }
    /**
     * Constructor.
     */
    public LayoutPrint(Record recLayout, PrintWriter out)
    {
        this();
        this.init(recLayout, out);
    }
    /**
     * Init Method.
     */
    public void init(Record recLayout, PrintWriter out)
    {
        boolean bFirstTime = false;
        boolean bNewRecord = false;
        if (out == null)
        { // First time
            bFirstTime = true;
            if (recLayout == null)
            {
                recLayout = new Layout(Utility.getRecordOwner((Record)null));
                bNewRecord = true;
            }
            recLayout.getField(Layout.kID).setValue(1);
            try   {
                if (!recLayout.seek("="))
                { // Error - top level not found?
                }
            } catch   (DBException ex)  {
                ex.printStackTrace();
                return;
            }
            String strFileName = recLayout.getField(Layout.kName).toString() + ".idl";
            try   {
                FileOutputStream outStream = new FileOutputStream(strFileName);
                BufferedOutputStream buffOut = new BufferedOutputStream(outStream);
                out = new PrintWriter(buffOut);
            } catch (IOException ex)    {
                ex.printStackTrace();
                return;
            }
        }
            
        this.printIt(recLayout, out, 0, ";");
        
        if (bFirstTime)
            out.close();
        if (bNewRecord)
            recLayout.free();
    }
    /**
     * PrintIt Method.
     */
    public void printIt(Record recLayout, PrintWriter out, int iIndents, String strEnd)
    {
        // Print out the current record
        String strName = recLayout.getField(Layout.kName).toString();
        String strType = recLayout.getField(Layout.kType).toString();
        String strValue = recLayout.getField(Layout.kFieldValue).toString();
        String strReturns = recLayout.getField(Layout.kReturnsValue).toString();
        String strMax = recLayout.getField(Layout.kMax).toString();
        String strDescription = recLayout.getField(Layout.kComment).toString();
        boolean bLoop = false;
        if ((strType.equalsIgnoreCase("module")) ||
            (strType.equalsIgnoreCase("enum")) ||
            (strType.equalsIgnoreCase("struct")) ||
            (strType.equalsIgnoreCase("interface")))
                bLoop = true;
        if (bLoop)
        {
            this.println(out, strType + " " + strName, strDescription, iIndents, "");
            String strEndLoop = ";";
            if (strType.equalsIgnoreCase("enum"))
                strEndLoop = null;
            this.println(out, "{", null, iIndents, "");
            Layout recLayoutLoop = new Layout(Utility.getRecordOwner(recLayout));
            recLayoutLoop.setKeyArea(Layout.kParentFolderIDKey);
            recLayoutLoop.addListener(new SubFileFilter(recLayout));
            try   {
                boolean bFirstLoop = true;
                while (recLayoutLoop.hasNext())
                {
                    if (strEndLoop == null) if (!bFirstLoop)
                        this.println(out, ",", null, 0, "");
                    recLayoutLoop.next();
                    this.printIt(recLayoutLoop, out, iIndents + 1, strEndLoop);
                    bFirstLoop = false;
                }
                if (strEndLoop == null) if (!bFirstLoop)
                    this.println(out, "", null, 0, "");   // Carriage return
                recLayoutLoop.free();
                recLayoutLoop = null;
            } catch (DBException ex)    {
                ex.printStackTrace();
            }
            this.println(out, "}", null, iIndents, strEnd);
        }
        else
        {
            if (strType.equalsIgnoreCase("collection"))
                strType = "typedef sequence<" + strValue +">";
            else if (strType.equalsIgnoreCase("method"))
            {
                strType = strReturns + " ";
                strName += "(" + strValue + ")";
            }
            else if (strType.equalsIgnoreCase("comment"))
                strType = "//\t" + strType;
            else if (strValue.length() > 0)
                strName += " = " + strValue;
            this.println(out, strType + " " + strName, strDescription, iIndents, strEnd);
        }
    }
    /**
     * Println Method.
     */
    public void println(PrintWriter out, String string, String strDescription, int iIndents, String strEnd)
    {
        while (iIndents-- > 0)
            out.print("\t");
        if (strEnd != null) if ((strDescription != null) && (strDescription.length() > 0))
            strEnd = strEnd + "\t\t// " + strDescription;
        if (strEnd != null)
            out.println(string + strEnd);
        else
            out.print(string);
    }

}
