/**
 * Top level methods and vars.
 */
if(!dojo._hasResource["tourapp.java"]){
dojo._hasResource["tourapp.java"] = true;
dojo.provide("tourapp.java");

dojo.require("dojo.back");

if (!tourapp.util)
{
	dojo.addOnLoad(function(){
		dojo.back.setInitialState(new tourapp.java.State(tourapp.java.getCommandFromHash(window.location.hash)));
	});
}

/**
 * Browser back support.
 */
tourapp.java = {
	/**
	 * This is called from the history state object when the state is popped by a browser back command.
	 * I Call the java doJavaBrowserBack method.
	 * @param command Is the command pushed onto the history stack.
	 */
	doJavaBrowserBack: function(command)
	{
		if (!tourapp.java.isJavaWindow())
			tourapp.java.displayApplet(command);
		if (tourapp.java.ignoreBack != true)
		{
			if (document.tourapp)
				document.tourapp.doJavaBrowserBack(command);
			if (tourapp.debug == true)
				console.log("doJavaBrowserBack command =" + command);
		}
		tourapp.java.ignoreBack = false;
	},
	/**
	 * This is called from the history state object when the state is popped by a browser back command.
	 * I Call the java doJavaBrowserForward method.
	 * @param command Is the command pushed onto the history stack.
	 */
	doJavaBrowserForward: function(command)
	{
		if (!tourapp.java.isJavaWindow())
			tourapp.java.displayApplet(command);
		else if (document.tourapp)
			document.tourapp.doJavaBrowserForward(command);
		if (tourapp.debug == true)
			console.log("doJavaBrowserForward command =" + command);
	},
	/**
	 * This is called from the history state object when the state is popped by a browser back command.
	 * I Call the java doJavaBrowserForward method.
	 * @param command Is the command pushed onto the history stack.
	 */
	doJavaBrowserHashChange: function(command)
	{
		if (tourapp.util)
			if (tourapp.util.getProperty(command, "applet") == null)
			{
				if (tourapp.java.isJavaWindow())
					tourapp.java.prepareWindowForApplet(false);
				tourapp.util.doCommand(command);
				return;
			}
		if (!tourapp.java.isJavaWindow())
			tourapp.java.displayApplet(command);
		else if (document.tourapp)
			document.tourapp.doJavaBrowserHashChange(command);
		else if (tourapp.util)
		{	// Must be an xsl command
			tourapp.java.prepareWindowForApplet(false);
			tourapp.util.doBrowserHashChange(command);
		}
		if (tourapp.debug == true)
			console.log("doJavaBrowserHashChange command =" + command);
	},
	/**
	 * This is called from java to push a history object onto the stack.
	 * @param command Is the command to be pushed onto the history stack.
	 */
	pushBrowserHistory: function(command, title)
	{
		dojo.back.addToHistory(new tourapp.java.State(command));
		if (title)
			document.title = title;
		if (tourapp.debug == true)
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
			tourapp.java.ignoreBack = true;
		history.go(move);
		if (title)
			document.title = title;
		if (tourapp.debug == true)
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
		var params = tourapp.util.commandToProperties(command);

		var domToAppendTo = document.getElementById("content-area");
		// First, delete all the old nodes
		tourapp.gui.removeChildren(domToAppendTo, false);
		// Then, add the new nodes (via xslt)
		//+ var desc = tourapp.gui.changeTitleFromData(domToBeTransformed);
		var attributes = tourapp.java.getAppletAttributes(params);
		var jnlp = tourapp.java.getJnlpURL(attributes, params);
		if (!params.jnlp_href)
			params['jnlp_href'] = jnlp;
		
		tourapp.java.prepareWindowForApplet(true);
		
		var html = tourapp.java.runApplet(attributes, params, '1.6');
		domToAppendTo.innerHTML = html;
		tourapp.java.pushBrowserHistory(command);
		return true;
	},
	// Return true if java applet is displayed
	isJavaWindow: function()
	{
		if (tourapp.util)
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
				tourapp.java.oldClassName = document.body.parentNode.className;
			document.body.parentNode.className="java";	// For firefox html.class
			tourapp.gui.changeTheTitle("Java Window");
		}
		else
		{
		    if (tourapp.java.oldClassName)
				document.body.parentNode.className=tourapp.java.oldClassName;
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
			if ((location.pathname.indexOf('tourapp') < 1) && (location.pathname.indexOf('tourapp/') != 0))
				params.baseURL += "/";
			else
				params.baseURL += location.pathname.substring(0, location.pathname.indexOf('tourapp'));
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
			attributes.name = 'tourapp';
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
				jnlp['applet'] = attributes['code'];
		var command = attributes.codebase + 'tourapp' + tourapp.util.propertiesToCommand(jnlp);
		return command;
	},
    /**
     * Same as deployJava, except I add to a string instead of doing document.write(xx).
     */
    runApplet: function(attributes, parameters, minimumVersion) {
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
                    return tourapp.java.writeAppletTag(attributes, parameters);
                } else if (deployJava.installJRE(minimumVersion + '+')) {
                    // after successfull install we need to refresh page to pick
                    // pick up new plugin
                    deployJava.refresh();
                    location.href = document.location;
                    return tourapp.java.writeAppletTag(attributes, parameters);
                }
            } else {
                // for unknown or Safari - just try to show applet
            	return tourapp.java.writeAppletTag(attributes, parameters);
            }
        } else {
            if (deployJava.debug) {
                alert('Invalid minimumVersion argument to runApplet():' + 
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
				if (hash.substring(0, 1) == '#')
					hash = hash.substring(1);
		return hash;
	},
	ignoreBack: false
};

tourapp.java.State.prototype.back = function() { tourapp.java.doJavaBrowserBack(this.changeUrl); };
tourapp.java.State.prototype.forward = function() { tourapp.java.doJavaBrowserForward(this.changeUrl); };

/**
 * For java to call these, these must be at the root.
 */
function pushBrowserHistory(command, title)
{
	tourapp.java.pushBrowserHistory(command, title);
}
function popBrowserHistory(count, commandHandledByClient, title)
{
	 tourapp.java.popBrowserHistory(count, commandHandledByClient, title);
}

}