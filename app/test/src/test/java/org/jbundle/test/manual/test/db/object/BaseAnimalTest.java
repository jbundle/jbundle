package org.jbundle.test.manual.test.db.object;

//******************************************************************************
// Test the basic table functions (add, remove, move, etc.)
//******************************************************************************
import junit.framework.TestCase;

import org.apache.log4j.Category;
import org.jbundle.app.test.vet.db.Cat;
import org.jbundle.app.test.vet.db.Dog;
import org.jbundle.app.test.vet.db.Vet;
import org.jbundle.base.util.DBConstants;
import org.jbundle.model.DBException;


/**
 * BaseTest is the standard code to set for all the other tests.
 * @author  don
 * @version 
 */
public class BaseAnimalTest extends TestCase
{
    public Category category = null;

    /**
      * Creates new TestAll.
      */
    public BaseAnimalTest(String strTestName)
    {
        super(strTestName);
    }
    /**
     * Add animal test records.
     */
    public void addAnimalRecords(Cat cat, Dog dog, Vet vet, boolean bPrintStatus)
    {
        category = Category.getInstance(BaseAnimalTest.class.getName());
        int iVet1 = 0;
        int iVet2 = 0;
        int iCount = 0;
        category.debug("Begin Test\n");

        category.debug("Open Vet.\n");
        try   {
            vet.open();             // Open the table
        } catch (DBException e)   {
            String strError = e.getMessage();
            category.debug(strError);
            System.exit(0);
        }

        
        try   {     
            vet.setKeyArea(DBConstants.MAIN_KEY_AREA);
            iCount = 0;
            vet.close();
            while (vet.hasNext()) {
                vet.move(+1);
                category.debug(vet.toString());
                iCount++;
            }
        } catch (DBException e)   {
            category.debug("Error reading through file: Error" + e.getMessage() + "\n");
        }
            
        category.debug("Delete all old Vets.\n");
        iCount = 0;
        try   {
            vet.close();
            while (vet.hasNext())
            {
                vet.move(+1);
                vet.remove();
                iCount++;
            }
        } catch (DBException e)   {
            category.debug("Could not delete record: Error" + e.getMessage() + "\n");
            category.debug(vet.toString());
            System.exit(0);
        }
        category.debug("deleted records " + iCount + "\n");

        try   {
            category.debug("Add Vets.\n");

            vet.setOpenMode(DBConstants.OPEN_REFRESH_AND_LOCK_ON_CHANGE_STRATEGY);
            vet.addNew();
            vet.getField(Vet.kName).setString("Dr. Smith");
            iVet1 = (int)vet.getField(Vet.kID).getValue();
            vet.add();

            vet.addNew();
            vet.getField(Vet.kName).setString("Dr. Jones");
            iVet2 = (int)vet.getField(Vet.kID).getValue();
            vet.add();
            
            vet.close();
            
//          vet.free();
//          vet = null;
        } catch (DBException e)   {
            String strError = e.getMessage();
            category.debug(strError);
            e.printStackTrace();
            System.exit(0);
        }

        try   {
            cat.open();             // Open the table
        } catch (DBException e)   {
            String strError = e.getMessage();
            category.debug(strError);
            System.exit(0);
        }

        
        try   {     
            cat.setKeyArea(DBConstants.MAIN_KEY_AREA);
            iCount = 0;
            cat.close();
            while (cat.hasNext()) {
                cat.move(+1);
                category.debug(cat.toString());
                iCount++;
            }
        } catch (DBException e)   {
            category.debug("Error reading through file: Error" + e.getMessage() + "\n");
        }
            
        category.debug("Delete all old Cats.\n");
        iCount = 0;
        try   {
            cat.close();
            while (cat.hasNext())
            {
                cat.move(+1);
                cat.remove();
                iCount++;
            }
        } catch (DBException e)   {
            category.debug("Could not delete record: Error" + e.getMessage() + "\n");
            category.debug(cat.toString());
            System.exit(0);
        }
        category.debug("deleted records " + iCount + "\n");

        try   {
            category.debug("Add Cats.\n");

            cat.setOpenMode(DBConstants.OPEN_REFRESH_AND_LOCK_ON_CHANGE_STRATEGY);    // Make sure keys are updated before sync


            cat.addNew();
            cat.getField(Cat.kName).setString("Tabby");
            cat.getField(Cat.kColor).setString("Black and White");
            cat.getField(Cat.kWeight).setValue(2.0);
            cat.getField(Cat.kVet).setValue(iVet1);
            cat.add();

            cat.addNew();
            cat.getField(Cat.kName).setString("Manx");
            cat.getField(Cat.kColor).setString("Grey");
            cat.getField(Cat.kWeight).setValue(2.5);
            cat.getField(Cat.kVet).setValue(iVet1);
            cat.add();

            cat.addNew();
            cat.getField(Cat.kName).setString("Siamese");
            cat.getField(Cat.kColor).setString("Cream");
            cat.getField(Cat.kWeight).setValue(1.5);
            cat.getField(Cat.kVet).setValue(iVet2);
            cat.add();

            cat.addNew();
            cat.getField(Cat.kName).setString("Persian");
            cat.getField(Cat.kColor).setString("Grey");
            cat.getField(Cat.kWeight).setValue(3.0);
            cat.getField(Cat.kVet).setValue(iVet2);
            cat.add();

                category.debug("cats added.\n");
        } catch (DBException e)   {
            category.debug("Error adding record. Error: " + e.getMessage() + "\n");
            category.debug(cat.toString());
            System.exit(0);
        }

        category.debug("Count keys in both indexes.\n");
        try   {
            cat.setKeyArea(DBConstants.MAIN_KEY_AREA);
            iCount = 0;
            cat.close();
            while (cat.hasNext()) {
                cat.move(+1);
                iCount++;
            }
            category.debug("Main index: Count: " + iCount + "\n");
            assertTrue("Error wrong cat count 4 Count: " + iCount + "\n", iCount == 4);
            cat.setKeyArea(DBConstants.MAIN_KEY_AREA+1);
            iCount = 0;
            cat.close();
            while (cat.hasPrevious()) {
                cat.move(-1);
                iCount++;
            }
            category.debug("Secondary index: Count: " + iCount + "\n");
//          assertTrue("Error wrong cat count 4 Count: " + iCount + "\n", iCount == 4);
            cat.setKeyArea(DBConstants.MAIN_KEY_AREA);
        } catch (DBException e)   {
            category.debug("Error reading through file: Error" + e.getMessage() + "\n");
            category.debug(cat.toString());
            System.exit(0);
        }

// Now set up the dog table
            category.debug("Open dog.\n");
        try   {
            dog.open();             // Open the table
        } catch (DBException e)   {
            String strError = e.getMessage();
            category.debug(strError);
            System.exit(0);
        }

        
        try   {     
            dog.setKeyArea(DBConstants.MAIN_KEY_AREA);
            iCount = 0;
            dog.close();
            while (dog.hasNext()) {
                dog.move(+1);
                category.debug(dog.toString());
                iCount++;
            }
        } catch (DBException e)   {
            category.debug("Error reading through file: Error" + e.getMessage() + "\n");
        }
            
        category.debug("Delete all old Dogs.\n");
        iCount = 0;
        try   {
            dog.close();
            while (dog.hasNext()) {
                dog.move(+1);
                    dog.remove();
                iCount++;
            }
        } catch (DBException e)   {
            category.debug("Could not delete record: Error" + e.getMessage() + "\n");
            category.debug(dog.toString());
            System.exit(0);
        }
        category.debug("deleted records " + iCount + "\n");

        try   {
            category.debug("Add Dogs.\n");

            dog.setOpenMode(DBConstants.OPEN_REFRESH_AND_LOCK_ON_CHANGE_STRATEGY);    // Make sure keys are updated before sync


            dog.addNew();
            dog.getField(Dog.kName).setString("Doberman");
            dog.getField(Dog.kColor).setString("Black");
            dog.getField(Dog.kWeight).setValue(21.0);
            dog.getField(Dog.kVet).setValue(iVet2);
            dog.getField(Dog.kBark).setState(true);
            dog.add();

            dog.addNew();
            dog.getField(Dog.kName).setString("Poodle");
            dog.getField(Dog.kColor).setString("White");
            dog.getField(Dog.kWeight).setValue(3.5);
            dog.getField(Dog.kVet).setValue(iVet1);
            dog.getField(Dog.kBark).setState(false);
            dog.add();

            dog.addNew();
            dog.getField(Dog.kName).setString("Saint Bernard");
            dog.getField(Dog.kColor).setString("Brown and White");
            dog.getField(Dog.kWeight).setValue(71.0);
            dog.getField(Dog.kVet).setValue(iVet2);
            dog.getField(Dog.kBark).setState(true);
            dog.add();

            dog.addNew();
            dog.getField(Dog.kName).setString("German Shepherd");
            dog.getField(Dog.kColor).setString("Grey");
            dog.getField(Dog.kWeight).setValue(30.0);
            dog.getField(Dog.kVet).setValue(iVet1);
            dog.getField(Dog.kBark).setState(true);
            dog.add();

            dog.addNew();
            dog.getField(Dog.kName).setString("Dalmation");
            dog.getField(Dog.kColor).setString("Black and White");
            dog.getField(Dog.kWeight).setValue(35.0);
            dog.getField(Dog.kVet).setValue(iVet2);
            dog.getField(Dog.kBark).setState(true);
            dog.add();

            category.debug("dogs added.\n");
        } catch (DBException e)   {
            category.debug("Error adding record. Error: " + e.getMessage() + "\n");
            category.debug(dog.toString());
            System.exit(0);
        }

        category.debug("Count keys in both indexes.\n");
        try   {
            dog.setKeyArea(DBConstants.MAIN_KEY_AREA);
            iCount = 0;
            dog.close();
            while (dog.hasNext()) {
                dog.move(+1);
                iCount++;
            }
            category.debug("Main index: Count: " + iCount + "\n");
            assertTrue("Error wrong dog count 5 Count: " + iCount + "\n", iCount == 5);
            dog.setKeyArea(DBConstants.MAIN_KEY_AREA+1);
            iCount = 0;
            dog.close();
            while (dog.hasPrevious()) {
                dog.move(-1);
                iCount++;
            }
            category.debug("Secondary index: Count: " + iCount + "\n");
//          assertTrue("Error wrong dog secondary count 5 Count: " + iCount + "\n", iCount == 5);
            dog.setKeyArea(DBConstants.MAIN_KEY_AREA);
        } catch (DBException e)   {
            category.debug("Error reading through file: Error" + e.getMessage() + "\n");
            category.debug(dog.toString());
            System.exit(0);
        }
    }
}
