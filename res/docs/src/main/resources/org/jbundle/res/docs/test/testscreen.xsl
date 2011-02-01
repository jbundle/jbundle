<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<!--
Microsoft version.
<xsl:stylesheet  version="1.0" xmlns:xsl="http://www.w3.org/TR/WD-xsl">
-->
	<xsl:template match="/">
		<xsl:apply-templates />
	</xsl:template>
	<xsl:template match="full-screen">
		<html>
			<head>
				<title>
					<xsl:value-of select="title"/>
				</title>
			</head>
			<body>
				<table>
					<tr>
						<td colspan="2">Title</td>
					</tr>
					<tr>
						<td>
							<xsl:apply-templates select="navigation-menu" />
						</td>
						<td>Right menu</td>
					</tr>
				</table>
			</body>
		</html>
	</xsl:template>
	
	<xsl:template match="navigation-menu">
		<table border="0" bgcolor="yellow">
			<tr>
				<th>Title</th>
				<th>Artist</th>
			</tr>
			<xsl:for-each select="navigation-item">
				<tr>
					<td>
						<xsl:element name="img">
						<xsl:attribute name="src">
							images/buttons/<xsl:value-of select="icon"/>
						</xsl:attribute>
						</xsl:element>
					</td>
					<td>
						<xsl:element name="a">
							<xsl:attribute name="href">
								<xsl:value-of select="link"/>
							</xsl:attribute>
							<xsl:value-of select="name"/>
						</xsl:element>
					</td>
				</tr>
			</xsl:for-each>
		</table>
	</xsl:template>
</xsl:stylesheet>

