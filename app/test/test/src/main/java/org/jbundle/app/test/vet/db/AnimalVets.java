/**
 * @(#)AnimalVets.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.test.vet.db;

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
import org.jbundle.base.model.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.jbundle.model.app.test.vet.db.*;

/**
 *  AnimalVets - Test Query.
 */
public class AnimalVets extends QueryRecord
     implements AnimalVetsModel
{
    private static final long serialVersionUID = 1L;
    /**
     * Default constructor.
     */
    public AnimalVets()
    {
        super();
    }
    /**
     * Constructor.
     */
    public AnimalVets(RecordOwner screen)
    {
        this();
        this.init(screen);
    }
    /**
     * Initialize class fields.
     */
    public void init(RecordOwner screen)
    {
        super.init(screen);
    }

    public static final String kAnimalVetsFile = "AnimalVets";
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(kAnimalVetsFile, bAddQuotes) : super.getTableNames(bAddQuotes);
    }
    /**
     * Get the name of a single record.
     */
    public String getRecordName()
    {
        return "AnimalVets";
    }
    /**
     * Get the Database Name.
     */
    public String getDatabaseName()
    {
        return "test";
    }
    /**
     * Is this a local (vs remote) file?.
     */
    public int getDatabaseType()
    {
        return DBConstants.REMOTE | DBConstants.USER_DATA;
    }
    /**
     * Override this to Setup all the records for this query.
     * Only used for querys and abstract-record queries.
     * Actually adds records not tables, but the records aren't physically
     * added here, the record's tables are added to my table.
     * @param The recordOwner to pass to the records that are added.
     * @see addTable.
     */
    public void addTables(RecordOwner recordOwner)
    {
        //?   this.addTable(new Animal(recordOwner));
        this.addTable(new Dog(recordOwner));
        this.addTable(new Vet(recordOwner));
    }
    /**
     * SelectFields Method.
     */
    public void selectFields()
    {
        this.setSelected(false);    // de-select all
        super.selectFields();
        
        //?this.getField(Animal.ANIMAL_FILE, Animal.ID).setSelected(true);
        //?this.getField(Animal.ANIMAL_FILE, Animal.NAME).setSelected(true);
        //?this.getField(Animal.ANIMAL_FILE, Animal.COLOR).setSelected(true);
        //?this.getField(Animal.ANIMAL_FILE, Animal.WEIGHT).setSelected(true);
        
        this.getField(Dog.DOG_FILE, Animal.ID).setSelected(true);
        this.getField(Dog.DOG_FILE, Animal.NAME).setSelected(true);
        this.getField(Dog.DOG_FILE, Animal.COLOR).setSelected(true);
        this.getField(Dog.DOG_FILE, Animal.WEIGHT).setSelected(true);
        
        this.getField(Vet.VET_FILE, Vet.NAME).setSelected(true);
    }
    /**
     * SetupRelationships Method.
     */
    public void setupRelationships()
    {
        //?this.addRelationship(DBConstants.LEFT_INNER, this.getRecord(Animal.ANIMAL_FILE), this.getRecord(Vet.VET_FILE), Animal.VET, Vet.ID);
        this.addRelationship(DBConstants.LEFT_INNER, this.getRecord(Dog.DOG_FILE), this.getRecord(Vet.VET_FILE), Animal.VET, Vet.ID);
    }

}
