/**
 * @(#)DemoRegistrationScreen.
 * Copyright © 2013 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.demo;

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
import org.jbundle.main.user.db.*;
import org.jbundle.thin.base.thread.*;
import org.jbundle.base.thread.*;
import org.jbundle.thin.base.db.buff.*;
import java.net.*;
import java.io.*;
import org.jbundle.app.program.script.scan.*;
import org.jbundle.app.program.manual.convert.*;

/**
 *  DemoRegistrationScreen - .
 */
public class DemoRegistrationScreen extends BaseRegistrationScreen
{
    /**
     * Default constructor.
     */
    public DemoRegistrationScreen()
    {
        super();
    }
    /**
     * Constructor.
     * @param record The main record for this screen.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     * @param properties Addition properties to pass to the screen.
     */
    public DemoRegistrationScreen(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
    {
        this();
        this.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Initialize class fields.
     */
    public void init(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
    {
        super.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * GetSiteProperties Method.
     */
    public Map<String, Object> getSiteProperties()
    {
        Map<String, Object> properties = super.getSiteProperties();
        
        String strDomain = this.getProperty(DBParams.DOMAIN);
        if ((strDomain == null) || (strDomain.length() == 0))
            strDomain = DEFAULT_DOMAIN;
        if (strDomain.indexOf('.') != strDomain.lastIndexOf('.'))
            strDomain = strDomain.substring(strDomain.indexOf('.') + 1);
        strDomain = "demo." + strDomain;    // Demo sub-domain
        properties.put(DBParams.DOMAIN, strDomain);
        
        return properties;
    }

}
