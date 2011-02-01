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

<xsl:template match="paragraph">
  <p>
    <xsl:apply-templates/>
  </p>
</xsl:template>

<xsl:template match="link">
		<xsl:element name="a">
			<xsl:attribute name="href">
				<xsl:value-of select="url"/>
			</xsl:attribute>
				<xsl:value-of select="name"/>
		</xsl:element>
</xsl:template>

<xsl:template match="list">
			<xsl:for-each select="item">
				<xsl:apply-templates select="." />
				<br/>
			</xsl:for-each>
</xsl:template>

</xsl:stylesheet>
