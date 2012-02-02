/*
// Throw up a grid screen for a object table.
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.app.test.manual.vet;

import java.util.Map;

import org.jbundle.app.test.vet.db.Animal;
import org.jbundle.app.test.vet.db.AnimalVets;
import org.jbundle.app.test.vet.db.Dog;
import org.jbundle.app.test.vet.db.Vet;
import org.jbundle.base.db.Record;
import org.jbundle.base.screen.model.BasePanel;
import org.jbundle.base.screen.model.GridScreen;
import org.jbundle.base.screen.model.util.ScreenLocation;
import org.jbundle.thin.base.db.Converter;


/**
  Agency File Maintenance
 */
public class GridTestAnimalVets extends GridScreen
{
    /**
      MAmAgcy Method.
     */
    public GridTestAnimalVets()
    {
        super();
    }
    /**
      MAmAgcy Method.
     */
    public GridTestAnimalVets(Record mainFile,ScreenLocation itsLocation,BasePanel parentScreen,Converter fieldConverter,int iDisplayFieldDesc, Map properties)
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
        this.getMainRecord().setKeyArea(Animal.NAME_KEY);
//      this.setEditing(true);
    }
    /**
      OpenMainFile Method.
     */
    public Record  openMainRecord()
    {
        return new AnimalVets(this);
    }
    /**
      Open the Other Files
     */
    public void  openOtherRecords()
    {
        super.openOtherRecords();
    }
    /**
      Set up all the screen fields
     */
    public void  setupSFields()
    {
        Record record = this.getMainRecord();
        this.addColumn(record.getField(Dog.DOG_FILE, Animal.NAME));
        this.addColumn(record.getField(Dog.DOG_FILE, Animal.COLOR));
        this.addColumn(record.getField(Vet.VET_FILE, Vet.NAME));
    }
}
