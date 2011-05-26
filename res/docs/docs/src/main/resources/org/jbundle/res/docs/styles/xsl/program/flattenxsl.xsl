<xsl:stylesheet version="2.0"
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
   <xsl:output indent="no" method="xml"/>

<!--  NOTE: I could never figure out how to get rid of the duplicate templates - do it manually for now -->
<xsl:template match="*">
  <xsl:copy>
    <xsl:for-each select="@*">
      <xsl:attribute name="{name()}">
        <xsl:value-of select="."/>
      </xsl:attribute>
    </xsl:for-each>
  	<xsl:apply-templates />
  </xsl:copy>
</xsl:template>

<xsl:template match="xsl:import">
	<xsl:call-template name="newdate">
		<xsl:with-param name="href">
			<xsl:value-of select="@href"></xsl:value-of>
		</xsl:with-param>
	</xsl:call-template>
</xsl:template>
 
<xsl:template name="xyz">
<xsl:value-of select="name(@match)"/>
<xsl:if test="name()='xsl:template' and @match!=''"><t>xyz</t></xsl:if>
<x><xsl:value-of select="concat('/xsl:stylesheet/xsl:template@', @match)"/></x>
<x><xsl:value-of select="/xsl:stylesheet/xsl:template[attribute::match=@match]"/></x>
<xsl:if test="name()='xsl:template' and @match!='' and name(/xsl:stylesheet/xsl:template[attribute::match=@match]) != ''"><t>----------------</t></xsl:if>
</xsl:template>

<xsl:template name="newdate">
	<xsl:param name="href"></xsl:param>	<!-- data -->
	<!-- Can't figure out how to get a relative (to the target document) uri -->
<xsl:variable name="imported" select="document(concat('/home/don/workspace/jdance/WebContent/docs/styles/xsl/ajax/base/', $href))/xsl:stylesheet/*"/>
		<xsl:apply-templates select="$imported"/>
</xsl:template>

<xsl:template name="elementPath">
<xsl:for-each select="ancestor-or-self::*">
  <xsl:value-of select="name()" /><xsl:text>/</xsl:text>
</xsl:for-each>
 </xsl:template>

</xsl:stylesheet>
