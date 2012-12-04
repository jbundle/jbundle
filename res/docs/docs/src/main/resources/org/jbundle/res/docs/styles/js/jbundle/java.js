/**
 * Top level methods and vars.
 */
if(!dojo._hasResource["jbundle.java"]){
dojo._hasResource["jbundle.java"] = true;
dojo.provide("jbundle.java");

dojo.require("dojo.back");

if (!jbundle.util)
{
	dojo.addOnLoad(function(){
		dojo.back.setInitialState(new jbundle.java.State(jbundle.java.getCommandFromHash(window.location.hash)));
	});
}

/**
 * Browser back support.
 */
jbundle.java = {
	    SERVLET_NAME: "webstart",          // The generic queue for remote sent transaction messages.
	/**
	 * This is called from the history state object when the state is popped by a browser back command.
	 * I Call the java doJavaBrowserBack method.
	 * @param command Is the command pushed onto the history stack.
	 */
	doJavaBrowserBack: function(command)
	{
		if (!jbundle.java.isJavaWindow())
			jbundle.java.displayApplet(command);
		if (jbundle.java.ignoreBack != true)
		{
			if (document.jbundle)
				document.jbundle.doJavaBrowserBack(command);
			if (jbundle.debug == true)
				console.log("doJavaBrowserBack command =" + command);
		}
		jbundle.java.ignoreBack = false;
	},
	/**
	 * This is called from the history state object when the state is popped by a browser back command.
	 * I Call the java doJavaBrowserForward method.
	 * @param command Is the command pushed onto the history stack.
	 */
	doJavaBrowserForward: function(command)
	{
		if (!jbundle.java.isJavaWindow())
			jbundle.java.displayApplet(command);
		else if (document.jbundle)
			document.jbundle.doJavaBrowserForward(command);
		if (jbundle.debug == true)
			console.log("doJavaBrowserForward command =" + command);
	},
	/**
	 * This is called from the history state object when the state is popped by a browser back command.
	 * I Call the java doJavaBrowserForward method.
	 * @param command Is the command pushed onto the history stack.
	 */
	doJavaBrowserHashChange: function(command)
	{
		if (jbundle.util)
			if (jbundle.util.getProperty(command, "applet") == null)
			{
				if (jbundle.java.isJavaWindow())
					jbundle.java.prepareWindowForApplet(false);
				jbundle.util.doCommand(command);
				return;
			}
		if (!jbundle.java.isJavaWindow())
			jbundle.java.displayApplet(command);
		else if (document.jbundle)
			document.jbundle.doJavaBrowserHashChange(command);
		else if (jbundle.util)
		{	// Must be an xsl command
			jbundle.java.prepareWindowForApplet(false);
			jbundle.util.doBrowserHashChange(command);
		}
		if (jbundle.debug == true)
			console.log("doJavaBrowserHashChange command =" + command);
	},
	/**
	 * This is called from java to push a history object onto the stack.
	 * @param command Is the command to be pushed onto the history stack.
	 */
	pushBrowserHistory: function(command, title)
	{
		dojo.back.addToHistory(new jbundle.java.State(command));
		if (title)
			document.title = title;
		if (jbundle.debug == true)
			console.log("pushBrowserHistory command =" + command + " title= " + title);
	},
	/**
	 * This is called from java to pop history object(s) from the stack.
	 * Note: The global variable ignoreBack keeps me from notifying java of the change in the page.
	 * @param count Is the number of commands to pop from the stack.
	 */
	popBrowserHistory: function(count, commandHandledByClient, title)
	{
		move = 0 - count;
		if ((commandHandledByClient == 'true') || (commandHandledByClient == true))
			jbundle.java.ignoreBack = true;
		history.go(move);
		if (title)
			document.title = title;
		if (jbundle.debug == true)
			console.log('popBrowserHistory count =' + count + ' move = ' + move + ' handled = ' + commandHandledByClient + " title= " + title);
	},
	/**
	 * For now - just do the html link.
	 */
	doLink: function(command)
	{
		window.location = command;
	},
	/**
	 * The state object.
	 * Note: The Url hash is the command
	 * Note: The back and forward functions are prototypes.
	 */
	State: function(command)
	{
		this.changeUrl = command;
	},
	// Display a applet with this command in the content area.
	// Returns true if successful
	// --NOTE-- For this work, you must include deployJava.js in mainstyles-ajax
	displayApplet: function(command)
	{
		if (!command)
			return false;
		var params = jbundle.util.commandToProperties(command);

		var domToAppendTo = document.getElementById("content-area");
		// First, delete all the old nodes
		jbundle.gui.removeChildren(domToAppendTo, false);
		// Then, add the new nodes (via xslt)
		//+ var desc = jbundle.gui.changeTitleFromData(domToBeTransformed);
		var attributes = jbundle.java.getAppletAttributes(params);
		var jnlp = jbundle.java.getJnlpURL(attributes, params);
		if (!params.jnlp_href)
			params['jnlp_href'] = jnlp;
		
		jbundle.java.prepareWindowForApplet(true);
		
		var html = jbundle.java.getAppletHtml(attributes, params, '1.6');
		domToAppendTo.innerHTML = html;
		jbundle.java.pushBrowserHistory(command);
		return true;
	},
	// Return true if java applet is displayed
	isJavaWindow: function()
	{
		if (jbundle.util)
			return (document.body.parentNode.className == "java");
		return true;	// This is only for tourapp windows
	},
	oldClassName: "",
	// Setup/restore this screen to display this applet
	prepareWindowForApplet: function(flag)
	{
		if (flag == true)
		{
			if (document.body.parentNode.className != "java")
				jbundle.java.oldClassName = document.body.parentNode.className;
			document.body.parentNode.className="java";	// For firefox html.class
			jbundle.gui.changeTheTitle("Java Window");
		}
		else
		{
		    if (jbundle.java.oldClassName)
				document.body.parentNode.className=jbundle.java.oldClassName;
		}
	},
	// Get the applet attributes from the params
	getAppletAttributes: function(params)
	{
		var attributes = {};
		if ((params.applet != null) && (params.applet != 'undefined') && (!params.code))
		{
			params.code = params.applet;
			delete params.applet;
		}
		for (var key in params)
		{
			param = params[key];
			move = false;
			remove = false;
			if (key == 'code')
				move = true;
			if (key == 'codebase')
				move = true;
			if (key == 'name')
				move = true;
			if ((key == 'height') || (key == 'width'))
			{
				move = true;
				remove = true;
			}
			if (move == true)
				attributes[key] = param;
			if (remove == true)
				delete params[key];
		}
		if (!params.domain)
			params.domain = location.hostname;
		if (!params.baseURL)
		{
			params.baseURL = location.host;
			if ((location.pathname.indexOf(jbundle.java.SERVLET_NAME) < 1) && (location.pathname.indexOf(jbundle.java.SERVLET_NAME + '/') != 0))
				params.baseURL += "/";
			else
				params.baseURL += location.pathname.substring(0, location.pathname.indexOf(jbundle.java.SERVLET_NAME));
		}
		if (!params.url)
			params.url = location.protocol + '//' + location.host + location.pathname;
		if (!attributes.codebase)
			attributes.codebase = location.protocol + '//' + params.baseURL;
		if (!attributes.width)
			attributes.width = '100%';
		if (!attributes.height)
			attributes.height = '98%';
		if (!attributes.name)
			attributes.name = 'jbundle';
		if (!params.hash) if (window.location.hash)
		{	// How do I keep from picking up the xsl hash?
//			params.hash = location.hash;
		}
		if (!params.draggable)
			params.draggable = true;
		return attributes;
	},
	// Get the jnlp URL from the params
	getJnlpURL: function(attributes, params)
	{
		var jnlp = {};
		for (var name in params)
		{
			jnlp[name] = params[name];
		}
		jnlp['datatype']='jnlpapplet';
		if (!jnlp.applet)
				if (attributes['code'])
					jnlp['appletClass'] = attributes['code'];
		var command = attributes.codebase + jbundle.java.SERVLET_NAME + jbundle.util.propertiesToCommand(jnlp);
		return command;
	},
    /**
     * Similar to deployJava, except I pass the complete command.
     */
    runAppletWithCommand: function(command) {
		if (!command)
			return false;
		var params = jbundle.util.commandToProperties(command);

		var attributes = jbundle.java.getAppletAttributes(params);
		var jnlp = jbundle.java.getJnlpURL(attributes, params);
		if (!params.jnlp_href)
			params['jnlp_href'] = jnlp;
		
		deployJava.runApplet(attributes, params, '1.6');
		jbundle.java.pushBrowserHistory(command);
		return true;
    },
    /**
     * Same as deployJava, except I add to a string instead of doing document.write(xx).
     * NOTE: This method only works with the jbundle.gui code.
     */
    getAppletHtml: function(attributes, parameters, minimumVersion) {
        if (minimumVersion == 'undefined' || minimumVersion == null) {
            minimumVersion = '1.1';
        }

        var regex = "^(\\d+)(?:\\.(\\d+)(?:\\.(\\d+)(?:_(\\d+))?)?)?$";

        var matchData = minimumVersion.match(regex);

        if (deployJava.returnPage == null) {
            // if there is an install, come back here and run the applet
            deployJava.returnPage = document.location;
        }

        if (matchData != null) {
            var browser = deployJava.getBrowser();
            if ((browser != '?') && (browser != 'Safari')) {
                if (deployJava.versionCheck(minimumVersion + '+')) {
                    return jbundle.java.writeAppletTag(attributes, parameters);
                } else if (deployJava.installJRE(minimumVersion + '+')) {
                    // after successfull install we need to refresh page to pick
                    // pick up new plugin
                    deployJava.refresh();
                    location.href = document.location;
                    return jbundle.java.writeAppletTag(attributes, parameters);
                }
            } else {
                // for unknown or Safari - just try to show applet
            	return jbundle.java.writeAppletTag(attributes, parameters);
            }
        } else {
            if (deployJava.debug) {
                alert('Invalid minimumVersion argument to getAppletHtml():' + 
                      minimumVersion);
            }
        }
    },
    /**
     * Same as deployJava, except I add to a string instead of doing document.write(xx).
     */
    writeAppletTag: function(attributes, parameters) {
        var s = '<' + 'applet ';
        for (var attribute in attributes) {
            s += (' ' + attribute + '="' + attributes[attribute] + '"');
        }
        s += '>';
    
        if (parameters != 'undefined' && parameters != null) {
            var codebaseParam = false;
            for (var parameter in parameters) {
                if (parameter == 'codebase_lookup') {
                    codebaseParam = true;
                }
                s += '<param name="' + parameter + '" value="' + 
                    parameters[parameter] + '">';
            }
            if (!codebaseParam) {
            	s += '<param name="codebase_lookup" value="false">';
            }
        }
        s += '<' + '/' + 'applet' + '>';
        return s;
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
	ignoreBack: false
};

jbundle.java.State.prototype.back = function() { jbundle.java.doJavaBrowserBack(this.changeUrl); };
jbundle.java.State.prototype.forward = function() { jbundle.java.doJavaBrowserForward(this.changeUrl); };

/**
 * For java to call these, these must be at the root.
 */
function pushBrowserHistory(command, title)
{
	jbundle.java.pushBrowserHistory(command, title);
}
function popBrowserHistory(count, commandHandledByClient, title)
{
	 jbundle.java.popBrowserHistory(count, commandHandledByClient, title);
}

}