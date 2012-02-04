/**
 * @(#)ProgramControl.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.db;

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
import org.jbundle.base.model.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.jbundle.app.program.resource.db.*;
import org.jbundle.model.app.program.db.*;

/**
 *  ProgramControl - Program control.
 */
public class ProgramControl extends ControlRecord
     implements ProgramControlModel
{
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     */
    public ProgramControl()
    {
        super();
    }
    /**
     * Constructor.
     */
    public ProgramControl(RecordOwner screen)
    {
        this();
        this.init(screen);
    }
    /**
     * Initialize class fields.
     */
    public void init(RecordOwner screen)
    {
        super.init(screen);
    }
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(PROGRAM_CONTROL_FILE, bAddQuotes) : super.getTableNames(bAddQuotes);
    }
    /**
     * Get the Database Name.
     */
    public String getDatabaseName()
    {
        return "program";
    }
    /**
     * Is this a local (vs remote) file?.
     */
    public int getDatabaseType()
    {
        return DBConstants.LOCAL | DBConstants.USER_DATA;
    }
    /**
     * Add this field in the Record's field sequence.
     */
    public BaseField setupField(int iFieldSeq)
    {
        BaseField field = null;
        //if (iFieldSeq == 0)
        //{
        //  field = new CounterField(this, ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        //  field.setHidden(true);
        //}
        //if (iFieldSeq == 1)
        //{
        //  field = new RecordChangedField(this, LAST_CHANGED, Constants.DEFAULT_FIELD_LENGTH, null, null);
        //  field.setHidden(true);
        //}
        //if (iFieldSeq == 2)
        //{
        //  field = new BooleanField(this, DELETED, Constants.DEFAULT_FIELD_LENGTH, null, new Boolean(false));
        //  field.setHidden(true);
        //}
        if (iFieldSeq == 3)
            field = new StringField(this, PROJECT_NAME, 30, null, null);
        if (iFieldSeq == 4)
        {
            field = new StringField(this, BASE_DIRECTORY, 127, null, "/home/don/workspace/tour/");
            field.addListener(new InitOnceFieldHandler(null));
        }
        if (iFieldSeq == 5)
        {
            field = new StringField(this, SOURCE_DIRECTORY, 127, null, "src/main/java/");
            field.addListener(new InitOnceFieldHandler(null));
        }
        if (iFieldSeq == 6)
        {
            field = new StringField(this, CLASS_DIRECTORY, 127, null, "target/classes/");
            field.addListener(new InitOnceFieldHandler(null));
        }
        if (iFieldSeq == 7)
        {
            field = new StringField(this, ARCHIVE_DIRECTORY, 127, null, "data/archive/");
            field.addListener(new InitOnceFieldHandler(null));
        }
        if (iFieldSeq == 8)
            field = new ResourceTypeField(this, RESOURCE_TYPE, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 9)
            field = new ResourceTypeField(this, CLASS_RESOURCE_TYPE, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 10)
            field = new StringField(this, PACKAGE_NAME, 40, null, null);
        if (iFieldSeq == 11)
        {
            field = new StringField(this, INTERFACE_PACKAGE, Constants.DEFAULT_FIELD_LENGTH, null, ".rec");
            field.addListener(new InitOnceFieldHandler(null));
        }
        if (iFieldSeq == 12)
        {
            field = new StringField(this, THIN_PACKAGE, 40, null, ".thin");
            field.addListener(new InitOnceFieldHandler(null));
        }
        if (iFieldSeq == 13)
        {
            field = new StringField(this, RESOURCE_PACKAGE, 40, null, ".res");
            field.addListener(new InitOnceFieldHandler(null));
        }
        if (iFieldSeq == 14)
            field = new DateTimeField(this, LAST_PACKAGE_UPDATE, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 15)
            field = new StringField(this, PACKAGES_BASE_PATH, 128, null, null);
        if (iFieldSeq == 16)
            field = new StringField(this, PACKAGES_PATH, 128, null, null);
        if (field == null)
            field = super.setupField(iFieldSeq);
        return field;
    }
    /**
     * Add this key area description to the Record.
     */
    public KeyArea setupKey(int iKeyArea)
    {
        KeyArea keyArea = null;
        if (iKeyArea == 0)
        {
            keyArea = this.makeIndex(DBConstants.UNIQUE, "ID");
            keyArea.addKeyField(ID, DBConstants.ASCENDING);
        }
        if (keyArea == null)
            keyArea = super.setupKey(iKeyArea);     
        return keyArea;
    }
    /**
     * AddMasterListeners Method.
     */
    public void addMasterListeners()
    {
        super.addMasterListeners();
        this.getField(ProgramControl.LAST_PACKAGE_UPDATE).setEnabled(false);
    }

}
