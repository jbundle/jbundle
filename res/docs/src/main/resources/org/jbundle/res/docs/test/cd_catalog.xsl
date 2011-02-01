<?xml version='1.0'?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="/">
  <xsl:processing-instruction name="cocoon-format">type="text/html"</xsl:processing-instruction>
 <html>
  <body>
   <xsl:apply-templates/>
  </body>
 </html>
</xsl:template>


<xsl:template match="CATALOG">

<table border="2" bgcolor="yellow">
	<tr>
		<th>Title</th>
		<th>Artist</th>
	</tr>
	<xsl:for-each select="CD">
		<tr>
			<td><xsl:value-of select="TITLE"/></td>
			<td><xsl:value-of select="ARTIST"/></td>
		</tr>
	</xsl:for-each>
</table>


</xsl:template>

</xsl:stylesheet>
