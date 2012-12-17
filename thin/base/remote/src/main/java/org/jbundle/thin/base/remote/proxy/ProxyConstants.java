/*
 * ProxyConstants.java
 *
 * Created on November 16, 2002, 1:45 AM

 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.thin.base.remote.proxy;

/**
 *
 * @author  don
 */
public interface ProxyConstants
{
    public static final String REMOTE_COMMAND = "remoteCommand";
    public static final String TARGET = "target";
    
    /**
     * Application commands.
     */
    public static final String CREATE_REMOTE_TASK = "createRemoteTask";
    /**
     * Task commands.
     */
    public static final String CREATE_REMOTE_RECEIVE_QUEUE = "createRemoteReceiveQueue";
    public static final String CREATE_REMOTE_SEND_QUEUE = "createRemoteSendQueue";
    public static final String FREE_REMOTE_SESSION = "freeRemoteSession";
    public static final String GET_REMOTE_DATABASE = "getRemoteDatabase";
    public static final String MAKE_REMOTE_SESSION = "makeRemoteSession";
    public static final String SET_REMOTE_MESSAGE_TASK = "setRemoteMessageTask";
    public static final String GET_REMOTE_MESSAGE_TASK = "getRemoteMessageTask";
    public static final String LOGIN = "login";
    /**
     * Database commands.
     */
    public static final String CLOSE = "close";
    public static final String COMMIT = "commit";
    public static final String ROLLBACK = "rollback";
    public static final String GET_DB_PROPERTIES = "getDBProperties";
    public static final String SET_DB_PROPERTIES = "setDBProperties";
    /**
     * Table commands.
     */
    public static final String OPEN = "open";
    public static final String ADD = "add";
    public static final String EDIT = "edit";
    public static final String SET = "set";
    public static final String REMOVE = "remove";
    public static final String DO_MOVE = "doMove";
    public static final String SEEK = "seek";
    public static final String DO_SET_HANDLE = "doSetHandle";
    public static final String GET_LAST_MODIFIED = "getLastModified";
    public static final String GET = "get";
    public static final String SET_REMOTE_PROPERTY = "setRemoteProperty";
    public static final String MAKE_FIELD_LIST = "makeFieldList";
    public static final String MAKE_REMOTE_TABLE = "makeRemoteTable";
    /**
     * Session commands.
     */
    public static final String GET_REMOTE_TABLE = "getRemoteTable";
    public static final String DO_REMOTE_ACTION = "doRemoteAction";
//    public static final String MAKE_REMOTE_SESSION = "makeRemoteSession";
    public static final String SETUP_REMOTE_SESSION_FILTER = "setupRemoteSessionFilter";
    /**
     * Queue commands.
     */
    public static final String SEND_MESSAGE = "sendMessage";
    public static final String RECEIVE_REMOTE_MESSAGE = "receiveRemoteMessage";
    public static final String ADD_REMOTE_MESSAGE_FILTER = "addRemoteMessageFilter";
    public static final String REMOVE_REMOTE_MESSAGE_FILTER = "removeRemoteMessageFilter";
    public static final String UPDATE_REMOTE_FILTER_PROPERTIES = "updateRemoteFilterProperties";

    public static final String NAME = "name";
    public static final String TYPE = "type";
    public static final String SESSION_CLASS_NAME = "sessionClassName";
    
    public static final String OPEN_MODE = "openmode";
    public static final String DIRECTION = "direction";
    public static final String FIELDS = "fields";
    public static final String POSITION = "position";
    public static final String COUNT = "count";
    public static final String SIGN = "sign";
    public static final String KEY = "key";
    public static final String BOOKMARK = "bookmark";
    public static final String INDEX = "index";
    public static final String VALUE = "value";
    public static final String SESSION = "session";
    public static final String KEY_DATA = "keydata";
    public static final String INITIAL_KEY = "initialKey";
    public static final String END_KEY = "endKey";
    public static final String BEHAVIOR_DATA = "behaviorData";
    public static final String PROPERTIES = "properties";
    public static final String PROPERTIES_DB = "propertiesDB";
    public static final String DATA = "data";
    public static final String ID = "id";
    public static final String FILTER = "filter";
    public static final String MESSAGE = "message";
    public static final String FREE = "free";
    public static final String MAP = "map";

    public static final String NULL = "null";
    public static final char PATH_SEPARATOR = '/';
    
    public static final String REMOTE_TABLE = "RemoteTable";
    public static final String REMOTE_SESSION = "RemoteSession";
    public static final String REMOTE_BASE_SESSION = "RemoteBaseSession";
    public static final char CLASS_SEPARATOR = ':';
}
