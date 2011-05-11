package org.jbundle.thin.base.util;

import org.jbundle.model.Task;
import org.jbundle.model.util.Util;
import org.jbundle.util.osgi.ClassService;
import org.jbundle.util.osgi.finder.BaseClassServiceImpl;


/**
 * Thin specific static utility methods.
 */
public class ClassServiceImpl extends BaseClassServiceImpl
	implements ClassService
{
   /**
    * Create this object given the class name.
    * @param className
    * @return
    */
   public Object makeObjectFromClassName(String className, Object task, boolean bErrorIfNotFound)
   {
	   if (className == null)
		   return null;
	   className = Util.getFullClassName(className);
	   return super.makeObjectFromClassName(className, task, bErrorIfNotFound);
   }
   /**
    * Handle the class exception (set the error code).
    * @param ex
    * @param className
    * @param task
    * @param bErrorIfNotFound
    */
   public void handleClassException(Exception ex, String className, Object task, boolean bErrorIfNotFound)
   {
       if (bErrorIfNotFound)
       {
           Util.getLogger().warning("Error on create class: " + className);
           if (ex != null)
        	   ex.printStackTrace();
           if (task != null)
               ((Task)task).setLastError("Error on create class: " + className);	// TODO(don) Hack
       }
       
   }
}
