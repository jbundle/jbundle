package org.jbundle.app.program.manual.backup;

/**
 * OrderEntry.java:   Applet
 *  Copyright � 1997 jbundle.org. All rights reserved.
 *  
 *  @author Don Corley don@tourgeek.com
 *  @version 1.0.0.
 */
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import javax.swing.JApplet;
import javax.swing.Timer;

import org.jbundle.base.db.util.log.BackupConstants;
import org.jbundle.base.util.DBConstants;
import org.jbundle.thin.base.db.Constants;
import org.jbundle.thin.base.message.BaseMessage;
import org.jbundle.thin.base.message.BaseMessageFilter;
import org.jbundle.thin.base.message.BaseMessageListener;
import org.jbundle.thin.base.message.BaseMessageManager;
import org.jbundle.thin.base.message.BaseMessageReceiver;
import org.jbundle.thin.base.message.remote.RemoteMessageManager;
import org.jbundle.thin.base.screen.ThinApplication;
import org.jbundle.thin.base.util.Util;


/**
 * Main Class for BackupServerApp
 * java -Xms256m -Xmx512m org.jbundle.base.db.util.log.BackupServerApp
 *  filenametemplate=ftp://download:donwpp@www.donandann.com/backup/trxlog_{0}.txt
 *  cycletime=15    // Max minutes to start a new file (Default = use same file)
 *  timeouttime=15000   // Max timeout in ms (optional - for ftp, network connections.) (default = no timeout)
 *  (or instead of filenametemplate)
 *  folder=ftp://download:donwpp@www.donandann.com/backup/
 */
