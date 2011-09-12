/*
 *  @(#)TestReptileReport.
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.app.test.vet.shared.report;

import java.awt.*;
import java.util.*;

import org.jbundle.base.db.*;
import org.jbundle.thin.base.util.*;
import org.jbundle.thin.base.db.*;
import org.jbundle.base.db.event.*;
import org.jbundle.base.db.filter.*;
import org.jbundle.base.field.*;
import org.jbundle.base.field.convert.*;
import org.jbundle.base.field.event.*;
import org.jbundle.base.screen.model.*;
import org.jbundle.base.screen.model.util.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.base.screen.model.report.*;
import org.jbundle.app.test.vet.db.*;
import org.jbundle.app.test.vet.shared.db.*;

/**
 *  TestReptileReport - .
 */
public class TestReptileReport extends ReportScreen
{
    /**
     * Default constructor.
     */
    public TestReptileReport()
    {
        super();
    }
    /**
     * Constructor.
     * @param record The main record for this screen.
     * @param itsLocation The location of this component within the parent.
     * @param parentScreen The parent screen.
     * @param fieldConverter The field this screen field is linked to.
     * @param iDisplayFieldDesc Do I display the field desc?
     * @param properties Addition properties to pass to the screen.
     */
    public TestReptileReport(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
    {
        this();
        this.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Initialize class fields.
     */
    public void init(Record record, ScreenLocation itsLocation, BasePanel parentScreen, Converter fieldConverter, int iDisplayFieldDesc, Map<String,Object> properties)
    {
        super.init(record, itsLocation, parentScreen, fieldConverter, iDisplayFieldDesc, properties);
    }
    /**
     * Override this to open the main file.
     * <p />You should pass this record owner to the new main file (ie., new MyNewTable(thisRecordOwner)).
     * @return The new record.
     */
    public Record openMainRecord()
    {
        return new Reptile(this);
    }
    /**
     * Override this to open the other files in the query.
     */
    public void openOtherRecords()
    {
        super.openOtherRecords();
        new Vet(this);
    }
    /**
     * Add all the screen listeners.
     */
    public void addListeners()
    {
        super.addListeners();
        
        this.getMainRecord().setKeyArea(Reptile.kVetIDKey);
        Record recVet = this.getRecord(Vet.kVetFile); //((ReferenceField)this.getMainRecord().getField(Reptile.kVetID)).getReferenceRecord();
        this.getMainRecord().getField(Reptile.kVetID).addListener(new ReadSecondaryHandler(recVet));
        //?this.getRecord(Cat.kCatFile).getField(Animal.kVet).addListener(new ReadSecondaryHandler(recVet));
        //?this.getRecord(Dog.kDogFile).getField(Animal.kVet).addListener(new ReadSecondaryHandler(recVet));
    }
    /**
     * SetupSFields Method.
     */
    public void setupSFields()
    {
        this.addColumn(new MultipleTableFieldConverter(this.getRecord(Reptile.kReptileFile), Reptile.kName));
        this.addColumn(new MultipleTableFieldConverter(this.getRecord(Reptile.kReptileFile), Reptile.kWeight));
        this.addColumn(new MultipleTableFieldConverter(this.getRecord(Reptile.kReptileFile), Reptile.kVetID));
        
        new TestReptileBreak(null, null, this, null, ScreenConstants.DONT_DISPLAY_FIELD_DESC | HtmlConstants.FOOTING_SCREEN, null);
    }

}
