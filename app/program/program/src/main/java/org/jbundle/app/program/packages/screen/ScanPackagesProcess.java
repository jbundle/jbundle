/**
 * @(#)ScanPackagesProcess.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.packages.screen;

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
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.jbundle.base.thread.*;
import org.jbundle.thin.base.screen.*;
import org.jbundle.app.program.packages.db.*;
import org.jbundle.app.program.manual.convert.*;
import org.jbundle.app.program.db.*;

/**
 *  ScanPackagesProcess - .
 */
public class ScanPackagesProcess extends BaseProcess
{
    /**
     * Default constructor.
     */
    public ScanPackagesProcess()
    {
        super();
    }
    /**
     * Constructor.
     */
    public ScanPackagesProcess(RecordOwnerParent taskParent, Record recordMain, Map<String,Object> properties)
    {
        this();
        this.init(taskParent, recordMain, properties);
    }
    /**
     * Initialize class fields.
     */
    public void init(RecordOwnerParent taskParent, Record recordMain, Map<String, Object> properties)
    {
        super.init(taskParent, recordMain, properties);
    }
    /**
     * Open the main file.
     */
    public Record openMainRecord()
    {
        return new Packages(this);
    }
    /**
     * Open the other files.
     */
    public void openOtherRecords()
    {
        super.openOtherRecords();
        new ProgramControl(this);
    }
    /**
     * Run Method.
     */
    public void run()
    {
        Packages recPackages = (Packages)this.getMainRecord();
        ProgramControl recPackagesControl = (ProgramControl)this.getRecord(ProgramControl.kProgramControlFile);
        try {
            recPackagesControl.edit();
            recPackagesControl.getField(ProgramControl.kLastPackageUpdate).setValue(DateTimeField.currentTime());
        
            this.scanProjects(0);
        
            recPackages.setKeyArea(Packages.kIDKey);
            recPackages.close();
            while (recPackages.hasNext())
            {
                recPackages.next();
                Date timePackages = ((DateTimeField)recPackages.getField(Packages.kLastUpdated)).getDateTime();
                Date timeUpdated = ((DateTimeField)recPackagesControl.getField(ProgramControl.kLastPackageUpdate)).getDateTime();
                boolean bManual = recPackages.getField(Packages.kManual).getState();
                if (((timePackages == null) || (timePackages.before(timeUpdated)))
                    && (bManual == false))
                {
                    recPackages.edit();
                    recPackages.remove();
                }
            }
            recPackagesControl.set();
        } catch (DBException ex)    {
            ex.printStackTrace();
        } finally {
            recPackagesControl.free();
        }
    }
    /**
     * ScanProjects Method.
     */
    public void scanProjects(int parentProjectID)
    {
        ClassProject classProject = new ClassProject(this);
        try {
            classProject.addNew();
            classProject.setKeyArea(ClassProject.kIDKey);
            classProject.getField(ClassProject.kID).setValue(parentProjectID);
            if (classProject.seek(null))
            {
                if (isBaseDatabase(classProject))
                    this.scanAllPackages(classProject);
            }
            IntegerField field = new IntegerField(null, null, -1, null, null);
            field.setValue(parentProjectID);
            classProject.addNew();
            classProject.close();
            classProject.setKeyArea(ClassProject.kParentFolderIDKey);
            classProject.addListener(new SubFileFilter(field, ClassProject.kParentFolderID, null, -1, null, -1));
            while (classProject.hasNext())
            {
                classProject.next();
                this.scanProjects((int)classProject.getField(ClassProject.kID).getValue());
            }
            field.free();
        } catch (DBException e) {
            e.printStackTrace();
        } finally {
            classProject.free();
        }
    }
    /**
     * ScanAllPackages Method.
     */
    public void scanAllPackages(ClassProject classProject)
    {
        this.setProperty("projectID", classProject.getField(ClassProject.kID).toString());
        
        this.scanPackages(classProject, ClassProject.CodeType.RESOURCE_CODE);
        this.scanPackages(classProject, ClassProject.CodeType.RESOURCE_PROPERTIES);
        this.scanPackages(classProject, ClassProject.CodeType.THIN);
        this.scanPackages(classProject, ClassProject.CodeType.INTERFACE);
        this.scanPackages(classProject, ClassProject.CodeType.THICK);
    }
    /**
     * ScanPackages Method.
     */
    public void scanPackages(ClassProject classProject, ClassProject.CodeType codeType)
    {
        String projectClassDirectory = classProject.getFileName(null, null, codeType, true, false);
        Packages recPackages = (Packages)this.getMainRecord();
        Map<String, Object> prop = new HashMap<String, Object>();
        prop.put(ConvertCode.SOURCE_DIR, projectClassDirectory);
        prop.put(ConvertCode.DEST_DIR, null);
        prop.put("codeType", codeType);
        
        Task taskParent = this.getTask();
        ConvertCode convert = new ConvertCode(taskParent, null, prop);
        convert.setScanListener(new PackagesScanListener(convert, recPackages));
        convert.run();      
    }
    /**
     * IsBaseDatabase Method.
     */
    public boolean isBaseDatabase(Record record)
    {
        BaseDatabase database = record.getTable().getDatabase();
        boolean isBaseDB = true;
        int counter = (int)record.getCounterField().getValue();
        String startingID = database.getProperty(BaseDatabase.STARTING_ID);
        String endingID = database.getProperty(BaseDatabase.ENDING_ID);
        if (startingID != null)
            if (counter < Integer.parseInt(Converter.stripNonNumber(startingID)))
                isBaseDB = false;
        if (endingID != null)
            if (counter > Integer.parseInt(Converter.stripNonNumber(endingID)))
                isBaseDB = false;
        return isBaseDB;
    }

}
