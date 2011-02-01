<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html"/>
<xsl:template match="/">
	<html>
		<head>
			<title>This is the Title2</title>
		</head>
		<body>
			<xsl:apply-templates/>
		</body>
	</html>
</xsl:template>

<xsl:template match="junk">
		<xsl:if test="count(image)=0">[none]x</xsl:if>
		<xsl:if test="count(image)>0"><xsl:value-of select="image" />y</xsl:if>
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

</xsl:stylesheet>
