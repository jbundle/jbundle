/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.app.program.manual.util.process;

import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.model.ScreenConstants;
import org.jbundle.base.screen.model.BasePanel;
import org.jbundle.base.screen.model.Screen;
import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.main.db.Menus;
import org.jbundle.model.DBException;
import org.jbundle.app.program.db.ClassInfo;
import org.jbundle.thin.base.db.Converter;


/**
 *  WriteResources - Constructor
 */
public class CopyHelpInfo extends Screen
{
    /**
     * Constructor.
     */
    public CopyHelpInfo()
    {
        super();
    }
    /**
     * Constructor.
     */
    public CopyHelpInfo(Record mainFile, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        this();
        this.init(mainFile, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Init.
     */
    public void init(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String, Object> properties)
    {
        super.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     *  Free.
     */
    public void free()
    {
        super.free();
    }
    /**
     * This is a special method that runs some code when this screen is opened as a task.
     */
    public void run()
    {
        this.copyClassHelp();
        
        this.copyMenuHelp();
        
        BasePanel panel = this.getRootScreen();
        panel.free();
    }
    /**
     * Open the main file.
     */
    public Record openMainRecord()
    {
        return new ClassInfo(this);  // Open the Agency File
    }
    /**
     * Open the main file.
     */
    public void openOtherRecords()
    {
        new Menus(this);  // Open the Agency File
    }
    /**
     * This is a special method that runs some code when this screen is opened as a task.
     */
    public void copyClassHelp()
    {
        Record recClassInfo = this.getMainRecord();
        
        Record recClassInfo2 = new ClassInfo(this);
        recClassInfo2.setKeyArea(ClassInfo.CLASS_NAME_KEY);

        try   {
            recClassInfo.setKeyArea(ClassInfo.ID_KEY);
            recClassInfo.close();
            while (recClassInfo.hasNext())
            {
                recClassInfo.next();
                String strClassReference = recClassInfo.getField(ClassInfo.COPY_DESC_FROM).toString();
                if ((strClassReference != null) && (strClassReference.length() > 0))
                {
                    recClassInfo2.getField(ClassInfo.CLASS_NAME).setString(strClassReference);
                    if (recClassInfo2.seek(null))
                    {
                        recClassInfo.edit();
                        if (!recClassInfo2.getField(ClassInfo.CLASS_DESC).isNull())
                            recClassInfo.getField(ClassInfo.CLASS_DESC).moveFieldToThis(recClassInfo2.getField(ClassInfo.CLASS_DESC));
                        recClassInfo.getField(ClassInfo.CLASS_EXPLAIN).moveFieldToThis(recClassInfo2.getField(ClassInfo.CLASS_EXPLAIN));
                        recClassInfo.getField(ClassInfo.CLASS_HELP).moveFieldToThis(recClassInfo2.getField(ClassInfo.CLASS_HELP));
                        recClassInfo.set();
                    }
                }
            }
        } catch (DBException ex)    {
            ex.printStackTrace();
        } finally {
            recClassInfo2.free();
        }
    }
    /**
     * This is a special method that runs some code when this screen is opened as a task.
     */
    public void copyMenuHelp()
    {
        Record recClassInfo = this.getMainRecord();
        recClassInfo.setKeyArea(ClassInfo.CLASS_NAME_KEY);
        
        Record recMenus = this.getRecord(Menus.MENUS_FILE);

        try   {
            recMenus.setKeyArea(ClassInfo.ID_KEY);
            recMenus.close();
            while (recMenus.hasNext())
            {
                recMenus.next();
                if (recMenus.getField(Menus.AUTO_DESC).getState())
                {
                    String strClassReference = recMenus.getField(Menus.PROGRAM).toString();
                    String strClassType = recMenus.getField(Menus.TYPE).toString();
                    if ((strClassReference != null) && (strClassReference.length() > 0))
                        if (!strClassType.equalsIgnoreCase("menu"))
                    {
                        if (strClassReference.indexOf('.') != -1)
                            strClassReference = strClassReference.substring(strClassReference.lastIndexOf('.') + 1);
                        recClassInfo.getField(ClassInfo.CLASS_NAME).setString(strClassReference);
                        if (recClassInfo.seek(null))
                        {
                            recMenus.edit();
                            if (!recClassInfo.getField(ClassInfo.CLASS_DESC).isNull())
                                recMenus.getField(Menus.NAME).moveFieldToThis(recClassInfo.getField(ClassInfo.CLASS_DESC));
                            recMenus.getField(Menus.COMMENT).moveFieldToThis(recClassInfo.getField(ClassInfo.CLASS_EXPLAIN));
                            recMenus.set();
                        }
                    }
                }
            }
        } catch (DBException ex)    {
            ex.printStackTrace();
        }
    }
    public void setupSFields()
    {
        this.getRecord(ClassInfo.CLASS_INFO_FILE).getField(ClassInfo.CLASS_NAME).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(Menus.MENUS_FILE).getField(Menus.NAME).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
    }
}
