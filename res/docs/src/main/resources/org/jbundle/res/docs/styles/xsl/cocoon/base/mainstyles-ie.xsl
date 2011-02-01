<?xml version="1.0"?>
<!-- The base main stylesheet for Internet Explorer transformations -->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="mainstyles-base.xsl"/>

	<xsl:template match="top-menu">
		<table cellspacing="0" cellpadding="0" style="border: 0; background-color: blue; width: 100%">
			<tr>
				<td>
					<table cellspacing="0" cellpadding="1" style="border: 0;">
					<tr>
					<xsl:for-each select="menu-item">
					<td style="border-style: inset; border-width: 1px">
						<xsl:apply-templates select="." />
					</td>
					</xsl:for-each>
					</tr>
					</table>
				</td>
				<td style="text-align: center;">
					<span style="font-family: Arial, Helvetica, SanSerif; font-weight: bold; color: white">
						<xsl:value-of select="/full-screen/title" />
					</span>
				</td>
				<td style="text-align: right;">
					<table cellspacing="0" cellpadding="1" style="border: 0;">
					<tr>
					<td style="border-style: inset; border-width: 1px">
						<xsl:apply-templates select="help-item" />
					</td>
					</tr>
					</table>
				</td>
			</tr>
			<tr style="background-color: black">
				<td width="100%" height="1" colspan="6">
				</td>
			</tr>
		</table>
	</xsl:template>

</xsl:stylesheet>
