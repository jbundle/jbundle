<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                 version="1.0">
<xsl:import href="formtest.xsl"/>

<xsl:param name="countrycode"/>

<xsl:template match="page">
<html>
<body>
<xsl:choose>

<xsl:when test="not($countrycode)">
<p>Choose a country (ie):</p>
<form action="formtest.xml" method="get">
<select name="countrycode" size="1">
<xsl:apply-templates select="country" mode="form"/>
</select>
<input type="submit"/>
</form>
</xsl:when>

<xsl:when test="country[@code=$countrycode]">
<xsl:apply-templates select="country[@code=$countrycode]"
               mode="selected"/>
</xsl:when>

<xsl:otherwise>
<p>Unknown country code
                <em><xsl:value-of select="$countrycode"/></em>.
             </p>
</xsl:otherwise>

</xsl:choose>
</body>
</html>
</xsl:template>

</xsl:stylesheet>


