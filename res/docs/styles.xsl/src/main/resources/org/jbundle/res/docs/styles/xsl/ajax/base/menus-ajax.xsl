<?xml version="1.0"?>
<!-- WARNING: Since Chrome does not support the import tag, you must run the flattening utility after changing -->
<xsl:stylesheet
 version="1.0"
 xmlns:xfm="http://www.w3.org/2000/12/xforms"
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="../../ajax/base/mainstyles-ajax.xsl"/>
	<xsl:import href="../../cocoon/base/menus-base.xsl"/>

<xsl:output method="html" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"  doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"/> 
	<!-- The menu items (using dojo) -->
	<xsl:template match="menu_list/Menus">
		<xsl:call-template name="button_link">
			<xsl:with-param name="name"><xsl:value-of select="Name" /></xsl:with-param>
			<xsl:with-param name="id"><xsl:value-of select="Name" /></xsl:with-param>
			<xsl:with-param name="description"><xsl:value-of select="description" /></xsl:with-param>
			<xsl:with-param name="link"><xsl:value-of select="link" /></xsl:with-param>
			<xsl:with-param name="image">
				<xsl:choose>
 					<xsl:when test="IconResource != ''">
						<xsl:value-of select="IconResource"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="Type"/>
 					</xsl:otherwise>
				</xsl:choose>
			</xsl:with-param>
			<xsl:with-param name="type">icon</xsl:with-param>
		</xsl:call-template>
		<br />
		<span class="comment">
			<xsl:apply-templates select="Comment"/>
		</span>
	</xsl:template>

</xsl:stylesheet>
