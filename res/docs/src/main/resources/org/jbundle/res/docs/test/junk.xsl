<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="CATALOG">
  <xsl:processing-instruction name="cocoon-format">type="text/html"</xsl:processing-instruction>
   <html>
    <head>
     <title>
      <xsl:value-of select="title"/><xsl:text> - Hello</xsl:text>
     </title>
    </head>
    <body bgcolor="#ffffff">
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
    </body>
   </html>
  </xsl:template>

  

</xsl:stylesheet>
