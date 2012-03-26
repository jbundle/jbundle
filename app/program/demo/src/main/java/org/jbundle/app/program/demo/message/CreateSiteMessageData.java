/**
 * @(#)CreateSiteMessageData.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.demo.message;

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
import org.jbundle.thin.base.message.*;

/**
 *  CreateSiteMessageData - .
 */
public class CreateSiteMessageData extends MessageRecordDesc
{
    public static final String CREATE_SITE = "CreateSite";
    /**
     * Default constructor.
     */
    public CreateSiteMessageData()
    {
        super();
    }
    /**
     * CreateSiteMessageData Method.
     */
    public CreateSiteMessageData(MessageDataParent messageDataParent, String strKey)
    {
        this();
        this.init(messageDataParent, strKey);
    }
    /**
     * Init Method.
     */
    public void init(MessageDataParent messageDataParent, String strKey)
    {
        if (strKey == null)
            strKey = CREATE_SITE;
        super.init(messageDataParent, strKey);
    }
    /**
     * Setup sub-Message Data.
     */
    public void setupMessageDataDesc()
    {
        super.setupMessageDataDesc();
        
        this.addMessageDataDesc(new UserInfoMessageData(this, null));
        this.addMessageDataDesc(new MenusMessageData(this, null));
    }

}
