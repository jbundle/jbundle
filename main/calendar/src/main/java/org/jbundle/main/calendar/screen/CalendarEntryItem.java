/**
 * @(#)CalendarEntryItem.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.calendar.screen;

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
import org.jbundle.main.calendar.db.*;
import org.jbundle.util.calendarpanel.model.*;
import org.jbundle.base.screen.model.calendar.*;
import javax.swing.*;

/**
 *  CalendarEntryItem - Customize the calendar screen.
 */
public class CalendarEntryItem extends CalendarRecordItem
{
    /**
     * Default constructor.
     */
    public CalendarEntryItem()
    {
        super();
    }
    /**
     * A class to return a CalendarItem from a record.
     */
    public CalendarEntryItem(BaseScreen gridScreen, int iIconField, int iStartDateTimeField, int iEndDateTimeField, int iDescriptionField, int iStatusField)
    {
        this();
        this.init(gridScreen, iIconField, iStartDateTimeField, iEndDateTimeField, iDescriptionField, iStatusField);
    }
    /**
     * Initialize class fields.
     */
    public void init(BaseScreen gridScreen, int iIconField, int iStartDateTimeField, int iEndDateTimeField, int iDescriptionField, int iStatusField)
    {
        super.init(gridScreen, iIconField, iStartDateTimeField, iEndDateTimeField, iDescriptionField, iStatusField);
    }
    /**
     * Get the icon (opt).
     * @return The icon.
     */
    public Object getIcon(int iIconType)
    {
        if (iIconType == CalendarConstants.START_ICON)
            return ((CalendarEntry)this.getMainRecord().getTable().getCurrentTable().getRecord()).getStartIcon();
        return super.getIcon(iIconType);
    }

}
