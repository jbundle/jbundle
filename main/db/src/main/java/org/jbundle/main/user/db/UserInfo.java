/**
 * @(#)UserInfo.
 * Copyright © 2013 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.user.db;

import java.util.*;

import org.jbundle.base.db.*;
import org.jbundle.thin.base.util.*;
import org.jbundle.thin.base.db.*;
import org.jbundle.base.db.event.*;
import org.jbundle.base.db.filter.*;
import org.jbundle.base.field.*;
import org.jbundle.base.field.convert.*;
import org.jbundle.base.field.event.*;
import org.jbundle.base.model.*;
import org.jbundle.base.util.*;
import org.jbundle.model.*;
import org.jbundle.model.db.*;
import org.jbundle.model.screen.*;
import org.jbundle.thin.base.screen.*;
import org.jbundle.main.db.*;
import org.jbundle.main.db.base.*;
import org.jbundle.model.main.user.db.*;

/**
 *  UserInfo - User Information.
 */
public class UserInfo extends PropertiesRecord
     implements UserInfoModel
{
    private static final long serialVersionUID = 1L;

    public static final int ENTRY_SCREEN_MODE = ScreenConstants.MAINT_MODE;
    public static final int VERBOSE_MAINT_MODE = ScreenConstants.LAST_MODE * 2;
    public static final int LOGIN_SCREEN_MODE = VERBOSE_MAINT_MODE * 2;
    public static final int PREFERENCES_SCREEN_MODE = LOGIN_SCREEN_MODE * 2;
    public static final int PASSWORD_CHANGE_SCREEN_MODE = PREFERENCES_SCREEN_MODE * 2;
    /**
     * Default constructor.
     */
    public UserInfo()
    {
        super();
    }
    /**
     * Constructor.
     */
    public UserInfo(RecordOwner screen)
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
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(USER_INFO_FILE, bAddQuotes) : super.getTableNames(bAddQuotes);
    }
    /**
     * Get the name of a single record.
     */
    public String getRecordName()
    {
        return "User";
    }
    /**
     * Get the Database Name.
     */
    public String getDatabaseName()
    {
        return "main";
    }
    /**
     * Is this a local (vs remote) file?.
     */
    public int getDatabaseType()
    {
        return DBConstants.LOCAL | DBConstants.USER_DATA;
    }
    /**
     * MakeScreen Method.
     */
    public ScreenParent makeScreen(ScreenLoc itsLocation, ComponentParent parentScreen, int iDocMode, Map<String,Object> properties)
    {
        ScreenParent screen = null;
        if ((iDocMode & ScreenConstants.DOC_MODE_MASK) == ScreenConstants.DETAIL_MODE)
            screen = Record.makeNewScreen(UserRegistrationModel.USER_REGISTRATION_GRID_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
        else if ((iDocMode & ScreenConstants.MAINT_MODE) == ScreenConstants.MAINT_MODE)
            screen = Record.makeNewScreen(USER_ENTRY_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
        else if ((iDocMode & ScreenConstants.DISPLAY_MODE) == ScreenConstants.DISPLAY_MODE)
            screen = Record.makeNewScreen(USER_INFO_GRID_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
        else if ((iDocMode & UserInfo.VERBOSE_MAINT_MODE) == UserInfo.VERBOSE_MAINT_MODE)
            screen = Record.makeNewScreen(USER_INFO_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
        else if ((iDocMode & UserInfo.LOGIN_SCREEN_MODE) == UserInfo.LOGIN_SCREEN_MODE)
            screen = Record.makeNewScreen(USER_LOGIN_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
        else if ((iDocMode & UserInfo.PREFERENCES_SCREEN_MODE) == UserInfo.PREFERENCES_SCREEN_MODE)
            screen = Record.makeNewScreen(USER_PREFERENCES_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
        else if ((iDocMode & UserInfo.PASSWORD_CHANGE_SCREEN_MODE) == UserInfo.PASSWORD_CHANGE_SCREEN_MODE)
            screen = Record.makeNewScreen(USER_PASSWORD_CHANGE_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
        else
            screen = super.makeScreen(itsLocation, parentScreen, iDocMode, properties);
        return screen;
    }
    /**
     * Add this field in the Record's field sequence.
     */
    public BaseField setupField(int iFieldSeq)
    {
        BaseField field = null;
        //if (iFieldSeq == 0)
        //{
        //  field = new CounterField(this, ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        //  field.setHidden(true);
        //}
        //if (iFieldSeq == 1)
        //{
        //  field = new RecordChangedField(this, LAST_CHANGED, Constants.DEFAULT_FIELD_LENGTH, null, null);
        //  field.setHidden(true);
        //}
        //if (iFieldSeq == 2)
        //{
        //  field = new BooleanField(this, DELETED, Constants.DEFAULT_FIELD_LENGTH, null, new Boolean(false));
        //  field.setHidden(true);
        //}
        //if (iFieldSeq == 3)
        //  field = new PropertiesField(this, PROPERTIES, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 4)
            field = new ShortStringField(this, USER_NAME, 60, null, null);
        if (iFieldSeq == 5)
            field = new StringField(this, FIRST_NAME, 20, null, null);
        if (iFieldSeq == 6)
            field = new StringField(this, LAST_NAME, 20, null, null);
        if (iFieldSeq == 7)
            field = new StringField(this, EXTENSION, 4, null, null);
        if (iFieldSeq == 8)
            field = new PasswordField(this, PASSWORD, 80, null, null);
        if (iFieldSeq == 9)
            field = new DateField(this, TERMINATION_DATE, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 10)
            field = new BooleanField(this, READ_ONLY_RECORD, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 11)
            field = new UserGroupField(this, USER_GROUP_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 12)
            field = new ReferenceField(this, USER_REFERENCE, Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == 13)
        {
            field = new BooleanField(this, FRAMES, Constants.DEFAULT_FIELD_LENGTH, null, null);
            field.setVirtual(true);
        }
        if (iFieldSeq == 14)
        {
            field = new UserMenubarField(this, MENUBARS, 10, null, null);
            field.setVirtual(true);
        }
        if (iFieldSeq == 15)
        {
            field = new UserNavMenusField(this, NAV_MENUS, 10, null, null);
            field.setVirtual(true);
        }
        if (iFieldSeq == 16)
        {
            field = new UserJavaField(this, JAVA, 10, null, null);
            field.setVirtual(true);
        }
        if (iFieldSeq == 17)
        {
            field = new StringField(this, BANNERS, 10, null, null);
            field.setVirtual(true);
        }
        if (iFieldSeq == 18)
        {
            field = new UserLogosField(this, LOGOS, 10, null, null);
            field.setVirtual(true);
        }
        if (iFieldSeq == 19)
        {
            field = new UserLogosField(this, TRAILERS, 10, null, null);
            field.setVirtual(true);
        }
        if (iFieldSeq == 20)
        {
            field = new HelpPageField(this, HELP_PAGE, 10, null, null);
            field.setVirtual(true);
        }
        if (iFieldSeq == 21)
        {
            field = new UserLanguageField(this, LANGUAGE, 2, null, null);
            field.setVirtual(true);
        }
        if (iFieldSeq == 22)
        {
            field = new UserHomeField(this, HOME, 30, null, null);
            field.setVirtual(true);
        }
        if (iFieldSeq == 23)
        {
            field = new UserMenuField(this, MENU, 30, null, null);
            field.setVirtual(true);
        }
        if (iFieldSeq == 24)
        {
            field = new BooleanField(this, MENU_DESC, Constants.DEFAULT_FIELD_LENGTH, null, null);
            field.setVirtual(true);
        }
        if (iFieldSeq == 25)
        {
            field = new ContactTypeLevelOneField(this, CONTACT_TYPE_ID, 10, null, null);
            field.setVirtual(true);
        }
        if (iFieldSeq == 26)
        {
            field = new ContactField(this, CONTACT_ID, Constants.DEFAULT_FIELD_LENGTH, null, null);
            field.setVirtual(true);
        }
        if (field == null)
            field = super.setupField(iFieldSeq);
        return field;
    }
    /**
     * Add this key area description to the Record.
     */
    public KeyArea setupKey(int iKeyArea)
    {
        KeyArea keyArea = null;
        if (iKeyArea == 0)
        {
            keyArea = this.makeIndex(DBConstants.UNIQUE, ID_KEY);
            keyArea.addKeyField(ID, DBConstants.ASCENDING);
        }
        if (iKeyArea == 1)
        {
            keyArea = this.makeIndex(DBConstants.SECONDARY_KEY, USER_NAME_KEY);
            keyArea.addKeyField(USER_NAME, DBConstants.ASCENDING);
        }
        if (keyArea == null)
            keyArea = super.setupKey(iKeyArea);     
        return keyArea;
    }
    /**
     * AddMasterListeners Method.
     */
    public void addMasterListeners()
    {
        super.addMasterListeners();
        this.getField(UserInfo.USER_NAME).addListener(new CheckNonNumericListener(null));
        this.addListener(new ReadOnlyRecordHandler(this.getField(UserInfo.READ_ONLY_RECORD), true));
        this.addListener(new FileListener(null)
        {
            /**
             * Make sure the user group ID is non-null (set it to the anon group id)
             */
            public int doRecordChange(FieldInfo field, int iChangeType, boolean bDisplayOption)
            { // Read a valid record
                switch (iChangeType)
                {
                    case DBConstants.ADD_TYPE:
                    case DBConstants.UPDATE_TYPE:
                        if (this.getOwner().getField(UserInfo.USER_GROUP_ID).isNull())
                            if (this.getOwner().getRecordOwner() != null)
                        {
                            Record recUserControl = (Record)this.getOwner().getRecordOwner().getRecord(UserControl.USER_CONTROL_FILE);
                            if (recUserControl == null)
                            {
                                recUserControl = new UserControl(this.getOwner().getRecordOwner());
                                this.getOwner().addListener(new FreeOnFreeHandler(recUserControl));
                            }
                            this.getOwner().getField(UserInfo.USER_GROUP_ID).moveFieldToThis(recUserControl.getField(UserControl.ANON_USER_GROUP_ID));
                        }
                        break;
                }
                return super.doRecordChange(field, iChangeType, bDisplayOption);        // Initialize the record
            }
            
        });
    }
    /**
     * Convert the command to the screen document type.
     * @param strCommand The command text.
     * @param The standard document type (MAINT/DISPLAY/SELECT/MENU/etc).
     */
    public int commandToDocType(String strCommand)
    {
        if (UserInfo.VERBOSE_MAINT_SCREEN.equalsIgnoreCase(strCommand))
            return UserInfo.VERBOSE_MAINT_MODE;
        if (UserInfo.LOGIN_SCREEN.equalsIgnoreCase(strCommand))
            return UserInfo.LOGIN_SCREEN_MODE;
        if (UserInfo.ENTRY_SCREEN.equalsIgnoreCase(strCommand))
            return UserInfo.ENTRY_SCREEN_MODE;
        if (UserInfo.PREFERENCES_SCREEN.equalsIgnoreCase(strCommand))
            return UserInfo.PREFERENCES_SCREEN_MODE;
        if (UserInfo.PASSWORD_CHANGE_SCREEN.equalsIgnoreCase(strCommand))
            return UserInfo.PASSWORD_CHANGE_SCREEN_MODE;
        return super.commandToDocType(strCommand);
    }
    /**
     * Add The listeners to sync the property field with the virtual fields.
     */
    public void addPropertyListeners()
    {
        BaseField fldProperties = this.getField(PropertiesRecord.PROPERTIES);
        if (fldProperties.getListener(CopyConvertersHandler.class) != null)
            return;
        
        this.addPropertiesFieldBehavior(this.getField(UserInfo.FRAMES), DBParams.FRAMES);
        this.addPropertiesFieldBehavior(this.getField(UserInfo.JAVA), DBParams.JAVA);
        this.addPropertiesFieldBehavior(this.getField(UserInfo.HOME), DBParams.HOME);
        this.addPropertiesFieldBehavior(this.getField(UserInfo.MENU), DBParams.MENU);
        
        this.addPropertiesFieldBehavior(this.getField(UserInfo.BANNERS), DBParams.BANNERS);
        this.addPropertiesFieldBehavior(this.getField(UserInfo.TRAILERS), DBParams.TRAILERS);
        this.addPropertiesFieldBehavior(this.getField(UserInfo.MENUBARS), DBParams.MENUBARS);
        this.addPropertiesFieldBehavior(this.getField(UserInfo.LOGOS), DBParams.LOGOS);
        this.addPropertiesFieldBehavior(this.getField(UserInfo.NAV_MENUS), DBParams.NAVMENUS);
        this.addPropertiesFieldBehavior(this.getField(UserInfo.MENU_DESC), DBParams.MENUDESC);
        this.addPropertiesFieldBehavior(this.getField(UserInfo.HELP_PAGE), MenuConstants.USER_HELP_DISPLAY);
        this.addPropertiesFieldBehavior(this.getField(UserInfo.LANGUAGE), DBParams.LANGUAGE);
        
        Record recContactType = ((ReferenceField)this.getField(UserInfo.CONTACT_TYPE_ID)).getReferenceRecord();
        this.getField(UserInfo.CONTACT_TYPE_ID).addListener(new ReadSecondaryHandler(recContactType));
        BaseField fldContactTypeCode = recContactType.getField(ContactType.CODE);
        CopyConvertersHandler listener = new CopyConvertersHandler(new PropertiesConverter(fldProperties, DBParams.CONTACT_TYPE), fldContactTypeCode);
        this.getField(UserInfo.CONTACT_TYPE_ID).addListener(listener);
        listener.setRespondsToMode(DBConstants.INIT_MOVE, false);
        listener.setRespondsToMode(DBConstants.READ_MOVE, false);
        this.addPropertiesFieldBehavior(this.getField(UserInfo.CONTACT_TYPE_ID), DBParams.CONTACT_TYPE + DBParams.ID);
        this.addPropertiesFieldBehavior(this.getField(UserInfo.CONTACT_ID), DBParams.CONTACT_ID);
    }
    /**
     * GetUserInfo Method.
     */
    public boolean getUserInfo(String strUser, boolean bForceRead)
    {
        boolean bFound = false;
        if ((strUser == null) || (strUser.length() ==0))
            return false; // Not found.
        int iUserID = -1;
        try   {   // First see if strUser is the UserID in string format
            if (Utility.isNumeric(strUser))
                if ((strUser != null) && (strUser.length() > 0))
                    iUserID = Integer.parseInt(strUser);
            if (iUserID == 0)
                iUserID = -1;
        } catch (NumberFormatException ex)  {
            iUserID = -1;
        }
        if ((iUserID == -1) && (strUser.length() > 0))
        { // Read using the User name
            if (!bForceRead)
                if ((this.getEditMode() == DBConstants.EDIT_CURRENT) || (this.getEditMode() == DBConstants.EDIT_IN_PROGRESS))
                    if (this.getField(UserInfo.USER_NAME).toString().equalsIgnoreCase(strUser))
                        return true;    // Already current
            this.getField(UserInfo.USER_NAME).setString(strUser);
            this.setKeyArea(UserInfo.USER_NAME_KEY);
            try   {
                bFound = this.seek(null);
            } catch (DBException ex)    {
                ex.printStackTrace();
                bFound = false;
            }
            this.setKeyArea(UserInfo.ID_KEY);
        }
        if (iUserID != -1)
        {   // Valid UserID, read it!
            if (!bForceRead)
                if ((this.getEditMode() == DBConstants.EDIT_CURRENT) || (this.getEditMode() == DBConstants.EDIT_IN_PROGRESS))
                    if (this.getField(UserInfo.ID).getValue() == iUserID)
                        return true;    // Already current
            this.getField(UserInfo.ID).setValue(iUserID);
            try   {
                this.setKeyArea(UserInfo.ID_KEY);
                bFound = this.seek(null);
            } catch (DBException ex)    {
                ex.printStackTrace();
                bFound = false;
            }
        }
        return bFound;
    }
    /**
     * Is this a valid user property?.
     */
    public boolean validUserProperty(String strProperty)
    {
        if (DBParams.FRAMES.equalsIgnoreCase(strProperty))
            return true;
        if (DBParams.MENUBARS.equalsIgnoreCase(strProperty))
            return true;
        if (DBParams.NAVMENUS.equalsIgnoreCase(strProperty))
            return true;
        if (DBParams.JAVA.equalsIgnoreCase(strProperty))
            return true;
        if (DBParams.BANNERS.equalsIgnoreCase(strProperty))
            return true;
        if (DBParams.LOGOS.equalsIgnoreCase(strProperty))
            return true;
        if (DBParams.TRAILERS.equalsIgnoreCase(strProperty))
            return true;
        if (DBParams.LANGUAGE.equalsIgnoreCase(strProperty))
            return true;
        return false;
    }
    /**
     * SetupNewUserHandler Method.
     */
    public void setupNewUserHandler()
    {
        this.addListener(new SetupNewUserHandler(null));
    }

}
