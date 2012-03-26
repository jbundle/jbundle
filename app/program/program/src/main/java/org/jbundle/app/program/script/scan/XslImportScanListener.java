/**
 * @(#)XslImportScanListener.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.script.scan;

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
import org.jbundle.base.model.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import java.io.*;
import org.jbundle.thin.base.screen.*;

/**
 *  XslImportScanListener - .
 */
public class XslImportScanListener extends BaseScanListener
{
    protected boolean m_bSkipTag = false;
    protected boolean m_bSkipTemplate = false;
    protected Set<String> m_setMatches = new HashSet<String>();
    protected Set<String> m_setNames = new HashSet<String>();
    public static final String ROOT_FILE = "rootFile";
    /**
     * Default constructor.
     */
    public XslImportScanListener()
    {
        super();
    }
    /**
     * Constructor.
     */
    public XslImportScanListener(RecordOwnerParent parent, String strSourcePrefix)
    {
        this();
        this.init(parent, strSourcePrefix);
    }
    /**
     * Initialize class fields.
     */
    public void init(RecordOwnerParent parent, String strSourcePrefix)
    {
        super.init(parent, strSourcePrefix);
    }
    /**
     * MoveSourceToDest Method.
     */
    public void moveSourceToDest(LineNumberReader reader, PrintWriter dataOut)
    {
        m_bSkipTag = false;
        m_bSkipTemplate = false;
        m_setMatches = new HashSet<String>();
        m_setNames = new HashSet<String>();
        try {
            this.scanTemplates(reader);
            reader.close();
        
            FileInputStream fileIn = new FileInputStream(m_fileSource);
            InputStreamReader inStream = new InputStreamReader(fileIn);
            reader = new LineNumberReader(inStream);
            
        } catch (FileNotFoundException ex)  {
            ex.printStackTrace();
        } catch (IOException ex)    {
            ex.printStackTrace();
        }
        super.moveSourceToDest(reader, dataOut);
    }
    /**
     * ScanTemplates Method.
     */
    public void scanTemplates(LineNumberReader reader)
    {
        try {
            String string;
            boolean bTemplate = false;
            while ((string = reader.readLine()) != null)
            {
                if (string.indexOf("<xsl:template ") != -1)
                    bTemplate = true;
                if (bTemplate)
                {
                    int iStart = string.indexOf("match=\"") + 7;
                    int iEnd = string.indexOf('\"', iStart);
                    if (iStart != 6)
                        if (iEnd != -1)
                        {
                            String strMatch = string.substring(iStart, iEnd);
                            this.addMatch(strMatch, false);
                        }               
                    iStart = string.indexOf("name=\"") + 6;
                    iEnd = string.indexOf('\"', iStart);
                    if (iStart != 5)
                        if (iEnd != -1)
                        {
                            String strName = string.substring(iStart, iEnd);
                            this.addName(strName, false);
                        }               
                }
                if (string.indexOf(">") != -1)
                    bTemplate = false;
            }
        } catch (FileNotFoundException ex)  {
            ex.printStackTrace();
        } catch (IOException ex)    {
            ex.printStackTrace();
        }
    }
    /**
     * AddMatch Method.
     */
    public void addMatch(String strMatch, boolean bAddAll)
    {
        m_setMatches.add(strMatch);
        if (bAddAll)
            if (m_parent instanceof XslImportScanListener)
                ((XslImportScanListener)m_parent).addMatch(strMatch, bAddAll);  // Make sure no one else adds it
        System.out.println("match " + strMatch);
    }
    /**
     * AddName Method.
     */
    public void addName(String strName, boolean bAddAll)
    {
        m_setNames.add(strName);
        if (bAddAll)
            if (m_parent instanceof XslImportScanListener)
                ((XslImportScanListener)m_parent).addName(strName, bAddAll);    // Make sure no one else adds it
        System.out.println("name " + strName);
    }
    /**
     * IsMatch Method.
     */
    public boolean isMatch(String strMatch)
    {
        if (m_setMatches.contains(strMatch))
            return true;
        if (m_parent instanceof XslImportScanListener)
            return ((XslImportScanListener)m_parent).isMatch(strMatch);
        return false;
    }
    /**
     * IsName Method.
     */
    public boolean isName(String strName)
    {
        if (m_setNames.contains(strName))
            return true;
        if (m_parent instanceof XslImportScanListener)
            return ((XslImportScanListener)m_parent).isName(strName);
        return false;
    }
    /**
     * Do any string conversion on the file text.
     */
    public String convertString(String string)
    {
        if (string.indexOf("<xsl:import") != -1)
        {
            int iStart = string.indexOf("href=\"") + 6;
            int iEnd = string.indexOf('\"', iStart);
            if (iStart != 5)
                if (iEnd != -1)
                {
                    String strFilename = string.substring(iStart, iEnd);
                    
                    m_writer.write("<!-- " + "Import inlined from: " + strFilename + " - Do not modify this generated file." + "-->");
                    m_writer.println();
        
                    this.writeImport(strFilename);
                }
            return null;
        }
        if (Boolean.FALSE.toString().equals(this.getProperty(ROOT_FILE)))
        {
            if (string.indexOf("xsl:stylesheet") != -1)
                m_bSkipTag = true;
            if (string.indexOf("<?xml version=") != -1)
                m_bSkipTag = true;
        //          if (string.indexOf("<xsl:output method=") != -1)
        //              m_bSkipTag = true;
        }
        if (m_bSkipTag)
        {
            if (string.indexOf('>') != -1)
                m_bSkipTag = false;
            return null;
        }
        if (string.indexOf("<xsl:template ") != -1)
        {
            int iStart = string.indexOf("match=\"") + 7;
            int iEnd = string.indexOf('\"', iStart);
            if (iStart != 6)
                if (iEnd != -1)
                {
                    String strMatch = string.substring(iStart, iEnd);
                    if (m_parent instanceof XslImportScanListener)
                        m_bSkipTemplate = ((XslImportScanListener)m_parent).isMatch(strMatch);
                    if (!m_bSkipTemplate)
                        if (m_parent instanceof XslImportScanListener)
                            ((XslImportScanListener)m_parent).addMatch(strMatch, true);   // Make sure no one else adds it
                    if (m_bSkipTemplate)
                        System.out.println("Skip match " + strMatch);
        
                }               
            iStart = string.indexOf("name=\"") + 6;
            iEnd = string.indexOf('\"', iStart);
            if (iStart != 5)
                if (iEnd != -1)
                {
                    String strName = string.substring(iStart, iEnd);
                    if (m_parent instanceof XslImportScanListener)
                        m_bSkipTemplate = ((XslImportScanListener)m_parent).isName(strName);
                    if (!m_bSkipTemplate)
                        if (m_parent instanceof XslImportScanListener)
                            ((XslImportScanListener)m_parent).addName(strName, true); // Make sure no one else adds it
                    if (m_bSkipTemplate)
                        System.out.println("Skip name " + strName);
                }               
        }
        if (m_bSkipTemplate)
        {
            if (string.indexOf("</xsl:template>") != -1)
                m_bSkipTemplate = false;
            return null;
        }
        return string;
    }
    /**
     * WriteImport Method.
     */
    public void writeImport(String strFilename)
    {
        strFilename = this.getProperty(org.jbundle.app.program.manual.convert.ConvertCode.DIR_PREFIX) + this.getProperty(org.jbundle.app.program.manual.convert.ConvertCode.SOURCE_DIR) + '/' + strFilename;
        System.out.println(strFilename);
        try {
            File fileSource = new File(strFilename);
            FileInputStream fileIn = new FileInputStream(fileSource);
            InputStreamReader inStream = new InputStreamReader(fileIn);
            LineNumberReader reader = new LineNumberReader(inStream);
        
            PrintWriter dataOut = m_writer;
        
            XslImportScanListener listener = new XslImportScanListener(this, m_strSourcePrefix);
            listener.setPrintWriter(dataOut);
            listener.setSourceFile(fileSource);
            Map<String,Object> oldProperties = m_parent.getProperties();
            Map<String,Object> newProperties = new HashMap<String,Object>(oldProperties);
            m_parent.setProperties(newProperties);
            m_parent.setProperty(ROOT_FILE, Boolean.FALSE.toString());
            listener.moveSourceToDest(reader, dataOut);
            listener.free();
            m_parent.setProperties(oldProperties);
            
            reader.close();
            
        } catch (FileNotFoundException ex)  {
            ex.printStackTrace();
        } catch (IOException ex)    {
            ex.printStackTrace();
        }
    }

}
