package org.jbundle.app.program.manual.util.stream;

/**
 *  WriteJava
 *  Copyright (c) 2005 jbundle.org. All rights reserved.
 */
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

//*******************************************************************
// StreamOut - Output a text file
//*******************************************************************

public class StreamOut extends PrintWriter
{
    protected int m_iTabs;
    protected boolean m_bTabOnNextLine = false;

    public StreamOut(String fileName) throws IOException
    {
        super(new FileWriter(fileName));
        m_iTabs = 0;
        m_bTabOnNextLine = false;
//      ASSERT(this->Open(fileName, CFile::modeCreate | CFile::modeWrite)); // | CFile::typeText));
    }
    public void free()
    {
        this.close();
    }
    public void setTabs(int iRelTabs)
    {
        m_iTabs += iRelTabs;
    }
    public void writeit(String strTemp)     // Convert the PL/1 Name to the new format
    {
            String strTabs = "";
            int i = 0;
            for (i = 0; i < m_iTabs; i++)
            {
                strTabs += "\t";
            }
            if (m_bTabOnNextLine) if ((strTemp.length() > 0) && (strTemp.charAt(0) != '\n'))
                strTemp = strTabs + strTemp;
            m_bTabOnNextLine = false;
            int iIndex = 0;
            int iIndex2;
            while (iIndex != -1)
            {
                iIndex = strTemp.indexOf('\n', iIndex);
                if ((iIndex != -1) && (iIndex < strTemp.length() - 1))
                {
                    iIndex2 = iIndex + 1;
                    if (iIndex > 1) if (strTemp.charAt(iIndex) == '\r')
                        iIndex--;
                    strTemp = strTemp.substring(0, iIndex+1) + strTabs + strTemp.substring(iIndex2, strTemp.length());
                    iIndex = iIndex + 2;
                }
                else
                    iIndex = -1;
            }
            iIndex = 0;
            while (iIndex != -1)
            {
                iIndex = strTemp.indexOf('\r', iIndex);
                if (iIndex != -1)
                    strTemp = strTemp.substring(0, iIndex) + strTemp.substring(iIndex + 1, strTemp.length());
            }
            strTemp = this.tabsToSpaces(strTemp);
            this.print(strTemp);
            if (strTemp.length() > 0) if (strTemp.charAt(strTemp.length() - 1) == '\n');
                m_bTabOnNextLine = true;
    }
    public static final String FOUR_SPACES = "    ";
    /**
     * Convert the tabs to spaces.
     */
    public String tabsToSpaces(String string)
    {
        int iOffset = 0;
        for (int i = 0; i < string.length(); i++)
        {
            if (string.charAt(i) == '\n')
                iOffset = i + 1;
            if (string.charAt(i) == '\t')
            {
                int iSpaces = (i - iOffset) % 4;
                if (iSpaces == 0)
                    iSpaces = 4;
                string = string.substring(0, i) + FOUR_SPACES.substring(0, iSpaces) + string.substring(i + 1);
            }
        }
        return string;
    }
}
