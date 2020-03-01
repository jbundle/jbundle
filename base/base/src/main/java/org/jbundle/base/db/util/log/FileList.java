/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.db.util.log;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * List of the files in this directory.
 * @author don
 *
 */
public class FileList extends Object
    implements BackupConstants
{
    protected FilenameFilter m_filter = null;
    protected String m_strFileLocation = null;
    
    /**
     * Constructor.
     */
    public FileList()
    {
        super();
    }
    /**
     * Constructor.
     */
    public FileList(String strFileLocation, String filter)
    {
        this();
        this.init(strFileLocation, filter);
    }
    /**
     * Constructor.
     */
    public FileList(String strFileLocation)
    {
        this();
        this.init(strFileLocation, DEFAULT_REGEX);
    }
    /**
     * Constructor.
     */
   public void init(String strFileLocation, String filter)
    {
        m_strFileLocation = strFileLocation;
        m_filter = new Filter(filter);
    }
    public String[] getFileNames()
    {
        String[] files = null;
        if (m_strFileLocation.indexOf(':') == -1)
            files = this.readFileDir(m_strFileLocation);
        else
            files = this.readFtpDir(m_strFileLocation);
        files = this.sortFiles(files);
        return files;
    }
    
    public String[] sortFiles(String[] files)
    {
        int count = 0;
        Date[] dates = new Date[files.length];
        for (int i = 0; i < files.length; i++)
        {
                String strDate = files[i];
                int iStart = 0;
                for (iStart = 0; iStart < strDate.length(); iStart++)
                {
                    if (Character.isDigit((strDate.charAt(iStart))))
                        break;
                }
                if (iStart == strDate.length())
                    iStart = 0;
                int iEnd = strDate.indexOf('.', iStart);
                if (iEnd == -1)
                    iEnd = strDate.length();
                strDate = strDate.substring(iStart, iEnd);
                strDate = strDate.replace('_', ' ');
//                dates[i] = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG).parse(strDate);
                StringTokenizer st = new StringTokenizer(strDate);
                int[] number = new int[10];
                int index = 0;
                while (st.hasMoreTokens())
                {
                    try {
                        String strToken = st.nextToken();
                        if (index == 6)
                            if ("pm".equalsIgnoreCase(strToken))
                                number[3] += 12;
                        number[index++] = Integer.parseInt(strToken);
                    } catch (NumberFormatException e) {
                        number[index++] = 0;
                    }
                }
                if (index == 10)
                {
                    GregorianCalendar cal = new GregorianCalendar(number[2] + 2000, number[0] - 1, number[1], number[3], number[4], number[5]);
                    dates[i] = cal.getTime();
                    count++;
                }
                else
                    dates[i] = null;
        }
        // Now sort them
        for (int x = 0; x < files.length; x++)
        {
            for (int y = x + 1; y < files.length; y++)
            {
                boolean bSwitch = false;
                if (dates[x] == null)
                    bSwitch = false;
                else if (dates[y] == null)
                    bSwitch = true;
                else
                    bSwitch = dates[x].after(dates[y]);
                if (bSwitch)
                {
                    Date dateTemp = dates[x];
                    dates[x] = dates[y];
                    dates[y] = dateTemp;
                    
                    String filesTemp = files[x];
                    files[x] = files[y];
                    files[y] = filesTemp;
                }
            }
        }
        
        String[] newfiles = new String[count];
        // Now sort them
        int z = 0;
        for (int x = 0; x < files.length; x++)
        {
            if (dates[x] != null)
                newfiles[z++] = files[x];
        }
        
        return newfiles;
    }
    
    public String[] readFtpDir(String strFtpURL)
    {
        try {
            URL url = new URL(strFtpURL);
            URLConnection urlc = url.openConnection();
            InputStream is = urlc.getInputStream(); // To download
            
            byte[] b = new byte[100];
            String string = "";
            Vector<String> vector = new Vector<String>();
            while (true)
            {
                int bytesRead = is.read(b);
                string = string + new String(b);
                int iPos = string.indexOf('\n');
                if (iPos == -1)
                    iPos = string.length();
                if (iPos != 0)
                {
                    String str = string.substring(0, iPos);
                    string = string.substring(iPos + 1);
                    System.out.println("xx: " + str);
                    StringTokenizer st = new StringTokenizer(str);
                    String filename  = null;
                    while (st.hasMoreTokens()) {
                        filename = st.nextToken();
                    }
                    if (m_filter.accept(null, filename))
                        vector.add(filename);
                }
                if (bytesRead < b.length)
                    break;
            }
            
            is.close();
            String[] str = new String[vector.size()];
            vector.copyInto(str);
            return str;
            
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;        
    }
    public String[] readFileDir(String strDir)
    {
        File file = new File(strDir);
        String[] files = null;
        
        if (file.isDirectory())
        {
            files = file.list(m_filter);
        }
        return files;
    }
    class Filter
        implements FilenameFilter
        {
            public Filter(String strFilter)
            {
                m_regex = strFilter;
            }
            protected String m_regex = null;
            @Override
            public boolean accept(File dir, String name) {
                return name.matches(m_regex);
            }
        
        }
    /**
     * @param args
     */
    public static void main(String[] args) {
//        FileList pgm = new FileList("/home/dcorley/", "trxlog.*txt");
        FileList pgm = new FileList("ftp://download:donwpp@www.donandann.com/backup/", "trxlog.*txt");
        String[] files = pgm.getFileNames();
        for (int i = 0; i < files.length; i++)
        {
            System.out.println("files: " + files[i]);
        }
    }
    

}
