/**
 *  @(#)Company.
 *  Copyright © 2010 tourapp.com. All rights reserved.
 */
package org.jbundle.main.db;

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
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.base.message.trx.message.*;
import org.jbundle.main.msg.db.*;

/**
 *  Company - Mailing list of company names.
 */
public class Company extends Person
{
    private static final long serialVersionUID = 1L;

    public static final int kContact = kPersonLastField + 1;
    public static final int kCompanyLastField = kContact;
    public static final int kCompanyFields = kContact - DBConstants.MAIN_FIELD + 1;
    /**
     * Default constructor.
     */
    public Company()
    {
        super();
    }
    /**
     * Constructor.
     */
    public Company(RecordOwner screen)
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

    public static final String kCompanyFile = null;   // Screen field
    /**
     * Add this field in the Record's field sequence.
     */
    public BaseField setupField(int iFieldSeq)
    {
        BaseField field = null;
        if (iFieldSeq == kContact)
            field = new StringField(this, "Contact", 30, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kCompanyLastField)
                field = new EmptyField(this);
        }
        return field;
    }
    /**
     * Add all standard file & field behaviors.
     * Override this to add record listeners and filters.
     */
    public void addListeners()
    {
        super.addListeners();
        
        this.getField(Person.kName).removeListener(this.getField(Person.kName).getListener(CopyLastHandler.class), true);    // Only if dest is null (ie., company name is null)
        this.getField(Person.kName).addListener(new CopyFieldHandler(Person.kNameSort));
        this.getField(Company.kNameSort).addListener(new CheckForTheHandler(null));
        
        CopyLastHandler listener = new CopyLastHandler(Company.kNameSort);
        this.getField(Company.kContact).addListener(listener);    // Only if dest is null (ie., company name is null)
        listener.setOnlyIfDestNull(true);
    }

}
