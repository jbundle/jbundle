/*
 *  @(#)TestReport.
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.app.test.manual.screentest;

import java.util.ListResourceBundle;

public class TestReportScreenRecordResources extends ListResourceBundle
{
    public Object[][] getContents()
    {
        return contents;
    }

    // To Localize this, just change the strings in the second column
    protected static final Object[][] contents =
    {
        {"VetID", "Vet"},
        {"ReportCount", "Count"}
    // END OF MATERIAL TO LOCALIZE
    };
}
