/**
 *  @(#)MenusMessageData.
 *  Copyright © 2010 tourapp.com. All rights reserved.
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
import org.jbundle.base.screen.model.*;
import org.jbundle.base.screen.model.util.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.thin.base.message.*;
import org.jbundle.main.db.*;

/**
 *  MenusMessageData - .
 */
public class MenusMessageData extends MessageRecordDesc
{
    public static final String SITE_NAME = "SiteName";
    public static final String DOMAIN_NAME = "domainName";
    public static final String XSL_TEMPLATE_PATH = "xslTemplate";
    public static final String SITE_TEMPLATE_MENU = "SiteTemplateCode";
    public static final String SITE_HOME_MENU = "SiteHomeCode";
    public static final String SITE_PREFIX = "sitePrefix";
    public static final String[] PROPERTIES = {
            XSL_TEMPLATE_PATH,
            DOMAIN_NAME,
            SITE_NAME,
            SITE_TEMPLATE_MENU,
            SITE_HOME_MENU,
            SITE_PREFIX,
    };
    /**
     * Default constructor.
     */
    public MenusMessageData()
    {
        super();
    }
    /**
     * MenusMessageData Method.
     */
    public MenusMessageData(MessageDataParent messageDataParent, String strKey)
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
            strKey = Menus.kMenusFile;
        super.init(messageDataParent, strKey);
    }
    /**
     * Setup sub-Message Data.
     */
    public void setupMessageDataDesc()
    {
        super.setupMessageDataDesc();
        
        for (String key: PROPERTIES)
        {
            this.addMessageFieldDesc(key, String.class, MessageFieldDesc.REQUIRED, null);
        }
    }

}
