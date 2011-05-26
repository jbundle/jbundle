<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<!-- This template copies the name and attributes and applies templates to child nodes -->
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

<xsl:template match="b">
 <xsl:element name="i">
  <xsl:apply-templates />
 </xsl:element>
</xsl:template>

</xsl:stylesheet>
