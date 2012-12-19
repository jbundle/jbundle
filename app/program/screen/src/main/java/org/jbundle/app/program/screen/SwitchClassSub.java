/**
 * @(#)SwitchClassSub.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.screen;

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
import org.jbundle.base.model.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.jbundle.base.screen.model.*;
import org.jbundle.base.screen.model.util.*;

/**
 *  SwitchClassSub - .
 */
public class SwitchClassSub extends SwitchSubScreenHandler
{
    /**
     * Default constructor.
     */
    public SwitchClassSub()
    {
        super();
    }
    /**
     * SwitchClassSub Method.
     */
    public SwitchClassSub(BaseField field, BasePanel screenParent, BasePanel subScreen)
    {
        this();
        this.init(field, screenParent, subScreen);
    }
    /**
     * Initialize class fields.
     */
    public void init(Record record)
    {
        super.init(record);
    }
    /**
     * GetSubScreen Method.
     */
    public BasePanel getSubScreen(BasePanel parentScreen, ScreenLocation screenLocation, Map<String,Object> properties, int screenNo)
    {
        switch (screenNo)
        {
        case 0:
            return new LogicFileGridScreen(null, screenLocation, parentScreen, null, ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
        case 1:
            return new FieldDataGridScreen(null, screenLocation, parentScreen, null, ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
        case 2:
            return new KeyInfoGridScreen(null, screenLocation, parentScreen, null, ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
        case 3:
            return new ClassFieldsGridScreen(null, screenLocation, parentScreen, null, ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
        case 4:
            return new ScreenInGridScreen(null, screenLocation, parentScreen, null, ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
        case 5:
            return new ClassInfoDescScreen(null, screenLocation, parentScreen, null, ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
        case 6:
            return new ClassInfoHelpScreen(null, screenLocation, parentScreen, null, ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
        case 7:
            return new ClassResourceGridScreen(null, screenLocation, parentScreen, null, ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
        case 8:
            return new ClassIssueGridScreen(null, screenLocation, parentScreen, null, ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
        case 9:
            return new FileHdrScreen(null, screenLocation, parentScreen, null, ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
        case 10:
            return new ExportRecordsScreen(null, screenLocation, parentScreen, null, ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties);
        }
        return null;
    }

}
