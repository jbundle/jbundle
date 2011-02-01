  function onLoad() {

    var buttonObj = document.getElementById("myButton");
    dojo.connect(buttonObj, "onclick", this, "sendTheMessage");
    
    buttonObj = document.getElementById("queueButton");
    dojo.connect(buttonObj, "onclick", this, "addTheQueue");
    
    buttonObj = document.getElementById("filterButton");
    dojo.connect(buttonObj, "onclick", this, "addTheListener");
    
    buttonObj = document.getElementById("inButton");
    dojo.connect(buttonObj, "onclick", this, "logon");
    
    buttonObj = document.getElementById("outButton");
    dojo.connect(buttonObj, "onclick", this, "logoff");
    
    buttonObj = document.getElementById("freeButton");
    dojo.connect(buttonObj, "onclick", this, "freeSession");
    
    buttonObj = document.getElementById("createButton");
    dojo.connect(buttonObj, "onclick", this, "createSession");
    
    buttonObj = document.getElementById("actionButton");
    dojo.connect(buttonObj, "onclick", this, "doAction");
  }
  
	function sendTheMessage()
	{
	    var curr = document.getElementById("myForm").message.value;
	    message = {
	  	queueName: "chat",
	    	data: curr
	    };
	    tourapp.util.sendMessage(message);
	}
	
	function addTheQueue()
	{
	  var filter = {
	  	queueName: "chat"
	  };
	  tourapp.util.addSendQueue(filter);
	  tourapp.util.addReceiveQueue(filter);
	}
	
	function addTheListener()
	{
	  var filter = {
	  	queueName: "chat",
	  	onMessage: addLineToOutput
	  };
	  tourapp.util.addMessageListener(filter);
	}
	
	function addLineToOutput(message)
	{
	  var curr = document.getElementById("myForm").author.value;
	  document.getElementById("myForm").author.value = curr + "\n" + message;
	}
	
	function logon()
	{
	  tourapp.util.login("don", "donwpp");
	}
	
	function logoff()
	{
	  tourapp.util.logout();
	}
	
	function freeSession()
	{
	  tourapp.util.free();
	}
 
	var session;
	function createSession()
	{
		session = tourapp.util.makeRemoteSession(".main.remote.AjaxScreenSession");
	}
  
	function doAction()
	{
		if (!session)
			createSession();
		var remoteCommand = "?menu=Main";
		var properties = null;
		var callbackFunction = doCallback;

		var messageFilter = new tourapp.classes.MessageFilter(session, callbackFunction);
		messageFilter.properties = properties;
		messageFilter.remoteCommand = remoteCommand;
		if (session.sessionID)	// Only add the physical remote filter if the receive queue is set up, otherwise the filter will be set up later
			tourapp.remote.doRemoteAction(messageFilter);
	}

	function doCallback(properties)
	{
		var domToAppendTo = document.getElementById("menuarea");
		// First, delete all the old nodes
		tourapp.gui.removeAllChildren(domToAppendTo);
		// Then, add the new nodes (via xslt)
		var xsltURI = tourapp.getServerPath("docs/styles/xsl/ajax/base/menus.xsl");
		tourapp.xml.doXSLT(properties, xsltURI, domToAppendTo);
	}
