/**
 * @(#)ScreenIn.
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
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.app.program.screen.*;
import org.jbundle.model.app.program.db.*;

/**
 *  ScreenIn - Screen In File.
 */
public class ScreenIn extends VirtualRecord
     implements ScreenInModel
{
    private static final long serialVersionUID = 1L;

    //public static final int kID = kID;
    public static final int kScreenInProgName = kVirtualRecordLastField + 1;
    public static final int kScreenOutNumber = kScreenInProgName + 1;
    public static final int kScreenItemNumber = kScreenOutNumber + 1;
    public static final int kScreenFileName = kScreenItemNumber + 1;
    public static final int kScreenFieldName = kScreenFileName + 1;
    public static final int kScreenRow = kScreenFieldName + 1;
    public static final int kScreenCol = kScreenRow + 1;
    public static final int kScreenGroup = kScreenCol + 1;
    public static final int kScreenPhysicalNum = kScreenGroup + 1;
    public static final int kScreenLocation = kScreenPhysicalNum + 1;
    public static final int kScreenFieldDesc = kScreenLocation + 1;
    public static final int kScreenText = kScreenFieldDesc + 1;
    public static final int kScreenAnchor = kScreenText + 1;
    public static final int kScreenControlType = kScreenAnchor + 1;
    public static final int kScreenInLastField = kScreenControlType;
    public static final int kScreenInFields = kScreenControlType - DBConstants.MAIN_FIELD + 1;

    public static final int kIDKey = DBConstants.MAIN_KEY_FIELD;
    public static final int kScreenInProgNameKey = kIDKey + 1;
    public static final int kScreenInLastKey = kScreenInProgNameKey;
    public static final int kScreenInKeys = kScreenInProgNameKey - DBConstants.MAIN_KEY_FIELD + 1;
    /**
     * Default constructor.
     */
    public ScreenIn()
    {
        super();
    }
    /**
     * Constructor.
     */
    public ScreenIn(RecordOwner screen)
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

    public static final String kScreenInFile = "ScreenIn";
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(kScreenInFile, bAddQuotes) : super.getTableNames(bAddQuotes);
    }
    /**
     * Get the name of a single record.
     */
    public String getRecordName()
    {
        return "Screen";
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
        return DBConstants.REMOTE | DBConstants.SHARED_DATA | DBConstants.HIERARCHICAL;
    }
    /**
     * Make a default screen.
     */
    public BaseScreen makeScreen(ScreenLocation itsLocation, BasePanel parentScreen, int iDocMode, Map<String,Object> properties)
    {
        BaseScreen screen = null;
        if ((iDocMode & ScreenConstants.MAINT_MODE) != 0)
            screen = new ScreenInScreen(this, itsLocation, parentScreen, null, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
        else if ((iDocMode & ScreenConstants.DISPLAY_MODE) != 0)
            screen = new ScreenInGridScreen(this, itsLocation, parentScreen, null, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
        else
            screen = super.makeScreen(itsLocation, parentScreen, iDocMode, properties);
        return screen;
    }
    /**
     * Add this field in the Record's field sequence.
     */
    public BaseField setupField(int iFieldSeq)
    {
        BaseField field = null;
        if (iFieldSeq == kScreenInProgName)
            field = new StringField(this, "ScreenInProgName", 40, null, null);
        //if (iFieldSeq == kID)
        //{
        //  field = new CounterField(this, "ID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        //  field.setHidden(true);
        //}
        if (iFieldSeq == kScreenOutNumber)
            field = new ShortField(this, "ScreenOutNumber", 2, null, null);
        if (iFieldSeq == kScreenItemNumber)
            field = new ShortField(this, "ScreenItemNumber", 4, null, null);
        if (iFieldSeq == kScreenFileName)
        {
            field = new StringField(this, "ScreenFileName", 40, null, null);
            field.addListener(new InitOnceFieldHandler(null));
        }
        if (iFieldSeq == kScreenFieldName)
            field = new StringField(this, "ScreenFieldName", 40, null, null);
        if (iFieldSeq == kScreenRow)
            field = new ShortField(this, "ScreenRow", 8, null, null);
        if (iFieldSeq == kScreenCol)
            field = new ShortField(this, "ScreenCol", 4, null, null);
        if (iFieldSeq == kScreenGroup)
            field = new StringField(this, "ScreenGroup", 4, null, null);
        if (iFieldSeq == kScreenPhysicalNum)
            field = new ShortField(this, "ScreenPhysicalNum", 4, null, null);
        if (iFieldSeq == kScreenLocation)
            field = new ScreenLocField(this, "ScreenLocation", 20, null, null);
        if (iFieldSeq == kScreenFieldDesc)
            field = new FieldDescField(this, "ScreenFieldDesc", 30, null, null);
        if (iFieldSeq == kScreenText)
            field = new MemoField(this, "ScreenText", 9999, null, null);
        if (iFieldSeq == kScreenAnchor)
            field = new ScreenAnchorField(this, "ScreenAnchor", 20, null, null);
        if (iFieldSeq == kScreenControlType)
            field = new ControlTypeField(this, "ScreenControlType", 20, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kScreenInLastField)
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
        if (iKeyArea == kScreenInProgNameKey)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, "ScreenInProgName");
            keyArea.addKeyField(kScreenInProgName, DBConstants.ASCENDING);
            keyArea.addKeyField(kScreenItemNumber, DBConstants.ASCENDING);
        }
        if (keyArea == null) if (iKeyArea < kScreenInLastKey)
        {
            keyArea = super.setupKey(iKeyArea);     
            if (keyArea == null) if (iKeyArea < kScreenInLastKey)
                keyArea = new EmptyKey(this);
        }
        return keyArea;
    }

}
