/**
 * @(#)TestTableModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.app.test.test.db;

import org.jbundle.model.db.*;

public interface TestTableModel extends Rec
{
    public static final String TEST_CODE = "TestCode";
    public static final String TEST_NAME = "TestName";
    public static final String TEST_CURRENCY = "TestCurrency";
    public static final String TEST_VIRTUAL = "TestVirtual";

    public static final String TEST_TABLE_FILE = "TestTable";
    public static final String THIN_CLASS = "org.jbundle.thin.app.test.test.db.TestTable";
    public static final String THICK_CLASS = "org.jbundle.app.test.test.db.TestTable";

}
