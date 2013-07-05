/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.model;

/**
 * @(#)Constants.java 1.16 95/12/14 Don Corley
 *
 * Copyright © 2012 tourgeek.com. All Rights Reserved.
 *      don@tourgeek.com
 *
 */
import org.jbundle.thin.base.db.Params;

/**
 * XMLTags = Used to create screen xml.
 */
public interface XMLTags extends Params
{
    public static final String FULL_SCREEN = "full-screen";
    public static final String TITLE = "title";
    public static final String META_REDIRECT = "meta-redirect";
    public static final String META_KEYWORDS = "meta-keywords";
    public static final String META_DESCRIPTION = "meta-description";
    public static final String CONTENT_AREA = "content-area xmlns:xfm=\"http://www.w3.org/2000/12/xforms\" xmlns:fo=\"http://www.w3.org/1999/XSL/Format\"";
    public static final String HTML = "html";
    public static final String TEXT = "text";
    public static final String MENU_LIST = "menu_list";
    public static final String LINK = "link";
    public static final String HELPLINK = "helplink";
    public static final String FIELD_LIST = "field_list";
    public static final String KEY_LIST = "key_list";
    public static final String FILE = "file";
    public static final String DATA = "data";
    public static final String DETAIL = "detail";
    public static final String CONTROLS = "form";
    public static final String FOOTING = "footing";
    public static final String HEADING = "heading";
    public static final String ERROR = "error";
    public static final String ERROR_CODE = "errorCode";
    public static final String STATUS_TEXT = "status-text";
}
