/**
 * @(#)CalendarScanListener.
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
import org.jbundle.app.program.manual.convert.*;

/**
 *  CalendarScanListener - .
 */
public class CalendarScanListener extends BaseScanListener
{
    public static final String[] m_rgstrMonths = {
    "01 - January",
    "02 - February",
    "03 - March",
    "04 - April",
    "05 - May",
    "06 - June",
    "07 - July",
    "08 - August",
    "09 - September",
    "10 - October",
    "11 - November",
    "12 - December"
    };;
    /**
     * Default constructor.
     */
    public CalendarScanListener()
    {
        super();
    }
    /**
     * Constructor.
     */
    public CalendarScanListener(RecordOwnerParent parent, String strSourcePrefix)
    {
        this();
        this.init(parent, strSourcePrefix);
    }
    /**
     * Init Method.
     */
    public void init(RecordOwnerParent parent, String strSourcePrefix)
    {
        super.init(parent, strSourcePrefix);
        if (this.getProperty(ConvertCode.SOURCE_DIR) != null)
            if ((this.getProperty(ConvertCode.SOURCE_DIR) + "out").equalsIgnoreCase(this.getProperty(ConvertCode.DEST_DIR)))
                this.setProperty(ConvertCode.DEST_DIR, this.getProperty(ConvertCode.DEST_DIR).substring(0, this.getProperty(ConvertCode.DEST_DIR).length() - 3));
    }
    /**
     * If this file should be processed, return true.
     */
    public boolean filterDirectory(File file)
    {
        boolean bProcessFile = super.filterDirectory(file);
        if (bProcessFile == false)
            return bProcessFile;
        String strName = file.getName();
        for (String strMonth : m_rgstrMonths)
        {
            if (strName.equalsIgnoreCase(strMonth))
                return false;
        }
        if (Utility.isNumeric(strName))
        {
            int iYear = Integer.parseInt(strName);
            if ((iYear > 1990) && (iYear < 2020))
                return false;
        }
        return bProcessFile;
    }
    /**
     * If this file should be processed, return true.
     */
    public boolean filterFile(File file)
    {
        boolean bProcessFile = super.filterFile(file);
        if (bProcessFile == false)
            return bProcessFile;
        if ((this.getProperty(ConvertCode.DEST_DIR) != null) && (!this.getProperty(ConvertCode.DEST_DIR).equalsIgnoreCase(this.getProperty(ConvertCode.SOURCE_DIR))))
            return bProcessFile;
        String s = this.getNewCalendarPath(file);
        return (s != null);   // Skip if no new path (Only if moving to the same directory)
    }
    /**
     * GetNewCalendarPath Method.
     */
    public String getNewCalendarPath(File file)
    {
        StringTokenizer st = new StringTokenizer(file.getName());
        int iPlace = 0, iMonth = 0, iYear = 0;
        while (st.hasMoreTokens())
        {
            String strToken = st.nextToken();
            if (strToken.length() > 2)
                break;  // Not a date
            if (!Utility.isNumeric(strToken))
                break;  // Not a date
            if (strToken.indexOf('.') != -1)
                break;
            int iNumber = Integer.parseInt(strToken);
            iPlace++;
            if (iPlace == 1)
            { // Month
                if ((iNumber < 1) || (iNumber > 12))
                    break;  // Invalid month
                iMonth = iNumber;
            }
            else if (iPlace == 2)
            { // day
                if ((iNumber < 1) || (iNumber > 31))
                    break;  // Invalid day
            }
            else if (iPlace == 3)
            { // Year
                if ((iNumber < 1) || (iNumber > 2020))
                    break;  // Invalid year
                iYear = iNumber;
                if (iYear <= 20)
                    iYear = iYear + 2000;
                return Integer.toString(iYear) + '/' + m_rgstrMonths[iMonth - 1];
            }
        }
        return null;    // Not formatted correctly
    }
    /**
     * Do whatever processing that needs to be done on this file.
     */
    public void moveThisFile(File fileSource, File fileDestDir, String strDestName)
    {
        fileDestDir.mkdirs();
        String strNewDir = this.getNewCalendarPath(fileSource);
        if (strNewDir != null)
        {
            fileDestDir = new File(fileDestDir, strNewDir);
            fileDestDir.mkdirs();
        }
        File fileDest = new File(fileDestDir, strDestName);
        if (fileSource.renameTo(fileDest))
            return;
        if (true)
        {
            System.out.println("Warning, system does not support move file");
            return;
        }
        super.moveThisFile(fileSource, fileDestDir, strDestName);
        if (strNewDir == null)
            return;   // Moving to a different dir = move all.
        fileSource.delete();    // Moving to the same dir (into the new directory) means delete to old file at the root
    }
    /**
     * MoveSourceToDest Method.
     */
    public void moveSourceToDest(LineNumberReader reader, PrintWriter dataOut)
    {
        super.moveSourceToDest(reader, dataOut);
    }

}
