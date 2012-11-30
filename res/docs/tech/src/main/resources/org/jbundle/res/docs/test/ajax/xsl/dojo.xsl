<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html"/>
<xsl:template match="/">
	<html>
<head>
<title>XSLT example</title>

<script type="text/javascript">
	djConfig = {
		isDebug: true,
		preventBackButtonFix: false
	};
</script>

<script djConfig="parseOnLoad: true" src="../../../../docs/styles/js/dojo/dojo.js" type="text/javascript"></script>
<style type="text/css">
	@import "../../../../docs/styles/js/dijit/themes/tundra/tundra.css";
</style>
			
<script>
dojo.require("dijit.form.Button");
dojo.require("dijit.form.TextBox");

function getFormData()
{
	alert( "submit");
}
function getResetData(data)
{
	alert(data);
}

</script>
</head>
		<body>
<button onclick="getFormData()">Submit</button>

<form method="post" action="appxsl" id="submit1">
<button onClick="getResetData('Submit');" dojoType="dijit.form.Button" class="button">Submit</button>
<button onClick="getResetData('Reset');" dojoType="dijit.form.Button" class="button">Reset</button>
</form>
			<xsl:apply-templates/>
		</body>
	</html>
</xsl:template>

<xsl:template match="html">
	<xsl:choose>
 		<xsl:when test="body != ''">
			<xsl:copy-of select="body/child::node()" />
  		</xsl:when>
 		<xsl:otherwise>
			<xsl:copy-of select="./child::node()"/>
 		</xsl:otherwise>
	</xsl:choose>
</xsl:template>
<xsl:template match="stuff">
<button onClick="getResetData('Submit');" dojoType="dijit.form.Button" class="button"><xsl:value-of select="."/></button>
</xsl:template>
</xsl:stylesheet>
