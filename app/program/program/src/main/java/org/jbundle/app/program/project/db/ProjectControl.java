/**
 * @(#)ProjectControl.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.project.db;

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
import org.jbundle.model.app.program.project.db.*;

/**
 *  ProjectControl - .
 */
public class ProjectControl extends ControlRecord
     implements ProjectControlModel
{
    private static final long serialVersionUID = 1L;

    //public static final int kID = kID;
    public static final int kStartIcon = kControlRecordLastField + 1;
    public static final int kEndIcon = kStartIcon + 1;
    public static final int kStartParentIcon = kEndIcon + 1;
    public static final int kEndParentIcon = kStartParentIcon + 1;
    public static final int kTaskColor = kEndParentIcon + 1;
    public static final int kTaskSelectColor = kTaskColor + 1;
    public static final int kParentTaskColor = kTaskSelectColor + 1;
    public static final int kParentTaskSelectColor = kParentTaskColor + 1;
    public static final int kProjectControlLastField = kParentTaskSelectColor;
    public static final int kProjectControlFields = kParentTaskSelectColor - DBConstants.MAIN_FIELD + 1;

    public static final int kIDKey = DBConstants.MAIN_KEY_FIELD;
    public static final int kProjectControlLastKey = kIDKey;
    public static final int kProjectControlKeys = kIDKey - DBConstants.MAIN_KEY_FIELD + 1;
    /**
     * Default constructor.
     */
    public ProjectControl()
    {
        super();
    }
    /**
     * Constructor.
     */
    public ProjectControl(RecordOwner screen)
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

    public static final String kProjectControlFile = "ProjectControl";
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(kProjectControlFile, bAddQuotes) : super.getTableNames(bAddQuotes);
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
        //if (iFieldSeq == kID)
        //{
        //  field = new CounterField(this, "ID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        //  field.setHidden(true);
        //}
        if (iFieldSeq == kStartIcon)
            field = new ImageField(this, "StartIcon", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kEndIcon)
            field = new ImageField(this, "EndIcon", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kStartParentIcon)
            field = new ImageField(this, "StartParentIcon", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kEndParentIcon)
            field = new ImageField(this, "EndParentIcon", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kTaskColor)
            field = new ColorField(this, "TaskColor", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kTaskSelectColor)
            field = new ColorField(this, "TaskSelectColor", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kParentTaskColor)
            field = new ColorField(this, "ParentTaskColor", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kParentTaskSelectColor)
            field = new ColorField(this, "ParentTaskSelectColor", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kProjectControlLastField)
                field = new EmptyField(this);
        }
        return field;
    }
    /**
     * Add this key area description to the Record.
     */
    public KeyArea setupKey(int iKeyArea)
    {
        KeyArea keyArea = null;
        if (iKeyArea == kIDKey)
        {
            keyArea = this.makeIndex(DBConstants.UNIQUE, "PrimaryKey");
            keyArea.addKeyField(kID, DBConstants.ASCENDING);
        }
        if (keyArea == null) if (iKeyArea < kProjectControlLastKey)
        {
            keyArea = super.setupKey(iKeyArea);     
            if (keyArea == null) if (iKeyArea < kProjectControlLastKey)
                keyArea = new EmptyKey(this);
        }
        return keyArea;
    }

}
