/**
 * Top level methods and vars.
 */
if(!dojo._hasResource["tourapp.gui"]){
dojo._hasResource["tourapp.gui"] = true;
dojo.provide("tourapp.gui");

dojo.require("dojo.parser");
dojo.require("dijit.form.Button");
dojo.require("dijit.form.TextBox");
dojo.require("dijit.form.CheckBox");
dojo.require("dijit.Editor");
dojo.require("dijit.Dialog");

/**
 * Screen utilities.
 */
tourapp.gui = {
	/**
	 * Get scratch area.
	 * @return The dom to the scratch area.
	 */
	getScratch: function()
	{
		var domToAppendTo = document.getElementById("scratch");
		if (!domToAppendTo)
		{
			var htmlDom = document.getElementsByTagName("body")[0];
			domToAppendTo = document.createElement("div");
			htmlDom.appendChild(domToAppendTo);
			domToAppendTo.setAttribute("id", "scratch");
		}
		return domToAppendTo;
	},
	/**
	 * Send this message.
	 */
	displayLogonDialog: function(domToAppendTo, user, dialogTitle, command)
	{
		if (!domToAppendTo)
			domToAppendTo = tourapp.gui.getScratch();
		if (!user)
		{
			user = "";
			if (tourapp.getTaskSession().security)
				if (tourapp.getTaskSession().security.userProperties)
					if (tourapp.getTaskSession().security.userProperties.user)
						user = tourapp.getTaskSession().security.userProperties.user;
		}
		if (!dialogTitle)
			dialogTitle = "Login";
		if (!command)
			command = "";
		var xmlToBeTransformed = "<root><user>" + user + "</user><dialogTitle>" + dialogTitle + "</dialogTitle><command>" + command + "</command></root>";
		var xsltURI = tourapp.getServerPath("docs/styles/js/tourapp/xsl/logon.xsl");

		var logonDialog = dijit.byId("logonDialog");
		if (!logonDialog)
		{
			var domToBeTransformed = dojox.data.dom.createDocument(xmlToBeTransformed);
			tourapp.xml.doXSLT(domToBeTransformed, xsltURI, domToAppendTo, tourapp.gui.handleDisplayLogonDialog);
		}
		else
		{
			var form = document.getElementById("logonForm");
			// TODO (don) Fix 0.9			form.title = dialogTitle;
			form.elements.user.value = user;
			form.elements.password.value = "";
			form.elements.saveUser.checked = true;
			form.elements.command.value = command;

        	logonDialog.show();
		}
		return true;
	},
	doNothing: function()
	{
	},
	/**
	 * After the logon dialog is set up, display it.
	 */
	handleDisplayLogonDialog: function(domToAppendTo)
	{
		dojo.parser.parse(domToAppendTo);

		var logonDialog = dijit.byId("logonDialog");
       	logonDialog.show();
		return;
	},
	/**
	 * User pressed submit or cancel.
	 */
	submitLogonDialog: function(submit)
	{
		dlg0 = dijit.byId("logonDialog");
		if (submit == true)
		{
			var form = document.getElementById("logonForm");
			var user = form.elements.user.value;
			var command = form.elements.command.value;

			var password = form.elements.password.value;
			tourapp.util.saveUser = form.elements.saveUser.checked;

			if (password)
				password = b64_sha1(password);

			tourapp.gui.handleLoginLink = dojo.connect(tourapp.remote, "handleLogin", tourapp.gui, "handleLogin");

			tourapp.util.lastCommand = "?menu=";
			tourapp.util.handleLoginLink = dojo.connect(tourapp.remote, "handleLogin", tourapp.util, "doLoginCommand");

			if (command)
				if (command != "")
			{
				tourapp.util.lastCommand = command;	// Make sure it does the correct command.
				tourapp.gui.handleLoginLink = dojo.connect(tourapp.remote, "handleLogin", tourapp.gui, "handleLogin");
			}
			tourapp.remote.login(tourapp.getTaskSession(), user, password);
		}
		dlg0.hide();
		if (submit)
			if (submit != true)
			if (submit != false)
		{
			tourapp.util.doCommand(submit);
		}
		return false;	// If called from post, don't submit form
	},
	// Save the user name.
	saveUser: null,
	// handleLogin event link. Note: Is there a potential concurrency problem here?
	handleLoginLink: null,
	/**
	 *
	 */
	handleLogin: function(data, ioArgs)
	{
		dojo.disconnect(tourapp.gui.handleLoginLink);
//		if (tourapp.remote.checkForDataError(data, "Could not log in"))
	//		return;
		var user = "";
		var userid = null;
		if (tourapp.getTaskSession().security)
			if (tourapp.getTaskSession().security.userProperties)
		{
			user = tourapp.getTaskSession().security.userProperties.user;
			userid = tourapp.getTaskSession().security.userProperties.userid;
		}
		if ((tourapp.util.saveUser == true)
			&& (userid)
				&& (userid != "1"))	// Anon
			tourapp.util.setCookie("userid", userid, +365);
		else if (tourapp.util.saveUser == false)
			tourapp.util.setCookie("userid", null);
		tourapp.util.saveUser = null;
		tourapp.gui.changeUser(user);
		var desc = tourapp.gui.LOGOUT_DESC;
		var command = "Logout";
		if ((!user) || (user == "") || (userid == "1"))
		{
			desc = tourapp.gui.LOGIN_DESC;
			command = "Login";
		}
		tourapp.gui.changeButton(dijit.byId(tourapp.gui.LOGIN_DESC), desc, command);	// Could be either
		tourapp.gui.changeButton(dijit.byId(tourapp.gui.LOGOUT_DESC), desc, command);
	},
	LOGOUT_DESC: "Sign out",	// Change these for I18N
	LOGIN_DESC: "Sign in",
	/*
	 * Display an error message.
	 */
	displayErrorMessage: function(message)
	{
		var domToAppendTo = tourapp.gui.getScratch();
		
		var alertDialog = dijit.byId("alertDialog");
		if (!alertDialog)
		{
			var params =
				{
					bgColor: "white",
					bgOpacity: "0.5",
					toggle: "fade",
					toggleDuration: "250",
					title: "Error!",
					iconSrc: "images/buttons/Error.gif",
					displayCloseAction: true,
					id: "alertDialog"
				};
			alertDialog = new dijit.Dialog(params, domToAppendTo);
		}
		// todo (don) This seems lame
		message = "<div style='text-align: center;'>" + 
			message + 
				"<br/><button id=\"alertDialogOkay\" onClick=\"dijit.byId('alertDialog').hide();\" dojoType=\"dijit.form.Button\" class=\"button\" ><img src=\"images/buttons/Close.gif\" width=\"16\" height=\"16\" alt=\"Close\" class=\"button\" />Close</button>" + 
//x				"&#160;&#160;&#160;" +
//x				"<button id=\"alertDialogNewUser\" onClick=\"tourapp.util.doCommand('?screen=.main.user.screen.UserEntryScreen&amp;java=no');\" dojoType=\"dijit.form.Button\" class=\"button\" ><img src=\"images/buttons/Form.gif\" width=\"16\" height=\"16\" alt=\"Create new account\" class=\"button\" />Create new account</button>" + 
				"</div>";
		alertDialog.setContent(message);

       	alertDialog.show();

		return true;
	},
	/**
	 * Display this message on the screen (the status of the just-executed command).
	 */
	displayScreenInfoMessage: function(infoText, infoClass)
	{
		var messageArea = document.getElementById('status-area');
		if (messageArea)
		{
			tourapp.gui.removeChildren(messageArea);
			messageArea.appendChild(document.createTextNode(infoText));
			if (!infoClass)
				infoClass = "information";
			messageArea.setAttribute("class", infoClass);
		}
	},
	/**
	 * Switch this button's description and command.
	 */
	changeButton: function(button, desc, command)
	{
		if (!command)
			command = desc;
		var imageName = "<img src=\"images/buttons/" + command + ".gif\" width=\"16\" height=\"16\"/>" + desc;
		if (button)
			button.setLabel(imageName);
	},
	/**
	 * Switch this document's user name.
	 */
	changeUser: function(user)
	{
		if (!user)
			user = "";
		var div = document.getElementById("userName");
		if (div)
		{	// Always
			tourapp.gui.removeChildren(div);
			div.appendChild(document.createTextNode(user));
		}
	},
	/**
	 * Change the screen title.
	 */
	changeTheTitle: function(newtitle)
	{
		if (!newtitle)
			newtitle = "";
		var div = document.getElementById("title");
		if (div)
		{	// Always
			tourapp.gui.removeChildren(div);
			div.appendChild(document.createTextNode(newtitle));
		}
		document.title = newtitle;
	},
	/**
	 * Given the DOM, get the screen title and change it.
	 */
	changeTitleFromData: function(domToBeTransformed)
	{
		var elements = domToBeTransformed.getElementsByTagName("Name");
		var title = null;
		if (elements)
			if (elements.length > 0)
				tourapp.gui.changeTheTitle(title = elements[0].textContent);
		return title;
	},
	/**
	 * Change the nav menus.
	 */
	changeNavMenus: function(navmenus)
	{
		var navStart = document.getElementById("navStart");
		var navStartShadow = document.getElementById("navStartShadow");
		var navStartVShadow = document.getElementById("navStartVShadow");
		var navStartSECorner = document.getElementById("navStartSECorner");
		
		var style = "navStart";
		var styleShadow = "navStartShadow";
		if (navmenus == "IconsOnly")
		{
			if (navStart.className != "navIconsOnlyStart")
				style = "navIconsOnlyStart";	// This will toggle the icons
		}
		else if (navmenus == "No")
		{
			style = "navNoStart";
			styleShadow = "navNoStartShadow";
		}
		navStart.className = style;
		navStartShadow.className = styleShadow;
		navStartVShadow.className = style;
		navStartSECorner.className = styleShadow;
	},
	/**
	 * Hide/show the menu bar.
	 */
	changeMenubar: function(menus)
	{
		var menubarTable = document.getElementById("menubar");
		
		if (menus == "No")
			menubarTable.style.display="none";
		else
			menubarTable.style.display="";
	},
	/**
	 * Parse this new dom for dojo controls.
	 */
	fixNewDOM: function(domToAppendTo)
	{
		dojo.parser.parse(domToAppendTo);
	},
	/**
	 * Get all the form data from this screen and stick it in a name/value object.
	 */
	getFormData: function(hiddenOnly) {
		var form = document.getElementById("submit1");
		var formValues = {};
		for (var i = 0; i < form.elements.length; i++) {
			var elem = form.elements[i];
			if ((elem.name == "button") || (elem.name == "") || (!elem.name))
				continue;
			if (hiddenOnly)
				if (elem.type != "hidden")
					continue;
			formValues[elem.name] = elem.value;
		}
		if (!hiddenOnly)
		{
			var elements = document.getElementsByTagName("div");
			i = 0;
			while (elem = elements[i++])
			{
			    if (elem.className)
			    	if (elem.className.indexOf("tourapp.Editor") != -1)
			    {
					var editor = dijit.byNode(elem);
					if (editor)
						formValues[editor.name] = editor.getValue();
			    }
			}
		}
		return formValues;
	},
	/**
	 * Is the screen a data entry form?
	 */
	isForm: function() {
		if (document.getElementById("submit1"))
			return true;
		return false;
	},
	/**
	 * Get all the form data from this screen and stick it in a name/value object.
	 */
	setFormData: function(formValues) {
		var form = document.getElementById("submit1");
		if (!form)
			return;
		for (var i = 0; i < form.elements.length; i++) {
			var elem = form.elements[i];
			if ((elem.name == "button") || (elem.name == "") || (!elem.name) || (elem.type == "hidden"))
				continue;
			if ((formValues) && (formValues[elem.name]))
				elem.value = formValues[elem.name];
			else if ((tourapp.gui.defaultFormValues) && (tourapp.gui.defaultFormValues[elem.name]))
				elem.value = tourapp.gui.defaultFormValues[elem.name];
			else
				elem.value = "";
		}
		var elements = document.getElementsByTagName("div");
		i = 0;
		while (elem = elements[i++])
		{
		    if (elem.className)
		    	if (elem.className.indexOf("tourapp.Editor") != -1)
		    {
				var editor = dijit.byNode(elem);
				if (editor)
				{
					if ((formValues) && (formValues[editor.name]))
						editor.setValue(formValues[editor.name]);
					else if ((tourapp.gui.defaultFormValues) && (tourapp.gui.defaultFormValues[editor.name]))
						editor.setValue(tourapp.gui.defaultFormValues[editor.name]);
					else
						editor.setValue("");
				}
		    }
		}
	},
	defaultFormValues: null,
	/**
	 * Clear all the data in this form and make sure the hidden objectID is cleared.
	 */
	clearFormData: function() {
		tourapp.gui.setFormData(null);
		if (document.getElementById('objectID'))
			document.getElementById('objectID').value = "";
	},
	/**
	 * Get all the form data from this screen and stick it in a name/value object.
	 */
	clearGridData: function(objectID) {
		var tr = document.getElementById(objectID);
		if (tr)
			tourapp.gui.removeChildren(tr, true);
	},
	/**
	 * Utility - Remove all children from this node.
	 */
	removeChildren: function(dom, removeFromParent)
	{
		if (dom.nodeType == 1)	// Node.ELEMENT_NODE)
		{
			if (dom.getAttribute("widgetid"))
			{	// TODO (don) - Fix this to use dom.destroy();
				if (removeFromParent)
					if (dijit.byId(dom.getAttribute("widgetid")))
					{
						dijit.byId(dom.getAttribute("widgetid")).destroyRecursive();
						removeFromParent = false;	// Previous command removed it.
					}
//						dijit.util.manager.remove(dom.getAttribute("widgetid"));	// dojo destroy
			}
			var children = dom.childNodes;
			for (var i = children.length-1; i >= 0; i--)
			{
				tourapp.gui.removeChildren(children[i], true);
			}
		}
		if (removeFromParent)
			dom.parentNode.removeChild(dom);
	},
	// Display the wait cursor
	waitCursor: function()
	{
		document.body.style.cursor = 'wait';
	},
	// Display the normal cursor
	restoreCursor: function(command)
	{
		document.body.style.cursor = 'default';
	},
	// Display a applet with this command in the content area.
	// Returns true if successful
	displayApplet: function(command)
	{
		if (!tourapp.java)
			return false;	// No java.js
		tourapp.gui.appletDisplayed = tourapp.java.displayApplet(command);
		return tourapp.gui.appletDisplayed;
	},
	appletDisplayed: false
};
}