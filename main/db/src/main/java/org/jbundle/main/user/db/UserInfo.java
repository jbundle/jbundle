/**
 * @(#)UserInfo.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.main.user.db;

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

    //public static final int kID = kID;
    //public static final int kLastChanged = kLastChanged;
    //public static final int kProperties = kProperties;
    public static final int kUserName = kPropertiesRecordLastField + 1;
    public static final int kFirstName = kUserName + 1;
    public static final int kLastName = kFirstName + 1;
    public static final int kExtension = kLastName + 1;
    public static final int kPassword = kExtension + 1;
    public static final int kTerminationDate = kPassword + 1;
    public static final int kReadOnlyRecord = kTerminationDate + 1;
    public static final int kUserGroupID = kReadOnlyRecord + 1;
    public static final int kUserReference = kUserGroupID + 1;
    public static final int kFrames = kUserReference + 1;
    public static final int kMenubars = kFrames + 1;
    public static final int kNavMenus = kMenubars + 1;
    public static final int kJava = kNavMenus + 1;
    public static final int kBanners = kJava + 1;
    public static final int kLogos = kBanners + 1;
    public static final int kTrailers = kLogos + 1;
    public static final int kHelpPage = kTrailers + 1;
    public static final int kLanguage = kHelpPage + 1;
    public static final int kHome = kLanguage + 1;
    public static final int kMenu = kHome + 1;
    public static final int kMenuDesc = kMenu + 1;
    public static final int kContactTypeID = kMenuDesc + 1;
    public static final int kContactID = kContactTypeID + 1;
    public static final int kUserInfoLastField = kContactID;
    public static final int kUserInfoFields = kContactID - DBConstants.MAIN_FIELD + 1;

    public static final int kIDKey = DBConstants.MAIN_KEY_FIELD;
    public static final int kUserNameKey = kIDKey + 1;
    public static final int kUserInfoLastKey = kUserNameKey;
    public static final int kUserInfoKeys = kUserNameKey - DBConstants.MAIN_KEY_FIELD + 1;
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

    public static final String kUserInfoFile = "UserInfo";
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(kUserInfoFile, bAddQuotes) : super.getTableNames(bAddQuotes);
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
        //if (iFieldSeq == kID)
        //{
        //  field = new CounterField(this, "ID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        //  field.setHidden(true);
        //}
        if (iFieldSeq == kUserName)
            field = new ShortStringField(this, "UserName", 60, null, null);
        if (iFieldSeq == kFirstName)
            field = new StringField(this, "FirstName", 20, null, null);
        if (iFieldSeq == kLastName)
            field = new StringField(this, "LastName", 20, null, null);
        if (iFieldSeq == kExtension)
            field = new StringField(this, "Extension", 4, null, null);
        if (iFieldSeq == kPassword)
            field = new PasswordField(this, "Password", 80, null, null);
        if (iFieldSeq == kTerminationDate)
            field = new DateField(this, "TerminationDate", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kReadOnlyRecord)
            field = new BooleanField(this, "ReadOnlyRecord", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kUserGroupID)
            field = new UserGroupField(this, "UserGroupID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kUserReference)
            field = new ReferenceField(this, "UserReference", Constants.DEFAULT_FIELD_LENGTH, null, null);
        //if (iFieldSeq == kLastChanged)
        //{
        //  field = new RecordChangedField(this, "LastChanged", Constants.DEFAULT_FIELD_LENGTH, null, null);
        //  field.setHidden(true);
        //}
        if (iFieldSeq == kFrames)
        {
            field = new BooleanField(this, "Frames", Constants.DEFAULT_FIELD_LENGTH, null, null);
            field.setVirtual(true);
        }
        if (iFieldSeq == kMenubars)
        {
            field = new UserMenubarField(this, "Menubars", 10, null, null);
            field.setVirtual(true);
        }
        if (iFieldSeq == kNavMenus)
        {
            field = new UserNavMenusField(this, "NavMenus", 10, null, null);
            field.setVirtual(true);
        }
        if (iFieldSeq == kJava)
        {
            field = new UserJavaField(this, "Java", 10, null, null);
            field.setVirtual(true);
        }
        if (iFieldSeq == kBanners)
        {
            field = new StringField(this, "Banners", 10, null, null);
            field.setVirtual(true);
        }
        if (iFieldSeq == kLogos)
        {
            field = new UserLogosField(this, "Logos", 10, null, null);
            field.setVirtual(true);
        }
        if (iFieldSeq == kTrailers)
        {
            field = new UserLogosField(this, "Trailers", 10, null, null);
            field.setVirtual(true);
        }
        if (iFieldSeq == kHelpPage)
        {
            field = new HelpPageField(this, "HelpPage", 10, null, null);
            field.setVirtual(true);
        }
        if (iFieldSeq == kLanguage)
        {
            field = new UserLanguageField(this, "Language", 2, null, null);
            field.setVirtual(true);
        }
        if (iFieldSeq == kHome)
        {
            field = new UserHomeField(this, "Home", 30, null, null);
            field.setVirtual(true);
        }
        if (iFieldSeq == kMenu)
        {
            field = new UserMenuField(this, "Menu", 30, null, null);
            field.setVirtual(true);
        }
        if (iFieldSeq == kMenuDesc)
        {
            field = new BooleanField(this, "MenuDesc", Constants.DEFAULT_FIELD_LENGTH, null, null);
            field.setVirtual(true);
        }
        if (iFieldSeq == kContactTypeID)
        {
            field = new ContactTypeLevelOneField(this, "ContactTypeID", 10, null, null);
            field.setVirtual(true);
        }
        if (iFieldSeq == kContactID)
        {
            field = new ContactField(this, "ContactID", Constants.DEFAULT_FIELD_LENGTH, null, null);
            field.setVirtual(true);
        }
        //if (iFieldSeq == kProperties)
        //  field = new PropertiesField(this, "Properties", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kUserInfoLastField)
                field = new EmptyField(this);
        }
        return field;
    }
    /**
     * Add this key area description to the Record.
     */
    public KeyArea setupKey(int iKeyArea)
    {
        KeyArea keyArea = null;
        if (iKeyArea == kIDKey)
        {
            keyArea = this.makeIndex(DBConstants.UNIQUE, "PrimaryKey");
            keyArea.addKeyField(kID, DBConstants.ASCENDING);
        }
        if (iKeyArea == kUserNameKey)
        {
            keyArea = this.makeIndex(DBConstants.SECONDARY_KEY, "UserName");
            keyArea.addKeyField(kUserName, DBConstants.ASCENDING);
        }
        if (keyArea == null) if (iKeyArea < kUserInfoLastKey)
        {
            keyArea = super.setupKey(iKeyArea);     
            if (keyArea == null) if (iKeyArea < kUserInfoLastKey)
                keyArea = new EmptyKey(this);
        }
        return keyArea;
    }
    /**
     * AddMasterListeners Method.
     */
    public void addMasterListeners()
    {
        super.addMasterListeners();
        this.getField(UserInfo.kUserName).addListener(new CheckNonNumericListener(null));
        this.addListener(new ReadOnlyRecordHandler(UserInfo.kReadOnlyRecord, true));
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
                        if (this.getOwner().getField(UserInfo.kUserGroupID).isNull())
                            if (this.getOwner().getRecordOwner() != null)
                        {
                            Record recUserControl = (Record)this.getOwner().getRecordOwner().getRecord(UserControl.kUserControlFile);
                            if (recUserControl == null)
                            {
                                recUserControl = new UserControl(this.getOwner().getRecordOwner());
                                this.getOwner().addListener(new FreeOnFreeHandler(recUserControl));
                            }
                            this.getOwner().getField(UserInfo.kUserGroupID).moveFieldToThis(recUserControl.getField(UserControl.kAnonUserGroupID));
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
        BaseField fldProperties = this.getField(PropertiesRecord.kProperties);
        if (fldProperties.getListener(CopyConvertersHandler.class) != null)
            return;
        
        this.addPropertiesFieldBehavior(this.getField(UserInfo.kFrames), DBParams.FRAMES);
        this.addPropertiesFieldBehavior(this.getField(UserInfo.kJava), DBParams.JAVA);
        this.addPropertiesFieldBehavior(this.getField(UserInfo.kHome), DBParams.HOME);
        this.addPropertiesFieldBehavior(this.getField(UserInfo.kMenu), DBParams.MENU);
        
        this.addPropertiesFieldBehavior(this.getField(UserInfo.kBanners), DBParams.BANNERS);
        this.addPropertiesFieldBehavior(this.getField(UserInfo.kTrailers), DBParams.TRAILERS);
        this.addPropertiesFieldBehavior(this.getField(UserInfo.kMenubars), DBParams.MENUBARS);
        this.addPropertiesFieldBehavior(this.getField(UserInfo.kLogos), DBParams.LOGOS);
        this.addPropertiesFieldBehavior(this.getField(UserInfo.kNavMenus), DBParams.NAVMENUS);
        this.addPropertiesFieldBehavior(this.getField(UserInfo.kMenuDesc), DBParams.MENUDESC);
        this.addPropertiesFieldBehavior(this.getField(UserInfo.kHelpPage), MenuConstants.USER_HELP_DISPLAY);
        this.addPropertiesFieldBehavior(this.getField(UserInfo.kLanguage), DBParams.LANGUAGE);
        
        Record recContactType = ((ReferenceField)this.getField(UserInfo.kContactTypeID)).getReferenceRecord();
        this.getField(UserInfo.kContactTypeID).addListener(new ReadSecondaryHandler(recContactType));
        BaseField fldContactTypeCode = recContactType.getField(ContactType.kCode);
        CopyConvertersHandler listener = new CopyConvertersHandler(new PropertiesConverter(fldProperties, DBParams.CONTACT_TYPE), fldContactTypeCode);
        this.getField(UserInfo.kContactTypeID).addListener(listener);
        listener.setRespondsToMode(DBConstants.INIT_MOVE, false);
        listener.setRespondsToMode(DBConstants.READ_MOVE, false);
        this.addPropertiesFieldBehavior(this.getField(UserInfo.kContactTypeID), DBParams.CONTACT_TYPE + DBParams.ID);
        this.addPropertiesFieldBehavior(this.getField(UserInfo.kContactID), DBParams.CONTACT_ID);
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
                    if (this.getField(UserInfo.kUserName).toString().equalsIgnoreCase(strUser))
                        return true;    // Already current
            this.getField(UserInfo.kUserName).setString(strUser);
            this.setKeyArea(UserInfo.kUserNameKey);
            try   {
                bFound = this.seek(null);
            } catch (DBException ex)    {
                ex.printStackTrace();
                bFound = false;
            }
            this.setKeyArea(UserInfo.kIDKey);
        }
        if (iUserID != -1)
        {   // Valid UserID, read it!
            if (!bForceRead)
                if ((this.getEditMode() == DBConstants.EDIT_CURRENT) || (this.getEditMode() == DBConstants.EDIT_IN_PROGRESS))
                    if (this.getField(UserInfo.kID).getValue() == iUserID)
                        return true;    // Already current
            this.getField(UserInfo.kID).setValue(iUserID);
            try   {
                this.setKeyArea(UserInfo.kIDKey);
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
