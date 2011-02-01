/**
 *  @(#)TestReport.
 * Copyright (c) 2009 tourapp.com. All Rights Reserved.
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
