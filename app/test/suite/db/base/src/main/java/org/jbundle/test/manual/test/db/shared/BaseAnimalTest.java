/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.test.manual.test.db.shared;

//******************************************************************************
// Test the basic table functions (add, remove, move, etc.)
//******************************************************************************
import org.jbundle.app.test.vet.db.Vet;
import org.jbundle.app.test.vet.shared.db.Lizard;
import org.jbundle.app.test.vet.shared.db.Snake;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.Utility;
import org.jbundle.model.DBException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * BaseTest is the standard code to set for all the other tests.
 * @author  don
 * @version 
 */
public class BaseAnimalTest extends TestCase
{
    
    /**
      * Creates new TestAll.
      */
    public BaseAnimalTest(String strTestName)
    {
        super(strTestName);
    }
    public static Test suite()
    {
        TestSuite suite= new TestSuite();     
        // Hack - Get rid of mvn test error
        return suite;
    }
    /**
     * Add animal test records.
     */
    public void addAnimalRecords(Lizard lizard, Snake snake, Vet vet, boolean bPrintStatus)
    {
        int iVet1 = 0;
        int iVet2 = 0;
        int iCount = 0;
        Utility.getLogger().info("Begin Test\n");

        Utility.getLogger().info("Open Vet.\n");
        try   {
            vet.open();             // Open the table
        } catch (DBException e)   {
            String strError = e.getMessage();
            Utility.getLogger().info(strError);
            System.exit(0);
        }

        
        try   {     
            vet.setKeyArea(DBConstants.MAIN_KEY_AREA);
            iCount = 0;
            vet.close();
            while (vet.hasNext()) {
                vet.move(+1);
                Utility.getLogger().info(vet.toString());
                iCount++;
            }
        } catch (DBException e)   {
            Utility.getLogger().info("Error reading through file: Error" + e.getMessage() + "\n");
        }
            
        Utility.getLogger().info("Delete all old Vets.\n");
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
            Utility.getLogger().info("Could not delete record: Error" + e.getMessage() + "\n");
            Utility.getLogger().info(vet.toString());
            System.exit(0);
        }
        Utility.getLogger().info("deleted records " + iCount + "\n");

        try   {
            Utility.getLogger().info("Add Vets.\n");

            vet.setOpenMode(DBConstants.OPEN_REFRESH_AND_LOCK_ON_CHANGE_STRATEGY);
            vet.addNew();
            vet.getField(Vet.NAME).setString("Dr. Smith");
            iVet1 = (int)vet.getField(Vet.kID).getValue();
            vet.add();

            vet.addNew();
            vet.getField(Vet.NAME).setString("Dr. Jones");
            iVet2 = (int)vet.getField(Vet.kID).getValue();
            vet.add();
            
            vet.close();
            
//          vet.free();
//          vet = null;
        } catch (DBException e)   {
            String strError = e.getMessage();
            Utility.getLogger().info(strError);
            e.printStackTrace();
            System.exit(0);
        }

        try   {
            lizard.open();             // Open the table
        } catch (DBException e)   {
            String strError = e.getMessage();
            Utility.getLogger().info(strError);
            System.exit(0);
        }

        
        try   {     
            lizard.setKeyArea(DBConstants.MAIN_KEY_AREA);
            iCount = 0;
            lizard.close();
            while (lizard.hasNext()) {
                lizard.move(+1);
                Utility.getLogger().info(lizard.toString());
                iCount++;
            }
        } catch (DBException e)   {
            Utility.getLogger().info("Error reading through file: Error" + e.getMessage() + "\n");
        }
            
        Utility.getLogger().info("Delete all old lizards.\n");
        iCount = 0;
        try   {
            lizard.close();
            while (lizard.hasNext())
            {
                lizard.move(+1);
                lizard.remove();
                iCount++;
            }
        } catch (DBException e)   {
            Utility.getLogger().info("Could not delete record: Error" + e.getMessage() + "\n");
            Utility.getLogger().info(lizard.toString());
            System.exit(0);
        }
        Utility.getLogger().info("deleted records " + iCount + "\n");

        try   {
            Utility.getLogger().info("Add lizards.\n");

            lizard.setOpenMode(DBConstants.OPEN_REFRESH_AND_LOCK_ON_CHANGE_STRATEGY);    // Make sure keys are updated before sync


            lizard.addNew();
            lizard.getField(lizard.NAME).setString("Blue Belly");
            lizard.getField(lizard.CLEARANCE).setValue(5);
            lizard.getField(lizard.WEIGHT).setValue(2.0);
            lizard.getField(lizard.VET_ID).setValue(iVet1);
            lizard.add();

            lizard.addNew();
            lizard.getField(lizard.NAME).setString("Alligator lizard");
            lizard.getField(lizard.CLEARANCE).setValue(2);
            lizard.getField(lizard.WEIGHT).setValue(2.5);
            lizard.getField(lizard.VET_ID).setValue(iVet1);
            lizard.add();

            lizard.addNew();
            lizard.getField(lizard.NAME).setString("Sand lizard");
            lizard.getField(lizard.CLEARANCE).setValue(8);
            lizard.getField(lizard.WEIGHT).setValue(1.5);
            lizard.getField(lizard.VET_ID).setValue(iVet2);
            lizard.add();

            lizard.addNew();
            lizard.getField(lizard.NAME).setString("Milk lizard");
            lizard.getField(lizard.CLEARANCE).setValue(12);
            lizard.getField(lizard.WEIGHT).setValue(3.0);
            lizard.getField(lizard.VET_ID).setValue(iVet2);
            lizard.add();

                Utility.getLogger().info("lizards added.\n");
        } catch (DBException e)   {
            Utility.getLogger().info("Error adding record. Error: " + e.getMessage() + "\n");
            Utility.getLogger().info(lizard.toString());
            System.exit(0);
        }

