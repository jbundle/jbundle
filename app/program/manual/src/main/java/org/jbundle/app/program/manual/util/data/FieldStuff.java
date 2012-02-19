/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.app.program.manual.util.data;

/**
 *  WriteJava
 *  Copyright © 2012 jbundle.org. All rights reserved.
 */

//*******************************************************************
//  FieldStuff - Pass by pointer.
//*******************************************************************
    public class FieldStuff
    {
        public String strFieldClass;
        public String strFieldName;
        public String strFieldLength;
        public String strFileFieldName;
        public String strDefaultField;
        public String strFieldDesc;
        public String strFieldTip;
        public String strDataClass;
        public String strBaseFieldClass;    // Base class in the .field package
        public boolean bNotNullField;
        public boolean bHidden;

        public FieldStuff()
        {
            strFieldClass = null;
            strFieldName = null;
            strFieldLength = null;
            strFileFieldName = null;
            strDefaultField = null;
            strFieldDesc = null;
            strFieldTip = null;
            strDataClass = null;
            strBaseFieldClass = null; // Base class in the .field package
            bNotNullField = false;
            bHidden = true;
        }
}
