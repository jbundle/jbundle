package org.jbundle.base.services;

import java.io.PrintWriter;
import java.util.ResourceBundle;

import org.jbundle.base.db.RecordOwner;
import org.jbundle.base.field.BaseField;


public interface ClassInfoService {
    public ClassInfoService readClassInfo(RecordOwner recordOwner, String className);
    public String getClassName();
    public String getFullClassName();
    public void free();
    public String getClassExplain();
    public String getClassHelp();
    public String getSeeAlso();
    public String getClassDesc();
    public String getTechnicalInfo();
    public BaseField getField(String fieldName);
    public String getClassType();
    public String getLink();
    public boolean isValidRecord();
    public void printHtmlTechInfo(PrintWriter out, String strTag, String strParams, String strData);
    public void printScreen(PrintWriter out, ResourceBundle reg);
}
