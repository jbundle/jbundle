/**
 * @(#)RegistrationModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.app.program.resource.db;

import org.jbundle.model.db.*;

public interface RegistrationModel extends Rec
{

    //public static final String ID = ID;
    public static final String RESOURCE_ID = "ResourceID";
    public static final String CODE = "Code";
    public static final String LANGUAGE = "Language";
    public static final String LOCALE = "Locale";
    public static final String KEY_VALUE = "KeyValue";
    public static final String OBJECT_VALUE = "ObjectValue";

    public static final String RESOURCE_ID_KEY = "ResourceID";

    public static final String CODE_KEY = "Code";
    public static final String REGISTRATION_SCREEN_CLASS = "org.jbundle.app.program.resource.screen.RegistrationScreen";
    public static final String REGISTRATION_GRID_SCREEN_CLASS = "org.jbundle.app.program.resource.screen.RegistrationGridScreen";

    public static final String REGISTRATION_FILE = "Registration";
    public static final String THIN_CLASS = "org.jbundle.thin.app.program.resource.db.Registration";
    public static final String THICK_CLASS = "org.jbundle.app.program.resource.db.Registration";

}
