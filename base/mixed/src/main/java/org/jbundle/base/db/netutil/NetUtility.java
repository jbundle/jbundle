package org.jbundle.base.db.netutil;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.ServletException;

import org.jbundle.base.db.BaseDatabase;
import org.jbundle.base.db.BaseTable;
import org.jbundle.base.db.Record;
import org.jbundle.base.db.RecordOwner;
import org.jbundle.base.util.BaseApplication;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.Environment;
import org.jbundle.base.util.Utility;
import org.jbundle.model.util.Util;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.db.FieldList;
import org.jbundle.thin.base.db.mem.base.PDatabase;
import org.jbundle.thin.base.db.mem.base.PTable;
import org.jbundle.thin.base.db.mem.base.PhysicalDatabaseParent;
import org.jbundle.thin.base.db.mem.memory.MDatabase;
import org.jbundle.thin.base.db.model.ThinPhysicalDatabase;
import org.jbundle.thin.base.db.model.ThinPhysicalDatabaseOwner;
import org.jbundle.thin.base.db.model.ThinPhysicalTableOwner;
import org.jbundle.thin.base.util.ThinUtil;

/**
 * Read a net table into a stream.
 * @author don
 *
 */
public class NetUtility {

    /**
     * Process an HTML get or post.
     * @param req The servlet request.
     * @param res The servlet response object.
     * @exception ServletException From inherited class.
     * @exception IOException From inherited class.
     */
    public static void getNetTable(String strRecordClass, String strTableName, String strLanguage, ThinPhysicalDatabaseOwner databaseOwner, ThinPhysicalTableOwner tableOwner, RecordOwner recordOwner, OutputStream ostream) 
    	throws IOException
    {
    	String oldLanguage = null;
        if (strLanguage != null) if (strLanguage.length() > 0)
        {	// Possible concurrency problems
        	oldLanguage = recordOwner.getTask().getApplication().getLanguage(true);
        	if (!strLanguage.equals(oldLanguage))
        		recordOwner.getTask().getApplication().setLanguage(strLanguage);
        }
        String databaseName = null;
        if (strTableName != null)
        {
            if (strTableName.lastIndexOf('/') != -1)
            {
                databaseName = strTableName.substring(0, strTableName.lastIndexOf('/'));
                if (databaseName.lastIndexOf('/') != -1)
                    databaseName = databaseName.substring(databaseName.lastIndexOf('/') + 1);
                strTableName = strTableName.substring(strTableName.lastIndexOf('/') + 1);
                if (strTableName.indexOf('.') != -1)
                    strTableName = strTableName.substring(0, strTableName.indexOf('.'));
            }
        }

        Utility.getLogger().info("Net DB Requested: " + strRecordClass);

        if (strRecordClass == null)
            return;
        if (strRecordClass.startsWith("."))
        	strRecordClass = DBConstants.ROOT_PACKAGE + strRecordClass.substring(1);	// Never
        String strThinRecordClass = Util.convertClassName(strRecordClass, DBConstants.THIN_SUBPACKAGE);
        FieldList fieldList = NetUtility.makeThinRecordFromClassname(strThinRecordClass);
        if (fieldList == null)
            return;
        
        Environment env = ((BaseApplication)recordOwner.getTask().getApplication()).getEnvironment();
        PhysicalDatabaseParent dbParent = (PhysicalDatabaseParent)env.getPDatabaseParent(mapDBParentProperties, true);
        PDatabase pDatabase = dbParent.getPDatabase(databaseName, ThinPhysicalDatabase.MEMORY_TYPE, true);  // Get/create database
        pDatabase.addPDatabaseOwner(databaseOwner);
        if (strLanguage != null)
            if (strLanguage.length() > 0)
            	if (fieldList.getTableNames(false) != null)
	                fieldList.setTableNames(fieldList.getTableNames(false) + '_' + strLanguage);
        PTable pTable = pDatabase.getPTable(fieldList, false);
        if (pTable == null)
        {
            pTable = pDatabase.getPTable(fieldList, true, true);    // Create it (with grid access)
            pTable.addPTableOwner(tableOwner);

            if (strRecordClass.equalsIgnoreCase(strThinRecordClass))
            {	// Asking for the thin record, get the thick record class name
            	int startThin = strThinRecordClass.indexOf(Constants.THIN_SUBPACKAGE, 0);
            	if (startThin != -1)	// Always
            		strRecordClass = strThinRecordClass.substring(0, startThin) + strThinRecordClass.substring(startThin + Constants.THIN_SUBPACKAGE.length());
            }
            Record record = Record.makeRecordFromClassName(strRecordClass, recordOwner, false, true);
            if (record == null)
                return;
            if (strTableName != null)
                if (strTableName.length() > 0)
                    if ((!strTableName.equalsIgnoreCase(record.getTableNames(false))))
                        if ((record.getDatabaseType() & DBConstants.MAPPED) != 0)
                            record.setTableNames(strTableName);
            if (databaseName != null)
            	if (!databaseName.equalsIgnoreCase(record.getDatabaseName()))
        	{	// Set alternate database name
            		// Yikes... This is a little off logic because I use the actual long database name to create a database (It should work though)
                BaseDatabase database = recordOwner.getDatabaseOwner().getDatabase(databaseName, record.getDatabaseType(), null);
                BaseTable table = database.makeTable(record);
        		record.setTable(table);
        	}
            
            record.init(recordOwner);

            record.populateThinTable(fieldList, false);
            
            record.free();
            fieldList.getTable().free();
        }
        ObjectOutputStream outstream = new ObjectOutputStream(ostream);

        outstream.writeObject(pTable); // Write the tree to the stream.
        pTable.removePTableOwner(tableOwner, true);   // Hopefully this is cached, so I don't have to create it again
        pDatabase.removePDatabaseOwner(databaseOwner, true);
        outstream.flush();
        if (oldLanguage != null) if (oldLanguage.length() > 0)
        	recordOwner.getTask().getApplication().setLanguage(oldLanguage);
    }
    public static Map<String,Object> mapDBParentProperties = new Hashtable<String,Object>();
    static
    {
        mapDBParentProperties.put(PhysicalDatabaseParent.TIME, "-1");   // Default time
        mapDBParentProperties.put(PhysicalDatabaseParent.DBCLASS, MDatabase.class.getName());   // Hybrid (net) database.        
    }
    /**
     * Given the fully qualified (domain prefix not required) class name, create the thin version of the record.
     * @param strClassName The thick record class name.
     * @return The new thin fieldlist for this record.
     */
    public static FieldList makeThinRecordFromClassname(String strClassName)
    {
        FieldList record = (FieldList)ThinUtil.getClassService().makeObjectFromClassName(strClassName);
        if (record != null)
        	record.init(null);
        return record;
    }

}
