/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.app.program.manual.jtidy;

//******************************************************************************
// Test the basic table functions (add, remove, move, etc.)
//******************************************************************************
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.thread.BaseProcess;
import org.jbundle.base.thread.ProcessRunnerTask;
import org.jbundle.base.util.Environment;
import org.jbundle.base.util.MainApplication;
import org.jbundle.app.program.db.ClassInfo;
import org.jbundle.model.DBException;
import org.jbundle.model.RecordOwnerParent;
import org.jbundle.model.Task;
import org.jbundle.model.util.Util;
import org.jbundle.thin.base.screen.BaseApplet;
import org.w3c.tidy.Tidy;


/**
 * Template to change one record's field to another value.
 */
public class ConvertGeneric extends BaseProcess
{

    /**
     * Constructor.
     */
    public ConvertGeneric()
    {
        super();
    }
    /**
     * Initialization.
     * @param taskParent Optional task param used to get parent's properties, etc.
     * @param recordMain Optional main record.
     * @param properties Optional properties object (note you can add properties later).
     */
    public ConvertGeneric(Task taskParent, Record recordMain, Map<String, Object> properties)
    {
        this();
        this.init(taskParent, recordMain, properties);
    }
    /**
     * Initialization.
     * @param taskParent Optional task param used to get parent's properties, etc.
     * @param recordMain Optional main record.
     * @param properties Optional properties object (note you can add properties later).
     */
    public void init(RecordOwnerParent taskParent, Record recordMain, Map<String, Object> properties)
    {
        super.init(taskParent, recordMain, properties);
    }
    /**
     * Free.
     */
    public void free()
    {
        super.free();
    }
    
    public static void main(String[] args)
    {
        BaseApplet.main(args);      // This says I'm stand-alone
        Map<String,Object> properties = null;
        if (args != null)
        {
            properties = new Hashtable<String,Object>();
            Util.parseArgs(properties, args);
        }
        Environment env = new Environment(properties);
        MainApplication app = new MainApplication(env, properties, null);
        ProcessRunnerTask task = new ProcessRunnerTask(app, null, properties);
        ConvertGeneric test = new ConvertGeneric(task, null, null);
        test.go();
        test.free();
        test = null;
        System.exit(0);
    }
    public void go()
    {
        try   {
            ClassInfo record = new ClassInfo(this);
            while (record.hasNext())
            {
                record.next();
//x             if ((record.getField(ClassInfo.kClassName).toString().equals("Account")))
                {
                    record.edit();
                    
                    String strHtml = record.getField(ClassInfo.kClassExplain).toString();
                    String strXml = record.getField(ClassInfo.kClassHelp).toString();

                    strHtml = this.convertTidy(strHtml, false, false);
                    strXml = this.convertTidy(strXml, true, false);
                    if (strXml.length() > 0)
                        if (!strXml.startsWith("<html>"))
                        {
                            strXml = "<html>\n" + strXml + "\n</html>";
                        }

                    record.getField(ClassInfo.kClassExplain).setString(strHtml);
                    record.getField(ClassInfo.kClassHelp).setString(strXml);

                    record.set();
                }
            }
            record.free();
            record = null;
        } catch (DBException ex)    {
            ex.printStackTrace();
        }
    }
    
    public String convertTidy(String strIn, boolean bXMLType, boolean bForce)
    {
        String strOut = "";
        if (bXMLType)
            if (strIn != null)
                if (strIn.startsWith("<html>"))
                    bXMLType = false;   // Parse as html
        if (!bForce)
            if (strIn != null)
                if (strIn.indexOf('<') == -1)
                    return strIn;   // Don't force conversion if there is no markup

        BufferedInputStream in;
//        FileOutputStream out;
        Tidy tidy = new Tidy();
        tidy.setXmlOut(true);
        tidy.setXHTML(false);
        tidy.setTidyMark(false);
        tidy.setSmartIndent(true);
//x            tidy.setErrout(new PrintWriter(new FileWriter(errOutFileName), true)); 
            tidy.setErrout(new PrintWriter(System.out)); 
//x            u = new URL(url);
//x            in = new BufferedInputStream(u.openStream());
            in = new BufferedInputStream(new ByteArrayInputStream(strIn.getBytes()));
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            tidy.parse(in, out);

        strOut = out.toString();
        if (bXMLType)
        {
            int iStart = strOut.indexOf("<body>");
            int iEnd = strOut.lastIndexOf("</body>");
            if ((iStart != -1) && (iEnd != -1))
            {
                strOut = strOut.substring(iStart + 6, iEnd);
            }
        }
        else
        {
            String TITLE = "<title></title>";
            int iTitle = strOut.indexOf(TITLE);
            int iStart = strOut.indexOf("<head>");
            int iEnd = strOut.lastIndexOf("</head>");
            if (iTitle != -1)
                if ((iStart != -1) && (iEnd != -1))
            {
                strOut = strOut.substring(0, iStart + 6) + strOut.substring(iEnd);
            }
        }
        strOut = this.stripLeadTrail(strOut);
System.out.println("in: " + strIn);
System.out.println("out: " + strOut);
        return strOut;
    }
    
    public String stripLeadTrail(String strIn)
    {
        for (int i = 0; i < strIn.length(); i++)
        {
            if (!Character.isWhitespace(strIn.charAt(i)))
            {
                if (i > 0)
                {
                    strIn = strIn.substring(i);
                }
                break;
            }
        }
        for (int i = strIn.length() - 1; i > 0; i--)
        {
            if (!Character.isWhitespace(strIn.charAt(i)))
            {
                if (i < strIn.length() - 1)
                {
                    strIn = strIn.substring(0, i + 1);
                }
                break;
            }
        }
        return strIn;
    }
}
