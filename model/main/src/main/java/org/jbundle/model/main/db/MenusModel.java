/**
 * @(#)MenusModel.
 * Copyright © 2011 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.model.main.db;

import org.jbundle.model.main.db.*;

public interface MenusModel extends FolderModel
{
    public static final String TYPE = "Type";
    public static final String PROGRAM = "Program";
    public static final String PARAMS = "Params";
    public static final String ICON_RESOURCE = "IconResource";
    public static final String KEYWORDS = "Keywords";
    public static final String XML_DATA = "XmlData";

    public static final String PARENT_FOLDER_ID_KEY = "ParentFolderID";

    public static final String CODE_KEY = "Code";

    public static final String TYPE_KEY = "Type";

    public static final String MENUS_FILE = "Menus";
    public static final String THIN_CLASS = "org.jbundle.thin.main.db.Menus";
    public static final String THICK_CLASS = "org.jbundle.main.db.Menus";
    /**
     * Get the html code to access this link.
     */
    public String getLink();
    /**
     * Get the XML for this menu item and it's sub-menus.
     */
    public String getSubMenuXML();

}
