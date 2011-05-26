<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="page">
<html>
<head>
<title>
<xsl:value-of select="title"/>
</title>
</head>
<body bgcolor="#ffffff">
<xsl:apply-templates/>
</body>
</html>
</xsl:template>

</xsl:stylesheet>
