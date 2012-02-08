/**
 * @(#)SiteRegistrationScreen.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.demo;

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
import org.jbundle.app.program.demo.message.*;
import org.jbundle.base.message.core.trx.*;
import org.jbundle.main.user.db.*;

/**
 *  SiteRegistrationScreen - Reservation site registration.
 */
public class SiteRegistrationScreen extends BaseRegistrationScreen
{
    public static final String DEFAULT_SITE_TEMPLATE = "site.tourgeek.com";
    /**
     * Default constructor.
     */
    public SiteRegistrationScreen()
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
    public SiteRegistrationScreen(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
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
     * Get the screen display title.
     */
    public String getTitle()
    {
        return "Reservation site registration";
    }
    /**
     * Add all the screen listeners.
     */
    public void addListeners()
    {
        this.setProperty("terms", "terms");   // Terms resource EY (change for different terms)
        
        super.addListeners();
    }
    /**
     * GetSiteProperties Method.
     */
    public Map<String, Object> getSiteProperties()
    {
        Map<String, Object> properties = super.getSiteProperties();
        
        properties.put(BaseRegistrationScreen.SITE_TEMPLATE_CODE_PARAM, DEFAULT_SITE_TEMPLATE);
        properties.put(BaseRegistrationScreen.SITE_NAME_TEMPLATE_PARAM, "{0} Site");
        properties.put(MenusMessageData.SITE_TEMPLATE_MENU, "site");
        properties.put(MenusMessageData.SITE_HOME_MENU, "siteStart");
        properties.put(TrxMessageHeader.DESTINATION_PARAM, "http://tour-0020.tourgeek.com:8181/xmlws");
        
        return properties;
    }
    /**
     * AddOtherSFields Method.
     */
    public void addOtherSFields()
    {
        super.addOtherSFields();
        
        // Subdomain field
        UserInfo recUserInfo = (UserInfo)this.getRecord(UserInfo.USER_INFO_FILE);
        BaseField field = new StringField(recUserInfo, "Sub-Domain", 10, null, null);
        field.setVirtual(true);
        
        recUserInfo.addPropertiesFieldBehavior(field, MenusMessageData.SITE_PREFIX);
        
        field.setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        new SStaticString(this.getNextLocation(ScreenConstants.RIGHT_OF_LAST, ScreenConstants.DONT_SET_ANCHOR), this, "xxxxx.tourgeek.com");
    }

}
