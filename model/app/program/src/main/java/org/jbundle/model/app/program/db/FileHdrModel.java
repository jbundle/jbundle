/**
 * @(#)FileHdrModel.
 * Copyright © 2012 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.app.program.db;

import org.jbundle.model.db.*;

public interface FileHdrModel extends Rec
{

    //public static final String ID = ID;
    //public static final String LAST_CHANGED = LAST_CHANGED;
    //public static final String DELETED = DELETED;
    public static final String FILE_NAME = "FileName";
    public static final String FILE_DESC = "FileDesc";
    public static final String FILE_MAIN_FILENAME = "FileMainFilename";
    public static final String TYPE = "Type";
    public static final String FILE_NOTES = "FileNotes";
    public static final String DATABASE_NAME = "DatabaseName";
    public static final String FILE_REC_CALLED = "FileRecCalled";
    public static final String DISPLAY_CLASS = "DisplayClass";
    public static final String MAINT_CLASS = "MaintClass";

    public static final String FILE_NAME_KEY = "FileName";
    public static final String FILE_HDR_SCREEN_CLASS = "org.jbundle.app.program.screen.FileHdrScreen";

    public static final String FILE_HDR_FILE = "FileHdr";
    public static final String THIN_CLASS = "org.jbundle.thin.app.program.db.FileHdr";
    public static final String THICK_CLASS = "org.jbundle.app.program.db.FileHdr";

}
