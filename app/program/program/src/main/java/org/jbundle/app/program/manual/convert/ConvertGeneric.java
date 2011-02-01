package org.jbundle.app.program.manual.convert;

//******************************************************************************
// Test the basic table functions (add, remove, move, etc.)
//******************************************************************************
import java.util.Hashtable;
import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.field.ReferenceField;
import org.jbundle.base.thread.BaseProcess;
import org.jbundle.base.thread.ProcessRunnerTask;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.Environment;
import org.jbundle.base.util.MainApplication;
import org.jbundle.main.db.Menus;
import org.jbundle.model.DBException;
import org.jbundle.model.RecordOwnerParent;
import org.jbundle.model.Task;
import org.jbundle.thin.base.screen.BaseApplet;
import org.jbundle.thin.base.util.Util;


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
//            LogicFile record = new LogicFile(this);
            Menus record = new Menus(this);
            while (record.hasNext())
            {
                record.next();
                Record menu = ((ReferenceField)record.getField(Menus.kParentFolderID)).getReference();
                if (record.getField(Menus.kParentFolderID).getValue() != 0)
                    if ((menu == null)
                            || (menu.getEditMode() == DBConstants.EDIT_NONE))
                {
	                    System.out.println(record.getField(Menus.kName).toString());
	                    record.edit();
	                    record.remove();
                }
            }
            record.free();
            record = null;
        } catch (DBException ex)    {
            ex.printStackTrace();
        }
    }
}
