/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.app.program.manual.util;

import java.util.Map;

import org.jbundle.app.program.db.FileHdr;
import org.jbundle.base.db.Record;
import org.jbundle.base.screen.model.BasePanel;
import org.jbundle.base.screen.model.Screen;
import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.thin.base.db.Converter;


/**
 *  WriteJava - Constructor.
 */
public class WriteClassesScreen extends Screen
{
    
    /**
     * Constructor.
     */
    public WriteClassesScreen()
    {
        super();
    }
    /**
     * Constructor.
     */
    public WriteClassesScreen(Record mainFile, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
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
        process.run();
        BasePanel panel = this.getRootScreen();
        panel.free();
    }
    WriteClasses process = null;
    /**
    *
    */
   public void setupSFields()
   {   // Override this to add screen behaviors
       process = new WriteClasses(this, null, null);
       Record query = process.getMainRecord();
       query.getField(FileHdr.FILE_NAME).setupFieldView(this);   // Add this view to the list
   }
}
