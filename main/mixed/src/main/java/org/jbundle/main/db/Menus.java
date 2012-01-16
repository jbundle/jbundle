/**
 * @(#)Menus.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
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
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.net.*;
import java.util.*;
import org.jbundle.base.db.xmlutil.*;
import org.jbundle.model.main.db.*;

/**
 *  Menus - Menu maintenance.
 */
public class Menus extends Folder
     implements MenusModel
{
    private static final long serialVersionUID = 1L;

    //public static final int kID = kID;
    //public static final int kSequence = kSequence;
    //public static final int kName = kName;
    //public static final int kComment = kComment;
    //public static final int kParentFolderID = kParentFolderID;
    //public static final int kCode = kCode;
    public static final int kType = kFolderLastField + 1;
    public static final int kAutoDesc = kType + 1;
    public static final int kProgram = kAutoDesc + 1;
    public static final int kParams = kProgram + 1;
    public static final int kIconResource = kParams + 1;
    public static final int kKeywords = kIconResource + 1;
    public static final int kXmlData = kKeywords + 1;
    public static final int kMenusHelp = kXmlData + 1;
    public static final int kMenusLastField = kMenusHelp;
    public static final int kMenusFields = kMenusHelp - DBConstants.MAIN_FIELD + 1;

    public static final int kIDKey = DBConstants.MAIN_KEY_FIELD;
    public static final int kParentFolderIDKey = kIDKey + 1;
    public static final int kCodeKey = kParentFolderIDKey + 1;
    public static final int kTypeKey = kCodeKey + 1;
    public static final int kMenusLastKey = kTypeKey;
    public static final int kMenusKeys = kTypeKey - DBConstants.MAIN_KEY_FIELD + 1;
    /**
     * Default constructor.
     */
    public Menus()
    {
        super();
    }
    /**
     * Constructor.
     */
    public Menus(RecordOwner screen)
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

    public static final String kMenusFile = "Menus";
    /**
     * Get the table name.
     */
    public String getTableNames(boolean bAddQuotes)
    {
        return (m_tableName == null) ? Record.formatTableNames(kMenusFile, bAddQuotes) : super.getTableNames(bAddQuotes);
    }
    /**
     * Get the name of a single record.
     */
    public String getRecordName()
    {
        return "Menu";
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
        return DBConstants.LOCAL | DBConstants.SHARED_DATA | DBConstants.LOCALIZABLE;
    }
    /**
     * MakeScreen Method.
     */
    public BaseScreen makeScreen(ScreenLocation itsLocation, BasePanel parentScreen, int iDocMode, Map<String,Object> properties)
    {
        BaseScreen screen = null;
        if ((iDocMode & ScreenConstants.DOC_MODE_MASK) == ScreenConstants.DETAIL_MODE)
            screen = BaseScreen.makeNewScreen(MENUS_GRID_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
        else if ((iDocMode & ScreenConstants.MAINT_MODE) == ScreenConstants.MAINT_MODE)
            screen = BaseScreen.makeNewScreen(MENUS_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
        else if ((iDocMode & ScreenConstants.DISPLAY_MODE) != 0)
            screen = BaseScreen.makeNewScreen(MENUS_GRID_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
        else if ((iDocMode & ScreenConstants.MENU_MODE) != 0)
            screen = BaseScreen.makeNewScreen(MENU_SCREEN_CLASS, itsLocation, parentScreen, iDocMode | ScreenConstants.DONT_DISPLAY_FIELD_DESC, properties, this, true);
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
        if (iFieldSeq == kSequence)
            field = new ShortField(this, "Sequence", Constants.DEFAULT_FIELD_LENGTH, null, new Short((short)0));
        if (iFieldSeq == kType)
            field = new StringField(this, "Type", 10, null, null);
        if (iFieldSeq == kName)
            field = new StringField(this, "Name", 50, null, null);
        if (iFieldSeq == kAutoDesc)
            field = new BooleanField(this, "AutoDesc", Constants.DEFAULT_FIELD_LENGTH, null, new Boolean(true));
        if (iFieldSeq == kComment)
            field = new XmlField(this, "Comment", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kProgram)
            field = new StringField(this, "Program", 255, null, null);
        if (iFieldSeq == kParams)
            field = new XMLPropertiesField(this, "Params", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kIconResource)
            field = new StringField(this, "IconResource", 255, null, null);
        if (iFieldSeq == kKeywords)
            field = new StringField(this, "Keywords", 50, null, null);
        if (iFieldSeq == kParentFolderID)
            field = new MenusField(this, "ParentFolderID", Constants.DEFAULT_FIELD_LENGTH, null, null);
        //if (iFieldSeq == kCode)
        //  field = new StringField(this, "Code", 30, null, null);
        if (iFieldSeq == kXmlData)
            field = new XmlField(this, "XmlData", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (iFieldSeq == kMenusHelp)
            field = new XmlField(this, "MenusHelp", Constants.DEFAULT_FIELD_LENGTH, null, null);
        if (field == null)
        {
            field = super.setupField(iFieldSeq);
            if (field == null) if (iFieldSeq < kMenusLastField)
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
        if (iKeyArea == kParentFolderIDKey)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, "ParentFolderID");
            keyArea.addKeyField(kParentFolderID, DBConstants.ASCENDING);
            keyArea.addKeyField(kSequence, DBConstants.ASCENDING);
            keyArea.addKeyField(kType, DBConstants.ASCENDING);
            keyArea.addKeyField(kName, DBConstants.ASCENDING);
        }
        if (iKeyArea == kCodeKey)
        {
            keyArea = this.makeIndex(DBConstants.SECONDARY_KEY, "Code");
            keyArea.addKeyField(kCode, DBConstants.ASCENDING);
        }
        if (iKeyArea == kTypeKey)
        {
            keyArea = this.makeIndex(DBConstants.NOT_UNIQUE, "Type");
            keyArea.addKeyField(kType, DBConstants.ASCENDING);
            keyArea.addKeyField(kProgram, DBConstants.ASCENDING);
        }
        if (keyArea == null) if (iKeyArea < kMenusLastKey)
        {
            keyArea = super.setupKey(iKeyArea);     
            if (keyArea == null) if (iKeyArea < kMenusLastKey)
                keyArea = new EmptyKey(this);
        }
        return keyArea;
    }
    /**
     * Get the html code to access this link.
     */
    public String getLink()
    {
        String strType = this.getField(Menus.kType).getString();
        String strLink = this.getField(Menus.kProgram).getString();
        if (strLink != null)
            if (strLink.length() > 1)
                if (strLink.charAt(0) == '.')
                    strLink = DBConstants.ROOT_PACKAGE + strLink.substring(1);
        String strTitle = this.getField(Menus.kName).getString();
        String strParams = Utility.addURLParam(null, strType, strLink); // Default command
        if ((strType.equalsIgnoreCase(DBParams.SCREEN))
                || (strType.equalsIgnoreCase(DBParams.RECORD)))
        {
            strParams = Utility.addURLParam(null, strType, strLink);
        }
        else if ((strType.equalsIgnoreCase(MenuConstants.MENUREC)) 
                || (strType.equalsIgnoreCase(MenuConstants.FORM))
                || (strType.equalsIgnoreCase(MenuConstants.GRID)))
        {
            strParams = Utility.addURLParam(null, DBParams.RECORD, strLink);
            strParams = Utility.addURLParam(strParams, DBParams.COMMAND, strType);
        }
        else if (strType.equalsIgnoreCase(DBParams.MENU))
        { // Default is correct
        }
        else if (strType.equalsIgnoreCase(DBParams.XML))
        {
            strParams = Utility.addURLParam(strParams, "title", strTitle);
        }
        else if (strType.equalsIgnoreCase(DBParams.LINK))
        {
            strParams = strLink;
            if ((strLink.indexOf('.') < strLink.indexOf('/'))
                && (strLink.indexOf('.') != -1))
                    strParams = "http://" + strLink;
        }
        else if (strType.equalsIgnoreCase(DBParams.MAIL))
        {
            strParams = strLink;
            if (strLink.indexOf("mailto:") != 0)
                strParams = "mailto:" + strLink;
        }
        else if (strType.equalsIgnoreCase(DBParams.APPLET))
        { // Default is usually okay
            if ((strLink == null) || (strLink.length() == 0))
                strParams = Utility.addURLParam(null, strType, DBParams.BASE_APPLET); // Default command
            if (strParams.indexOf(DBConstants.DEFAULT_SERVLET) != 0)
                strParams = DBConstants.DEFAULT_SERVLET + strParams;   // Make sure applets don't go through cocoon
        }
        else
        { // Default is okay
        }
        Map<String,Object> properties = ((PropertiesField)this.getField(Menus.kParams)).loadProperties();
        if (properties != null)
            if (DBConstants.BLANK.equalsIgnoreCase((String)properties.get(DBParams.USER_NAME)))
        {       // They want me to fill in the user name
            if (this.getTask() != null)
            {
                if (this.getTask().getProperty(DBParams.USER_ID) != null)
                    properties.put(DBParams.USER_ID, this.getTask().getProperty(DBParams.USER_ID));
        // todo (don) For now, don't pass authentication to remote menu items
        //        if (this.getTask().getProperty(DBParams.AUTH_TOKEN) != null)    // This is used instead of a password to authenticate
        //            properties.put(DBParams.AUTH_TOKEN, this.getTask().getProperty(DBParams.AUTH_TOKEN));
                properties.remove(DBParams.USER_NAME);
            }
        }
        if (properties != null)
            if (!strType.equalsIgnoreCase(DBParams.MENU))
                strParams = Utility.propertiesToURL(strParams, properties);
        return strParams;
    }
    /**
     * Add a tag to this XML for the menu link.
     */
    public StringBuffer addLinkTag(StringBuffer sb)
    {
        int iIndex = sb.lastIndexOf(Utility.endTag(this.getTableNames(false)));
        if (iIndex != -1)
        {
            String strLink = Utility.encodeXML(this.getLink());
            String strHelpLink = strLink;
            if (strLink.indexOf('?') != -1)   // Always
                strHelpLink = "?" + DBParams.HELP + "=" + "&amp;" + strLink.substring(strLink.indexOf('?') + 1);
            sb.insert(iIndex,
                    Utility.startTag(XMLTags.LINK) +
                        strLink +
                    Utility.endTag(XMLTags.LINK) + XmlUtilities.NEWLINE +
                    Utility.startTag(XMLTags.HELPLINK) +
                        strHelpLink +
                    Utility.endTag(XMLTags.HELPLINK) + XmlUtilities.NEWLINE);
        }
        return sb;
    }
    /**
     * Get the XML for this menu item and it's sub-menus.
     */
    public String getSubMenuXML()
    {
        StringBuffer sbMenuArea = new StringBuffer();
        
        if (this.getEditMode() == Constants.EDIT_CURRENT)
        {
            if (this.getField(Menus.kIconResource).isNull())
            {
                String strIcon = this.getField(Menus.kType).toString();
                if (strIcon.length() > 0)
                    strIcon = strIcon.substring(0, 1).toUpperCase() + strIcon.substring(1);
                this.getField(Menus.kIconResource).setString(strIcon);
            }
            sbMenuArea.append(XmlUtilities.createXMLStringRecord(this));
            sbMenuArea = this.addLinkTag(sbMenuArea);
        }
        
        if (sbMenuArea.length() == 0)
            sbMenuArea.append(
                    Utility.startTag(this.getTableNames(false)) +
                    " <Name>Name</Name>" +
                    " <Description>Description</Description>" +
                    " <Program>Program</Program>" +
                    " <Params>Params</Params>" +
                    " <IconResource>IconResource</IconResource>" +
                    " <Keywords>Keywords</Keywords>" +
                    " <Html>Html</Html>" +
                    Utility.endTag(this.getTableNames(false)));
        
        StringBuffer sbContentArea = new StringBuffer();
        sbContentArea.append(Utility.startTag(XMLTags.MENU_LIST));
        try   {
            String strMenu = this.getField(Menus.kID).toString();
            this.setKeyArea(Menus.kParentFolderIDKey);
            FileListener behavior = new StringSubFileFilter(strMenu, Menus.kParentFolderID, null, -1, null, -1);
            this.addListener(behavior);
            this.close();
            while (this.hasNext())
            {
                this.next();
                if (this.getField(Menus.kIconResource).isNull())
                {
                    String strIcon = this.getField(Menus.kType).toString();
                    if (strIcon.length() > 0)
                        strIcon = strIcon.substring(0, 1).toUpperCase() + strIcon.substring(1);
                    this.getField(Menus.kIconResource).setString(strIcon);
                }
                StringBuffer sbMenuItem = new StringBuffer();
                sbMenuItem.append(XmlUtilities.createXMLStringRecord(this));
                sbMenuItem = this.addLinkTag(sbMenuItem);
                sbContentArea.append(sbMenuItem);
            }
            this.removeListener(behavior, true);
        } catch (DBException ex)    {
            ex.printStackTrace();
        }
        sbContentArea.append(Utility.endTag(XMLTags.MENU_LIST));
        sbMenuArea.insert(sbMenuArea.lastIndexOf(Utility.endTag(this.getTableNames(false))), sbContentArea);
        return sbMenuArea.toString();
    }

}
