/*
// Test the basic object processing functions.
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.test.manual.test.db.object;

import org.jbundle.app.test.vet.db.Animal;
import org.jbundle.app.test.vet.db.AnimalVets;
import org.jbundle.app.test.vet.db.Cat;
import org.jbundle.app.test.vet.db.Dog;
import org.jbundle.app.test.vet.db.Vet;
import org.jbundle.base.db.BaseTable;
import org.jbundle.base.db.GridTable;
import org.jbundle.base.db.Record;
import org.jbundle.base.db.event.DisplayReadHandler;
import org.jbundle.base.db.filter.SubFileFilter;
import org.jbundle.base.model.DBConstants;
import org.jbundle.model.DBException;


// SimpleForm is the data entry form for the sample
public class ObjectTest extends BaseAnimalTest
{

    /**
      *Creates new TestAll
      */
    public ObjectTest(String strTestName)
    {
        super(strTestName);
    }
    
    Vet vet = null;
    Cat cat = null;
    Dog dog = null;
    /**
     * Set up for the test.
     */
    public void setUp()
    {
    }
    /**
     * Tear down for the test.
     */
    public void tearDown()
    {
    }
    /*
     * Do the test.
     */
    public void testDatabase()
    {
        int iCount = 0;
// Now, start the tests....
        Animal animal = null;
        category.debug("Open the Animal Table\n");
        Animal recAnimal = new Animal(vet.getRecordOwner());

        BaseTable animalTable = recAnimal.getTable();
        Object bookmark = null;
        Object dbBookmark = null;

        try   {
//          recAnimal.setKeyArea(Animal.NAME_KEY);
            iCount = 0;
            while ((animal = (Animal)animalTable.move(+1)) != null)
            {
                category.debug(animal.toString());
                iCount++;
                if (iCount == 6)
                {
                    bookmark = animalTable.getHandle(DBConstants.OBJECT_ID_HANDLE);
                    dbBookmark = animalTable.getHandle(DBConstants.DATA_SOURCE_HANDLE);
                }
            }
            assertTrue("Error wrong count 9 Count: " + iCount + "\n", iCount == 9);
        category.debug("Set a bookmark\n");
        if (bookmark != null)
            animal = (Animal)animalTable.setHandle(bookmark, DBConstants.OBJECT_ID_HANDLE);
        else
            animal = (Animal)animalTable.setHandle(dbBookmark, DBConstants.DATA_SOURCE_HANDLE);
        if (animal != null)
        {
            category.debug("Success\n" + animal.toString());
        }
        else
            fail("Failure - no bookmark\n");
        } catch (DBException ex)    {
            iCount = -1;
        }
        category.debug("Count:" + iCount);

        try   {
            category.debug("Now, update animal class");
            animal.edit();
            animal.getField(Animal.NAME).setString("New Animal Name");
            animal.set();
        } catch (DBException ex)    {
            ex.printStackTrace();
            fail("Failure\n");
        }

        iCount = 0;
        category.debug("Now, test the sub-file behaviors on the animal class");
        try   {
            vet.close();
            vet.next();
            category.debug(vet.toString());
            animal = null;
            recAnimal = new Animal(vet.getRecordOwner());
            recAnimal.addListener(new SubFileFilter(vet));
            animalTable = recAnimal.getTable();

            iCount = 0;
            while ((animal = (Animal)animalTable.move(+1)) != null)
            {
                category.debug(animal.toString());
                iCount++;
            }
        } catch (DBException e)   {
            category.debug("Could not delete record: Error" + e.getMessage() + "\n");
            category.debug(animal.toString());
            fail("Failure");;
        }
        category.debug("Count for this vet: " + iCount);

        
                
        iCount = 0;
        category.debug("Now, test the query record stuff");
        AnimalVets animalVets = new AnimalVets(vet.getRecordOwner());
        BaseTable tblAnimalVets = animalVets.getTable();
        try   {
            animalVets.close();
            Record rec2 = (Record)tblAnimalVets.move(+1);
            category.debug(rec2.toString());
            rec2 = (Record)tblAnimalVets.move(+1);
            category.debug(rec2.toString());

            while (tblAnimalVets.hasNext())
            {
                Record rec = (Record)tblAnimalVets.move(+1);
                category.debug(rec.toString());
//              category.debug(animalVets.toString());
                iCount++;
            }
        } catch (DBException e)   {
            category.debug("Could not delete record: Error" + e.getMessage() + "\n");
            category.debug(animalVets.toString());
            fail("Failure");;
        }
        category.debug("Count:" + iCount);
//      animalVets.free();
//      animalVets = null;
        iCount = 0;
        category.debug("Now, test the query record stuff using behaviors");
        recAnimal = new Animal(vet.getRecordOwner());
        animalTable = recAnimal.getTable();
//x     recAnimal.getField(Animal.VET).addListener(new ReadSecondaryHandler(vet));
        recAnimal.addListener(new DisplayReadHandler(Animal.VET, vet, Vet.kID));
        try   {
            recAnimal.close();
            while ((animal = (Animal)animalTable.move(+1)) != null)
            {
                category.debug(animal.toString());
                category.debug(vet.toString());
                iCount++;
            }
        } catch (DBException e)   {
            category.debug("Could not delete record: Error " + e.getMessage() + "\n");
            category.debug(animalVets.toString());
            fail("Failure");;
        }
        category.debug("Count:" + iCount);

// End of tests, close the table    
        category.debug("Test complete.\n");
    }
    /**
     *
     */
    public void testGrid()
    {
        int iCount;

        category.debug("Now, let's test a ObjectTable.\n");
        category.debug("Open table.\n");
        Animal animal = new Animal(vet.getRecordOwner());
        BaseTable animalTable = animal.getTable();
        try   {
            animal.open();              // Open the table
        } catch (DBException e)   {
            String strError = e.getMessage();
            category.debug(strError);
            System.exit(0);
        }

        category.debug("Count keys in both indexes.\n");
        try   {
            animal.setKeyArea(DBConstants.MAIN_KEY_AREA);
            iCount = 0;
            animal.close();
            while (animalTable.hasNext())
            {
                animal = (Animal)animalTable.move(+1);
                iCount++;
            }
            animalTable.close();
            category.debug("Main index: Count: " + iCount + "\n");
//+            assertTrue("Error wrong count 9 Count: " + iCount + "\n", iCount == 9);
        } catch (DBException e)   {
            category.debug("Error reading through file: Error" + e.getMessage() + "\n");
            category.debug(animal.toString());
            System.exit(0);
        }
// Now, start the tests....

        GridTable animalList = new GridTable(null, animalTable.getRecord());
        try   {
            animalList.open();
            category.debug("First, lets read through the table using MoveTo.\n");
            int index = 0;
            Record newRecord = null;
            for (index = 0; ; index++)
            {
                newRecord = (Record)animalList.get(index);
                if (newRecord == null)
                    break;
            }
            category.debug("Move to count: " + index);
            assertTrue("Error wrong count 9 Count: " + index + "\n", index == 9);

            category.debug("Now, lets access a record in the record buffer.\n");
            newRecord = (Record)animalList.get(4);
            category.debug(newRecord);

            category.debug("Now, lets access a record not in the record buffer.\n");
            newRecord = (Record)animalList.get(7);
            category.debug(newRecord);

            category.debug("Now, lets access a record not in the record buffer.\n");
            newRecord = (Record)animalList.get(3);
            category.debug(newRecord);
        } catch (DBException e)   {
            category.debug("Error reading through file: Error" + e.getMessage() + "\n");
            category.debug(animalList);
            System.exit(0);
        }


// End of tests, close the table    
        category.debug("Free the table object\n");
        animalList.free();
        animalList = null;
        category.debug("Test complete.\n");
    }
}
