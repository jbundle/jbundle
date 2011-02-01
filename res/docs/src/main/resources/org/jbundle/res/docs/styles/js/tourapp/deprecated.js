/**
 * Top level methods and vars.
 */
//dojo.provide("tourapp.gui");

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
tourapp.gui = {

// Replaced with dojo.back


	historyCommand: [],
	historyDesc: [],
	/**
	 * Push this command unto the history stack and update the history display
	 */
	pushHistory: function(desc, command)
	{
		tourapp.gui.historyCommand.push(command);
		tourapp.gui.historyDesc.push(desc);
		if (tourapp.gui.historyCommand.length == 1)
			return;		// Don't display just one
		var historyDOM = document.getElementById("history-area");
		if (historyDOM)
		{
			var count = historyDOM.childNodes.length;
			var lastChild = null;
			if (count == 0)
				historyDOM.appendChild(document.createTextNode(tourapp.gui.historyDesc[0]));
			if (count > 0)
				lastChild = historyDOM.childNodes[count - 1];
			historyDOM.insertBefore(document.createTextNode("  >  "), lastChild);
			historyDOM.insertBefore(document.createTextNode(desc), lastChild);
			if (count == 0)
			{
				historyDOM.appendChild(document.createElement("br"));
				tourapp.gui.showBackButton();
			}
		}
	},
	/**
	 * Pop the last command from the history stack and update the history display
	 */
	popHistory: function()
	{
		var command = tourapp.gui.historyCommand.pop();
		tourapp.gui.historyDesc.pop();
		
		var historyDOM = document.getElementById("history-area");
		if (historyDOM)
		{
			var count = historyDOM.childNodes.length;
			var lastChild = null;
			if (count > 4)
			{
				tourapp.gui.removeChildren(historyDOM.childNodes[count - 2], true);
				tourapp.gui.removeChildren(historyDOM.childNodes[count - 3], true);	// >
			}
			else
			{
				tourapp.gui.removeChildren(historyDOM);	// Remove all
				tourapp.gui.hideBackButton();
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