public class BackupServerApp extends ThinApplication
    implements BackupConstants, ActionListener
{
    protected ObjectOutputStream m_writer = null;
    protected String m_strFilenameTemplate = null;
    protected Timer m_cycletimer = null;
    protected Timer m_timeouttimer = null;
    protected double m_lastWrite = 0;
    protected int iCycleTime = -1;

    /**
     * The message filter.
     */
    protected BaseMessageFilter m_messageFilter = null;

    /**
     *  Chat Screen Constructor.
     */
    public BackupServerApp()
    {
        super();
    }
    /**
     *  Chat Screen Constructor.
     * @param parent Typically, you pass the BaseApplet as the parent.
     * @param @record and the record or GridTableModel as the parent.
     */
    public BackupServerApp(String[] args)
    {
        this();
        Map<String,Object> properties = null;
        if (args != null)
        {
            properties = new Hashtable<String,Object>();
            Util.parseArgs(properties, args);
        }
        this.init(null, properties, null);
    }
    /**
     *  Chat Screen Constructor.
     * @param parent Typically, you pass the BaseApplet as the parent.
     * @param @record and the record or GridTableModel as the parent.
     */
    public void init(Object env, Map<String,Object> properties, JApplet applet)
    {
        super.init(env, properties, applet);
        if (this.registerUniqueApplication(BACKUP_QUEUE_NAME, BACKUP_QUEUE_TYPE) != Constants.NORMAL_RETURN)
        {
            this.free();    // Don't start this application (It's already running somewhere)
            return;
        }
        BaseMessageManager messageManager = RemoteMessageManager.getMessageManager(this);
        BaseMessageReceiver receiver = (BaseMessageReceiver)messageManager.getMessageQueue(BACKUP_QUEUE_NAME, BACKUP_QUEUE_TYPE).getMessageReceiver();
        
        m_messageFilter = new BaseMessageFilter(BACKUP_QUEUE_NAME, BACKUP_QUEUE_TYPE, this, null);
        new BaseMessageListener(m_messageFilter)    // Listener added to filter.
        {
            public int handleMessage(BaseMessage message)
            {
                Object objMessage = message.get(MESSAGE_PARAM);
                backupTrxLog(objMessage);
                return Constants.NORMAL_RETURN;
            }
        };
        receiver.addMessageFilter(m_messageFilter);
    }
    /**
     * 
     */
    public void free()
    {
        if (m_cycletimer != null)
            m_cycletimer.stop();
        m_cycletimer = null;
        this.startNextFile();
        super.free();
    }
    /**
     * Add this data to the backup stream.
     * @param strMessage the data to log.
     */
    public synchronized void backupTrxLog(Object objMessage)
    {
        try {
            this.getWriter().writeObject(objMessage);
            m_lastWrite = System.currentTimeMillis();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 
     */
    public synchronized void startNextFile()
    {
        if (m_writer == null)
            return; // No data written in this interval
        try {
            this.getWriter().close();
            m_writer = null;
            if (m_timeouttimer != null)
                m_timeouttimer.stop();  // No need to worry out timeouts when the file is closed
            m_lastWrite = System.currentTimeMillis();   // Being paranoid
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Get the backup stream writer.
     * @return
     */
    public ObjectOutputStream getWriter()
    {
        if (m_writer == null)
        {
            try {
                String filename = this.getFilename();
                OutputStream os = null;
                if (filename.indexOf(':') == -1)
                    os = new FileOutputStream(filename);
                else
                {
                    URL url = new URL(filename);    // "ftp://user01:pass1234@ftp.foo.com/README.txt;type=i"
                    URLConnection urlc = url.openConnection();
                    os = urlc.getOutputStream(); // To upload                    
                }
                m_writer = new ObjectOutputStream(os);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return m_writer;
    }
    /**
     * 
     * @return
     */
    public String getFilename()
    {
        if (m_strFilenameTemplate == null)
            m_strFilenameTemplate = this.getProperty("filenametemplate");   // ie.,ftp://user01:pass1234@ftp.foo.com/README.txt;type=i
        if (m_strFilenameTemplate == null)
            m_strFilenameTemplate = DEFAULT_PATTERN;
        if (this.getProperty("folder") != null)
        {
            String strFolder = this.getProperty("folder");
            if (!strFolder.endsWith("/"))
                strFolder += "/";
            m_strFilenameTemplate = strFolder + m_strFilenameTemplate;
        }
        String filename = this.getBackupFilename(m_strFilenameTemplate);
        if (!filename.equalsIgnoreCase(m_strFilenameTemplate))
        {   // Usually
            if (this.getProperty("cycletime") != null)
            {
                int iCycleTime = Integer.parseInt(this.getProperty("cycletime"));
                if (iCycleTime < 10000)     // Must be greater than a 10 secs for the date change to work
                    iCycleTime = iCycleTime * 1000 * 60;    // Minutes -> ms
                this.startCycleTimer(iCycleTime);
            }
            if (this.getProperty("timeouttime") != null)
            {
                iCycleTime = Integer.parseInt(this.getProperty("timeouttime"));
                m_lastWrite = System.currentTimeMillis();
                this.startTimeoutTimer(iCycleTime); // This is guaranteed to be stopped in startNextFile()
            }
        }
        return filename;
    }
    /**
     *  The main() method acts as the applet's entry point when it is run
     *  as a standalone application. It is ignored if the applet is run from
     *  within an HTML page.
     */
    public static void main(String args[])
    {
        new BackupServerApp(args);
    }
    /*
     * Add the files to the backup file
     */
    public String getBackupFilename(String strFilenameTemplate)
    {
        Date now = new Date();
        String strDate = this.dateToString(now);
        for (int i = 0; i < strDate.length(); i++)
        {
            char ch = strDate.charAt(i);
            if (!Character.isLetterOrDigit(ch))
                strDate = strDate.substring(0, i) + '_' + strDate.substring(i + 1, strDate.length());
        }
        return MessageFormat.format(strFilenameTemplate, strDate);
    }
    /*
     * Convert this date object to a string.
     * @param The date or null if unparseable.
     * @return strDate the date to parse.
     */
    public String dateToString(Date date)
    {
        if (date == null)
            return "";
        try {
            return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG).format(date);
        } catch (Exception ex) {
        }
        return "";
    }
    /**
     * Wait, then close the file so a new file can open.
     */
    public void startCycleTimer(int iDelayMS)
    {
        if (m_cycletimer == null)
        {
            m_cycletimer = new Timer(iDelayMS, this);
            m_cycletimer.setRepeats(false);
            m_cycletimer.start();
        }
        else
            m_cycletimer.restart();
    }
    /**
     * Wait, then close the file so a new file can open.
     */
    public void startTimeoutTimer(int iDelayMS)
    {
        if (m_timeouttimer == null)
        {
            m_timeouttimer = new Timer(iDelayMS, this);
            m_timeouttimer.setRepeats(false);
            m_timeouttimer.start();
        }
        else
            m_timeouttimer.restart();
    }
    /**
     * Timer handling.
     */
    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == m_cycletimer)
            this.startNextFile();
        else if (e.getSource() == m_timeouttimer)
            this.checkTimeout();
    }
    /**
     * 
     */
    public void checkTimeout()
    {
        int iTimeSinceLastActivity = (int)(System.currentTimeMillis() - m_lastWrite);
        if (iCycleTime != -1)
        {   // Always
            if (iTimeSinceLastActivity < iCycleTime)
            {   // There has been activity since my last flush, flush again to keep from timing out.
                if (m_writer != null)
                    this.flush(true);   // Sending a fake object guarantees activity.
                m_timeouttimer.restart();
            }
            else
            {   // No activity since last timeout - Flush and close this stream so I can start a new transfer
                this.startNextFile();
            }
        }
    }
    /**
     * Flush the buffer.
     * @param bSendFakeTrx Send a fake trx before flushing (This guarantees activity on the stream before the flush [In case it just autoflushed the entire buffer])
     */
    public synchronized void flush(boolean bSendFakeTrx)
    {
        try {
            if (bSendFakeTrx)
                this.getWriter().writeObject(FAKE_TRX);
            this.getWriter().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static Vector<Object> FAKE_TRX = new Vector<Object>();
    static {
        Object[] FAKE_DATA = {NOTRX, DBConstants.BLANK, "0"};
        for (int i = 0; i < FAKE_DATA.length; i++)
        {
            FAKE_TRX.add(FAKE_DATA[i]);
        }
    }
}
