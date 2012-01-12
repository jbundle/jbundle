/**
 * @(#)MenusModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.main.db;

public interface MenusModel extends org.jbundle.model.main.db.FolderModel
{
    public static final String ID_KEY = "ID";
    public static final String TYPE = "Type";
    public static final String PARAMS = "Params";
    public static final String ICON_RESOURCE = "IconResource";
    public static final String PARENT_FOLDER_ID_KEY = "ParentFolderID";
    public static final String PROGRAM = "Program";
    public static final String MENUS_FILE = "Menus";
    public static final String THIN_CLASS = "org.jbundle.thin.main.db.Menus";
    public static final String THICK_CLASS = "org.jbundle.main.db.Menus";
    public static final String XML_DATA = "XmlData";
    public static final String KEYWORDS = "Keywords";
    public static final String TYPE_KEY = "Type";
    /**
     * Get the html code to access this link.
     */
    public String getLink();
    public String getSubMenuXML();
}
