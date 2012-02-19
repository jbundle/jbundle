/*
 *  @(#)CustSaleDetailParser.
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.base.screen.model.report.parser;

import java.io.PrintWriter;

import org.jbundle.base.db.Record;
import org.jbundle.base.model.RecordOwner;
import org.jbundle.base.screen.model.BasePanel;
import org.jbundle.model.app.program.db.ClassInfoModel;


/**
 *  CustSaleDetailParser - Parse the Booking detail records.
 */
public class HelpParser extends XMLParser
{
    /**
     *
     */
    protected ClassInfoModel m_recDetail = null;
    /**
     *  Default constructor.
     */
    public HelpParser()
    {
        super();
    }
    /**
     *  Constructor.
     */
    public HelpParser(RecordOwner screen, ClassInfoModel recDetail)
    {
        this();
        this.init(screen, recDetail);
    }
    /**
     *  Initialize class fields.
     */
    public void init(RecordOwner screen, ClassInfoModel recDetail)
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
        else if (strTag.equalsIgnoreCase("HELP"))
            this.printHtmlHelp(out, strTag, strParams, strData);
        else if (strTag.equalsIgnoreCase("SEEALSO"))
            this.printHtmlSeeAlso(out, strTag, strParams, strData);
        else if (strTag.equalsIgnoreCase("TECHNICAL"))
            this.printHtmlTechnical(out, strTag, strParams, strData);
        else if (strTag.equalsIgnoreCase("FIELD"))
            this.printHtmlField(out, strTag, strParams, strData);
        else if (strTag.equalsIgnoreCase("PACKAGEDIR"))
            this.printHtmlPackageDir(out, strTag, strParams, strData);
        else if (strTag.equalsIgnoreCase("TECHINFO"))
            this.printHtmlTechInfo(out, strTag, strParams, strData);
        else if (strTag.equalsIgnoreCase("CLASSINFO"))
            this.printHtmlClassInfo(out, strTag, strParams, strData);
        else if (strTag.equalsIgnoreCase("OPTIONAL"))
            this.printHtmlOptional(out, strTag, strParams, strData);
        else if (strTag.equalsIgnoreCase("LINK"))
            this.printHtmlLink(out, strTag, strParams, strData);
        else
            return super.parseHtmlTag(out, strTag, strParams, strData);     // Tag not found, just print the tag!
        return true;        // Tag processed
    }
    /**
     *  Output this screen using HTML..
     */
    public void printHtmlTitle(PrintWriter out, String strTag, String strParams, String strData)
    {
        out.print(m_recDetail.getClassDesc());
    }
    /**
     *  Output this screen using HTML..
     */
    public void printHtmlMenuDesc(PrintWriter out, String strTag, String strParams, String strData)
    {
        out.print(m_recDetail.getClassExplain());
    }
    /**
     *  Output this screen using HTML..
     */
    public void printHtmlHelp(PrintWriter out, String strTag, String strParams, String strData)
    {
        this.parseHtmlData(out, m_recDetail.getClassHelp());
    }
    /**
     *  Output this screen using HTML.
     */
    public void printHtmlSeeAlso(PrintWriter out, String strTag, String strParams, String strData)
    {
        String strSeeAlso = m_recDetail.getSeeAlso();
        if ((strSeeAlso != null) && (strSeeAlso.length() > 0) && (strSeeAlso.indexOf('<') == -1))
        { // List of classes to reference
            int iStartClass = 0;
            while (true)
            {
                int iEndClass = strSeeAlso.indexOf(',', iStartClass);
                if (iEndClass == -1)
                    iEndClass = strSeeAlso.length();
                String strClass = strSeeAlso.substring(iStartClass, iEndClass);

                this.parseHtmlClassInfo(out, strTag, strParams, strData, strClass);

                iStartClass = iEndClass + 1;
                if (iStartClass >= strSeeAlso.length())
                    break;
                if (strSeeAlso.charAt(iStartClass) == ' ')
                    iStartClass++;
            }
        }
        else
            this.parseHtmlData(out, strSeeAlso);
    }
    /**
     *  Output this screen using HTML.
     */
    public void printHtmlTechnical(PrintWriter out, String strTag, String strParams, String strData)
    {
        this.parseHtmlData(out, m_recDetail.getTechnicalInfo());
    }
    /**
     *  Print the package as a directory (ie., app.test -> app/test).
     */
    public void printHtmlPackageDir(PrintWriter out, String strTag, String strParams, String strData)
    {
        if (m_recDetail.getField("ClassPackage") != null)
        {
            String string = m_recDetail.getField("ClassPackage").toString();
            string = string.replace('.', '/');
            out.print(string);
        }
    }
    /**
     *  Output this screen using HTML.
     */
    public void printHtmlField(PrintWriter out, String strTag, String strParams, String strData)
    {
        String strField = ((BasePanel)this.getRecordOwner()).getProperty("name");
        if ((strField != null) && (strField.length() > 0) && (m_recDetail.getField(strField) != null))
        {
            String string = m_recDetail.getField(strField).toString();
            out.print(string);
        }
    }
    /**
     *  Output this screen using HTML.
     */
    public void printHtmlTechInfo(PrintWriter out, String strTag, String strParams, String strData)
    {
        if (m_recDetail.getClassType().equalsIgnoreCase("Record"))
        {
        	m_recDetail.printHtmlTechInfo(out, strTag, strParams, strData);
        }
    }
    /**
     *  Output this screen using HTML.
     */
    public void printHtmlLink(PrintWriter out, String strTag, String strParams, String strData)
    {
        String strLink = m_recDetail.getLink();
        out.print(strLink);
    }
    /**
     *  Output this screen using HTML..
     */
    public void printHtmlClassInfo(PrintWriter out, String strTag, String strParams, String strData)
    {
        String strClass = ((BasePanel)this.getRecordOwner()).getProperty("name");
        if (strClass != null) if (strClass.length() > 0)
            if (strData != null) if (strData.length() > 0)
        {
            this.parseHtmlClassInfo(out, strTag, strParams, strData, strClass);
        }
    }
    /**
     *  Output this screen using HTML.
     */
    public void printHtmlOptional(PrintWriter out, String strTag, String strParams, String strData)
    {
        String strField = ((BasePanel)this.getRecordOwner()).getProperty("field");
        if ((strField != null) && (strField.length() > 0) && (m_recDetail.getField(strField) != null))
        {
            String string = m_recDetail.getField(strField).toString();
            if (strField.equalsIgnoreCase("TechnicalInfo"))
            {
                string = this.getRecordOwner().getProperty("tech");   // Special case - For Technical Info, Only output if &tech is found
                if (string != null)
                    string = "yes";
            }
            if ((string != null) && (string.length() > 0))
                this.parseHtmlData(out, strData);   // If the data is not blank, then process the HTML
            // Otherwise, don't output the HTML
        }
    }
    /**
     *  Output this screen using HTML.
     */
    public void parseHtmlClassInfo(PrintWriter out, String strTag, String strParams, String strData, String className)
    {
        ClassInfoModel classInfo = (ClassInfoModel)Record.makeRecordFromClassName(ClassInfoModel.THICK_CLASS, this.getRecordOwner());
        if (classInfo != null)
            classInfo = classInfo.readClassInfo(this.getRecordOwner(), className);
        if (classInfo != null)
        {
        	if (classInfo.isValidRecord())
            {
                XMLParser parser = new HelpParser(this.getRecordOwner(), classInfo);
                parser.parseHtmlData(out, strData);
                parser.free();
                parser = null;
            }
            classInfo.free();
            classInfo = null;
        }
    }
}
