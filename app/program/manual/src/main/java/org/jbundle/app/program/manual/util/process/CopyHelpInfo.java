package org.jbundle.app.program.manual.util.process;

import java.util.Map;

import org.jbundle.base.db.Record;
import org.jbundle.base.screen.model.BasePanel;
import org.jbundle.base.screen.model.Screen;
import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.base.util.ScreenConstants;
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
        recClassInfo2.setKeyArea(ClassInfo.kClassNameKey);

        try   {
            recClassInfo.setKeyArea(ClassInfo.kIDKey);
            recClassInfo.close();
            while (recClassInfo.hasNext())
            {
                recClassInfo.next();
                String strClassReference = recClassInfo.getField(ClassInfo.kCopyDescFrom).toString();
                if ((strClassReference != null) && (strClassReference.length() > 0))
                {
                    recClassInfo2.getField(ClassInfo.kClassName).setString(strClassReference);
                    if (recClassInfo2.seek(null))
                    {
                        recClassInfo.edit();
                        if (!recClassInfo2.getField(ClassInfo.kClassDesc).isNull())
                            recClassInfo.getField(ClassInfo.kClassDesc).moveFieldToThis(recClassInfo2.getField(ClassInfo.kClassDesc));
                        recClassInfo.getField(ClassInfo.kClassExplain).moveFieldToThis(recClassInfo2.getField(ClassInfo.kClassExplain));
                        recClassInfo.getField(ClassInfo.kClassHelp).moveFieldToThis(recClassInfo2.getField(ClassInfo.kClassHelp));
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
        recClassInfo.setKeyArea(ClassInfo.kClassNameKey);
        
        Record recMenus = this.getRecord(Menus.kMenusFile);

        try   {
            recMenus.setKeyArea(ClassInfo.kIDKey);
            recMenus.close();
            while (recMenus.hasNext())
            {
                recMenus.next();
                if (recMenus.getField(Menus.kAutoDesc).getState())
                {
                    String strClassReference = recMenus.getField(Menus.kProgram).toString();
                    String strClassType = recMenus.getField(Menus.kType).toString();
                    if ((strClassReference != null) && (strClassReference.length() > 0))
                        if (!strClassType.equalsIgnoreCase("menu"))
                    {
                        if (strClassReference.indexOf('.') != -1)
                            strClassReference = strClassReference.substring(strClassReference.lastIndexOf('.') + 1);
                        recClassInfo.getField(ClassInfo.kClassName).setString(strClassReference);
                        if (recClassInfo.seek(null))
                        {
                            recMenus.edit();
                            if (!recClassInfo.getField(ClassInfo.kClassDesc).isNull())
                                recMenus.getField(Menus.kName).moveFieldToThis(recClassInfo.getField(ClassInfo.kClassDesc));
                            recMenus.getField(Menus.kComment).moveFieldToThis(recClassInfo.getField(ClassInfo.kClassExplain));
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
        this.getRecord(ClassInfo.kClassInfoFile).getField(ClassInfo.kClassName).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        this.getRecord(Menus.kMenusFile).getField(Menus.kName).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
    }
}
