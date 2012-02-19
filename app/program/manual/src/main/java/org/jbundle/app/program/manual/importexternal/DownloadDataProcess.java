/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.app.program.manual.importexternal;

/**
 * Copyright © 2012 jbundle.org. All Rights Reserved.
 *  .
 *      don@tourgeek.com
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.model.DBParams;
import org.jbundle.base.thread.BaseProcess;
import org.jbundle.model.RecordOwnerParent;
import org.jbundle.model.Task;


/**
import java.test.*;
import com.sun.java.swing.*;
import com.sun.java.swing.text.*;
*/
public class DownloadDataProcess extends BaseProcess
{
    /**
     * Initialization.
     */
    public DownloadDataProcess()
    {
        super();
    }
    /**
     * Initialization.
     * @param taskParent Optional task param used to get parent's properties, etc.
     * @param recordMain Optional main record.
     * @param properties Optional properties object (note you can add properties later).
     */
    public DownloadDataProcess(Task taskParent, Record recordMain, Map<String,Object> properties)
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
     * Free the resources.
     */
    public void free()
    {
            super.free();
    }
    /**
     * Move the files in this directory to the new directory.
     */
    public void run()
    {

        String strURL = this.getProperty(DBParams.URL);
        String strOut = this.getProperty("out");
        
        this.moveThisFile(strURL, strOut);
    }
    /**
     * Move this file.
     */
    public void moveThisFile(String strURL, String strName)
    {
        try {
            URL url = new URL(strURL);
            InputStream inputStream = url.openStream();
            InputStreamReader inStream = new InputStreamReader(inputStream);
            LineNumberReader reader = new LineNumberReader(inStream);

            String strDir = strName.substring(0, strName.lastIndexOf('/'));
            File fileDirDest = new File(strDir);
            fileDirDest.mkdirs();
            File fileDest = new File(strName);
            fileDest.createNewFile();
            FileOutputStream fileOut = new FileOutputStream(fileDest);
            PrintWriter dataOut = new PrintWriter(fileOut);
        //+
            String string;            
            while ((string = reader.readLine()) != null)
            {
                string = this.convertString(string);
                dataOut.write(string);
                dataOut.println();
            }
        //+
            dataOut.close();
            fileOut.close();

            reader.close();
            inStream.close();

//?            url.close();
        } catch (FileNotFoundException ex)  {
            ex.printStackTrace();
        } catch (IOException ex)    {
            ex.printStackTrace();
        }
    }
    /**
     *
     */
    public String convertString(String string)
    {
        return string;
    }
}
