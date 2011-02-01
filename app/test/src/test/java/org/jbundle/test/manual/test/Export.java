package org.jbundle.test.manual.test;

import org.jbundle.app.test.test.db.TestTable;
import org.jbundle.base.db.Record;
import org.jbundle.base.db.xmlutil.XmlInOut;


//import com.ibm.xml.parser.*;
//import com.sun.xml.tree.*;
//import com.ibm.xml.siteoutliner.*;
//import org.w3c.dom.*;

// SimpleForm is the data entry form for the sample
public class Export extends Object
{
    // Create the form
    public Export()
    {
        super();
    }
    public void free()
    {
    }
    public void go()
    {
//x        int iCount = 0;
        System.out.print("Begin Test\n");
        System.out.print("Open table.\n");
        TestTable testTable = new TestTable(null);

        
        this.importXML(testTable);
        
            
        System.out.print("Free the table object\n");
        testTable.free();
        testTable = null;
        System.out.print("Test complete.\n");

    }

    protected String m_strFileName = null;

    /**
     * Close this table.
     */
    public boolean exportXML(Record record)
    {
        String strFileName = this.getXMLFileName(record);
        XmlInOut inOut = new XmlInOut(null, null, null);
        boolean bSuccess = inOut.exportXML(record, strFileName);
        inOut.free();
        return bSuccess;
    }
    /**
     * Open this table (requery the table).
     * @param record The record to import.
     */
    public boolean importXML(Record record)
    {
        String strFileName = this.getXMLFileName(record);
        XmlInOut inOut = new XmlInOut(null, null, null);
        boolean bSuccess = inOut.importXML(record, strFileName, null);
        inOut.free();
        return bSuccess;
    }

    public String getXMLFileName(Record record)
    {
        if (m_strFileName == null)
        {
//          BaseDatabase pDatabase = record.getDatabase();
            String strRecordName = record.getTableNames(false);
            String strDatabaseName = record.getDatabaseName();
            m_strFileName = "xml_" + strDatabaseName + "_" + strRecordName + ".xml";
        }
        return m_strFileName;
    }
}
