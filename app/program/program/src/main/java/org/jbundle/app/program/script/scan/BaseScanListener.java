/**
 * @(#)BaseScanListener.
 * Copyright © 2011 jbundle.org. All rights reserved.
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
import org.jbundle.base.screen.model.*;
import org.jbundle.base.screen.model.util.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import java.io.*;
import org.jbundle.app.program.manual.*;
import org.jbundle.thin.base.screen.*;
import org.jbundle.thin.base.thread.*;

/**
 *  BaseScanListener - .
 */
public class BaseScanListener extends Object
     implements ScanListener
{
    protected File m_fileSource = null;
    protected RecordOwnerParent m_parent = null;
    protected String m_strSourcePrefix = null;
    protected PrintWriter m_writer = null;
    protected RecordOwnerCollection m_recordOwnerCollection = null;
    /**
     * Default constructor.
     */
    public BaseScanListener()
    {
        super();
    }
    /**
     * Constructor.
     */
    public BaseScanListener(RecordOwnerParent parent, String strSourcePrefix)
    {
        this();
        this.init(parent, strSourcePrefix);
    }
    /**
     * Init Method.
     */
    public void init(RecordOwnerParent parent, String strSourcePrefix)
    {
        m_parent = parent;
        m_strSourcePrefix = strSourcePrefix;
    }
    /**
     * Free Method.
     */
    public void free()
    {
        if (m_recordOwnerCollection != null)
            m_recordOwnerCollection.free();
        m_recordOwnerCollection = null;
        // Need to add code here
    }
    /**
     * If this file should be processed, return true.
     */
    public boolean filterDirectory(File file)
    {
        if ("CVS".equalsIgnoreCase(file.getName()))
            return false;
        if (file.getName().startsWith("."))
            return false;
        return true;    // By default, process all directories
    }
    /**
     * Do whatever processing that needs to be done on this directory.
     * @return caller specific information about this directory.
     */
    public Object processThisDirectory(File fileDir, Object objDirID)
    {
        return null;    // By default don't process directories
    }
    /**
     * Do whatever processing that needs to be done on this directory.
     * @return caller specific information about this directory.
     */
    public void postProcessThisDirectory(File fileDir, Object objDirID)
    {
        // By default don't process directories
    }
    /**
     * If this file should be processed, return true.
     */
    public boolean filterFile(File file)
    {
        String strName = file.getName();
        if (strName.endsWith("~"))
            return false;
        if (strName.endsWith("#"))
            return false;
        if (strName.startsWith("#"))
            return false;
        if (strName.startsWith("."))
            return false;
        return true;    // Process this.
    }
    /**
     * SetSourceFile Method.
     */
    public void setSourceFile(File fileSource)
    {
        m_fileSource = fileSource;
    }
    /**
     * Do whatever processing that needs to be done on this file.
     */
    public void moveThisFile(File fileSource, File fileDestDir, String strDestName)
    {
        fileDestDir.mkdirs();
        m_fileSource = fileSource;
        try {
            FileInputStream fileIn = new FileInputStream(fileSource);
            InputStreamReader inStream = new InputStreamReader(fileIn);
            LineNumberReader reader = new LineNumberReader(inStream);
            
            this.moveThisFile(reader, fileDestDir, strDestName);
            
            reader.close();
            inStream.close();
        
            fileIn.close();
        
        } catch (FileNotFoundException ex)  {
            ex.printStackTrace();
        } catch (IOException ex)  {
            ex.printStackTrace();
        }
    }
    /**
     * MoveThisFile Method.
     */
    public void moveThisFile(LineNumberReader reader, File fileDestDir, String strDestName)
    {
        try {
            File fileDest = new File(fileDestDir, strDestName);
            fileDest.createNewFile();
            FileOutputStream fileOut = new FileOutputStream(fileDest);
            m_writer = new PrintWriter(fileOut);
        
            this.moveSourceToDest(reader, m_writer);
                    
            m_writer.close();
            fileOut.close();
                
        } catch (FileNotFoundException ex)  {
            ex.printStackTrace();
        } catch (IOException ex)    {
            ex.printStackTrace();
        }
    }
    /**
     * SetPrintWriter Method.
     */
    public void setPrintWriter(PrintWriter writer)
    {
        m_writer = writer;
    }
    /**
     * MoveSourceToDest Method.
     */
    public void moveSourceToDest(LineNumberReader reader, PrintWriter dataOut)
    {
        try {
            String string;
            while ((string = reader.readLine()) != null)
            {
                string = this.convertString(string);
                if (string != null)
                {
                    dataOut.write(string);
                    dataOut.println();
                }
            }
        } catch (FileNotFoundException ex)  {
            ex.printStackTrace();
        } catch (IOException ex)    {
            ex.printStackTrace();
        }
    }
    /**
     * Do any string conversion on the file text.
     */
    public String convertString(String string)
    {
        return string;
    }
    /**
     * Get this property (from my parent ConvertCode class).
     * @param strProperty the property key
     * @return The value.
     */
    public String getProperty(String strProperty)
    {
        return m_parent.getProperty(strProperty);
    }
    /**
     * GetProperties Method.
     */
    public Map<String, Object> getProperties()
    {
        return m_parent.getProperties();
    }
    /**
     * RetrieveUserProperties Method.
     */
    public PropertyOwner retrieveUserProperties(String strRegistrationKey)
    {
        return m_parent.retrieveUserProperties(strRegistrationKey);
    }
    /**
     * SetProperties Method.
     */
    public void setProperties(Map<String, Object> properties)
    {
        m_parent.setProperties(properties);
    }
    /**
     * SetProperty Method.
     */
    public void setProperty(String strProperty, String strValue)
    {
        m_parent.setProperty(strProperty, strValue);
    }
    /**
     * GetTask Method.
     */
    public Task getTask()
    {
        return m_parent.getTask();
    }
    /**
     * Add this record owner to my list.
     * @param recordOwner The record owner to add.
     */
    public boolean addRecordOwner(RecordOwnerParent recordOwner)
    {
        if (m_recordOwnerCollection == null)
                m_recordOwnerCollection = new RecordOwnerCollection(this);
            return m_recordOwnerCollection.addRecordOwner(recordOwner);
    }
    /**
     * Remove this record owner to my list.
     * @param recordOwner The record owner to remove.
     */
    public boolean removeRecordOwner(RecordOwnerParent recordOwner)
    {
        if (m_recordOwnerCollection != null)
            return m_recordOwnerCollection.removeRecordOwner(recordOwner);
        return false;
    }

}
