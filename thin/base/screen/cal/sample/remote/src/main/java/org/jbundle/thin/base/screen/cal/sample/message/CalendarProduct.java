/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.cal.sample.message;

import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.ImageIcon;

import org.jbundle.model.message.MessageManager;
import org.jbundle.model.message.MessageSender;
import org.jbundle.thin.base.message.BaseMessageReceiver;
import org.jbundle.thin.base.message.MapMessage;
import org.jbundle.thin.base.screen.BaseApplet;
import org.jbundle.thin.base.screen.cal.opt.CachedItem;
import org.jbundle.thin.base.screen.cal.popup.ProductTypeInfo;
import org.jbundle.thin.base.util.Application;
import org.jbundle.util.calendarpanel.model.CalendarConstants;
import org.jbundle.util.calendarpanel.model.CalendarItem;
import org.jbundle.util.calendarpanel.model.CalendarModel;


public class CalendarProduct extends CachedItem implements CalendarItem
{

    /**
     * Constructor.
     */
    public CalendarProduct(CalendarModel model, Date startTime, Date endTime, String description, String strProductType, String strMeals, int iStatus)
    {
        super();
        this.init(model, startTime, endTime, description, strProductType, strMeals, iStatus);
    }
    /**
     * Constructor.
     */
    public void init(CalendarModel model, Date startTime, Date endTime, String description, String strProductType, String strMeals, int iStatus)
    {
        ProductTypeInfo productType = ProductTypeInfo.getProductType(strProductType);
        String[] rgstrMeals = new String[2];
        for (int i = 0; i < rgstrMeals.length; i++)
        {
            rgstrMeals[i] = strMeals;
        }
        super.init(model, productType, startTime, endTime, description, rgstrMeals, iStatus);
        this.setIcon(BaseApplet.getSharedInstance().loadImageIcon("tour/buttons/Lookup.gif", "Lookup"), CalendarConstants.START_ICON + 1);
        this.setIcon(BaseApplet.getSharedInstance().loadImageIcon("tour/buttons/Price.gif", "Price"), CalendarConstants.START_ICON + 2);
        this.setIcon(BaseApplet.getSharedInstance().loadImageIcon("tour/buttons/Inventory.gif", "Inventory"), CalendarConstants.START_ICON + 3);
    }
    /**
     * I'm done with this item, free the resources.
     */
    public void free()
    {
        super.free();
    }
    /**
     * Delete this item.
     */
    public boolean remove()
    {
        return super.remove();
    }
    /**
     * Change the start time of this service.
     */
    public Date setStartDate(Date time)
    {
        return super.setStartDate(time);
    }
    /**
     * Change the ending time of this service.
     */
    public Date setEndDate(Date time)
    {
        return super.setEndDate(time);
    }
    /**
     * Set the icon (opt).
     */
    public void setIcon(Object icon, int iIconType)
    {
        super.setIcon(icon, iIconType);
    }
    /**
     * Get the display window for this object.
     */
    public Object getVisualJavaBean(int iPanelType)
    {
        return super.getVisualJavaBean(iPanelType);
    }
    /**
     * Change the date of the actual data.
     * Override this to change the date.
     */
    public void changeRemoteDate(Application application, String strParams, CachedItem productItem, Date dateStart, Date dateEnd)
    {
//+     this.setIcon(BaseApplet.getSharedInstance().loadImageIcon("tour/buttons/Lookup.gif", "Lookup"), CalendarConstants.START_ICON + 1);
//x     this.setIcon(BaseApplet.getSharedInstance().loadImageIcon("tour/buttons/Price.gif", "Price"), CalendarConstants.START_ICON + 2);
        productItem.setStatus(productItem.getStatus() | (1 << 2));
//+     this.setIcon(BaseApplet.getSharedInstance().loadImageIcon("tour/buttons/Inventory.gif", "Inventory"), CalendarConstants.START_ICON + 3);
//+     if (application == null)
//+         application = BaseApplet.getSharedInstance().getApplication();
        
// Step 1 - Send a message to the subscriber that I want to lookup the date, price, and inventory.
//+     CalendarDateChangeMessage message = new CalendarDateChangeMessage(strParams, productItem, dateStart, dateEnd);
        

// Step 2 - Listen for messages that modify this object's information
        // I could potentially get 3 messages back: 1. chg date 2. set price 3. set avail.

//      message.getModel();
//x     application.getTaskScheduler().addTask(message);
//+     this.sendMessage(new CalendarDateChangeMessage(application, strParams, productItem, dateStart, dateEnd));
        
        BaseApplet applet = BaseApplet.getSharedInstance();

        try   {
//+         if (database == null)
            MessageSender sendQueue = null;
            String strSendQueueName = "lookupHotelRate";
            Map<String,Object> properties = new Hashtable<String,Object>();
            properties.put("rateType", "Rack");
            properties.put("roomClass", "Single");

            MessageManager messageManager = applet.getApplication().getMessageManager();
            sendQueue = messageManager.getMessageQueue(strSendQueueName, null).getMessageSender();
if (gbFirstTime)
{
    BaseMessageReceiver handler = (BaseMessageReceiver)messageManager.getMessageQueue("sendHotelRate", null).getMessageReceiver();
    /*?JMessageListener listener =*/ new CalendarMessageListener(handler, m_model);
    
    gbFirstTime = false;
}
            properties.put("correlationID", Integer.toString(this.hashCode()));

            sendQueue.sendMessage(new MapMessage(null, properties)); // See ya!
        } catch (Exception ex)  {
            ex.printStackTrace();
        }
    }

public static boolean gbFirstTime = true;
}
