package org.jbundle.thin.base.screen.cal.sample.multitask;

import java.util.Date;

import org.jbundle.model.App;
import org.jbundle.thin.base.screen.cal.opt.CachedItem;
import org.jbundle.thin.base.screen.cal.opt.DateChangeTask;


public class CalendarDateChangeTask extends DateChangeTask
{

    /**
     * Constructor.
     */
    public CalendarDateChangeTask()
    {
        super();
    }
    /**
     * Constructor.
     */
    public CalendarDateChangeTask(App application, String strParams, CachedItem productItem, Date dateStart, Date dateEnd)
    {
        this();
        this.init(application, strParams, productItem, dateStart, dateEnd);
    }
    /**
     * Constructor.
     */
    public void init(App application, String strParams, CachedItem productItem, Date dateStart, Date dateEnd)
    {
        super.init(application, strParams, productItem, dateStart, dateEnd);
    }
    /**
     * Start running this thread.
     */
    public void runTask()
    {
        super.runTask();
        this.getPrice();
        this.checkInventory();
    }
    /**
     * Get the price of this product.
     */
    public void getPrice()
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
            Thread.sleep(9000);     // Sleep for nine seconds
        } catch (InterruptedException e)    {
        }
//      m_productItem.removeIcon(CalendarConstants.START_ICON + 2);   // HACK Serious synchronization problems!!!
        m_productItem.setStatus(m_productItem.getStatus() & ~(1 << 2));
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
        m_productItem.setStatus(m_productItem.getStatus() & ~(1 << 3));
    }
}
