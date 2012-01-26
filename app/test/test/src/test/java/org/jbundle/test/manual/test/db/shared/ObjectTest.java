/*
// Test the basic object processing functions.
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.test.manual.test.db.shared;

import org.jbundle.app.test.vet.db.Vet;
import org.jbundle.app.test.vet.shared.db.Lizard;
import org.jbundle.app.test.vet.shared.db.Reptile;
import org.jbundle.app.test.vet.shared.db.ReptileTypeField;
import org.jbundle.app.test.vet.shared.db.Snake;
import org.jbundle.base.db.BaseTable;
import org.jbundle.base.db.GridTable;
import org.jbundle.base.db.Record;
import org.jbundle.base.db.filter.SubFileFilter;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.Utility;
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
    Lizard lizard = null;
    Snake snake = null;
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
        Reptile animal = null;
        Utility.getLogger().info("Open the Reptile Table\n");
        Reptile recReptile = new Reptile(snake.getRecordOwner());

        BaseTable animalTable = recReptile.getTable();
        Object bookmark = null;
        Object dbBookmark = null;

        try   {
//          recReptile.setKeyArea(Reptile.kNameKey);
            iCount = 0;
            while ((animal = (Reptile)animalTable.move(+1)) != null)
            {
                Utility.getLogger().info(animal.toString());
                iCount++;
                if (iCount == 6)
                {
                    bookmark = animalTable.getHandle(DBConstants.OBJECT_ID_HANDLE);
                    dbBookmark = animalTable.getHandle(DBConstants.DATA_SOURCE_HANDLE);
                }
            }
            assertTrue("Error wrong count 9 Count: " + iCount + "\n", iCount == 9);
        Utility.getLogger().info("Set a bookmark\n");
        if (bookmark != null)
            animal = (Reptile)animalTable.setHandle(bookmark, DBConstants.OBJECT_ID_HANDLE);
        else
            animal = (Reptile)animalTable.setHandle(dbBookmark, DBConstants.DATA_SOURCE_HANDLE);
        if (animal != null)
        {
            Utility.getLogger().info("Success\n" + animal.toString());
        }
        else
            fail("Failure - no bookmark\n");
        } catch (DBException ex)    {
            iCount = -1;
        }
        Utility.getLogger().info("Count:" + iCount);

/*        try   {
            Utility.getLogger().info("Now, update animal class");
            animal.edit();
            animal.getField(Reptile.kName).setString("New Snake Name");
            animal.set();
        } catch (DBException ex)    {
            ex.printStackTrace();
            fail("Failure\n");
        }
*/
        iCount = 0;
        Utility.getLogger().info("Now, test the sub-file behaviors on the animal class");
        try   {
            vet.close();
            vet.next();
            Utility.getLogger().info(vet.toString());
            animal = null;
            recReptile = new Reptile(snake.getRecordOwner());
            recReptile.addListener(new SubFileFilter(vet));
            animalTable = recReptile.getTable();

            iCount = 0;
            while ((animal = (Reptile)animalTable.move(+1)) != null)
            {
                Utility.getLogger().info(animal.toString());
                iCount++;
            }
        } catch (DBException e)   {
            Utility.getLogger().info("Could not delete record: Error" + e.getMessage() + "\n");
            Utility.getLogger().info(animal.toString());
            fail("Failure");;
        }
        Utility.getLogger().info("Count for this vet: " + iCount);

        Utility.getLogger().info("Now, test the update/delete/add behaviors of this type of table");
        try   {
            recReptile.close();

            iCount = 0;
            while (recReptile.hasNext())
            {
                animal = (Reptile)recReptile.next();
                Utility.getLogger().info(animal.toString());
                if (iCount == 0)
                {
                    animal.edit();
                    animal.getField(Reptile.kName).setString("New reptile #1");
                    animal.set();
                }
                if (iCount == 1)
                {       // This is generally not done, but I need to be able to handle it.
                    recReptile.edit();
                    recReptile.getField(Reptile.kName).setString("New reptile #2");
                    recReptile.set();
                }
                if (iCount == 2)
                {       // This is not cools, but it should work.
                    recReptile.edit();
                    animal.getField(Reptile.kName).setString("New reptile #3");
                    recReptile.set();
                }
                iCount++;
            }
        } catch (DBException e)   {
            Utility.getLogger().info("Error: " + e.getMessage() + "\n");
            Utility.getLogger().info(animal.toString());
            fail("Failure");
        }
        Utility.getLogger().info("Count for this vet: " + iCount);

        recReptile.free();
