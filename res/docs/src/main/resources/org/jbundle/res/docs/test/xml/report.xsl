<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 xmlns:xfm="http://www.w3.org/2000/12/xforms">
<!--	<xsl:import href="mainstyles.xsl"/> -->
	<xsl:template match="/">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="content-area">
	<html>
	<head>
	</head>
	<body>
		<xsl:value-of select="Menus/Name"/>
		<xsl:apply-templates select="form" />
		<xsl:apply-templates select="file" />
	</body>
	</html>
	</xsl:template>

	<xsl:template match="form">
	</xsl:template>

	<xsl:template match="file">
		<table border="1" cellpadding="5" width="100%">
			<tr>
				<xsl:for-each select="../form/*">
					<th style="text-align: center; vertical-align: baseline">
						<xsl:value-of select="xfm:caption"/>
					</th>
				</xsl:for-each>
			</tr>

			<xsl:for-each select="*">
			<tr>
				<xsl:variable name="record" select="."/>
				<xsl:for-each select="../../form/*">
					<xsl:variable name="ref"><xsl:value-of select="@ref"/></xsl:variable>
					<td style="text-align: left; vertical-align: baseline">
						<xsl:value-of select="$record/*[name()=$ref]"/>
					</td>
				</xsl:for-each>
			</tr>
			</xsl:for-each>
		</table>
	</xsl:template>
	

</xsl:stylesheet>

