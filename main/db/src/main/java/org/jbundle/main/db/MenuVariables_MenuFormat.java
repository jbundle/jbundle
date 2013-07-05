/**
 * @(#)MenuVariables_MenuFormat.
 * Copyright © 2013 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.db;

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

/**
 *  MenuVariables_MenuFormat - .
 */
public class MenuVariables_MenuFormat extends IntegerField
{
    /**
     * Default constructor.
     */
    public MenuVariables_MenuFormat()
    {
        super();
    }
    /**
     * MenuVariables_MenuFormat Method.
     */
    public MenuVariables_MenuFormat(Record record, String strName, int iDataLength, String strDesc, Object strDefault)
    {
        this();
        this.init(record, strName, iDataLength, strDesc, strDefault);
    }
    /**
     * Initialize the field.
     */
    public int initField(boolean displayOption)
    {
        return this.setString(""/**ID_LYNX_ICON*/, displayOption, DBConstants.INIT_MOVE);
    }

}
