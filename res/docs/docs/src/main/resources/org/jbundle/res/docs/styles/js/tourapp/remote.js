/**
 * Top level methods and vars.
 */
if(!dojo._hasResource["tourapp.remote"]){
dojo._hasResource["tourapp.remote"] = true;
dojo.provide("tourapp.remote");

/**
 * Remote access utilities.
 */
tourapp.remote = {
	/**
	 * Send this command to the web server and bind the return to this function.
	 */
	sendToAjax: function(remoteCommand, args, bindFunction, url, mimetype, bindArgs) {
		if (!args)
			args = new Object();
		if (remoteCommand)
			args.remoteCommand = remoteCommand;
		if (!url)
			url = tourapp.getServerPath();
		if (!mimetype)
			mimetype = "text/html";
		
		var timeout = 30 * 1000;	// 30 seconds max
		if (remoteCommand == "receiveRemoteMessage")
			timeout = 180 * 1000;	// 3 minutes

		if (!bindArgs)
			bindArgs = {};
		bindArgs.url = url;
		bindArgs.content = args;
		bindArgs.mimetype = mimetype;
		bindArgs.load = bindFunction;
		bindArgs.error = tourapp.remote.transportError;
		bindArgs.timeout = timeout;
//			timeout: tourapp.remote.timeoutError,	// Now handled by error.
		
		dojo.xhrPost(bindArgs);
	  	if (djConfig.isDebug)
		  	console.log("Called " + remoteCommand);
	},
	/**
	 * Transport error.
	 */
	transportError: function(data, ioArgs) {
		var displayError = tourapp.gEnvState;
		if (ioArgs)
			if (ioArgs.args)
			if (ioArgs.args.content)
				if (ioArgs.args.content.remoteCommand == "receiveRemoteMessage")
				{
					displayError = false;
					if (data.dojoType == "timeout")	{
						ioArgs.xhr.abort();
						tourapp.remote.receiveRemoteMessage(tourapp.getTaskSession().getSessionByFullSessionID(ioArgs.args.content.target));	// Wait for the next message.
					}
				}
		if (displayError == true)	// Ignore the error if the user moves away from this window
			tourapp.util.displayErrorMessage("Transport error: " + data + "\nArgs: " + ioArgs.toSource());
	},
	// ------- ApplicationServer --------
	/**
	 * Create the remote task.
	 */
	createRemoteTask: function(user, password) {
		var args = {};
		var props = {
  			user: user,
			password: password
		};
		args.properties = dojo.toJson(props);

		tourapp.remote.sendToAjax("createRemoteTask", args, tourapp.remote.handleCreateRemoteTask);
	},
	/**
	 *
	 */
	handleCreateRemoteTask: function(data, ioArgs) {
		if (tourapp.remote.checkForDataError(data, "Could not create remote task"))
			return;
	  	if (djConfig.isDebug)
		  	console.log("handleCreateRemoteTask session " + data);
		tourapp.getTaskSession().sessionID = data;
	
		// If there are any queues for this new task, add them to the remote queue now
		var childSessions = tourapp.getTaskSession().childSessions;
		if (childSessions)
		{
			for (var i = 0; i < childSessions.length; i++)
			{
				if (childSessions[i] instanceof tourapp.classes.SendQueue)
					tourapp.remote.createRemoteSendQueue(childSessions[i]);
				if (childSessions[i] instanceof tourapp.classes.ReceiveQueue)
					tourapp.remote.createRemoteReceiveQueue(childSessions[i]);
				if (childSessions[i] instanceof tourapp.classes.Session)
					tourapp.remote.makeRemoteSession(childSessions[i]);
			}
		}
	},
	// ------- RemoteBaseSession --------
    /**
     * Release the session and its resources.
     */
	freeRemoteSession: function(session)
	{
		var args = {
			target: session.getFullSessionID()
		};

		tourapp.remote.sendToAjax("freeRemoteSession", args, tourapp.remote.handleFreeRemoteSession);
	},
	/**
	 *
	 */
	handleFreeRemoteSession: function(data, ioArgs) {
//x		if (tourapp.remote.checkForDataError(data, "Could not free remote session"))
//x			return;
		// TODO (don) Free/remove the session and set gTaskSession to null if IT was freed
	  	if (djConfig.isDebug)
		  	console.log("freeRemoteSession session " + data);
	},
    /**
     * Build a new remote session and initialize it.
     * @param parentSessionObject The parent session for this new session (if null, parent = me).
     * @param strSessionClassName The class name of the remote session to build.
     */
	makeRemoteSession: function(session)
	{
		var args = {
			name: session.sessionClassName,
			target: session.parentSession.getFullSessionID(),
			localSessionID: session.localSessionID
		};

		tourapp.remote.sendToAjax("makeRemoteSession", args, tourapp.remote.handleMakeRemoteSession);
	},
	/**
	 *
	 */
	handleMakeRemoteSession: function(data, ioArgs)
	{
		if (tourapp.remote.checkForDataError(data, "Could not create remote session"))
			return;
		var session = tourapp.getTaskSession().getSessionByLocalSessionID(ioArgs.args.content.localSessionID);
	
	  	if (djConfig.isDebug)
		  	console.log("makeRemoteSession session " + data);
		session.sessionID = data;
		if (data.indexOf(":") > 0)
		{	// Always
			session.sessionID = data.substr(data.indexOf(":") + 1);
			session.sessionType = data.substr(0, data.indexOf(":"));
		}		
		
		// If there are any commands for this new session, add them to the remote queue now
		if (session.remoteFilters)
		{
			for (var key in session.remoteFilters) {
		    	tourapp.remote.doRemoteAction(session.remoteFilters[key]);
			}
		}
	},
	/**
	 * Send this message.
	 */
	doRemoteAction: function(messageFilter)
	{
		var args = {
			target: messageFilter.parentSession.getFullSessionID(),
			filter: messageFilter.filterID,
			name: messageFilter.name,
			properties: messageFilter.properties
		};
		if (messageFilter.properties)
			if (messageFilter.properties instanceof Object)
				args.properties = dojo.toJson(messageFilter.properties);

		tourapp.remote.sendToAjax("doRemoteAction", args, tourapp.remote.handleDoRemoteAction, null, null, messageFilter.bindArgs);
	},
	/**
	 *
	 */
	handleDoRemoteAction: function(data, ioArgs)
	{
		if (tourapp.remote.checkForDataError(data, "Could not do remote action", true))
			return;
		if (djConfig.isDebug)
			console.log("handleDoRemoteAction, data received: " + data);
		var messageFilter = tourapp.getTaskSession().getSessionByFullSessionID(ioArgs.args.content.target).getMessageFilter(ioArgs.args.content.filter);
		try {
//?			if ((data) && (data.length > 0) && (data.charAt(0) == '(') && (data.charAt(data.length - 1) == ')'))
//?				data = eval(data);
			messageFilter.methodToCall(data, ioArgs);
		} catch (e) {
	  		tourapp.util.displayErrorMessage("Error: " + e.message);
		}
	},
	// ------- RemoteTask --------
	/**
	 * Create the send queue.
	 */
	createRemoteSendQueue: function(session)
	{
		var args = {
			queueName: session.queueName,
			queueType: session.queueType,
			target: session.parentSession.getFullSessionID()
		};

		tourapp.remote.sendToAjax("createRemoteSendQueue", args, tourapp.remote.handleCreateRemoteSendQueue);
	},
	/**
	 *
	 */
	handleCreateRemoteSendQueue: function(data, ioArgs)
	{
		if (tourapp.remote.checkForDataError(data, "Could not create remote send queue"))
			return;
	  	if (djConfig.isDebug)
		  	console.log("createRemoteSendQueue session " + data);
		tourapp.getTaskSession().getSendQueue(ioArgs.args.content.queueName, ioArgs.args.content.queueType).sessionID = data;
	},
	/**
	 * Create the receive queue.
	 */
	createRemoteReceiveQueue: function(session)
	{
		var args = {
			queueName: session.queueName,
			queueType: session.queueType,
			target: session.parentSession.getFullSessionID()
		};

		tourapp.remote.sendToAjax("createRemoteReceiveQueue", args, tourapp.remote.handleCreateRemoteReceiveQueue);
	},
	/**
	 *
	 */
	handleCreateRemoteReceiveQueue: function(data, ioArgs)
	{
		if (tourapp.remote.checkForDataError(data, "Could not create remote receive queue"))
			return;
		var receiveQueue = tourapp.getTaskSession().getReceiveQueue(ioArgs.args.content.queueName, ioArgs.args.content.queueType);
	
	  	if (djConfig.isDebug)
		  	console.log("createRemoteReceiveQueue session " + data);
		receiveQueue.sessionID = data;
		
		// If there are any filters for this new receive queue, add them to the remote queue now
		for (var key in receiveQueue.remoteFilters) {
	    	tourapp.remote.addRemoteMessageFilter(receiveQueue.remoteFilters[key]);
		}

		tourapp.remote.receiveRemoteMessage(tourapp.getTaskSession().getReceiveQueue(ioArgs.args.content.queueName, ioArgs.args.content.queueType));	// Wait for the next message.
	},
	/**
	 * Login.
	 */
	login: function(session, user, password) {
		if (!user)
			user = "";
		if (!password)
			password = "";
		var args = {
			target: session.getFullSessionID(),
  			user: user,
			password: password
		};

		tourapp.remote.sendToAjax("login", args, tourapp.remote.handleLogin);
	},
	/**
	 *
	 */
	handleLogin: function(data, ioArgs)
	{
		if (tourapp.remote.checkForDataError(data, "Could not log in"))
			return;
		data = eval(data);
	  	if (djConfig.isDebug)
		  	console.log("Login ok ");
		tourapp.getTaskSession().security = data;
	},
	/**
	 * Add a remote message filter.
	 */
	addRemoteMessageFilter: function(messageFilter)
	{
		var args = {
			target: messageFilter.parentSession.getFullSessionID(),
			filter: messageFilter.filterID
		};

		tourapp.remote.sendToAjax("addRemoteMessageFilter", args, tourapp.remote.handleAddRemoteMessageFilter);
	},
	/**
	 *
	 */
	handleAddRemoteMessageFilter: function(data, ioArgs)
	{
		if (tourapp.remote.checkForDataError(data, "Could not add remote message filter"))
			return;
	  	if (djConfig.isDebug)
		  	console.log("handleAddRemoteMessageFilter to filter " + data);
		var messageFilter = tourapp.getTaskSession().getSessionByFullSessionID(ioArgs.args.content.target).getMessageFilter(ioArgs.args.content.filter);
		messageFilter.remoteFilterID = data;
	},
	/**
	 * Receive a message.
	 */
	receiveRemoteMessage: function(receiveQueue)
	{
		var args = {
			target: receiveQueue.getFullSessionID()
		};
	
		tourapp.remote.sendToAjax("receiveRemoteMessage", args, tourapp.remote.handleReceiveMessage);
	},
	/**
	 *
	 */
	handleReceiveMessage: function(data, ioArgs) {
		if (tourapp.remote.checkForDataError(data, null))
		{
			// Ignore receive data errors.
		}
		else
		{
			try {
				data = eval(data);
			  	if (djConfig.isDebug)
				  	console.log("receiveRemoteMessage to filter " + data.id + ", message: " + data.message);
				tourapp.getTaskSession().getReceiveQueue(data.queueName, data.queueType).getMessageFilterByRemoteID(data.id).methodToCall(data.message);
			} catch (e) {
		  		tourapp.util.displayErrorMessage("Error: " + e.description);
			}
		}
		tourapp.remote.receiveRemoteMessage(tourapp.getTaskSession().getReceiveQueue(data.queueName, data.queueType));	// Wait for the next message.
	},
	/**
	 * Send this message.
	 */
	sendMessage: function(sendQueue, message)
	{
		var args = {
			message: message,
			target: sendQueue.getFullSessionID()
		};
	
		tourapp.remote.sendToAjax("sendMessage", args, tourapp.remote.handleSendMessage);
	},
	/**
	 *
	 */
	handleSendMessage: function(data, ioArgs)
	{
	  	if (djConfig.isDebug)
		  	console.log("sendMessage ok");
		// Don't do anything
	},
	/*
	 * Check to see that this return data is valid
	 */
	checkForDataError: function(data, errorText, ignoreXMLError)
	{
		if ((data == undefined) || (data == null) || (data.length == 0))
		{
			if (errorText)
				tourapp.util.displayErrorMessage(errorText);
			return true;	// Error
		}
		if (!ignoreXMLError)
			if ((data != null) && (data.indexOf("<status-text>") != -1))
		{
			var startErrorText = data.indexOf("<error>");
			var endErrorText = data.indexOf("</error>");
			if ((startErrorText != -1) && (endErrorText > startErrorText))
				if (data.substring(startErrorText + 7, endErrorText) == "error")	// Error level
				{
					startErrorText = data.indexOf("<text>");
					endErrorText = data.indexOf("</text>");
					if ((startErrorText != -1) && (endErrorText > startErrorText))
					{
						errorText = data.substring(startErrorText + 6, endErrorText);
						tourapp.util.displayErrorMessage(errorText);
						return true;
					}
				}
		}
		return false;	// No error
	}
};
}
