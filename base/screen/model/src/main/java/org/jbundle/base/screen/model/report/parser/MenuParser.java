/*
 *  @(#)CustSaleDetailParser.
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.model.report.parser;

import java.io.PrintWriter;

import org.jbundle.base.db.Record;
import org.jbundle.base.db.filter.StringSubFileFilter;
import org.jbundle.base.field.BaseListener;
import org.jbundle.base.model.DBConstants;
import org.jbundle.base.model.DBParams;
import org.jbundle.base.model.HtmlConstants;
import org.jbundle.base.model.RecordOwner;
import org.jbundle.base.model.Utility;
import org.jbundle.model.main.db.MenusModel;


/**
 *  Parse the Menu detail records.
 */
public class MenuParser extends BaseMenuParser
{
    /**
     *  Default constructor.
     */
    public MenuParser()
    {
        super();
    }
    /**
     *  Constructor.
     */
    public MenuParser(RecordOwner screen, Record recDetail)
    {
        this();
        this.init(screen, recDetail);
    }
    /**
     *  Initialize class fields.
     */
    public void init(RecordOwner screen, Record recDetail)
    {
        super.init(screen, recDetail);
    }
    /**
     *  Output this screen using HTML..
     */
    public void printHtmlTitle(PrintWriter out, String strTag, String strParams, String strData)
    {
        out.print(m_recDetail.getField(MenusModel.NAME).toString());
    }
    /**
     *  Output this screen using HTML.
     */
    public void printHtmlMenuDesc(PrintWriter out, String strTag, String strParams, String strData)
    {
        String strText = m_recDetail.getField(MenusModel.COMMENT).toString();
        strText = MenuParser.stripHtmlOverhead(strText);
        out.print(strText);
    }
    /**
     * Get rid of the extra HTML code.
     * @param strText
     * @return
     */
    public static String stripHtmlOverhead(String strText)
    {
        strText = MenuParser.stripTopLevelTag(strText, "html");
        strText = MenuParser.stripTopLevelTag(strText, "body");
        return strText;
    }
    /**
     * Get rid of the extra HTML code.
     * @param strText
     * @return
     */
    public static String stripTopLevelTag(String strText, String strTag)
    {
        int iStart = MenuParser.getFirstNonWhitespace(strText);
        int iEnd = MenuParser.getLastNonWhitespace(strText);
        if ((iStart != -1) && (iEnd != -1))
        {
            String strStartTag = Utility.startTag(strTag);
            String strEndTag = Utility.endTag(strTag);
            if (strText.substring(iStart, iStart + strStartTag.length()).equalsIgnoreCase(strStartTag))
                if (strText.substring(iEnd - strEndTag.length() + 1, iEnd + 1).equalsIgnoreCase(strEndTag))
                    strText = strText.substring(iStart + strStartTag.length(), iEnd - strEndTag.length() + 1);
        }
        return strText;
    }
    /**
     * Get the position of the first non-whitespace char.
     * @param strText
     * @return
     */
    public static int getFirstNonWhitespace(String strText)
    {
        if (strText != null)
        {
            for (int i = 0; i < strText.length(); i++)
            {
                if (!Character.isWhitespace(strText.charAt(i)))
                    return i;
            }
        }
        return -1;
    }
    /**
     * Get the position of the first non-whitespace char.
     * @param strText
     * @return
     */
    public static int getLastNonWhitespace(String strText)
    {
        if (strText != null)
        {
            for (int i = strText.length() - 1; i >= 0; i--)
            {
                if (!Character.isWhitespace(strText.charAt(i)))
                    return i;
            }
        }
        return -1;
    }
    /**
     *  Output this screen using HTML.
     */
    public void printHtmlType(PrintWriter out, String strTag, String strParams, String strData)
    {
        out.print(m_recDetail.getField(MenusModel.TYPE).toString());
    }
    /**
     *  Output this screen using HTML.
     */
    public void printHtmlIcon(PrintWriter out, String strTag, String strParams, String strData)
    {
        String strIcon = m_recDetail.getField(MenusModel.ICON_RESOURCE).toString();
        if (strIcon.length() == 0)
        {
            strIcon = m_recDetail.getField(MenusModel.TYPE).toString();
            if (strIcon.length() > 0)
                strIcon = strIcon.substring(0, 1).toUpperCase() + strIcon.substring(1);
        }
        if (strIcon.indexOf('.') == -1)
            strIcon = strIcon + ".gif";
        if (strIcon.indexOf('/') != 0)
            strIcon = HtmlConstants.HTML_ROOT + HtmlConstants.ICON_DIR + strIcon;
        out.print(strIcon + "\n");
    }
    /**
     *  Output this screen using HTML.
     */
    public void printHtmlLink(PrintWriter out, String strTag, String strParams, String strData)
    {
        String strLink = ((MenusModel)m_recDetail).getLink();
/*      
        String strParam = this.getRecordOwner().getProperty(DBParams.MENUBARS);
        if (strParam != null)
            strLink = Utility.addURLParam(strLink, DBParams.MENUBARS, strParam);
        strParam = this.getRecordOwner().getProperty(DBParams.NAVMENUS);
        if (strParam != null)
            strLink = Utility.addURLParam(strLink, DBParams.NAVMENUS, strParam);
        strParam = this.getRecordOwner().getProperty(DBParams.LOGOS);
        if (strParam != null)
            strLink = Utility.addURLParam(strLink, DBParams.LOGOS, strParam);
        strParam = this.getRecordOwner().getProperty(DBParams.TRAILERS);
        if (strParam != null)
            strLink = Utility.addURLParam(strLink, DBParams.TRAILERS, strParam);
        strParam = this.getRecordOwner().getProperty(DBConstants.DB_SUFFIX);
        if (strParam != null)
            strLink = Utility.addURLParam(strLink, DBConstants.DB_SUFFIX, strParam);
*/      
        out.print(strLink);
    }
    /**
     *  Output this screen using HTML.
     */
    public void printHtmlJavaLogo(PrintWriter out, String strTag, String strParams, String strData)
    {
        String strType = m_recDetail.getField(MenusModel.TYPE).toString();
        String strJava = this.getRecordOwner().getProperty(DBParams.JAVA);
        if ((strJava == null) || (strJava.length() == 0))
            strJava = DBConstants.NO;
        if (strJava.toUpperCase().charAt(0) != 'N')
            if ((strType.equalsIgnoreCase(DBParams.RECORD))
                || (strType.equalsIgnoreCase(DBParams.SCREEN))
                    || (strType.equalsIgnoreCase("form")))
            out.print(strData);
    }
    /**
     * Code to display a Menu.
     */
    public void preSetupGrid(String strMenu)
    {
        if (strMenu == null)    // Always
            strMenu = m_recDetail.getField(MenusModel.ID).toString();

        m_recDetail.setKeyArea(MenusModel.PARENT_FOLDER_ID_KEY);
        m_recDetail.addListener(new StringSubFileFilter(strMenu, m_recDetail.getField(MenusModel.PARENT_FOLDER_ID), null, null, null, null));
    }
    /**
     * Code to display a Menu.
     */
    public void postSetupGrid()
    {
        Record menu = m_recDetail;
        BaseListener behMenu = menu.getListener(StringSubFileFilter.class.getName());
        menu.removeListener(behMenu, true);
    }
}
