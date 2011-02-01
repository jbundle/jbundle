<xsl:stylesheet version="2.0"
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
   <xsl:output indent="no" method="xml"/>

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

</xsl:stylesheet>
