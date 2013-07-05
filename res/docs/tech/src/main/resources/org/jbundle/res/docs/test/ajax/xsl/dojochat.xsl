<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html"/>
<xsl:template match="/">

<html>
<head>
<title>Example 15</title>

<script>
	djConfig = {
		isDebug: true
//		debugAtAllCosts: true
	};
</script>
<script type="text/javascript" src="../../../styles/js/dojo/dojo.js" djConfig="parseOnLoad: true"></script>
<style type="text/css">
        @import "../../../styles/js/dijit/themes/tundra/tundra.css";
</style>

<!-- 
<script language="javascript" src="../../styles/js/jbundle/main.js" type="text/javascript"></script>
<script language="javascript" type="text/javascript">
tourgeek.SERVER_PATH = "../../../";
</script>
<script language="javascript" src="../../styles/js/jbundle/classes.js" type="text/javascript"></script>
<script language="javascript" src="../../styles/js/jbundle/remote.js" type="text/javascript"></script>
<script language="javascript" src="../../styles/js/jbundle/util.js" type="text/javascript"></script>
<script language="javascript" src="../../styles/js/jbundle/gui.js" type="text/javascript"></script>
<script language="javascript" src="../../styles/js/jbundle/xml.js" type="text/javascript"></script>
 -->
 
<script language="javascript" src="dojochat.js" type="text/javascript"></script>

<script language="javascript" type="text/javascript">

dojo.require("tourgeek.main");
	tourgeek.SERVER_PATH = "../../../../";
dojo.require("tourgeek.classes");
dojo.require("tourgeek.remote");
dojo.require("tourgeek.util");
dojo.require("tourgeek.gui");
dojo.require("tourgeek.xml");

dojo.require("dijit.form.Button");
dojo.require("dijit.form.TextBox");
dojo.require("dijit.form.CheckBox");
dojo.require("dijit.Dialog");
dojo.require("dojo.parser");

</script>
</head>

<body onload="onLoad();" class="tundra">
	<form id="myForm" action="">
		<p>Dialog:</p>
		<textarea rows="10" cols="100" name="author"></textarea>
		<p>Please enter text:</p>
		<p><input type="text" dojoType="dijit.form.TextBox" required="true"
			name="message" value="Test text"/></p>
		<p><input type="button" id="inButton" value="Login" />
		<input type="button" id="outButton" value="Logout" />
		<input type="button" id="queueButton" value="Add Queue" />
		<input type="button" id="filterButton" value="Add Listener" />
		<input type="button" id="freeButton" value="Free session" />
		<input type="button" id="createButton" value="Create session" />
		<input type="button" id="actionButton" value="Do Action" />
		<input type="button" id="myButton" value="Submit" /></p>

		<p/>
		<button dojoType="dijit.form.Button" onClick="go();" name="Go">End</button>
		<p/>

	</form>

	<br/>
	<button dojoType="dijit.form.Button" onClick="tourgeek.gui.displayLogonDialog(document.getElementById('scratch'));">Form</button>

	<div id="menuarea">
	</div>

	<div id="scratch">
	</div>

</body>
</html>
</xsl:template>
</xsl:stylesheet>
