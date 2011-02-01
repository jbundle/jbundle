package org.jbundle.thin.base.screen.cal.sample.multitask;

import java.util.Date;

import javax.swing.ImageIcon;

import org.jbundle.thin.base.screen.BaseApplet;
import org.jbundle.thin.base.screen.cal.opt.CachedItem;
import org.jbundle.thin.base.screen.cal.popup.ProductTypeInfo;
import org.jbundle.util.calendarpanel.model.CalendarConstants;
import org.jbundle.util.calendarpanel.model.CalendarItem;
import org.jbundle.util.calendarpanel.model.CalendarModel;
import org.jbundle.thin.base.util.Application;


public class CalendarProduct extends CachedItem
    implements CalendarItem
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
    public void setIcon(ImageIcon icon, int iIconType)
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
        productItem.setStatus(productItem.getStatus() | (1 << 3) | (1 << 2) | (1 << 1));

        if (application == null)
            application = BaseApplet.getSharedInstance().getApplication();
        application.getTaskScheduler().addTask(new CalendarDateChangeTask(application, strParams, productItem, dateStart, dateEnd));
    }
    /**
     * Get the status of this object.
     */
    public int getStatus()
    {
        return super.getStatus();
    }
    /**
     * Set the status of this item.
     */
    public int setStatus(int iStatus)
    {
        return super.setStatus(iStatus);
    }
}
