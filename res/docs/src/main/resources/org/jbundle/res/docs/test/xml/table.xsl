<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="/">
	<html>
		<head>
			<title>This is the Title</title>
		</head>
		<body>
			<xsl:apply-templates select="full-screen/content-area"/>
		</body>
	</html>
</xsl:template>

	<xsl:template match="content-area">
		<xsl:value-of select="Menus/Name"/>
		<xsl:apply-templates select="menulist" />
	</xsl:template>

	<xsl:template match="menulist">
		<table border="0" cellpadding="5" width="100%">
			<xsl:for-each select="Menus">
			<tr>
				<td style="text-align: center; vertical-align: baseline; width: 100%; font-style: italic">
					<xsl:value-of select="Name"/>
				</td>
			</tr>
			</xsl:for-each>
		</table>
	</xsl:template>

</xsl:stylesheet>

