/**
 * @(#)PackagesReportScreenRecord.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.packages.screen;

import java.awt.*;
import java.util.*;

import org.jbundle.base.db.*;
import org.jbundle.thin.base.util.*;
import org.jbundle.thin.base.db.*;
import org.jbundle.base.db.event.*;
import org.jbundle.base.db.filter.*;
import org.jbundle.base.field.*;
import org.jbundle.base.field.convert.*;
import org.jbundle.base.field.event.*;
import org.jbundle.base.screen.model.*;
import org.jbundle.base.screen.model.util.*;
import org.jbundle.base.model.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.jbundle.main.db.*;
import org.jbundle.app.program.packages.db.*;

/**
 *  PackagesReportScreenRecord - .
 */
public class PackagesReportScreenRecord extends ReportScreenRecord
{
    private static final long serialVersionUID = 1L;

    public static final int kJnlpFileID = kReportScreenRecordLastField + 1;
    public static final int ktemplate = kJnlpFileID + 1;
    public static final int kfileout = ktemplate + 1;
    public static final int kPackagesTree = kfileout + 1;
    public static final int kExcludePackages = kPackagesTree + 1;
    public static final int kPackagesReportScreenRecordLastField = kExcludePackages;
    public static final int kPackagesReportScreenRecordFields = kExcludePackages - DBConstants.MAIN_FIELD + 1;
    /**
     * Default constructor.
     */
    public PackagesReportScreenRecord()
    {
        super();
    }
    /**
     * Constructor.
     */
    public PackagesReportScreenRecord(RecordOwner screen)
    {
        this();
        this.init(screen);
    }
    /**
     * Initialize class fields.
     */
    public void init(RecordOwner screen)
    {
        super.init(screen);
    }

    public static final String kPackagesReportScreenRecordFile = null;  // Screen field
    /**
     * Add this field in the Record's field sequence.
     */
    public BaseField setupField(int iFieldSeq)
    {
        BaseField field = null;
        if (iFieldSeq == kJnlpFileID)
            field = new JnlpFileField(this, "JnlpFileID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == ktemplate)
            field = new StringField(this, "template", 120, null, "docs/styles/xsl/cocoon/program/templatebuild.xsl");
        if (iFieldSeq == kfileout)
            field = new StringField(this, "fileout", 256, null, null);
        if (iFieldSeq == kPackagesTree)
            field = new StringField(this, "PackagesTree", 120, null, null);
        if (iFieldSeq == kExcludePackages)
            field = new XmlField(this, "ExcludePackages", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kPackagesReportScreenRecordLastField)
                field = new EmptyField(this);
        }
        return field;
    }

}