        Utility.getLogger().info("Count keys in both indexes.\n");
        try   {
            lizard.setKeyArea(DBConstants.MAIN_KEY_AREA);
            iCount = 0;
            lizard.close();
            while (lizard.hasNext()) {
                lizard.move(+1);
                iCount++;
            }
            Utility.getLogger().info("Main index: Count: " + iCount + "\n");
            assertTrue("Error wrong lizard count 4 Count: " + iCount + "\n", iCount == 4);
            lizard.setKeyArea(DBConstants.MAIN_KEY_AREA+1);
            iCount = 0;
            lizard.close();
            while (lizard.hasPrevious()) {
                lizard.move(-1);
                iCount++;
            }
            Utility.getLogger().info("Secondary index: Count: " + iCount + "\n");
//          assertTrue("Error wrong lizard count 4 Count: " + iCount + "\n", iCount == 4);
            lizard.setKeyArea(DBConstants.MAIN_KEY_AREA);
        } catch (DBException e)   {
            Utility.getLogger().info("Error reading through file: Error" + e.getMessage() + "\n");
            Utility.getLogger().info(lizard.toString());
            System.exit(0);
        }

// Now set up the snake table
            Utility.getLogger().info("Open snake.\n");
        try   {
            snake.open();             // Open the table
        } catch (DBException e)   {
            String strError = e.getMessage();
            Utility.getLogger().info(strError);
            System.exit(0);
        }

        
        try   {     
            snake.setKeyArea(DBConstants.MAIN_KEY_AREA);
            iCount = 0;
            snake.close();
            while (snake.hasNext()) {
                snake.move(+1);
                Utility.getLogger().info(snake.toString());
                iCount++;
            }
        } catch (DBException e)   {
            Utility.getLogger().info("Error reading through file: Error" + e.getMessage() + "\n");
        }
            
        Utility.getLogger().info("Delete all old snakes.\n");
        iCount = 0;
        try   {
            snake.close();
            while (snake.hasNext()) {
                snake.move(+1);
                    snake.remove();
                iCount++;
            }
        } catch (DBException e)   {
            Utility.getLogger().info("Could not delete record: Error" + e.getMessage() + "\n");
            Utility.getLogger().info(snake.toString());
            System.exit(0);
        }
        Utility.getLogger().info("deleted records " + iCount + "\n");

        try   {
            Utility.getLogger().info("Add snakes.\n");

            snake.setOpenMode(DBConstants.OPEN_REFRESH_AND_LOCK_ON_CHANGE_STRATEGY);    // Make sure keys are updated before sync


            snake.addNew();
            snake.getField(snake.NAME).setString("Python");
            snake.getField(snake.WEIGHT).setValue(21.0);
            snake.getField(snake.VET_ID).setValue(iVet2);
            snake.add();

            snake.addNew();
            snake.getField(snake.NAME).setString("Rattlesnake");
            snake.getField(snake.WEIGHT).setValue(3.5);
            snake.getField(snake.VET_ID).setValue(iVet1);
            snake.add();

            snake.addNew();
            snake.getField(snake.NAME).setString("Gopher snake");
            snake.getField(snake.WEIGHT).setValue(71.0);
            snake.getField(snake.VET_ID).setValue(iVet2);
            snake.add();

            snake.addNew();
            snake.getField(snake.NAME).setString("Anaconda");
            snake.getField(snake.WEIGHT).setValue(30.0);
            snake.getField(snake.VET_ID).setValue(iVet1);
            snake.add();

            snake.addNew();
            snake.getField(snake.NAME).setString("Striped racer");
            snake.getField(snake.WEIGHT).setValue(35.0);
            snake.getField(snake.VET_ID).setValue(iVet2);
            snake.add();

            Utility.getLogger().info("snakes added.\n");
        } catch (DBException e)   {
            Utility.getLogger().info("Error adding record. Error: " + e.getMessage() + "\n");
            Utility.getLogger().info(snake.toString());
            System.exit(0);
        }

        Utility.getLogger().info("Count keys in both indexes.\n");
        try   {
            snake.setKeyArea(DBConstants.MAIN_KEY_AREA);
            iCount = 0;
            snake.close();
            while (snake.hasNext()) {
                snake.move(+1);
                iCount++;
            }
            Utility.getLogger().info("Main index: Count: " + iCount + "\n");
            assertTrue("Error wrong snake count 5 Count: " + iCount + "\n", iCount == 5);
            snake.setKeyArea(DBConstants.MAIN_KEY_AREA+1);
            iCount = 0;
            snake.close();
            while (snake.hasPrevious()) {
                snake.move(-1);
                iCount++;
            }
            Utility.getLogger().info("Secondary index: Count: " + iCount + "\n");
//          assertTrue("Error wrong snake secondary count 5 Count: " + iCount + "\n", iCount == 5);
            snake.setKeyArea(DBConstants.MAIN_KEY_AREA);
        } catch (DBException e)   {
            Utility.getLogger().info("Error reading through file: Error" + e.getMessage() + "\n");
            Utility.getLogger().info(snake.toString());
            System.exit(0);
        }
    }
}
