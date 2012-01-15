/**
 * @(#)RegistrationModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.app.program.resource.db;

import org.jbundle.model.db.*;

public interface RegistrationModel extends Rec
{
    public static final String REGISTRATION_SCREEN_CLASS = "org.jbundle.app.program.resource.screen.RegistrationScreen";
    public static final String REGISTRATION_GRID_SCREEN_CLASS = "org.jbundle.app.program.resource.screen.RegistrationGridScreen";

    public static final String REGISTRATION_FILE = "Registration";
    public static final String THIN_CLASS = "org.jbundle.thin.app.program.resource.db.Registration";
    public static final String THICK_CLASS = "org.jbundle.app.program.resource.db.Registration";

}
