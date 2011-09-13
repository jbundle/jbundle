/**
 * @(#)UserJavaField.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.user.db;

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
import org.jbundle.thin.base.screen.*;
import org.jbundle.main.db.*;
import org.jbundle.main.user.screen.*;

/**
 *  UserJavaField - .
 */
public class UserJavaField extends StringPopupField
{
    public static final String DEFAULT = "";
    public static final String WEBSTART = "WebStart";
    public static final String PLUG_IN = "Plug-in";
    public static final String YES = "Yes";
    public static final String NO = "No";
    /**
     * Default constructor.
     */
    public UserJavaField()
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
    public UserJavaField(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
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
            {DEFAULT, "Use default Java"}, 
            {WEBSTART, "Java Web Start"}, 
            {PLUG_IN, "JDK Plug-in"}, 
            {YES, "Java Applet Tags"},
            {NO, "No Java - Use HTML"}
        };
        return string;
    }

}
