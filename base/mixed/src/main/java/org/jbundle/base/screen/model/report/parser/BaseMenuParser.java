/**
 *  @(#)CustSaleDetailParser.
 *  Copyright � 1998 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.model.report.parser;

import java.io.PrintWriter;

import org.jbundle.base.db.Record;
import org.jbundle.base.db.RecordOwner;
import org.jbundle.base.util.DBConstants;
import org.jbundle.base.util.DBParams;
import org.jbundle.base.util.HtmlConstants;
import org.jbundle.base.util.Utility;
import org.jbundle.model.DBException;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.util.ThinMenuConstants;


/**
 *  Parse the Menu detail records.
 */
public class BaseMenuParser extends XMLParser
{
    /**
     * 
     */
    protected Record m_recDetail = null;
    /**
     *  Default constructor.
     */
    public BaseMenuParser()
    {
        super();
    }
    /**
     *  Constructor.
     */
    public BaseMenuParser(RecordOwner screen, Record recDetail)
    {
        this();
        this.init(screen, recDetail);
    }
    /**
     *  Initialize class fields.
     */
    public void init(RecordOwner screen, Record recDetail)
    {
        m_recDetail = null;
        m_recDetail = recDetail;
        super.init(screen);
    }
    /**
     *  Output this screen using HTML.
     */
    public void parseHtmlData(PrintWriter out, String str)
    {
//      if ((str == null) || (str.length() == 0))
//          str = "<DATE/> - <DESC/>";
        super.parseHtmlData(out, str);
    }
    /**
     *  Process this XML Tag.
     */
    public boolean parseHtmlTag(PrintWriter out, String strTag, String strParams, String strData)
    {
        strTag = strTag.toUpperCase();
        if (strTag.equalsIgnoreCase("MENUTITLE"))
            this.printHtmlTitle(out, strTag, strParams, strData);
        else if (strTag.equalsIgnoreCase("MENUDESC"))
            this.printHtmlMenuDesc(out, strTag, strParams, strData);
        else if (strTag.equalsIgnoreCase("ITEMS"))
            this.printHtmlItems(out, strTag, strParams, strData);
        else if (strTag.equalsIgnoreCase("TYPE"))
            this.printHtmlType(out, strTag, strParams, strData);
        else if (strTag.equalsIgnoreCase("SUBMENU"))
            this.printHtmlSubmenu(out, strTag, strParams, strData);
        else if (strTag.equalsIgnoreCase("ICON"))
            this.printHtmlIcon(out, strTag, strParams, strData);
        else if (strTag.equalsIgnoreCase("LINK"))
            this.printHtmlLink(out, strTag, strParams, strData);
        else if (strTag.equalsIgnoreCase("JAVALOGO"))
            this.printHtmlJavaLogo(out, strTag, strParams, strData);
        else
            return super.parseHtmlTag(out, strTag, strParams, strData);     // Tag not found, just print the tag!
        return true;        // Tag processed
    }
    /**
     * Code to display a Menu.
     */
    public void preSetupGrid(String strMenu)
    {
        Record record = m_recDetail;
        if (record != null) if (record.getKeyArea().getKeyName().equals(DBConstants.PRIMARY_KEY))
            record.setKeyArea(record.getDefaultScreenKeyArea());
    }
    /**
     * Code to display a Menu.
     */
    public void postSetupGrid()
    {
    }
    /**
     *  Output this screen using HTML..
     */
    public void printHtmlTitle(PrintWriter out, String strTag, String strParams, String strData)
    {
        out.print(m_recDetail.getField(DBConstants.MAIN_FIELD + 1).toString());
    }
    /**
     *  Output this screen using HTML.
     */
    public void printHtmlMenuDesc(PrintWriter out, String strTag, String strParams, String strData)
    {
    }
    /**
     *  Output this screen using HTML.
     */
    public void printHtmlType(PrintWriter out, String strTag, String strParams, String strData)
    {
    }
    /**
     *  Output this screen using HTML.
     */
    public void printHtmlSubmenu(PrintWriter out, String strTag, String strParams, String strData)
    {
    }
    /**
     *  Output this screen using HTML.
     */
    public void printHtmlIcon(PrintWriter out, String strTag, String strParams, String strData)
    {
        String strIcon = "Menu";
        strIcon = HtmlConstants.HTML_ROOT + HtmlConstants.ICON_DIR + strIcon + ".gif";
        out.print(strIcon + "\n");
    }
    /**
     *  Output this screen using HTML.
     */
    public void printHtmlLink(PrintWriter out, String strTag, String strParams, String strData)
    {
        String strRecordClass = m_recDetail.getClass().getName();
        String strLink = HtmlConstants.SERVLET_LINK;
        strLink = Utility.addURLParam(strLink, DBParams.RECORD, strRecordClass);
        strLink = Utility.addURLParam(strLink, DBParams.COMMAND, ThinMenuConstants.FORM);
        if ((m_recDetail.getEditMode() == Constants.EDIT_IN_PROGRESS) || (m_recDetail.getEditMode() == Constants.EDIT_CURRENT))
        {
            try   {
                String strBookmark = m_recDetail.getHandle(DBConstants.OBJECT_ID_HANDLE).toString();
                strLink = Utility.addURLParam(strLink, DBConstants.STRING_OBJECT_ID_HANDLE, strBookmark);
            } catch (DBException ex)    {
                ex.printStackTrace();
            }
        }

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
        strParam = this.getRecordOwner().getProperty(DBConstants.SUB_SYSTEM_LN_SUFFIX);
        if (strParam != null)
            strLink = Utility.addURLParam(strLink, DBConstants.SUB_SYSTEM_LN_SUFFIX, strParam);
        
        out.print(strLink);
    }
    /**
     *  Output this screen using HTML.
     */
    public void printHtmlJavaLogo(PrintWriter out, String strTag, String strParams, String strData)
    {
    }
    /**
     *  Detail items.
     */
    public void printHtmlItems(PrintWriter out, String strTag, String strParams, String strData)
    {
        int iTableColumns = 1;
        String strColumns = this.getProperty(DBParams.COLUMNS, strParams);
        if ((strColumns != null) && (strColumns.length() > 0))
            iTableColumns = Integer.parseInt(strColumns);

        String strCellFormat = strData;
        if ((strCellFormat == null) || (strCellFormat.length() == 0))
            strCellFormat = "<td><a href=\"<link/>\"><img src=\"" +
                "<icon/>\" width=24 height=24 alt=\"Run this program\"></a></td>" +
                "<td><a href=\"<link/>&help=\"><img src=\"" + HtmlConstants.HELP_ICON +
                "\" width=24 height=24></a></td><td><a href=\"<link/>&help=\">" +
                HtmlConstants.TITLE_TAG + "</a></td><td>" + HtmlConstants.MENU_DESC_TAG + "</td>";
        this.preSetupGrid(null);
        
        boolean bFirstRow = true;
        try
        {
            int iRowCount = 0;
            m_recDetail.close();
            out.println("<table border=\"0\" cellspacing=\"10\" width=\"100%\">");
            while (m_recDetail.hasNext())
            {
                m_recDetail.next();
                if (iRowCount == 0)
                    out.println("<tr>");
        
                String strHTML = strCellFormat;
                int iIndex;
                if (bFirstRow)
                {   // Special case - First row
                    iIndex = strHTML.indexOf("<HR");
                    if (iIndex == -1)
                        iIndex = strHTML.indexOf("<hr");
                    if (iIndex != -1) if ((iIndex < strHTML.indexOf("<A")) || (iIndex < strHTML.indexOf("<a")))
                    { // Before the first reference (Get rid of the horizontal break)
                        strHTML = strHTML.substring(0, iIndex) + strHTML.substring(strHTML.indexOf('>', iIndex) + 1);
                    }
                }
                this.parseHtmlData(out, strHTML);
            
                iRowCount++;
                if ((iRowCount == iTableColumns) || (!m_recDetail.hasNext()))
                {
                    iRowCount = 0;
                    bFirstRow = false;
                    out.println("</tr>");
                }
            }
        } catch (DBException ex)    {
            ex.printStackTrace();
        }
        if (bFirstRow)
        { // No items in list
            out.println("<tr><td>Empty Menu</td></tr>");
        }
        
        out.println("</table>");
        this.postSetupGrid();
    }
}
