<xsl:stylesheet version="1.0"
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

<xsl:template match="file/AcctDetail/TrxDate">
	<xsl:call-template name="newdate">
		<xsl:with-param name="data">
			<xsl:value-of select="."></xsl:value-of>
		</xsl:with-param>
	</xsl:call-template>
</xsl:template>

<xsl:template name="nbsp-ref">
   <xsl:text>&#160;</xsl:text>
</xsl:template>

<xsl:template name="newdate">
	<xsl:param name="data"></xsl:param>	<!-- data -->
	<xsl:param name="type"></xsl:param>	<!-- result date type -->
  <xsl:copy>
  	<xsl:if test="$type='eom'">Oct 31, 2009</xsl:if>
  	<xsl:if test="$type=''">
  		<xsl:value-of select="$data" />
  	</xsl:if>
  </xsl:copy>
</xsl:template>

</xsl:stylesheet>
