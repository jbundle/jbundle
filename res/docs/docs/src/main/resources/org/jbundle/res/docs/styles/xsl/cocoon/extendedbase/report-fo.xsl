<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 xmlns:xfm="http://www.w3.org/2000/12/xforms">

	<xsl:import href="../base/mainstyles-fo.xsl"/>

	<xsl:template match="/">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="navigation-menu">
	</xsl:template>
	<xsl:template name="navigation-corner">
	</xsl:template>

	<xsl:template match="form">
	</xsl:template>

	<xsl:template match="data">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="detail/file">
	</xsl:template>

	<xsl:template match="heading/file|footing/file">
	</xsl:template>

</xsl:stylesheet>

