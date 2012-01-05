/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.app.test.manual.vet;

import java.util.Map;

import org.jbundle.app.test.vet.db.Animal;
import org.jbundle.base.db.Record;
import org.jbundle.base.field.convert.MultipleTableFieldConverter;
import org.jbundle.base.screen.model.BasePanel;
import org.jbundle.base.screen.model.Screen;
import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.base.util.ScreenConstants;
import org.jbundle.thin.base.db.Converter;


/**
  Demonstrate localization of data files
 */
public class ResourceScreenTest extends Screen
{
    /**
      MAmAgcy Method.
     */
    public ResourceScreenTest()
    {
        super();
    }
    /**
      MAmAgcy Method.
     */
    public ResourceScreenTest(Record mainFile, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map properties)
    {
        this();
        this.init(mainFile, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
      addListeners Method.
     */
    public void addListeners()
    {
        super.addListeners();
        this.addMainKeyBehavior();
        this.getMainRecord().setKeyArea(Animal.kNameKey);
//+     this.setAdding(false);
    }
    /**
      OpenMainFile Method.
     */
    public Record openMainRecord()
    {
//      return new Cat(this);
        return new Animal(this);
    }
    /**
      Open the Other Files
     */
    public void openOtherRecords()
    {
        super.openOtherRecords();
    }
    /**
      Set up all the screen fields
     */
    public void setupSFields()
    {
//      super.setupSFields();
//      Converter conv2 = this.getMainRecord().getField(Animal.kName);
//      conv2.setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        Converter conv = new MultipleTableFieldConverter(this.getMainRecord(), Animal.kName);
        conv.setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        new MultipleTableFieldConverter(this.getMainRecord(), Animal.kColor).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        new MultipleTableFieldConverter(this.getMainRecord(), Animal.kWeight).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);
        new MultipleTableFieldConverter(this.getMainRecord(), Animal.kVet).setupDefaultView(this.getNextLocation(ScreenConstants.NEXT_LOGICAL, ScreenConstants.ANCHOR_DEFAULT), this, ScreenConstants.DEFAULT_DISPLAY);

    }
}
