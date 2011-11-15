<?xml version="1.0"?>

<!-- The base main stylesheet for all html transformations -->
<!-- NOTE: This stylesheet should not be imported from any stylesheets except mainstyles -->

<xsl:stylesheet version="1.0"
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 xmlns:xfm="http://www.w3.org/2000/12/xforms">
<xsl:output method="html" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"  doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"/> 

	<xsl:template match="full-screen">
		<html>
			<body>
				<xsl:apply-templates select="content-area" />
			</body>
		</html>
	</xsl:template>

</xsl:stylesheet>