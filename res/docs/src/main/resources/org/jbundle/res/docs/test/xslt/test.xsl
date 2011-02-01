<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  
<xsl:output method="html" indent="yes"/>

<xsl:template match="gods">
  <div>
    <h2>Gods</h2>
    <ul>
      <xsl:apply-templates select="god"/>
    </ul>
  </div>
</xsl:template>

<xsl:template match="god">
  <li><xsl:value-of select="."/></li>
</xsl:template>

</xsl:stylesheet>