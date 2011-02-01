/**
 * Top level methods and vars.
 */
if(!dojo._hasResource["tourapp.classes"]){
dojo._hasResource["tourapp.classes"] = true;
dojo.provide("tourapp.classes");

/**
 * Classes.
 */
tourapp.classes = {
	// Top-level task(s)
	Session: function(parentSession) {
		if (parentSession)
		{
			this.parentSession = parentSession;
			parentSession.addChildSession(this);
		}
		this.childSessions = new Array();
		this.localSessionID = tourapp.classes.nextLocalSessionID;
		tourapp.classes.nextLocalSessionID++;
	},
	// Send queue(s)
	SendQueue: function(parentSession, queueName, queueType) {
		this.parentSession = parentSession;
		this.queueName = queueName;
		this.queueType = queueType;
		parentSession.addChildSession(this);
		this.localSessionID = tourapp.classes.nextLocalSessionID;
		tourapp.classes.nextLocalSessionID++;
	},
	// Receive queue(s)
	ReceiveQueue: function(parentSession, queueName, queueType) {
		this.parentSession = parentSession;
		this.queueName = queueName;
		this.queueType = queueType;
		parentSession.addChildSession(this);
		this.remoteFilters = new Object();
		this.localSessionID = tourapp.classes.nextLocalSessionID;
		tourapp.classes.nextLocalSessionID++;
	},
	// Filters in this receive queue.
	MessageFilter: function(parentSession, methodToCall) {
		this.parentSession = parentSession;
		this.methodToCall = methodToCall;
		this.filterID = tourapp.classes.nextFilterID.toString();
		parentSession.addMessageFilter(this, tourapp.classes.nextFilterID.toString());
		tourapp.classes.nextFilterID++;
	},
	// Next unique local message filter ID
	nextFilterID: 1,
	// Next unique local session ID
	nextLocalSessionID: 1,
	// Utility function to get the full session ID (don't call this directly, it is a session function)
	getFullSessionID: function() {
		var sessionID = this.sessionID;
		if (sessionID)
			if (this.parentSession)
				sessionID = this.parentSession.getFullSessionID() + "/" + sessionID;
		return sessionID;
	},
	// Utility function to add a filter to this session
	addMessageFilter: function(messageFilter, filterID) {
		if (!this.remoteFilters)
			this.remoteFilters = new Object();
		this.remoteFilters[filterID] = messageFilter;
	},
	// Utility function to get the filter in this session
	getMessageFilter: function(filterID) {
		if (this.remoteFilters)
			return this.remoteFilters[filterID];
	}
};
tourapp.classes.Session.prototype.getFullSessionID = tourapp.classes.getFullSessionID;
tourapp.classes.Session.prototype.addMessageFilter = tourapp.classes.addMessageFilter;
tourapp.classes.Session.prototype.getMessageFilter = tourapp.classes.getMessageFilter;
tourapp.classes.Session.prototype.addChildSession = function(session) {
	tourapp.getTaskSession().childSessions.push(session);
};
// Get the remote send queue with this name and type
tourapp.classes.Session.prototype.getSendQueue = function(queueName, queueType)
{
	if (queueName === undefined)
		queueName = tourapp.TRX_SEND_QUEUE;
	if (queueType === undefined)
		queueType = tourapp.DEFAULT_QUEUE_TYPE;
	var childSessions = tourapp.getTaskSession().childSessions;
	if (childSessions)
	{
		for (var i = 0; i < childSessions.length; i++)
		{
			if (childSessions[i] instanceof tourapp.classes.SendQueue)
				if (childSessions[i].queueName == queueName)
					if (childSessions[i].queueType == queueType)
						return childSessions[i];
		}
	}
};
// Get the remote receive queue with this name and type
tourapp.classes.Session.prototype.getReceiveQueue = function(queueName, queueType)
{
	if (queueName === undefined)
		queueName = tourapp.TRX_RECEIVE_QUEUE;
	if (queueType === undefined)
		queueType = tourapp.DEFAULT_QUEUE_TYPE;
	var childSessions = tourapp.getTaskSession().childSessions;
	if (childSessions)
	{
		for (var i = 0; i < childSessions.length; i++)
		{
			if (childSessions[i] instanceof tourapp.classes.ReceiveQueue)
				if (childSessions[i].queueName == queueName)
					if (childSessions[i].queueType == queueType)
						return childSessions[i];
		}
	}
};
// Lookup session by session ID
tourapp.classes.Session.prototype.getSessionByFullSessionID = function(fullSessionID) {
	var sessionID = fullSessionID;
	if (fullSessionID.indexOf("/") > 0)
	{	// Next session
		sessionID = fullSessionID.substr(0, fullSessionID.indexOf("/"));
		fullSessionID = fullSessionID.substr(fullSessionID.indexOf("/") + 1);
	}
	else
	{
		sessionID = fullSessionID;
		fullSessionID = null;	// Last time
	}
	if (this.sessionID == sessionID)
	{
		if (fullSessionID == null)
			return this;		// Found
		if (!this.childSessions)
			return;	// No more children, Not found
		for (var i = 0; i < this.childSessions.length; i++)
		{
			if (this.childSessions[i] instanceof tourapp.classes.Session)
			{
				var session = this.childSessions[i].getSessionByFullSessionID(fullSessionID);
				if (session)
					return session;
			}
			else
			{	// Send and receive queues are special sessions that can't have children
				if (this.childSessions[i].sessionID == fullSessionID)
					return this.childSessions[i];
			}
		}
	}
	// Not found
}
// Lookup session by session ID
tourapp.classes.Session.prototype.getSessionByLocalSessionID = function(localSessionID) {
	if (!this.childSessions)
		return;	// No children, Not found
	for (var i = 0; i < this.childSessions.length; i++)
	{
		if (this.childSessions[i].localSessionID == localSessionID)
			return this.childSessions[i];
		else if (this.childSessions[i] instanceof tourapp.classes.Session)
		{	// Continue looking down the chain
			var session = this.childSessions[i].getSessionByLocalSessionID(localSessionID);
			if (session)
				return session;
		}
	}
	// Not found
}
tourapp.classes.SendQueue.prototype.getFullSessionID = tourapp.classes.getFullSessionID;
tourapp.classes.ReceiveQueue.prototype.addMessageFilter = tourapp.classes.addMessageFilter;
tourapp.classes.ReceiveQueue.prototype.getMessageFilter = tourapp.classes.getMessageFilter;
tourapp.classes.ReceiveQueue.prototype.getMessageFilterByRemoteID = function(remoteFilterID) {
	for (var key in this.remoteFilters) {
    	if (this.remoteFilters[key].remoteFilterID == remoteFilterID)
    		return this.remoteFilters[key];
	}
};
tourapp.classes.ReceiveQueue.prototype.getFullSessionID = tourapp.classes.getFullSessionID;
}
