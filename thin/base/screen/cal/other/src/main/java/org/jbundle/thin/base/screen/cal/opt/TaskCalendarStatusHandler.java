/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.screen.cal.opt;

import org.jbundle.model.Task;
import org.jbundle.util.calendarpanel.StatusListener;

/**
 * Change the status display when the calendar status changes.
 * @author don
 */
public class TaskCalendarStatusHandler extends Object
	implements StatusListener
{
	protected Task task = null;
	protected String oldStatusText = null;

	/**
	 * Constructor.
	 */
	public TaskCalendarStatusHandler()
	{
		super();
	}
	/**
	 * Constructor.
	 */
	public TaskCalendarStatusHandler(Task applet)
	{
		super();
		this.init(applet);
	}
	/**
	 * Constructor.
	 */
	public void init(Task applet)
	{
		this.task = applet;
	}
	
    /**
     * Change the status display.
     * @param status
     */
	@Override
	public void setStatusText(String status) {
		if (status != null)
			oldStatusText = task.getStatusText(0);
		else
			status = oldStatusText;
		task.setStatusText(status);
	}

}
