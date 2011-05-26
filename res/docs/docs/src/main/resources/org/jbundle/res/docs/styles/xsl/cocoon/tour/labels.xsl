<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 xmlns:xfm="http://www.w3.org/2000/12/xforms">
<xsl:import href="../extendedbase/report.xsl"/>

	<xsl:output method="html" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"  doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"/> 

	<xsl:template match="detail/file">

			<xsl:for-each select="*">
			
				<xsl:value-of select="Request.GenericName" /><br/>
				<xsl:value-of select="RequestText" /><br/>
				<xsl:apply-templates select="FullAddress" /><br/>

			</xsl:for-each>

	</xsl:template>

	<xsl:template match="heading/file|footing/file">
	</xsl:template>

	<xsl:template match="top-menu">
	</xsl:template>

	</xsl:stylesheet>
