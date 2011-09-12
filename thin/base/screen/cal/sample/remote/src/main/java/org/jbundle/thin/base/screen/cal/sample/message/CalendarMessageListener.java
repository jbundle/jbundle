/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.cal.sample.message;

import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.base.message.BaseMessageListener;
import org.jbundle.thin.base.message.BaseMessageReceiver;
import org.jbundle.util.calendarpanel.model.CalendarModel;


public class CalendarMessageListener extends BaseMessageListener
{
    protected CalendarModel m_model = null;
    /**
     * Constructor.
     */
    public CalendarMessageListener()
    {
        super();
    }
    /**
     * Constructor.
     */
    public CalendarMessageListener(BaseMessageReceiver messageHandler, CalendarModel model)
    {
        this();
        this.init(messageHandler, model);
    }
    /**
     * Constructor.
     */
    public void init(BaseMessageReceiver messageHandler, CalendarModel model)
    {
        super.init(messageHandler, null);
        m_model = model;
    }
    /**
     * Handle this message.
     */
    public int handleMessage(BaseMessage message)
    {
//x     super.run();
        this.getPrice(message);
//x     this.checkInventory();
        return super.handleMessage(message);
    }
    /**
     * Get the price of this product.
     */
    public void getPrice(BaseMessage message)
    {
            String strPrice = (String)message.get("hotelRate");
            System.out.println("Price: " + strPrice);

/**
        LineItem lineItem = m_productItem.getLineItem();
        if (!(lineItem instanceof TourLineItem))
            return;
        try   {
            double dPrive = ((TourLineItem)lineItem).getPrice();
        } catch (java.rmi.RemoteException ex) {
            ex.printStackTrace();
        }
*/
        String strItem = (String)message.get("correlationID");
        int iHash = -1;
        try   {
            iHash = Integer.parseInt(strItem);
        } catch (NumberFormatException ex)  {
        }
        if (iHash != -1)
        {
            CalendarProduct m_productItem = null;
            for (int i = 0; i < m_model.getRowCount(); i++)
            {
                if (m_model.getItem(i).hashCode() == iHash)
                    m_productItem = (CalendarProduct)m_model.getItem(i);
            }
            if (m_productItem != null)
                m_productItem.setStatus(m_productItem.getStatus() & ~(1 << 2));
        }

    }
    /**
     * Check the inventory.
     */
    public void checkInventory()
    {
/**
        LineItem lineItem = m_productItem.getLineItem();
        if (!(lineItem instanceof TourLineItem))
            return;
        try   {
            double dPrive = ((TourLineItem)lineItem).getPrice();
        } catch (java.rmi.RemoteException ex) {
            ex.printStackTrace();
        }
*/
        try   {
            Thread.sleep(4000);     // Sleep for nine seconds
        } catch (InterruptedException e)    {
        }
//      m_productItem.removeIcon(CalendarConstants.START_ICON + 3);   // HACK Serious synchronization problems!!!
//          m_productItem.setStatus(m_productItem.getStatus() & ~(1 << 3));
    }
}
