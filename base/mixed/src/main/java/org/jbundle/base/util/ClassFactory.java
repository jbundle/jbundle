package org.jbundle.base.util;

import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.db.RecordOwner;
import org.jbundle.base.screen.model.BasePanel;
import org.jbundle.base.screen.model.BaseScreen;
import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.base.services.ClassInfoService;
import org.jbundle.base.services.Services;
import org.jbundle.main.screen.DetailGridScreen;
import org.jbundle.thin.base.db.Converter;
import org.jbundle.util.osgi.finder.ClassServiceImpl;

public class ClassFactory {
	private ClassFactory() {
	}
	
    public static BaseScreen createScreenFromClassName(String strScreenClass, Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object>  properties)
    {
		strScreenClass = ClassFactory.fixClassName(strScreenClass, Utility.getRecordOwner(record));
		BaseScreen screen = BaseScreen.makeNewScreen(strScreenClass, itsLocation, parentScreen, iDisplayFieldDesc, properties, false);
		screen.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
		return screen;
    }
    public static BaseScreen createScreenFromClassName(String strScreenClass, Record recHeader, Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object>  properties)
    {
    	RecordOwner recordOwner = null;
    	if (record != null)
    		recordOwner = Utility.getRecordOwner(record);
    	else
    		recordOwner = Utility.getRecordOwner(recHeader);
    	if (recordOwner != null)
    		if (recordOwner.getTask().getApplication().getSystemRecordOwner() instanceof RecordOwner)
    			recordOwner = (RecordOwner)recordOwner.getTask().getApplication().getSystemRecordOwner();
		strScreenClass = ClassFactory.fixClassName(strScreenClass, recordOwner);
		BaseScreen screen = BaseScreen.makeNewScreen(strScreenClass, itsLocation, parentScreen, iDisplayFieldDesc, properties, false);
		if (screen instanceof DetailGridScreen)	// Always
			((DetailGridScreen)screen).init(recHeader, record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
		else
		{
			screen.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
			Utility.getLogger().warning("Attempt to create a detail screen for: " + strScreenClass);
		}
		return screen;
    }
    /**
     * FixClassName - Get the entire class path from the classname.
     * NOTE: I do not free the classInfo service in case I will be using it again with this recordOwner.
     * @param className
     * @param recordOwner
     * @return
     */
	public static String fixClassName(String className, RecordOwner recordOwner)
	{
		if (className.startsWith("."))
			ClassServiceImpl.getFullClassName(className);
		if (className.contains("."))
			return className;
    	ClassInfoService classInfo = Services.getClassInfo(recordOwner, className, true);
    	if (classInfo == null)
    		classInfo = Services.createClassInfo(recordOwner, className, true);
    	if (classInfo != null)
    		className = classInfo.getFullClassName();
		return className;
	}
}
