/**
 * @(#)SetupProjectProcess.
 * Copyright © 2013 jbundle.org. All rights reserved.
 * GPL3 Open Source Software License.
 */
package org.jbundle.app.program.script.process;

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
import org.jbundle.base.thread.*;
import org.jbundle.app.program.db.*;
import java.net.*;
import java.io.*;
import java.util.jar.*;
import java.util.zip.*;
import org.jbundle.model.app.program.db.ClassProjectModel.*;
import org.jibx.runtime.*;
import org.jibx.schema.org.apache.maven.maven_v4_0_0.*;

/**
 *  SetupProjectProcess - .
 */
public class SetupProjectProcess extends BaseProcess
{
    public final static String STRING_ENCODING = "UTF8";
    public final static String URL_ENCODING = "UTF-8";
    public final static String BINDING_NAME = "binding";
    public final static String START = "${";
    public final static String END = "}";
    public static final String TEMPLATE_LOCATION = "org/jbundle/res/project/template";
    /**
     * Default constructor.
     */
    public SetupProjectProcess()
    {
        super();
    }
    /**
     * Constructor.
     */
    public SetupProjectProcess(RecordOwnerParent taskParent, Record recordMain, Map<String,Object> properties)
    {
        this();
        this.init(taskParent, recordMain, properties);
    }
    /**
     * Initialize class fields.
     */
    public void init(RecordOwnerParent taskParent, Record recordMain, Map<String, Object> properties)
    {
        super.init(taskParent, recordMain, properties);
    }
    /**
     * Open the main file.
     */
    public Record openMainRecord()
    {
        return new ClassProject(this);
    }
    /**
     * Open the other files.
     */
    public void openOtherRecords()
    {
        new ProgramControl(this);
        super.openOtherRecords();
    }
    /**
     * Run Method.
     */
    public void run()
    {
        String bookmark = this.getProperty(DBConstants.OBJECT_ID);
        try {
            this.getMainRecord().setHandle(bookmark, DBConstants.OBJECT_ID_HANDLE);
        } catch (DBException e) {
            e.printStackTrace();
        }
        this.populateSourceDir(TEMPLATE_LOCATION);
    }
    /**
     * PopulateSourceDir Method.
     */
    public boolean populateSourceDir(String templateDir)
    {
        URL fromDirUrl = this.getTask().getApplication().getResourceURL(templateDir, null);
        if ("jar".equalsIgnoreCase(fromDirUrl.getProtocol()))
        {   // Copy jar files
            try {
                String fileName = fromDirUrl.getFile();
                if (fileName.lastIndexOf(':') != -1)
                    fileName = fileName.substring(fileName.lastIndexOf(':') + 1);
                if (fileName.lastIndexOf('!') != -1)
                    fileName = fileName.substring(0, fileName.lastIndexOf('!'));
                InputStream inStream = new FileInputStream(fileName);
                JarInputStream file = new JarInputStream(inStream);
                ZipEntry entry = null;
                while ((entry = file.getNextEntry()) != null)
                {
                    String path = entry.getName();
                    if (path.contains("META-INF"))
                        continue;
                    if (!path.endsWith("pom.xml"))
                        continue;
                    if (path.startsWith(templateDir))
                        path = path.substring(templateDir.length());
                    int lStreamLength = (int)entry.getSize();
                        
                    String xml = this.transferStream(file, lStreamLength);
                    this.convertAndWriteXML(xml, path);
                }                
            } catch (IOException ex)    {
                ex.printStackTrace();
            }
        }
        return true;
    }
    /**
     * ConvertAndWriteXML Method.
     */
    public void convertAndWriteXML(String xml, String path)
    {
        ClassProject classProject = (ClassProject)this.getMainRecord();
        Record recProgramControl = this.getRecord(ProgramControl.PROGRAM_CONTROL_FILE);
        
        Model model = (Model)this.unmarshalMessage(xml);
        String name = model.getName();
        
        CodeType codeType = CodeType.THICK;
        String thickDir = classProject.getFileName("", null, codeType, true, false);
        if (name == null)
            codeType = CodeType.THICK;
        else if (name.endsWith("model"))
            codeType = CodeType.INTERFACE;
        else if (name.endsWith("thin"))
            codeType = CodeType.THIN;
        else if (name.endsWith("res"))
            codeType = CodeType.RESOURCE_PROPERTIES;
        xml = replaceParams(xml, codeType);
        model = (Model)this.unmarshalMessage(xml);
        String strSourcePath = recProgramControl.getField(ProgramControl.CLASS_DIRECTORY).toString();
        String destDir = classProject.getFileName("", null, codeType, true, false);
        if (codeType != CodeType.THICK)
            if (destDir.equals(thickDir))
                return; // Can't be the same as thick dir
        if (destDir.endsWith(strSourcePath))
            destDir = destDir.substring(0, destDir.length() - strSourcePath.length());
        if (name != null)
            if (name.endsWith("reactor"))
            {
                if (destDir.endsWith("/"))
                    destDir = destDir.substring(0, destDir.length() - 1);
                if (destDir.lastIndexOf('/') != -1)
                    destDir = destDir.substring(0, destDir.lastIndexOf('/'));
            }
        path = "pom.xml";
        path = org.jbundle.base.model.Utility.addToPath(destDir, path);
        File fileOut = new File(path);
        File fileDir = fileOut.getParentFile();
        if (!fileDir.exists())
            fileDir.mkdirs();
        
        //xml = this.marshalObject(model);  // Later
        Reader in = new StringReader(xml);
        
        org.jbundle.base.model.Utility.transferURLStream(null, path, in, null);
    }
    /**
     * ReplaceParams Method.
     */
    public String replaceParams(String xml, CodeType codeType)
    {
        ClassProject classProject = (ClassProject)this.getMainRecord();
        Map<String, String> ht = new HashMap<String, String>();
        ht.put(START + "project" + END, classProject.getField(ClassProject.NAME).toString());
        ht.put(START + "version" + END, "1.0.0-SNAPSHOT");
        ht.put(START + "groupId" + END, classProject.getFullPackage(CodeType.THICK, ""));
        ht.put(START + "packagedb" + END, classProject.getFullPackage(CodeType.THICK, ""));
        ht.put(START + "packagethin" + END, classProject.getFullPackage(CodeType.THIN, ""));
        ht.put(START + "packageres" + END, classProject.getFullPackage(CodeType.RESOURCE_PROPERTIES, ""));
        ht.put(START + "packagemodel" + END, classProject.getFullPackage(CodeType.INTERFACE, ""));
        ht.put(START + "package" + END, classProject.getFullPackage(codeType, ""));
        xml = org.jbundle.base.model.Utility.replace(xml, ht);
        return xml;
    }
    /**
     * TransferStream Method.
     */
    public String transferStream(InputStream in, int size)
    {
        StringBuilder sb = new StringBuilder();
        try {
            byte[] cbuf = new byte[1000];
            int iLen = Math.max(size, cbuf.length);
            while ((iLen = in.read(cbuf, 0, iLen)) > 0)
            {   // Write the entire file to the output buffer
                for (int i = 0; i < iLen; i++)
                {
                    sb.append(Character.toChars(cbuf[i]));
                }
                size = size - iLen;
                iLen = Math.max(size, cbuf.length);
            }
        } catch (MalformedURLException ex)  {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return sb.toString();
    }
    /**
     * Marshal this object to an XML string
     * @param message
     * @return.
     */
    public String marshalObject(Object message)
    {
        try {
            IBindingFactory jc = BindingDirectory.getFactory(Model.class);
            IMarshallingContext marshaller = jc.createMarshallingContext();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            marshaller.setIndent(2);
            marshaller.marshalDocument(message, URL_ENCODING, null, out);
            String xml = out.toString(STRING_ENCODING);
            return xml;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JiBXException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * Unmarshal this xml Message to an object.
     * @param xml
     * @param system
     * @return.
     */
    public Object unmarshalMessage(String xml)
    {
        try {
            IBindingFactory jc = BindingDirectory.getFactory(Model.class);
            IUnmarshallingContext unmarshaller = jc.createUnmarshallingContext();
            Reader inStream = new StringReader(xml);
            Object message = unmarshaller.unmarshalDocument( inStream, BINDING_NAME);
            return message;
        } catch (JiBXException e) {
            e.printStackTrace();
        }
        return null;
    }

}
