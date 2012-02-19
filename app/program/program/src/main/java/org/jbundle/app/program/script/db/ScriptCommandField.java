/**
 * @(#)ScriptCommandField.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.script.db;

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
import org.jbundle.main.db.*;
import org.jbundle.app.program.script.screen.*;
import org.jbundle.app.program.script.data.importfix.base.*;
import org.jbundle.app.program.db.*;
import org.jbundle.util.osgi.finder.*;

/**
 *  ScriptCommandField - .
 */
public class ScriptCommandField extends StringPopupField
{
    /**
     * Default constructor.
     */
    public ScriptCommandField()
    {
        super();
    }
    /**
     * Constructor.
     * @param record The parent record.
     * @param strName The field name.
     * @param iDataLength The maximum string length (pass -1 for default).
     * @param strDesc The string description (usually pass null, to use the resource file desc).
     * @param strDefault The default value (if object, this value is the default value, if string, the string is the default).
     */
    public ScriptCommandField(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
    {
        this();
        this.init(record, strName, iDataLength, strDesc, strDefault);
    }
    /**
     * Initialize class fields.
     */
    public void init(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
    {
        super.init(record, strName, iDataLength, strDesc, strDefault);
    }
    /**
     * Get the conversion Map.
     */
    public String[][] getPopupMap()
    {
        String string[][] = {
            {DBConstants.BLANK, "(none)"}, 
            {Script.RUN, "Run script"}, 
            {Script.RUN_REMOTE, "Run remote script"}, 
            {Script.SEEK, "Read record"}, 
            {Script.COPY_RECORDS, "Copy Records"}, 
            {Script.COPY_FIELDS, "Copy Fields"},
            {Script.COPY_DATA, "Copy Data"},
        };
        return string;
    }

}
