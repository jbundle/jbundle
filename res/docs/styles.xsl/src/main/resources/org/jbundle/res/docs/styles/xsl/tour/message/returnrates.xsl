<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html"/>
<xsl:template match="/">
	<html>
		<head>
			<title>This is the Title</title>
		</head>
		<body>
			<xsl:apply-templates/>
		</body>
	</html>
</xsl:template>

<xsl:template match="root">
	Please respond with the following rates:<p/>
	<xsl:for-each select="*">
        <br/><xsl:value-of select="name()"/>: <xsl:value-of select="."/>
	</xsl:for-each>
	<p/>Registry: <xsl:value-of select="header/registryID" />
</xsl:template>

</xsl:stylesheet>
