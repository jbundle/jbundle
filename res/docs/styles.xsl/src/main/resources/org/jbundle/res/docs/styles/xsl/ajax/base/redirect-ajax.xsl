<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 xmlns:xfm="http://www.w3.org/2000/12/xforms">
<xsl:output method="html" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"  doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"/> 
	<xsl:template match="full-screen">
<html>
<head>
	<xsl:element name="META">
		<xsl:attribute name="HTTP-EQUIV">REFRESH</xsl:attribute>
		<xsl:attribute name="CONTENT">0; URL=<xsl:value-of select="meta-redirect"/></xsl:attribute>
	</xsl:element>
</head>
<body>
</body>
</html>
	</xsl:template>
</xsl:stylesheet>