// Test the logic of accessing a concrete class from the base record.
        try {
            recReptile = new Reptile(snake.getRecordOwner());
            recReptile.addNew();
            recReptile.getField(Reptile.kReptileTypeID).setValue(ReptileTypeField.LIZARD);
//            recReptile.setOpenMode(DBConstants.OPEN_REFRESH_AND_LOCK_ON_CHANGE);
            recReptile.addNew();

            recReptile = (Lizard)recReptile.getTable().getCurrentTable().getRecord();  // lizard
            recReptile.getField(Reptile.kName).setString("Barney");
            
            recReptile.add();
            Object objBookmark = recReptile.getLastModified(DBConstants.BOOKMARK_HANDLE);
            Object recReturned = recReptile.setHandle(objBookmark, DBConstants.BOOKMARK_HANDLE);
            
            recReptile.edit();
            recReptile.getField(Reptile.kWeight).setValue(240);
            recReptile.set();
        } catch (DBException ex)   {
            Utility.getLogger().info("Error: " + ex.getMessage() + "\n");
            ex.printStackTrace();
            Utility.getLogger().info(animal.toString());
            fail("Failure");
        }
// End of tests, close the table    
        Utility.getLogger().info("Test complete.\n");
    }
    /**
     *
     */
    public void testGrid()
    {
        int iCount;

        Utility.getLogger().info("Now, let's test a ObjectTable.\n");
        Utility.getLogger().info("Open table.\n");
        Reptile animal = new Reptile(null);
        BaseTable animalTable = animal.getTable();
        try   {
            animal.open();              // Open the table
        } catch (DBException e)   {
            String strError = e.getMessage();
            Utility.getLogger().info(strError);
            System.exit(0);
        }

        Utility.getLogger().info("Count keys in both indexes.\n");
        try   {
            animal.setKeyArea(DBConstants.MAIN_KEY_AREA);
            iCount = 0;
            animal.close();
            while (animalTable.hasNext())
            {
                animal = (Reptile)animalTable.move(+1);
                iCount++;
            }
            animalTable.close();
            Utility.getLogger().info("Main index: Count: " + iCount + "\n");
            assertTrue("Error wrong count 9 Count: " + iCount + "\n", iCount == 9);
        } catch (DBException e)   {
            Utility.getLogger().info("Error reading through file: Error" + e.getMessage() + "\n");
            Utility.getLogger().info(animal.toString());
            System.exit(0);
        }
// Now, start the tests....

        GridTable animalList = new GridTable(null, animalTable.getRecord());
        try   {
            animalList.open();
            Utility.getLogger().info("First, lets read through the table using MoveTo.\n");
            int index = 0;
            Record newRecord = null;
            for (index = 0; ; index++)
            {
                newRecord = (Record)animalList.get(index);
                if (newRecord == null)
                    break;
            }
            Utility.getLogger().info("Move to count: " + index);
            assertTrue("Error wrong count 9 Count: " + index + "\n", index == 9);

            Utility.getLogger().info("Now, lets access a record in the record buffer.\n");
            newRecord = (Record)animalList.get(4);
            Utility.getLogger().info(newRecord.toString());

            Utility.getLogger().info("Now, lets access a record not in the record buffer.\n");
            newRecord = (Record)animalList.get(7);
            Utility.getLogger().info(newRecord.toString());

            Utility.getLogger().info("Now, lets access a record not in the record buffer.\n");
            newRecord = (Record)animalList.get(3);
            Utility.getLogger().info(newRecord.toString());
        } catch (DBException e)   {
            Utility.getLogger().info("Error reading through file: Error" + e.getMessage() + "\n");
            Utility.getLogger().info(animalList.toString());
            System.exit(0);
        }


// End of tests, close the table    
        Utility.getLogger().info("Free the table object\n");
        animalList.free();
        animalList = null;
        Utility.getLogger().info("Test complete.\n");
    }
}
