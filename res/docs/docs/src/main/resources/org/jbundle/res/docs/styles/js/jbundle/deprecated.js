/**
 * Top level methods and vars.
 */
//dojo.provide("jbundle.gui");

//dojo.require("dojo.debug.console");
//dojo.require("dojo.io.*");
//dojo.require("dojo.event.*");
//dojo.require("dojo.widget.*");
//dojo.require("dojo.json");
//dojo.require("dojo.dom");
//dojo.require("dojo.xml.*");
//dojo.require("dojo.xml.XslTransform");
//dojo.require("dojo.crypto.SHA1");
dojo.require("dojo.parser");
dojo.require("dijit.form.Button");
dojo.require("dijit.form.TextBox");
dojo.require("dijit.form.CheckBox");
dojo.require("dijit.Dialog");

/**
 * Screen utilities.
 */
jbundle.gui = {

// Replaced with dojo.back


	historyCommand: [],
	historyDesc: [],
	/**
	 * Push this command unto the history stack and update the history display
	 */
	pushHistory: function(desc, command)
	{
		jbundle.gui.historyCommand.push(command);
		jbundle.gui.historyDesc.push(desc);
		if (jbundle.gui.historyCommand.length == 1)
			return;		// Don't display just one
		var historyDOM = document.getElementById("history-area");
		if (historyDOM)
		{
			var count = historyDOM.childNodes.length;
			var lastChild = null;
			if (count == 0)
				historyDOM.appendChild(document.createTextNode(jbundle.gui.historyDesc[0]));
			if (count > 0)
				lastChild = historyDOM.childNodes[count - 1];
			historyDOM.insertBefore(document.createTextNode("  >  "), lastChild);
			historyDOM.insertBefore(document.createTextNode(desc), lastChild);
			if (count == 0)
			{
				historyDOM.appendChild(document.createElement("br"));
				jbundle.gui.showBackButton();
			}
		}
	},
	/**
	 * Pop the last command from the history stack and update the history display
	 */
	popHistory: function()
	{
		var command = jbundle.gui.historyCommand.pop();
		jbundle.gui.historyDesc.pop();
		
		var historyDOM = document.getElementById("history-area");
		if (historyDOM)
		{
			var count = historyDOM.childNodes.length;
			var lastChild = null;
			if (count > 4)
			{
				jbundle.gui.removeChildren(historyDOM.childNodes[count - 2], true);
				jbundle.gui.removeChildren(historyDOM.childNodes[count - 3], true);	// >
			}
			else
			{
				jbundle.gui.removeChildren(historyDOM);	// Remove all
				jbundle.gui.hideBackButton();
			}
		}
		
		return command;
	},
	showBackButton: function()
	{
		var backButton = dijit.byId("backButton");
		if (backButton)
			backButton.domNode.style.display = "inherit";
	},
	hideBackButton: function()
	{
		var backButton = dijit.byId("backButton");
		if (backButton)
			backButton.domNode.style.display = "none";
	}
};