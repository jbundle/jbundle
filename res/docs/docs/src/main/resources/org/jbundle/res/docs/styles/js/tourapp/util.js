/**
 * Top level methods and vars.
 */
if(!dojo._hasResource["tourapp.util"]){
dojo._hasResource["tourapp.util"] = true;
dojo.provide("tourapp.util");

dojo.require("dojox.data.dom");
dojo.require("dojo.back");

/**
 * Public Utilities.
 */
tourapp.util = {
	// Initialize environment
	init: function(user, password)
	{
		dojo.addOnUnload(tourapp.util, "free");
	
	  	if (djConfig.isDebug)
		  	if (console)
			  	console.log("init env");
	  	tourapp.gEnvState = true;

		tourapp.gTaskSession = new tourapp.classes.Session();
// For now, you'll have to start the queues manually
//		tourapp.util.addSendQueue();
//		tourapp.util.addReceiveQueue();
		if (!user)
			user = tourapp.util.getCookie("userid");
		if (!password)
			password = "";
		htmlSession = tourapp.util.getCookie("JSESSIONID");
		if (!user)
			if (htmlSession)
				user = "";	// Special case - The html session is authenticated - need to re-sign on as anonymous
	  	
	  	var pathname = location.pathname;
	  	var host = location.hostname;
	  	var search = location.search;
	  	if ((!user) || (user == ""))
	  	{	// User passed in in URL
	  		if (tourapp.util.getProperty(search, "user"))
	  			user = tourapp.util.getProperty(search, "user");
	  	}
		if (search)
			if (search != "")
				if (tourapp.util.getProperty(search, "menu") != null)
					tourapp.util.lastCommand = search;	// Make sure it does the correct command.
	  	
	  	tourapp.util.user = user;
		tourapp.gui.handleCreateRemoteTaskLink = dojo.connect(tourapp.remote, "handleCreateRemoteTask", tourapp.util, "handleCreateRemoteTask");

	  	tourapp.remote.createRemoteTask(user, password);
	  	
	  	if (pathname)
	  		if (tourapp.SERVER_PATH == "")
	  	{	// Nail down the host name, so call won't mistakenly call relative to .js directories
	  		if (pathname.lastIndexOf("/") != -1)
	  			pathname = pathname.substring(0, pathname.lastIndexOf("/"));
	  		pathname = pathname + "/";
	  		tourapp.SERVER_PATH = pathname;
	  	}
	  	var command = window.location.search;
		var bookmarkId = tourapp.util.getCommandFromHash(window.location.hash);
		if ((command) && (bookmarkId))
			command = command + '&' + bookmarkId;
		else if (bookmarkId)
			command = bookmarkId;
		if (!command)
			command = tourapp.util.DEFAULT_MENU;
		if (user == '')
			if (command != tourapp.util.DEFAULT_MENU)
				if (tourapp.util.getProperty(command, "menu") != null)
					tourapp.util.lastCommand = command;	// Special case
		if (!user)
			if (host)
			{
//?				var iDot = host.lastIndexOf('.com');
//?				if (iDot == -1)
//?					iDot = host.length;
//?				iDot = host.lastIndexOf('.', iDot - 1);
//?				if (iDot != -1)
//?					host = host.substring(iDot + 1);
//?				command = command + host;
			}

		if (bookmarkId && (bookmarkId.indexOf("?") == 0))
		{	//If we have a bookmark, load that as the initial state.
			command = bookmarkId;
			if (command != tourapp.util.DEFAULT_MENU)
				tourapp.util.lastCommand = command;
		} else {
			// Nothing special to do on initial page load
		}
		dojo.addOnLoad(function(){
			var appState = new tourapp.util.ApplicationState(command, bookmarkId, null);
			dojo.back.setInitialState(appState);
		});
	},
	DEFAULT_MENU: "?menu=",
	user: null,
	// handleLogin event link. Note: Is there a potential concurrency problem here?
	handleCreateRemoteTaskLink: null,
	// Special handler to sign on user after create initial task.
	handleCreateRemoteTask: function(data, ioArgs)
	{
		dojo.disconnect(tourapp.util.handleCreateRemoteTaskLink);
		if (tourapp.util.user != null)
		{
			tourapp.util.saveUser = null;	// Don't change cookie
			tourapp.gui.handleLoginLink = dojo.connect(tourapp.remote, "handleLogin", tourapp.gui, "handleLogin");
			tourapp.util.handleLoginLink = dojo.connect(tourapp.remote, "handleLogin", tourapp.util, "doLoginCommand");
			tourapp.remote.login(tourapp.getTaskSession(), tourapp.util.user);
		}
		if (!tourapp.util.user)
		{
			var action = "Login";
			tourapp.gui.changeUser(null);
			tourapp.gui.changeButton(dijit.byId(tourapp.gui.LOGOUT_DESC), tourapp.gui.LOGIN_DESC, action);	// Could be either
			tourapp.gui.changeButton(dijit.byId(tourapp.gui.LOGIN_DESC), tourapp.gui.LOGIN_DESC, action);
		}
	},
	// Free the environment
	free: function()
	{
	  	tourapp.gEnvState = false;	// Ignore the errors (from canceling the receive loop)
		if (tourapp.gTaskSession)
			tourapp.remote.freeRemoteSession(tourapp.gTaskSession);
	},
	// Make a remote session
	makeRemoteSession: function(sessionClassName)
	{
		var session = new tourapp.classes.Session(tourapp.getTaskSession());
		session.sessionClassName = sessionClassName;
		if (session.parentSession.sessionID)	// Only add the physical session if the parent session is set up, otherwise the handler will set it up later
			tourapp.remote.makeRemoteSession(session);
		return session;
	},
	// Login
	login: function(user, password)
	{
		tourapp.remote.login(tourapp.getTaskSession(), user, password);
	},
	// Logout
	logout: function()
	{
		tourapp.util.setCookie("userid", null);	// Clear cookie
		tourapp.gui.handleLoginLink = dojo.connect(tourapp.remote, "handleLogin", tourapp.gui, "handleLogin");
		tourapp.remote.login(tourapp.getTaskSession(), null, null);

		tourapp.util.lastCommand = "?menu=";
		tourapp.util.handleLoginLink = dojo.connect(tourapp.remote, "handleLogin", tourapp.util, "doLoginCommand");
	},
	// Add a new send message queue
	addSendQueue: function(filter)
	{
		if (!filter)
			filter = new Object();
		if (filter.queueName == null)
			filter.queueName = tourapp.TRX_SEND_QUEUE;
		if (filter.queueType == null)
			filter.queueType = tourapp.DEFAULT_QUEUE_TYPE;
		if (tourapp.getTaskSession().getSendQueue(filter.queueName, filter.queueType))
			return;	// The queue already exists.
		var sendQueue = new tourapp.classes.SendQueue(tourapp.getTaskSession(), filter.queueName, filter.queueType);
		if (tourapp.getTaskSession().sessionID)	// Only add if the remote task session exists (otherwise this will be called automatically)
			tourapp.remote.createRemoteSendQueue(sendQueue);
		return sendQueue;
	},
	// Add a new receive message queue
	addReceiveQueue: function(filter)
	{
		if (!filter)
			filter = new Object();
		if (filter.queueName == null)
			filter.queueName = tourapp.TRX_RECEIVE_QUEUE;
		if (filter.queueType == null)
			filter.queueType = tourapp.DEFAULT_QUEUE_TYPE;
		if (tourapp.getTaskSession().getReceiveQueue(filter.queueName, filter.queueType))
			return;	// The queue already exists.
	  	var receiveQueue = new tourapp.classes.ReceiveQueue(tourapp.getTaskSession(), filter.queueName, filter.queueType);
		if (tourapp.getTaskSession().sessionID)	// Only add if the remote task session exists (otherwise this will be called automatically)
			tourapp.remote.createRemoteReceiveQueue(receiveQueue);
		return receiveQueue;
	},
	// Send a message
	sendMessage: function(message)
	{
		if (message.queueName == null)
			message.queueName = tourapp.TRX_SEND_QUEUE;
		if (message.queueType == null)
			message.queueType = tourapp.DEFAULT_QUEUE_TYPE;
		var sendQueue = tourapp.getTaskSession().getSendQueue(message.queueName, message.queueType);
		if (!sendQueue)
			sendQueue = tourapp.util.addSendQueue(message);
		tourapp.remote.sendMessage(sendQueue, message.data);
	},
	// Add a message listener to this receive queue
	addMessageListener: function(filter)
	{
		if (filter.queueName == null)
			filter.queueName = tourapp.TRX_RECEIVE_QUEUE;
		if (filter.queueType == null)
			filter.queueType = tourapp.DEFAULT_QUEUE_TYPE;
		var receiveQueue = tourapp.getTaskSession().getReceiveQueue(filter.queueName, filter.queueType);
		if (!receiveQueue)
			receiveQueue = tourapp.util.addReceiveQueue(filter);
		var messageFilter = new tourapp.classes.MessageFilter(receiveQueue, filter.onMessage);
		if (receiveQueue.sessionID)	// Only add the physical remote filter if the receive queue is set up, otherwise the filter will be set up later
			tourapp.remote.addRemoteMessageFilter(messageFilter);
	},
	/*
	 * Display an error message.
	 */
	displayErrorMessage: function(message)
	{
		if (tourapp.gui)
			tourapp.gui.displayErrorMessage(message);
		else
			alert(message);	// Note: Do something else here.
	},
	// Handle an onClick in an <a> link
	handleLink: function(link)
	{
		if (link)
		{
			var command = link.href;
			if (command)
				return tourapp.util.doCommand(command);	// Link handled, don't follow link
		}
		return true;	// Link not handled by me, so follow link
	},
	// Do this screen link command
	doLink: function(command)
	{
		tourapp.util.doCommand(command);
	},
	// Do this screen button command
	doButton: function(command)
	{
		if (command.indexOf("?") == -1)
			command = "command=" + command;
		tourapp.util.doCommand(command);
		return false;	// This tells form not to submit.
	},
	// Last command
	lastCommand: null,
	// Do this screen command
	doCommand: function(command, decode, addHistory)
	{
		if (arguments.length < 3)
			addHistory = true;
		if (command == null)
			return;
		if (decode == null)
			decode = true;
		if (decode)
			command = decodeURI(command);
		if ((command.indexOf("Login") != -1) || (tourapp.util.getProperty(command, "user") == ''))
		{
			var user = tourapp.util.getProperty(command, "user");
			if (user == "")
				user = null;
			if (user == null)
			{
				if (tourapp.getTaskSession().security)
					if (tourapp.getTaskSession().security.userProperties)
						if (tourapp.getTaskSession().security.userProperties.user)
							user = tourapp.getTaskSession().security.userProperties.user;
				if ((user == "1") || (user == ""))
					user = null;
			}
			if (user == null)
				tourapp.gui.displayLogonDialog();
			else
				tourapp.util.logout();
		}
		else if (command.indexOf("preferences=") != -1)
		{
			var navmenus = tourapp.util.getProperty(command, "navmenus");
			if (navmenus)
				tourapp.gui.changeNavMenus(navmenus);
		}
		else if (tourapp.util.getProperty(command, "help") != null)
		{
			if (tourapp.util.lastCommand)
				if (tourapp.util.getProperty(command, "class") != null)
					command = tourapp.util.lastCommand + "&help=";
			tourapp.util.doScreen(command, addHistory);
		}
		else if (((tourapp.util.getProperty(command, "screen") != null)
			|| (tourapp.util.getProperty(command, "menu") != null)
			|| (tourapp.util.getProperty(command, "xml") != null)
			|| (tourapp.util.getProperty(command, "record") != null))
			&& (tourapp.util.getProperty(command, "applet") == null))
		{
			if ((tourapp.util.getProperty(command, "user") != null)
				&& (tourapp.util.getProperty(command, "user").length > 0)
				&& ((tourapp.util.user == null) || (tourapp.util.user.length == 0) || (tourapp.util.user != tourapp.util.getProperty(command, "user"))))
			{	// Special case - sign on before doing command.
				var user = tourapp.util.getProperty(command, "user");
				var password = tourapp.util.getProperty(command, "auth");

				tourapp.gui.handleLoginLink = dojo.connect(tourapp.remote, "handleLogin", tourapp.gui, "handleLogin");
				tourapp.util.lastCommand = command;
				tourapp.util.handleLoginLink = dojo.connect(tourapp.remote, "handleLogin", tourapp.util, "doLoginCommand");
				tourapp.remote.login(tourapp.getTaskSession(), user, password);
			}
			else
			{
				tourapp.util.lastCommand = command;	// TODO (don) This logic is very weak
				tourapp.util.doScreen(command, addHistory);
			}
		}
		else if (tourapp.util.getProperty(command, "command"))
		{
			tourapp.util.doLocalCommand(command, addHistory);
		}
		else if (tourapp.util.getProperty(command, "applet") != null)
		{
			javaApplet = null;
			if (tourapp.getTaskSession().security != null)	// Signed on
				javaApplet = tourapp.getTaskSession().security.userProperties.javaApplet;
			if ((!javaApplet) || ((javaApplet.indexOf('J') == 0) || (javaApplet.indexOf('Y') == 0)))
			{	// Display an applet
				// Note: For now I do not render an applet page, I jump to a new applet page (Since I can't figure out how to run js in xsl)
				if (tourapp.gui.displayApplet(command) == true)
					return false;	// Success
				// drop thru if not handled
			}
			console.log("do link: " + command);
			window.location = command;
		}
		else
		{	// This is just a link to be opened in the browser
			console.log("do link: " + command);
			window.location = command;
		}
		return false;	// In case this was called from onClick in a link (do not follow link since I handled the link).
	},
	// handleLogin event link. Note: Is there a potential concurrency problem here?
	handleLoginLink: null,
	// After logging into a new account, process the command.
	doLoginCommand: function()
	{
		dojo.disconnect(tourapp.util.handleLoginLink);

		var command = tourapp.util.lastCommand;
		if (tourapp.util.getProperty(command, "user") != null)
		{	// strip out user param
			var iStart = command.indexOf("user=");
			var iEnd = command.indexOf("&", iStart);
			if (iEnd == -1)
				command = command.substring(0, iStart - 1);
			else
				command = command.substring(0, iStart - 1) + command.substring(iEnd);
		}
		tourapp.util.lastCommand = command;	// Make sure this is the 'last' command
		if ((command) && (command != ""))
			tourapp.util.doCommand(command, false, false);		
	},
	/*
	 * Local commands are formatted commmand=xyz
	 */
	doLocalCommand: function(command, addHistory)
	{
		var commandTarget = tourapp.util.getProperty(command, "command");
		console.log("do local command: " + command);
		if (commandTarget == "Back")
		{
			parent.frames[0].history.back();
		}
		else if (commandTarget == "Submit")
		{
			tourapp.util.submitData(commandTarget);
		}
		else if (commandTarget == "Reset")
		{
			tourapp.gui.clearFormData();
		}
		else if (commandTarget == "Delete")
		{
			tourapp.util.deleteData(command);
		}
		else if ((commandTarget == "FormLink")
			|| (commandTarget == "Form")
			|| (commandTarget == "Link"))
		{
			tourapp.util.doScreen(command, addHistory);
		}
		else
		{
			tourapp.util.submitData(commandTarget);
		}
	},
	/**
	 * Submit the form data to the screen.
	 */
	submitData: function(command) {
		var messageFilter = new tourapp.classes.MessageFilter(tourapp.util.getAjaxSession(), tourapp.util.doRemoteSubmitCallback);
		messageFilter.name = command;
		messageFilter.properties = tourapp.gui.getFormData();
		if (tourapp.util.getAjaxSession().sessionID)	// Only add the physical remote filter if the receive queue is set up, otherwise the filter will be set up later
		{
			tourapp.gui.waitCursor();
			tourapp.remote.doRemoteAction(messageFilter);
		}
	},
	/**
	 * Handle the XML coming back from the menu action.
	 */
	doRemoteSubmitCallback: function(data, ioArgs)
	{
		var bSuccess = tourapp.util.handleReturnData(data, ioArgs);
		if (bSuccess == true)
			tourapp.gui.clearFormData();
		tourapp.gui.restoreCursor();
	},
	/**
	 * Submit the form data to the screen.
	 */
	deleteData: function(command) {
		var messageFilter = new tourapp.classes.MessageFilter(tourapp.util.getAjaxSession(), tourapp.util.doRemoteDeleteCallback);
		messageFilter.name = command;
		if (tourapp.gui.isForm())
			messageFilter.properties = tourapp.gui.getFormData(true);	// Hidden fields
		if (tourapp.util.getAjaxSession().sessionID)	// Only add the physical remote filter if the receive queue is set up, otherwise the filter will be set up later
			tourapp.remote.doRemoteAction(messageFilter);
	},
	/**
	 * Handle the XML coming back from the menu action.
	 */
	doRemoteDeleteCallback: function(data, ioArgs)
	{
		var bSuccess = tourapp.util.handleReturnData(data, ioArgs);
		if (bSuccess == true)
		{
			if (tourapp.gui.isForm())
			{
				tourapp.gui.clearFormData();
			}
			else
			{
				if (ioArgs)
					if (ioArgs.args)
						if (ioArgs.args.content)
							if (ioArgs.args.content.name)
				tourapp.gui.clearGridData(tourapp.util.getProperty(ioArgs.args.content.name, "objectID"));
			}
		}
	},
	/**
	 * Get my ajax session.
	 */
	getAjaxSession: function()
	{
		if (!tourapp.util.jsSession)
			tourapp.util.jsSession = tourapp.util.makeRemoteSession(".main.remote.AjaxScreenSession");
		return tourapp.util.jsSession;
	},
	jsSession: null,
	/**
	 * Do this screen command.
	 */
	doScreen: function(command, addHistory)
	{
		console.log("do screen: " + command);
		var messageFilter = new tourapp.classes.MessageFilter(tourapp.util.getAjaxSession(), tourapp.util.doRemoteScreenActionCallback);
		messageFilter.bindArgs = {
			addHistory: addHistory
		};
		messageFilter.name = "createScreen";
		messageFilter.properties = tourapp.util.commandToProperties(command);
		if (tourapp.util.getAjaxSession().sessionID)	// Only add the physical remote filter if the receive queue is set up, otherwise the filter will be set up later
			tourapp.remote.doRemoteAction(messageFilter);
	},
	// Handle the XML coming back from the menu action
	doRemoteScreenActionCallback: function(data, ioArgs)
	{
		tourapp.util.handleReturnData(data, ioArgs);
	},
	// Handle the XML coming back from the menu action
	// Return true if success (non-error return)
	handleReturnData: function(data, ioArgs)
	{
		var domToBeTransformed = dojox.data.dom.createDocument(data);
		var info = domToBeTransformed.getElementsByTagName("status-text");
		if (info)
			if (info.length > 0)
				if (info[0].parentNode == domToBeTransformed)
		{
			if (tourapp.util.checkCommand(info[0]))
				return true;
			var infoLevel = tourapp.util.handleScreenInfoMessage(info[0]);
			return (infoLevel != "error");	// Only return false if error level
		}

		var domToAppendTo = document.getElementById("content-area");
		var contentParent = domToAppendTo.parentNode;
		// First, delete all the old nodes
		tourapp.gui.removeChildren(domToAppendTo, true);	// Note: I remove the node also since the replacement's root is <div id='content-area'>
		// Then, add the new nodes (via xslt)
		var desc = tourapp.gui.changeTitleFromData(domToBeTransformed);
		if (ioArgs)
			if (ioArgs.args.addHistory)
		{
			var command = ioArgs.args.content.name;
			var bookmark = tourapp.util.propertiesToCommand(ioArgs.args.content.properties);
			var appState = new tourapp.util.ApplicationState(command, bookmark, data);
			dojo.back.addToHistory(appState);
		}
		// Extract stylesheet name from XML
		var xsltURI = null;
		for (var i = 0; i < domToBeTransformed.childNodes.length; i++)
		{
			if (domToBeTransformed.childNodes[i].nodeType == 7) // Node.PROCESSING_INSTRUCTION_NODE)
				if (domToBeTransformed.childNodes[i].nodeName == "xml-stylesheet")
				{
					data = domToBeTransformed.childNodes[i].data;
					var startURI = data.indexOf("href=\"");
					if (startURI != -1)
					{
						startURI  = startURI + 6;
						var endURI = data.indexOf("\"", startURI);
						xsltURI = data.substring(startURI, endURI);
					}
					break;
				}
		}
		if (xsltURI == null)
			xsltURI = "docs/styles/xsl/ajax/base/menus-ajax.xsl";
		xsltURI = tourapp.getServerPath(xsltURI);
		tourapp.xml.doXSLT(domToBeTransformed, xsltURI, contentParent, tourapp.gui.fixNewDOM);
		return true;	// Success (so far)
	},
	// See if this status message contains a command
	checkCommand: function(infoDOM)
	{
		var command; 
		if (infoDOM.getElementsByTagName("command"))
			if (infoDOM.getElementsByTagName("command")[0])
				command = infoDOM.getElementsByTagName("command")[0].firstChild.nodeValue;
		if (command)
		{
			tourapp.util.doCommand(command);
			return true;
		}
		return false;	// No command in status text.
	},
	// Handle this return error or info message.
	// Return the error level.
	handleScreenInfoMessage: function(infoDOM)
	{
		var ACCESS_DENIED = 101, LOGIN_REQUIRED = 102, AUTHENTICATION_REQUIRED = 103, CREATE_USER_REQUIRED = 104;
		var errorCode = infoDOM.getElementsByTagName("errorCode");
		if ((errorCode) && (errorCode[0]) && (errorCode[0].firstChild))
			errorCode = errorCode[0].firstChild.nodeValue;
		else
			errorCode = null;
		var error = 0;
		if (errorCode)
		{
			try {
				error = parseInt(errorCode);
			} catch (ex) {
				error = 0;
			} 
		}
		var infoClass = infoDOM.getElementsByTagName("error")[0].firstChild.nodeValue;
		var infoText = infoDOM.getElementsByTagName("text")[0].firstChild.nodeValue;
		if (error == 0)
			if (infoText)
			{
				try {
					error = parseInt(infoText);
					if (error)
						infoText = null;
				} catch (ex) {
					error = 0;
				} 
			}
		if (!infoText)
		{
			if (error == AUTHENTICATION_REQUIRED)
				infoText = "Authentication required";
			else if (error == LOGIN_REQUIRED)
				infoText = "Sign in required";
			else if (error == ACCESS_DENIED)
				infoText = "Access denied";
		}
		if ((error == AUTHENTICATION_REQUIRED) || (error == LOGIN_REQUIRED))
			tourapp.gui.displayLogonDialog(null, null, infoText, tourapp.util.lastCommand);	// Repeat the last command
		else if (error == ACCESS_DENIED)
			tourapp.gui.displayErrorMessage(infoText);
		else if (error == CREATE_USER_REQUIRED)
			;	// ?
		else
			tourapp.gui.displayScreenInfoMessage(infoText, infoClass);
		return infoClass;
	},
	// Convert this properties object to a command
	propertiesToCommand: function(properties)
	{
		var command = "?";
		if (properties)
		{
			if (typeof(properties) == 'string')
				if (properties.length > 1)
				{
					if (!properties.substring(0, 1) != "(")
						properties = "(" + properties + ")";
					properties = eval(properties);
				}
			for (var name in properties)
			{
				if (command.length > 1)
					command += "&";
				command += name + "=" + escape(properties[name]);
			}
		}
		return command;
	},
	// Convert this command string to a properties object.
	commandToProperties: function(command, properties)
	{
		if (!properties)
			properties = {};
		var commandArray = command.split(/[;&?]/);
		for (var i = 0; i < commandArray.length; i++)
		{
			var thisCommand = commandArray[i];
			while ((thisCommand.charAt(0) == ' ') || (thisCommand.charAt(0) == '?'))
				thisCommand = thisCommand.substring(1, thisCommand.length);
			var equals = thisCommand.indexOf('=');
			if (equals != -1)	// Always
				properties[thisCommand.substring(0, equals)] = unescape(thisCommand.substring(equals+1, thisCommand.length));
		}
		return properties;
	},
	// Convert this array to an xml string
	propertiesToXML: function(properties)
	{
		xml = "";
		if (!properties)
			return xml;
		for (var key in properties)
		{
			xml = xml + '<' + key + '>' + properties[key] + '</' + key + '>\n';
		}
		return xml;
	},
	// Get this property from this command string
	getProperty: function(command, key)
	{
		var nameEQ = key.toUpperCase() + "=";
		if (command == null)
			return null;
		if (command.indexOf("?") != -1)
			if ((command.indexOf("?") < command.indexOf("&") || (command.indexOf("&") == -1)))
				command = command.substring(command.indexOf("?") + 1);
		var ca = command.split(/[;&]/);
		for (var i = 0; i < ca.length; i++)
		{
			var c = ca[i];
			while ((c.charAt(0) == ' ') || (c.charAt(0) == '?'))
				c = c.substring(1, c.length);
			if (c.toUpperCase().indexOf(nameEQ) == 0)
				return unescape(c.substring(nameEQ.length, c.length));
		}
		return null;
	},
	// Set this cookie (if value=null, delete the cookie)
	setCookie: function(name, value, days)
	{
		if (days) {
			var date = new Date();
			date.setTime(date.getTime()+(days*24*60*60*1000));
			var expires = "; expires="+date.toGMTString();
		}
		else
			var expires = "";
		if (!value)
			value = "";
		document.cookie = name+"="+escape(value)+expires+"; path=/";
	},
	// Get this cookie.
	getCookie: function(name)
	{
		var value = tourapp.util.getProperty(document.cookie, name);
		if (value != null)
			value = unescape(value);
		return value;
	},
	// Remove the hash mark
	getCommandFromHash: function(hash)
	{
		if (hash)
			if (hash.length > 0)
		{
			hash = unescape(hash);
			if (hash.substring(0, 1) == '#')
				hash = hash.substring(1);
		}
		return hash;
	},
	// Non-history hash change
	doHashChange: function(command)
	{
		tourapp.util.doCommand(this.command, true, false);
	}
};
/*
ApplicationState is an object that represents the application state.
It will be given to dojo.undo.browser to represent the current application state.
*/
tourapp.util.ApplicationState = function(screenCommand, bookmarkValue, stateData) {
	this.command = screenCommand;
	this.data = stateData;
	if (bookmarkValue)	// The browser URL to change
		this.changeUrl = bookmarkValue;
};

tourapp.util.ApplicationState.prototype.back = function() {
	if (tourapp.java)
		if (tourapp.java.isJavaWindow())
			tourapp.java.prepareWindowForApplet(false);		// Change from applet display
	if (this.data)
		tourapp.util.doRemoteScreenActionCallback(this.data);
	else
		tourapp.util.doCommand(this.command, true, false);
};

tourapp.util.ApplicationState.prototype.forward = function() {
	if (tourapp.java)
		if (tourapp.java.isJavaWindow())
			tourapp.java.prepareWindowForApplet(false);		// Change from applet display
	if (this.data)
		tourapp.util.doRemoteScreenActionCallback(this.data);
	else
		tourapp.util.doCommand(this.command, true, false);
};


dojo.addOnLoad(tourapp.util, "init");

}
